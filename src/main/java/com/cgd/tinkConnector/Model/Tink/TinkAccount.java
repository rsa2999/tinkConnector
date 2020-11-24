package com.cgd.tinkConnector.Model.Tink;

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
