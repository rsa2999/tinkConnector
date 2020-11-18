package com.cgd.tinkConnector.Model;

import java.util.List;

public class CGDAccount extends TinkAccount {

    private List<CGDTransaction> transactions;

    private String plasticNumber;

    public String getPlasticNumber() {
        return plasticNumber;
    }

    public void setPlasticNumber(String plasticNumber) {
        this.plasticNumber = plasticNumber;
    }

    public List<CGDTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<CGDTransaction> transactions) {
        this.transactions = transactions;
    }

    public TinkTransactionAccount toTransactionAccount() {

        TinkTransactionAccount ac = new TinkTransactionAccount();
        ac.setReservedAmount(this.getReservedAmount());
        ac.setBalance(this.getBalance());
        ac.setTransactions(this.getTransactions());
        return ac;
    }

    public TinkAccount toTinkAccount() {

        TinkAccount ac = new TinkAccount();
        ac.setReservedAmount(this.getReservedAmount());
        ac.setBalance(this.getBalance());
        ac.setAvailableCredit(this.getAvailableCredit());
        ac.setClosed(this.isClosed());
        ac.setExternalId(this.getExternalId());
        ac.setExclusion(this.getExclusion());
        ac.setFlags(this.getFlags());
        ac.setName(this.getName());
        ac.setNumber(this.getPlasticNumber());
        return ac;
    }
}
