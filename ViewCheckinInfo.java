package com.example.myattendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;



public class ViewCheckinInfo extends AppCompatActivity implements View.OnClickListener {

    private ListView mylistView;
    private DatabaseReference myRef;
    ArrayList<String> myArrayList = new ArrayList<>();
    private FirebaseDatabase mFirebaseDatabase;
    ArrayAdapter<String> adapter;

    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_checkin_info);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);

        mylistView = (ListView)findViewById(R.id.listview);

        DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mFirebaseDatabase.child("Attendance").child("Check-In");
        myArrayList = new ArrayList<>();
        final ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myArrayList);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String email = ds.child("Email").getValue(String.class);
                    String Date = ds.child("Date").getValue(String.class);
                    Double Latitude = ds.child("Latitude").getValue(Double.class);
                    Double Longitude = ds.child("Longitude").getValue(Double.class);
                    myArrayList.add(Date + " \n" + email + " \n" + Latitude + " \n " + Longitude);
                }
                mylistView.setAdapter(myArrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        myRef.addListenerForSingleValueEvent(eventListener);
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
    public void onClick(View v) {

        //if logout is pressed
        if(v == buttonLogout){

            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}

