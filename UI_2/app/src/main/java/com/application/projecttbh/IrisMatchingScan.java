package com.application.projecttbh;

import android.annotation.SuppressLint;
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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IrisMatchingScan extends Activity {
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
    int countTime = 3;


    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @SuppressLint("SetTextI18n")
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
                allData = allData.replaceAll("Done", "");
                scanningTag.setText("Done!");
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        createCapture();
                    }
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
                            serialPort.setBaudRate(57600);
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



    @SuppressLint("SetTextI18n")
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

        scanningTag.setText("Begin Scan...");

        sendButton.setOnClickListener(v -> {
            serialPort.write(IRIS_SCAN_CODE.getBytes());
            scanningTag.setVisibility(View.VISIBLE);
            countTime = 3;
            allData = "";
            startTimer();
            // sendButton.setEnabled(false);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countTime = 3;
        scanningTag.setText("Begin Scan...");
        allData = "";
        cancelTimer();
        onCloseConnection();
    }

    void startTimer() {
        cTimer = new CountDownTimer(3001, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                scanningTag.setText("Picture in " + countTime + " ...");
                countTime -= 1;
            }
            @SuppressLint("SetTextI18n")
            public void onFinish() {
                // sendButton.setEnabled(false);
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
        if (serialPort != null) {
            serialPort.close();
            unregisterReceiver(broadcastReceiver);
            Toast.makeText(getApplicationContext(), String.format("%s\n", "Serial Connection Closed"), Toast.LENGTH_LONG).show();
        }
    }

    public void createCapture() throws IOException {

        String data = allData.trim();
        MatchingProperties.getInstance().setIris_image(data);
        String[] dataArray = data.split(" ");
        ArrayList<Integer> cleanData = new ArrayList<Integer>();
        for(int i = 0; i < dataArray.length; i++) {
            int intdata = Integer.parseInt(dataArray[i], 16);
            cleanData.add(intdata);
        }

        Context context = getApplicationContext();
        File dir = new File(context.getFilesDir(), "Iris");
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir, "output.jpg");

        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int y = 0;
        for (int x = 0; x < cleanData.size(); x++) {
            y = y + 1;
            if (y % 33 == 0) {
                continue;
            } else {
                bos.write(cleanData.get(x));
            }
        }

        Intent intent = new Intent(IrisMatchingScan.this, ConfirmIrisMatch.class); // Call a secondary view
        startActivity(intent);
    }

}
