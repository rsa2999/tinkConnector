package com.cgd.tinkConnector.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class TinkUserAccountsId implements Serializable {

    private String tinkId;
    private String externalAccountId;

    public TinkUserAccountsId(String tinkId, String externalAccountId) {
        this.tinkId = tinkId;
        this.externalAccountId = externalAccountId;
    }

    public TinkUserAccountsId() {

    }

    public String getTinkId() {
        return tinkId;
    }

    public void setTinkId(String tinkId) {
        this.tinkId = tinkId;
    }

    public String getExternalAccountId() {
        return externalAccountId;
    }

    public void setExternalAccountId(String externalAccountId) {
        this.externalAccountId = externalAccountId;
    }

    public boolean equals(Object o) {

        TinkUserAccountsId id = (TinkUserAccountsId) o;

        return id.getExternalAccountId().equals(this.getExternalAccountId()) && id.getTinkId().equals(this.getTinkId());

    }
}
