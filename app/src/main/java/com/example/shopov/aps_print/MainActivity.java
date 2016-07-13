package com.example.shopov.aps_print;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    private static final String TAG = MainActivity.class.getSimpleName();
    private PendingIntent mPermissionIntent;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                            //call method to set up device communication
                            Log.d(TAG, "permission granted for device " + device);
                            byte[] bytes = new byte[4];
                            int TIMEOUT = 0;
                            boolean forceClaim = true;

                            bytes[0] = 'A';
                            bytes[1] = 'P';
                            bytes[2] = 'S';
                            bytes[3] = '\n';

                            UsbInterface intf = device.getInterface(0);
                            UsbEndpoint endpoint = intf.getEndpoint(0);
                            UsbDeviceConnection connection = mUsbManager.openDevice(device);
                            connection.claimInterface(intf, forceClaim);
                            connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT); //do in another thread
                        }
                    }
                    else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
    }
    public void enumerate(View view) {
        Button enumerate_button = (Button)findViewById(R.id.buttonEnumerate);

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        if (deviceList.isEmpty())
            enumerate_button.setText("failed");
        else
        {
            enumerate_button.setText("enumeration successful" + deviceList.values().toArray()[0]);
            UsbDevice device = deviceList.values().toArray(new UsbDevice[0])[0];
            manager.requestPermission(device, mPermissionIntent);
        }
    }
}
