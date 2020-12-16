package com.cgd.tinkConnector.Model.IO;

import com.cgd.tinkConnector.entities.PCEClientSubscription;
import com.cgd.tinkConnector.entities.PCEUploadRequest;

import java.util.List;

public class UserInteractionsResponse {

    private List<PCEClientSubscription> subscriptions;

    private List<PCEUploadRequest> requests;

    public List<PCEClientSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<PCEClientSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<PCEUploadRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<PCEUploadRequest> requests) {
        this.requests = requests;
    }
}
