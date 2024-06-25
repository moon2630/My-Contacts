package com.example.mycontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.List;

public class forgetPassword extends AppCompatActivity {

    AppCompatButton btn_next;
    EditText txt_email;
    private String email;
    private FirebaseAuth auth;
    ProgressDialog dialog;


    Animation D1,D2;

    TextView txtTop,txtBottom,back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);


        back = findViewById(R.id.back_To_login);

        txtTop = findViewById(R.id.designTop);
        txtBottom = findViewById(R.id.designBottom);
        btn_next = findViewById(R.id.getemail);
        txt_email = findViewById(R.id.textemail);
        dialog = new ProgressDialog(this);
        Intent i = getIntent();



        D1 = AnimationUtils.loadAnimation(this,R.anim.middle2);
        txtTop.setAnimation(D1);
        D2 = AnimationUtils.loadAnimation(this,R.anim.middle2);
        txtBottom.setAnimation(D2);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = txt_email.getText().toString();
                if (validInput(email)) {
                    checkEmail(email);
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), login2.class);
                startActivity(i);
            }
        });

    }

    /*
          checkEmail method will check the user email is registered or not
          if it will register then it will send email otherwise show error massage.
     */
    public void checkEmail(String email) {
        dialog.setMessage("Sending Email . . .");
        dialog.setCancelable(false);
        dialog.show();
        auth = FirebaseAuth.getInstance();

        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    SignInMethodQueryResult result = task.getResult();
                    List<String> signInMethods = result.getSignInMethods();

                    if (signInMethods != null && !signInMethods.isEmpty()) {
                        // Email is already registered
                        // You can handle this case, e.g., show an error message
                        auth.sendPasswordResetEmail(email);
                        dialog.dismiss();
                        Toast.makeText(forgetPassword.this, "Please Check Your Email", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), login2.class));
                        finish();

                    } else {
                        // Email is not registered
                        dialog.dismiss();
                        txt_email.requestFocus();
                        txt_email.setError("Email is not Registered");
                    }
                } else {
                    // Error occurred, handle the error
                    Exception exception = task.getException();
                    if (exception != null) {
                        // Handle the error, e.g., show an error message
                    }
                }
            }
        });

    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validInput(String email) {

        if (TextUtils.isEmpty(email)) {
            txt_email.requestFocus();
            txt_email.setError("Email is required.");
            return false;
        } else if (!isValidEmail(email)) {
            txt_email.requestFocus();
            txt_email.setError("Invalid email format.");
            return false;
        }
        return true;
    }
}