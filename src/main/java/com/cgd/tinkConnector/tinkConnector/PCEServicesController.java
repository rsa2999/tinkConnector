package com.cgd.tinkConnector.tinkConnector;


import com.cgd.TinkAPI.Model.TransactionsUploadRequest;
import com.cgd.TinkAPI.Model.TransactionsUploadResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestScope
@RequestMapping("/api/v1")
public class PCEServicesController {

    private ConcurrentTaskExecutor executor;


    @PostMapping(path = "/uploadTransactions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Receives a batch movements for upload", httpMethod = "POST")
    public TransactionsUploadResponse uploadTransactions(
            HttpServletRequest httpServletRequest, @RequestBody TransactionsUploadRequest request) {

        return null;
    }
}
