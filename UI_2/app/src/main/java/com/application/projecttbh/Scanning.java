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
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Scanning extends Activity {
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    ArrayList<Byte> allData = new ArrayList<Byte>();
    final String FINGER_PRINT_CODE = "a";

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data;
            try {
                Byte[] byteObj = new Byte[arg0.length];
                int i = 0;
                for(byte b: arg0) {
                    byteObj[i++] = b;
                }
                allData.addAll(Arrays.asList(byteObj));
                data = new String(arg0, "UTF-8");
                data.concat("/n");
                // Getting data length
                // We want 52116 bytes
                // tvAppend(textView, String.valueOf(allData.size()));
                // TODO: Add correct logic here
                // TODO: and flow to next page properly
                int seq_num = AppProperties.getInstance().getFp_seq_num();
                String tag = OnboardData.getInstance().getPassportId() + seq_num;
                OnboardData.getInstance().updateFP_data(tag, seq_num);
                if (seq_num == 3) {
                    AppProperties.getInstance().setFp_seq_num(0);
                    Intent intent = new Intent(Scanning.this, IrisScan.class); // Call a secondary view
                    startActivity(intent);
                    // Go to next page
                } else {
                    int currentScan = seq_num + 1;
                    AppProperties.getInstance().setFp_seq_num(currentScan);
                    Intent intent = new Intent(Scanning.this, FingerprintScan.class); // Call a secondary view
                    startActivity(intent);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
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
                onClickStart();
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                onUsbStop();
            }
        }
        ;
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanning);
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onUsbStop();
    }

    public void onClickStart() {

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341)//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                    try {
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    serialPort.write(FINGER_PRINT_CODE.getBytes());
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

    public void onUsbStop() {
        serialPort.close();
    }
}