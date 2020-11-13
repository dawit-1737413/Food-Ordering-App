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
    Button sign_up, sign_in;
    ProgressBar progressBar;
    public static FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this method inflates the sign up page layout
        setContentView(R.layout.activity_signup);

        //initialising java objects
        et_first_name = (EditText) findViewById(R.id.et_first_name);
        et_last_name = (EditText) findViewById(R.id.et_last_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);

        sign_up = (Button) findViewById(R.id.btn_sign_up);
        sign_in = (Button) findViewById(R.id.btn_sign_in);
        progressBar = (ProgressBar)findViewById(R.id.progressBar1);
        //intialising the firebase authentication
        auth = FirebaseAuth.getInstance();

        //this method will trigger when the user clicks the sign up button
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            //getting the input from the user
            public void onClick(View view) {
                String first_name = et_first_name.getText().toString().trim();
                String last_name = et_last_name.getText().toString().trim();
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                //this will register the user with the values entered
                registerUser(first_name, last_name, email, password);
            }
        });
        //this method will trigger when the user clicks the sign in button and checks the credentials
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this is navigation, when the user selects sign in, we move from sign up page to sign in activity
                Intent intent = new Intent(SignupActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
    //this method will register the user to the firebase database
    private void registerUser(final String first_name, final String last_name, final String email, String password) {
        //this checks if anything has been entered in the field
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
        //this checks if the email format entered is correct
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Please provide valid email!");
            return;
        }
        //this checks if a password has been entered
        if (TextUtils.isEmpty(password)) {
            et_password.setError("Password is required!");
            return;
        }
        //this is a length check that at least 6 char has been entered
        if (password.length() < 6) {
            et_password.setError("Minimum Password length should be 6-character!");
            return;
        }
        //create a user and store user data in the firebase database
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //create hashmap to store user data
                            Map<String, Object> user = new HashMap<>();
                            user.put("first_name", first_name);
                            user.put("last_name", last_name);
                            user.put("email", email);

                            //use the firebase database object to store user data in the database
                            FirebaseDatabase.getInstance().getReference().child("Users").push().updateChildren(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //once successfully completed user will receive a confirmation messsage and be redirected to the next page
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignupActivity.this, "User registration is successful!", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.VISIBLE);
                                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                //if it fails user will be prompted with an error message and wont be able to continue to the next page
                                            } else {
                                                Toast.makeText(SignupActivity.this, "User registration failed 1, Please try again!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                            //if it fails user will be prompted with an error message and wont be able to continue to the next page
                            } else {
                            Toast.makeText(SignupActivity.this, "User registration failed 2, Please try again!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

}