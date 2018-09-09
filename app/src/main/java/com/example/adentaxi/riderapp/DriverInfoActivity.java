package com.example.adentaxi.riderapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverInfoActivity extends AppCompatActivity {
    private final String TAG = "DriverInfoActivity";

    Button btn_call;
    TextView driver_name,driver_car,driver_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info);

        btn_call = findViewById(R.id.calldriver);
        driver_name = findViewById(R.id.drivername);
        driver_car = findViewById(R.id.drivercar);
        driver_phone = findViewById(R.id.driverphone);

        final String intent_phone =getIntent().getStringExtra("phone");
        Log.d(TAG, "onCreate: "+intent_phone);
        FirebaseDatabase.getInstance()
                .getReference("Drivers")
                .orderByChild("phone")
                .equalTo(intent_phone)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                            String name =snapshot.child("name").getValue(String.class);
                            driver_name.setText(name);
                            String car =snapshot.child("car_name").getValue(String.class);
                            driver_car.setText(car);
                            String phone =snapshot.child("phone").getValue(String.class);
                            driver_phone.setText(phone);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        //call button
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent=new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+driver_phone.getText().toString()));
                startActivity(callIntent);

            }
        });

    }

}
