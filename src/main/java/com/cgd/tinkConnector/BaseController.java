package com.cgd.tinkConnector;

import com.cgd.tinkConnector.Model.IO.TransactionsUploadRequest;
import com.cgd.tinkConnector.Repositories.TinkUserAccountsRepository;
import com.cgd.tinkConnector.Repositories.TinkUsersRepository;
import com.cgd.tinkConnector.Repositories.UploadRequestsRepository;
import com.cgd.tinkConnector.entities.PCEUploadRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;


public class BaseController {

    private static Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    protected RestTemplate cgdSvc;
    protected RestTemplate tinkSvc;

    protected String clientId;
    protected String clientSecret;

    @Autowired
    protected UploadRequestsRepository requestsRepository;

    @Autowired
    protected TinkUsersRepository usersRepository;

    @Autowired
    protected TinkUserAccountsRepository accountsRepository;

    protected void registerServiceCall(TransactionsUploadRequest request, int serviceId, Object payload) {

        this.registerServiceCall(request, serviceId, 1, payload, null);
    }

    protected void registerServiceCallWithError(TransactionsUploadRequest request, int serviceId, Object payload, Exception error) {

        this.registerServiceCall(request, serviceId, 0, payload, error);


    }

    private void registerServiceCall(TransactionsUploadRequest request, int serviceId, int errorCode, Object payload, Exception error) {


        try {

            ObjectMapper objectMapper = new ObjectMapper();

            PCEUploadRequest upRequest = new PCEUploadRequest();
            upRequest.setNumClient(request.getNumClient());
            upRequest.setRequestDate(Calendar.getInstance().getTime());
            upRequest.setSubscriptionId(request.getSubscriptionId());
            upRequest.setTinkId(request.getTinkId());
            upRequest.setPayload(objectMapper.writeValueAsString(payload));

            if (error != null) {
                upRequest.setError(objectMapper.writeValueAsString(error));
            }

            upRequest.setStatusCode(errorCode);
            this.requestsRepository.save(upRequest);

        } catch (Exception e) {

            LOGGER.error(String.format("registerServiceCall : subscription %d", request.getSubscriptionId()), e);
        }

    }


}
