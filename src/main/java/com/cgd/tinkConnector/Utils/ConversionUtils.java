package com.cgd.tinkConnector.Utils;

import com.cgd.tinkConnector.TinkConnectorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConversionUtils {


    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
    private static Logger LOGGER = LoggerFactory.getLogger(ConversionUtils.class);
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
            LOGGER.error("stringToDate ", e);
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
            LOGGER.error("generateAccountExternalId ", e);
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
            LOGGER.error("generateInternalAccountId ", e);
        }

        return null;

    }

    public synchronized static String generateTransactionExternalId(Long numClient, String accountNumber, float amount, String description, long date, int position) {
        try {
            if (md == null) {

                md = MessageDigest.getInstance("MD5");

            }

            md.update((numClient + accountNumber + amount + description + date + position).getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toLowerCase();

        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("generateTransactionExternalId ", e);
        }

        return null;

    }


}
