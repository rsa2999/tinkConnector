package com.cgd.tinkConnector.Clients;

import com.cgd.tinkConnector.Model.IO.TinkCardSubscriptionCheckRequest;
import com.cgd.tinkConnector.Model.IO.TinkCardSubscriptionCheckResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class CGDClient {


    public static final String CGD_CLIENT_SYSTEM = "CGD_APP";

    private RestTemplate client;

    public CGDClient(RestTemplate client) {
        this.client = client;
    }


    private HttpHeaders getHeaders() {

        HttpHeaders headers = new HttpHeaders();

        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
        headers.add("x-cgd-client-system", CGD_CLIENT_SYSTEM);
        headers.add("x-cgd-app-name", "APP_DABOX_NON_CUSTOMERS");
        headers.add("x-cgd-app-device", "as4");
        headers.add("x-cgd-app-version", "1.0.0");
        headers.add("x-cgd-app-language", "pt");

        return headers;
    }

    public TinkCardSubscriptionCheckResponse checkTinkCardSubscriptions(Long numClient, int subscriptionType) {

        HttpHeaders headers = this.getHeaders();


        TinkCardSubscriptionCheckRequest req = new TinkCardSubscriptionCheckRequest();
        req.setClientNumbers(new ArrayList<>());
        req.getClientNumbers().add(numClient);
        req.setSubscriptionType(subscriptionType);

        HttpEntity<TinkCardSubscriptionCheckRequest> request = new HttpEntity<>(req, headers);
        ResponseEntity<TinkCardSubscriptionCheckResponse> response;
        response = this.client.postForEntity(
                "/business/dabox/cards/check",
                request,
                TinkCardSubscriptionCheckResponse.class);

        return response.getBody();


    }

    public TinkCardSubscriptionCheckResponse updateTinkCardSubscriptions(int subscriptionType, List<Long> clientNumbers) {

        HttpHeaders headers = this.getHeaders();


        TinkCardSubscriptionCheckRequest req = new TinkCardSubscriptionCheckRequest();
        req.setClientNumbers(clientNumbers);
        req.setSubscriptionType(subscriptionType);

        HttpEntity<TinkCardSubscriptionCheckRequest> request = new HttpEntity<>(req, headers);
        ResponseEntity<TinkCardSubscriptionCheckResponse> response;
        response = this.client.postForEntity(
                "/business/dabox/cards/updatestate",
                request,
                TinkCardSubscriptionCheckResponse.class);

        return response.getBody();


    }

}
