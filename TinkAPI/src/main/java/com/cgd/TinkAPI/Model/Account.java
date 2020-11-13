package com.cgd.TinkAPI.Model;

import java.util.List;

public class Account {

    private long availableCredit;
    private long balance;
    private long reservedAmount;
    private boolean closed;
    private String exclusion;
    private String externalId;
    private List<String> flags;
    private String name;
    private String number;


    private List<CGDTransaction> transactions;

    public long getAvailableCredit() {
        return availableCredit;
    }

    public void setAvailableCredit(long availableCredit) {
        this.availableCredit = availableCredit;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getReservedAmount() {
        return reservedAmount;
    }

    public void setReservedAmount(long reservedAmount) {
        this.reservedAmount = reservedAmount;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getExclusion() {
        return exclusion;
    }

    public void setExclusion(String exclusion) {
        this.exclusion = exclusion;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
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

    public List<CGDTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<CGDTransaction> transactions) {
        this.transactions = transactions;
    }
}
