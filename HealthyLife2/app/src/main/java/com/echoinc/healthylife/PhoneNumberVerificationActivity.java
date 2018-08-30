package com.echoinc.healthylife;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class PhoneNumberVerificationActivity extends AppCompatActivity {

    EditText donorMobileNumET, verificatonCodeET;
    TextView otpText, verifyText;
    Button nextButton, submitButton;
    ProgressBar progressBar;
    String donorNum;

    private FirebaseAuth mAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseUser user;
    public static FirebaseDatabase mDatabase;
    static String LoggedIn_User_Phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verification);
        Firebase.setAndroidContext(this);

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        verifyText = findViewById(R.id.textView);
        donorMobileNumET = findViewById(R.id.mobileNumber);
        nextButton = findViewById(R.id.nextBtn);
        otpText = findViewById(R.id.otpTV);
        verificatonCodeET = findViewById(R.id.otp_editText);
        submitButton = findViewById(R.id.otp_button);
        progressBar = findViewById(R.id.progress_bar);

        user = mAuth.getCurrentUser();
        Log.d("LOGGED", "user: " + user);

        if (user != null) {
            LoggedIn_User_Phone =user.getPhoneNumber();
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED ) {

            } else {
                checkLocationPermission();
            }
        }


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(PhoneNumberVerificationActivity.this,"verification Success"+ phoneAuthCredential,Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                progressBar.setVisibility(View.GONE);

                if (e instanceof FirebaseAuthInvalidCredentialsException){

                    Toast.makeText(PhoneNumberVerificationActivity.this,"Please Enter Your Phone Number",Toast.LENGTH_LONG).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {

                    Toast.makeText(PhoneNumberVerificationActivity.this,"Time out" ,Toast.LENGTH_LONG).show();

                }
                else{

                    Toast.makeText(PhoneNumberVerificationActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                Toast.makeText(PhoneNumberVerificationActivity.this,"Verification code sent to mobile",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                mVerificationId = verificationId;
                mResendToken = token;

                verifyText.setVisibility(View.GONE);
                donorMobileNumET.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
                otpText.setVisibility(View.VISIBLE);
                verificatonCodeET.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.VISIBLE);

            }
        };



        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                donorNum = donorMobileNumET.getText().toString();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+880"+donorNum,
                        60,
                        TimeUnit.SECONDS,
                        PhoneNumberVerificationActivity.this,
                        mCallbacks);
                progressBar.setVisibility(View.VISIBLE);
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otpEditview = verificatonCodeET.getText().toString();

                if (TextUtils.isEmpty(otpEditview)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Code!", Toast.LENGTH_SHORT).show();
                    return;
                }

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpEditview);
                signInWithPhoneAuthCredential(credential);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(PhoneNumberVerificationActivity.this, "Verification done", Toast.LENGTH_LONG).show();
                            //FirebaseUser phoneNum = mAuth.getCurrentUser();
                            FirebaseUser user = task.getResult().getUser();
                            Intent intent = new Intent(PhoneNumberVerificationActivity.this, UserInfromationActivity.class);
                            intent.putExtra("phoneNum", donorNum);
                            startActivity(intent);

                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(PhoneNumberVerificationActivity.this,"Verification failed code invalid",Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                });

    }

    public static final int MY_PERMISSIONS_REQUEST_CALL = 98;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ActivityCompat.requestPermissions(PhoneNumberVerificationActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                    }

                } else {
                    closeNow();
                }
                return;
            }
        }
    }
    private void closeNow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            finishAffinity();
        }else{
            finish();
        }
    }

}
