package com.cgd.tinkConnector.translators;

import com.cgd.tinkConnector.Clients.CGDClient;
import com.cgd.tinkConnector.Repositories.PCEClientSubscriptionRepository;
import com.cgd.tinkConnector.Repositories.TestUsersRepository;

import java.util.List;

public class PCEBasedSyncSubscriptionsUserTranslator extends TinkUsersTranslator {


    public PCEBasedSyncSubscriptionsUserTranslator(CGDClient cgdClient, TestUsersRepository testUsers, PCEClientSubscriptionRepository subscriptions) {


    }


    @Override
    public void getTinkIdForClientNumber(List<Long> numClients) {

    }

    @Override
    public String getTinkIdForClientNumber(Long numClient) {
        return null;
    }

    @Override
    public boolean requiresPreprocessing() {
        return false;
    }
}
