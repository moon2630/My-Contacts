package com.example.mycontacts;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login2 extends Fragment {

    AppCompatButton btn;
    TextView txt;
    private FirebaseAuth auth;
    private FirebaseUser user;
    String email, password;
    EditText txt_email, txt_password;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login2, container, false);

        btn = view.findViewById(R.id.login_button);
        txt = view.findViewById(R.id.forgetPass);
        txt_email = view.findViewById(R.id.login_number);
        txt_password = view.findViewById(R.id.login_password);
        auth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = txt_email.getText().toString();
                password = txt_password.getText().toString();

                if(validInput(email,password)) {

                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getActivity(), list.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }
                    });
                }
            }
        });

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),forgetPassword.class);
                startActivity(intent);
            }
        });


        return view;


    }
    private boolean validInput(String email,String Password){

        if (TextUtils.isEmpty(email)) {
            txt_email.requestFocus();
            txt_email.setError("Email is required.");
            return false;
        } else if (!isValidEmail(email)) {
            txt_email.requestFocus();
            txt_email.setError("Invalid email format.");
            return false;
        }
        if (TextUtils.isEmpty(Password)) {
            txt_password.requestFocus();
            txt_password.setError("Password is required.");
            return false;
        } else if (Password.length()<=5) {
            txt_password.requestFocus();
            txt_password.setError("Password is Too short Min 6 Char.");
            return false;
        }

        return true;

    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}