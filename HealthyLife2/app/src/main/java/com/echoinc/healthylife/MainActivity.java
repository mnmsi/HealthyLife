package com.echoinc.healthylife;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.text.Normalizer;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.geojson.GeoJsonPoint;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "MainActivity";

    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFrag;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private ProgressDialog mProgressDialog;
    private double end_latitude, end_longitude;

    private Geocoder geocoder;
    private List<Address> addresses;

    private SearchView searchView;
    private TextView userName;
    private TextView userPhone;
    private ImageView userImage;
    private Button showDirection;
    private String currentUserName, currentUserPhone;
    private double updateLat, updateLng;

    private FirebaseDatabase myDatabase;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
    private NearbyService nearbyService;
    private int radius = 1000;
    private String typeHospital = "hospital";
    private String typeResturent = "restaurant";
    private String typeBank = "bank";
    private String typeATM = "atm";

    private static final String BASE_URL_DIR = "https://maps.googleapis.com/maps/api/directions/";
    private DirectionServiceInterface directionServiceInterface;
    private String dirMode = "driving";

    private int year, month, day;
    private Calendar calendar;
    private String currentDate;
    private String donorLastDonationDate;
    private int differenceOfMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (UserInfromationActivity.mDatabase == null){
            UserInfromationActivity .mDatabase = FirebaseDatabase.getInstance();
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            this.finish();
            startActivity(new Intent(getApplicationContext(), PhoneNumberVerificationActivity.class));
        }

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        searchView = findViewById(R.id.search_bar);
        searchView.clearFocus();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        locationButtonMargin();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        userName = header.findViewById(R.id.userNameTV);
        userPhone = header.findViewById(R.id.userPhoneTV);
        userImage = header.findViewById(R.id.imageView);
        showDirection =findViewById(R.id.showingButton);

        geocoder = new Geocoder(this);

        user = mAuth.getCurrentUser();

        if (user != null){
            currentUserPhone = user.getPhoneNumber();
            userPhone.setText(currentUserPhone);
            FirebaseDatabase Database = FirebaseDatabase.getInstance();
            DatabaseReference reference = Database.getReference().child("Users").child(currentUserPhone);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentUserName = dataSnapshot.child("name").getValue(String.class);
                    userName.setText(currentUserName);
                    if (currentUserName.length() > 0){
                        startService(new Intent(MainActivity.this, UpdateUserLocationService.class));
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                myDatabase = FirebaseDatabase.getInstance();
                myRef = myDatabase.getReference();
                Query donorQuery = myRef.child("Users").orderByChild("bloodGroup").equalTo(query.toUpperCase().trim() + "(ve)");
                donorQuery.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final ArrayList<UserSingleInfo> userSingleInfo = new ArrayList<>();
                        for (DataSnapshot users : dataSnapshot.getChildren()) {

                            UsersDetailed usersDetailed = users.getValue(UsersDetailed.class);
                            String userName = usersDetailed.getname();
                            String userPhone = usersDetailed.getPhone();
                            String userBloodGroup = usersDetailed.getBloodGroup();
                            String userLastDonationDate = usersDetailed.getPreviousDonationDate();
                            String userLat = usersDetailed.getLat();
                            String userLng = usersDetailed.getLng();

                            LatLng userLocation = new LatLng(Double.parseDouble(userLat), Double.parseDouble(userLng));
                            Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(userLocation)
                                    .title(userName));
                            userSingleInfo.add(new UserSingleInfo(userName, userPhone, userBloodGroup, userLastDonationDate,marker.getId()));
                            myRef.keepSynced(true);

                        }
                        showDirection.setText("Show Direction");
                        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                            String userPhoneNum;

                            @Override
                            public boolean onMarkerClick(Marker marker) {

                                CustomDialogBuilder builder = new CustomDialogBuilder(MainActivity.this);

                                for (int i = 0; i < userSingleInfo.size(); i++) {

                                    if (marker.getId().equals(userSingleInfo.get(i).getMarkerId())) {

                                        builder.setTitle(userSingleInfo.get(i).getUserName());
                                        builder.setMessage("Phone:   " + userSingleInfo.get(i).getUserPhone() + "\n" + "Blood Group:  " + userSingleInfo.get(i).getUserBloodGroup()
                                                + "\n" + "Last Donation:  " + userSingleInfo.get(i).getUserLastDonationDate());
                                        userPhoneNum = "tel:"+userSingleInfo.get(i).getUserPhone();
                                    }
                                }
                                builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        Intent intent = new Intent(Intent.ACTION_CALL);
                                        intent.setData(Uri.parse(userPhoneNum));
                                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            checkCallPermission();
                                            return;
                                        }
                                        startActivity(intent);
                                    }
                                });
                                builder.setNegativeButton("Cancel", null);
                                builder.show();

                                if (userPhoneNum.isEmpty()){
                                    mProgressDialog = new ProgressDialog(MainActivity.this);
                                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    mProgressDialog.setMessage("Please wait.");
                                    mProgressDialog.show();
                                }

                                showDirection.setVisibility(View.VISIBLE);
                                end_latitude =marker.getPosition().latitude;
                                end_longitude = marker.getPosition().longitude;
                                showDirection.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showDirectionDrawPolyline();
                                    }
                                });

                                return false;
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                       // Toast.makeText(MainActivity.this,"data retrive failed", Toast.LENGTH_SHORT).show();
                    }

                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mGoogleMap.clear();
                return false;
            }
        });

    }

    private void locationButtonMargin() {
        View mapView = mapFrag.getView();
        if (mapView != null &&
                mapView.findViewById(1) != null) {

            View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();

            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 300);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            mAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), PhoneNumberVerificationActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_donor_feed) {
            startActivity(new Intent(MainActivity.this, DonorFeed.class));
        } else if (id == R.id.nav_donation_date_update) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    updateDateListener, year, month, day);
            datePickerDialog.show();
        } else if (id == R.id.nav_nearby_hospital) {
            onShowNearbyHospitals();
        } else if (id == R.id.nav_nearby_resturent) {
            onShowNearbyResturent();
        } else if (id == R.id.nav_nearby_bank) {
            onShowNearbyBank();
        } else if (id == R.id.nav_nearby_atm) {
            onShowNearbyATM();
        } else if (id == R.id.nav_share) {
            searchView.clearFocus();

        } else if (id == R.id.nav_send) {
            searchView.clearFocus();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private DatePickerDialog.OnDateSetListener updateDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            calendar.set(i, i1, i2);
            currentDate = sdf.format(calendar.getTime());
            FirebaseDatabase Database = FirebaseDatabase.getInstance();
            DatabaseReference reference = Database.getReference().child("Users").child(currentUserPhone);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().child("previousDonationDate").setValue(currentDate);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"please try again",Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private void onShowNearbyATM() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        nearbyService = retrofit.create(NearbyService.class);
        mGoogleMap.clear();
        showDirection.setText("Show Direction");
        String urlString = String.format("json?location=%f,%f&radius=%d&type=%s&key=%s",updateLat,updateLng,radius,typeATM,getResources().getString(R.string.google_maps_key));
        Call<NearbyPlaceResponse> nearbyPlaceResponseCall = nearbyService.getResponse(urlString);

        nearbyPlaceResponseCall.enqueue(new Callback<NearbyPlaceResponse>() {
            @Override
            public void onResponse(Call<NearbyPlaceResponse> call, Response<NearbyPlaceResponse> response) {
                if(response.code() == 200){
                    NearbyPlaceResponse nearbyPlaceResponse = response.body();
                    List<NearbyPlaceResponse.Result>results = nearbyPlaceResponse.getResults();
                    searchView.clearFocus();
                    for(int i = 0; i < results.size(); i++){
                        double lat = results.get(i).getGeometry().getLocation().getLat();
                        double lon = results.get(i).getGeometry().getLocation().getLng();
                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(results.get(i).getName())).showInfoWindow();
                    }
                }
                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        showDirection.setVisibility(View.VISIBLE);
                        end_latitude =marker.getPosition().latitude;
                        end_longitude = marker.getPosition().longitude;
                        showDirection.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showDirectionDrawPolyline();
                            }
                        });
                        return false;
                    }
                });
            }

            @Override
            public void onFailure(Call<NearbyPlaceResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onShowNearbyBank() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        nearbyService = retrofit.create(NearbyService.class);
        mGoogleMap.clear();
        showDirection.setText("Show Direction");
        String urlString = String.format("json?location=%f,%f&radius=%d&type=%s&key=%s",updateLat,updateLng,radius,typeBank,getResources().getString(R.string.google_maps_key));
        Call<NearbyPlaceResponse> nearbyPlaceResponseCall = nearbyService.getResponse(urlString);
        nearbyPlaceResponseCall.enqueue(new Callback<NearbyPlaceResponse>() {
            @Override
            public void onResponse(Call<NearbyPlaceResponse> call, Response<NearbyPlaceResponse> response) {
                if(response.code() == 200){
                    NearbyPlaceResponse nearbyPlaceResponse = response.body();
                    List<NearbyPlaceResponse.Result>results = nearbyPlaceResponse.getResults();
                    searchView.clearFocus();
                    for(int i = 0; i < results.size(); i++){
                        double lat = results.get(i).getGeometry().getLocation().getLat();
                        double lon = results.get(i).getGeometry().getLocation().getLng();
                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(results.get(i).getName()));
                    }
                    mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            showDirection.setVisibility(View.VISIBLE);
                            end_latitude =marker.getPosition().latitude;
                            end_longitude = marker.getPosition().longitude;
                            showDirection.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showDirectionDrawPolyline();
                                }
                            });
                            return false;
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<NearbyPlaceResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onShowNearbyResturent() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        nearbyService = retrofit.create(NearbyService.class);
        mGoogleMap.clear();
        showDirection.setText("Show Direction");
        String urlString = String.format("json?location=%f,%f&radius=%d&type=%s&key=%s",updateLat,updateLng,radius,typeResturent,getResources().getString(R.string.google_maps_key));
        Call<NearbyPlaceResponse> nearbyPlaceResponseCall = nearbyService.getResponse(urlString);
        nearbyPlaceResponseCall.enqueue(new Callback<NearbyPlaceResponse>() {
            @Override
            public void onResponse(Call<NearbyPlaceResponse> call, Response<NearbyPlaceResponse> response) {
                if(response.code() == 200){
                    NearbyPlaceResponse nearbyPlaceResponse = response.body();
                    List<NearbyPlaceResponse.Result>results = nearbyPlaceResponse.getResults();
                    searchView.clearFocus();
                    for(int i = 0; i < results.size(); i++){
                        double lat = results.get(i).getGeometry().getLocation().getLat();
                        double lon = results.get(i).getGeometry().getLocation().getLng();
                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(results.get(i).getName()));
                    }
                    mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            showDirection.setVisibility(View.VISIBLE);
                            end_latitude =marker.getPosition().latitude;
                            end_longitude = marker.getPosition().longitude;
                            showDirection.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showDirectionDrawPolyline();
                                }
                            });
                            return false;
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<NearbyPlaceResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onShowNearbyHospitals() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        nearbyService = retrofit.create(NearbyService.class);
        mGoogleMap.clear();
        showDirection.setText("Show Direction");
        String urlString = String.format("json?location=%f,%f&radius=%d&type=%s&key=%s",updateLat,updateLng,radius,typeHospital,getResources().getString(R.string.google_maps_key));
        Call<NearbyPlaceResponse> nearbyPlaceResponseCall = nearbyService.getResponse(urlString);
        nearbyPlaceResponseCall.enqueue(new Callback<NearbyPlaceResponse>() {
            @Override
            public void onResponse(Call<NearbyPlaceResponse> call, Response<NearbyPlaceResponse> response) {
                if(response.code() == 200){
                    NearbyPlaceResponse nearbyPlaceResponse = response.body();
                    List<NearbyPlaceResponse.Result>results = nearbyPlaceResponse.getResults();
                    searchView.clearFocus();
                    for(int i = 0; i < results.size(); i++){
                        double lat = results.get(i).getGeometry().getLocation().getLat();
                        double lon = results.get(i).getGeometry().getLocation().getLng();
                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(results.get(i).getName()));
                    }
                    mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            showDirection.setVisibility(View.VISIBLE);
                            end_latitude =marker.getPosition().latitude;
                            end_longitude = marker.getPosition().longitude;
                            showDirection.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showDirectionDrawPolyline();
                                }
                            });
                            return false;
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<NearbyPlaceResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                onMapSingleClicked();
                onMapLongClicked();

            } else {
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            onMapSingleClicked();
            onMapLongClicked();
        }
    }

    private void onMapSingleClicked() {
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(getCurrentFocus()!= null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                mGoogleMap.clear();
                searchView.clearFocus();
                showDirection.setVisibility(View.GONE);
            }
        });
    }

    private void onMapLongClicked() {
        mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mGoogleMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                mGoogleMap.addMarker(markerOptions.position(latLng));
                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        return false;

                    }
                });

                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String knownName = addresses.get(0).getFeatureName();

                    CustomDialogBuilder builder = new CustomDialogBuilder(MainActivity.this);
                    builder.setTitle("Show picked Address");
                    builder.setTitleColor("#FF00FF");
                    builder.setDividerColor("#FF00FF");
                    builder.setMessage(address+", "+city+", "+ state+", "+country+", "+", "+knownName);
                    builder.setPositiveButton("Ok", null);
                    builder.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * 60 * 3);
        mLocationRequest.setFastestInterval(1000 * 5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MainActivity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        else {
            updateLat = mLastLocation.getLatitude();
            updateLng = mLastLocation.getLongitude();
            LatLng latLng = new LatLng(updateLat, updateLng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
        }
    }

    private void showDirectionDrawPolyline(){

        Retrofit retrofitDir = new Retrofit.Builder()
                .baseUrl(BASE_URL_DIR)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        directionServiceInterface = retrofitDir.create(DirectionServiceInterface.class);
        mGoogleMap.clear();
        String dirUrlString = String.format("json?origin=%f,%f&destination=%f,%f&mode=%s&key=%s", updateLat, updateLng, end_latitude, end_longitude, dirMode, getResources().getString(R.string.google_maps_key));
        Call<DirectionResponse> directionResponseCall = directionServiceInterface.getResponse(dirUrlString);
        directionResponseCall.enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
                if (response.code() == 200){
                    DirectionResponse directionResponse = response.body();
                    List<DirectionResponse.Leg> legList = directionResponse.getRoutes().get(0).getLegs();
                    List<DirectionResponse.Step> stepList = legList.get(0).getSteps();
                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(end_latitude, end_longitude)));
                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(updateLat, updateLng)));
                    String distance = legList.get(0).getDistance().getText();
                    String duration = legList.get(0).getDuration().getText();
                    searchView.clearFocus();
                    for (int i = 0; i < stepList.size(); i++){
                        List<LatLng> pointList = PolyUtil.decode(stepList.get(i).getPolyline().getPoints());
                        PolylineOptions options = new PolylineOptions()
                                .addAll(pointList)
                                .color(Color.RED)
                                .width(15);
                        Polyline polyline = mGoogleMap.addPolyline(options);
                        polyline.setTag(duration);
                    }
                    showDirection.setText(distance+ "\n"+ duration);

                }
            }

            @Override
            public void onFailure(Call<DirectionResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"No internet connection", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION );

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }
    public static final int MY_PERMISSIONS_REQUEST_CALL = 100;
    public void checkCallPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {

                new AlertDialog.Builder(this)
                        .setTitle("Call Permission Needed")
                        .setMessage("This app needs the Call permission, please accept to use Call functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        MY_PERMISSIONS_REQUEST_CALL );
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL );
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CALL_PHONE)
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
