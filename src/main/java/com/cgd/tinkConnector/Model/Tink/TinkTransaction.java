package com.cgd.tinkConnector.Model.Tink;

import com.cgd.tinkConnector.Model.CGDTransaction;
import com.cgd.tinkConnector.Utils.ConversionUtils;

import java.util.Map;
import java.util.Objects;

public class TinkTransaction {

    private float amount;
    private long date;
    private String description;
    private String externalId;
    private Map<String, String> payload;
    private boolean pending;
    private String tinkId;
    private String type;

    public TinkTransaction() {

    }


    public TinkTransaction(Long numClient, String accountNumber, CGDTransaction t) {

        this.amount = ConversionUtils.formatAmmount(t.getAmount());
        this.date = t.getDate();
        this.description = t.getDescription();
        this.externalId = ConversionUtils.generateTransactionExternalId(numClient, accountNumber, this.amount, this.description, this.date, 1);
        this.payload = t.getPayload();
        this.pending = t.isPending();
        //this.tinkId = t.getTinkId();
        this.type = t.getType();

    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TinkTransaction that = (TinkTransaction) o;
        return Float.compare(that.amount, amount) == 0 &&
                date == that.date &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, date, description);
    }
}
