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
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

public class FingerprintMatching extends Activity {
    private static final String FINGER_PRINT_CODE = "c";
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    ImageView stillFP, gifFP;
    Button sendButton;
    TextView scanningTag;
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    CountDownTimer cTimer = null;
    String allData = "";


    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceivedData(byte[] byteArray) {
            String data = new String(byteArray, StandardCharsets.UTF_8);
            allData += data;
           if (allData.contains("Transfer")) {
                allData = allData.replace("Transfer", "");
//                scanningTag.setText("Transfer");
            }  else if (allData.contains("Error")) {
                scanningTag.setText("Error");
            } else if (allData.contains("No Match")) {
               scanningTag.setText("No Match");
            } else if (allData.contains("Match")) {
                scanningTag.setText("Match");
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanning_finger);
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        sendButton = findViewById(R.id.buttonSend);
        scanningTag = findViewById(R.id.scanning);
        stillFP = findViewById(R.id.stillFP);
        gifFP = findViewById(R.id.gifFP);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        onStartUSB();

        sendButton.setOnClickListener(v -> {
            try {
                scanningTag.setVisibility(View.VISIBLE);
                scanningTag.setText("Press finger on sensor");
                loadFP();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
        onCloseConnection();
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

    public void loadFP() throws IOException {
        int seqNum = AppProperties.getInstance().getSeqNum();
        String fileName = MatchingProperties.getInstance().getPassportId() + "_FP_" + seqNum + ".txt";
        Context context = getApplicationContext();
        String result = S3Client.downloadFP(fileName, context);
        serialPort.write(FINGER_PRINT_CODE.getBytes());
        startTimer(result);

    }

    void startTimer(String result) {
        cTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                serialPort.write(result.getBytes());
            }
        };
        cTimer.start();
    }

    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }
}
