package com.example.mycontacts;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;


public class search_contacts extends AppCompatActivity implements ItemClickListner {

    private TextView iv_mic, back_search;
    private EditText searchView;
    ArrayList<userinfo> arrayList;
    Adapter adapter;
    FirebaseUser firebaseUser;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);


        iv_mic = findViewById(R.id.iv_mic);
        back_search = findViewById(R.id.close_search);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.Search_list);

        arrayList = new ArrayList<>();
        adapter = new Adapter(arrayList, this);
        adapter.setClickListner(this);
        recyclerView.setAdapter(adapter);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        searchView.setFocusableInTouchMode(true);
        searchView.requestFocus();

        if (searchView.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }


        iv_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                    Toast
                            .makeText(search_contacts.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });


        back_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), list.class);
                startActivity(i);
                finish();
            }
        });


        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    arrayList.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    searchUser(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void searchUser(String userName) {
        arrayList.clear();
        DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference("ContactsList");
        Query userWiseQuery = contactRef.orderByChild("user_id").equalTo(firebaseUser.getUid());

        userWiseQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userinfo user = dataSnapshot.getValue(userinfo.class);
                    // Check if the user's name matches the search query
                    if (user.getName().toLowerCase().contains(userName.toLowerCase())) {
                        arrayList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }


    @Override
    public void onClick(View view, int pos) {
        userinfo u = arrayList.get(pos);
        Intent i = new Intent(getApplicationContext(), contactDetails.class);
        i.putExtra("node_id", u.getNode_id());
        startActivity(i);
        finish();
    }
}