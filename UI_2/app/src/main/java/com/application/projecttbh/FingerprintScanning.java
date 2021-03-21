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
import java.util.HashMap;
import java.util.Map;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        @Override
        public void onReceivedData(byte[] byteArray) {
            StringBuffer hexStringBuffer = new StringBuffer();
            for (int i = 0; i < byteArray.length; i++) {
                hexStringBuffer.append(byteToHex(byteArray[i]));
            }
            String data = hexStringBuffer.toString();
            allData += data;
            if (allData.length() == 115214) {
                try {
                    saveData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

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
        sendButton = (Button) findViewById(R.id.buttonSend);
        scanningTag = (TextView) findViewById(R.id.scanning);
        stillFP = (ImageView) findViewById(R.id.stillFP);
        gifFP = (ImageView) findViewById(R.id.gifFP);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        onStartUSB();

        sendButton.setOnClickListener(v -> {
            serialPort.write(FINGER_PRINT_CODE.getBytes());
            stillFP.getLayoutParams().height = 0;

            gifFP.getLayoutParams().height = 800;
            stillFP.requestLayout();
            gifFP.requestLayout();
            scanningTag.setVisibility(View.VISIBLE);
            sendButton.setEnabled(false);
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
        serialPort.close();
        Toast.makeText(getApplicationContext(), String.format("%s\n", "Serial Connection Closed"), Toast.LENGTH_LONG).show();
    }

    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public void saveData() throws IOException {
        String formattedData = "";
        allData = allData.substring(14);
        int picData[][] = new int[160][120];
        int x = 0;
        int y = 0;
        for (int i = 0; i < allData.length(); i += 6) {
            String hex0 = String.valueOf(allData.charAt(i)) + String.valueOf(allData.charAt(i+1));
            String ascii0 = hexToAscii(hex0);
            String hex1 = String.valueOf(allData.charAt(i+2)) + String.valueOf(allData.charAt(i+3));
            String ascii1 = hexToAscii(hex1);
            String hex2 = String.valueOf(allData.charAt(i+4)) + String.valueOf(allData.charAt(i+5));
            String ascii2 = hexToAscii(hex2);
            if (x != 159) {
                formattedData += ascii0 + ascii1 + ascii2 + ",";
                x++;
            } else {
                formattedData += ascii0 + ascii1 + ascii2 + "\n";
                x = 0;
                y+= 1;
            }
        }

        Context context = getApplicationContext();
        int seqNum = AppProperties.getInstance().getSeqNum();
        String fileName = OnboardData.getInstance().getPassportId() + "_FP_" + seqNum + ".txt";
        OnboardData.getInstance().update_S3_fp_data(fileName, seqNum);
        File dir = new File(context.getFilesDir(), "FP");
        if(!dir.exists()){
            dir.mkdir();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(context.getFilesDir() + "/FP/" + fileName));
        writer.write(formattedData);

        writer.close();
        allData = "";

        AppProperties.getInstance().setSeqNum(seqNum + 1);
        Intent intent = new Intent(FingerprintScanning.this, InitialScan.class); // Call a secondary view
        startActivity(intent);

    }
}
