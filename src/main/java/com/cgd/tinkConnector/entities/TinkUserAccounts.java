package com.cgd.tinkConnector.entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "tinkusersaccounts")
public class TinkUserAccounts {

    @EmbeddedId
    private TinkUserAccountsId id;
    private String accountNumber;
    private Date uploadDate;

    public TinkUserAccountsId getId() {
        return id;
    }

    public void setId(TinkUserAccountsId id) {
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
