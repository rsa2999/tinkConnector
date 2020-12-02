package com.cgd.tinkConnector;

import com.cgd.tinkConnector.Clients.*;
import com.cgd.tinkConnector.Model.CGDAccount;
import com.cgd.tinkConnector.Model.IO.*;
import com.cgd.tinkConnector.Model.Tink.TinkAccount;
import com.cgd.tinkConnector.Model.Tink.TinkTransactionAccount;
import com.cgd.tinkConnector.entities.TestUsers;
import com.cgd.tinkConnector.entities.TinkUserAccounts;
import com.cgd.tinkConnector.entities.TinkUsers;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        Runnable task = () -> processUpload(request);
        this.executor.execute(task);
        TransactionsUploadResponse response = new TransactionsUploadResponse();
        return response;
    }

    @GetMapping(path = "/addUser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Receives a batch movements for upload", httpMethod = "POST")
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

    @PostMapping(path = "/unsubscribe", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "unsubscribe a user on tink ", httpMethod = "POST")
    public TinkUnsubscribeResponse unsubscribeFromTink(HttpServletRequest httpServletRequest, @RequestBody TinkUnsubscribeRequest request) {

        Runnable task = () -> processUnsubscribe(request);
        this.executor.execute(task);
        TinkUnsubscribeResponse response = new TinkUnsubscribeResponse();
        return response;
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


    private void processUpload(TransactionsUploadRequest request) {

        // ObjectMapper mapper = new ObjectMapper();

        // request.setTinkId("aa8c025e1e9947ecb48991d9b22bd479");


        boolean hasErrors = false;
        try {

            //String x = mapper.writeValueAsString(request);

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

                tinkClient.ingestTransactions(svcToken.getAccessToken(), tinkUser, transactions);
                registerServiceCall(request, TinkServices.INGEST_TRANSACTIONS.getServiceCode(), transactions);

            } catch (HttpClientErrorException e) {

                LOGGER.error(String.format("processUpload : subscription %s", request.getSubscriptionId()), e);
                registerServiceCallWithError(request, TinkServices.INGEST_TRANSACTIONS.getServiceCode(), transactions, e);
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
