package com.example.mycontacts;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class signup extends Fragment {

    AppCompatButton btn;
    private String email, name, password;
    EditText txt_email, txt_name, txt_password, txt_confirm_password;
    private FirebaseAuth auth;
    private DatabaseReference database;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        btn = view.findViewById(R.id.signup_button);
        txt_email = view.findViewById(R.id.signup_number);
        txt_name = view.findViewById(R.id.signup_email);
        txt_confirm_password = view.findViewById(R.id.signup_confirm);
        txt_password = view.findViewById(R.id.signup_password);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("User");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = txt_name.getText().toString();
                email = txt_email.getText().toString();
                password = txt_confirm_password.getText().toString();

                if (validInput(name, email, password)) {

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user u = new user();
                                u.setUser_Email(email);
                                u.setUser_Name(name);
                                u.setUser_Id(auth.getUid());
                                AddUser(u);
                                Intent intent = new Intent(getActivity(), list.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }
                    });
                }
            }
        });
        return view;
    }

    private void AddUser(user u) {

        database.push().setValue(u);

    }

    private boolean validInput(String name, String email, String Password) {

        if (TextUtils.isEmpty(email)) {
            txt_email.setError("Email is required.");
            return false;
        } else if (!isValidEmail(email)) {
            txt_email.setError("Invalid email format.");
            return false;
        }
        if (TextUtils.isEmpty(Password)) {
            txt_confirm_password.setError("Password is required.");
            return false;
        }
        if (!txt_password.getText().toString().equals(password)) {
            txt_confirm_password.setError("Password Is not same ");
            return false;
        }
        if (txt_password.getText().toString().length() <= 5) {
            txt_password.requestFocus();
            txt_password.setError("Password is Too short Min 6 Char.");
            return false;
        }

        if (TextUtils.isEmpty(txt_password.getText().toString())) {
            txt_password.setError("Password is required.");
            return false;
        }

        if (TextUtils.isEmpty(name)) {
            txt_name.setError("Name is required.");
            return false;
        }
        return true;

    }

    private boolean isValidEmail(String email) {
        //@.gmail.com permission is complusory
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}