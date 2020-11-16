package com.cgd.tinkConnector.Model;

import java.util.List;

public class IngestTransactionsRequest {


    private boolean autoBook;
    private boolean overridePending;
    private List<TinkTransactionAccount> transactionAccounts;
    private String type;

    public boolean isAutoBook() {
        return autoBook;
    }

    public void setAutoBook(boolean autoBook) {
        this.autoBook = autoBook;
    }

    public boolean isOverridePending() {
        return overridePending;
    }

    public void setOverridePending(boolean overridePending) {
        this.overridePending = overridePending;
    }

    public List<TinkTransactionAccount> getTransactionAccounts() {
        return transactionAccounts;
    }

    public void setTransactionAccounts(List<TinkTransactionAccount> transactionAccounts) {
        this.transactionAccounts = transactionAccounts;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /*

    {
  "autoBook": false,
  "overridePending": false,
  "transactionAccounts": [
    {
      "balance": 7000.0,
      "externalId": "2d3bd65493b549e1927d97a2d0683ab9",
      "payload": {},
      "reservedAmount": 2000.0,
      "transactions": [
        {
          "amount": -98.5,
          "date": 1455740874875,
          "description": "Riche Teatergrillen",
          "externalId": "40dc04e5353547378c84f34ffc88f853",
          "payload": {},
          "pending": false,
          "tinkId": "string",
          "type": "CREDIT_CARD"
        }
      ]
    }
  ],
  "type": "REAL_TIME"
}
     */
}
