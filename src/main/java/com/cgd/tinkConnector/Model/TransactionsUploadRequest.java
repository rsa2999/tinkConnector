package com.cgd.tinkConnector.Model;

import java.util.List;

public class TransactionsUploadRequest {

    private long numClient;
    private String tinkId;
    private boolean isFinalRequest;
    private List<TinkAccount> accounts;
    private String subscriptionId;

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

    public List<TinkAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<TinkAccount> accounts) {
        this.accounts = accounts;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
}
