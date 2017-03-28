package com.formiik.jamarprint.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.formiik.jamarprint.R;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by jonathan on 22/03/17.
 */

public class PrinterTicket {

    public static ArrayList<PrinterLine> getZebraPrinterLines() {

        ArrayList<PrinterLine> printerLines = new ArrayList<PrinterLine>();

        PrinterLineZebra printerLine;

        printerLine = new PrinterLineZebra(true);
        printerLine.setIsFinalLine(true);
        printerLines.add(printerLine);

        printerLines.add(new PrinterLineZebra(true));
        int image = R.drawable.ic_printer_black_48dp;
        printerLine = new PrinterLineZebra(image);
        printerLines.add(printerLine);

        String title = "RECIBO PROVISIONAL";

        printerLine = new PrinterLineZebra(title, PrinterLineAlignmentZebra.CENTER);
        printerLine.setIsHeader(true);
        printerLines.add(printerLine);

        printerLine = new PrinterLineZebra("***************************", PrinterLineAlignmentZebra.CENTER);
        printerLine.setIsHeader(true);
        printerLines.add(printerLine);

        //Ticket body

        printerLines.add(new PrinterLineZebra("No.:", "1234"));
        printerLines.add(new PrinterLineZebra("Fecha de pago: ", "22 Marzo 2017"));
        printerLines.add(new PrinterLineZebra("No. credito: ", "352"));
        printerLines.add(new PrinterLineZebra("Nombre de cliente:", "", PrinterLineAlignmentZebra.LEFT));
        printerLines.add(new PrinterLineZebra("", "Mark Zucker", PrinterLineAlignmentZebra.RIGHT));

        printerLines.add(new PrinterLineZebra("No. cedula: ", "3423"));
        printerLines.add(new PrinterLineZebra("No. celular: ", "5529493563"));
        printerLines.add(new PrinterLineZebra("Tipo de pago: ", "Parcial"));

        printerLines.add(new PrinterLineZebra(true));

        Locale locale = new Locale("es", "MX");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

        printerLines.add(new PrinterLineZebra("Valor de la cuota o abono:", " " + currencyFormatter.format(Double.valueOf(990))));

        printerLines.add(new PrinterLineZebra(true));

        printerLines.add(new PrinterLineZebra("Firma:_____________", "", PrinterLineAlignmentZebra.CENTER));
        printerLines.add(new PrinterLineZebra(true));
        printerLines.add(new PrinterLineZebra("Telefono:_______________", "", PrinterLineAlignmentZebra.CENTER));
        printerLines.add(new PrinterLineZebra(true));

        printerLines.add(new PrinterLineZebra("Ejecutivo: _______________________", "", PrinterLineAlignmentZebra.LEFT));

        printerLines.add(new PrinterLineZebra(true));

        //footer
        printerLine = new PrinterLineZebra("NOTAS ADICIONALES:", PrinterLineAlignmentZebra.JUSTIFIED);
        printerLine.setIsFooter(true);
        printerLines.add(printerLine);

        printerLine = new PrinterLineZebra("Conserve este ticket. En caso de reclamo llamar a la linea de soluciones: 3208899800.", PrinterLineAlignmentZebra.JUSTIFIED);
        printerLine.setIsFooter(true);
        printerLines.add(printerLine);

        printerLine = new PrinterLineZebra("Ser puntual en sus pagos le ayudara a tener un buen historial crediticio.", PrinterLineAlignmentZebra.JUSTIFIED);
        printerLine.setIsFooter(true);
        printerLines.add(printerLine);
        return printerLines;
    }


    public static ArrayList<String> getLinesRaw() {

        ArrayList<String> printerLines = new ArrayList<>();

        //printerLines.add("^XA^LL30^FB380,10,,^FD\\&^FS^XZ");
        printerLines.add("^XA^LL30^ADN,18,10^FB380,1,0,L^FDNombre: Jonathan Oropeza^FS^XZ^XA^LL40^A0N,28,28^FB380,1,0,C^FDEl recibo^FS^XZ");
        //printerLines.add("^XA^LL40^ADN,18,10^FB380,1,0,R^FDEl recibo^FS^XZ");
        //printerLines.add("^XA^LL30^ADN,18,10^FB380,1,0,L^FDNombre: Jonathan Oropeza^FS^XZ");
        //printerLines.add("^XA^LL30^A0N,25,25^FB380,1,0,C^FDEsto es una prueba manual^FS^XZ");


        return printerLines;
    }
}
