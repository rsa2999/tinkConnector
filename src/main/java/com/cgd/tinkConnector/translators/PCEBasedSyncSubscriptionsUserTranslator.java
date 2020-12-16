package com.cgd.tinkConnector.translators;

import com.cgd.tinkConnector.Clients.CGDClient;
import com.cgd.tinkConnector.Repositories.PCEClientSubscriptionRepository;
import com.cgd.tinkConnector.Repositories.TestUsersRepository;

import java.util.List;

public class PCEBasedSyncSubscriptionsUserTranslator extends TinkUsersTranslator {

    private PCEClientSubscriptionRepository subscriptionsRepository;

    private CGDClient cgdClient;

    public PCEBasedSyncSubscriptionsUserTranslator(CGDClient cgdClient, TestUsersRepository testUsers, PCEClientSubscriptionRepository subscriptions) {

        this.testUsers = testUsers;
        subscriptionsRepository = subscriptions;
        this.cgdClient = cgdClient;

        this.init();

    }


    @Override
    public void getTinkIdForClientNumber(List<Long> numClients) {

    }

    @Override
    public String getTinkIdForClientNumber(Long numClient) {
        return null;
    }

}
