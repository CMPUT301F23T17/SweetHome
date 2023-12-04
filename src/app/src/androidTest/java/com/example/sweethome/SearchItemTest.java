package com.example.sweethome;

/* necessary imports */
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @class SearchItemTest
 * <p>This class tests searching for items</p>
 *
 * @date <p>December 4, 2023</p>
 *
 * @source Answer To:Write a test that clicks on views inside PopupWindow. The original answer
 * post was made by [username]. ([year], [month] [day]). Most recently the answer was edited by
 * [username]. ([year], [month] [day]). StackOverflow. The content of the posts on StackOverflow
 * are licensed under Creative Commons Attribution-ShareAlike.
 * @link https://stackoverflow.com/questions/27382147/write-a-test-that-clicks-on-views-inside-popupwindow
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchItemTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> scenario=new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    @Before
    public void setup() throws InterruptedException{
        Thread.sleep(5000);
    }
    @Test
    public void testSearchItem() throws InterruptedException{
        //login
        onView(withId(R.id.editTextUsername)).perform(clearText(), ViewActions.typeText("logintest"));
        onView(withId(R.id.editTextPassword)).perform(clearText(), ViewActions.typeText("logintest"));
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(5000);

        // Add first item
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.item_name_field)).perform(ViewActions.typeText("SearchItemTest"));
        Thread.sleep(3000);
        onView(withId(R.id.serial_number_field)).perform(ViewActions.typeText("2224567"));
        onView(withId(R.id.serial_number_field)).perform(ViewActions.pressImeActionButton());
        onView(withId(R.id.tag_input)).perform(ViewActions.typeText("TagA"));
        onView(withId(R.id.tag_input)).perform(ViewActions.pressImeActionButton());
        Thread.sleep(3000);
        closeSoftKeyboard();
        onView(withId(R.id.description_field)).perform(ViewActions.typeText("This is a search test"));
        closeSoftKeyboard();
        onView(withId(R.id.make_field)).perform(ViewActions.typeText("testMake"));
        closeSoftKeyboard();
        onView(withId(R.id.model_field)).perform(ViewActions.typeText("testModel"));
        closeSoftKeyboard();
        onView(withId(R.id.date_field)).perform(click());
        onView(withText("OK")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.value_field)).perform(ViewActions.typeText("10"));
        closeSoftKeyboard();
        onView(withId(R.id.comment_field)).perform(ViewActions.typeText("testComment"));
        closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.scroll_view)).perform(ViewActions.swipeDown());
        Thread.sleep(3000);
        onView(withId(R.id.check_icon)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.search_input)).perform(click(), ViewActions.typeText("SearchItemTest"), ViewActions.pressImeActionButton());
        Thread.sleep(2000);
        onData(anything()).inAdapterView(withId(R.id.item_list))
                .atPosition(0).onChildView(withId(R.id.item_name))
                .check(matches(withText("SearchItemTest")));
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0)
                .onChildView(withId(R.id.item_name)).check(matches(withText("SearchItemTest")));
        closeSoftKeyboard();
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_name)).check(matches(withText("SearchItemTest")));
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_checkBox)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.delete_action_button)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.delete_button)).perform(click());

        //logout
        onView(withId(R.id.btn_logout)).perform(click());
        onView(withId(R.id.profile_logout)).perform(click());
    }
}

