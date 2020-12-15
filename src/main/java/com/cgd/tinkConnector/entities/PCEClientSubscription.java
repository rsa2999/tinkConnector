package com.cgd.tinkConnector.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;


@Entity
@Table(name = "pceclientsubscriptions",
        indexes = {@Index(name = "pceclientsubscriptions_num_client", columnList = "numClient", unique = true),
                @Index(name = "pceclientsubscriptions_subscriptionStatus", columnList = "subscriptionStatus,numClient", unique = false)})

public class PCEClientSubscription {

    @Id
    private String subscriptionId;
    private Long numContrato;
    private String tinkUserId;
    private Long numClient;
    private Date creationDate;
    private Date updateDate;
    private int subscriptionStatus;
    private int subscriptionType;

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getNumContrato() {
        return numContrato;
    }

    public void setNumContrato(Long numContrato) {
        this.numContrato = numContrato;
    }

    public String getTinkUserId() {
        return tinkUserId;
    }

    public void setTinkUserId(String tinkUserId) {
        this.tinkUserId = tinkUserId;
    }

    public Long getNumClient() {
        return numClient;
    }

    public void setNumClient(Long numClient) {
        this.numClient = numClient;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public int getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(int subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public int getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(int subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
}
