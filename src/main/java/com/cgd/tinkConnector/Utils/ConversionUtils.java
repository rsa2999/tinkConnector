package com.cgd.tinkConnector.Utils;

import com.cgd.tinkConnector.TinkConnectorConfiguration;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConversionUtils {


    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

    private static MessageDigest md;

    public static float formatAmmount(float amount) {


        return formatAmmount(amount, TinkConnectorConfiguration.invertTransactionsSignal);
    }

    public static float formatAmmount(float amount, boolean invertTransactionsSignal) {

        float t = (float) (amount / (float) 100);

        if (invertTransactionsSignal) return t * -1;
        else return t;
    }

    public static Date stringToDate(String date) {

        try {
            return dateFormatter.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public synchronized static String generateAccountExternalId(Long numClient, String accountNumber) {
        try {
            if (md == null) {

                md = MessageDigest.getInstance("MD5");

            }
            md.update((numClient.toString() + accountNumber).getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toLowerCase();


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String maskAccountNumber(String accountNumber) {

        return String.format("%s **** **** **%s", accountNumber.substring(0, 4), accountNumber.substring(accountNumber.length() - 2));
    }

    public synchronized static String generateInternalAccountId(String externalAccountId, String tinkId) {
        try {
            if (md == null) {

                md = MessageDigest.getInstance("MD5");

            }

            md.update((externalAccountId.toString() + tinkId).getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toLowerCase();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;

    }

    public synchronized static String generateTransactionExternalId(Long numClient, String accountNumber, float amount, String description, long date) {
        try {
            if (md == null) {

                md = MessageDigest.getInstance("MD5");

            }

            md.update((numClient.toString() + accountNumber + Math.abs(amount) + description + date).getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toLowerCase();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;

    }


}
