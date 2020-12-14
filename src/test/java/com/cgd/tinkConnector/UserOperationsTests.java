package com.cgd.tinkConnector;


import com.cgd.tinkConnector.Model.CGDAccount;
import com.cgd.tinkConnector.Model.IO.TinkUnsubscribeRequest;
import com.cgd.tinkConnector.Model.IO.TransactionsUploadRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class UserOperationsTests {

    @Autowired
    private TestRestTemplate restTemplate;


    private String BASE_URL = "http://localhost:8080/api/v1";


    @Test
    public void testUnsubscribe() throws URISyntaxException, JsonProcessingException {


        final String baseUrl = BASE_URL + "/unsubscribe";
        URI uri = new URI(baseUrl);

        HttpHeaders headers = new HttpHeaders();
        //headers.set("Authorization", authorizationHeader);

        TinkUnsubscribeRequest req = new TinkUnsubscribeRequest();

        req.setTinkId("aa8c025e1e9947ecb48991d9b22bd479");

        HttpEntity<TinkUnsubscribeRequest> request = new HttpEntity<>(req, headers);
        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);

        //Verify request succeed
        Assert.assertEquals(201, result.getStatusCodeValue());

    }

    @Test
    public void testJob() throws URISyntaxException, JsonProcessingException {


        final String baseUrl = BASE_URL + "/forcejob";
        URI uri = new URI(baseUrl);

        HttpHeaders headers = new HttpHeaders();
        //headers.set("Authorization", authorizationHeader);


        HttpEntity<?> request = new HttpEntity<Object>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(baseUrl, HttpMethod.GET, request, String.class);

    }

    @Test
    public void addUser() throws URISyntaxException, JsonProcessingException {


        Map<String, String> users = new HashMap<>();

        users.put("46493834", "087d65d6b89d4b30bedf64cbd95c73f6");

        users.put("115898752", "a288cdc5a8cd49119a6fc91051f419a2");

        users.put("162640208", "fd11e7b50446454b9fc7cef6385fe920");

        users.put("94408113", "57bb076f213f46119e1d63cb6e1e17dd");

        for (String num : users.keySet()) {

            String baseUrl = String.format("%s/addUser?numClient=%s&tinkId=%s", BASE_URL, num, users.get(num));

            HttpHeaders headers = new HttpHeaders();
            //headers.set("Authorization", authorizationHeader);


            HttpEntity<?> request = new HttpEntity<Object>(headers);
            ResponseEntity<String> result = this.restTemplate.exchange(baseUrl, HttpMethod.GET, request, String.class);

        }

    }

    @Test
    public void testPing() throws URISyntaxException, JsonProcessingException {


        final String baseUrl = BASE_URL + "/pingTink";

        HttpHeaders headers = new HttpHeaders();
        //headers.set("Authorization", authorizationHeader);


        HttpEntity<?> request = new HttpEntity<Object>(headers);
        ResponseEntity<String> result = this.restTemplate.exchange(baseUrl, HttpMethod.GET, request, String.class);

    }

    @Test
    public void testeCreateUser() throws URISyntaxException, JsonProcessingException {


        String json = "{\"numClient\":157103482,\"tinkId\":\"9c9710a87a54470682334d7520d52ab1\",\"subscriptionId\":\"82FFC1C7CF22F4A7AD988FB2A9E1D15D\",\"isFinalRequest\":true,\"subscriptionType\":1,\"numContrato\":27225,\"accounts\":[{\"availableCredit\":0,\"balance\":0,\"reservedAmount\":0,\"closed\":false,\"externalId\":\"d9fb0212fab039e0608acdae56b246cd\",\"name\":\"Caixautomática Electron\",\"number\":\"10001069805\",\"plasticNumber\":\"4061 **** **** 8072\",\"transactions\":[],\"type\":\"CREDIT_CARD\"},{\"availableCredit\":172404,\"balance\":102596,\"reservedAmount\":0,\"closed\":false,\"externalId\":\"675347c9d009fd5214e39cd3a82be599\",\"name\":\"ND(901.548005.1)\",\"number\":\"10001032977\",\"plasticNumber\":\"4124 **** **** 7237\",\"transactions\":[{\"amount\":8510,\"date\":1604966400000,\"description\":\"Testes TINK 15 S15 8510\",\"externalId\":\"781cd2e3352e7693aa49f67f2923f0f1\",\"pending\":false,\"type\":\"CREDIT_CARD\"},{\"amount\":8900,\"date\":1604966400000,\"description\":\"Testes TINK 14 S15 8900\",\"externalId\":\"530f4c537116749f5169e7ffad720b6c\",\"pending\":false,\"type\":\"CREDIT_CARD\"},{\"amount\":7000,\"date\":1604966400000,\"description\":\"Testes TINK 13 S15 7000\",\"externalId\":\"e9a0bd42d5e15d68f015c18de41025d5\",\"pending\":false,\"type\":\"CREDIT_CARD\"},{\"amount\":12550,\"date\":1604966400000,\"description\":\"Testes TINK 18 S15 12550\",\"externalId\":\"4b021f19a91f1af68ade26c697f6f08e\",\"pending\":false,\"type\":\"CREDIT_CARD\"},{\"amount\":8900,\"date\":1604966400000,\"description\":\"Testes TINK 16 S15 8900\",\"externalId\":\"29059b2d04cc034a98a7077e3c61db9e\",\"pending\":false,\"type\":\"CREDIT_CARD\"},{\"amount\":5800,\"date\":1604966400000,\"description\":\"Testes TINK 17 S15 5800\",\"externalId\":\"9f79f7c7ca4a3c8edf1a0ffad3995492\",\"pending\":false,\"type\":\"CREDIT_CARD\"},{\"amount\":11000,\"date\":1604966400000,\"description\":\"Testes TINK 12 S15 11000\",\"externalId\":\"cac84978d1a070a1edf6224bacf5178a\",\"pending\":false,\"type\":\"CREDIT_CARD\"},{\"amount\":18000,\"date\":1604966400000,\"description\":\"Testes TINK 11 S15 18000\",\"externalId\":\"8ea8379f69aefe8378537b2d799ba660\",\"pending\":false,\"type\":\"CREDIT_CARD\"},{\"amount\":21000,\"date\":1604966400000,\"description\":\"Testes TINK 19 S15 21000\",\"externalId\":\"a2740dc0ebf4243525a4ddb39e2d1ace\",\"pending\":false,\"type\":\"CREDIT_CARD\"},{\"amount\":900,\"date\":1581033600000,\"description\":\"DISPONIBILIZACAO CARTAO CREDITO 1 ANO\",\"externalId\":\"f7df3e358b8553373feea12ad2f4ddc5\",\"pending\":false,\"type\":\"CREDIT_CARD\"},{\"amount\":36,\"date\":1581033600000,\"description\":\"IMPOSTO SELO S/COMISSOES (C)\",\"externalId\":\"d83bdde132f594fae558cdf7ed783919\",\"pending\":false,\"type\":\"CREDIT_CARD\"}],\"type\":\"CREDIT_CARD\"},{\"availableCredit\":0,\"balance\":0,\"reservedAmount\":0,\"closed\":false,\"externalId\":\"50141ca9438425c4caeac16d4d90e740\",\"name\":\"Caixautomática Electron\",\"number\":\"10001037364\",\"plasticNumber\":\"4061 **** **** 1325\",\"transactions\":[],\"type\":\"CREDIT_CARD\"},{\"availableCredit\":0,\"balance\":0,\"reservedAmount\":0,\"closed\":false,\"externalId\":\"e700fd25089096396cfed17e9b6f2278\",\"name\":\"Caixautomática Electron\",\"number\":\"10001080828\",\"plasticNumber\":\"4061 **** **** 2546\",\"transactions\":[],\"type\":\"CREDIT_CARD\"},{\"availableCredit\":0,\"balance\":0,\"reservedAmount\":0,\"closed\":false,\"externalId\":\"c3b688745ed32c56ae9d68aa983e8e1c\",\"name\":\"Caixautomática Electron\",\"number\":\"10001069649\",\"plasticNumber\":\"4061 **** **** 1035\",\"transactions\":[],\"type\":\"CREDIT_CARD\"},{\"availableCredit\":0,\"balance\":0,\"reservedAmount\":0,\"closed\":false,\"externalId\":\"e98815abb7ab0845a16c77c6b6156308\",\"name\":\"Caixautomática Electron\",\"number\":\"10001030872\",\"plasticNumber\":\"4061 **** **** 7701\",\"transactions\":[],\"type\":\"CREDIT_CARD\"},{\"availableCredit\":274064,\"balance\":936,\"reservedAmount\":0,\"closed\":false,\"externalId\":\"505a684591d0c027c6f42c41ad0a4799\",\"name\":\"ND(901.548005.1)\",\"number\":\"10001032233\",\"plasticNumber\":\"4124 **** **** 8482\",\"transactions\":[{\"amount\":900,\"date\":1581033600000,\"description\":\"DISPONIBILIZACAO CARTAO CREDITO 1 ANO\",\"externalId\":\"8282adde575ad26b44ac58efb1cb22f3\",\"pending\":false,\"type\":\"CREDIT_CARD\"},{\"amount\":36,\"date\":1581033600000,\"description\":\"IMPOSTO SELO S/COMISSOES (C)\",\"externalId\":\"bf45dffe8e876b176f9fdfa1fdb5f507\",\"pending\":false,\"type\":\"CREDIT_CARD\"}],\"type\":\"CREDIT_CARD\"}]}";


        //{"numClient":157103482,"tinkId":"b21080bed7364bce9f2f4ae20c8a38b1","subscriptionId":"AA18CBC3C881CB6E9EEF6D10E0A85781","subscriptionType":1,"numContrato":27225,"accounts":[{"availableCredit":0,"balance":0,"reservedAmount":0,"closed":false,"exclusion":null,"externalId":"d9fb0212fab039e0608acdae56b246cd","flags":null,"name":"Caixautomática Electron","number":"10001069805","plasticNumber":"4061 **** **** 8072","transactions":[]},{"availableCredit":172404,"balance":102596,"reservedAmount":0,"closed":false,"exclusion":null,"externalId":"675347c9d009fd5214e39cd3a82be599","flags":null,"name":"ND(901.548005.1)","number":"10001032977","plasticNumber":"4124 **** **** 7237","transactions":[{"amount":8510.0,"date":1604966400000,"description":"Testes TINK 15 S15 8510","externalId":"781cd2e3352e7693aa49f67f2923f0f1","payload":null,"pending":false,"tinkId":null,"type":"CREDIT_CARD"},{"amount":8900.0,"date":1604966400000,"description":"Testes TINK 14 S15 8900","externalId":"530f4c537116749f5169e7ffad720b6c","payload":null,"pending":false,"tinkId":null,"type":"CREDIT_CARD"},{"amount":7000.0,"date":1604966400000,"description":"Testes TINK 13 S15 7000","externalId":"e9a0bd42d5e15d68f015c18de41025d5","payload":null,"pending":false,"tinkId":null,"type":"CREDIT_CARD"},{"amount":12550.0,"date":1604966400000,"description":"Testes TINK 18 S15 12550","externalId":"4b021f19a91f1af68ade26c697f6f08e","payload":null,"pending":false,"tinkId":null,"type":"CREDIT_CARD"},{"amount":8900.0,"date":1604966400000,"description":"Testes TINK 16 S15 8900","externalId":"29059b2d04cc034a98a7077e3c61db9e","payload":null,"pending":false,"tinkId":null,"type":"CREDIT_CARD"},{"amount":5800.0,"date":1604966400000,"description":"Testes TINK 17 S15 5800","externalId":"9f79f7c7ca4a3c8edf1a0ffad3995492","payload":null,"pending":false,"tinkId":null,"type":"CREDIT_CARD"},{"amount":11000.0,"date":1604966400000,"description":"Testes TINK 12 S15 11000","externalId":"cac84978d1a070a1edf6224bacf5178a","payload":null,"pending":false,"tinkId":null,"type":"CREDIT_CARD"},{"amount":18000.0,"date":1604966400000,"description":"Testes TINK 11 S15 18000","externalId":"8ea8379f69aefe8378537b2d799ba660","payload":null,"pending":false,"tinkId":null,"type":"CREDIT_CARD"},{"amount":21000.0,"date":1604966400000,"description":"Testes TINK 19 S15 21000","externalId":"a2740dc0ebf4243525a4ddb39e2d1ace","payload":null,"pending":false,"tinkId":null,"type":"CREDIT_CARD"},{"amount":900.0,"date":1581033600000,"description":"DISPONIBILIZACAO CARTAO CREDITO 1 ANO","externalId":"f7df3e358b8553373feea12ad2f4ddc5","payload":null,"pending":false,"tinkId":null,"type":"CREDIT_CARD"},{"amount":36.0,"date":1581033600000,"description":"IMPOSTO SELO S/COMISSOES (C)","externalId":"d83bdde132f594fae558cdf7ed783919","payload":null,"pending":false,"tinkId":null,"type":"CREDIT_CARD"}]},{"availableCredit":0,"balance":0,"reservedAmount":0,"closed":false,"exclusion":null,"externalId":"50141ca9438425c4caeac16d4d90e740","flags":null,"name":"Caixautomática Electron","number":"10001037364","plasticNumber":"4061 **** **** 1325","transactions":[]},{"availableCredit":0,"balance":0,"reservedAmount":0,"closed":false,"exclusion":null,"externalId":"e700fd25089096396cfed17e9b6f2278","flags":null,"name":"Caixautomática Electron","number":"10001080828","plasticNumber":"4061 **** **** 2546","transactions":[]},{"availableCredit":0,"balance":0,"reservedAmount":0,"closed":false,"exclusion":null,"externalId":"c3b688745ed32c56ae9d68aa983e8e1c","flags":null,"name":"Caixautomática Electron","number":"10001069649","plasticNumber":"4061 **** **** 1035","transactions":[]},{"availableCredit":0,"balance":0,"reservedAmount":0,"closed":false,"exclusion":null,"externalId":"e98815abb7ab0845a16c77c6b6156308","flags":null,"name":"Caixautomática Electron","number":"10001030872","plasticNumber":"4061 **** **** 7701","transactions":[]},{"availableCredit":274064,"balance":936,"reservedAmount":0,"closed":false,"exclusion":null,"externalId":"505a684591d0c027c6f42c41ad0a4799","flags":null,"name":"ND(901.548005.1)","number":"10001032233","plasticNumber":"4124 **** **** 8482","transactions":[{"amount":900.0,"date":1581033600000,"description":"DISPONIBILIZACAO CARTAO CREDITO 1 ANO","externalId":"8282adde575ad26b44ac58efb1cb22f3","payload":null,"pending":false,"tinkId":null,"type":"CREDIT_CARD"},{"amount":36.0,"date":1581033600000,"description":"IMPOSTO SELO S/COMISSOES (C)","externalId":"bf45dffe8e876b176f9fdfa1fdb5f507","payload":null,"pending":false,"tinkId":null,"type":"CREDIT_CARD"}]}],"finalRequest":false}
        ObjectMapper mapper = new ObjectMapper();

        //   TransactionsUploadRequest req2 = mapper.readValue(json, TransactionsUploadRequest.class);

        TransactionsUploadRequest req = new TransactionsUploadRequest();

        req.setAccounts(new ArrayList<>());

        //req.setTinkId("b21080bed7364bce9f2f4ae20c8a38b1");
        req.setTinkId("aa8c025e1e9947ecb48991d9b22bd479");

        List<CGDAccount> accounts = new ArrayList<>();

        CGDAccount ac = new CGDAccount();
        ac.setExternalId("1");
        ac.setName("teste");
        ac.setNumber("1");
        accounts.add(ac);

        ac = new CGDAccount();
        ac.setExternalId("2");
        ac.setName("teste");
        ac.setNumber("2");
        accounts.add(ac);

        final String baseUrl = BASE_URL + "/uploadTransactions";
        URI uri = new URI(baseUrl);

        HttpHeaders headers = new HttpHeaders();
        //headers.set("Authorization", authorizationHeader);


        HttpEntity<TransactionsUploadRequest> request = new HttpEntity<>(req, headers);
        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);

        //Verify request succeed
        Assert.assertEquals(201, result.getStatusCodeValue());


    }


}
