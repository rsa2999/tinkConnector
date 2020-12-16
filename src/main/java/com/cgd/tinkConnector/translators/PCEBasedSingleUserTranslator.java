package com.cgd.tinkConnector.translators;

import com.cgd.tinkConnector.Clients.CGDClient;
import com.cgd.tinkConnector.Model.IO.TinkCardSubscriptionCheckResponse;
import com.cgd.tinkConnector.Model.TinkCardSubscription;
import com.cgd.tinkConnector.Repositories.TestUsersRepository;

import java.util.List;

public class PCEBasedSingleUserTranslator extends TinkUsersTranslator {

    private CGDClient cgdClient;

    public PCEBasedSingleUserTranslator(CGDClient cgdClient, TestUsersRepository testUsers) {

        this.testUsers = testUsers;
        this.cgdClient = cgdClient;
        this.init();

    }

    @Override
    public void getTinkIdForClientNumber(List<Long> numClients) {

    }

    @Override
    public String getTinkIdForClientNumber(Long numClient) {

        try {

            String tinkId = this.getTinkIdForNumClient(numClient);
            if (tinkId != null) return tinkId;

            TinkCardSubscriptionCheckResponse response = cgdClient.checkTinkCardSubscriptions(numClient, 1);
            if (response.getSubscriptions() == null || response.getSubscriptions().size() == 0) {

                this.clientSubscriptions.put(numClient, "");
                return null;
            } else {

                if (!response.getSubscriptions().containsKey(numClient)) {

                    this.clientSubscriptions.put(numClient, "");
                    return null;
                } else {

                    TinkCardSubscription sub = response.getSubscriptions().get(numClient);
                    this.clientSubscriptions.put(numClient, sub.getTinkId());
                    return sub.getTinkId();
                }
            }

        } catch (Exception e) {


            this.clientSubscriptions.put(numClient, "");
            return null;
        }

    }


}
