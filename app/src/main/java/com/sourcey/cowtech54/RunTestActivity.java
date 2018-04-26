package com.sourcey.cowtech54;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Wily on 04/04/2018.
 */


public class RunTestActivity extends AppCompatActivity {

    protected ProgressDialog mProgressDialog;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_test);
        populateTable();
    }

    private void populateTable() {
        mProgressDialog = ProgressDialog.show(this, "Please wait","Long operation starts...", true);
        new Thread() {
            @Override
            public void run() {

                doLongOperation();
                try {

                    // code runs in a thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                        }
                    });
                } catch (final Exception ex) {
                    Log.i("---","Exception in thread");
                }
            }
        }.start();

    }

    /** fake operation for testing purpose */
    protected void doLongOperation() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }

    }
}