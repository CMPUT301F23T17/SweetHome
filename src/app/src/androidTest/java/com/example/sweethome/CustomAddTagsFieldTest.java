package com.example.sweethome;

/* necessary imports */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

/**
 * @class CustomAddTagsFieldTest
 * <p>This class tests adding tags field</p>
 *
 * @date <p>December 4, 2023</p>
 */
@RunWith(AndroidJUnit4.class)
public class CustomAddTagsFieldTest {

    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(MainActivity.class);
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    @Test
    public void testAddTag() {
        scenario.onActivity(activity -> {
            CustomAddTagsField customAddTagsField = new CustomAddTagsField(activity);

            // Add tags
            customAddTagsField.addTag("Tag1");
            customAddTagsField.addTag("Tag2");
            customAddTagsField.addTag("Tag3");

            // Check the list of added tag names
            ArrayList<String> addedTagNames = customAddTagsField.getAddedTagNames();
            assertEquals(3, addedTagNames.size());
            assertTrue(addedTagNames.contains("Tag1"));
            assertTrue(addedTagNames.contains("Tag2"));
            assertTrue(addedTagNames.contains("Tag3"));

        });
    }

    @Test
    public void testRemoveTag() {
        scenario.onActivity(activity -> {
            CustomAddTagsField customAddTagsField = new CustomAddTagsField(activity);

            // Add tags
            customAddTagsField.addTag("Tag1");
            customAddTagsField.addTag("Tag2");
            customAddTagsField.addTag("Tag3");

            // Remove Tag1
            customAddTagsField.removeTag("Tag1");

            // Check the list of added tag names
            ArrayList<String> addedTagNamesAfterRemoval = customAddTagsField.getAddedTagNames();

            // Check that Tag1 is removed
            assertEquals(2, addedTagNamesAfterRemoval.size());
            assertFalse(addedTagNamesAfterRemoval.contains("Tag1"));
            assertTrue(addedTagNamesAfterRemoval.contains("Tag2"));
            assertTrue(addedTagNamesAfterRemoval.contains("Tag3"));
        });
    }
}
