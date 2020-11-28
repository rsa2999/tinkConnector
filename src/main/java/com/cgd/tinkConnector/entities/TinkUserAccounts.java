package com.cgd.tinkConnector.entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "tinkusersaccounts")
public class TinkUserAccounts {

    @EmbeddedId
    private TinkUserAccountId id;

    private String accountNumber;
    private Date uploadDate;

    public TinkUserAccounts() {


    }

    public TinkUserAccounts(String externalId, String tinkId) {

        this.id = new TinkUserAccountId(externalId, tinkId);
        
    }


    public TinkUserAccountId getId() {
        return id;
    }

    public void setId(TinkUserAccountId id) {
        this.id = id;
    }

    public TinkUserAccounts(TinkUserAccountId id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
}
