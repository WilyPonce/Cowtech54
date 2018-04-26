package com.sourcey.cowtech54;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity { //ActionBarActivity
    private static final String TAG = "MainActivity";

    private TextView mBluetoothDevice;

    private static final int REQUEST_BT = 1;
    private static final int REQUEST_DYNAMO = 2;
    private static final int REQUEST_GYRO = 3;

    public static final String EXTRA_BT_MESSAGE = "EXTRA_BT_MESSAGE";

    public static final String EXTRA_DYNAMO_MESSAGE = "EXTRA_DYNAMO_MESSAGE";
    public static final String EXTRA_GYRO_MESSAGE = "EXTRA_GYRO_MESSAGE";

    public static final String EXTRA_COLOR = "EXTRA_COLOR";
    public String mMessage = "unknown";

    @Bind(R.id.bt_mainButton) Button _btButton; //Needs ButterKnife.bind to work
    @Bind(R.id.dynamo_mainButton) Button _dynamoButton; //Needs ButterKnife.bind to work
    private Button _sendBtn;
    private Button _updateBtn;
    private Button _createBtn;
    private Button _deleteBtn;
    private Button _connectedBtn;
    private Button _gyroBtn;
    private Button _simplePlotBtn;
    private Button _dynamicPlotBtn;
    private Button _runTestBtn;

    //Checker if btactivity has started once
    private boolean btActStarted = false;

    // Declare a DynamoDBMapper object
    public static AmazonClientManager clientManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = new Intent(this, LoginActivity.class);  //  << ========== ----
        startActivity(intent);

        ButterKnife.bind(this); // In order to bind the objects and buttons views!

        clientManager = new AmazonClientManager(this);

        checkPermissions();

        mBluetoothDevice = (TextView)findViewById(R.id.btText);

        _btButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the BlueToot activity
                /*if(!btActStarted) {
                    btActStarted = true;
                    Intent btIntent = new Intent(getApplicationContext(), BluetoothActivity.class);
                    btIntent.putExtra(EXTRA_BT_MESSAGE, mMessage);

                    startActivityForResult(btIntent, REQUEST_BT);
                    //startActivityForResult (Intent intent, int requestCode)
                    //requestCode	int: If >= 0, this code will be returned in onActivityResult() when the activity exits.
                    //finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else //onBackPressed();
                {*/
                    Intent openBtAct= new Intent(MainActivity.this, BluetoothActivity.class);
                    openBtAct.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityIfNeeded(openBtAct, 0);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                //}
            }
        });


        _dynamoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Dynamo activity
                Intent Intent = new Intent(getApplicationContext(), DynamoDBActivity.class);
                Intent.putExtra(EXTRA_DYNAMO_MESSAGE, mMessage);

                startActivityForResult(Intent, REQUEST_DYNAMO);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _connectedBtn = (Button) findViewById(R.id.connectedBtn);
        _gyroBtn = (Button) findViewById(R.id.gyroBtn);
        _simplePlotBtn = (Button) findViewById(R.id.simplePlotBtn);
        _dynamicPlotBtn = (Button) findViewById(R.id.dynamicPlotBtn);
        _runTestBtn = (Button) findViewById(R.id.runTestBtn);

        _connectedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Connected Tabbed Activity
                Intent Intent = new Intent(getApplicationContext(), ConnectedActivity.class);
                startActivity(Intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        //---- GYROSCOPE BUTTN
        _gyroBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Start the Dynamo activity
                Intent Intent = new Intent(getApplicationContext(), OrientationSensorExampleActivity.class);
                Intent.putExtra(EXTRA_GYRO_MESSAGE, mMessage);

                startActivityForResult(Intent, REQUEST_GYRO);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _simplePlotBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Start the Dynamo activity
                Intent Intent = new Intent(getApplicationContext(), SimpleXYPlotActivity.class);

                startActivity(Intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _dynamicPlotBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Start the Dynamo activity
                Intent Intent = new Intent(getApplicationContext(), DynamicXYPlotActivity.class);

                startActivity(Intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        _runTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(getApplicationContext(), RunTestActivity.class);

                startActivity(Intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


        /* ------ Dynamo buttons  --------- */
        _sendBtn = (Button)findViewById(R.id.dynamoSendBtn);
        _updateBtn = (Button)findViewById(R.id.dynamoUpdateBtn);
        _createBtn = (Button)findViewById(R.id.dynamoCreateBtn);
        _deleteBtn = (Button)findViewById(R.id.dynamoDeleteBtn);

        /*_sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNews();
                // /bluetoothOn(v);
            }
        });

        _createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTable();
                // /bluetoothOn(v);
            }
        });

        _updateBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new GetCanListTask().execute();
                //bluetoothOff(v);
            }
        });*/

        //Dynamo sample
        _createBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i(TAG, "createTableBttn clicked.");

                new DynamoDBManagerTask()
                        .execute(DynamoDBManagerType.CREATE_TABLE);
            }
        });

        _sendBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i(TAG, "insertUsersBttn clicked.");

                new DynamoDBManagerTask()
                        .execute(DynamoDBManagerType.INSERT_CANID);
            }
        });

        _updateBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i(TAG, "listUsersBttn clicked.");

                new DynamoDBManagerTask()
                        .execute(DynamoDBManagerType.LIST_USERS);
            }
        });

        _deleteBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i(TAG, "deleteTableBttn clicked.");

                new DynamoDBManagerTask().execute(DynamoDBManagerType.CLEAN_UP);
            }
        });

    }


    //===========================================================================
    //============================= PERMISSIONS  ====================================
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    String[] permissionsList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};


    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissionsList) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissionsList[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permissions granted.
                } else {
                    String permissions = "";
                    for (String per : permissionsList) {
                        permissions += "\n" + per;
                    }
                    // permissions list of don't granted permission
                }
                return;
            }
        }
    }


    //===========================================================================
    //============================= DYNAMO   ====================================
    public void createTable(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //dynamoDBMapper.save(newsItem);

               DynamoDBManager.createTable();


                /*if (result.toString().length() != 0){
                    Toast.makeText(
                            MainActivity.this,
                            "Table status: "
                                    + result.toString(),
                            Toast.LENGTH_LONG).show();
                }*/
                // Item saved
            }
        }).start();
    }

    public Truck40DO createNewItem() {
        final Truck40DO newsItem = new Truck40DO();

        newsItem.setTruckID("AVZ01");

        newsItem.setCanID(55.0);

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        newsItem.setTimeStamp(Double.parseDouble(ts));
        newsItem.setByte1(1.0);
        newsItem.setByte2(2.0);
        newsItem.setByte3(3.0);
        newsItem.setByte4(4.0);
        newsItem.setByte5(5.0);
        newsItem.setByte6(6.0);
        newsItem.setByte7(7.0);
        newsItem.setByte8(88.0);

        return newsItem;
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                //dynamoDBMapper.save(newsItem);
                DynamoDBManager.insertItem(newsItem);
                // Item saved
            }
        }).start();*/
    }

    //------------=---------------=----------------=-

    private class DynamoDBManagerTask extends
            AsyncTask<DynamoDBManagerType, Void, DynamoDBManagerTaskResult> {

        protected DynamoDBManagerTaskResult doInBackground(
                DynamoDBManagerType... types) {

            String tableStatus = DynamoDBManager.getTestTableStatus();

            DynamoDBManagerTaskResult result = new DynamoDBManagerTaskResult();
            result.setTableStatus(tableStatus);
            result.setTaskType(types[0]);

            if (types[0] == DynamoDBManagerType.CREATE_TABLE) {
                if (tableStatus.length() == 0) {
                    DynamoDBManager.createTable();
                }
            } else if (types[0] == DynamoDBManagerType.INSERT_USER) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    DynamoDBManager.insertUsers();
                }
            } else if (types[0] == DynamoDBManagerType.LIST_USERS) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    DynamoDBManager.getUserList();
                }
            } else if (types[0] == DynamoDBManagerType.CLEAN_UP) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    DynamoDBManager.cleanUp();
                }
            } else if (types[0] == DynamoDBManagerType.INSERT_CANID) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    DynamoDBManager.insertItem(createNewItem());
                }
            }


            return result;
        }

        protected void onPostExecute(DynamoDBManagerTaskResult result) {

            if (result.getTaskType() == DynamoDBManagerType.CREATE_TABLE) {
                if (result.getTableStatus().length() == 0) {
                    Toast.makeText(
                            MainActivity.this,
                            "The table was created!.\nTable Status: "
                                    + result.getTableStatus(),
                            Toast.LENGTH_LONG).show();
                }

                if (result.getTableStatus().length() != 0) {
                    Toast.makeText(
                            MainActivity.this,
                            "The test table already exists.\nTable Status: "
                                    + result.getTableStatus(),
                            Toast.LENGTH_LONG).show();
                }

            } else if (result.getTaskType() == DynamoDBManagerType.LIST_USERS
                    && result.getTableStatus().equalsIgnoreCase("ACTIVE")) {

              /*  startActivity(new Intent(MainActivity.this,
                        UserListActivity.class));*/

            } else if (!result.getTableStatus().equalsIgnoreCase("ACTIVE")) {

                Toast.makeText(
                        MainActivity.this,
                        "The test table is not ready yet.\nTable Status: "
                                + result.getTableStatus(), Toast.LENGTH_LONG)
                        .show();
            } else if (result.getTableStatus().equalsIgnoreCase("ACTIVE")
                    && result.getTaskType() == DynamoDBManagerType.INSERT_USER) {
                Toast.makeText(MainActivity.this,
                        "Users inserted successfully!", Toast.LENGTH_SHORT).show();
            } else if (result.getTableStatus().equalsIgnoreCase("ACTIVE")
                    && result.getTaskType() == DynamoDBManagerType.INSERT_CANID) {
                Toast.makeText(MainActivity.this,
                        "CanID Tram inserted successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private enum DynamoDBManagerType {
        GET_TABLE_STATUS, CREATE_TABLE, INSERT_USER, LIST_USERS, CLEAN_UP, INSERT_CANID
    }

    private class DynamoDBManagerTaskResult {
        private DynamoDBManagerType taskType;
        private String tableStatus;

        public DynamoDBManagerType getTaskType() {
            return taskType;
        }

        public void setTaskType(DynamoDBManagerType taskType) {
            this.taskType = taskType;
        }

        public String getTableStatus() {
            return tableStatus;
        }

        public void setTableStatus(String tableStatus) {
            this.tableStatus = tableStatus;
        }
    }

    //===========================================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_BT ){
            Log.d("BT", "REQUEST CODE = REQUEST_BT");
            if (resultCode == BluetoothActivity.RESULT_OK){
                Log.d("BT", "Result ok!");
                //mMessage = data.getStringExtra(EXTRA_MESSAGE);
                //mBackgroundColor = data.getIntExtra(EXTRA_COLOR, Color.GRAY);
                //updateUI();
                mMessage = data.getStringExtra(EXTRA_BT_MESSAGE);
                mBluetoothDevice.setText(mMessage);

            }

        }
    }
}
