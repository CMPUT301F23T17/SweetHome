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
import android.view.View;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.UiController;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @class ApplyTagTest
 * <p>This class tests applying a tag to items</p>
 *
 * @date <p>November 30, 2023</p>
 *
 * @source Answer To:Using Espresso to click view inside RecyclerView item. The original answer
 * post was made by blade. (2015, May 20). Most recently the answer was edited by
 * blade. (2016, August 30). StackOverflow. The content of the posts on StackOverflow
 * are licensed under Creative Commons Attribution-ShareAlike.
 * @link https://stackoverflow.com/a/30338665
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ApplyTagTest {
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

        /* Create sample item 1 */
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.item_name_field)).perform(ViewActions.typeText("AT1"));
        Thread.sleep(3000);
        onView(withId(R.id.serial_number_field)).perform(ViewActions.typeText("1234567"));
        onView(withId(R.id.serial_number_field)).perform(ViewActions.pressImeActionButton());
        onView(withId(R.id.tag_input)).perform(ViewActions.typeText("testTag"));
        onView(withId(R.id.tag_input)).perform(ViewActions.pressImeActionButton());
        Thread.sleep(3000);
        closeSoftKeyboard();
        onView(withId(R.id.description_field)).perform(ViewActions.typeText("This is an apply tag test"));
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

        /* Create sample item 2 */
        Thread.sleep(3000);
        onView(withId(R.id.add_button)).perform(click());
        onView(withId(R.id.item_name_field)).perform(ViewActions.typeText("AT2"));
        Thread.sleep(3000);
        onView(withId(R.id.serial_number_field)).perform(ViewActions.typeText("1224567"));
        onView(withId(R.id.serial_number_field)).perform(ViewActions.pressImeActionButton());
        onView(withId(R.id.tag_input)).perform(ViewActions.typeText("testTag"));
        onView(withId(R.id.tag_input)).perform(ViewActions.pressImeActionButton());
        Thread.sleep(3000);
        closeSoftKeyboard();
        onView(withId(R.id.description_field)).perform(ViewActions.typeText("This is an apply tag test"));
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

        /* Create sample tag 1 */
        onView(withId(R.id.tag_action_on_button)).perform(click());
        onView(withId(R.id.create_tag_button)).perform(click());
        onView(withId(R.id.tag_editable_input)).perform(ViewActions.typeText("SampleApply1"));
        Thread.sleep(3000);
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(R.id.create_new_tag_button)).perform(click());

        /* Create sample tag 2 */
        onView(withId(R.id.tag_editable_input)).perform(ViewActions.typeText("SampleApply2"));
        Thread.sleep(3000);
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(R.id.create_new_tag_button)).perform(click());
        /* Scroll Down */
        onView(ViewMatchers.withId(R.id.tags_scroll)).perform(ViewActions.swipeUp());
        Thread.sleep(3000);
        /* Click Done */
        onView(withId(R.id.done_create_button)).perform(click());

        /* Click the items' checkboxes */
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_checkBox)).perform(click());
        Thread.sleep(3000);
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(1).onChildView(withId(R.id.item_checkBox)).perform(click());
        Thread.sleep(3000);

    }
    @Test
    public void testApplyTag() throws InterruptedException {
        /* Click tag action button */
        onView(withId(R.id.tag_action_on_button)).perform(click());

        /* Click Add Tag button */
        onView(withId(R.id.add_tag_panel)).perform(click());

        /* Click spinner */
        onView(withId(R.id.spinner_container)).perform(click());

        /* Select tag to apply */
        onData(CoreMatchers.is("SampleApply1")).perform(click());
        Thread.sleep(3000);

        /* Click Apply Button */
        onView(withId(R.id.apply_new_tag_button)).perform(click());
        Thread.sleep(3000);

        /* Click spinner */
        onView(withId(R.id.spinner_container)).perform(click());

        /* Select tag to apply */
        onData(CoreMatchers.is("SampleApply2")).perform(click());
        Thread.sleep(3000);

        /* Click Apply Button */
        onView(withId(R.id.apply_new_tag_button)).perform(click());
        Thread.sleep(3000);

        /* Press Done button */
        onView(withId(R.id.done_create_button)).perform(click());

        /* Check if the two items have the sample tags applied */
        Thread.sleep(2000);

        /* Check ags for first item */
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.tags_container)).onChildView(withText("SampleApply1")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.tags_container)).onChildView(withText("SampleApply2")).check(matches(isDisplayed()));

        /* Check tags for first item */
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(1).onChildView(withId(R.id.tags_container)).onChildView(withText("SampleApply1")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(1).onChildView(withId(R.id.tags_container)).onChildView(withText("SampleApply2")).check(matches(isDisplayed()));


    }

    @After
    public void clear() throws InterruptedException{
        /* Delete sample items */
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).onChildView(withId(R.id.item_checkBox)).perform(click());
        Thread.sleep(3000);
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(1).onChildView(withId(R.id.item_checkBox)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.delete_action_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.delete_button)).perform(click());
        Thread.sleep(3000);


        /* Delete sample tags */
        onView(withId(R.id.tag_action_on_button)).perform(click());
        onView(withId(R.id.create_tag_button)).perform(click());
        onView(withId(R.id.tags_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.remove_tag_from_list)));
        Thread.sleep(2000);
        onView(withId(R.id.tags_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.remove_tag_from_list)));
        Thread.sleep(2000);
        onView(withId(R.id.tags_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.remove_tag_from_list)));

        /* Scroll Down */
        onView(ViewMatchers.withId(R.id.tags_scroll)).perform(ViewActions.swipeUp());

        /* Click Done */
        onView(withId(R.id.done_create_button)).perform(click());

        Thread.sleep(2000);
        logout();

    }

    /* Source: https://stackoverflow.com/a/30338665 */
    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }
            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
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
