package com.cgd.tinkConnector;

import com.cgd.tinkConnector.Clients.CGDClient;
import com.cgd.tinkConnector.Clients.TinkClient;
import com.cgd.tinkConnector.Model.*;
import com.cgd.tinkConnector.Repositories.UploadRequestsRepository;
import com.cgd.tinkConnector.Utils.ConversionUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestScope
@RequestMapping("/api/v1")
public class PCEServicesController extends BaseController {

    private static Logger LOGGER = LoggerFactory.getLogger(PCEServicesController.class);

    private ConcurrentTaskExecutor executor;

    @Autowired
    private UploadRequestsRepository requestsRepository;

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


    private void processUpload(TransactionsUploadRequest request) {

        try {

            TinkClient tinkClient = new TinkClient(tinkSvc, clientId, clientSecret);
            TinkClient.OAuthToken svcToken = tinkClient.token("client_credentials", TinkClient.ALL_SCOPES, null, null);

            List<TinkAccount> tinkAccounts = new ArrayList<>();

            List<TinkTransactionAccount> transactions = new ArrayList<>();

            for (CGDAccount acc : request.getAccounts()) {

                acc.setAvailableCredit(ConversionUtils.formatAmmount(acc.getAvailableCredit()));
                acc.setBalance(ConversionUtils.formatAmmount(acc.getBalance()));
                acc.setReservedAmount(ConversionUtils.formatAmmount(acc.getReservedAmount()));
                tinkAccounts.add(acc.toTinkAccount());
                transactions.add(acc.toTransactionAccount());
            }
            tinkClient.ingestAccounts(svcToken.getAccessToken(), request.getTinkId(), tinkAccounts);

            tinkClient.ingestTransactions(svcToken.getAccessToken(), request.getTinkId(), transactions);

            CGDClient cgdClient = new CGDClient(this.cgdSvc);

            List<Long> clientNumber = new ArrayList<>();
            clientNumber.add(request.getNumClient());

            cgdClient.updateTinkCardSubscriptions(request.getSubscriptionType(), clientNumber);


        } catch (Exception e) {


        }


    }
}
