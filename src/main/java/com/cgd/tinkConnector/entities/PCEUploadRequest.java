package com.cgd.tinkConnector.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "pceuploadsrequests",
        indexes = {@Index(name = "idx_num_client", columnList = "numClient", unique = false),
                @Index(name = "idx_tinkId", columnList = "tinkId", unique = false),
                @Index(name = "idx_payloadHash", columnList = "payloadHash,serviceId,statusCode", unique = false)})
public class PCEUploadRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long requestId;
    private long numClient;
    @JsonIgnore
    private String tinkId;
    private String subscriptionId;
    private Date requestDate;
    private int statusCode;
    private String payloadHash;
    @Lob
    @JsonIgnore
    private String payload;
    @Lob
    private String error;
    private int serviceId;
    private int responseCode;

    public String getPayload() {
        return payload;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getPayloadHash() {
        return payloadHash;
    }

    public void setPayloadHash(String payloadHash) {
        this.payloadHash = payloadHash;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public long getNumClient() {
        return numClient;
    }

    public void setNumClient(long numClient) {
        this.numClient = numClient;
    }

    public String getTinkId() {
        return tinkId;
    }

    public void setTinkId(String tinkId) {
        this.tinkId = tinkId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
