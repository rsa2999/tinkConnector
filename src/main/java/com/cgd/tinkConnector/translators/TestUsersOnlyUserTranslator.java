package com.cgd.tinkConnector.translators;

import com.cgd.tinkConnector.Repositories.TestUsersRepository;

import java.util.List;

public class TestUsersOnlyUserTranslator extends TinkUsersTranslator {

    private TestUsersRepository testUsers;


    public TestUsersOnlyUserTranslator(TestUsersRepository testUsers) {

        this.testUsers = testUsers;
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
