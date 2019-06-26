package com.example.myattendanceapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CheckOut extends AppCompatActivity implements View.OnClickListener{

    String timer[] = {"Select time", "8 am", "12 pm", "5 pm"};
    String tim;
    Button mLocationBtn;
    TextView mText, DisplayDateTime;
    GPS_Service gps;
    Geocoder geocoder;
    List<Address> addresses;

    Calendar calendar;
    SimpleDateFormat simpledateformat;
    String Date;

    //Firebase Work
    DatabaseReference mDatabaseLocationDetails;

    private Button buttonCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        geocoder = new Geocoder(this, Locale.getDefault());

        DisplayDateTime = (TextView) findViewById(R.id.textView);
        mText = (TextView) findViewById(R.id.location_tv);
        Spinner mSpinTime = (Spinner) findViewById(R.id.spinner_time);
        mLocationBtn = (Button) findViewById(R.id.location_btn);
        mDatabaseLocationDetails = FirebaseDatabase.getInstance().getReference().child("Attendance").child("Check-Out").push();

        buttonCheckout = (Button) findViewById(R.id.buttonCheckout);
        buttonCheckout.setOnClickListener(this);

//      permission check
        if (!runtime_permission())
            enable_button();
        runtime_permission();


        mSpinTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                tim = adapterView.getItemAtPosition(i).toString();
                if (tim.equals("Select time")) {
                    Toast.makeText(CheckOut.this, "Please Select Session!", Toast.LENGTH_SHORT).show();
                }
                if (tim == "8 am") {
                    tim = String.valueOf(tim.charAt(0));
                    Toast.makeText(CheckOut.this, tim + "", Toast.LENGTH_SHORT).show();
                }
                if (tim == "12 pm") {
                    tim = tim.substring(0, 2);
                    Toast.makeText(CheckOut.this, tim + "", Toast.LENGTH_SHORT).show();
                }
                if (tim == "5 pm") {
                    tim = tim.substring(0, 2);
                    Toast.makeText(CheckOut.this, tim + "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                tim = String.valueOf(0);
            }
        });

        ArrayAdapter arrayAdapterCity = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, timer);
        arrayAdapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinTime.setAdapter(arrayAdapterCity);
    }

    private void enable_button() {

        calendar = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date = simpledateformat.format(calendar.getTime());



        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gps = new GPS_Service(CheckOut.this, tim);
                startService(new Intent(CheckOut.this, GPS_Service.class));
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    String Date = simpledateformat.format(calendar.getTime());
                    String email = user.getEmail();
                    String uid = user.getUid();
                    storeInDatabase(latitude, longitude, Date, email, uid);

                    try{
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);

                        String address = addresses.get(0).getAddressLine(0);
                        String area = addresses.get(0).getLocality();
                        String city = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalcode = addresses.get(0).getPostalCode();

                        String fullAddress = address + ", " + area + ", " + city + ", " + country + ", " + postalcode;

                        mText.setText(fullAddress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    gps.showSettingsAlert();
                }

                DisplayDateTime.setText(Date);


            }
        });


    }

    private void storeInDatabase(double latitude, double longitude, String Date, String email, String uid) {


        mDatabaseLocationDetails.child("Latitude").setValue(latitude);
        mDatabaseLocationDetails.child("Longitude").setValue(longitude);
        mDatabaseLocationDetails.child("Date").setValue(Date);
        mDatabaseLocationDetails.child("Email").setValue(email);
        mDatabaseLocationDetails.child("Uid").setValue(uid);
    }

    private boolean runtime_permission() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(CheckOut.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(CheckOut.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.mnuShare)
        {
            Toast.makeText(this, "Main Menu", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ProfileActivity.class));

        }
        else if(id==R.id.mnuAttach)
        {
            Toast.makeText(this, "LogOut", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                enable_button();
            } else {
                runtime_permission();
            }
        }
    }

    @Override
    public void onClick(View v) {
        //if logout is pressed
        if(v == buttonCheckout){

            startActivity(new Intent(this, ProfileActivity.class));
        }

    }
}
