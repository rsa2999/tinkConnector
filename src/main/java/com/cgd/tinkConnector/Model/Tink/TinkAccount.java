package com.cgd.tinkConnector.Model.Tink;

import com.cgd.tinkConnector.Utils.ConversionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

public class TinkAccount {

    private float availableCredit;
    private float balance;
    private float reservedAmount;
    private boolean closed;
    //private String exclusion;
    private String externalId;
    //  private List<String> flags;
    private String name;
    private String number;
    private String type;

    @JsonIgnore
    private List<TinkTransaction> transactions;
    @JsonIgnore
    private Map<String, Integer> transactionsByIdAndCount;

    public Map<String, Integer> getTransactionsByIdAndCount() {
        return transactionsByIdAndCount;
    }

    public void setTransactionsByIdAndCount(Map<String, Integer> transactionsByIdAndCount) {
        this.transactionsByIdAndCount = transactionsByIdAndCount;
    }

    public TinkTransactionAccount toTransactionAccount() {

        TinkTransactionAccount ac = new TinkTransactionAccount();
        ac.setReservedAmount(this.getReservedAmount());
        ac.setBalance(this.getBalance());
        ac.setExternalId(this.getExternalId());
        ac.setTransactions(this.getTransactions());
        return ac;
    }


    public List<TinkTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TinkTransaction> transactions) {
        this.transactions = transactions;
    }

    public TinkAccount(Long numClient, String accountNumber) {

        this.externalId = ConversionUtils.generateAccountExternalId(numClient, accountNumber);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getAvailableCredit() {
        return availableCredit;
    }

    public void setAvailableCredit(float availableCredit) {
        this.availableCredit = availableCredit;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public float getReservedAmount() {
        return reservedAmount;
    }

    public void setReservedAmount(float reservedAmount) {
        this.reservedAmount = reservedAmount;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }


    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
