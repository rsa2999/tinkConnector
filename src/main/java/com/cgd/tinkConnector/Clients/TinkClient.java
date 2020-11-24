package com.cgd.tinkConnector.Clients;

import com.cgd.tinkConnector.Model.IO.*;
import com.cgd.tinkConnector.Model.Tink.TinkAccount;
import com.cgd.tinkConnector.Model.Tink.TinkTransactionAccount;
import com.cgd.tinkConnector.PCEServicesController;
import com.cgd.tinkConnector.entities.TinkUsers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
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
            + "properties:read,properties:write,providers:read,investments:read,identity:read,identity:write,authorization:grant";

    public static final String USER_SCOPE = "accounts:read,accounts:write,user:read,transactions:read,transactions:write,credentials:read,credentials:refresh,credentials:write";


    private static final String BASE_URL = "";

    private String clientId;
    private String clientSecret;
    private RestTemplate client;

    public TinkClient(RestTemplate client, String clientId, String clientSecret) {

        this.client = client;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

/*
    url -X POST https://api.tink.com/api/v1/oauth/authorization-grant \
            -H 'Authorization: Bearer {YOUR_CLIENT_ACCESS_TOKEN}' \
            -d 'user_id=CREATED_USER_ID' \
            -d 'scope=accounts:read,transactions:read,user:read,credentials:read,credentials:refresh,credentials:write'
*/

    public OAuthGrant usertoken(String accessToken, String tinkUserId, String scope) throws HttpClientErrorException {


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //headers.add("Authorization", String.format("Bearer %s", accessToken));
        headers.setBearerAuth(accessToken);
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        //form.add("external_user_id", tinkUserId);
        form.add("user_id", tinkUserId);
        form.add("scope", scope);


        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);
        ResponseEntity<OAuthGrant> response;

        response = this.client.postForEntity(
                "/api/v1/oauth/authorization-grant",
                request,
                OAuthGrant.class);

        OAuthGrant ret = response.getBody();


        return ret;


    }
/*
    public OAuthGrant grant(String accessToken, String externalUserId) throws HttpClientErrorException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("user_id", externalUserId);
        form.add("scope", ALL_SCOPES);
        //form.add("scope", "credentials:read,credentials:refresh,user:read");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);
        ResponseEntity<OAuthGrant> response;

        response = this.client.postForEntity(
                "/oauth/authorization-grant",
                request,
                OAuthGrant.class);

        return response.getBody();
    }
*/

    public TinkUsers getUser(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<TinkUsers> response = this.client.exchange(
                "/api/v1/user",
                HttpMethod.GET,
                request,
                TinkUsers.class
        );
        return response.getBody();
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
                "/api/v1/oauth/token",
                request,
                OAuthToken.class);

        OAuthToken ret = response.getBody();

        //   LOGGER.info("{} token(): end.", username);

        return ret;
    }


    public TinkUserCredentialResponse getUserCredentials(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<TinkUserCredentialResponse> response = this.client.exchange(
                "/api/v1/credentials/list",
                HttpMethod.GET,
                request,
                TinkUserCredentialResponse.class
        );
        return response.getBody();

    }

    public IngestAccountsResponse ingestAccounts(String accessToken, String externalUserId, List<TinkAccount> accounts) throws HttpClientErrorException {

        ObjectMapper mapper = new ObjectMapper();


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setBearerAuth(accessToken);

        IngestAccountsRequest req = new IngestAccountsRequest();

        req.setAccounts(accounts);
        try {
            String x = mapper.writeValueAsString(req);
            System.out.println(x);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpEntity<IngestAccountsRequest> request = new HttpEntity<>(req, headers);
        ResponseEntity<IngestAccountsResponse> response;

        response = this.client.postForEntity(
                String.format("/connector/users/%s/accounts", externalUserId),
                request,
                IngestAccountsResponse.class);

        // LOGGER.info("createUser(): TINK user created.; externalUserId={}, status code={}", externalUserId, response.getStatusCode());

        return response.getBody();


    }

    public IngestTransactionsResponse ingestTransactions(String accessToken, TinkUsers user, List<TinkTransactionAccount> accounts) throws HttpClientErrorException {

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
                String.format("/connector/users/%s/transactions", user.getExternalUserId()),
                request,
                IngestTransactionsResponse.class);

        // LOGGER.info("createUser(): TINK user created.; externalUserId={}, status code={}", externalUserId, response.getStatusCode());

        return response.getBody();


    }


}
