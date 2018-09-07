package com.example.adentaxi.riderapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.adentaxi.riderapp.Interface.RiderInfo;
import com.example.adentaxi.riderapp.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;

public class Signup extends AppCompatActivity {
    ActivitySignupBinding binding;

    private EditText inputName,inputEmail,inputPassword;
    private Button btn_SignUp;
    private ImageButton back;
    private FirebaseAuth Auth;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        //Get Firebase auth instance
        Auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Riders");

        inputName = (EditText)findViewById(R.id.et_full_name);
        inputEmail = (EditText)findViewById(R.id.et_email_address);
        inputPassword = (EditText)findViewById(R.id.et_password);
        btn_SignUp = (Button)findViewById(R.id.btn_signup);
        back = (ImageButton) findViewById(R.id.back);


        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = inputName.getText().toString().trim();
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();



                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "ادخل البريد الالكتروني!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "ادخل كلمة المرور!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "يجب الا تقل كلمة المرور عن 6 احرف!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //create user
                Auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(Signup.this, "تم انشاء حسابك بنجاح .." + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Signup.this, "العملية فشلت .." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    String id = Auth.getCurrentUser().getUid();
                                    RiderInfo riderInfo = new RiderInfo();
                                    riderInfo.setName(name);
                                    riderInfo.setEmail(email);
                                    riderInfo.setPassword(password);
                                    databaseReference.child(id).setValue(riderInfo);
                                    startActivity(new Intent(Signup.this, Login.class));
                                    finish();
                                }
                            }
                        });

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this,SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }




    public void login(View view) {
        startActivity(new Intent(this, Login.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

}
