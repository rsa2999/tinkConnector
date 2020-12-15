package com.cgd.tinkConnector.translators;

import com.cgd.tinkConnector.Repositories.TestUsersRepository;

import java.util.List;
import java.util.Map;


public abstract class TinkUsersTranslator {

    protected Map<Long, String> clientSubscriptions;
    protected TestUsersRepository testUsers;
    

    public abstract void getTinkIdForClientNumber(List<Long> numClients);

    public abstract String getTinkIdForClientNumber(Long numClient);

    public abstract boolean requiresPreprocessing();


    public void clear() {


    }


}
