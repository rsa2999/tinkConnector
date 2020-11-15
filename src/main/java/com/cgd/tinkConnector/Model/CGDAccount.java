package com.cgd.tinkConnector.Model;

import java.util.List;

public class CGDAccount extends TinkAccount {

    private String plasticNumber;

    public String getPlasticNumber() {
        return plasticNumber;
    }

    public void setPlasticNumber(String plasticNumber) {
        this.plasticNumber = plasticNumber;
    }

    private List<CGDTransaction> transactions;

    public List<CGDTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<CGDTransaction> transactions) {
        this.transactions = transactions;
    }
}
