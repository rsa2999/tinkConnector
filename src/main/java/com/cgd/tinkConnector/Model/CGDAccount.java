package com.cgd.tinkConnector.Model;

import com.cgd.tinkConnector.Model.Tink.TinkAccount;
import com.cgd.tinkConnector.Model.Tink.TinkTransaction;
import com.cgd.tinkConnector.Model.Tink.TinkTransactionAccount;
import com.cgd.tinkConnector.Utils.ConversionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CGDAccount {

    private long availableCredit;
    private long balance;
    private long reservedAmount;
    private boolean closed;
    private String exclusion;
    private String externalId;
    private List<String> flags;
    private String name;
    private String number;
    private String plasticNumber;
    private List<CGDTransaction> transactions;

    public CGDAccount(Long numClient, String accountNumber) {

        this.externalId = ConversionUtils.generateAccountExternalId(numClient, accountNumber);
    }

    public CGDAccount() {


    }

    public long getAvailableCredit() {
        return availableCredit;
    }

    public void setAvailableCredit(long availableCredit) {
        this.availableCredit = availableCredit;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getReservedAmount() {
        return reservedAmount;
    }

    public void setReservedAmount(long reservedAmount) {
        this.reservedAmount = reservedAmount;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getExclusion() {
        return exclusion;
    }

    public void setExclusion(String exclusion) {
        this.exclusion = exclusion;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPlasticNumber() {
        return plasticNumber;
    }

    public void setPlasticNumber(String plasticNumber) {
        this.plasticNumber = plasticNumber;
    }

    public List<CGDTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<CGDTransaction> transactions) {
        this.transactions = transactions;
    }


    public TinkTransactionAccount toTransactionAccount(Long clientNumber) {

        TinkTransactionAccount ac = new TinkTransactionAccount();
        ac.setReservedAmount(ConversionUtils.formatAmmount(this.getReservedAmount()));
        ac.setBalance(ConversionUtils.formatAmmount(this.getBalance()));
        ac.setExternalId(ConversionUtils.generateAccountExternalId(clientNumber, this.getNumber()));
        List<TinkTransaction> transactions = new ArrayList<>();

        Map<String, Integer> transactionsByIdAndCount = new HashMap<>();

        for (CGDTransaction t : this.transactions) {

            TinkTransaction trans = new TinkTransaction(clientNumber, this.getNumber(), t);

            if (transactionsByIdAndCount.containsKey(trans.getExternalId())) {

                int v = transactionsByIdAndCount.get(trans.getExternalId()) + 1;
                transactionsByIdAndCount.put(trans.getExternalId(), v);
                trans.setExternalId(ConversionUtils.generateTransactionExternalId(clientNumber, this.getNumber(), trans.getAmount(), trans.getDescription(), trans.getDate(), v));
            } else {
                transactionsByIdAndCount.put(trans.getExternalId(), 1);
            }

            transactions.add(trans);
        }
        ac.setTransactions(transactions);
        return ac;
    }

    public TinkAccount toTinkAccount(String accountType, Long numClient) {


        TinkAccount ac = new TinkAccount(numClient, this.getNumber());
        ac.setReservedAmount(ConversionUtils.formatAmmount(this.getReservedAmount()));
        ac.setBalance(ConversionUtils.formatAmmount(this.getBalance()));
        ac.setAvailableCredit(ConversionUtils.formatAmmount(this.getAvailableCredit()));
        ac.setClosed(this.isClosed());
        // ac.setExternalId(ConversionUtils.generateAccountExternalId(numClient, this.getNumber()));
        ac.setName(this.getName());
        ac.setNumber(ConversionUtils.maskAccountNumber(this.getPlasticNumber()));
        ac.setType(accountType);
        return ac;
    }

    @Override
    public String toString() {
        return "CGDAccount{" +
                "availableCredit=" + availableCredit +
                ", balance=" + balance +
                ", reservedAmount=" + reservedAmount +
                ", closed=" + closed +
                ", exclusion='" + exclusion + '\'' +
                ", externalId='" + externalId + '\'' +
                ", flags=" + flags +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", plasticNumber='" + plasticNumber + '\'' +
                ", transactions=" + transactions +
                '}';
    }
}
