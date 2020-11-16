package com.cgd.tinkConnector.Model;

import java.util.List;

public class IngestAccountsRequest {

    private List<TinkAccount> accounts;

    public List<TinkAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<TinkAccount> accounts) {
        this.accounts = accounts;
    }
}
