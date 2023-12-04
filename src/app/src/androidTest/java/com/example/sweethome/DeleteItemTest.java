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
import static org.hamcrest.Matchers.not;

import android.app.DatePickerDialog;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @class DeleteItemTest
 * <p>This class tests deleting an item and multiple items at the same time</p>
 *
 * @date <p>November 9, 2023</p>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeleteItemTest {
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
    public void testDeleteItem() throws InterruptedException{
        /* Click Add New Button */
        onView(withId(R.id.add_button)).perform(click());
        /* Type item's name */
        onView(withId(R.id.item_name_field)).perform(ViewActions.typeText("DeleteTest"));
        Thread.sleep(3000);
        /* Type item's serial number */
        onView(withId(R.id.serial_number_field)).perform(ViewActions.typeText("1234567"));
        /* Press Enter */
        onView(withId(R.id.serial_number_field)).perform(ViewActions.pressImeActionButton());
        /* Create a tag by typing the tag name */
        onView(withId(R.id.tag_input)).perform(ViewActions.typeText("testTag"));
        /* Pres Enter */
        onView(withId(R.id.tag_input)).perform(ViewActions.pressImeActionButton());
        Thread.sleep(3000);
        closeSoftKeyboard();
        /* Type the item's description */
        onView(withId(R.id.description_field)).perform(ViewActions.typeText("This is a delete test"));
        closeSoftKeyboard();
        /* Type the item's make */
        onView(withId(R.id.make_field)).perform(ViewActions.typeText("testMake"));
        closeSoftKeyboard();
        /* Type the item's model */
        onView(withId(R.id.model_field)).perform(ViewActions.typeText("testModel"));
        closeSoftKeyboard();
        /* Specify item's purchase date */
        onView(withId(R.id.date_field)).perform(click());
        onView(withText("OK")).perform(click());
        Thread.sleep(3000);
        /* Type item's estimated value */
        onView(withId(R.id.value_field)).perform(ViewActions.typeText("119.98"));
        closeSoftKeyboard();
        /* Type comment */
        onView(withId(R.id.comment_field)).perform(ViewActions.typeText("testComment"));
        closeSoftKeyboard();
        /* Scroll up */
        onView(ViewMatchers.withId(R.id.scroll_view)).perform(ViewActions.swipeDown());
        Thread.sleep(3000);
        onView(withId(R.id.check_icon)).perform(click());
        Thread.sleep(3000);

        /* Check if the item added is on the list */
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_name)).check(matches(withText("DeleteTest")));

        /* Click on the item's checkbox */
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_checkBox)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.delete_action_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.delete_button)).perform(click());
        try {
            /* Check position 0 if the item that was supposed to be deleted are still in that that certain position */
            onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_name)).check((matches(not(withText("DeleteTest")))));
        } catch (PerformException e) {
            //Error means there's nothing on the list
        }
    }

    @Test
    public void testDeleteMultipleItems() throws InterruptedException{

        /* Add item 1 */
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.item_name_field)).perform(ViewActions.typeText("DMI1"));
        Thread.sleep(3000);
        onView(withId(R.id.serial_number_field)).perform(ViewActions.typeText("1234567"));
        onView(withId(R.id.serial_number_field)).perform(ViewActions.pressImeActionButton());
        onView(withId(R.id.tag_input)).perform(ViewActions.typeText("testTag"));
        onView(withId(R.id.tag_input)).perform(ViewActions.pressImeActionButton());
        Thread.sleep(3000);
        closeSoftKeyboard();
        onView(withId(R.id.description_field)).perform(ViewActions.typeText("This is a delete test"));
        closeSoftKeyboard();
        onView(withId(R.id.make_field)).perform(ViewActions.typeText("testMake"));
        closeSoftKeyboard();
        onView(withId(R.id.model_field)).perform(ViewActions.typeText("testModel"));
        closeSoftKeyboard();
        onView(withId(R.id.date_field)).perform(click());
        onView(withText("OK")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.value_field)).perform(ViewActions.typeText("119.98"));
        closeSoftKeyboard();
        onView(withId(R.id.comment_field)).perform(ViewActions.typeText("testComment"));
        closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.scroll_view)).perform(ViewActions.swipeDown());
        Thread.sleep(3000);
        onView(withId(R.id.check_icon)).perform(click());
        Thread.sleep(3000);

        /* Check if the first item was added to the list */
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_name)).check(matches(withText("DMI1")));
        Thread.sleep(3000);

        /* Add item 2 */
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.item_name_field)).perform(ViewActions.typeText("DMI2"));
        Thread.sleep(3000);
        onView(withId(R.id.serial_number_field)).perform(ViewActions.typeText("1224567"));
        onView(withId(R.id.serial_number_field)).perform(ViewActions.pressImeActionButton());
        onView(withId(R.id.tag_input)).perform(ViewActions.typeText("testTag"));
        onView(withId(R.id.tag_input)).perform(ViewActions.pressImeActionButton());
        Thread.sleep(3000);
        closeSoftKeyboard();
        onView(withId(R.id.description_field)).perform(ViewActions.typeText("This is a delete test"));
        closeSoftKeyboard();
        onView(withId(R.id.make_field)).perform(ViewActions.typeText("testMake"));
        closeSoftKeyboard();
        onView(withId(R.id.model_field)).perform(ViewActions.typeText("testModel"));
        closeSoftKeyboard();
        onView(withId(R.id.date_field)).perform(click());
        onView(withText("OK")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.value_field)).perform(ViewActions.typeText("2909.98"));
        closeSoftKeyboard();
        onView(withId(R.id.comment_field)).perform(ViewActions.typeText("testComment"));
        closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.scroll_view)).perform(ViewActions.swipeDown());
        Thread.sleep(3000);
        onView(withId(R.id.check_icon)).perform(click());
        Thread.sleep(3000);

        /* Check if the second item was added to the list */
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(1).onChildView(withId(R.id.item_name)).check(matches(withText("DMI2")));
        Thread.sleep(3000);
        /* Click the items' checkboxes */
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_checkBox)).perform(click());
        Thread.sleep(3000);
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(1).onChildView(withId(R.id.item_checkBox)).perform(click());
        Thread.sleep(3000);

        /* Delete items */
        onView(withId(R.id.delete_action_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.delete_button)).perform(click());

        try {
            /* Check position 0 if any of the items that were supposed to be deleted are still in that that certain position */
            onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_name)).check((matches(not(withText("DMI2")))));
            onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_name)).check((matches(not(withText("DMI1")))));
        } catch (PerformException e) {
            //Error means there's nothing on the list
        }

        try {
            /* Check position 1 if any of the items that were supposed to be deleted are still in that that certain position */
            onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(1).onChildView(withId(R.id.item_name)).check((matches(not(withText("DMI2")))));
            onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(1).onChildView(withId(R.id.item_name)).check((matches(not(withText("DMI1")))));
        } catch (PerformException e) {
            // Error means that there is one or no items on the list, nothing in position 1
        }
    }

    @After
    public void clear() throws InterruptedException{
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

    private void logout() throws InterruptedException {
        Thread.sleep( 3000);
        onView(withId(R.id.btn_logout)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.profile_logout)).perform(click());
        Thread.sleep(5000);
    }
}

