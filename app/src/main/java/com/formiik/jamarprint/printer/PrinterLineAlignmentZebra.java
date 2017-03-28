package com.formiik.jamarprint.printer;

/**
 * Created by jonathan on 22/03/17.
 */
public enum PrinterLineAlignmentZebra {
    LEFT("L"),

    RIGHT("R"),

    CENTER("C"),

    JUSTIFIED("J");

    String value;

    PrinterLineAlignmentZebra(String value) {
        this.value = value;
    }

    public String getAlignment() {
        return String.format("%s", this.value);
    }

}
