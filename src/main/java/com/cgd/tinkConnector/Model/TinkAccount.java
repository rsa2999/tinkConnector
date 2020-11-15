package com.cgd.tinkConnector.Model;

import java.util.List;

public class TinkAccount {


    protected long availableCredit;
    protected long balance;
    protected long reservedAmount;
    protected boolean closed;
    protected String exclusion;
    protected String externalId;
    protected List<String> flags;
    protected String name;
    protected String number;

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


}
