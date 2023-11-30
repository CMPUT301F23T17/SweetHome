package com.example.sweethome;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import android.app.DatePickerDialog;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import android.support.test.espresso.contrib.*;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


// sources: https://stackoverflow.com/questions/27382147/write-a-test-that-clicks-on-views-inside-popupwindow
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeleteItemTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario=new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() throws InterruptedException{
        Thread.sleep(5000);
    }
    @Test
    public void testDeleteItem() throws InterruptedException{
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.item_name_field)).perform(ViewActions.typeText("DeleteTest"));
        Thread.sleep(3000);
        onView(withId(R.id.serial_number_field)).perform(ViewActions.typeText("7234567"));
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


        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_name)).check(matches(withText("DeleteTest")));
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_checkBox)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.delete_action_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.delete_button)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_name)).check((matches(not(withText("DeleteTest")))));
    }
}

