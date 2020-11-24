package com.cgd.tinkConnector.Model.Tink;

import java.util.Map;

public class TinkUserCredential {


    private Map<String, String> fields;
    private String id;
    private String providerName;
    private long sessionExpiryDate;
    private String status;
    private String statusPayload;
    private long statusUpdate;
    private String supplementalInformation;
    private String type;
    private long updated;
    private String userId;

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public long getSessionExpiryDate() {
        return sessionExpiryDate;
    }

    public void setSessionExpiryDate(long sessionExpiryDate) {
        this.sessionExpiryDate = sessionExpiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusPayload() {
        return statusPayload;
    }

    public void setStatusPayload(String statusPayload) {
        this.statusPayload = statusPayload;
    }

    public long getStatusUpdate() {
        return statusUpdate;
    }

    public void setStatusUpdate(long statusUpdate) {
        this.statusUpdate = statusUpdate;
    }

    public String getSupplementalInformation() {
        return supplementalInformation;
    }

    public void setSupplementalInformation(String supplementalInformation) {
        this.supplementalInformation = supplementalInformation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    /*
    "fields": {
        "username": "198410045701"
      },
      "id": "6e68cc6287704273984567b3300c5822",
      "providerName": "handelsbanken-bankid",
      "sessionExpiryDate": 1493379467000,
      "status": "UPDATED",
      "statusPayload": "Analyzed 1,200 out of 1,200 transactions.",
      "statusUpdated": 1493379467000,
      "supplementalInformation": null,
      "type": "MOBILE_BANKID",
      "updated": 1493379467000,
      "userId": "c4ae034f96c740da91ae00022ddcac4d"
     */
}
