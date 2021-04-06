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
import java.util.HashMap;
import java.util.Map;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

public class FingerprintScanning extends Activity {
    private static final String FINGER_PRINT_CODE = "a";
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    ImageView stillFP, gifFP;
    Button sendButton;
    TextView scanningTag;
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    String allData = "";

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceivedData(byte[] byteArray) {
            String data = new String(byteArray, StandardCharsets.UTF_8);
            allData += data;
            if (allData.contains("Finger")) {
                allData = allData.replace("Finger", "");
                scanningTag.setText("Place Finger on Sensor");
            } else if (allData.contains("Remove")) {
                allData = allData.replace("Remove", "");
                scanningTag.setText("Remove finger");
            } else if (allData.contains("Failed") || allData.contains("Exists") || allData.contains("Error")) {
                allData = "";
                scanningTag.setText("Error Reading Finger");
            } else if (allData.contains("Enroll Complete")) {
                scanningTag.setText("Enrollment Complete");
                sendButton.setEnabled(false);
                allData = "";
            }
            String[] split = allData.split(" ");
            if (split.length == 498) {
                try {
                    if ((allData.charAt(allData.length() -1) == ('1') && allData.charAt(allData.length() -2) == (' ')) || (allData.charAt(allData.length() -1) == (' ') && allData.charAt(allData.length() -2) == ('1') && allData.charAt(allData.length() -3) == (' ') )) {
                        allData = "";
                        scanningTag.setText("Error Reading Finger");
                    } else {
                        if (allData.charAt(allData.length() - 1) != (' ')) {
                            allData += " ";
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            saveData();
                        }
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
                serialPort.write(FINGER_PRINT_CODE.getBytes());
                stillFP.getLayoutParams().height = 0;

                gifFP.getLayoutParams().height = 800;
                stillFP.requestLayout();
                gifFP.requestLayout();
                scanningTag.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // sendButton.setEnabled(false);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        if (serialPort != null) {
            serialPort.close();
            unregisterReceiver(broadcastReceiver);
            Toast.makeText(getApplicationContext(), String.format("%s\n", "Serial Connection Closed"), Toast.LENGTH_LONG).show();
        }
    }

    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveData() throws IOException {


        Context context = getApplicationContext();
        int seqNum = AppProperties.getInstance().getSeqNum();
        String fileName = OnboardData.getInstance().getPassportId() + "_FP_" + seqNum + ".txt";
        OnboardData.getInstance().update_S3_fp_data(fileName, seqNum);
        File dir = new File(context.getFilesDir(), "FP");
        if(!dir.exists()){
            dir.mkdir();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(context.getFilesDir() + "/FP/" + fileName));
        writer.write(allData);

        writer.close();
        allData = "";

        AppProperties.getInstance().setSeqNum(seqNum + 1);
        Intent intent = new Intent(FingerprintScanning.this, InitialScan.class); // Call a secondary view
        startActivity(intent);
    }
}
