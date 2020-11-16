package com.cgd.tinkConnector;

import com.cgd.tinkConnector.Clients.TinkClient;
import com.cgd.tinkConnector.Model.TinkAccount;
import com.cgd.tinkConnector.Model.TransactionsUploadRequest;
import com.cgd.tinkConnector.Model.TransactionsUploadResponse;
import com.cgd.tinkConnector.Repositories.UploadRequestsRepository;
import com.cgd.tinkConnector.Utils.ConversionUtils;
import io.swagger.annotations.ApiOperation;
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

@RestController
@RequestScope
@RequestMapping("/api/v1")
public class PCEServicesController extends BaseController {

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

            for (TinkAccount acc : request.getAccounts()) {

                acc.setAvailableCredit(ConversionUtils.formatAmmount(acc.getAvailableCredit()));
                acc.setBalance(ConversionUtils.formatAmmount(acc.getBalance()));
                acc.setReservedAmount(ConversionUtils.formatAmmount(acc.getReservedAmount()));
            }
            tinkClient.ingestAccounts(svcToken.getAccessToken(), request.getTinkId(), request.getAccounts());

        } catch (Exception e) {


        }

        
    }
}