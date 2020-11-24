package com.cgd.tinkConnector.entities;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tinkusers")
public class TinkUsers {

    @Id
    private String id;
    private String externalUserId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }
/*
    {
  "appId": "56a33be25eb9443fbb696f7c61eabd94",
  "created": "string",
  "externalUserId": "2d3bd65493b549e1927d97a2d0683ab8",
  "flags": [
    "TRANSFERS",
    "TEST_PINK_ONBOARDING"
  ],
  "id": "6e68cc6287704273984567b3300c5822",
  "nationalId": "198410045701",
  "profile": {
    "cashbackEnabled": false,
    "currency": "SEK",
    "locale": "sv_SE",
    "market": "SE
     */
}
