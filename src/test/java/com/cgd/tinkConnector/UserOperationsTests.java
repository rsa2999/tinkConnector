package com.cgd.tinkConnector;


import com.cgd.tinkConnector.Model.TransactionsUploadRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.net.URISyntaxException;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class UserOperationsTests {

    @Autowired
    private TestRestTemplate restTemplate;


    private String BASE_URL = "http://localhost:8081/api/v1";

    @Test
    public void testeCreateUser() throws URISyntaxException {


        final String baseUrl = BASE_URL + "/uploadTransactions";
        URI uri = new URI(baseUrl);


        HttpHeaders headers = new HttpHeaders();
        //headers.set("Authorization", authorizationHeader);
        TransactionsUploadRequest req = new TransactionsUploadRequest();


        HttpEntity<TransactionsUploadRequest> request = new HttpEntity<>(req, headers);

        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);

        //Verify request succeed
        Assert.assertEquals(201, result.getStatusCodeValue());


    }


}
