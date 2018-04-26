package com.sourcey.cowtech54;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.TableOrder;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Created by Wily on 07/03/2018.
 */



public class BluetoothActivity extends AppCompatActivity {

    // GUI Components
    CheckBox btRadio;
    CheckBox sdRadio;
    CheckBox lvRadio;
    CheckBox obdRadio;

    private TextView lostIDsText;
    private TextView recIDsText;
    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    Button mScanBtn;
    Button mOffBtn;
    Button mListPairedDevicesBtn;
    Button mDiscoverBtn;
    private BluetoothAdapter mBTAdapter;
    Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    ListView mDevicesListView;
    CheckBox gpsCheckbox;
    private String gpsSwitch = "gpsOff";

    String mMessage; //Message to transmit to main

    private final String TAG = BluetoothActivityV1.class.getSimpleName();
    private static Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier


    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    // Defines
    private XYPlot plot;
    Button updPlotBtn;
    Button listIDsBtn;
    Button stopPlotBtn;
    private Spinner spinnerID;
    private PlotRun plotRun;

    //Radio btns (Checkbox as radio btns)
    CheckBox radioB1;
    CheckBox radioB2;
    CheckBox radioB3;
    CheckBox radioB4;
    CheckBox radioB5;
    CheckBox radioB6;
    CheckBox radioB7;
    CheckBox radioB8;

    //BtMessageManager
    public BtMessageManager btMessageManager;

    //CSV
    private Button updateTimeBtn;
    private Button deleteCSVBtn;
    private EditText updateTimeTxt;
    private TextView filePathTxt;
    private TextView newTimeStatusTxt;
    private EditText folderTxt;
    private EditText prefixTxt;
    private Button updPrefixFolderBtn;
    //Initilizing CSV File
    private BtMessageFiles fileCSV;

    //Preferences
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Preferences
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        //Create BtMessageManager object
        btMessageManager = new BtMessageManager();
        //Init plotRun
        plotRun = new PlotRun();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);

        btRadio = (CheckBox) findViewById(R.id.modeBtRadio);
        sdRadio = (CheckBox) findViewById(R.id.modeSDRadio);
        lvRadio = (CheckBox) findViewById(R.id.modeLabviewRadio);
        obdRadio = (CheckBox) findViewById(R.id.modeOBDRadio);


        updPlotBtn = (Button)findViewById(R.id.updatePlotBtn);
        listIDsBtn = (Button) findViewById(R.id.listIdsBtn);
        stopPlotBtn = (Button) findViewById(R.id.stopPlotBtn);
        spinnerID = (Spinner) findViewById(R.id.listIdsSpinner);

        updateTimeBtn = (Button) findViewById(R.id.updateTimeBtn);
       // deleteCSVBtn = (Button) findViewById(R.id.deleteFileBtn);
        updateTimeTxt = (EditText) findViewById(R.id.updateTimeText);
        filePathTxt = (TextView) findViewById(R.id.fileText);
        newTimeStatusTxt = (TextView) findViewById(R.id.newTimeStatusText);
        folderTxt = (EditText) findViewById(R.id.folderText);
        prefixTxt = (EditText) findViewById(R.id.prefixText);
        updPrefixFolderBtn = (Button) findViewById(R.id.updatePrefixFolderBtn);

        radioB1 = (CheckBox)findViewById(R.id.radioB1);
        radioB2 = (CheckBox)findViewById(R.id.radioB2);
        radioB3 = (CheckBox)findViewById(R.id.radioB3);
        radioB4 = (CheckBox)findViewById(R.id.radioB4);
        radioB5 = (CheckBox)findViewById(R.id.radioB5);
        radioB6 = (CheckBox)findViewById(R.id.radioB6);
        radioB7 = (CheckBox)findViewById(R.id.radioB7);
        radioB8 = (CheckBox)findViewById(R.id.radioB8);

        lostIDsText = (TextView) findViewById(R.id.lostText);
        recIDsText = (TextView) findViewById(R.id.recIDsText);

        mBluetoothStatus = (TextView)findViewById(R.id.bluetoothStatus);
        mReadBuffer = (TextView) findViewById(R.id.readBuffer);
        mScanBtn = (Button)findViewById(R.id.scan);
        mOffBtn = (Button)findViewById(R.id.off);
        mDiscoverBtn = (Button)findViewById(R.id.discover);
        mListPairedDevicesBtn = (Button)findViewById(R.id.PairedBtn);
        gpsCheckbox = (CheckBox)findViewById(R.id.checkboxGps);

        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = (ListView)findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Ask for location permission if not already allowed
        /*if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        */



        //Initilizing CSV File with stored preferences
        initBtMessageFiles();


        bluetoothOn();

        //Set MODE radio buttons
        btRadio.setChecked(true);
        sdRadio.setChecked(true);



        //Connect to COW if available
        if(mBTAdapter.isEnabled()) {
            //TODO: Create strings in SharedPreferences that are to update an
            spawnConnectThread("98:D3:32:20:E2:08", "COW");
        }
        mHandler = new Handler(){
            public void handleMessage(Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    //readMessage = readMessage.replaceAll("�","");
                    //Delete first line if it does not begin with '['
                    /*char[] firstChar = {'c'}; //new char[1];
                    readMessage.getChars(0,1,firstChar,0);
                    if(firstChar[0]!='['){
                        recIDsText.setText(Character.toString(firstChar[0]));
                        readMessage = readMessage.replaceFirst(".*\n","");

                    }*/

                    //TODO string. substring( string.length()+1)   if equals ] then it is ok
                    btMessageManager.addNewMessage(readMessage);
                   
                    lostIDsText.setText(String.valueOf(btMessageManager.getLostIDs()));
                    recIDsText.setText(String.valueOf(btMessageManager.getRecIDs()));
                    //recIDsText.setText(Character.toString(firstChar[0]));

                    mReadBuffer.setText(btMessageManager.getMessagePurged());
                    //Using Copy to avoid conflict while saving in file
                    if(fileCSV!=null)
                        fileCSV.writeInFile(btMessageManager.getMessagePurgedCopy());
                    //mReadBuffer.setText(readMessage);
                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1)
                        mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }
            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }
        else {

            gpsCheckbox.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){  gpsOnOff();           }
            });

            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn();
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    bluetoothOff(v);
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){ listPairedDevices(v); }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    discover(v);
                }
            });

            //Plot
            updPlotBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    updatePlot();
                }
            });
            stopPlotBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    plotRun.setStop(true);
                }
            });

            //IDsList
            listIDsBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    updateIDsList();
                }
            });

            //CSV
            updateTimeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newTime = Integer.parseInt( updateTimeTxt.getText().toString());
                    if(newTime >= 1 && newTime <= 60) {
                        fileCSV.setTimeGenerator(newTime);
                        newTimeStatusTxt.setText(Integer.toString(newTime));
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Time btw [1- 60]",Toast.LENGTH_SHORT).show();
                }
            });

            updPrefixFolderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fileCSV.setFolder(folderTxt.getText().toString());
                    fileCSV.setPrefix(prefixTxt.getText().toString());

                    Toast.makeText(getApplicationContext(),"Filepath updated",Toast.LENGTH_SHORT).show();
                }
            });
        }

        initPlot();
    }

    public void initBtMessageFiles(){
        //Initilizing CSV File with stored preferences
        String extStore = System.getenv("EXTERNAL_STORAGE");
        File f_exts = new File(extStore);

        String dirStr = f_exts.getAbsolutePath() + File.separator + getResources().getString(R.string.app_name);

        String defaultString = getResources().getString(R.string.folder_string_default);
        String folderStr = sharedPref.getString(getString(R.string.folder_string), defaultString);

        defaultString = getResources().getString(R.string.prefix_string_default);
        String prefixStr = sharedPref.getString(getString(R.string.prefix_string), defaultString);

        int defaultInt = 5; //5 min of def
        int timeFileGenerator = sharedPref.getInt(getString(R.string.update_time_file), defaultInt);

        fileCSV = new BtMessageFiles(prefixStr, folderStr);
        fileCSV.setParentDirStr(dirStr);
        fileCSV.setFilePathTextView(filePathTxt);
        //fileCSV.setParentDirStr("/storage/9016-4EF8/Android/data/com.sourcey.cowtech54/files"); //set the parent directory of app
        fileCSV.initTimerGenerator(timeFileGenerator);

        //Upd UI
        newTimeStatusTxt.setText(Integer.toString(timeFileGenerator));
        updateTimeTxt.setText(Integer.toString(timeFileGenerator));
        folderTxt.setText(folderStr);
        prefixTxt.setText(prefixStr);
    }

    @Override
    public void onPause(){
        //Save preferences
        if(fileCSV!=null) {
            editor.putString(getString(R.string.parent_dir_string), fileCSV.getParentDirStr());
            editor.putInt(getString(R.string.update_time_file), fileCSV.getTimeGenerator());

            editor.putString(getString(R.string.folder_string), fileCSV.getFolder());
            editor.putString(getString(R.string.prefix_string), fileCSV.getPrefix());
            editor.commit();
        }
        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();
    }
    @Override
    public void onDestroy() {
        //Save preferences
        editor.putString(getString(R.string.parent_dir_string), fileCSV.getParentDirStr());
        editor.putInt(getString(R.string.update_time_file), fileCSV.getTimeGenerator());
        editor.commit();
        //Stop the autogenerating file task
        fileCSV.getTimer().cancel();
        super.onDestroy();
    }



    public  boolean isAccessCoarseLocationGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted0");
                //Initilizing CSV File with stored preferences
                return true;
            } else {
                Log.v(TAG,"Permission is revoked0");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted0");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }
    void writtingCSV(){

        fileCSV.writeInFile("23\t34\t45\t5\n6\t78\t89\t99");
        fileCSV.writeInFile("23\t34\t45\t5\n6\t78\t89\t99");

    }

    private void updateIDsList(){
        //
        TreeSet<String> sortedSet =new TreeSet<String>(btMessageManager.getIDsListSet());
        String[] newStringList = sortedSet.toArray(new String[sortedSet.size()]);

        ArrayAdapter dataAdapter;

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,newStringList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerID.setAdapter(dataAdapter);
        spinnerID.setBackgroundColor(Color.DKGRAY);
    }
    private void resetRadioBtns(){
        radioB1.setChecked(false); radioB5.setChecked(false);
        radioB2.setChecked(false); radioB6.setChecked(false);
        radioB3.setChecked(false); radioB7.setChecked(false);
        radioB4.setChecked(false); radioB8.setChecked(false);
    }
    private void setRadioBtns(){
        radioB1.setChecked(true); radioB5.setChecked(true);
        radioB2.setChecked(true); radioB6.setChecked(true);
        radioB3.setChecked(true); radioB7.setChecked(true);
        radioB4.setChecked(true); radioB8.setChecked(true);
    }

    private void initPlot(){
        //======= declare XYPlot =========
        plot = (XYPlot) findViewById(R.id.plot);
        setRadioBtns();
        //Time in seconds to Shift every 10 seconds
        plot.setRangeStepMode(StepMode.INCREMENT_BY_VAL);
        plot.setRangeStepValue(10);
        //Time in seconds to Shift every 10 seconds
        plot.setDomainStepMode(StepMode.INCREMENT_BY_VAL);
        plot.setDomainStepValue(10);
        //Legend
        plot.getLegend().setTableModel(new DynamicTableModel(4, 2, TableOrder.ROW_MAJOR));

        //PanZoom.attach(plot, PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.STRETCH_HORIZONTAL);
        PanZoom.attach(plot);
    }
    private void updatePlot(){
        //Check if there is something list, if not, create the IDList
        if(spinnerID.getSelectedItem()==null)  updateIDsList();

        try {
            String selecIDStr = spinnerID.getSelectedItem().toString();
            //int selecIDInt = Integer.parseInt(selecIDStr);
            double selecIDDouble = Double.parseDouble(selecIDStr);

            LinkedList<Number> t1 = btMessageManager.MatIDs.get((int)selecIDDouble).getTime();

            LinkedList<Number> v1 = btMessageManager.MatIDs.get((int)selecIDDouble).getByte1();
            LinkedList<Number> v2 = btMessageManager.MatIDs.get((int)selecIDDouble).getByte2();
            LinkedList<Number> v3 = btMessageManager.MatIDs.get((int)selecIDDouble).getByte3();
            LinkedList<Number> v4 = btMessageManager.MatIDs.get((int)selecIDDouble).getByte4();
            LinkedList<Number> v5 = btMessageManager.MatIDs.get((int)selecIDDouble).getByte5();
            LinkedList<Number> v6 = btMessageManager.MatIDs.get((int)selecIDDouble).getByte6();
            LinkedList<Number> v7 = btMessageManager.MatIDs.get((int)selecIDDouble).getByte7();
            LinkedList<Number> v8 = btMessageManager.MatIDs.get((int)selecIDDouble).getByte8();

            /* //This create just once the plot and does not update
            LinkedList<Number> t1 = (LinkedList<Number>) btMessageManager.MatIDs.get(selecIDInt).getTime().clone();
            LinkedList<Number> v1 = (LinkedList<Number>) btMessageManager.MatIDs.get(selecIDInt).getByte1().clone();
            LinkedList<Number> v2 = (LinkedList<Number>) btMessageManager.MatIDs.get(selecIDInt).getByte2().clone();
            LinkedList<Number> v3 = (LinkedList<Number>) btMessageManager.MatIDs.get(selecIDInt).getByte3().clone();
            LinkedList<Number> v4 = (LinkedList<Number>) btMessageManager.MatIDs.get(selecIDInt).getByte4().clone();
            LinkedList<Number> v5 = (LinkedList<Number>) btMessageManager.MatIDs.get(selecIDInt).getByte5().clone();
            LinkedList<Number> v6 = (LinkedList<Number>) btMessageManager.MatIDs.get(selecIDInt).getByte6().clone();
            LinkedList<Number> v7 = (LinkedList<Number>) btMessageManager.MatIDs.get(selecIDInt).getByte7().clone();
            LinkedList<Number> v8 = (LinkedList<Number>) btMessageManager.MatIDs.get(selecIDInt).getByte8().clone();*/

            if (t1.size() > 1) {//there is something to plot

                Log.d("PLOT", "Plotting! ");

                if(!plotRun.isRunning()) {

                    //plotRun = new PlotRun(this, t1, v1, v2, plot);
                    plotRun = new PlotRun(t1, v1, v2, v3, v4, v5, v6, v7, v8, plot,this);
                    new Thread(plotRun).start();
                    Log.d("PLOT", "Plot is running! ");

                }
                else{ //It IS Runing therefore Stop and Launch again
                    plotRun.setStop(true);
                    //plotRun = new PlotRun(this, t1, v1, v2, plot);
                    plotRun = new PlotRun(t1, v1, v2, v3, v4, v5, v6, v7, v8, plot,this);
                    new Thread(plotRun).start();
                    Log.d("PLOT", "Plot Set to Stop! ");

                }
//                plotRun.setStop(true);


            } else {
                Log.d("PLOT", "Err404: There are not values in Matrix ");
            }
        }catch(NullPointerException ie){
            Log.d("PLOT", "Err405: Null pointer exception: " + ie);
        }

        //plotRunning=!plotRunning;

    }

    private void gpsOnOff(){
        if(mConnectedThread != null) //First check to make sure thread created
        {
            Log.d("BT_GPS","Ext GPS changed");
            if(gpsCheckbox.isChecked()) {
                gpsSwitch = "GPS on";
                Log.d("BT_GPS","GPS ON");
            }
            else{
                gpsSwitch = "GPS off";
                Log.d("BT_GPS","GPS OFF");
            }
            mConnectedThread.write(gpsSwitch);
        }
    }
    private void bluetoothOn(){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                mBluetoothStatus.setText("Enabled");
            }
            else
                mBluetoothStatus.setText("Disabled");
        }
    }

    private void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off
        mBluetoothStatus.setText("Bluetooth disabled");
        Toast.makeText(getApplicationContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    private void discover(View view){
        //Set visible/invisible the array list
        if(mDevicesListView.getVisibility() == View.GONE)
            mDevicesListView.setVisibility(View.VISIBLE);
        else
            mDevicesListView.setVisibility(View.GONE);


        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();

                // Register for broadcasts when a device is discovered.
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

                Log.d("BT", "Dev found f2");
//                popupDevices();
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }


        }
    }



    private final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d("BT", "Broadcast Receiver init");
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                Log.d("BT", "Dev found f1");
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();


            }
        }
    };

    private void listPairedDevices(View view){
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();

        popupDevices();

    }

    private void popupDevices(){
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(BluetoothActivity.this, mDiscoverBtn);

        for (BluetoothDevice device : mPairedDevices)
            popup.getMenu().add(device.getName() + "\n" + device.getAddress());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if(!mBTAdapter.isEnabled()) {
                    Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                    return false;
                }
                String info = item.getTitle().toString();
                final String address = info.substring(info.length() - 17);
                final String name = info.substring(0,info.length() - 17);

                spawnConnectThread(address, name);
                Toast.makeText(
                        BluetoothActivity.this,
                        "Connecting to: " + name,
                        Toast.LENGTH_SHORT

                ).show();
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @SuppressLint("SetTextI18n")
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
            spawnConnectThread(address, name);

            mDevicesListView.setVisibility(View.GONE);
        }
    };

    public void spawnConnectThread(final String address, final String name){
        new Thread()
        {
            public void run() {
                boolean fail = false;


                BluetoothDevice device = mBTAdapter.getRemoteDevice(address);


                Log.d("BluetoothW", "Spawn threading");

                try {
                    mBTSocket = createBluetoothSocket(device);

                    Log.d("BluetoothW", "bt socket created");
                } catch (IOException e) {
                    fail = true;
                    Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                }
                // Establish the Bluetooth socket connection.
                try {
                    Log.d("BluetoothW", "bt socket TRYING TO connect!");
                    mBTSocket.connect();
                    //mBTSocket.connect();
                    Log.d("BluetoothW", "bt socket connected!");
                } catch (IOException e) {
                    try {
                        fail = true;
                        mBTSocket.close();

                        Log.d("BluetoothW", "bt socket TRYed, now Closed");
                        mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                .sendToTarget();
                    } catch (IOException e2) {
                        //insert code to deal with this
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                }
                if(!fail) {
                    mConnectedThread = new ConnectedThread(mBTSocket);
                    mConnectedThread.start();

                    //Send mode messages
                    modeSend("BT", true);

                    try{
                        Thread.sleep(2000); //Wait to send the 2nd message
                    }catch(InterruptedException e){

                    }
                    //modeSend("SD", true);

                    mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                            .sendToTarget();
                }
            }
        }.start();
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer; // = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        buffer = new byte[51200]; //supossing a 300Hz freq of IDs //byte[1024];
                        SystemClock.sleep(1000); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        //    Call this from the main activity to send data to the remote device
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
                Thread.sleep(500);
                Log.d("BluetoothW", "Sending String as Byte");
            } catch (IOException | InterruptedException ie) { }
        }

        // Call this from the main activity to shutdown the connection
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


    //----------*-------------------*-------------------------------

    public void onBackPressed() {
        // Disable going back to the MainActivity
        //moveTaskToBack(true);

        //mCurrentBackgroundColor = selectedColor;
        mMessage = "No Device - Unknown";//mDevicesListView.getSelectedItem().toString();


        Intent returnIntent = new Intent();


        //returnIntent.putExtra(MainActivity.EXTRA_BT_MESSAGE, mMessage);
        //returnIntent.putExtra(MainActivity.EXTRA_COLOR, mCurrentBackgroundColor);
        //setResult(BluetoothActivityV1.RESULT_OK, returnIntent);

        //finish();
/*
        returnIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(returnIntent);*/
        Intent openBtAct= new Intent(BluetoothActivity.this,MainActivity.class);
        openBtAct.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openBtAct, 0);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }


    //----------*-------------------*-------------------------------

    //--------------------------------------------------------------
    //----------------------- Radio buttons func ---------------------------

    private void modeSend(String subMode, Boolean state){

        if(mConnectedThread != null) //First check to make sure thread created
        {
            String sendFrame = "";
            Log.d("COW_MODE", "changed");
            if(state) {
                sendFrame = subMode+" on";
                Log.d("COW_MODE",subMode + " ON");
            }
            else{
                sendFrame = subMode+" off";
                Log.d("COW_MODE",subMode + " Off");
            }
            mConnectedThread.write(sendFrame);

        }



    }

    //If there is not connection Do not change the status of the Radio button
    void isRadioConnected(View view, boolean checked){
        if(mConnectedThread==null){
            ((CheckBox) view).setChecked(!checked);
            Toast.makeText(getApplicationContext(), "Device not connected", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((CheckBox) view).isChecked();


        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.modeBtRadio:
                if(checked)
                    modeSend("BT",true);
                else
                    modeSend("BT",false);
                //If there is not connection Do not change the status of the Radio button
                isRadioConnected(view, checked);

                break;
            case R.id.modeSDRadio:
                if(checked)
                    modeSend("SD",true);
                else
                    modeSend("SD",false);
                isRadioConnected(view, checked);

                break;
            case R.id.modeLabviewRadio:
                if(checked)
                    modeSend("LV",true);
                else
                    modeSend("LV",false);
                isRadioConnected(view, checked);

                break;
            case R.id.modeOBDRadio:
                if(checked)
                    modeSend("OBD",true);
                else
                    modeSend("OBD",false);
                isRadioConnected(view, checked);

                break;



            //-------------
            case R.id.radioTime:
                if (checked){
/*
                    Number timePeriod = btMessageManager.MatIDs.get(0).getTimePeriod();
                    Log.d("TestConsole", "Time Period ID 0: " + timePeriod);

                    LinkedList<Number> timeList = btMessageManager.MatIDs.get(0).getTime();
                    Log.d("TestConsole", "Time Linked List ID 0 : " + timeList);
*/
                    int lostIDs = btMessageManager.getLostIDs();
                    Log.d("TestConsole", "Lost IDs: " + lostIDs);

                }
                // Pirates are the best
                break;
            case R.id.radioLen:
                if (checked)
                    // Ninjas rule
                    break;
            case R.id.radioB1:
                if (checked) plotRun.enablePlotByte(1);
                else {
                    plotRun.disablePlotByte(1);
                    plotRun.updatePlot();
                }
                    // Pirates are the best
                    break;
            case R.id.radioB2:
                if (checked) plotRun.enablePlotByte(2);
                else         plotRun.disablePlotByte(2);
                // Pirates are the best
                    // Ninjas rule
                    break;
            case R.id.radioB3:
                if (checked) plotRun.enablePlotByte(3);
                else         plotRun.disablePlotByte(3);
                    break;
            case R.id.radioB4:
                if (checked) plotRun.enablePlotByte(4);
                else         plotRun.disablePlotByte(4);
                    break;
            case R.id.radioB5:
                if (checked) plotRun.enablePlotByte(5);
                else         plotRun.disablePlotByte(5);
                    break;
            case R.id.radioB6:
                if (checked) plotRun.enablePlotByte(6);
                else         plotRun.disablePlotByte(6);
                    break;
            case R.id.radioB7:
                if (checked) plotRun.enablePlotByte(7);
                else         plotRun.disablePlotByte(7);
                    break;
            case R.id.radioB8:
                if (checked) plotRun.enablePlotByte(8);
                else         plotRun.disablePlotByte(8);
                    break;
        }
    }




    //----------*-------------------*-------------------------------
    //----------*-------------------*-------------------------------
    //----------*-------------------*-------------------------------

    class DynamicXYDatasource implements Runnable {

        // encapsulates management of the observers watching this datasource for update events:
        class MyObservable extends Observable {
            @Override
            public void notifyObservers() {
                setChanged();
                super.notifyObservers();
            }
        }


        private static final int SAMPLE_SIZE = 31;

        private MyObservable notifier;
        private boolean keepRunning = false;

        {
            notifier = new MyObservable();
        }

        public void stopThread() {
            keepRunning = false;
        }

        //@Override
        public void run() {
            try {
                keepRunning = true;
                boolean isRising = true;
                while (keepRunning) {

                    Thread.sleep(10); // decrease or remove to speed up the refresh rate.

                    notifier.notifyObservers();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public int getItemCount(int series) {
            return SAMPLE_SIZE;
        }

        public Number getX(int series, int index) {
            if (index >= SAMPLE_SIZE) {
                throw new IllegalArgumentException();
            }
            return index;
        }

        public Number getY(int series, int index) {
            if (index >= SAMPLE_SIZE) {
                throw new IllegalArgumentException();
            }
            /*double angle = (index + (phase))/FREQUENCY;
            double amp = sinAmp * Math.sin(angle);
            switch (series) {
                case SINE1:
                    return amp;
                case SINE2:
                    return -amp;
                default:
                    throw new IllegalArgumentException();
            }*/
            return 1; //
        }

        public void addObserver(Observer observer) {
            notifier.addObserver(observer);
        }

        public void removeObserver(Observer observer) {
            notifier.deleteObserver(observer);
        }

    }

    class DynamicSeries implements XYSeries {
        private DynamicXYDatasource datasource;
        private int seriesIndex;
        private String title;

        public DynamicSeries(DynamicXYDatasource datasource, int seriesIndex, String title) {
            this.datasource = datasource;
            this.seriesIndex = seriesIndex;
            this.title = title;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public int size() {
            return datasource.getItemCount(seriesIndex);
        }

        @Override
        public Number getX(int index) {
            return datasource.getX(seriesIndex, index);
        }

        @Override
        public Number getY(int index) {
            return datasource.getY(seriesIndex, index);
        }
    }

}



