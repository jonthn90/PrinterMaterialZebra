package com.formiik.jamarprint.printer;

import com.formiik.jamarprint.utils.Constants;

/**
 * Created by jonathan on 22/03/17.
 */

public class PrinterLineZebra extends PrinterLine {

    protected boolean isFinalLine;

    {
        this.isFinalLine = false;
    }

    protected PrinterLineAlignmentZebra alignment;

    {
        this.alignment = PrinterLineAlignmentZebra.LEFT;
    }

    public boolean isFinalLine() {
        return isFinalLine;
    }

    public void setIsFinalLine(boolean isFinalLine) {
        this.isFinalLine = isFinalLine;
        this.typeLine = Constants.TYPE_FINAL;
    }

    public PrinterLineZebra() {
        super();
    }

    public PrinterLineZebra(boolean isEmptyLine) {
        super(isEmptyLine);
    }

    public PrinterLineZebra(String value) {
        super(value);
    }

    public PrinterLineZebra(String label, String value) {
        super(label, value);
    }

    public PrinterLineZebra(int image) {
        super(image);
    }

    public PrinterLineZebra(String value, PrinterLineAlignmentZebra alignment) {
        this(null, value, alignment);
    }

    public PrinterLineZebra(String label, String value, PrinterLineAlignmentZebra alignment) {
        this.label = label;
        this.value = value;

        if (this.label != null && !this.label.isEmpty()) {
            this.typeLine = Constants.TYPE_ITEM;
        }

        if (alignment != null) {
            this.alignmentName = alignment.name();
            this.alignment = alignment;
        }
    }

    @Override
    public void setIsHeader(boolean isHeader) {
        super.setIsHeader(isHeader);
        if (isHeader) this.typeLine = Constants.TYPE_HEADER;

        this.alignment = PrinterLineAlignmentZebra.CENTER;
        this.alignmentName = this.alignment.name();
    }

    @Override
    public void setIsFooter(boolean isFooter) {
        super.setIsFooter(isFooter);
        if (isFooter) this.typeLine = Constants.TYPE_FOOTER;
        this.alignment = PrinterLineAlignmentZebra.JUSTIFIED;
        this.alignmentName = this.alignment.name();
    }


    public String getDataToPrintDefaultFormat() {
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        //stringBuilder.append("^XA^POI^LL40");

        switch (this.typeLine) {
            case Constants.TYPE_FOOTER:
                stringBuilder.append("^XA^LL60^CI27");
                stringBuilder.append("^FO5,1");
                stringBuilder.append("\r\n");
                stringBuilder.append("^A0,N,16,16");
                stringBuilder.append("\r\n");
                stringBuilder.append("^FB380,10,,%s");
                stringBuilder.append("^FD%s %s^FS");
                stringBuilder.append("^XZ");

                line = String.format(stringBuilder.toString(), this.alignment.getAlignment(), this.getLabel(), this.getValue());
                break;
            case Constants.TYPE_HEADER:
                stringBuilder.append("^XA^LL40^CI27");
                stringBuilder.append("^FO5,1");
                stringBuilder.append("\r\n");
                stringBuilder.append("^A0,N,28,28");
                stringBuilder.append("\r\n");
                stringBuilder.append("^FB380,1,,%s");
                stringBuilder.append("^FD%s %s^FS");
                stringBuilder.append("^XZ");

                line = String.format(stringBuilder.toString(), this.alignment.getAlignment(), this.getLabel(), this.getValue());
                break;
            case Constants.TYPE_ITEM:
                stringBuilder.append("^XA^LL25^CI27");
                stringBuilder.append("^FO5,1");
                stringBuilder.append("\r\n");
                stringBuilder.append("^A0,N,20,20");
                stringBuilder.append("\r\n");
                stringBuilder.append("^FB380,2,,%s");
                stringBuilder.append("^FD%s %s^FS");
                stringBuilder.append("^XZ");

                line = String.format(stringBuilder.toString(), this.alignment.getAlignment(), this.getLabel(), this.getValue());
                break;
            case Constants.TYPE_IMAGE:
                stringBuilder.append("^XA^LL77^CI27");
                stringBuilder.append("^FO5,1");
                stringBuilder.append("\r\n");
                stringBuilder.append("^A0,N,20,20");
                stringBuilder.append("\r\n");
                stringBuilder.append("^FB380,10,,%s");

                stringBuilder.append("^FD%s^FS");
                stringBuilder.append("^XZ");

                line = String.format(stringBuilder.toString(), "", "\\&");
                break;
            case Constants.TYPE_EMPTY:
                stringBuilder.append("^XA^LL30^CI27");
                stringBuilder.append("^FO5,1");
                stringBuilder.append("\r\n");
                stringBuilder.append("^A0,N,20,20");
                stringBuilder.append("\r\n");
                stringBuilder.append("^FB380,10,,%s");

                stringBuilder.append("^FD%s^FS");
                stringBuilder.append("^XZ");

                line = String.format(stringBuilder.toString(), "", "\\&");
                break;
            case Constants.TYPE_FINAL:
                stringBuilder.append("^XA^MMD,N^LL30^CI27");
                stringBuilder.append("^FO5,1");
                stringBuilder.append("\r\n");
                stringBuilder.append("^A0,N,20,20");
                stringBuilder.append("\r\n");
                stringBuilder.append("^FB380,10,,%s");

                stringBuilder.append("^FD%s^FS");
                stringBuilder.append("^XZ");

                line = String.format(stringBuilder.toString(), "", "\\&");
                break;
        }


        return line;
    }
}
