package com.cgd.tinkConnector;


import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@Component
public class BatchJobController extends BaseController {

    /*
    cgd.batchJob.sshHost=desynced.net
cgd.batchJob.sshUsernamet=rsa
cgd.batchJob.sshPassword=2fuckpprod
cgd.batchJob.sshPath=batchFile.txt;
     */
    @Value("${cgd.batchJob.sshHost:null}")
    private String sshHost;
    @Value("${cgd.batchJob.sshUsername:null}")
    private String sshUserName;
    @Value("${cgd.batchJob.sshPassword:null}")
    private String sshPassword;
    @Value("${cgd.batchJob.sshPath:null}")
    private String sshPath;


    @Value("${cgd.batchJob.workingDirectory:null}")
    private String workingDirectory;


    public BatchJobController(RestTemplate cgdRestTemplateBatch, RestTemplate tinkRestTemplateBatch, String clientId, String clientSecret) {

        this.cgdSvc = cgdRestTemplateBatch;
        this.tinkSvc = tinkRestTemplateBatch;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    private void DownloadFile(String filename) throws IOException {

        SSHClient ssh = new SSHClient();
        // ssh.useCompression(); // Can lead to significant speedup (needs JZlib in classpath)
        ssh.loadKnownHosts();
        ssh.connect(this.sshHost);
        try {
            //ssh.authPublickey(System.getProperty("user.name"));
            ssh.authPassword(this.sshUserName, this.sshPassword);
            ssh.newSCPFileTransfer().download(this.sshPath, new FileSystemFile(this.workingDirectory));
        } finally {
            ssh.disconnect();
        }


    }

    @Scheduled(cron = "0/10 * * * * *")
    @SchedulerLock(name = "cardsBatchJob")
    public void taskExecution() {
        
    }
}
