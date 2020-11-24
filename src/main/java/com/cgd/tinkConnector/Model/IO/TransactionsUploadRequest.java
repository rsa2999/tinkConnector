package com.cgd.tinkConnector.Model.IO;

import com.cgd.tinkConnector.Model.CGDAccount;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class TransactionsUploadRequest {

    private long numClient;
    private String tinkId;
    @JsonIgnore
    private boolean isFinalRequest;

    private String subscriptionId;
    private int subscriptionType;
    private long numContrato;
    private List<CGDAccount> accounts;

    public long getNumContrato() {
        return numContrato;
    }

    public void setNumContrato(long numContrato) {
        this.numContrato = numContrato;
    }

    public int getSubscriptionType() {
        return subscriptionType;
    }


    public long getNumClient() {
        return numClient;
    }

    public void setNumClient(long numClient) {
        this.numClient = numClient;
    }

    public String getTinkId() {
        return tinkId;
    }

    public void setTinkId(String tinkId) {
        this.tinkId = tinkId;
    }

    public boolean isFinalRequest() {
        return isFinalRequest;
    }

    public void setFinalRequest(boolean finalRequest) {
        isFinalRequest = finalRequest;
    }

    public List<CGDAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<CGDAccount> accounts) {
        this.accounts = accounts;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
}
