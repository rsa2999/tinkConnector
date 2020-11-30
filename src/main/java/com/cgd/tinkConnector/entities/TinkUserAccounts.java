package com.cgd.tinkConnector.entities;

import com.cgd.tinkConnector.Utils.ConversionUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "tinkusersaccounts")
public class TinkUserAccounts {

    @Id
    private String id;
    private String externalAccountId;
    private String tinkId;

    private String accountNumber;
    private String accountDescription;
    private Date uploadDate;

    public String getAccountDescription() {
        return accountDescription;
    }

    public void setAccountDescription(String accountDescription) {
        this.accountDescription = accountDescription;
    }

    public TinkUserAccounts() {


    }

    public TinkUserAccounts(Long numClient, String accountNumber, String tinkId) {

        this.externalAccountId = ConversionUtils.generateAccountExternalId(numClient, accountNumber);
        this.tinkId = tinkId;
        this.id = ConversionUtils.generateInternalAccountId(this.externalAccountId, this.tinkId);
    }

    public TinkUserAccounts(String externalId, String tinkId) {

        this.externalAccountId = externalId;
        this.tinkId = tinkId;
        this.id = ConversionUtils.generateInternalAccountId(this.externalAccountId, this.tinkId);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExternalAccountId() {
        return externalAccountId;
    }

    public void setExternalAccountId(String externalAccountId) {
        this.externalAccountId = externalAccountId;
    }

    public String getTinkId() {
        return tinkId;
    }

    public void setTinkId(String tinkId) {
        this.tinkId = tinkId;
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
