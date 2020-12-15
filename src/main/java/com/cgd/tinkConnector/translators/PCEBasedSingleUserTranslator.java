package com.cgd.tinkConnector.translators;

import com.cgd.tinkConnector.Clients.CGDClient;
import com.cgd.tinkConnector.Repositories.TestUsersRepository;

import java.util.List;

public class PCEBasedSingleUserTranslator extends TinkUsersTranslator {


    public PCEBasedSingleUserTranslator(CGDClient cgdClient, TestUsersRepository testUsers) {
        

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
