package com.cgd.tinkConnector.Model.IO;

import java.util.List;

public class TinkCardSubscriptionCheckRequest {

    private int subscriptionType;

    private List<Long> clientNumbers;

    public List<Long> getClientNumbers() {
        return clientNumbers;
    }

    public void setClientNumbers(List<Long> clientNumbers) {
        this.clientNumbers = clientNumbers;
    }

    public int getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(int subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

}
