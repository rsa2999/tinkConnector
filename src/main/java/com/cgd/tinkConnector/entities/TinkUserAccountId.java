package com.cgd.tinkConnector.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TinkUserAccountId implements Serializable {

    private String externalId;
    private String tinkId;

    public TinkUserAccountId() {


    }

    public TinkUserAccountId(String externalId, String tinkId) {
        this.externalId = externalId;
        this.tinkId = tinkId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getTinkId() {
        return tinkId;
    }

    public void setTinkId(String tinkId) {
        this.tinkId = tinkId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TinkUserAccountId that = (TinkUserAccountId) o;
        return externalId.equals(that.externalId) &&
                tinkId.equals(that.tinkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalId, tinkId);
    }
}
