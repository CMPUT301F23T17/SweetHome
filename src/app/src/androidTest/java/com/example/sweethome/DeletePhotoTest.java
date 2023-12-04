package com.example.sweethome;

/* necessary imports */
import static android.app.Activity.RESULT_OK;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @class DeletePhotoTest
 * <p>This class tests deleting a photo</p>
 *
 * @date <p>December 4, 2023</p>
 */
public class DeletePhotoTest {
    @Rule
    public ActivityScenarioRule<WelcomeActivity> welcomeScenario=new ActivityScenarioRule<>(WelcomeActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    @Before
    public void initWelcome() {
        Intents.init();
    }

    @Test
    public void testDeletePhoto() throws InterruptedException {
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
        // Create an Intent with the desired data
        Intent data = new Intent();
        data.setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/sweethome-7045b.appspot.com/o/images%2Fphoto_1701038721847.jpg?alt=media&token=c9cf1741-3f8a-4783-b5a6-63ab3662efcd"));

        // Start the activity
        ActivityScenario<ManageItemActivity> scenario = ActivityScenario.launch(ManageItemActivity.class);

        // Use the onActivity callback to interact with the activity
        scenario.onActivity(activity -> {
            // Simulate onActivityResult
            activity.onActivityResult(activity.getOpenGalleryRequestCode(), RESULT_OK, data);

            // Add assertions based on your implementation
            Assert.assertEquals(View.GONE, activity.getNoImagePlaceholder().getVisibility());
            Assert.assertEquals(View.VISIBLE, activity.getSliderViewFrame().getVisibility());

            ImageView myButton = activity.findViewById(R.id.remove_image_button);
            myButton.performClick();
            Assert.assertEquals(View.VISIBLE, activity.getNoImagePlaceholder().getVisibility());
            Assert.assertEquals(View.GONE, activity.getSliderViewFrame().getVisibility());
        });



        // Close the activity after testing
        scenario.close();
    }
}
