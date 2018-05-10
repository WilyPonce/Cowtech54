package com.sourcey.cowtech54;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ServiceActivity extends AppCompatActivity {

    private final String TAG = ServiceActivity.class.getSimpleName();

    CowService cowService;
    CowService.CowBinder cowBinder;
 //   IBinder cowBinder;
    boolean sBound = false;

    private Button sStartBtn;
    private Button sStopBtn;
    private Button sBindBtn;
    private Button sUnbindBtn;
    private Button sUpdateBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        sStartBtn = (Button) findViewById(R.id.serviceStartBtn);
        sStopBtn = (Button) findViewById(R.id.serviceStopBtn);
        sBindBtn = (Button) findViewById(R.id.serviceBindBtn);
        sUnbindBtn = (Button) findViewById(R.id.serviceUnbindBtn);

        sUpdateBtn = (Button) findViewById(R.id.serviceUpdateBtn);

        sStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceActivity.this,CowService.class);
                startService(intent);
            }
        });
        sStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceActivity.this,CowService.class);
                stopService(intent);
            }
        });
        sBindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceActivity.this, CowService.class);
                bindService(intent,sServerConn, Context.BIND_AUTO_CREATE);
            }
        });
        sUnbindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceActivity.this, CowService.class);
                if(sBound) {
                    unbindService(sServerConn);
                    sBound=false;
                }
            }
        });
        sUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sBound){
                    String filePathStr;
                    filePathStr = cowService.getFileCSV().getFilePathStr();
                    Toast.makeText(getApplicationContext(), filePathStr, Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    protected ServiceConnection sServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CowService.CowBinder binder = (CowService.CowBinder) service;
            cowService = binder.getService();
            sBound = true;
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sBound = false;
            Log.d(TAG, "onServiceDisconnected");
        }
    };


}
