package com.application.projecttbh;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IrisScan extends Activity {
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    Button sendButton;
    CountDownTimer cTimer = null;
    UsbManager usbManager;
    TextView scanningTag;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    final String IRIS_SCAN_CODE = "b";
    String allData = "";


    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceivedData(byte[] byteArray) {
            String data = new String(byteArray, StandardCharsets.UTF_8);
            allData += data;
            if (allData.contains("No Camera")) {
                allData = "";
                scanningTag.setText("No Camera Found");
            } else if (allData.contains("Snap in 3")) {
                allData = "";
                scanningTag.setText("Picture in 3 ...");
            } else if (allData.contains("Failed")) {
                allData = "";
                scanningTag.setText("Failed to capture");
            } else if (allData.contains("Done")) {
                allData = allData.substring(0, allData.length() - 4);
                try {
                    saveData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);

                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                onStartUSB();
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                onCloseConnection();
            }
        }
        ;
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iris_scan);
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        sendButton = findViewById(R.id.buttonSend);
        scanningTag = findViewById(R.id.scanning);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
        onStartUSB();

        sendButton.setOnClickListener(v -> {
            serialPort.write(IRIS_SCAN_CODE.getBytes());
            scanningTag.setVisibility(View.VISIBLE);
            startTimer();
            // sendButton.setEnabled(false);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
        onCloseConnection();
    }

    void startTimer() {
        cTimer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                scanningTag.setText("Picture in " + millisUntilFinished/1000 + " ...");
            }
            public void onFinish() {
                sendButton.setEnabled(false);
                scanningTag.setText("Processing ...");
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    public void onStartUSB() {

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341) //Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                    Toast.makeText(getApplicationContext(), String.format("%s\n", "Wrong ID"), Toast.LENGTH_LONG).show();
                }

                if (!keep)
                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), String.format("%s\n", "No Devices"), Toast.LENGTH_LONG).show();
        }
    }

    public void onCloseConnection() {
        serialPort.close();
        unregisterReceiver(broadcastReceiver);
        Toast.makeText(getApplicationContext(), String.format("%s\n", "Serial Connection Closed"), Toast.LENGTH_LONG).show();
    }

    public void saveData() throws IOException {
        Context context = getApplicationContext();
        int seq_num = AppProperties.getInstance().getSeqNum();
        if (AppProperties.getInstance().getType().equals("onboarding")) {
            int seq_tag = seq_num -2;
            String tag = OnboardData.getInstance().getPassportId() + "_IRIS_" + seq_tag + ".txt";
            OnboardData.getInstance().update_S3_iris_data(tag, seq_tag);

            File dir = new File(context.getFilesDir(), "IRIS");
            if(!dir.exists()){
                dir.mkdir();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(context.getFilesDir() + "/IRIS/" + tag));
            writer.write(allData);

            writer.close();


            int currentScan = seq_num + 1;
            AppProperties.getInstance().setSeqNum(currentScan);
            if (AppProperties.getInstance().getSeqNum() == 4) {
                Intent intent = new Intent(IrisScan.this, UploadOnboardData.class); // Call a secondary view
                startActivity(intent);
            } else {
                Intent intent = new Intent(IrisScan.this, InitialScan.class); // Call a secondary view
                startActivity(intent);
            }
        }
    }
}
