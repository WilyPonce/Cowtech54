package com.sourcey.cowtech54;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ServiceActivity extends AppCompatActivity {

    private Button sStartBtn;
    private Button sStopBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        sStartBtn = (Button) findViewById(R.id.serviceStartBtn);
        sStopBtn = (Button) findViewById(R.id.serviceStopBtn);

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
    }
}
