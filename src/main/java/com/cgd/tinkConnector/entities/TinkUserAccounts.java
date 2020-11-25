package com.cgd.tinkConnector.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Entity
@Table(name = "tinkusersaccounts")
public class TinkUserAccounts {

    @Id
    private String uniqueId;

    private String externalId;
    private String tinkId;
    private String accountNumber;
    private Date uploadDate;

    public TinkUserAccounts() {


    }

    public TinkUserAccounts(String externalId, String tinkId) throws NoSuchAlgorithmException {

        this.externalId = externalId;
        this.tinkId = tinkId;

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((externalId + tinkId).getBytes());
        byte[] digest = md.digest();
        this.uniqueId = DatatypeConverter.printHexBinary(digest).toLowerCase();
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


    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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
