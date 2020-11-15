package com.cgd.tinkConnector;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class BatchJobController extends BaseController {

    public BatchJobController(RestTemplate cgdRestTemplateBatch, RestTemplate tinkRestTemplateBatch, String clientId, String clientSecret) {

        this.cgdSvc = cgdRestTemplateBatch;
        this.tinkSvc = tinkRestTemplateBatch;
        this.clientId = clientId;
        this.clientSecret = clientSecret;


    }

    @Scheduled(cron = "0/10 * * * * *")
    public void taskExecution() {
        System.out.println("Hello World");
    }
}
