package com.cgd.tinkConnector;

import com.cgd.tinkConnector.Clients.*;
import com.cgd.tinkConnector.Model.CGDAccount;
import com.cgd.tinkConnector.Model.IO.*;
import com.cgd.tinkConnector.Model.Tink.TinkAccount;
import com.cgd.tinkConnector.Model.Tink.TinkTransactionAccount;
import com.cgd.tinkConnector.Utils.DynamicProperties;
import com.cgd.tinkConnector.entities.*;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestScope
@RequestMapping("/api/v1")
public class PCEServicesController extends BaseController {

    private static Logger LOGGER = LoggerFactory.getLogger(PCEServicesController.class);
    private ConcurrentTaskExecutor executor;

    public PCEServicesController(RestTemplate cgdRestTemplate, RestTemplate tinkRestTemplate, String clientId, String clientSecret) {

        this.cgdSvc = cgdRestTemplate;
        this.tinkSvc = tinkRestTemplate;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.executor = new ConcurrentTaskExecutor();


    }

    @PostMapping(path = "/uploadTransactions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Receives a batch movements for upload", httpMethod = "POST")
    public TransactionsUploadResponse uploadTransactions(HttpServletRequest httpServletRequest, @RequestBody TransactionsUploadRequest request) {

        this.initDynamicProperties();
        Runnable task = () -> processUpload(request);
        this.executor.execute(task);
        TransactionsUploadResponse response = new TransactionsUploadResponse();
        return response;
    }

    @GetMapping(path = "/addUser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds a clientNumer for testing", httpMethod = "GET")
    public ServiceResponse addTestUser(HttpServletRequest httpServletRequest, @RequestParam String numClient, @RequestParam String tinkId) {

        ServiceResponse ret = new ServiceResponse();
        try {
            Long numC = Long.parseLong(numClient);

            if (tinkId == null || tinkId.length() == 0) return ret;

            TestUsers tUser = new TestUsers();
            tUser.setNumClient(numC);
            tUser.setTinkUserId(tinkId);
            this.testUsersRepository.save(tUser);
            ret.setResultCode(1);


        } catch (Exception e) {

            LOGGER.error("addTestUser ", e);
        }
        return ret;
    }

    @GetMapping(path = "/pingTink", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "checks tink connectivity ", httpMethod = "GET")
    public ServiceResponse pingTink(HttpServletRequest httpServletRequest) {

        ServiceResponse ret = new ServiceResponse();
        try {

            TinkClient tinkClient = new TinkClient(tinkSvc, clientId, clientSecret);
            OAuthToken svcToken = tinkClient.token("client_credentials", TinkClient.ALL_SCOPES, null, null);
            ret.setResultCode(1);

        } catch (Exception e) {

            LOGGER.error("pingTink ", e);
            throw e;
        }


        return ret;


    }


    @PostMapping(path = "/unsubscribe", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "unsubscribe a user on tink ", httpMethod = "POST")
    public TinkUnsubscribeResponse unsubscribeFromTink(HttpServletRequest httpServletRequest, @RequestBody TinkUnsubscribeRequest request) {

        Runnable task = () -> processUnsubscribe(request);
        this.executor.execute(task);
        TinkUnsubscribeResponse response = new TinkUnsubscribeResponse();
        return response;
    }

    @GetMapping(path = "/forcejob", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Forces batch file processing ", httpMethod = "GET")
    public String forceBatchJob(HttpServletRequest httpServletRequest) {


        BatchJobController.jobComponent.batchFileJob();

        return null;
    }


    @GetMapping(path = "/userlog", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds or update property configuration ", httpMethod = "GET")
    public UserInteractionsResponse getUserInteractions(HttpServletRequest httpServletRequest, @RequestParam Long numContrato) {

        UserInteractionsResponse ret = new UserInteractionsResponse();

        List<PCEClientSubscription> subs = this.subscriptionsRepository.findByNumContrato(numContrato);

        if (subs.isEmpty()) {

            return ret;

        }

        List<PCEUploadRequest> requests = new ArrayList<>();
        for (PCEClientSubscription sub : subs) {

            List<PCEUploadRequest> r = this.requestsRepository.findByTinkId(sub.getTinkUserId());
            requests.addAll(r);
        }
        requests.sort((o1, o2) -> o2.getRequestDate().compareTo(o1.getRequestDate()));


        ret.setRequests(requests);
        ret.setSubscriptions(subs);
        return ret;
    }

    @GetMapping(path = "/prop", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds or update property configuration ", httpMethod = "GET")
    public ServiceResponse addProperty(HttpServletRequest httpServletRequest, @RequestParam String propKey, @RequestParam String propValue) {

        this.initDynamicProperties();
        TinkConnectorConfiguration.properties.saveProperty(propKey, propValue);
        ServiceResponse ret = new ServiceResponse();
        ret.setResultCode(1);
        return ret;
    }

    @GetMapping(path = "/reload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Reload current config", httpMethod = "GET")
    public ServiceResponse reloadProperties(HttpServletRequest httpServletRequest) {

        this.initDynamicProperties();
        TinkConnectorConfiguration.properties.reset();
        ServiceResponse ret = new ServiceResponse();
        ret.setResultCode(1);
        return ret;
    }


    private TinkUnsubscribeResponse processUnsubscribe(TinkUnsubscribeRequest request) {

        TinkClient tinkClient = new TinkClient(tinkSvc, clientId, clientSecret);
        OAuthToken svcToken = tinkClient.token("client_credentials", TinkClient.ALL_SCOPES, null, null);

        TinkUnsubscribeResponse ret = new TinkUnsubscribeResponse();

        Optional<TinkUsers> tinkUser = this.usersRepository.findById(request.getTinkId());

        // Get user oauth token
        OAuthGrant userToken = tinkClient.usertoken(svcToken.getAccessToken(), request.getTinkId(), TinkClient.USER_SCOPE);
        OAuthToken userAuth = tinkClient.token("authorization_code", null, userToken.getCode(), null);

        if (!tinkUser.isPresent()) {

            tinkUser = Optional.of(tinkClient.getUser(userAuth.getAccessToken()));
            this.usersRepository.save(tinkUser.get());
        }

        TinkUserCredentialResponse response = tinkClient.getUserCredentials(userAuth.getAccessToken());

        List<TinkUserAccounts> accounts = this.accountsRepository.findByTinkId(request.getTinkId());

        if (accounts == null) return ret;

        int accountsInError = 0;
        for (TinkUserAccounts acc : accounts) {

            try {

                tinkClient.deleteAccount(svcToken.getAccessToken(), tinkUser.get().getExternalUserId(), acc.getExternalAccountId());

            } catch (Exception e) {
                accountsInError++;

            }
        }

        ret.setSucess(accountsInError == 0);
        return ret;
    }

    private void checkSubscriptionForUploadRequest(TransactionsUploadRequest request) {


        Optional<PCEClientSubscription> clientSubscription = this.subscriptionsRepository.findById(request.getSubscriptionId());

        Date now = Calendar.getInstance().getTime();

        if (!clientSubscription.isPresent()) {
            PCEClientSubscription newSub = new PCEClientSubscription();
            newSub.setSubscriptionId(request.getSubscriptionId());
            newSub.setNumClient(request.getNumClient());
            newSub.setNumContrato(request.getNumContrato());
            newSub.setTinkUserId(request.getTinkId());
            newSub.setSubscriptionType(request.getSubscriptionType());
            newSub.setSubscriptionStatus(1);
            newSub.setCreationDate(now);
            newSub.setUpdateDate(now);
            this.subscriptionsRepository.save(newSub);
        } else {

            clientSubscription.get().setUpdateDate(Calendar.getInstance().getTime());
            clientSubscription.get().setSubscriptionStatus(1);
            this.subscriptionsRepository.save(clientSubscription.get());
        }

    }

    private void processUpload(TransactionsUploadRequest request) {


        boolean hasErrors = false;
        try {

            this.checkSubscriptionForUploadRequest(request);


            TinkClient tinkClient = new TinkClient(tinkSvc, clientId, clientSecret);
            OAuthToken svcToken = tinkClient.token("client_credentials", TinkClient.ALL_SCOPES, null, null);

            TinkUsers tinkUser = this.getTinkUserByTinkId(tinkClient, svcToken.getAccessToken(), request.getTinkId());

            List<TinkAccount> tinkAccounts = new ArrayList<>();
            List<TinkTransactionAccount> transactions = new ArrayList<>();
            String acountType = "CREDIT_CARD";

            // MessageDigest md = MessageDigest.getInstance("MD5");


            for (CGDAccount acc : request.getAccounts()) {

                TinkUserAccounts userAccount = new TinkUserAccounts(request.getNumClient(), acc.getNumber(), tinkUser.getId());
                Optional<TinkUserAccounts> dbAccount = this.accountsRepository.findById(userAccount.getId());

                if (!dbAccount.isPresent()) {
                    tinkAccounts.add(acc.toTinkAccount(acountType, request.getNumClient()));
                }

                if (acc.getTransactions() != null && acc.getTransactions().size() > 0) {
                    transactions.add(acc.toTransactionAccount(request.getNumClient()));
                }

            }
            this.uploadAccountsToTink(tinkClient, svcToken.getAccessToken(), request, tinkUser, tinkAccounts);

            try {
                if (TinkConnectorConfiguration.properties.getPropertyAsBoolean(DynamicProperties.CGD_ACTIVATE_UPLOAD_TO_TINK)) {

                    boolean isValid = validatePayloadBeforeUpload(TinkServices.INGEST_TRANSACTIONS.getServiceCode(), transactions);


                    tinkClient.ingestTransactions(svcToken.getAccessToken(), tinkUser, transactions);

                }

                registerServiceCall(request, TinkServices.INGEST_TRANSACTIONS.getServiceCode(), transactions);

            } catch (HttpClientErrorException e) {

                LOGGER.error(String.format("processUpload : subscription %s", request.getSubscriptionId()), e);
                registerServiceCallWithError(request, TinkServices.INGEST_TRANSACTIONS.getServiceCode(), e.getStatusCode().value(), transactions, e);
                hasErrors = true;
            }

            if (!hasErrors) {

                CGDClient cgdClient = new CGDClient(this.cgdSvc);
                List<Long> clientNumber = new ArrayList<>();
                clientNumber.add(request.getNumClient());
                cgdClient.updateTinkCardSubscriptions(request.getSubscriptionType(), clientNumber);
            }

        } catch (Exception e) {

            LOGGER.error(String.format("processUpload : subscription %s", request.getSubscriptionId()), e);

        }


    }
}
