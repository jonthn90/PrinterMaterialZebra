package com.formiik.jamarprint.utils;

/**
 * Created by jonathan on 22/03/17.
 */

public class Constants {
    public static final boolean LOG_ENABLED = true;
    public static final String FORMIIK_DATA = "formiikdata";

    //region dates
    public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    public static final String DATE_FORMAT_PM = "dd/MM/yyyy hh:mm:ss a";

    public static final String DATE_FORMAT_ONLY_DATE = "yyyy-MM-dd";

    public static final String DATE_FORMAT_ONLY_TIME = "hh:mm:ss";

    public static final String DATE_FORMAT_PRINTER = "yyyy-MM-dd hh:mm:ss";

    public static final String SHORT_DATE_FORMAT = "dd/MM/yyyy hh:mm:ss";

    public static final String SHORT_DATE_FORMAT_ONLY_DATE = "dd/MM/yyyy";

    public static final String DATE_FORMAT_PLAIN = "yyyyMMdd hhmmss.ssss";
    //endregion

    //region common
    public static final int INVALID_VALUE = -1;

    //region animations
    public static final int ANIMATION_1X_DELAY = 300;

    public static final int ANIMATION_2X_DELAY = ANIMATION_1X_DELAY * 2;

    public static final int ANIMATION_3X_DELAY = ANIMATION_1X_DELAY * 3;

    public static final int FLOATING_ACTION_BUTTON_SHOW_HIDE_ANIMATION_DURATION = 300;

    public static final int FLOATING_ACTION_BUTTON_MORPH_ANIMATION_DURATION = 700;

    public static final String ACTIVITY_TRANSITION_Y_START_POINT_KEY = "ActivityTransitionYStartPoint";
    //endregion

    //region animations
    public static final int RECYCLER_VIEW_ITEMS_ANIMATION_DURATION = 700;

    public static final int RECYCLER_VIEW_ITEMS_ANIMATION_DELAY = 200;
    //endregion

    //region camposFormulario
    public static final String FORMIIKCARTERA_NOMBRE = "NomCli";
    public static final String FORMIIKCARTERA_TELEFONO_CLIENTE = "TelCli";
    public static final String FORMIIKCARTERA_CELULAR_CLIENTE = "CelCli";
    public static final String FORMIIKCARTERA_NUMERO_CREDITO = "NumCre";
    public static final String FORMIIKCARTERA_CEDULA_CLIENTE = "NumDoc";//No se requiere // Siempre si
    public static final String FORMIIKCARTERA_TIPO_CONTACTO = "Contacto";
    public static final String FORMIIKCARTERA_MONTO_PAGO = "MontoPago";
    public static final String FORMIIKCARTERA_NOMBRE_TERCERO = "NombreTercero";

    public static final String FORMIIKCARTERA_ZONA = "Zona";

    public static final String FORMIIKCARTERA_TIPO_DE_PAGO = "TipoPago";
    public static final String FORMIIKCARTERA_NUMERO_CHEQUE = "NumeroCheque";
    public static final String FORMIIKCARTERA_CODIGO_CHEQUE = "CodigoCheque";

    public static final String FORMIIKCARTERA_NUM_CUOTAS_CANCELAR = "numCuot";//no se requiere

    public static final String FORMIIKCARTERA_NOMBRE_EJECUTIVO = "Ejecutivo";
    public static final String FORMIIKCARTERA_CELULAR_SMS = "CelSMS";
    public static final String FORMIIKCARTERA_ENVIAR_SMS_BOOLEAN = "EnvioSMS";
    public static final String FORMIIKCARTERA_ENVIAR_SMS_CODEUDOR = "Codeudor";
    public static final String FORMIIKCARTERA_ENVIAR_SMS_CLIENTE = "Cliente";

    public static final String FORMIIKCARTERA_NUMERO_TRANSACCION = "NumTra";

    public static final String FORMIIKCARTERA_CELULAR_CODEUDOR = "CelCod";
    //endregion


    public static final String AFECTEDFIELDS_READONLY = "ReadOnly";//"{\"Key\":\"ReadOnly\",\"Value\":\"True\"}";
    public static final String AFECTEDFIELDS_VISIBLE = "Visible";//"{\"Key\":\"Visible\",\"Value\":\"True\"}";
    public static final String AFECTEDFIELDS_SETTINGS = "Settings";

    public static final int PRINT = 1;
    public static final int PRINT_COPY = 2;
    public static final int REPRINT = 3;


    //region shared preferences
    public static final String SHARED_PREFERENCES_NAME = "CrezcamosPreferences";
    //endregion

    //commons
    public static final String CARTERA_ORDER_TYPE = "Cartera";
    public static final String REPRINT_ORDER_TYPE = "Reimpresiones";


    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_FOOTER = 2;
    public static final int TYPE_IMAGE = 3;
    public static final int TYPE_EMPTY = 4;
    public static final int TYPE_FINAL = 5;


    public static final String WAIT_MESSAGE = "Enviando SMS, por favor espere.";
    public static final String RESULT_OK_MESSAGE = "Envío exitoso";
    public static final String RESULT_ERROR_MESSAGE = "Fallo de Envío";
    public static final String RESULT_ERRORPDU_MESSAGE = "Fallo de Envío, PDU No definido";
    public static final String RESULT_ERROR_NO_SERVICE = "Fallo de envío, Sin servicio";
    public static final String RESULT_ERROR_RADIOOFF = "Fallo de envío, radio off";

}