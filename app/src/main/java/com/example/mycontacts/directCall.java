package com.example.mycontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class directCall extends AppCompatActivity {

    TextView back;


    ImageButton btn;
    EditText text;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_call);


        btn = findViewById(R.id.call);
        text = findViewById(R.id.number);
        back = findViewById(R.id.details_back22);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_the_number();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), list.class);
                startActivity(i);
                finish();

            }
        });
    }


    private void call_the_number() {
        userid = FirebaseAuth.getInstance().getUid();
        String number = text.getText().toString().trim();
        Call_History history = new Call_History();
        history.setUid(userid);
        history.setC_name(number);

        //admin
        if (ContextCompat.checkSelfPermission(directCall.this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {

            //user
            ActivityCompat.requestPermissions(directCall.this, new String[]{Manifest.permission.CALL_PHONE}, 1234);

        } else {

            //number lkhelo hoy to if block ma ane nai lkhelo hoy to else ma jase
            if (number.length() > 0) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy  hh:mm aaa");
                String dateTime = simpleDateFormat.format(calendar.getTime()).toString();
                history.setDate_time(String.valueOf(dateTime));

                databaseReference = FirebaseDatabase.getInstance().getReference("History");
                databaseReference.push().setValue(history);
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                startActivity(intent);


            } else {
                text.setError("please find number");
            }
        }
    }
}