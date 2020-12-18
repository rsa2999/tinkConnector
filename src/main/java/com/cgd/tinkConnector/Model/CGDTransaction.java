package com.cgd.tinkConnector.Model;

import java.util.Map;

public class CGDTransaction {

    private long amount;
    private long date;
    private String description;
    private String externalId;
    private Map<String, String> payload;
    private boolean pending;
    private String tinkId;
    private String type;


    public float getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Map<String, String> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, String> payload) {
        this.payload = payload;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public String getTinkId() {
        return tinkId;
    }

    public void setTinkId(String tinkId) {
        this.tinkId = tinkId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CGDTransaction{" +
                "amount=" + amount +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", externalId='" + externalId + '\'' +
                ", payload=" + payload +
                ", pending=" + pending +
                ", tinkId='" + tinkId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
