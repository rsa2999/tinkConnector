package com.cgd.tinkConnector.parser;

import java.util.ArrayList;
import java.util.List;

public class DelimitedParserInfo {

    private List<DelimitedParserField> parserFields = new ArrayList<>();

    private int currentPosition = 0;

    public void addField(String name, int lenght) {

        DelimitedParserField field = new DelimitedParserField(name, lenght);
        field.setStartPosition(currentPosition);
        currentPosition += lenght;
        parserFields.add(field);

    }


    public DelimitedParserResult parserLine(String line) {

        DelimitedParserResult ret = new DelimitedParserResult();

        for (DelimitedParserField field : this.parserFields) {
            String value = line.substring(field.getStartPosition(), field.getStartPosition() + field.getLength());
            ret.addValue(field.getName(), value);
        }

        return ret;
    }


}
