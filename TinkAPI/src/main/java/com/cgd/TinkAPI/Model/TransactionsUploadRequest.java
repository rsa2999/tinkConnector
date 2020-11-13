package com.cgd.TinkAPI.Model;

import java.util.List;

public class TransactionsUploadRequest {

    private long numClient;
    private String tinkId;
    private boolean isFinalRequest;
    private List<Account> accounts;
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

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
}
