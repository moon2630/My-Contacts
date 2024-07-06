package com.example.mycontacts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class list extends AppCompatActivity implements ItemClickListner{

    FloatingActionButton addContact, History, Logout, call, videoCall;
    ExtendedFloatingActionButton addAction;
    RecyclerView recyclerView;

    private TextView iv_mic, txtFilter;
    TextView addContactTxt, HistoryTxt, LogTxt, call_txt, videoCall_txt,txtUserId;
    DatabaseReference database;
    Query query;

    CardView searchCard;
    FirebaseUser fuser;
    Adapter adapter;
    ArrayList<userinfo> arrayList;
    ArrayList<userinfo> histories = new ArrayList<>();


    private FirebaseAuth auth;
    private ProgressBar progressBar;


    //To check whether sub FAB's are visible or not
    Boolean isAllFABVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        auth = FirebaseAuth.getInstance();


        searchCard = findViewById(R.id.searchCard);


        progressBar = findViewById(R.id.progressbar);
        addContact = findViewById(R.id.addContact);
        History = findViewById(R.id.history);
        call = findViewById(R.id.direct_call);
        addAction = findViewById(R.id.fab);
        videoCall = findViewById(R.id.videoCallFAB);
        Logout = findViewById(R.id.logout);

        recyclerView = findViewById(R.id.contact_list);
        progressBar.setVisibility(View.VISIBLE);


        addContactTxt = findViewById(R.id.alarm_text);
        HistoryTxt = findViewById(R.id.history_text);
        LogTxt = findViewById(R.id.logoutTxt);
        call_txt = findViewById(R.id.direct_call_txt);
        videoCall_txt = findViewById(R.id.videoCallTxt);
        txtUserId=findViewById(R.id.userID_list);


        //phela thi fab na badha btn nahi dekhay te mate jayre + btn par click kare tyre visible n gone thse
        addContact.setVisibility(View.GONE);
        addContactTxt.setVisibility(View.GONE);
        History.setVisibility(View.GONE);
        HistoryTxt.setVisibility(View.GONE);
        Logout.setVisibility(View.GONE);
        LogTxt.setVisibility(View.GONE);
        videoCall.setVisibility(View.GONE);
        videoCall_txt.setVisibility(View.GONE);
        call.setVisibility(View.GONE);
        call_txt.setVisibility(View.GONE);

        fuser = FirebaseAuth.getInstance().getCurrentUser();


        //now make the boolean boolean variable as false
        isAllFABVisible = false;

        //at first Ex FAb is set to shrink
        addAction.shrink();
        arrayList = new ArrayList<>();
        adapter = new Adapter(arrayList, this);
        adapter.setClickListner(this);
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance().getReference("ContactsList");
        query = database.orderByChild("user_id").equalTo(fuser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //histories.clear();
                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userinfo info = dataSnapshot.getValue(userinfo.class);
                    info.setNode_id(dataSnapshot.getKey());
                    arrayList.add(info);
                }
                adapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);

            }
        });


        //add click listener to the EX FAB"S

        addAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAllFABVisible) {
                    addContact.show();
                    History.show();
                    Logout.show();
                    call.show();
                    videoCall.show();
                    addContactTxt.setVisibility(View.VISIBLE);
                    HistoryTxt.setVisibility(View.VISIBLE);
                    LogTxt.setVisibility(View.VISIBLE);
                    call_txt.setVisibility(View.VISIBLE);
                    videoCall_txt.setVisibility(View.VISIBLE);

                    //now extend the EX fab
                    addAction.extend();

                    isAllFABVisible = true;


                } else {
                    //hide sub FAB's

                    addContact.hide();
                    History.hide();
                    Logout.hide();
                    call.hide();
                    videoCall.hide();
                    addContactTxt.setVisibility(View.GONE);
                    HistoryTxt.setVisibility(View.GONE);
                    LogTxt.setVisibility(View.GONE);
                    call_txt.setVisibility(View.GONE);
                    videoCall_txt.setVisibility(View.GONE);

                    //now extend the EX fab
                    addAction.extend();

                    isAllFABVisible = false;


                }
            }
        });

        displayUserName();


        searchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), search_contacts.class));

            }
        });

        //click listener on the sub FAB's
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(list.this, "You Can Create New Contact", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), createcontact.class));
                finish();

            }
        });


        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(list.this, "Video Calling", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), video_call.class));
                finish();

            }
        });

        History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(list.this, "Call History", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), history.class));

            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(list.this, "You Can log Out Your Account", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), login.class);
                startActivity(i);
                auth.signOut();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(list.this, "Direct call", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), directCall.class));
            }
        });



    }
    @Override
    public void onClick (View view,int pos){
        userinfo u = arrayList.get(pos);
        Intent i = new Intent(getApplicationContext(), contactDetails.class);
        i.putExtra("node_id", u.getNode_id());
        startActivity(i);
        finish();
    }

    public void displayUserName(){
        DatabaseReference userRef=FirebaseDatabase.getInstance().getReference("User");
        Query userQuery=userRef.orderByChild("user_Id").equalTo(fuser.getUid());
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot userSnapShot:snapshot.getChildren()) {
                        txtUserId.setText(userSnapShot.child("user_Name").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}






