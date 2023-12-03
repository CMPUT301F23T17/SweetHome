package com.example.sweethome;

/* necessary imports */
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.ComponentName;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @class SignUpTest
 * <p>This class tests signing up a user </p>
 *
 * @date <p>December 1, 2023</p>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignUpTest {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");
    private FirebaseAuth userAuth = FirebaseAuth.getInstance();

    @Rule
    public ActivityScenarioRule<SignUpActivity> scenario=new ActivityScenarioRule<SignUpActivity>(SignUpActivity.class);

    @Before
    public void before() {
        Intents.init();
    }

    @Test
    public void testGoToLogin() {
        /* click the words "Login" (as in "Already have an account? Login" on Sign Up page) */
        onView(withId(R.id.textViewLogin)).perform(click());
        /* check if the sign up activity is launched */
        intended(hasComponent(new ComponentName(getApplicationContext(), LoginActivity.class)));
    }

    @Test
    public void testGoToLoginAndBack() {
        /* click the words "Login" (as in "Already have an account? Login" on Sign Up page) */
        onView(withId(R.id.textViewLogin)).perform(click());
        /* click the words "Sign Up" (as in "Don't have an account? Sign Up" on Login page) */
        onView(withId(R.id.textViewSignUp)).perform(click());
        /* check if we went back to the sign up activity by checking if the sign up button is displayed */
        onView(withId(R.id.buttonSignUp)).check(matches(isDisplayed()));
    }

    @Test
    public void testSignUpWithEmptyEmail() {
        /* click the username field edit text and put in a random username */
        onView(withId(R.id.editTextUsernameSignUp)).perform(click());
        onView(withId(R.id.editTextUsernameSignUp)).perform(ViewActions.typeText("randomUsername"));
        /* click the password field edit text and put in a random password */
        onView(withId(R.id.editTextPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the confirm password field edit text and put in a random password */
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the sign up button */
        onView(withId(R.id.buttonSignUp)).perform(click());
        /* check that we get the expected error (email is empty) */
        onView(withId(R.id.editTextEmailSignUp)).check(matches(hasErrorText("Email cannot be empty or contain spaces.")));
    }

    @Test
    public void testSignUpWithSpacesInEmail() {
        /* click the email field edit text and put in a random email with a space */
        onView(withId(R.id.editTextEmailSignUp)).perform(click());
        onView(withId(R.id.editTextEmailSignUp)).perform(ViewActions.typeText("random email@randomemail.com"));
        /* click the username field edit text and put in a random username */
        onView(withId(R.id.editTextUsernameSignUp)).perform(click());
        onView(withId(R.id.editTextUsernameSignUp)).perform(ViewActions.typeText("randomUsername"));
        /* click the password field edit text and put in a random password */
        onView(withId(R.id.editTextPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the confirm password field edit text and put in a random password */
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the sign up button */
        onView(withId(R.id.buttonSignUp)).perform(click());
        /* check that we get the expected error (email has space) */
        onView(withId(R.id.editTextEmailSignUp)).check(matches(hasErrorText("Email cannot be empty or contain spaces.")));
    }

    @Test
    public void testSignUpWithEmptyUsername() {
        /* click the email field edit text and put in a random email */
        onView(withId(R.id.editTextEmailSignUp)).perform(click());
        onView(withId(R.id.editTextEmailSignUp)).perform(ViewActions.typeText("randomemail@randomemail.com"));
        /* click the password field edit text and put in a random password */
        onView(withId(R.id.editTextPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the confirm password field edit text and put in a random password */
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the sign up button */
        onView(withId(R.id.buttonSignUp)).perform(click());
        /* check that we get the expected error (username is empty) */
        onView(withId(R.id.editTextUsernameSignUp)).check(matches(hasErrorText("Username cannot be empty or contain spaces.")));
    }

    @Test
    public void testSignUpWithSpacesInUsername() {
        /* click the email field edit text and put in a random email */
        onView(withId(R.id.editTextEmailSignUp)).perform(click());
        onView(withId(R.id.editTextEmailSignUp)).perform(ViewActions.typeText("randomemail@randomemail.com"));
        /* click the username field edit text and put in a random username with a space */
        onView(withId(R.id.editTextUsernameSignUp)).perform(click());
        onView(withId(R.id.editTextUsernameSignUp)).perform(ViewActions.typeText("random Username"));
        /* click the password field edit text and put in a random password */
        onView(withId(R.id.editTextPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the confirm password field edit text and put in a random password */
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the sign up button */
        onView(withId(R.id.buttonSignUp)).perform(click());
        /* check that we get the expected error (username has space) */
        onView(withId(R.id.editTextUsernameSignUp)).check(matches(hasErrorText("Username cannot be empty or contain spaces.")));
    }

    @Test
    public void testSignUpWithEmptyPassword() {
        /* click the email field edit text and put in a random email */
        onView(withId(R.id.editTextEmailSignUp)).perform(click());
        onView(withId(R.id.editTextEmailSignUp)).perform(ViewActions.typeText("randomemail@randomemail.com"));
        /* click the username field edit text and put in a random username */
        onView(withId(R.id.editTextUsernameSignUp)).perform(click());
        onView(withId(R.id.editTextUsernameSignUp)).perform(ViewActions.typeText("randomUsername"));
        /* click the confirm password field edit text and put in a random password */
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the sign up button */
        onView(withId(R.id.buttonSignUp)).perform(click());
        /* check that we get the expected error (password is empty) */
        onView(withId(R.id.editTextPasswordSignUp)).check(matches(hasErrorText("Password cannot be empty or contain spaces.")));
    }

    @Test
    public void testSignUpWithSpacesInPassword() {
        /* click the email field edit text and put in a random email */
        onView(withId(R.id.editTextEmailSignUp)).perform(click());
        onView(withId(R.id.editTextEmailSignUp)).perform(ViewActions.typeText("randomemail@randomemail.com"));
        /* click the username field edit text and put in a random username */
        onView(withId(R.id.editTextUsernameSignUp)).perform(click());
        onView(withId(R.id.editTextUsernameSignUp)).perform(ViewActions.typeText("randomUsername"));
        /* click the password field edit text and put in a random password with a space */
        onView(withId(R.id.editTextPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextPasswordSignUp)).perform(ViewActions.typeText("random Password"));
        /* click the confirm password field edit text and put in a random password with a space */
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(ViewActions.typeText("random Password"));
        /* click the sign up button */
        onView(withId(R.id.buttonSignUp)).perform(click());
        /* check that we get the expected error (password has space) */
        onView(withId(R.id.editTextPasswordSignUp)).check(matches(hasErrorText("Password cannot be empty or contain spaces.")));
    }

    @Test
    public void testSignUpWithMismatchedPasswords() {
        /* click the email field edit text and put in a random email */
        onView(withId(R.id.editTextEmailSignUp)).perform(click());
        onView(withId(R.id.editTextEmailSignUp)).perform(ViewActions.typeText("randomemail@randomemail.com"));
        /* click the username field edit text and put in a random username */
        onView(withId(R.id.editTextUsernameSignUp)).perform(click());
        onView(withId(R.id.editTextUsernameSignUp)).perform(ViewActions.typeText("randomUsername"));
        /* click the password field edit text and put in a random password */
        onView(withId(R.id.editTextPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the confirm password field edit text and put in a different random password */
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(ViewActions.typeText("randomPassword2"));
        /* click the sign up button */
        onView(withId(R.id.buttonSignUp)).perform(click());
        /* check that we get the expected error (passwords do not match) */
        onView(withId(R.id.editTextConfirmPasswordSignUp)).check(matches(hasErrorText("Passwords do not match.")));
    }

    @Test
    public void testSignUpWithExistingUsername() throws InterruptedException {
        /* click the email field edit text and put in a random email */
        onView(withId(R.id.editTextEmailSignUp)).perform(click());
        onView(withId(R.id.editTextEmailSignUp)).perform(ViewActions.typeText("randomemail@randomemail.com"));
        /* click the username field edit text and put in an existing username */
        onView(withId(R.id.editTextUsernameSignUp)).perform(click());
        onView(withId(R.id.editTextUsernameSignUp)).perform(ViewActions.typeText("logintest"));
        /* click the password field edit text and put in a random password */
        onView(withId(R.id.editTextPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the confirm password field edit text and put in a random password */
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the sign up button */
        onView(withId(R.id.buttonSignUp)).perform(click());
        Thread.sleep(3000);
        /* check that we get the expected error (username taken already) */
        onView(withId(R.id.editTextUsernameSignUp)).check(matches(hasErrorText("Username is already taken. Please choose a different one.")));
    }

    @Test
    public void testSignUpWithExistingEmail() throws InterruptedException {
        /* click the email field edit text and put in an existing email */
        onView(withId(R.id.editTextEmailSignUp)).perform(click());
        onView(withId(R.id.editTextEmailSignUp)).perform(ViewActions.typeText("logintest@logintest.ca"));
        /* click the username field edit text and put in a random username */
        onView(withId(R.id.editTextUsernameSignUp)).perform(click());
        onView(withId(R.id.editTextUsernameSignUp)).perform(ViewActions.typeText("randomUsername"));
        /* click the password field edit text and put in a random password */
        onView(withId(R.id.editTextPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the confirm password field edit text and put in a random password */
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the sign up button */
        onView(withId(R.id.buttonSignUp)).perform(click());
        Thread.sleep(3000);
        /* check that we get the expected error (account already exists) */
        onView(withId(R.id.editTextEmailSignUp)).check(matches(hasErrorText("An account already exists with this email, please login instead.")));
    }

    @Test
    public void testSignUpWithInvalidEmail() throws InterruptedException {
        /* click the email field edit text and put in a random email with no "@" */
        onView(withId(R.id.editTextEmailSignUp)).perform(click());
        onView(withId(R.id.editTextEmailSignUp)).perform(ViewActions.typeText("randomemailrandomemail.com"));
        /* click the username field edit text and put in a random username */
        onView(withId(R.id.editTextUsernameSignUp)).perform(click());
        onView(withId(R.id.editTextUsernameSignUp)).perform(ViewActions.typeText("randomUsername"));
        /* click the password field edit text and put in a random password */
        onView(withId(R.id.editTextPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the confirm password field edit text and put in a random password */
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(ViewActions.typeText("randomPassword"));
        /* click the sign up button */
        onView(withId(R.id.buttonSignUp)).perform(click());
        Thread.sleep(3000);
        /* check that we get the expected error (invalid email) */
        onView(withId(R.id.editTextEmailSignUp)).check(matches(hasErrorText("Invalid email address.")));
    }

    @Test
    public void testSignUpGoToMain() throws InterruptedException {
        /* click the email field edit text and put in an email*/
        onView(withId(R.id.editTextEmailSignUp)).perform(click());
        onView(withId(R.id.editTextEmailSignUp)).perform(ViewActions.typeText("signuptest@signuptest.ca"));
        /* click the username field edit text and put in a random username */
        onView(withId(R.id.editTextUsernameSignUp)).perform(click());
        onView(withId(R.id.editTextUsernameSignUp)).perform(ViewActions.typeText("signuptest"));
        /* click the password field edit text and put in a random password */
        onView(withId(R.id.editTextPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextPasswordSignUp)).perform(ViewActions.typeText("signuptest"));
        /* click the confirm password field edit text and put in a random password */
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(click());
        onView(withId(R.id.editTextConfirmPasswordSignUp)).perform(ViewActions.typeText("signuptest"));
        /* click the sign up button */
        onView(withId(R.id.buttonSignUp)).perform(click());
        Thread.sleep(3000);
        /* click the logout popup button (would work if we were in main) */
        onView(withId(R.id.btn_logout)).perform(click());
        /* check that the username shown in the profile popup is our test username (shows successful sign up) */
        onView(withId(R.id.profile_username)).check(matches(withText("signuptest's\tSweetHome")));
        /* get the user so we can delete it now that the test is over */
        FirebaseUser user = userAuth.getCurrentUser();
        /* click logout */
        onView(withId(R.id.profile_logout)).perform(click());
        Thread.sleep(3000);
        /* delete the user account */
        user.delete();
        /* delete the user username/email mapping in the users collection */
        usersRef.whereEqualTo("email", "signuptest@signuptest.ca")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        if (doc.exists()) {
                            String userDocID = doc.getId();
                            usersRef.document(userDocID).delete();
                        }
                    }
                });
    }

    @After
    public void after() {
        Intents.release();
    }
}

