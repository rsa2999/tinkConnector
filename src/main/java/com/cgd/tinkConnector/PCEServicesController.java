package com.cgd.tinkConnector;

import com.cgd.tinkConnector.Clients.*;
import com.cgd.tinkConnector.Model.CGDAccount;
import com.cgd.tinkConnector.Model.CGDTransaction;
import com.cgd.tinkConnector.Model.IO.*;
import com.cgd.tinkConnector.Model.Tink.TinkAccount;
import com.cgd.tinkConnector.Model.Tink.TinkTransactionAccount;
import com.cgd.tinkConnector.entities.TinkUserAccounts;
import com.cgd.tinkConnector.entities.TinkUsers;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
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

        Runnable task = () -> processUpload(request);
        this.executor.execute(task);
        TransactionsUploadResponse response = new TransactionsUploadResponse();
        return response;
    }

    @PostMapping(path = "/unsubscribe", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "unsubscribe a user on tink ", httpMethod = "POST")
    public TinkUnsubscribeResponse unsubscribeFromTink(HttpServletRequest httpServletRequest, @RequestBody TinkUnsubscribeRequest request) {

        Runnable task = () -> processUnsubscribe(request);
        this.executor.execute(task);
        TinkUnsubscribeResponse response = new TinkUnsubscribeResponse();
        return response;
    }


    private boolean uploadAccountsToTink(TinkClient tinkClient, String accessToken, TransactionsUploadRequest request, TinkUsers user, List<TinkAccount> tinkAccounts) {

        if (tinkAccounts.size() == 0) return true;


        List<TinkAccount> accountsToSave = new ArrayList<>();
        try {

            tinkClient.ingestAccounts(accessToken, user.getExternalUserId(), tinkAccounts);
            accountsToSave.addAll(tinkAccounts);

            registerServiceCall(request, TinkServices.INGEST_ACOUNTS.getServiceCode(), tinkAccounts);
            return true;
        } catch (HttpClientErrorException e) {

            LOGGER.error(String.format("processUpload : subscription %s", request.getSubscriptionId()), e);
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
            }
            return false;
        } finally {

            try {

                Date now = Calendar.getInstance().getTime();
                for (TinkAccount ac : accountsToSave) {

                    TinkUserAccounts ua = new TinkUserAccounts(ac.getExternalId(), user.getId());
                    ua.setAccountNumber(ac.getNumber());
                    ua.setUploadDate(now);
                    this.accountsRepository.save(ua);
                    //accountsToSave.add(ua);
                }
            } catch (Exception e) {
                LOGGER.error(String.format("processUpload : subscription %s", request.getSubscriptionId()), e);
            }

        }


    }


    private void processUnsubscribe(TinkUnsubscribeRequest request) {

        TinkClient tinkClient = new TinkClient(tinkSvc, clientId, clientSecret);
        OAuthToken svcToken = tinkClient.token("client_credentials", TinkClient.ALL_SCOPES, null, null);


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

        return;
    }

    private void processUpload(TransactionsUploadRequest request) {

        // ObjectMapper mapper = new ObjectMapper();


        boolean hasErrors = false;
        try {

            //String x = mapper.writeValueAsString(request);

            TinkClient tinkClient = new TinkClient(tinkSvc, clientId, clientSecret);
            OAuthToken svcToken = tinkClient.token("client_credentials", TinkClient.ALL_SCOPES, null, null);

            Optional<TinkUsers> tinkUser = this.usersRepository.findById(request.getTinkId());

            if (!tinkUser.isPresent()) {

                // Get user oauth token
                OAuthGrant userToken = tinkClient.usertoken(svcToken.getAccessToken(), request.getTinkId(), TinkClient.USER_SCOPE);
                OAuthToken userAuth = tinkClient.token("authorization_code", null, userToken.getCode(), null);
                tinkUser = Optional.of(tinkClient.getUser(userAuth.getAccessToken()));
                this.usersRepository.save(tinkUser.get());
            }

            List<TinkAccount> tinkAccounts = new ArrayList<>();

            List<TinkTransactionAccount> transactions = new ArrayList<>();

            String acountType = "CREDIT_CARD";

            MessageDigest md = MessageDigest.getInstance("MD5");


            for (CGDAccount acc : request.getAccounts()) {

                for (CGDTransaction t : acc.getTransactions()) {

                    t.setTinkId(request.getTinkId());
                    // t.setAmount(ConversionUtils.formatAmmount(t.getAmount()));
                }


                TinkUserAccounts userAccount = new TinkUserAccounts(acc.getExternalId(), tinkUser.get().getId());
                Optional<TinkUserAccounts> dbAccount = this.accountsRepository.findById(userAccount.getUniqueId());

                if (!dbAccount.isPresent()) {
                    tinkAccounts.add(acc.toTinkAccount(acountType));
                }

                transactions.add(acc.toTransactionAccount());

            }

            this.uploadAccountsToTink(tinkClient, svcToken.getAccessToken(), request, tinkUser.get(), tinkAccounts);


            try {

                tinkClient.ingestTransactions(svcToken.getAccessToken(), tinkUser.get(), transactions);
                registerServiceCall(request, TinkServices.INGEST_ACOUNTS.getServiceCode(), transactions);

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
