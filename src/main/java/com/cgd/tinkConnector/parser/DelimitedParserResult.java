package com.cgd.tinkConnector.parser;

import com.cgd.tinkConnector.TinkConnectorConfiguration;
import com.cgd.tinkConnector.Utils.ConversionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DelimitedParserResult {

    private Map<String, String> values = new HashMap<>();

    public void addValue(String field, String value) {

        values.put(field, value.trim());

    }

    public String getParameterAsString(String key) {
        return values.get(key);
    }

    public Long getParameterAsLong(String key) {

        return Long.valueOf(values.get(key));

    }

    public Date getParameterAsDate(String key) {

        return ConversionUtils.stringToDate(values.get(key));

    }

    public float getParameterAsAmount(String key) {

        String raw = values.get(key);

        int sign = -1;
        if (raw.substring(0, 1).equals("+")) sign = 1;

        BigDecimal dec = new BigDecimal(raw.replace(",", ".").substring(1));

        float val = dec.floatValue() * sign;

        if (TinkConnectorConfiguration.invertTransactionsSignal) return val * -1;

        return val;
    }


}
