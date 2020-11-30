package com.cgd.tinkConnector.parser;

public class DelimitedParserField {

    private int startPosition;
    private int length;

    private String name;

    public DelimitedParserField(String name, int length) {
        this.length = length;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
