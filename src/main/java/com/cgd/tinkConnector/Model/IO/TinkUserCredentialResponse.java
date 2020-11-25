package com.cgd.tinkConnector.Model.IO;

import com.cgd.tinkConnector.Model.Tink.TinkUserCredential;

import java.util.List;

public class TinkUserCredentialResponse {

    private List<TinkUserCredential> credentials;

    public List<TinkUserCredential> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<TinkUserCredential> credentials) {
        this.credentials = credentials;
    }
}
