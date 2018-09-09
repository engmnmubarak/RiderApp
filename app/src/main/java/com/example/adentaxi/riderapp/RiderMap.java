package com.example.adentaxi.riderapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adentaxi.riderapp.Interface.DriverInfo;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RiderMap extends FragmentActivity implements OnMapReadyCallback
                {

    private FirebaseAuth Auth = FirebaseAuth.getInstance();
    private final String TAG = "RiderMap.class";
    ImageView btn_logout;
    Map<String, String> mMarkerMap = new HashMap<>();
    ArrayList<DriverInfo> driverInfos = new ArrayList<>();
    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.d(TAG,"onCreate");
        btn_logout = findViewById(R.id.logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        //Set Message and Title
                    AlertDialog.Builder builder = new AlertDialog.Builder(RiderMap.this);
                    builder.setMessage("هل انت متأكد انك تريد تسجيل الخروج من تطبيق عدن تاكسي")
                            .setTitle("تاكيد تسجيل الخروج                  ");

                    //Set When SEND Button Click
                    builder.setPositiveButton("خروج", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Auth.signOut();
                            Toast.makeText(RiderMap.this, "تم تسجيل الخروج بنجاج ..", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RiderMap.this,SplashActivity.class));
                            finish();
                        }
                    });

                    //Set When Cancel Button Click
                    builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //Dismissing the alertDialog
                            dialogInterface.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                        dialog.show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"onMapReady");
        mGoogleMap = googleMap;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        loadAllDriversActive();

                        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {

                                Intent intentDriverInfo = new Intent(getApplicationContext(), DriverInfoActivity.class);
                                intentDriverInfo.putExtra("phone",marker.getSnippet());
                                startActivity(intentDriverInfo);
                                return false;
                            }
                        });
                    }
                });
            }else{
                checkLocationPermission();
            }
        }

    }

    // check Location Permission to use and display the map
    private void checkLocationPermission() {
        Log.d(TAG,"checkLocationPermission");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(RiderMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(RiderMap.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void loadAllDriversActive(){
        final String _loadAllDriversActive ="loadAllDriversActive";
        Log.d(TAG,"loadAllDriversActive");

        FirebaseDatabase.getInstance()
                .getReference("Drivers")
                .orderByChild("driver_active")
                .equalTo("1")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mGoogleMap.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String name = snapshot.child("name")
                                    .getValue(String.class);
                            Log.d(TAG,_loadAllDriversActive+" onDataChange "+name);
                            String email = snapshot.child("email")
                                    .getValue(String.class);
                            String car_name = snapshot.child("car_name")
                                    .getValue(String.class);
                            String phone = snapshot.child("phone")
                                    .getValue(String.class);
                            Double driverLatitude  = snapshot.child("driverLatitude")
                                    .getValue(Double.class);
                            Double driverLongitude  = snapshot.child("driverLongitude")
                                    .getValue(Double.class);


                            DriverInfo driverInfo = dataSnapshot.getValue(DriverInfo.class);
                            driverInfos.add(driverInfo);
                            LatLng driver_location = new LatLng(driverLatitude,driverLongitude);
                            for (int i = 0; i < driverInfos.size(); i++) {
                                if (mGoogleMap != null) {
                                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                            .position(driver_location)
                                            .title("السائق : "+ name)
                                            .snippet(phone)
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_adentxicar)));

                                    mMarkerMap.put(marker.getId(),phone);
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    builder.include(driver_location);
                                    LatLngBounds bounds = builder.build();
                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                                    mGoogleMap.moveCamera(cu);
                                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);

                                }
                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }





}