package com.cgd.tinkConnector.Clients;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public class CGDClient {

    private RestTemplate client;

    public CGDClient(RestTemplate client) {
        this.client = client;
    }


    private HttpHeaders getHeaders() {

        HttpHeaders headers = new HttpHeaders();
/*
        headers.add("user-agent", userAgent);
        headers.add("x-cgd-client-system", CGD_CLIENT_SYSTEM);
        headers.add("x-cgd-app-name", appName);
        headers.add("x-cgd-app-device", cgdAppDevice);
        headers.add("x-cgd-app-version", deviceAppVersion);
        headers.add("x-cgd-app-language", language);
*/
        return headers;
    }

}
