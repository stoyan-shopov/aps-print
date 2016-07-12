package com.example.shopov.aps_print;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void enumerate(View view) {
        Button enumerate_button = (Button)findViewById(R.id.buttonEnumerate);

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        if (deviceList.isEmpty())
            enumerate_button.setText("failed");
        else
            enumerate_button.setText("enumeration successful" + deviceList.values().toArray()[0]);
    }
}
