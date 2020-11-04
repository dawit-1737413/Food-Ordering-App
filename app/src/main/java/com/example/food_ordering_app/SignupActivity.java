package com.example.food_ordering_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    EditText et_first_name, et_last_name, et_email, et_password;
    Button sign_up;
    ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_first_name = (EditText) findViewById(R.id.et_first_name);
        et_last_name = (EditText) findViewById(R.id.et_last_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);

        sign_up = (Button) findViewById(R.id.btn_sign_up);
        progressBar = (ProgressBar)findViewById(R.id.progressBar1);
        auth = FirebaseAuth.getInstance();

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String first_name = et_first_name.getText().toString().trim();
                String last_name = et_last_name.getText().toString().trim();
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                registerUser(first_name, last_name, email, password);
            }
        });

    }

    private void registerUser(final String first_name, final String last_name, final String email, String password) {
        if (TextUtils.isEmpty(first_name)) {
            et_first_name.setError("First Name is required!");
            return;
        }
        if (TextUtils.isEmpty(last_name)) {
            et_last_name.setError("Last Name is required!");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            et_email.setError("Email is required!");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Please provide valid email!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            et_password.setError("Password is required!");
            return;
        }
        if (password.length() < 6) {
            et_password.setError("Minimum Password length should be 6-character!");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> user = new HashMap<>();
                            user.put("fist_name", first_name);
                            user.put("last_name", last_name);
                            user.put("email", email);

                            FirebaseDatabase.getInstance().getReference().child("Users").push().updateChildren(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignupActivity.this, "User registration is successful!", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.VISIBLE);
                                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(SignupActivity.this, "User registration failed, Please try agian!", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(SignupActivity.this, "User registration failed, Please try agian!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

}