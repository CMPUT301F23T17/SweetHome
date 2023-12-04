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
 * @class SortItemTest
 * <p>This class tests sorting items</p>
 *
 * @date <p>December 4, 2023</p>
 *
 * @source Answer To:Write a test that clicks on views inside PopupWindow. The original answer
 * post was made by Nishanth. (2016, April 17). StackOverflow. The content of the posts on
 * StackOverflow are licensed under Creative Commons Attribution-ShareAlike.
 * @link https://stackoverflow.com/a/36673422
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SortItemTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> scenario=new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    @Before
    public void setup() throws InterruptedException{
        Thread.sleep(5000);
    }
    @Test
    public void testSortItem() throws InterruptedException{
        //login
        onView(withId(R.id.editTextUsername)).perform(clearText(), ViewActions.typeText("logintest"));
        onView(withId(R.id.editTextPassword)).perform(clearText(), ViewActions.typeText("logintest"));
        onView(withId(R.id.buttonLogin)).perform(click());
        Thread.sleep(5000);

        // Add first item
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.item_name_field)).perform(ViewActions.typeText("SortItemTest"));
        Thread.sleep(3000);
        onView(withId(R.id.serial_number_field)).perform(ViewActions.typeText("6224567"));
        onView(withId(R.id.serial_number_field)).perform(ViewActions.pressImeActionButton());
        onView(withId(R.id.tag_input)).perform(ViewActions.typeText("AAA"));
        onView(withId(R.id.tag_input)).perform(ViewActions.pressImeActionButton());
        Thread.sleep(3000);
        closeSoftKeyboard();
        onView(withId(R.id.description_field)).perform(ViewActions.typeText("This is a sort test"));
        closeSoftKeyboard();
        onView(withId(R.id.make_field)).perform(ViewActions.typeText("testMake"));
        closeSoftKeyboard();
        onView(withId(R.id.model_field)).perform(ViewActions.typeText("testModel"));
        closeSoftKeyboard();
        onView(withId(R.id.date_field)).perform(click());
        onView(withText("OK")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.value_field)).perform(ViewActions.typeText("10000000"));
        closeSoftKeyboard();
        onView(withId(R.id.comment_field)).perform(ViewActions.typeText("testComment"));
        closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.scroll_view)).perform(ViewActions.swipeDown());
        Thread.sleep(3000);
        onView(withId(R.id.check_icon)).perform(click());
        Thread.sleep(3000);

        // Add second item
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.item_name_field)).perform(ViewActions.typeText("SortItemTest2"));
        Thread.sleep(3000);
        onView(withId(R.id.serial_number_field)).perform(ViewActions.typeText("6654321"));
        onView(withId(R.id.serial_number_field)).perform(ViewActions.pressImeActionButton());
        onView(withId(R.id.tag_input)).perform(ViewActions.typeText("BBB"));
        onView(withId(R.id.tag_input)).perform(ViewActions.pressImeActionButton());
        Thread.sleep(3000);
        closeSoftKeyboard();
        onView(withId(R.id.description_field)).perform(ViewActions.typeText("This is a sort test for the second item"));
        closeSoftKeyboard();
        onView(withId(R.id.make_field)).perform(ViewActions.typeText("testMake2"));
        closeSoftKeyboard();
        onView(withId(R.id.model_field)).perform(ViewActions.typeText("testModel2"));
        closeSoftKeyboard();
        onView(withId(R.id.date_field)).perform(click());
        onView(withText("OK")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.value_field)).perform(ViewActions.typeText("1"));
        closeSoftKeyboard();
        onView(withId(R.id.comment_field)).perform(ViewActions.typeText("testComment2"));
        closeSoftKeyboard();
        onView(withId(R.id.scroll_view)).perform(ViewActions.swipeDown());
        Thread.sleep(3000);
        onView(withId(R.id.check_icon)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.spinner_sort_options)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Highest value"))).perform(click());
        Thread.sleep(3000);
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0)
                .onChildView(withId(R.id.item_name)).check(matches(withText("SortItemTest")));
        onView(withId(R.id.spinner_sort_options)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Tags: Z - A"))).perform(click());
        Thread.sleep(3000);
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0)
                .onChildView(withId(R.id.item_name)).check(matches(withText("SortItemTest2")));
        onView(withId(R.id.spinner_sort_options)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Newest"))).perform(click());
        Thread.sleep(3000);
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0)
                .onChildView(withId(R.id.item_name)).check(matches(withText("SortItemTest2")));

        //Delete the items
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_name)).check(matches(withText("SortItemTest2")));
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_checkBox)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.delete_action_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.delete_button)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_name)).check(matches(withText("SortItemTest")));
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

