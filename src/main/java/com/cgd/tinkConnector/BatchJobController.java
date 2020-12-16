package com.cgd.tinkConnector;


import com.cgd.tinkConnector.Clients.CGDClient;
import com.cgd.tinkConnector.Clients.OAuthToken;
import com.cgd.tinkConnector.Clients.TinkClient;
import com.cgd.tinkConnector.Clients.TinkServices;
import com.cgd.tinkConnector.Model.IO.TransactionsUploadRequest;
import com.cgd.tinkConnector.Model.Tink.TinkAccount;
import com.cgd.tinkConnector.Model.Tink.TinkTransaction;
import com.cgd.tinkConnector.Model.Tink.TinkTransactionAccount;
import com.cgd.tinkConnector.Repositories.BatchFilesRepository;
import com.cgd.tinkConnector.Utils.ConversionUtils;
import com.cgd.tinkConnector.Utils.DynamicProperties;
import com.cgd.tinkConnector.entities.BatchFile;
import com.cgd.tinkConnector.entities.TinkUserAccounts;
import com.cgd.tinkConnector.entities.TinkUsers;
import com.cgd.tinkConnector.parser.DelimitedParserInfo;
import com.cgd.tinkConnector.parser.DelimitedParserResult;
import com.cgd.tinkConnector.translators.*;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;


@Component
public class BatchJobController extends BaseController {

    private static Logger LOGGER = LoggerFactory.getLogger(BatchJobController.class);

    public static BatchJobController jobComponent;

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

    @Value("${cgd.disablePCESubscriptionCheck:false}")
    private boolean disablePCESubscriptionCheck;

    @Value("${cgd.batchJob.workingDirectory:null}")
    private String workingDirectory;

    @Autowired
    protected BatchFilesRepository batchFilesRepository;

    private DelimitedParserInfo parserInfoTransaction;
    private DelimitedParserInfo parserInfoBalances;

    private Semaphore semaphore = new Semaphore(1);
    private Map<Long, String> clientSubscriptions = new HashMap<>();

    private TinkUsersTranslator usersTranslator;
    private UserTranslatorType translatorType;

    public BatchJobController(RestTemplate cgdRestTemplateBatch, RestTemplate tinkRestTemplateBatch, String clientId, String clientSecret) {

        this.cgdSvc = cgdRestTemplateBatch;
        this.tinkSvc = tinkRestTemplateBatch;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.initParser();
        jobComponent = this;


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
        parserInfoBalances.addField(ZD4270AVAILBAL, 15);
        parserInfoBalances.addField(ZD4270CURBALAM, 15);

    }

    private TinkAccount getTinkTransactionAccountByUser(Map<String, Map<String, TinkAccount>> accountsByUser, Long numClient, String accountNumber, String plasticNumber) {

        String tinkId = this.usersTranslator.getTinkIdForClientNumber(numClient);

        if (tinkId == null) return null;


        Map<String, TinkAccount> accountsOfUser = null;
        if (!accountsByUser.containsKey(tinkId)) {

            accountsOfUser = new HashMap<>();
            accountsByUser.put(tinkId, accountsOfUser);
        } else accountsOfUser = accountsByUser.get(tinkId);

        TinkAccount account = null;
        if (!accountsOfUser.containsKey(accountNumber)) {

            account = new TinkAccount(numClient, accountNumber);
            account.setType("CREDIT_CARD");
            account.setClosed(false);

            accountsOfUser.put(accountNumber, account);
        } else account = accountsOfUser.get(accountNumber);

        if (plasticNumber != null && plasticNumber.length() > 0) {
            account.setNumber(ConversionUtils.maskAccountNumber(plasticNumber));
        }

        return account;
    }

    private boolean processBalanceLine(Map<String, Map<String, TinkAccount>> accountsByUser, DelimitedParserResult rawTransac) {

        Long numClient = rawTransac.getParameterAsLong(ZD4270CLINUM);
        String accountNumber = rawTransac.getParameterAsString(ZD4270ACCD);
        TinkAccount account = getTinkTransactionAccountByUser(accountsByUser, numClient, accountNumber, null);

        if (account == null) return false;

        float availableBalance = rawTransac.getParameterAsAmount(ZD4270AVAILBAL);
        float currentBalance = rawTransac.getParameterAsAmount(ZD4270CURBALAM);

        account.setBalance(currentBalance);
        account.setAvailableCredit(availableBalance);

        return true;

    }

    private boolean processTransactionLine(Map<String, Map<String, TinkAccount>> accountsByUser, DelimitedParserResult rawTransac) {

        Long numClient = rawTransac.getParameterAsLong(ZD4270CLINUM);
        String accountNumber = rawTransac.getParameterAsString(ZD4270ACCD);
        String plasticNumber = rawTransac.getParameterAsString(ZD4270PLID);
        TinkAccount account = getTinkTransactionAccountByUser(accountsByUser, numClient, accountNumber, plasticNumber);

        if (account == null) {
            //user nao subscripto
            return false;

        }

        if (account.getTransactions() == null) {

            account.setTransactions(new ArrayList<>());
        }

        TinkTransaction trans = new TinkTransaction();

        trans.setAmount(rawTransac.getParameterAsAmount(ZD4270TXNAMT));
        trans.setDate(rawTransac.getParameterAsDate(ZD4270TXNDATE).getTime());
        trans.setDescription(rawTransac.getParameterAsString(ZD4270TXNDESCTX));

        if (trans.getDescription().length() == 0) {
            return false;
        }

        trans.setPending(false);
        trans.setExternalId(ConversionUtils.generateTransactionExternalId(numClient, accountNumber, trans.getAmount(), trans.getDescription(), trans.getDate(), 1));
        trans.setType("CREDIT_CARD");

        if (account.getTransactionsByIdAndCount() == null) account.setTransactionsByIdAndCount(new HashMap<>());

        if (account.getTransactionsByIdAndCount().containsKey(trans.getExternalId())) {

            int v = account.getTransactionsByIdAndCount().get(trans.getExternalId()) + 1;
            account.getTransactionsByIdAndCount().put(trans.getExternalId(), v);
            trans.setExternalId(ConversionUtils.generateTransactionExternalId(numClient, accountNumber, trans.getAmount(), trans.getDescription(), trans.getDate(), v));


        } else {
            account.getTransactionsByIdAndCount().put(trans.getExternalId(), 1);
        }


        account.getTransactions().add(trans);

        return true;


    }

    /*

    private void getTinkIdForClientNumber(List<Long> numClients) {

        if (this.clientSubscriptions == null) {

            List<TestUsers> testUsers = this.testUsersRepository.findAll();
            this.clientSubscriptions = new HashMap<>();

            for (TestUsers u : testUsers) {
                this.clientSubscriptions.put(u.getNumClient(), u.getTinkUserId());
            }
        }

        try {
            CGDClient cgdClient = new CGDClient(this.cgdSvc);


            TinkCardSubscriptionCheckResponse response = cgdClient.checkTinkCardSubscriptions(numClients, 1);

            if (response.getSubscriptions() == null || response.getSubscriptions().size() == 0) {
                return;
            }

            for (Long num : numClients) {

                if (response.getSubscriptions().containsKey(num)) {

                    TinkCardSubscription sub = response.getSubscriptions().get(num);
                    this.clientSubscriptions.put(num, sub.getTinkId());
                } else {
                    this.clientSubscriptions.put(num, "");

                }

            }

        } catch (Exception e) {

            LOGGER.error("scanBatchFilesJob ", e);


        }


    }

    private String getTinkIdForClientNumber(Long numClient) {

        // numClient = new Long(157103482);

        if (this.clientSubscriptions.containsKey(numClient)) {

            String tinkId = this.clientSubscriptions.get(numClient);
            if (tinkId.length() == 0) return null;
            return tinkId;
        }

        Optional<TestUsers> testUser = this.testUsersRepository.findById(numClient);

        if (testUser.isPresent()) {

            this.clientSubscriptions.put(numClient, testUser.get().getTinkUserId());
            return testUser.get().getTinkUserId();
        }

        if (disablePCESubscriptionCheck) {

            this.clientSubscriptions.put(numClient, "");
            return null;
        }


        try {
            CGDClient cgdClient = new CGDClient(this.cgdSvc);

            TinkCardSubscriptionCheckResponse response = cgdClient.checkTinkCardSubscriptions(numClient, 1);

            if (response.getSubscriptions() == null || response.getSubscriptions().size() == 0) {

                this.clientSubscriptions.put(numClient, "");
                return null;
            } else {

                if (!response.getSubscriptions().containsKey(numClient)) {
                    this.clientSubscriptions.put(numClient, "");
                    return null;
                } else {

                    TinkCardSubscription sub = response.getSubscriptions().get(numClient);
                    this.clientSubscriptions.put(numClient, sub.getTinkId());
                    return sub.getTinkId();
                }
            }

        } catch (Exception e) {

            LOGGER.error("scanBatchFilesJob ", e);
            this.clientSubscriptions.put(numClient, "");
            return null;
        }
    }

     */

    private boolean uploadToTink(Map<String, Map<String, TinkAccount>> accountsByUser) {


        try {

            TinkClient tinkClient = new TinkClient(tinkSvc, clientId, clientSecret);
            OAuthToken svcToken = tinkClient.token("client_credentials", TinkClient.ALL_SCOPES, null, null);


            TransactionsUploadRequest request = new TransactionsUploadRequest();
            for (String tinkId : accountsByUser.keySet()) {

                Map<String, TinkAccount> accountsOfUser = accountsByUser.get(tinkId);
                request.setTinkId(tinkId);

                TinkUsers tinkUser = this.getTinkUserByTinkId(tinkClient, svcToken.getAccessToken(), tinkId);

                List<TinkAccount> accountsToUpload = new ArrayList<>();
                List<TinkTransactionAccount> transactions = new ArrayList<>();

                for (TinkAccount account : accountsOfUser.values()) {

                    if (account.getNumber() == null) {
                        continue;
                    }
                    if (account.getName() == null) {
                        TinkUserAccounts userAccount = new TinkUserAccounts(account.getExternalId(), tinkId);
                        Optional<TinkUserAccounts> dbAccount = this.accountsRepository.findById(userAccount.getId());

                        if (dbAccount.isPresent()) {
                            account.setName(dbAccount.get().getAccountDescription());
                        } else account.setName("Cartão de Crédito");

                    }
                    TinkUserAccounts checkAccount = new TinkUserAccounts(account.getExternalId(), tinkUser.getId());
                    Optional<TinkUserAccounts> dbAccount = this.accountsRepository.findById(checkAccount.getId());
                    if (!dbAccount.isPresent()) {

                        accountsToUpload.add(account);
                    }
                    if (account.getTransactions() != null && account.getTransactions().size() > 0) {
                        transactions.add(account.toTransactionAccount());
                    }


                }
                this.uploadAccountsToTink(tinkClient, svcToken.getAccessToken(), request, tinkUser, accountsToUpload);

                if (transactions.size() > 0) {

                    try {

                        if (activateUploadToTink) {

                            tinkClient.ingestTransactions(svcToken.getAccessToken(), tinkUser, transactions);
                        }
                        registerServiceCall(request, TinkServices.INGEST_TRANSACTIONS.getServiceCode(), transactions);

                    } catch (HttpClientErrorException e) {

                        LOGGER.error("uploadToTink ", e);
                        registerServiceCallWithError(request, TinkServices.INGEST_TRANSACTIONS.getServiceCode(), transactions, e);

                    }
                }
            }

            accountsByUser.clear();
            return true;
        } catch (Exception e) {

            LOGGER.error("uploadToTink ", e);
            return false;

        }

    }


    private void processFileForNumClients(File file) {

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            List<Long> numClients = new ArrayList<>();

            while ((line = br.readLine()) != null) {

                int type = Integer.parseInt(line.substring(33, 35));

                DelimitedParserResult rawTransac = null;

                if (type == 1) {
                    rawTransac = parserInfoTransaction.parserLine(line);


                } else {
                    rawTransac = parserInfoBalances.parserLine(line);

                }
                Long numClient = rawTransac.getParameterAsLong(ZD4270CLINUM);

                if (!numClients.contains(numClient)) {
                    numClients.add(numClient);
                }

                if (numClients.size() > 2000) {

                    this.usersTranslator.getTinkIdForClientNumber(numClients);
                    numClients.clear();
                }

            }

        } catch (Exception e) {

            LOGGER.error("scanBatchFilesJob ", e);
        } finally {


        }
    }

    private void processFile(File file) {

        if (this.translatorType == UserTranslatorType.PCE_GROUPED_USER) {

            this.processFileForNumClients(file);
        }

        Map<String, Map<String, TinkAccount>> accountsByUser = new HashMap<>();

        String accountNumber = null;
        int numberOfAccountsFound = 0;

        int numberOfLinesProcessed = 0;
        int totalLines = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {

                int type = Integer.parseInt(line.substring(33, 35));

                DelimitedParserResult rawTransac = null;
                totalLines++;
                if (type == 1) {
                    rawTransac = parserInfoTransaction.parserLine(line);
                    if (!this.processTransactionLine(accountsByUser, rawTransac)) {
                        continue;
                    }

                } else {
                    rawTransac = parserInfoBalances.parserLine(line);
                    if (!this.processBalanceLine(accountsByUser, rawTransac)) {
                        continue;
                    }

                }
                numberOfLinesProcessed++;

                String currentAccount = rawTransac.getParameterAsString(ZD4270ACCD);
                if (accountNumber == null) accountNumber = currentAccount;
                else if (!accountNumber.equals(currentAccount)) {


                    accountNumber = currentAccount;
                    numberOfAccountsFound++;
                    if (numberOfAccountsFound > 30) {

                        numberOfAccountsFound = 0;
                        this.uploadToTink(accountsByUser);
                    }


                }


            }
            this.uploadToTink(accountsByUser);

            Optional<BatchFile> jobFile = this.batchFilesRepository.findById(file.getName());

            BatchFile job;
            if (jobFile.isPresent()) {
                job = jobFile.get();
            } else job = new BatchFile();
            job.setFileName(file.getName());
            job.setProcessedLines(numberOfLinesProcessed);
            job.setFileSize(file.length());
            job.setProcessingDate(Calendar.getInstance().getTime());
            job.setStatus(1);
            job.setTotalLines(totalLines);
            this.batchFilesRepository.save(job);
//            this.batchFilesRepository.flush();


        } catch (Exception e) {

            LOGGER.error("scanBatchFilesJob ", e);
        } finally {

            file.delete();
        }


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

                } catch (Exception e) {

                    LOGGER.error("scanBatchFilesJob " + sshFile.getName(), e);
                }

            }


        } catch (Exception e) {

            LOGGER.error("scanBatchFilesJob ", e);

        } finally {

            try {
                if (ssh != null) {
                    ssh.disconnect();
                    ssh.close();
                }
            } catch (IOException e) {

                LOGGER.error("scanBatchFilesJob ", e);
            }

            this.checkForFilesToProcess();
            this.semaphore.release();

            this.clientSubscriptions = null;
        }

    }

    private Date extractDatefromFileName(String filename) {

        String[] comps = filename.split("\\.");
        //ZD4270O.D20201212.T101711

        return ConversionUtils.stringToDate(comps[1].substring(1));

    }

    private void checkForFilesToProcess() {

        File folder = new File(this.workingDirectory);
        File[] listOfFiles = folder.listFiles();

        List<File> filesToProcess = new ArrayList<>();
        for (File file : listOfFiles) {

            if (!file.isFile()) continue;

            Optional<BatchFile> jobFile = this.batchFilesRepository.findById(file.getName());

            if (jobFile.isPresent()) {
                if (jobFile.get().getStatus() == 1) continue;
            }
            filesToProcess.add(file);

        }

        if (filesToProcess.size() > 0) {

            filesToProcess.sort(new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {

                    return extractDatefromFileName(o1.getName()).compareTo(extractDatefromFileName(o2.getName()));
                }
            });

            this.initUserTranslator();

            for (File file : filesToProcess) this.processFile(file);

        }


    }

    private void initUserTranslator() {

        if (TinkConnectorConfiguration.properties == null) {

            TinkConnectorConfiguration.properties = new DynamicProperties(this.propertiesRepository);
        }


        if (!TinkConnectorConfiguration.properties.existsProperty(DynamicProperties.CGD_USER_TRANSLATION_TYPE)) {

            TinkConnectorConfiguration.properties.saveProperty(DynamicProperties.CGD_USER_TRANSLATION_TYPE, "4");
        }

        int userTranslationType = TinkConnectorConfiguration.properties.getPropertyAsInteger(DynamicProperties.CGD_USER_TRANSLATION_TYPE);

        this.translatorType = UserTranslatorType.fromValue((short) userTranslationType);

        switch (translatorType) {

            case TEST_USERS_ONLY:

                this.usersTranslator = new TestUsersOnlyUserTranslator(this.testUsersRepository);
                break;

            case PCE_SINGLE_USER:

                this.usersTranslator = new PCEBasedSingleUserTranslator(new CGDClient(this.cgdSvc), this.testUsersRepository);
                break;

            case PCE_SUSCRIPTIONS_SYNC:

                this.usersTranslator = new PCEBasedSyncSubscriptionsUserTranslator(new CGDClient(this.cgdSvc), this.testUsersRepository, this.subscriptionsRepository);
                break;

            case LOCAL_SUBSCRIPTION:

                this.usersTranslator = new LocalSubscriptionsUserTranslator(this.testUsersRepository, this.subscriptionsRepository);
                break;

            case PCE_GROUPED_USER:
                this.usersTranslator = new PCEBasedGroupedUserTranslator(new CGDClient(this.cgdSvc), this.testUsersRepository);
                break;

        }


    }


    @Scheduled(cron = "00 45 * * * *")
    // @Scheduled(fixedRate = 60 * 60 * 1000)
    @SchedulerLock(name = "cardsBatchJob")
    public void batchFileJob() {

        this.scanBatchFilesJob();


    }
/*
    @Scheduled(cron = "0/10 * * * * *")
    public void batchFileJob2() {

        System.out.println("job");


    }
    */

}
