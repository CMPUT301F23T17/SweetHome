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
 * @class ForgotPasswordTest
 * <p>This class tests that the forgot password page is
 * working as expected</p>
 *
 * @date <p>December 1, 2023</p>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ForgotPasswordTest {
    @Rule
    public ActivityScenarioRule<ForgotPasswordActivity> scenario = new ActivityScenarioRule<ForgotPasswordActivity>(ForgotPasswordActivity.class);

    @Before
    public void before() {
        Intents.init();
    }

    @Test
    public void testGoToLogin() {
        /* click the words "Login" (as in "Go back to Login" on Forgot Password page) */
        onView(withId(R.id.textViewBackToLogin)).perform(click());
        /* check if the login activity is launched */
        intended(hasComponent(new ComponentName(getApplicationContext(), LoginActivity.class)));
    }

    @Test
    public void testGoToLoginAndBack() {
        /* click the words "Login" (as in "Go back to Login" on Forgot Password page) */
        onView(withId(R.id.textViewBackToLogin)).perform(click());
        /* click the words "Forgot your password" on Login page */
        onView(withId(R.id.textViewForgotPassword)).perform(click());
        /* check if we went back to the forgot password activity by checking if the send email button is displayed */
        onView(withId(R.id.buttonSendEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void testForgotPasswordWithEmptyEmail() {
        /* click the send email button */
        onView(withId(R.id.buttonSendEmail)).perform(click());
        /* check that we get the expected error (email is empty) */
        onView(withId(R.id.editTextSendEmail)).check(matches(hasErrorText("Email cannot be empty.")));
    }

    @Test
    public void testForgotPasswordWithNonexistentEmail() throws InterruptedException {
        /* click the email field edit text and put in a nonexistent email */
        onView(withId(R.id.editTextSendEmail)).perform(click());
        onView(withId(R.id.editTextSendEmail)).perform(ViewActions.typeText("NonexistentEmail@NonexistentEmail.ca"));
        /* click the send email button */
        onView(withId(R.id.buttonSendEmail)).perform(click());
        Thread.sleep(3000);
        /* check that we get the expected error (email is not linked to an account) */
        onView(withId(R.id.editTextSendEmail)).check(matches(hasErrorText("Email is not linked to an existing account")));
    }

    @After
    public void after() {
        Intents.release();
    }
}
