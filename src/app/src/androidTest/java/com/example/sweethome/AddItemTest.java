package com.example.sweethome;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @class AddItemTest
 * <p>This class tests adding an item</p>
 *
 * @date <p>November 10, 2023</p>
 *
 * @source Answer To:Android Espresso click a button inside a ListView. The original answer
 * post was made by Zerosero. (2019, August 1). StackOverflow. The content of the posts on StackOverflow
 * are licensed under Creative Commons Attribution-ShareAlike.
 * @link https://stackoverflow.com/a/57301295
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddItemTest {
    @Rule
    public ActivityScenarioRule<WelcomeActivity> scenario=new ActivityScenarioRule<WelcomeActivity>(WelcomeActivity.class);

    @Before
    public void setup() throws InterruptedException{
        Thread.sleep(5000);

        /* Check if a user is logged in */
        if (isLoggedIn()) {
            /* Logout the current user */
            logout();
        }

        /* Log in the test account */
        onView(withId(R.id.editTextUsername)).perform(clearText());
        onView(withId(R.id.editTextUsername)).perform(ViewActions.typeText("logintest"));
        onView(withId(R.id.editTextPassword)).perform(clearText());
        onView(withId(R.id.editTextPassword)).perform(ViewActions.typeText("logintest"));
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(5000);
    }
    @Test
    public void testAddItem() throws InterruptedException{

        /* Click the Add New button */
        onView(withId(R.id.add_button)).perform(click());

        /* Type item's name*/
        onView(withId(R.id.item_name_field)).perform(ViewActions.typeText("AddItemTest"));
        Thread.sleep(3000);
        /* Type item's serial number*/
        onView(withId(R.id.serial_number_field)).perform(ViewActions.typeText("1224567"));
        /* Press Enter*/
        onView(withId(R.id.serial_number_field)).perform(ViewActions.pressImeActionButton());
        /* Type a tag name */
        onView(withId(R.id.tag_input)).perform(ViewActions.typeText("testTag"));
        /* Press Enter */
        onView(withId(R.id.tag_input)).perform(ViewActions.pressImeActionButton());
        Thread.sleep(3000);
        closeSoftKeyboard();
        /* Type description */
        onView(withId(R.id.description_field)).perform(ViewActions.typeText("This is an add test"));
        closeSoftKeyboard();
        /* Type item's make*/
        onView(withId(R.id.make_field)).perform(ViewActions.typeText("testMake"));
        closeSoftKeyboard();
        /* Type item's model*/
        onView(withId(R.id.model_field)).perform(ViewActions.typeText("testModel"));
        closeSoftKeyboard();
        /* Specify item's date of acquisition*/
        onView(withId(R.id.date_field)).perform(click());
        onView(withText("OK")).perform(click());
        Thread.sleep(3000);
        /* Type item's value*/
        onView(withId(R.id.value_field)).perform(ViewActions.typeText("119.98"));
        closeSoftKeyboard();
        /* Type a comment*/
        onView(withId(R.id.comment_field)).perform(ViewActions.typeText("testComment"));
        closeSoftKeyboard();
        /* Scroll up */
        onView(ViewMatchers.withId(R.id.scroll_view)).perform(ViewActions.swipeDown());
        Thread.sleep(3000);
        /* Click Save Button */
        onView(withId(R.id.check_icon)).perform(click());
        Thread.sleep(3000);

        /* Check if newly added item is on the item's list */
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_name)).check(matches(withText("AddItemTest")));

        /* Delete AddItemTest */
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_checkBox)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.delete_action_button)).perform(click());
        onView(withId(R.id.delete_button)).perform(click());
    }

    @After
    public void clear() throws InterruptedException {
        logout();
    }

    private boolean isLoggedIn(){

        try {
            onView(withId(R.id.btn_logout)).check(matches(isDisplayed()));
        } catch (NoMatchingViewException e) {
            return false;
        }
        return true;
    }

    private void logout() throws InterruptedException{
        Thread.sleep(3000);
        onView(withId(R.id.btn_logout)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.profile_logout)).perform(click());
        Thread.sleep(5000);
    }

}

