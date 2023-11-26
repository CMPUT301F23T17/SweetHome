package com.example.sweethome;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class WelcomeActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");
    private FirebaseAuth userAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_welcome);

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
                                DocumentSnapshot doc = q.getDocuments().get(0);
                                if (doc != null && doc.exists()) {
                                    User user = doc.toObject(User.class);
                                    assert user != null;
                                    String username = user.getUsername();
                                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                    intent.putExtra("USERNAME", username); //send username to main activity
                                    startActivity(intent);
                                    finish(); // Close the WelcomeActivity once the process is complete
                                } else { // if we cannot find the user for whatever reason also go to login activity
                                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish(); // Close the WelcomeActivity
                                }
                            }
                        }
                    });
        } else { // otherwise take them to the login activity
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the WelcomeActivity
        }
    }
}