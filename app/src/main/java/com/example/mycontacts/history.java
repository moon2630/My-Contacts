package com.example.mycontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class history extends AppCompatActivity {
    DatabaseReference database;


    TextView close55;
    Query query;
    FirebaseUser fuser;
    History_Adapter adapter;
    ArrayList<Call_History> alist;
    private String node_id;
    private FirebaseAuth auth;
    RecyclerView view;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        alist = new ArrayList<>();
        view = findViewById(R.id.contactRv);
        close55 = findViewById(R.id.close55);

        adapter = new History_Adapter(alist, this);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        view.setAdapter(adapter);

        database = FirebaseDatabase.getInstance().getReference("History");

        query = database.orderByChild("uid").equalTo(fuser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Call_History info = dataSnapshot.getValue(Call_History.class);
                    info.setNode_id(dataSnapshot.getKey());
                    alist.add(info);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        close55.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), list.class);
                startActivity(i);
                finish();
            }
        });

    }
}