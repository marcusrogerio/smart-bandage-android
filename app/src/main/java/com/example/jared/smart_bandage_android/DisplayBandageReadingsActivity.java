package com.example.jared.smart_bandage_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class DisplayBandageReadingsActivity extends AppCompatActivity {
    private static final String TAG = DisplayBandageReadingsActivity.class.getSimpleName();
    //probably not the best way to do this
    Context context;
    SendData sendData;
    String tempData;
    String humidityData;
    String moistureData;

    //EditText bandageID = (EditText) findViewById(R.id.bandageID);
    String bandageID = "1234";
    public static String DEVICE_LIST ="deviceList";
    public static HashMap<String,SmartBandage> deviceList;

    public final static String BANDAGE = "BANDAGE";

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static String BANDAGE_TEMP_AVAILABLE =
            "com.example.bluetooth.le.BANDAGE_TEMP_AVAILABLE";
    public static final String BANDAGE_HUMIDITY_AVAILABLE = "com.example.smart_bandage_android.BANDAGE_HUMIDITY_AVAILABLE";
    public static final String MOISTURE_DATA_AVAILABLE = "com.example.smart_bandage_android.MOISTURE_DATA_AVAILABLE";

    public DisplayBandageReadingsActivity(String humidityData, String moistureData, String bandageID) {
        this.humidityData = humidityData;
        this.moistureData = moistureData;
        this.bandageID = bandageID;
    }

    public DisplayBandageReadingsActivity(){};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_bandage_readings);
        context = this;
        //android.os.Debug.waitForDebugger();
        deviceList = (HashMap<String,SmartBandage>) getIntent().getSerializableExtra(DEVICE_LIST);

        updateActivity();

        sendData = new SendData();
        sendData.insert();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sb_menu, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }


    // Couldn't get this working in the onClick
    // TODO look into it
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_connection:
                viewNewConnection();
                return true;
            case R.id.advanced_view:
                viewAdvancedView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_CONNECTED.equals(action)) {

            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {

            } else if (ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothGatt.getServices());

            } else if (ACTION_DATA_AVAILABLE.equals(action)) {
               // Toast.makeText(DisplayBandageReadingsActivity.this, intent.getStringExtra(EXTRA_DATA), Toast.LENGTH_SHORT).show();
            }

            if (BANDAGE_TEMP_AVAILABLE.equals(action)) {
                Toast.makeText(DisplayBandageReadingsActivity.this, intent.getStringExtra(EXTRA_DATA), Toast.LENGTH_SHORT).show();
                setTempData(intent.getStringExtra(EXTRA_DATA));
                Log.i(TAG, "Caught the data: " + intent.getStringExtra(EXTRA_DATA).toString());
            }

            if (BANDAGE_HUMIDITY_AVAILABLE.equals(action)) {
                Toast.makeText(DisplayBandageReadingsActivity.this, intent.getStringExtra(EXTRA_DATA), Toast.LENGTH_SHORT).show();
                setHumidityData(intent.getStringExtra(EXTRA_DATA));
            }

            if (MOISTURE_DATA_AVAILABLE.equals(action)) {
                Toast.makeText(DisplayBandageReadingsActivity.this, intent.getStringExtra(EXTRA_DATA), Toast.LENGTH_SHORT).show();
                setMoistureData(intent.getStringExtra(EXTRA_DATA));
            }
            updateActivity();

        }
    };


    public void updateActivity(){
        // 1. pass context and data to the custom adapter
        BandageReadingAdapter adapter = new BandageReadingAdapter(this, generateData());

        // if extending Activity 2. Get ListView from activity_main.xml
        ListView listView = (ListView) findViewById(R.id.listView);

        // 3. setListAdapter
        listView.setAdapter(adapter);// if extending Activity
        // setListAdapter(adapter);
    }


    private ArrayList<DisplayModel> generateData(){
        ArrayList<DisplayModel> models = new ArrayList<DisplayModel>();
        models.add(new DisplayModel(R.drawable.thermometer,"Temperature: ", getTempData()));
        models.add(new DisplayModel(R.drawable.cloud,"Humidity: ", getHumidityData()));
        models.add(new DisplayModel(R.drawable.raindrop, "Moisture: ", getMoistureData()));

        return models;
    }

    public void viewNewConnection() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }


    public void viewAdvancedView(){
        Log.w("why", "about to go to DeviceServiceViewActivity");
        Intent intent = new Intent(this, ConnectedDevicesAdvancedActivity.class);
        intent.putExtra(ConnectedDevicesAdvancedActivity.DEVICE_LIST, deviceList);
       // Intent intent = new Intent(this, DeviceServiceViewActivity.class);
        startActivity(intent);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
       // intentFilter.addAction(ACTION_GATT_CONNECTED);
       // intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        //intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BANDAGE_HUMIDITY_AVAILABLE);
        intentFilter.addAction(BANDAGE_TEMP_AVAILABLE);
        intentFilter.addAction(MOISTURE_DATA_AVAILABLE);

        return intentFilter;
    }


    public String getTempData() {
        return tempData;
    }

    public void setTempData(String tempData) {
        this.tempData = tempData;
    }

    public String getHumidityData() {
        return humidityData;
    }

    public void setHumidityData(String humidityData) {
        this.humidityData = humidityData;
    }

    public String getMoistureData() {
        return moistureData;
    }

    public void setMoistureData(String moistureData) {
        this.moistureData = moistureData;
    }
}

