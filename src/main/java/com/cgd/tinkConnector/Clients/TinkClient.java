package com.cgd.tinkConnector.Clients;

import com.cgd.tinkConnector.Model.*;
import com.cgd.tinkConnector.PCEServicesController;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class TinkClient {

    private static Logger LOGGER = LoggerFactory.getLogger(PCEServicesController.class);

    public static final String ALL_SCOPES = "accounts:read,accounts:write,activities:read,budgets:read,budgets:write,"
            + "calendar:read,categories:read,contacts:read,credentials:read,credentials:refresh,credentials:write,"
            + "documents:read,documents:write,follow:read,follow:write,insights:read,insights:write,kyc:read,kyc:write,"
            + "payment:read,payment:write,transfer:read,transfer:execute,transfer:update,settings:read,settings:write,"
            + "statistics:read,tracking:read,tracking:write,transactions:categorize,transactions:read,"
            + "transactions:write,user:read,user:write,user:web_hooks,suggestions:read,streaming:access,"
            + "properties:read,properties:write,providers:read,investments:read,identity:read,identity:write";

    private String clientId;
    private String clientSecret;
    private RestTemplate client;

    public TinkClient(RestTemplate client, String clientId, String clientSecret) {

        this.client = client;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public OAuthToken token(String grantType, String scope, String code, String refreshToken) throws HttpClientErrorException {
        //LOGGER.info("{} token(): begin...", username);



        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", this.clientId);
        form.add("client_secret", this.clientSecret);
        form.add("grant_type", grantType);

        if (scope != null && !scope.isEmpty()) {
            form.add("scope", scope);
        }
        if (code != null && !code.isEmpty()) {
            form.add("code", code);
        }
        if (refreshToken != null && !refreshToken.isEmpty()) {
            form.add("refresh_token", refreshToken);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);
        ResponseEntity<OAuthToken> response;

        response = this.client.postForEntity(
                "/oauth/token",
                request,
                OAuthToken.class);

        OAuthToken ret = response.getBody();

        //   LOGGER.info("{} token(): end.", username);

        return ret;
    }

    public IngestAccountsResponse ingestAccounts(String accessToken, String tinkId, List<TinkAccount> accounts) throws HttpClientErrorException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setBearerAuth(accessToken);

        IngestAccountsRequest req = new IngestAccountsRequest();

        req.setAccounts(accounts);

        HttpEntity<IngestAccountsRequest> request = new HttpEntity<>(req, headers);
        ResponseEntity<IngestAccountsResponse> response;

        response = this.client.postForEntity(
                String.format("/connector/users/%s/accounts", tinkId),
                request,
                IngestAccountsResponse.class);

        // LOGGER.info("createUser(): TINK user created.; externalUserId={}, status code={}", externalUserId, response.getStatusCode());

        return response.getBody();


    }

    public IngestTransactionsResponse ingestTransactions(String accessToken, String tinkId, List<TinkTransactionAccount> accounts) throws HttpClientErrorException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setBearerAuth(accessToken);

        IngestTransactionsRequest req = new IngestTransactionsRequest();

        req.setAutoBook(false);
        req.setOverridePending(false);
        req.setTransactionAccounts(accounts);

        HttpEntity<IngestTransactionsRequest> request = new HttpEntity<>(req, headers);
        ResponseEntity<IngestTransactionsResponse> response;

        response = this.client.postForEntity(
                String.format("/connector/users/%s/transactions", tinkId),
                request,
                IngestTransactionsResponse.class);

        // LOGGER.info("createUser(): TINK user created.; externalUserId={}, status code={}", externalUserId, response.getStatusCode());

        return response.getBody();


    }



    public static class OAuthGrant {

        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
