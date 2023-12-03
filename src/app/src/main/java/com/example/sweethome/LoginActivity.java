package com.example.sweethome;

/* necessary imports */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * @class LoginActivity
 *
 * <p>This class handles logging in a user to the app. </p>
 *
 * @date <p>December 1, 2023</p>
 *
 * @source <p>Code used in this class was adapted from the official Firebase
 * documentation. Authenticate with Firebase using Password-Based Accounts on Android.
 * The documentation was most recently updated (2023, November 22). Firebase.
 * The content of the documentation on Firebase is licensed under the Creative
 * Commons Attribution 4.0 License and the code samples are licensed under the
 * Apache 2.0 license.
 * @link https://firebase.google.com/docs/auth/android/password-auth#java_2 </p>
 */
public class LoginActivity extends AppCompatActivity {
    /* attributes of this class */
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button loginButton;
    private FirebaseAuth userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get the TextView for Sign Up
        TextView textViewSignUp = findViewById(R.id.textViewSignUp);
        /* get the TextView for forgot password */
        TextView textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        // Initialize the EditTexts and Button
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        /* initialize firebase auth */
        userAuth = FirebaseAuth.getInstance();
        /* set up a connection to our db and a reference to the users collection */
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        String savedUsername = preferences.getString("username", "");
        if (!TextUtils.isEmpty(savedUsername)) {
            editTextUsername.setText(savedUsername);
        }

        // Underline the text "Sign Up"
        String text = textViewSignUp.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        textViewSignUp.setText(spannableString);

        // Set the click listener for the Sign Up TextView
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable()) {
                    // Start SignUpActivity
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    finish(); // Close the LoginActivity once the process is complete
                } else {
                    Toast.makeText(LoginActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* set the on click listener for the forgot password TextView */
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable()) {
                    /* start ForgotPasswordActivity */
                    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                    finish(); //close the LoginActivity
                } else {
                    Toast.makeText(LoginActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set the click listener for the Login Button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable()) {
                    String enteredUsername = editTextUsername.getText().toString().trim();
                    String password = editTextPassword.getText().toString();
                    // Call method to handle the login process
                    attemptLogin(enteredUsername, password);
                } else {
                    Toast.makeText(LoginActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    /**
     * Given a username and password, checks if they are valid before trying to
     * actually sign in (ie. meet all constraints like cannot be empty)
     * @param enteredUsername
     * @param password
     */
    private void attemptLogin(String enteredUsername, String password) {
        // Check for empty username input
        if (enteredUsername.isEmpty()) {
            editTextUsername.setError("Username cannot be empty.");
            return;
        } else if (enteredUsername.length() > 25) {
            editTextUsername.setError("Username cannot be longer than 25 characters.");
            return;
        }
        /* check if the password is empty */
        if (password.trim().isEmpty()) {
            editTextPassword.setError("Password cannot be empty.");
            return;
        }

        usersRef.whereEqualTo("username", enteredUsername)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot q = task.getResult();
                        if (q != null) {
                            List docs = q.getDocuments();
                            if (docs.size() != 0 && q.getDocuments().get(0) != null && q.getDocuments().get(0).exists()) {
                                // Username exists, now actually try to sign the user into their account
                                DocumentSnapshot doc = q.getDocuments().get(0);
                                User user = doc.toObject(User.class);
                                if (user != null) {
                                    String email = user.getEmail();
                                    loginToUserAccount(email, enteredUsername, password);
                                } else { // Username does not exist
                                    editTextUsername.setError("Username does not exist. Please sign up.");
                                }
                            } else { // Username does not exist
                                editTextUsername.setError("Username does not exist. Please sign up.");
                            }
                        } else { // Username does not exist
                            editTextUsername.setError("Username does not exist. Please sign up.");
                        }
                    } else {
                        // Handle errors here
                        Toast.makeText(LoginActivity.this, "Error checking username.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Given an email address, username, and password, logs a user into the app and
     * starts the main activity (passing the username into the main activity)
     * @param email
     * @param username
     * @param password
     */
    private void loginToUserAccount(String email, String username, String password) {
        userAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d("Firestore", "user login:success");
                            Toast.makeText(LoginActivity.this, "Login successful",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("USERNAME", username); //send username to main activity
                            startActivity(intent);
                            finish(); // Close the LoginActivity once the process is complete
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Firestore", "user login:failure", task.getException());
                            editTextUsername.setError("Incorrect username or password.");
                            editTextPassword.setError("Incorrect username or password.");
                            Toast.makeText(LoginActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
