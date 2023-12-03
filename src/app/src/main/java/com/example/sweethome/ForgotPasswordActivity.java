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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * @class ForgotPasswordActivity
 *
 * <p>This class handles the case where a user has forgotten their password
 * by sending a password reset email to an email address provided by the user.
 * The email is sent if it is linked to an existing account, otherwise no email
 * will be sent and the user will be informed that the account does not exist.</p>
 *
 * @date <p>December 1, 2023</p>
 *
 * @source <p>Code used in this class was adapted from the official Firebase
 * documentation. Manage Users in Firebase. The documentation was most recently
 * updated (2023, November 22). Firebase. The content of the documentation on
 * Firebase is licensed under the Creative Commons Attribution 4.0 License
 * and the code samples are licensed under the Apache 2.0 license.
 * @link https://firebase.google.com/docs/auth/android/manage-users#java_8. </p>
 */
public class ForgotPasswordActivity extends AppCompatActivity {
    /* attributes of this class */
    private EditText editTextSendEmail;
    private Button sendEmailButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        /* get the TextView for for going back to login */
        TextView textViewBackToLogin = findViewById(R.id.textViewBackToLogin);
        /* initialize the EditText and button */
        editTextSendEmail = findViewById(R.id.editTextSendEmail);
        sendEmailButton = findViewById(R.id.buttonSendEmail);

        // Underline the text "Login"
        String text = textViewBackToLogin.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        textViewBackToLogin.setText(spannableString);

        // Set the click listener for the Sign Up TextView
        textViewBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start LoginActivity
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close the ForgotPasswordActivity once the process is complete
            }
        });

        /* set the click listener for the send email button */
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable()) {
                    /* get the inputted email */
                    String email = editTextSendEmail.getText().toString().trim();
                    if (email.isEmpty()) { //check that the email is not empty
                        editTextSendEmail.setError("Email cannot be empty.");
                    } else { //otherwise try to send the password reset email
                        usersRef.whereEqualTo("email", email)
                            .limit(1)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot q = task.getResult();
                                    if (q != null) {
                                        List docs = q.getDocuments();
                                        if (docs.size() != 0 && docs.get(0) != null) {
                                            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> vtask) {
                                                        if (vtask.isSuccessful()) {
                                                            Log.d("Firestore", "password reset email sent:success");
                                                            Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                                                            // Start LoginActivity
                                                            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                                            startActivity(intent);
                                                            finish(); // Close the ForgotPasswordActivity
                                                        } else {
                                                            Log.w("Firestore", "password reset email sent:failure", task.getException());
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(ForgotPasswordActivity.this, "Password reset email could not be sent\n" + error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                        } else { // if we cannot find the email in our db
                                            editTextSendEmail.setError("Email is not linked to an existing account");
                                        }
                                    } else {
                                        editTextSendEmail.setError("Email is not linked to an existing account");
                                    }
                                } else {
                                    editTextSendEmail.setError("Email is not linked to an existing account");
                                }
                            });
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
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
}