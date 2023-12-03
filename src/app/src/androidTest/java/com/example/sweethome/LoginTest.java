package com.example.sweethome;

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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @class LoginTest
 * <p>This class tests logging in a user</p>
 *
 * @date <p>December 1, 2023</p>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> scenario=new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    @Before
    public void before() {
        Intents.init();
    }

    @Test
    public void testGoToSignUp() {
        /* click the words "Sign Up" (as in "Don't have an account? Sign Up" on Login page) */
        onView(withId(R.id.textViewSignUp)).perform(click());
        /* check if the sign up activity is launched */
        intended(hasComponent(new ComponentName(getApplicationContext(), SignUpActivity.class)));
    }

    @Test
    public void testGoToSignUpAndBack() {
        /* click the words "Sign Up" (as in "Don't have an account? Sign Up" on Login page) */
        onView(withId(R.id.textViewSignUp)).perform(click());
        /* click the words "Login" (as in "Already have an account? Login" on Sign Up page) */
        onView(withId(R.id.textViewLogin)).perform(click());
        /* check if we went back to the login activity by checking if the login button is displayed */
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
    }

    @Test
    public void testGoToForgotPassword() {
        /* click the words "Forgot your password" on Login page */
        onView(withId(R.id.textViewForgotPassword)).perform(click());
        /* check if the forgot password activity is launched */
        intended(hasComponent(new ComponentName(getApplicationContext(), ForgotPasswordActivity.class)));
    }

    @Test
    public void testGoToForgotPasswordAndBack() {
        /* click the words "Forgot your password" on Login page */
        onView(withId(R.id.textViewForgotPassword)).perform(click());
        /* click the words "Login" (as in "Go back to Login" on Forgot Password page) */
        onView(withId(R.id.textViewBackToLogin)).perform(click());
        /* check if we went back to the login activity by checking if the login button is displayed */
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginWithNonexistentUsername() throws InterruptedException {
        /* click the username field edit text and put in a nonexistent username */
        onView(withId(R.id.editTextUsername)).perform(click());
        onView(withId(R.id.editTextUsername)).perform(ViewActions.replaceText("NonexistentUsername"));
        /* click the password field edit text and put in a random password */
        onView(withId(R.id.editTextPassword)).perform(click());
        onView(withId(R.id.editTextPassword)).perform(ViewActions.typeText("randomPassword"));
        /* click the login button */
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(3000);
        /* check that we get the expected error (username doesn't exist) */
        onView(withId(R.id.editTextUsername)).check(matches(hasErrorText("Username does not exist. Please sign up.")));
    }

    @Test
    public void testLoginWithEmptyUsername() {
        /* click the username field edit text and put in a blank username */
        onView(withId(R.id.editTextUsername)).perform(click());
        onView(withId(R.id.editTextUsername)).perform(ViewActions.replaceText(""));
        /* click the password field edit text and put in a random password */
        onView(withId(R.id.editTextPassword)).perform(click());
        onView(withId(R.id.editTextPassword)).perform(ViewActions.typeText("randomPassword"));
        /* click the login button */
        onView(withId(R.id.buttonLogin)).perform(click());
        /* check that we get the expected error (username is empty) */
        onView(withId(R.id.editTextUsername)).check(matches(hasErrorText("Username cannot be empty.")));
    }

    @Test
    public void testLoginWithEmptyPassword() {
        /* click the username field edit text and put in a random username */
        onView(withId(R.id.editTextUsername)).perform(click());
        onView(withId(R.id.editTextUsername)).perform(ViewActions.replaceText("RandomUsername"));
        /* click the login button */
        onView(withId(R.id.buttonLogin)).perform(click());
        /* check that we get the expected error (username is empty) */
        onView(withId(R.id.editTextPassword)).check(matches(hasErrorText("Password cannot be empty.")));
    }

    @Test
    public void testLoginWithIncorrectPassword() throws InterruptedException {
        /* click the username field edit text and put our test username */
        onView(withId(R.id.editTextUsername)).perform(click());
        onView(withId(R.id.editTextUsername)).perform(ViewActions.replaceText("logintest"));
        /* click the password field edit text and put in a wrong password */
        onView(withId(R.id.editTextPassword)).perform(click());
        onView(withId(R.id.editTextPassword)).perform(ViewActions.typeText("wrongPassword"));
        /* click the login button */
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(3000);
        /* check that we get the expected error (incorrect password or username) */
        onView(withId(R.id.editTextUsername)).check(matches(hasErrorText("Incorrect username or password.")));
        onView(withId(R.id.editTextPassword)).check(matches(hasErrorText("Incorrect username or password.")));
    }

    @Test
    public void testGoToMain() throws InterruptedException {
        /* click the username field edit text and put our test username */
        onView(withId(R.id.editTextUsername)).perform(click());
        onView(withId(R.id.editTextUsername)).perform(ViewActions.replaceText("logintest"));
        /* click the password field edit text and put in our test password */
        onView(withId(R.id.editTextPassword)).perform(click());
        onView(withId(R.id.editTextPassword)).perform(ViewActions.typeText("logintest"));
        /* click the login button */
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(3000);
        /* check if the main activity is launched */
        intended(hasComponent(new ComponentName(getApplicationContext(), MainActivity.class)));
    }

    @Test
    public void testGoToMainAndBack() throws InterruptedException {
        /* click the username field edit text and put our test username */
        onView(withId(R.id.editTextUsername)).perform(click());
        onView(withId(R.id.editTextUsername)).perform(ViewActions.replaceText("logintest"));
        /* click the password field edit text and put in our test password */
        onView(withId(R.id.editTextPassword)).perform(click());
        onView(withId(R.id.editTextPassword)).perform(ViewActions.typeText("logintest"));
        /* click the login button */
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(3000);
        /* click the logout popup button */
        onView(withId(R.id.btn_logout)).perform(click());
        /* check that the username shown in the profile popup is our test username */
        onView(withId(R.id.profile_username)).check(matches(withText("logintest's\tSweetHome")));
        /* click cancel */
        onView(withId(R.id.profile_cancel)).perform(click());
        /* check if we are still in main activity by checking if the logout popup button is displayed */
        onView(withId(R.id.btn_logout)).check(matches(isDisplayed()));
        /* click the logout popup button again */
        onView(withId(R.id.btn_logout)).perform(click());
        /* actually click logout this time */
        onView(withId(R.id.profile_logout)).perform(click());
        Thread.sleep(3000);
        /* check if we went back to the login activity by checking if the login button is displayed */
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
    }

    @After
    public void after() {
        Intents.release();
    }
}