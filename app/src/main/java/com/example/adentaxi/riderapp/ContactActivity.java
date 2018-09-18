package com.example.adentaxi.riderapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ContactActivity extends AppCompatActivity {

    TextView appa_name,app_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);

        appa_name = findViewById(R.id.app_name);
        app_phone = findViewById(R.id.app_phone);
    }

}