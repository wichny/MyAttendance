package com.example.myattendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class AttendanceHistory extends AppCompatActivity implements View.OnClickListener {

    private CardView Checkininformation, Checkoutinformation;

    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);


        //defining cards
        Checkininformation = (CardView) findViewById(R.id.checkininformation);
        Checkoutinformation = (CardView) findViewById(R.id.checkoutinformation);

        //add click listener to the cards
        Checkininformation.setOnClickListener(this);
        Checkoutinformation.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.commonmenus,menu);
        return super.onCreateOptionsMenu(menu);
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

        Intent i;

        switch (v.getId()){
            case R.id.checkininformation : i =new Intent(this, ViewCheckinInfo.class);startActivity(i); break;
            case R.id.checkoutinformation : i =new Intent(this, ViewCheckoutInfo.class);startActivity(i); break;
            default:break;

        }
    }
}
