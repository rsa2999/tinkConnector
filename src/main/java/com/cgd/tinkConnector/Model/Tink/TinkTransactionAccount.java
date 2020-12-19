package com.cgd.tinkConnector.Model.Tink;

import java.util.List;

public class TinkTransactionAccount {


    private float balance;
    private float reservedAmount;
    private String externalId;
    private List<String> payload;
    private List<TinkTransaction> transactions;

    public TinkTransactionAccount() {

    }

    /*
        public TinkTransactionAccount(Long numClient, String accountNumber) {

            this.externalId = ConversionUtils.generateAccountExternalId(numClient, accountNumber);

        }
    */
    public float getBalance() {
        return balance;
    }

    public float getReservedAmount() {
        return reservedAmount;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public void setReservedAmount(float reservedAmount) {
        this.reservedAmount = reservedAmount;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<String> getPayload() {
        return payload;
    }

    public void setPayload(List<String> payload) {
        this.payload = payload;
    }

    public List<TinkTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TinkTransaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "TinkTransactionAccount{" +
                "balance=" + balance +
                ", reservedAmount=" + reservedAmount +
                ", externalId='" + externalId + '\'' +
                ", payload=" + payload +
                ", transactions=" + transactions +
                '}';
    }
}
