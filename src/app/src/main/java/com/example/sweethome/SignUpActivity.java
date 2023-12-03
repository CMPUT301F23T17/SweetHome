package com.example.sweethome;

/* necessary imports */
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.SpannableString;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * @class SignUpActivity
 *
 * <p>This class handles signing up a user to the app. New users
 * have unique usernames. </p>
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
public class SignUpActivity extends AppCompatActivity {
    /* attributes of this class */
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signUpButton;
    private FirebaseAuth userAuth;
    private Map<String, Object> userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Get the TextView for Login
        TextView textViewSignIn = findViewById(R.id.textViewLogin);
        /* initialize the EditTexts and button */
        emailEditText = findViewById(R.id.editTextEmailSignUp);
        usernameEditText = findViewById(R.id.editTextUsernameSignUp);
        passwordEditText = findViewById(R.id.editTextPasswordSignUp);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPasswordSignUp);
        signUpButton = findViewById(R.id.buttonSignUp);
        /* initialize firebase auth */
        userAuth = FirebaseAuth.getInstance();
        /* set up a connection to our db and a reference to the users collection */
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        // Underline the text "Login"
        String textSignIn = textViewSignIn.getText().toString();
        SpannableString spannableStringSignIn = new SpannableString(textSignIn);
        spannableStringSignIn.setSpan(new UnderlineSpan(), 0, textSignIn.length(), 0);
        textViewSignIn.setText(spannableStringSignIn);

        // Set the click listener for the Login TextView
        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start LoginActivity
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // If LoginActivity is already in the stack, bring it to front
                startActivity(intent);
            }
        });

        // Set the click listener for the Sign Up Button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable()) {
                    String email = emailEditText.getText().toString().trim();
                    String desiredUsername = usernameEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String confirmPassword = confirmPasswordEditText.getText().toString().trim();
                    // Call method to handle the sign-up process
                    attemptSignUp(email, desiredUsername, password, confirmPassword);
                } else {
                    Toast.makeText(SignUpActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
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
     * Given an email address, username and password, checks if they are valid
     * before trying to actually sign up the user (ie. meet all constraints like
     * username is unique or password should match confirmed password)
     * @param email
     * @param desiredUsername
     * @param password
     * @param confirmPassword
     */
    private void attemptSignUp(String email, String desiredUsername, String password, String confirmPassword) {

        /* check if the email contains any spaces or is empty */
        if (email.isEmpty() || email.contains(" ")) {
            emailEditText.setError("Email cannot be empty or contain spaces.");
            return;
        }
        // Check for empty or invalid username input, etc.
        if (desiredUsername.isEmpty() || desiredUsername.contains(" ")) {
            usernameEditText.setError("Username cannot be empty or contain spaces.");
            return;
        } else if (desiredUsername.length() > 25) {
            usernameEditText.setError("Username cannot be longer than 25 characters.");
            return;
        }
        /* check if the password contains any spaces or is empty */
        if (password.isEmpty() || password.contains(" ")) {
            passwordEditText.setError("Password cannot be empty or contain spaces.");
            return;
        }
        /* check that the passwords match */
        if (!confirmPassword.equals(password)) {
            confirmPasswordEditText.setError("Passwords do not match.");
            return;
        }
        // Check if the username is already taken
        usersRef.whereEqualTo("username", desiredUsername)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (task.getResult().isEmpty()) {
                            // Username is not taken, proceed with signup
                            createUserAccount(email, desiredUsername, password);
                        } else {
                            // Username is taken, prompt user to choose another
                            usernameEditText.setError("Username is already taken. Please choose a different one.");
                        }
                    } else {
                        // Handle errors here
                        Toast.makeText(SignUpActivity.this, "Error checking username availability.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Given an email address, username, and password, creates a new user account
     * and logs the new user into the app (starts the main activity and passes the
     * username into the main activity)
     * @param email
     * @param username
     * @param password
     */
    private void createUserAccount(String email, String username, String password) {
        userAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success
                            // Create a new user with a unique username
                            userInfo = new HashMap<>();
                            userInfo.put("username", username);
                            userInfo.put("email", email);
                            usersRef.add(userInfo);
                            Log.d("Firestore", "create new user:success");
                            Toast.makeText(SignUpActivity.this, "Account creation successful",Toast.LENGTH_SHORT).show();
                            // start the MainActivity
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.putExtra("USERNAME", username); //send username to main activity
                            startActivity(intent);
                            finish(); // Close the SignUpActivity once the process is complete
                        } else {
                            // If sign up fails, display a message to the user.
                            Log.w("Firestore", "create new user:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                emailEditText.setError("An account already exists with this email, please login instead.");
                            } else if (task.getException() instanceof FirebaseAuthWeakPasswordException){
                                passwordEditText.setError("Please choose a stronger password.");
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                emailEditText.setError("Invalid email address.");
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(SignUpActivity.this, "Account creation failed\n" + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}
