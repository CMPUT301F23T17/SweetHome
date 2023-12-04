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
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;

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
 * @class FilterItemTest
 * <p>This class tests filtering items</p>
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
public class FilterItemTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> scenario=new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    @Before
    public void setup() throws InterruptedException{
        Thread.sleep(5000);
    }
    @Test
    public void testFilterItem() throws InterruptedException{
        //login
        onView(withId(R.id.editTextUsername)).perform(clearText(), ViewActions.typeText("logintest"));
        onView(withId(R.id.editTextPassword)).perform(clearText(), ViewActions.typeText("logintest"));
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(5000);

        //test
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.item_name_field)).perform(ViewActions.typeText("FilterItemTest"));
        Thread.sleep(2000);
        onView(withId(R.id.serial_number_field)).perform(ViewActions.typeText("8224567"));
        onView(withId(R.id.serial_number_field)).perform(ViewActions.pressImeActionButton());
        onView(withId(R.id.tag_input)).perform(ViewActions.typeText("Filter"));
        onView(withId(R.id.tag_input)).perform(ViewActions.pressImeActionButton());
        Thread.sleep(3000);
        closeSoftKeyboard();
        onView(withId(R.id.description_field)).perform(ViewActions.typeText("This is a filter test"));
        closeSoftKeyboard();
        onView(withId(R.id.make_field)).perform(ViewActions.typeText("testFilterMake"));
        closeSoftKeyboard();
        onView(withId(R.id.model_field)).perform(ViewActions.typeText("testFilterModel"));
        closeSoftKeyboard();
        onView(withId(R.id.date_field)).perform(click());
        onView(withText("OK")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.value_field)).perform(ViewActions.typeText("119.98"));
        closeSoftKeyboard();
        onView(withId(R.id.comment_field)).perform(ViewActions.typeText("testComment filter"));
        closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.scroll_view)).perform(ViewActions.swipeDown());
        Thread.sleep(3000);
        onView(withId(R.id.check_icon)).perform(click());
        Thread.sleep(3000);

        //test implementation
        onView(withId(R.id.filter_button)).perform(click());
        onView(withId(R.id.make_field)).perform(click(), ViewActions.typeText("Filter"), ViewActions.pressImeActionButton());
        Thread.sleep(2000);
        onView(withId(R.id.keyword_field)).perform(click(), ViewActions.typeText("filter"), ViewActions.pressImeActionButton());
        Thread.sleep(2000);
        onView(withId(R.id.tag_filter_field)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Filter"))).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.apply_filter_button)).perform(click());

        //verify
        onData(anything()).inAdapterView(withId(R.id.item_list))
                .atPosition(0).onChildView(withId(R.id.item_name))
                .check(matches(withText("FilterItemTest")));

        //Delete the item
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_name)).check(matches(withText("FilterItemTest")));
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

