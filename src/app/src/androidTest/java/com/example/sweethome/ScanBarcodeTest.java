package com.example.sweethome;

/* necessary imports */
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import android.content.ComponentName;
import androidx.test.espresso.NoMatchingViewException;
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
 * @class ScanBarcodeTest
 * <p>This class tests the barcode scanning</p>
 *
 * @date <p>December 4, 2023</p>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScanBarcodeTest {
    @Rule
    public ActivityScenarioRule<WelcomeActivity> welcomeScenario=new ActivityScenarioRule<>(WelcomeActivity.class);
    @Before
    public void init() {
        Intents.init();
    }
    @Test
    public void testBarcode() throws InterruptedException {
        try {
            Thread.sleep(3000);
            onView(withId(R.id.search_add_container)).check(matches(isDisplayed()));
        } catch (NoMatchingViewException e) {
            /* click the username field edit text, clear text (if applicable) and put our test username */
            onView(withId(R.id.editTextUsername)).perform(click(), ViewActions.clearText(), ViewActions.typeText("logintest"));
            /* click the password field edit text, clear text (if applicable) and put in our test password */
            onView(withId(R.id.editTextPassword)).perform(click(), ViewActions.clearText(), ViewActions.typeText("logintest"));
            /* click the login button */
            onView(withId(R.id.buttonLogin)).perform(click());
            Thread.sleep(3000);
            /* check if the main activity is launched */
            intended(hasComponent(new ComponentName(getApplicationContext(), MainActivity.class)));
        }
        /* click add button on MainActivity */
        onView(withId(R.id.add_button)).perform(click());
        Thread.sleep(3000);
        /* Verify that we are in ManageItemActivity */
        intended(hasComponent(new ComponentName(getApplicationContext(), ManageItemActivity.class)));
        onView(withId(R.id.barcode_scan_icon)).perform(click());
        intended(hasComponent(new ComponentName(getApplicationContext(), ScanningBarcodeActivity.class)));
    }


    @After
    public void drop() {
        Intents.release();
    }
}

