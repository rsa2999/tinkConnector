package com.cgd.tinkConnector.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "testUsers")
public class TestUsers {

    @Id
    private Long numClient;
    private String tinkUserId;

    public Long getNumClient() {
        return numClient;
    }

    public void setNumClient(Long numClient) {
        this.numClient = numClient;
    }

    public String getTinkUserId() {
        return tinkUserId;
    }

    public void setTinkUserId(String tinkUserId) {
        this.tinkUserId = tinkUserId;
    }
}
