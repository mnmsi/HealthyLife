package com.echoinc.healthylife;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class UserInfromationActivity extends AppCompatActivity {

    private EditText nameET, addressET, phoneET;
    private TextView dateOfBirth, previousDonationDate;
    private RadioGroup genderRG, previousDonationRG;
    private Button submitButton;
    private Spinner bloodGroupSP;
    private String checkDonorAge;

    public static FirebaseDatabase mDatabase;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Geocoder geocoder;
    private List<Address> addresses;
    private String lat, lng;
    private String lastlat, lastlng;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private FirebaseUser user;
    static String LoggedIn_User_Phone;
    static String Current_User_Name;

    private int year, month, day;
    private Calendar calendar;
    private String previousDateOfDonation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infromation);

        nameET = findViewById(R.id.donorNameET);
        addressET = findViewById(R.id.addressET);
        phoneET = findViewById(R.id.phoneET);
        dateOfBirth = findViewById(R.id.dateOfBirthTV);
        previousDonationDate = findViewById(R.id.lastDateOfDonation);
        genderRG = findViewById(R.id.genderRG);
        previousDonationRG = findViewById(R.id.previousDonationRG);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progress_bar);
        bloodGroupSP = findViewById(R.id.bloodGroupSP);

        final ArrayList<String> bloodGroup = new ArrayList<>();
        bloodGroup.add("Select blood group");
        bloodGroup.add("O-(ve)");
        bloodGroup.add("O+(ve)");
        bloodGroup.add("A-(ve)");
        bloodGroup.add("A+(ve)");
        bloodGroup.add("B+(ve)");
        bloodGroup.add("B-(ve)");
        bloodGroup.add("AB+(ve)");
        bloodGroup.add("AB-(ve)");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, bloodGroup);
        bloodGroupSP.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Log.d("LOGGED", "user: " + user);

        if (user != null) {
            LoggedIn_User_Phone = user.getPhoneNumber();
        }
        if (getIntent().getStringExtra("phoneNum") != null){
            phoneET.setText("+88"+getIntent().getStringExtra("phoneNum"));
        }
        phoneET.setText(LoggedIn_User_Phone);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        geocoder = new Geocoder(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {

                    lat = String.valueOf(location.getLatitude());
                    lng = String.valueOf(location.getLongitude());

                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        addressET.setText(addresses.get(0).getAddressLine(0) + ","
                                + addresses.get(0).getLocality() + "," +
                                addresses.get(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        getUserLastLocation();
        requestCurrentLocationUpdate();

        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(UserInfromationActivity.this,
                        dateListener, year, month, day);
                datePickerDialog.show();
            }
        });

        previousDonationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(UserInfromationActivity.this,
                        previousdateListener, year, month, day);
                datePickerDialog.show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String donorName = nameET.getText().toString();
                Current_User_Name = donorName;
                if (TextUtils.isEmpty(donorName)) {
                    Toast.makeText(getApplicationContext(), "Enter Your name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (genderRG.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Please select Gender!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String donorGender = ((RadioButton) findViewById(genderRG.getCheckedRadioButtonId())).getText().toString();
                String donorAddress = addressET.getText().toString();
                if (TextUtils.isEmpty(donorAddress)){
                    Toast.makeText(getApplicationContext(), "Please enter your address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String donorPhoneNumber = phoneET.getText().toString();
                String donorBirthDay = dateOfBirth.getText().toString();
                if (donorBirthDay.length() == 13) {
                    Toast.makeText(getApplicationContext(), "Please pick your birthday!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String donorBloodGroup = bloodGroupSP.getSelectedItem().toString();
                if (donorBloodGroup.length() == 18){
                    Toast.makeText(getApplicationContext(), "Please pick your Blood Group!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (previousDonationRG.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Please select previous donation YES or NO!", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String donorPreviousDonation = ((RadioButton) findViewById(previousDonationRG.getCheckedRadioButtonId())).getText().toString();
                if (donorPreviousDonation.matches("No")) {
                    previousDateOfDonation = "No previous Donation";
                } else {
                    previousDateOfDonation = previousDonationDate.getText().toString();
                    if (previousDateOfDonation.length() == 21) {
                        Toast.makeText(getApplicationContext(), "Please pick previous donation date!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (Integer.parseInt(checkDonorAge) < 18){
                    Toast.makeText(getApplicationContext(),"you are unable to register because you are under 18 age", Toast.LENGTH_SHORT).show();
                }
                else {
                    mDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference mRootRef = mDatabase.getInstance().getReference();
                    DatabaseReference ref1= mRootRef.child("Users").child(donorPhoneNumber);

                    UsersDetailed users = new UsersDetailed(0, donorName, donorGender, donorAddress, donorPhoneNumber, donorBirthDay, donorBloodGroup, donorPreviousDonation, previousDateOfDonation, lastlat, lastlng);

                /*ref1.child("Image_Url").setValue("Null");
                ref1.child("Name").setValue(donorName);
                ref1.child("Gender").setValue(donorGender);
                ref1.child("Address").setValue(donorAddress);
                ref1.child("Phone").setValue(donorPhoneNumber);
                ref1.child("BirthDay").setValue(donorBirthDay);
                ref1.child("Blood Group").setValue(donorBloodGroup);
                ref1.child("Previous Donation").setValue(donorPreviousDonation);
                ref1.child("Previous Donation Date").setValue(previousDateOfDonation);
                ref1.child("Latitude").setValue(lastlat);
                ref1.child("Longitude").setValue(lastlng);*/

                    ref1.setValue(users);
                    //ref1.keepSynced(true);

                    Intent intent = new Intent(UserInfromationActivity.this, MainActivity.class);
                    startActivity(intent);
                }


            }
        });

    }

    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            calendar.set(i, i1, i2);
            String currentDate = sdf.format(calendar.getTime());
            dateOfBirth.setText(currentDate);
            checkDonorAge = calculateAge(i, i1, i2);
        }
    };

    private DatePickerDialog.OnDateSetListener previousdateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            calendar.set(i, i1, i2);
            String currentDate = sdf.format(calendar.getTime());
            previousDonationDate.setText(currentDate);
        }
    };

    private void requestCurrentLocationUpdate() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},2);

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public void getUserLastLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    lastlat = String.valueOf(location.getLatitude());
                    lastlng = String.valueOf(location.getLongitude());
                }
            }
        });
    }

    private String calculateAge(int year, int month, int day){

        //String donorBirthDay = dateOfBirth.getText().toString();

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}
