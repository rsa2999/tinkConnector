package com.cgd.tinkConnector;

import org.springframework.web.client.RestTemplate;

public class BaseController {

    protected RestTemplate cgdSvc;
    protected RestTemplate tinkSvc;

    protected String clientId;
    protected String clientSecret;


}
