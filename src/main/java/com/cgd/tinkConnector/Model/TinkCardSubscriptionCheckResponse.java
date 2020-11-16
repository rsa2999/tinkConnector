package com.cgd.tinkConnector.Model;

import java.util.Map;

public class TinkCardSubscriptionCheckResponse {

    private Map<Long, TinkCardSubscription> subscriptions;

    public Map<Long, TinkCardSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Map<Long, TinkCardSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

}
