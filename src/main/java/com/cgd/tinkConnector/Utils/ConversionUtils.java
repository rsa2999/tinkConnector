package com.cgd.tinkConnector.Utils;

import org.springframework.beans.factory.annotation.Value;

public class ConversionUtils {

    @Value("${cgd.invertTransactionsSignal:true}")
    private static boolean inverTransactionsSignal;

    public static float formatAmmount(float amount) {

        float t = (float) (amount / (float) 100);

        if (inverTransactionsSignal) return t * -1;
        else return t;
    }

}
