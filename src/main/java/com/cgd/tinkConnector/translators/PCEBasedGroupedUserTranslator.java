package com.cgd.tinkConnector.translators;

import com.cgd.tinkConnector.Clients.CGDClient;
import com.cgd.tinkConnector.Model.IO.TinkCardSubscriptionCheckResponse;
import com.cgd.tinkConnector.Model.TinkCardSubscription;
import com.cgd.tinkConnector.Repositories.TestUsersRepository;

import java.util.List;

public class PCEBasedGroupedUserTranslator extends TinkUsersTranslator {

    private CGDClient cgdClient;

    public PCEBasedGroupedUserTranslator(CGDClient cgdClient, TestUsersRepository testUsers) {

        this.testUsers = testUsers;
        this.cgdClient = cgdClient;
        this.init();
    }

    @Override
    public void getTinkIdForClientNumber(List<Long> numClients) {

        TinkCardSubscriptionCheckResponse response = cgdClient.checkTinkCardSubscriptions(numClients, 1);

        if (response.getSubscriptions() == null || response.getSubscriptions().size() == 0) {
            return;
        }

        for (Long num : numClients) {

            if (response.getSubscriptions().containsKey(num)) {

                TinkCardSubscription sub = response.getSubscriptions().get(num);
                this.clientSubscriptions.put(num, sub.getTinkId());
            } else {
                this.clientSubscriptions.put(num, "");

            }

        }

    }

    @Override
    public String getTinkIdForClientNumber(Long numClient) {

        return this.getTinkIdForNumClient(numClient);
    }
}
