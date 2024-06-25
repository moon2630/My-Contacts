package com.example.mycontacts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class contactDetails extends AppCompatActivity {
    private TextView txt_contact_name, txt_contact_email, txt_contact_note, txt_contact_number, txt_delete, back, txt_update;
    CircleImageView img_contact;
    DatabaseReference database;
    String phone;
    private static final int REQUEST_CALL = 1;
    private userinfo u;
    private TextView callText;
    private RelativeLayout callBtn, msgBtn;
    private String node_id;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);


        msgBtn = findViewById(R.id.msg_btn);
        txt_contact_number = findViewById(R.id.details_number);
        img_contact = findViewById(R.id.details_image);
        txt_contact_name = findViewById(R.id.details_name);

        callText = findViewById(R.id.details_number);

        txt_contact_email = findViewById(R.id.details_email);
        txt_contact_note = findViewById(R.id.details_note);
        txt_delete = findViewById(R.id.details_delete);
        callBtn = findViewById(R.id.call_btn);
        back = findViewById(R.id.details_back);
        txt_update = findViewById(R.id.details_update);


        txt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showWarningDialog();

            }
        });

        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = callText.getText().toString();

                // Create intent with ACTION_SENDTO and smsto: URI
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(Uri.parse("smsto:" + Uri.encode(phone)));

                // Set the initial text message body
                sendIntent.putExtra("sms_body", "");

                try {
                    // Start the activity to send the SMS
                    startActivity(sendIntent);
                } catch (ActivityNotFoundException e) {
                    // Handle the case where no activity can handle the intent
                    Toast.makeText(getApplicationContext(), "No SMS app found.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent i = getIntent();
        node_id = i.getStringExtra("node_id");
        database = FirebaseDatabase.getInstance().getReference("ContactsList");
        Query query = database.child(node_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userinfo u = snapshot.getValue(userinfo.class);
                txt_contact_name.setText(u.getName());
                txt_contact_email.setText(u.getEmail());
                txt_contact_number.setText(u.getNumber());
                txt_contact_note.setText(u.getNote());
                Glide.with(getApplicationContext()).load(u.getImageurl()).into(img_contact);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //method call karavi
                CallButton();

                database = FirebaseDatabase.getInstance().getReference("ContactsList");
                Query query = database.child(node_id);


                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userinfo u = snapshot.getValue(userinfo.class);
                        Call_History history = new Call_History();
                        history.setUid(u.getUser_id());
                        history.setC_name(u.getName());
                        history.setCc_image(u.getImageurl());
                        AddHistory(history);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), list.class);
                startActivity(i);
            }
        });

        txt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), editContact.class);
                i.putExtra("node_id", node_id);
                startActivity(i);
                finish();
            }
        });
    }

    public void AddHistory(Call_History history) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy hh:mm aaa");
        String dateTime = simpleDateFormat.format(calendar.getTime()).toString();
        history.setDate_time(String.valueOf(dateTime));

        DatabaseReference databasehistory = FirebaseDatabase.getInstance().getReference("History");
        databasehistory.push().setValue(history);
    }

    private void CallButton() {
        //je number save karelo hse te j call thse
        String number = callText.getText().toString();
        //0 thi vadhare number hase to j call karse
        if (number.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(contactDetails.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(contactDetails.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            //number jo 0 karta vadhare hoy to permission granted thse
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //method call karavi uper ni
                CallButton();
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showWarningDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (contactDetails.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(contactDetails.this).inflate(
                R.layout.layout_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Delete Contact?");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("This contact will be permanently deleted from your device");
        ((AppCompatButton) view.findViewById(R.id.buttonYes))
                .setText("Cancel");
        ((AppCompatButton) view.findViewById(R.id.buttonNo))
                .setText("Delete");
        ((ImageView) view.findViewById(R.id.imageIcon))
                .setImageResource(R.drawable.warning);
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                Toast.makeText(contactDetails.this,
                        "Contact is not deleted", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                DatabaseReference delete = FirebaseDatabase.getInstance().getReference("ContactsList").child(node_id);
                delete.removeValue();
                startActivity(new Intent(getApplicationContext(), list.class));

                Toast.makeText(contactDetails.this,
                        "Contact is deleted", Toast.LENGTH_SHORT).show();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

}
