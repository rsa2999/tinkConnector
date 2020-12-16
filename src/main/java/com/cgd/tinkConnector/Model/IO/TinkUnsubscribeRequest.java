package com.cgd.tinkConnector.Model.IO;

public class TinkUnsubscribeRequest {

    private long numClient;
    private String tinkId;
    private String subscriptionId;
    private int subscriptionType;
    private long numContrato;

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

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public int getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(int subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public long getNumContrato() {
        return numContrato;
    }

    public void setNumContrato(long numContrato) {
        this.numContrato = numContrato;
    }
}
