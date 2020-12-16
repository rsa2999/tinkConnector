package com.cgd.tinkConnector.translators;

import com.cgd.tinkConnector.Repositories.TestUsersRepository;
import com.cgd.tinkConnector.entities.TestUsers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class TinkUsersTranslator {

    protected Map<Long, String> clientSubscriptions = new HashMap<>();
    protected TestUsersRepository testUsers;


    public abstract void getTinkIdForClientNumber(List<Long> numClients);

    public abstract String getTinkIdForClientNumber(Long numClient);

    protected String getTinkIdForNumClient(Long numClient) {

        if (this.clientSubscriptions.containsKey(numClient)) {

            String tinkId = this.clientSubscriptions.get(numClient);
            if (tinkId.length() == 0) return null;
            return tinkId;
        }
        return null;
    }

    protected void init() {

        List<TestUsers> users = this.testUsers.findAll();

        for (TestUsers u : users) {

            this.clientSubscriptions.put(u.getNumClient(), u.getTinkUserId());
        }
        

    }

    public void clear() {


    }


}
