package com.cgd.tinkConnector.Clients;

public enum TinkServices {

    INGEST_ACOUNTS(1), INGEST_TRANSACTIONS(2);

    public int getServiceCode() {
        return serviceCode;
    }

    private int serviceCode;

    TinkServices(int code) {

        this.serviceCode = code;
    }


}
