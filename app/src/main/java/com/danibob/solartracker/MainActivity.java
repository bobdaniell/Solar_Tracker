package com.danibob.solartracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    Button  btnUp, btnLeft, btnDown, btnRight, btnDisconnect;

    TextView showTemperature, showHumidity, showVoltage, showCurrent, showPower;

    SwitchCompat switchCompat;

    Handler bluetoothIn;

    private Handler repeatUpdateHandler = new Handler();

    private boolean mAutoIncrement = false;

    final int handlerState = 0;

    private BluetoothAdapter btAdapter = null;

    private BluetoothSocket btSocket = null;

    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread MyConnectionBT;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String address;

    private long pressedTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);

        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);

        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        this.registerReceiver(broadcastReceiver, filter);


        showTemperature = (TextView) findViewById(R.id.showTemperature);

        showHumidity = (TextView) findViewById(R.id.showHumidity);

        showVoltage = (TextView) findViewById(R.id.showVoltage);

        showCurrent = (TextView) findViewById(R.id.showCurrent);

        showPower = (TextView) findViewById(R.id.showPower);


        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {

                if (msg.what == handlerState) {

                    String readMessage = (String) msg.obj;

                    recDataString.append(readMessage);

                    int endOfLineIndex = recDataString.indexOf("~");

                    if (endOfLineIndex > 0) {

                        String dataInPrint = recDataString.substring(0, endOfLineIndex);


                        if (recDataString.charAt(0) == '#') {

                            String Temperature = recDataString.substring(1, 6);

                            String Humidity = recDataString.substring(7, 12);

                            String Voltage = recDataString.substring(13, 18);

                            String Current = recDataString.substring(19, 24);

                            String Power = recDataString.substring(25, 30);

                            showTemperature.setText(Temperature + " ℃");

                            showHumidity.setText(Humidity + " %");

                            showVoltage.setText(Voltage + " V");

                            showCurrent.setText(Current + " A");

                            showPower.setText(Power + " W");
                        }
                        recDataString.delete(0, recDataString.length());

                        dataInPrint = " ";
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        CheckStatusBT();

        btnUp = findViewById(R.id.btnUp);

        btnLeft = findViewById(R.id.btnLeft);

        btnDown = findViewById(R.id.bntDown);

        btnRight = findViewById(R.id.bntRight);

        btnDisconnect = findViewById(R.id.btnDisconnect);

        switchCompat = findViewById(R.id.switchButton);


        btnUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MyConnectionBT.write("1"); //"1" stands for action Up

            }
        });

        btnUp.setOnTouchListener(new View.OnTouchListener() {

            private Handler mHandler;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:

                        if (mHandler != null) return true;

                        mHandler = new Handler();

                        mHandler.postDelayed(mAction, 100);

                        break;

                    case MotionEvent.ACTION_UP:

                        if (mHandler == null) return true;

                        mHandler.removeCallbacks(mAction);

                        mHandler = null;

                        break;
                }
                return false;
            }
            Runnable mAction = new Runnable() {
                @Override
                public void run() {

                    MyConnectionBT.write("2"); // 2 for continous run

                    mHandler.postDelayed(this, 100);
                }
            };
        });


        btnLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MyConnectionBT.write("3"); // "3" stands for Left

            }
        });

        btnLeft.setOnTouchListener(new View.OnTouchListener() {

            private Handler mHandler;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:

                        if (mHandler != null) return true;

                        mHandler = new Handler();

                        mHandler.postDelayed(mAction, 100);

                        break;

                    case MotionEvent.ACTION_UP:

                        if (mHandler == null) return true;

                        mHandler.removeCallbacks(mAction);

                        mHandler = null;

                        break;
                }
                return false;
            }
            Runnable mAction = new Runnable() {
                @Override
                public void run() {

                    MyConnectionBT.write("4");

                    mHandler.postDelayed(this, 100);
                }
            };
        });

        btnRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MyConnectionBT.write("5"); // "5" stands for Right

            }
        });

        btnRight.setOnTouchListener(new View.OnTouchListener() {

            private Handler mHandler;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:

                        if (mHandler != null) return true;

                        mHandler = new Handler();

                        mHandler.postDelayed(mAction, 100);

                        break;

                    case MotionEvent.ACTION_UP:

                        if (mHandler == null) return true;

                        mHandler.removeCallbacks(mAction);

                        mHandler = null;

                        break;
                }
                return false;
            }
            Runnable mAction = new Runnable() {
                @Override
                public void run() {

                    MyConnectionBT.write("6");

                    mHandler.postDelayed(this, 100);
                }
            };
        });



        btnDown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MyConnectionBT.write("7");  //"D" for down

            }
        });

        btnDown.setOnTouchListener(new View.OnTouchListener() {

            private Handler mHandler;
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:

                        if (mHandler != null) return true;

                        mHandler = new Handler();

                        mHandler.postDelayed(mAction, 100);

                        break;

                    case MotionEvent.ACTION_UP:

                        if (mHandler == null) return true;

                        mHandler.removeCallbacks(mAction);

                        mHandler = null;

                        break;
                }
                return false;
            }
            Runnable mAction = new Runnable() {
                @Override
                public void run() {

                    MyConnectionBT.write("8");

                    mHandler.postDelayed(this, 100);
                }
            };
        });




        btnDisconnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (btSocket != null) {

                    try {

                        btSocket.close();

                    } catch (IOException e) {

                        Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            }
        });


        switchCompat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (switchCompat.isChecked()) {

                    MyConnectionBT.write("9"); // "0" stands for Manual Mode

                } else {

                    MyConnectionBT.write("0"); // "9" stands for Auto Mode
                }
            }
        });
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        BluetoothDevice device;

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {

                Toast.makeText(getApplicationContext(), "Solar Tracker is now connected", Toast.LENGTH_SHORT).show();

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                Toast.makeText(getApplicationContext(), "Solar Tracker is disconnected", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    1);
        }
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume() {

        super.onResume();

        Intent intent = getIntent();

        address = intent.getStringExtra(PairedDevices.EXTRA_DEVICE_ADDRESS);

        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);

        } catch (IOException e) {

            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.BLUETOOTH_CONNECT },
                        1);
            }
            btSocket.connect();


        } catch (IOException e) {

            try {
                btSocket.close();

            } catch (IOException e2) {
            }
        }
        MyConnectionBT = new ConnectedThread(btSocket);

        MyConnectionBT.start();

    }

    @Override
    public void onPause() {

        super.onPause();

        try {

            btSocket.close();

        } catch (IOException e2) {

        }
    }

    private void CheckStatusBT() {

        BluetoothAdapter bluetoothAdapter = this.btAdapter;

        if (btAdapter == null) {

            Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_LONG).show();

        } else {

            if (btAdapter.isEnabled()) {

                Toast.makeText(getBaseContext(), "Connecting", Toast.LENGTH_SHORT).show();


                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.BLUETOOTH_CONNECT },
                            1);
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.BLUETOOTH_CONNECT },
                            1);
                }
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;

        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {


            InputStream tmpIn = null;

            OutputStream tmpOut = null;

            try{

                tmpIn = socket.getInputStream();

                tmpOut = socket.getOutputStream();

            } catch (IOException e) {

            }
            mmInStream = tmpIn;

            mmOutStream = tmpOut;
        }

        public void run(){

            byte[] buffer = new byte[1024];

            int bytes;

            while (true) {

                try {

                    bytes = mmInStream.read(buffer);

                    String readMessage = new String(buffer, 0, bytes);

                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();

                }catch (IOException e) {

                    break;
                }

            }

        }

        public void write(String input){

            byte [] msgBuffer = input.getBytes(); //converts entered String into bytes

            try {
                mmOutStream.write(msgBuffer);   //write bytes over BT connection via outstream

            } catch (IOException e){

                Toast.makeText(getBaseContext(), "Connection failure", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (pressedTime + 1000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

}