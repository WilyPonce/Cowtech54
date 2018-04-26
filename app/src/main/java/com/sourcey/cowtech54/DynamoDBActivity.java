package com.sourcey.cowtech54;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
//import com.amazonaws.services.dynamodbv2.RangeKe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wily on 12/03/2018.
 */




public class DynamoDBActivity extends ListActivity {
    //GUI Components
    private Button _sendBtn;
    private Button _updateBtn;


    private ArrayList<Truck40DO> items = null;
    private ArrayList<String> labels = null;
    private int currentPosition = 0;
    private ArrayAdapter<String> arrayAdapter = null;

    // Declare a DynamoDBMapper object
    DynamoDBMapper dynamoDBMapper;
    private static final String TAG = "Truck40DODemoActivity";
    public static AmazonClientManager clientManager = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clientManager = new AmazonClientManager(this);

        new GetCanListTask().execute();



       /* // Instantiate a AmazonDynamoDBMapperClient
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();*/


       /* setContentView(R.layout.activity_dynamo);
        _sendBtn = (Button)findViewById(R.id.dynamoSendBtn);
        _updateBtn = (Button)findViewById(R.id.dynamoUpdateBtn);
        _sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNews();
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
    }

    public void createNews() {
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


        new Thread(new Runnable() {
            @Override
            public void run() {
                //dynamoDBMapper.save(newsItem);
                DynamoDBManager.insertItem(newsItem);
                // Item saved
            }
        }).start();
    }

    public void readNews() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Truck40DO newsItem = dynamoDBMapper.load(
                        Truck40DO.class,
                        55.0,
                        "Article1"
                        );

                // Item read
                // Log.d("News Item:", newsItem.toString());
            }
        }).start();
    }
            //https://stackoverflow.com/questions/31850030/dynamodbmapper-load-vs-query
    public Truck40DO getEntryForDay(final Double hash, Double timeStamp) {
        final Truck40DO hashKeyValues = new Truck40DO ();
        hashKeyValues.setCanID(hash);
        final Condition rangeKeyCondition = new Condition()//
                .withComparisonOperator(ComparisonOperator.GT.toString())//
                .withAttributeValueList(new AttributeValue().withS(Double.toString(timeStamp)));//new AttributeValue().withS(new LocalDateMarshaller().marshall(timeStamp)));

        final DynamoDBQueryExpression<Truck40DO> queryExpression = new DynamoDBQueryExpression<Truck40DO>()//
                .withHashKeyValues(hashKeyValues)//
                .withRangeKeyCondition("timeStamp", rangeKeyCondition)//
                .withLimit(1);
        final List<Truck40DO> storedEntries = dynamoDBMapper
                .query(Truck40DO.class, queryExpression);
        if (storedEntries.isEmpty()) {
            return null;
        }
        return storedEntries.get(0);
    }

    //TODO: Declare the buttons bindings and create the Truck execution see if it works like that


    /**
     * This methods are adapted from the DynamoDBMapper_UserPrreference_Cognito
     *
     */
    protected boolean onLongListItemClick(View v, int position, long id) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        new DeleteCanTramTask().execute();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete this user?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

        currentPosition = position;

        return true;
    }

    private class GetCanListTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... inputs) {

            labels = new ArrayList<String>();

            items = DynamoDBManager.getLastIDs();

            for (Truck40DO up : items) {
                labels.add(up.getTimeStamp() + "    " + up.getCanID() + "    " + up.getByte1() + "  ...  " + up.getByte8());
            }

            return null;
        }

        protected void onPostExecute(Void result) {

            /*context
            Context: The current context.
            resource int: The resource ID for a layout file containing a TextView to use when instantiating views.
            List: The objects to represent in the ListView.*/
            arrayAdapter = new ArrayAdapter<String>(DynamoDBActivity.this,
                    R.layout.user_list_item, labels); // <--------
            setListAdapter(arrayAdapter); // ----> NEEDS! Extends ListActivity in the class!

            ListView lv = getListView();
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> av, View v,
                                               int pos, long id) {
                    return onLongListItemClick(v, pos, id);
                }
            });

            Toast toast = Toast.makeText(DynamoDBActivity.this,
                    "Tap and hold to delete users", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private class DeleteCanTramTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... types) {

            DynamoDBManager.deleteUser(items.get(currentPosition));
            items.remove(currentPosition);
            labels.remove(currentPosition);

            return null;
        }

        protected void onPostExecute(Void result) {

            arrayAdapter.notifyDataSetChanged();

        }
    }


    public void onBackPressed() {
        // Disable going back to the MainActivity
        //moveTaskToBack(true);

        //mCurrentBackgroundColor = selectedColor;
        String mMessage= "No Device - Unknown";//mDevicesListView.getSelectedItem().toString();


        Intent returnIntent = new Intent();
        returnIntent.putExtra(MainActivity.EXTRA_DYNAMO_MESSAGE, mMessage);
        setResult(DynamoDBActivity.RESULT_OK, returnIntent);


        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }



}