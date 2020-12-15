package com.cgd.tinkConnector;

import com.cgd.tinkConnector.Clients.OAuthGrant;
import com.cgd.tinkConnector.Clients.OAuthToken;
import com.cgd.tinkConnector.Clients.TinkClient;
import com.cgd.tinkConnector.Clients.TinkServices;
import com.cgd.tinkConnector.Model.IO.TransactionsUploadRequest;
import com.cgd.tinkConnector.Model.Tink.TinkAccount;
import com.cgd.tinkConnector.Repositories.*;
import com.cgd.tinkConnector.entities.PCEUploadRequest;
import com.cgd.tinkConnector.entities.TinkUserAccounts;
import com.cgd.tinkConnector.entities.TinkUsers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;


public class BaseController {

    private static Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    protected RestTemplate cgdSvc;
    protected RestTemplate tinkSvc;

    protected String clientId;
    protected String clientSecret;

    @Value("${cgd.invertTransactionsSignal:true}")
    private static boolean inverTransactionsSignal;

    @Value("${cgd.activateUploadToTink:false}")
    protected boolean activateUploadToTink;

    @Autowired
    protected UploadRequestsRepository requestsRepository;

    @Autowired
    protected TinkUsersRepository usersRepository;

    @Autowired
    protected TinkUserAccountsRepository accountsRepository;

    @Autowired
    protected TestUsersRepository testUsersRepository;

    @Autowired
    protected PCEClientSubscriptionRepository subscriptionsRepository;

    @Autowired
    protected DatabasePropertiesRepository propertiesRepository;


    protected void registerServiceCall(TransactionsUploadRequest request, int serviceId, Object payload) {

        this.registerServiceCall(request, serviceId, 1, payload, null);
    }

    protected void registerServiceCallWithError(TransactionsUploadRequest request, int serviceId, Object payload, Exception error) {

        this.registerServiceCall(request, serviceId, 0, payload, error);


    }

    protected TinkUsers getTinkUserByTinkId(TinkClient tinkClient, String accessToken, String tinkId) {

        Optional<TinkUsers> tinkUser = this.usersRepository.findById(tinkId);

        if (!tinkUser.isPresent()) {

            // Get user oauth token
            OAuthGrant userToken = tinkClient.usertoken(accessToken, tinkId, TinkClient.USER_SCOPE);
            OAuthToken userAuth = tinkClient.token("authorization_code", null, userToken.getCode(), null);
            tinkUser = Optional.of(tinkClient.getUser(userAuth.getAccessToken()));
            this.usersRepository.save(tinkUser.get());
        }

        return tinkUser.get();

    }

    protected boolean uploadAccountsToTink(TinkClient tinkClient, String accessToken, TransactionsUploadRequest request, TinkUsers user, List<TinkAccount> tinkAccounts) {

        if (!this.activateUploadToTink) return true;

        if (tinkAccounts.size() == 0) return true;


        List<TinkAccount> accountsToSave = new ArrayList<>();
        try {

            tinkClient.ingestAccounts(accessToken, user.getExternalUserId(), tinkAccounts);
            accountsToSave.addAll(tinkAccounts);

            registerServiceCall(request, TinkServices.INGEST_ACOUNTS.getServiceCode(), tinkAccounts);
            return true;
        } catch (HttpClientErrorException e) {

            LOGGER.error("processUpload", e);
            if (e.getStatusCode().value() == 409) {
                // uma das contas ja existe ,going account by account mode

                List<TinkAccount> testAccount = new ArrayList<>();
                for (TinkAccount a : tinkAccounts) {

                    testAccount.clear();
                    testAccount.add(a);
                    try {
                        tinkClient.ingestAccounts(accessToken, user.getExternalUserId(), testAccount);

                    } catch (HttpClientErrorException e1) {
                        if (e.getStatusCode().value() == 409) {

                            accountsToSave.add(a);
                        }

                    }

                }
                return true;
            } else {
                registerServiceCallWithError(request, TinkServices.INGEST_ACOUNTS.getServiceCode(), tinkAccounts, e);

            }
            return false;
        } finally {

            try {

                Date now = Calendar.getInstance().getTime();
                for (TinkAccount ac : accountsToSave) {

                    TinkUserAccounts ua = new TinkUserAccounts(ac.getExternalId(), user.getId());
                    ua.setAccountNumber(ac.getNumber());
                    ua.setAccountDescription(ac.getName());
                    ua.setUploadDate(now);
                    this.accountsRepository.save(ua);

                }
                this.accountsRepository.flush();
            } catch (Exception e) {

                LOGGER.error("processUpload", e);
            }

        }


    }


    protected void registerServiceCall(TransactionsUploadRequest request, int serviceId, int errorCode, Object payload, Exception error) {


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

            LOGGER.error(String.format("registerServiceCall : subscription %s", request.getSubscriptionId()), e);
        }

    }


}
