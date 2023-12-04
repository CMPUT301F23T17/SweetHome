package com.example.sweethome;

/* necessary imports */

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * @class WelcomeActivity
 *
 * <p>This class serves as an entry point to the SweetHome application.
 * It checks whether a user is currently signed in and takes them to the
 * main screen if so, otherwise it takes them to the login page. </p>
 *
 * @date <p>December 1, 2023</p>
 */
public class WelcomeActivity extends AppCompatActivity {
    /* attributes of this class */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");
    private FirebaseAuth userAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is signed in (non-null)
        FirebaseUser currentUser = userAuth.getCurrentUser();
        if(currentUser != null) {
            String email = currentUser.getEmail();
            usersRef.whereEqualTo("email", email)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> { // and if so take them to the main activity
                        if (task.isSuccessful()) {
                            QuerySnapshot q = task.getResult();
                            if (q != null) {
                                List docs = q.getDocuments();
                                if (docs.size() != 0 && q.getDocuments().get(0) != null && q.getDocuments().get(0).exists()) {
                                    // user exists, take them to their main page
                                    DocumentSnapshot doc = q.getDocuments().get(0);
                                    User user = doc.toObject(User.class);
                                    if (user != null) {
                                        String username = user.getUsername();
                                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                        intent.putExtra("USERNAME", username); //send username to main activity
                                        startActivity(intent);
                                        finish(); // Close the WelcomeActivity once the process is complete
                                    } else { // if we cannot find the user for whatever reason also go to login activity
                                        goToLogin();
                                    }
                                } else { // if we cannot find the user for whatever reason also go to login activity
                                    goToLogin();
                                }
                            } else { // if we cannot find the user for whatever reason also go to login activity
                                goToLogin();
                            }
                        } else { // if we cannot find the user for whatever reason also go to login activity
                            goToLogin();
                        }
                    });
        } else { // otherwise take them to the login activity
            goToLogin();
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close the WelcomeActivity
    }
}