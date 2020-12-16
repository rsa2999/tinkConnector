package com.cgd.tinkConnector.translators;

import com.cgd.tinkConnector.Repositories.PCEClientSubscriptionRepository;
import com.cgd.tinkConnector.Repositories.TestUsersRepository;
import com.cgd.tinkConnector.entities.PCEClientSubscription;

import java.util.List;
import java.util.Optional;

public class LocalSubscriptionsUserTranslator extends TinkUsersTranslator {

    private PCEClientSubscriptionRepository subscriptionsRepository;

    public LocalSubscriptionsUserTranslator(TestUsersRepository testUsers, PCEClientSubscriptionRepository subscriptions) {

        this.testUsers = testUsers;
        subscriptionsRepository = subscriptions;
        this.init();
    }


    @Override
    public void getTinkIdForClientNumber(List<Long> numClients) {

    }

    @Override
    public String getTinkIdForClientNumber(Long numClient) {

        String tinkId = getTinkIdForNumClient(numClient);

        if (tinkId != null) return tinkId;

        Optional<PCEClientSubscription> sub = subscriptionsRepository.findByNumClient(numClient);

        if (sub.isPresent()) {

            this.clientSubscriptions.put(numClient, sub.get().getTinkUserId());
            return sub.get().getTinkUserId();
        } else {
            this.clientSubscriptions.put(numClient, "");
            return null;
        }
        
    }

}
