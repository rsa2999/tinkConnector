package com.cgd.tinkConnector;


import com.cgd.tinkConnector.Model.Tink.TinkAccount;
import com.cgd.tinkConnector.Model.Tink.TinkTransaction;
import com.cgd.tinkConnector.Utils.ConversionUtils;
import com.cgd.tinkConnector.entities.BatchFile;
import com.cgd.tinkConnector.parser.DelimitedParserInfo;
import com.cgd.tinkConnector.parser.DelimitedParserResult;
import io.swagger.annotations.ApiOperation;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;


@RestController
@RequestScope
@RequestMapping("/api/v1")
public class BatchJobController extends BaseController {

    private static Logger LOGGER = LoggerFactory.getLogger(BatchJobController.class);

    private static final String ZD4270CLINUM = "ZD4270-CLI-NUM";
    private static final String ZD4270ACCD = "ZD4270-AC-CD";
    private static final String ZD4270TIPOREGISTO = "ZD4270-TIPO-REGISTO";
    private static final String ZD4270TXNAMT = "ZD4270-TXN-AMT";
    private static final String ZD4270PSTGDT = "ZD4270-PSTG-DT";
    private static final String ZD4270TXNDATE = "ZD4270-TXN-DATE";
    private static final String ZD4270GUEFFDT = "ZD4270-GU-EFF-DT";
    private static final String ZD4270TXNDESCTX = "ZD4270-TXN-DESC-TX";
    private static final String ZD4270MCC = "ZD4270-MCC";
    private static final String ZD4270TXNCD = "ZD4270-TXN-CD";
    private static final String ZD4270PLID = "ZD4270-PL-ID";


    private static final String ZD4270AVAILBAL = "ZD4270-AVAIL-BAL";
    private static final String ZD4270CURBALAM = "ZD4270-CUR-BAL-AM";


    @Value("${cgd.batchJob.sshHost:null}")
    private String sshHost;
    @Value("${cgd.batchJob.sshUsername:null}")
    private String sshUserName;
    @Value("${cgd.batchJob.sshPassword:null}")
    private String sshPassword;
    @Value("${cgd.batchJob.sshPath:null}")
    private String sshPath;

    @Value("${cgd.batchJob.filename:ZD4270O}")
    private String batchFilePrefix;


    @Value("${cgd.batchJob.workingDirectory:null}")
    private String workingDirectory;

    private DelimitedParserInfo parserInfoTransaction;
    private DelimitedParserInfo parserInfoBalances;


    private Semaphore semaphore = new Semaphore(1);

    public BatchJobController(RestTemplate cgdRestTemplateBatch, RestTemplate tinkRestTemplateBatch, String clientId, String clientSecret) {

        this.cgdSvc = cgdRestTemplateBatch;
        this.tinkSvc = tinkRestTemplateBatch;
        this.clientId = clientId;
        this.clientSecret = clientSecret;


    }

    @GetMapping(path = "/forcejob", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Forces batch file processing ", httpMethod = "GET")
    public String forceBatchJob(HttpServletRequest httpServletRequest) {

        scanBatchFilesJob();

        return null;
    }


    private void initParser() {

        parserInfoTransaction = new DelimitedParserInfo();

        parserInfoTransaction.addField(ZD4270CLINUM, 10);
        parserInfoTransaction.addField(ZD4270ACCD, 23);
        parserInfoTransaction.addField(ZD4270TIPOREGISTO, 2);

        parserInfoTransaction.addField(ZD4270TXNAMT, 17);
        parserInfoTransaction.addField(ZD4270PSTGDT, 8);
        parserInfoTransaction.addField(ZD4270TXNDATE, 8);
        parserInfoTransaction.addField(ZD4270GUEFFDT, 8);
        parserInfoTransaction.addField(ZD4270TXNDESCTX, 40);
        parserInfoTransaction.addField(ZD4270MCC, 4);
        parserInfoTransaction.addField(ZD4270TXNCD, 5);
        parserInfoTransaction.addField(ZD4270PLID, 23);

        parserInfoBalances = new DelimitedParserInfo();

        parserInfoBalances.addField(ZD4270CLINUM, 10);
        parserInfoBalances.addField(ZD4270ACCD, 23);
        parserInfoBalances.addField(ZD4270TIPOREGISTO, 2);
        parserInfoBalances.addField(ZD4270AVAILBAL, 17);
        parserInfoBalances.addField(ZD4270CURBALAM, 17);

    }

    private TinkAccount getTinkTransactionAccountByUser(Map<Long, Map<String, TinkAccount>> accountsByUser, Long numClient, String accountNumber, String plasticNumber) {

        Map<String, TinkAccount> accountsOfUser = null;
        if (!accountsByUser.containsKey(numClient)) {

            accountsOfUser = new HashMap<>();
        } else accountsOfUser = accountsByUser.get(numClient);

        TinkAccount account = null;
        if (!accountsOfUser.containsKey(accountNumber)) {

            account = new TinkAccount(numClient, accountNumber);
            account.setType("CREDIT_CARD");
            account.setClosed(false);

            accountsOfUser.put(accountNumber, account);
        } else account = accountsOfUser.get(accountNumber);

        if (plasticNumber.length() > 0) {
            account.setNumber(ConversionUtils.maskAccountNumber(plasticNumber));
        }

        return account;
    }

    private void processTransactionLine(Map<Long, Map<String, TinkAccount>> accountsByUser, DelimitedParserResult rawTransac) {

        Long numClient = rawTransac.getParameterAsLong(ZD4270CLINUM);
        String accountNumber = rawTransac.getParameterAsString(ZD4270ACCD);
        String plasticNumber = rawTransac.getParameterAsString(ZD4270PLID);
        TinkAccount account = getTinkTransactionAccountByUser(accountsByUser, numClient, accountNumber, plasticNumber);
/*
        if (account.getTransactions() == null) {

            account.setTransactions(new ArrayList<>());
        }
*/
        TinkTransaction trans = new TinkTransaction();

        trans.setAmount(rawTransac.getParameterAsAmount(ZD4270TXNAMT));
        trans.setDate(rawTransac.getParameterAsDate(ZD4270TXNDATE).getTime());
        trans.setDescription(rawTransac.getParameterAsString(ZD4270TXNDESCTX));
        trans.setPending(false);

        //      account.getTransactions().add(trans);


        /*
          private float amount;
    private long date;
    private String description;
    private String externalId;
    private Map<String, String> payload;
    private boolean pending;
    private String tinkId;
    private String type;
         */


    }


    private void processFile(File file) {

        Map<Long, Map<String, TinkAccount>> accountsByUser = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {

                int type = Integer.parseInt(line.substring(33, 35));

                if (type == 1) {
                    DelimitedParserResult rawTransac = parserInfoTransaction.parserLine(line);
                    this.processTransactionLine(accountsByUser, rawTransac);

                } else {
                    DelimitedParserResult rawBalance = parserInfoBalances.parserLine(line);

                }

            }
        } catch (Exception e) {

            LOGGER.error("scanBatchFilesJob ", e);
        }

        //buff.append(a.getAccountFullKey());
        //buff.append(requestContext.getSessionContext().getNumCliente());


    }


    private void scanBatchFilesJob() {


        SSHClient ssh = null;
        try {

            if (!this.semaphore.tryAcquire()) return;

            ssh = new SSHClient();
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            // ssh.useCompression(); // Can lead to significant speedup (needs JZlib in classpath)
            //   ssh.loadKnownHosts();
            ssh.connect(this.sshHost);

            //ssh.authPublickey(System.getProperty("user.name"));
            ssh.authPassword(this.sshUserName, this.sshPassword);
            SFTPClient sftpClient = ssh.newSFTPClient();

            List<RemoteResourceInfo> files = sftpClient.ls(this.sshPath);

            for (RemoteResourceInfo sshFile : files) {

                try {
                    if (!sshFile.isRegularFile()) continue;

                    if (!sshFile.getName().startsWith(batchFilePrefix)) continue;

                    if (this.batchFilesRepository.findById(sshFile.getName()).isPresent()) continue;
                    String localFileName = this.workingDirectory + sshFile.getName();
                    File localFile = new File(localFileName);
                    if (!localFile.exists() || localFile.length() != sshFile.getAttributes().getSize()) {

                        ssh.newSCPFileTransfer().download(sshFile.getPath(), localFileName);
                    }
                    ssh.close();
                    this.checkForFilesToProcess();

                } catch (Exception e) {

                    LOGGER.error("scanBatchFilesJob " + sshFile.getName(), e);
                }
            }

            this.checkForFilesToProcess();
            // ssh.newSCPFileTransfer().download(this.sshPath, new FileSystemFile(this.workingDirectory));
        } catch (Exception e) {

            LOGGER.error("scanBatchFilesJob ", e);

        } finally {

            this.semaphore.release();
            try {
                if (ssh != null) ssh.disconnect();
            } catch (IOException e) {

                LOGGER.error("scanBatchFilesJob ", e);
            }
        }

    }

    private void checkForFilesToProcess() {

        File folder = new File(this.workingDirectory);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {

            if (!file.isFile()) continue;

            Optional<BatchFile> jobFile = this.batchFilesRepository.findById(file.getName());

            if (jobFile.isPresent()) {
                if (jobFile.get().getStatus() == 1) continue;
            }


            this.processFile(file);


        }

    }


    @Scheduled(cron = "0/10 * * * * *")
    @SchedulerLock(name = "cardsBatchJob")
    public void batchFileJob() {


    }
}
