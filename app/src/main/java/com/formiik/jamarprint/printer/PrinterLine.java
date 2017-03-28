package com.formiik.jamarprint.printer;

import android.os.Parcel;
import android.os.Parcelable;

import com.formiik.jamarprint.utils.Constants;

/**
 * Created by jonathan on 22/03/17.
 */

public class PrinterLine implements Parcelable {
    public static final String TAG = "crezcamos_priterline";

    protected int typeLine;
    {
        typeLine = -1;
    }

    protected boolean emptyLine = false;

    protected boolean isHeader = false;

    protected boolean isFooter = false;

    protected boolean isImage = false;

    protected String label;

    protected String value;

    protected int image;


    protected String alignmentName;

    public PrinterLine() {
        this.emptyLine = false;
        this.isHeader = false;
        this.isFooter = false;
        this.isImage = false;
        this.label = "";
        this.value = "";
        //this.alignmentName = PrinterLineAlignment.LEFT.name();
        //this.alignment = PrinterLineAlignment.LEFT;
    }

    public PrinterLine(boolean isEmptyLine) {
        this.emptyLine = isEmptyLine;
        this.typeLine = Constants.TYPE_EMPTY;
    }

    public PrinterLine(String value) {
        this.value = value;
    }

    public PrinterLine(String label, String value) {
        this.label = label;
        this.value = value;
        this.typeLine = Constants.TYPE_ITEM;
    }

    public PrinterLine(int drawable) {
        this.image = drawable;
        this.isHeader = false;
        this.isFooter = false;
        this.emptyLine = false;
        //this.
        this.isImage = true;
        this.typeLine = Constants.TYPE_IMAGE;
    }

    protected PrinterLine(Parcel in) {
        this.emptyLine = in.readByte() != 0;

        if (!this.emptyLine) {
            this.isHeader = in.readByte() != 0;
            this.isFooter = in.readByte() != 0;
            this.isImage = in.readByte() != 0;

            this.label = in.readString();
            this.value = in.readString();
            this.alignmentName = in.readString();

            this.typeLine = in.readInt();
        }

    }

    public static final Creator<PrinterLine> CREATOR = new Creator<PrinterLine>() {
        @Override
        public PrinterLine createFromParcel(Parcel in) {
            return new PrinterLine(in);
        }

        @Override
        public PrinterLine[] newArray(int size) {
            return new PrinterLine[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (this.emptyLine ? 1 : 0));

        if (!this.emptyLine) {
            dest.writeByte((byte) (this.isHeader ? 1 : 0));
            dest.writeByte((byte) (this.isFooter ? 1 : 0));
            dest.writeByte((byte) (this.isImage ? 1 : 0));
            dest.writeString(this.label);
            dest.writeString(this.value);
            dest.writeString(this.alignmentName);
            dest.writeInt(this.typeLine);
        }
    }

    public boolean isEmptyLine() {
        return this.emptyLine;
    }

    public void setEmptyLine(boolean isEmptyLine) {
        this.emptyLine = isEmptyLine;
    }

    public String getLabel() {
        return this.label != null ? this.label : "";
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return this.value != null ? this.value : "";
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isHeader() {
        return this.isHeader;
    }

    public void setIsHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public boolean isFooter() {
        return this.isFooter;
    }

    public void setIsFooter(boolean isFooter) {
        this.isFooter = isFooter;
    }

    public boolean isImage() {
        return this.isImage;
    }

    public void setIsImage(boolean isImage) {
        this.isImage = isImage;
    }

    public int getImage() {
        return this.image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getTypeLine() {
        return typeLine;
    }

    public void setTypeLine(int typeLine) {
        this.typeLine = typeLine;
    }
}






