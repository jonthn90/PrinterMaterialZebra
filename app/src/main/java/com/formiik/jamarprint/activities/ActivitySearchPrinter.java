package com.formiik.jamarprint.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.formiik.jamarprint.R;
import com.formiik.jamarprint.adapters.AdapterBluetoothDevices;
import com.formiik.jamarprint.adapters.DividerItemDecoration;
import com.formiik.jamarprint.printer.PrinterLine;
import com.formiik.jamarprint.printer.PrinterLineZebra;
import com.formiik.jamarprint.printer.PrinterTicket;
import com.formiik.jamarprint.utils.Constants;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class ActivitySearchPrinter extends AppCompatActivity implements  AdapterBluetoothDevices.ConnectDeviceListener, View.OnClickListener{

    private RecyclerView mRecyclerView;
    private AdapterBluetoothDevices mAdapterBluetooth;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar toolbar;
    private ProgressBar mSpinner;
    private FloatingActionButton mPrintFAB;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothReceiver mBluetoothReceiver;

    public static ArrayList<BluetoothDevice> mBluetoothDevices;
    public static ArrayList<BluetoothDeviceFound> mBluetoothDevicesFound;

    public static BluetoothDevice mPrinter;

    private CountDownTimer mCountDownTimer;

    private ProgressDialog mProgressDialog;

    private Connection zebraConnection;

    public static int                   REQUEST_BLUETOOTH = 1;
    public static final int             REQUEST_CODE_ASK_PERMISSIONS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_printer);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !checkPermissionsDangerous(this)) {
            requestAllPermissionsDangerous();
        }

        mBluetoothDevices = new ArrayList<>();
        mBluetoothDevicesFound = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_devices);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapterBluetooth = new AdapterBluetoothDevices(this, mBluetoothDevicesFound);
        mRecyclerView.setAdapter(mAdapterBluetooth);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        mSpinner = (ProgressBar) findViewById(R.id.spinner);

        mPrintFAB = (FloatingActionButton) findViewById(R.id.fab_print);
        mPrintFAB.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_s);
        toolbar.setTitle("Seleccionar impresora");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitySearchPrinter.super.onBackPressed();
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /*
            Revisa si el telefono cuenta con bluetooth
         */
        if (mBluetoothAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.no_compatible)
                    .setMessage(R.string.no_bluetooh)
                    .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        enableBluetoothAdapter();

        /*
            BroadcastReceiver de Bluetooth para detectar eventos
         */
        mBluetoothReceiver  = new BluetoothReceiver();

        registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST));
        registerReceiver(mBluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST));

        /*
            Contador regresivo para detener la busqueda de dispositivos despues de 20 segundos
         */
        mCountDownTimer =  new CountDownTimer(1000 * 20, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                mSpinner.setVisibility(View.GONE);

                endDevicesSearch();
                Toast.makeText(getApplicationContext(), R.string.time_search_expired, Toast.LENGTH_SHORT).show();

                if(mBluetoothDevices.size() == 0){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), R.string.no_devices, Toast.LENGTH_LONG).show();
                }
            }
        };

        searchBluetooth();
    }

    public void enableBluetoothAdapter(){
        /*
            Encerder Bluetooth
         */
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }
    }



    /*
        BroadcastReceiver de Bluetooth para detectar eventos de inicio y termino de Discovering,
        dispositivos encontrados, dispositivos vinculados, dispositivos conectados y deconcectados
        y peticion de emparejamiento
     */
    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (android.bluetooth.BluetoothDevice.ACTION_FOUND.equals(action)) {
                android.bluetooth.BluetoothDevice device = intent.getParcelableExtra(android.bluetooth.BluetoothDevice.EXTRA_DEVICE);

                if (device != null && !mBluetoothDevices.contains(device)) {
                    mBluetoothDevices.add(device);
                    if (device.getName() != null) {
                        mBluetoothDevicesFound.add(new BluetoothDeviceFound(device.getName(), device.getAddress(), false));
                        Log.e("FOUND", "" +device.getName() + "  add:" +device.getAddress());
                    }else{
                        mBluetoothDevicesFound.add(new BluetoothDeviceFound(getResources().getString(R.string.no_name), device.getAddress(), false));
                    }
                    mAdapterBluetooth.notifyDataSetChanged();
                }
            }else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(getApplicationContext(), R.string.searching, Toast.LENGTH_SHORT).show();

            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                endDevicesSearch();

            }else if (android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state        = intent.getIntExtra(android.bluetooth.BluetoothDevice.EXTRA_BOND_STATE, android.bluetooth.BluetoothDevice.ERROR);
                final int prevState    = intent.getIntExtra(android.bluetooth.BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, android.bluetooth.BluetoothDevice.ERROR);

                if (state == android.bluetooth.BluetoothDevice.BOND_BONDED && prevState == android.bluetooth.BluetoothDevice.BOND_BONDING) {
                    //Toast.makeText(getApplicationContext(), "Conectado", Toast.LENGTH_SHORT).show();
                } else if (state == android.bluetooth.BluetoothDevice.BOND_NONE && prevState == android.bluetooth.BluetoothDevice.BOND_BONDED){
                    //Toast.makeText(getApplicationContext(), "Desconectado", Toast.LENGTH_SHORT).show();
                }

            }else if (android.bluetooth.BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                String pin = "1234";

                try {
                    byte[] pinBytes = pin.getBytes();
                    Method m = mPrinter.getClass().getMethod("setPin", byte[].class);
                    m.invoke(mPrinter, pinBytes);
                    mPrinter.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(mPrinter, true);

                    /*BluetoothDeviceFound device = intent.getParcelableExtra(BluetoothDeviceFound.EXTRA_DEVICE);
                    int pinINT = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 1234);
                    //the pin in case you need to accept for an specific pin
                    Log.d(TAG, "Start Auto Pairing. PIN = " + intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY",1234));
                    byte[] pinBytes2;
                    pinBytes2 = (""+pinINT).getBytes("UTF-8");
                    device.setPin(pinBytes2);
                    //setPairing confirmation if neeeded
                    device.setPairingConfirmation(true);
                    */
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else if (android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Conectado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
        Detener al busqueda de dispositivos bluetooth
     */
    private void endDevicesSearch(){
        //mButton_search.setText(R.string.search);
        mCountDownTimer.cancel();
        mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(mBluetoothAdapter.isEnabled() && requestCode == REQUEST_BLUETOOTH) {
            if(!mBluetoothAdapter.isDiscovering()){

            }
        }
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_search:

                searchBluetooth();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void searchBluetooth() {

        mPrintFAB.setVisibility(View.GONE);

        if(!mBluetoothAdapter.isEnabled()) {
            enableBluetoothAdapter();
            return;
        }

        if(!mBluetoothAdapter.isDiscovering()){

            mSpinner.setVisibility(View.VISIBLE);

            if(!mBluetoothDevices.isEmpty())mBluetoothDevices.clear();
            if(!mBluetoothDevicesFound.isEmpty()) mBluetoothDevicesFound.clear();


            mAdapterBluetooth.clear();
            mAdapterBluetooth.notifyDataSetChanged();

            /*
                Busca que dispositivos ya estaban emparejados al telefono y los agrega a la
                mPrintersNamesIDs

                Un dispositivo emparejado no implica que el dispositivo este encendido o conectado
            */
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            for(BluetoothDevice deviceBonded : pairedDevices){
                mBluetoothDevices.add(deviceBonded);
                if (deviceBonded.getName() != null) {
                    mBluetoothDevicesFound.add(new BluetoothDeviceFound(deviceBonded.getName(), deviceBonded.getAddress(), false));
                }else{
                    mBluetoothDevicesFound.add(new BluetoothDeviceFound(getResources().getString(R.string.no_name), deviceBonded.getAddress(), false));
                }
            }

            mAdapterBluetooth.notifyDataSetChanged();

            mBluetoothAdapter.startDiscovery();
            mCountDownTimer.start();

        }else {
            endDevicesSearch();
            mSpinner.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), R.string.search_sttoped, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBluetoothReceiver);

        if(mBluetoothAdapter.isDiscovering()){
            endDevicesSearch();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestAllPermissionsDangerous() {
        requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,},
                REQUEST_CODE_ASK_PERMISSIONS);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkPermissionsDangerous(Context context) {
        return context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults.length > 0) {
                    for (int gr : grantResults) {
                        // Check if request is granted or not
                        if (gr != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                }

                break;
            default:
                return;
        }
    }

    public class BluetoothDeviceFound {

        public String mName, mAddress;
        public boolean isSelected;

        public BluetoothDeviceFound(String mName, String mAddress, boolean isSelected) {
            this.mName = mName;
            this.mAddress = mAddress;
            this.isSelected = isSelected;
        }
    }

    @Override
    public void connectDevice(int position) {

        mPrintFAB.setVisibility(View.GONE);

        if(mBluetoothAdapter.isDiscovering()){
            endDevicesSearch();
            mSpinner.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), R.string.search_sttoped, Toast.LENGTH_SHORT).show();
        }

        mPrinter = mBluetoothDevices.get(position);

        if(!BluetoothAdapter.checkBluetoothAddress(mPrinter.getAddress())){

            Toast.makeText(getApplicationContext(), R.string.error_mac, Toast.LENGTH_LONG).show();

            return;
        }

        new ConnectPrinterAsyncTask(position).execute(mPrinter.getAddress());

    }

    private class ConnectPrinterAsyncTask extends AsyncTask<String, Void, Boolean> {

        private int position;

        public ConnectPrinterAsyncTask(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(R.string.conecting);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                if(zebraConnection!= null && zebraConnection.isConnected())
                    zebraConnection.close();
            } catch (ConnectionException ex) {
                ex.printStackTrace();
            }

            String address = params[0];
            try {
                zebraConnection = new BluetoothConnection(address);
                zebraConnection.open();
                return true;
            } catch (ConnectionException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {

                for (int i = 0; i< mBluetoothDevicesFound.size() ; i++) {
                    mBluetoothDevicesFound.get(i).isSelected = i == position;
                }
                mAdapterBluetooth.notifyDataSetChanged();

                mPrintFAB.setVisibility(View.VISIBLE);

            } else {

                Toast.makeText(getApplicationContext(), R.string.error_no_zebra, Toast.LENGTH_LONG).show();
            }

            dismissProgressDialog();
        }
    }

    private class PrintingAsyncTask extends AsyncTask<ArrayList<PrinterLine>, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(R.string.printing);
        }

        @Override
        protected Boolean doInBackground(ArrayList<PrinterLine>... params) {

            ArrayList<PrinterLine> lines = params[0];

            for (int j = lines.size() - 1; j >= 0; j--) {
                PrinterLine line = lines.get(j);
                if (line != null)
                    if (!printLineZebra(line)){
                        return false;
                    }
            }

            return true;
        }

        private boolean printLineZebra(PrinterLine line) {
            try {
                if (line.isImage()) {
                    zebraConnection.write(((PrinterLineZebra) line).getDataToPrintDefaultFormat().getBytes());
                    Bitmap largeIconCrezcamos = BitmapFactory.decodeResource(getResources(), R.drawable.ic_printer_black_48dp);
                    ZebraPrinter printer = ZebraPrinterFactory.getInstance(zebraConnection);
                    ZebraImageAndroid zebraImageAndroid = new ZebraImageAndroid(largeIconCrezcamos);
                    printer.printImage(zebraImageAndroid, 5, 0, 360, 77, false);
                } else {
                    zebraConnection.write(((PrinterLineZebra) line).getDataToPrintDefaultFormat().getBytes());
                }
                return true;

            } catch (ConnectionException ex) {
                ex.printStackTrace();
                return false;
            } catch (ZebraPrinterLanguageUnknownException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Toast.makeText(getApplicationContext(), R.string.finished_print, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_printing, Toast.LENGTH_LONG).show();
            }

            dismissProgressDialog();
        }
    }

    private class PrintingRawAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(R.string.printing);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                zebraConnection.write(params[0].getBytes());
            } catch (ConnectionException ex) {
                ex.printStackTrace();
                return false;
            }

            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo_crez_bn);
            ZebraImageAndroid zebraImageAndroid = new ZebraImageAndroid(logo);
            ZebraPrinter printer = null;

            try {
                String image = "^XA^LL77^A0N,20,20^FB380,10,0,C^FD\\&^FS^XZ";
                zebraConnection.write(image.getBytes());
                printer = ZebraPrinterFactory.getInstance(zebraConnection);
                printer.printImage(zebraImageAndroid, 0, 0 , 360, 77, false);
                String end = "^XA^LL30^A0N,20,20^FB380,10,0,C^FD\\&^FS^XZ";
                zebraConnection.write(end.getBytes());
            } catch (ConnectionException e) {
                e.printStackTrace();
                return false;
            } catch (ZebraPrinterLanguageUnknownException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Toast.makeText(getApplicationContext(), R.string.finished_print, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_printing, Toast.LENGTH_LONG).show();
            }

            dismissProgressDialog();
        }
    }

    private void showProgressDialog(int stringResourceId) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(stringResourceId));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.fab_print:

                //new PrintingAsyncTask().execute(PrinterTicket.getZebraPrinterLines());

                String ticket =

                        /*

                "^XA^LL70^ADN,10,10^FB380,5,0,J^FDSer puntual en sus pagos le ayudara a tener un buen historial crediticio.^FS^XZ" +
                "^XA^LL70^ADN,10,10^FB380,5,0,J^FDConserve este ticket. En caso de reclamo llamar a la linea de soluciones: 3208899800.^FS^XZ" +
                "^XA^LL80^ADN,20,12^FB380,1,0,J^FDNOTAS ADICIONALES:^FS^XZ" +
                "^XA^LL50^ADN,18,10^FB380,1,0,L^FDEjecutivo: ___________________^FS^XZ" +
                "^XA^LL50^ADN,18,10^FB380,1,0,L^FDTelefono:_____________________^FS^XZ" +
                "^XA^LL50^ADN,18,10^FB380,1,0,L^FDFirma:________________________^FS^XZ" +
                "^XA^LL40^FB380,1,,^FD\\&^FS^XZ" +
                "^XA^LL50^ADN,18,10^FB380,2,0,L^FDValor de cuota o abono: $1934.50^FS^XZ" +
                "^XA^LL30^ADN,18,10^FB380,1,0,L^FDTipo de pago: Efectivo^FS^XZ" +
                "^XA^LL30^ADN,18,10^FB380,1,0,L^FDNo. celular: 5512345678^FS^XZ" +
                "^XA^LL30^ADN,18,10^FB380,1,0,L^FDNo. cedula: 23423^FS^XZ" +
                "^XA^LL30^ADN,18,10^FB380,1,0,L^FDFulano Dominguez Perez^FS^XZ" +
                "^XA^LL30^ADN,18,10^FB380,1,0,L^FDNombre de cliente:^FS^XZ" +
                "^XA^LL30^ADN,18,10^FB380,1,0,L^FDNo. credito: 23^FS^XZ" +
                "^XA^LL30^ADN,18,10^FB380,1,0,L^FDFecha de pago: 17/03/2017^FS^XZ" +
                "^XA^LL40^ADN,25,17^FB380,1,0,C^FD***************^FS^XZ" +
                "^XA^LL60^ADN,25,17^FB380,2,0,C^FDRECIBO PROVISIONAL^FS^XZ";

                        */
                "^XA^LL30^A0N,16,16^FB380,3,0,J^FDSer puntual en sus pagos le ayudara a tener un buen historial crediticio.^FS^XZ" +
                "^XA^LL40^A0N,16,16^FB380,3,0,J^FDConserve este ticket. En caso de reclamo llamar a la linea de soluciones: 3208899800.^FS^XZ" +
                "^XA^LL80^A0N,20,20^FB380,1,0,J^FDNOTAS ADICIONALES:^FS^XZ" +
                "^XA^LL50^A0N,20,20^FB380,1,0,L^FDEjecutivo: ___________________________^FS^XZ" +
                "^XA^LL50^A0N,20,20^FB380,1,0,L^FDTelefono:_____________________________^FS^XZ" +
                "^XA^LL50^A0N,20,20^FB380,1,0,L^FDFirma:________________________________^FS^XZ" +
                "^XA^LL40^FB380,1,,^FD\\&^FS^XZ" +
                "^XA^LL30^A0N,20,20^FB380,1,0,L^FDValor de cuota o abono: $1934.50^FS^XZ" +
                "^XA^LL30^A0N,20,20^FB380,1,0,L^FDTipo de pago: Efectivo^FS^XZ" +
                "^XA^LL30^A0N,20,20^FB380,1,0,L^FDNo. celular: 5512345678^FS^XZ" +
                "^XA^LL30^A0N,20,20^FB380,1,0,L^FDNo. cedula: 23423^FS^XZ" +
                "^XA^LL30^A0N,20,20^FB380,1,0,L^FDFulano Dominguez Perez^FS^XZ" +
                "^XA^LL30^A0N,20,20^FB380,1,0,L^FDNombre de cliente:^FS^XZ" +
                "^XA^LL30^A0N,20,20^FB380,1,0,L^FDNo. credito: 23^FS^XZ" +
                "^XA^LL30^A0N,20,20^FB380,1,0,L^FDFecha de pago: 17/03/2017^FS^XZ" +
                "^XA^LL40^A0N,28,28^FB380,1,0,C^FD***************************^FS^XZ" +
                "^XA^LL40^A0N,28,28^FB380,1,0,C^FDRECIBO PROVISIONAL^FS^XZ";

                        


                new PrintingRawAsyncTask().execute(ticket);

                break;
        }
    }
}
