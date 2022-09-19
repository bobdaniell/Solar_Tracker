package com.danibob.solartracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;



public class PairedDevices extends AppCompatActivity {

    private static final String TAG = "PairedDevices";

    ListView IdList;



    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothAdapter mBtAdapter;

    private ArrayAdapter mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_paired_devices);
    }

    @Override
    public void onResume() {

        super.onResume();

        CheckStatusBT();



        mPairedDevicesArrayAdapter = new ArrayAdapter(this, R.layout.devices_found);

        IdList = (ListView) findViewById(R.id.IdList);

        IdList.setAdapter(mPairedDevicesArrayAdapter);

        IdList.setOnItemClickListener(mDeviceClickListener);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.BLUETOOTH_CONNECT },
                    1);
        }
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {

            findViewById(R.id.IdList).setVisibility(View.VISIBLE);

            for (BluetoothDevice device : pairedDevices) {

                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            }
        } else{
                mPairedDevicesArrayAdapter.add("No devices paired");
            }

    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {

            String info = ((TextView) v).getText().toString();

            String address = info.substring(info.length() - 17);

            finishAffinity();

            Intent intend = new Intent(PairedDevices.this, MainActivity.class);

            intend.putExtra(EXTRA_DEVICE_ADDRESS, address);

            startActivity(intend);

        }
    };

    private void CheckStatusBT() {

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBtAdapter == null) {

            Toast.makeText(getBaseContext(), "The device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {

            if (!mBtAdapter.isEnabled()) {


                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.BLUETOOTH_CONNECT },
                            1);
                }
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


}