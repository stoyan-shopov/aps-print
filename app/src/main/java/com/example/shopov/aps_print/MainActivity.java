package com.example.shopov.aps_print;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void enumerate(View view) {
        Button enumerate_button = (Button)findViewById(R.id.buttonEnumerate);
        enumerate_button.setText("failed");
    }
}
