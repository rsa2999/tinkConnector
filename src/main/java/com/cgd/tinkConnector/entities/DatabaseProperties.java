package com.cgd.tinkConnector.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "properties")
public class DatabaseProperties {

    private String key;
    private String value;

    public DatabaseProperties() {
    }

    public DatabaseProperties(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
