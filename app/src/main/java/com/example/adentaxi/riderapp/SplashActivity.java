package com.example.adentaxi.riderapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.adentaxi.riderapp.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

    }

    public void  login(View view)
    {
        startActivity(new Intent(this,Login.class));
    }


    public void  getStarted(View view)
    {
        startActivity(new Intent(this,Signup.class));
    }

}

