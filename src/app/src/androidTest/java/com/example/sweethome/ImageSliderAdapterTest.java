package com.example.sweethome;

/* necessary imports */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

/**
 * @class ImageSliderAdapterTest
 * <p>This class tests the image slider adapter</p>
 *
 * @date <p>December 4, 2023</p>
 */
@RunWith(AndroidJUnit4.class)
public class ImageSliderAdapterTest {

    private ImageSliderAdapter imageSliderAdapter;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        ArrayList<ImageSliderData> sliderDataArrayList = createDummyData();
        imageSliderAdapter = new ImageSliderAdapter(context, sliderDataArrayList);
    }

    @Test
    public void testItemCount() {
        assertEquals("Item count should match the size of the data list",
                createDummyData().size(), imageSliderAdapter.getCount());
    }

    @Test
    public void testViewHolderCreation() {
        ViewGroup parent = new ViewGroup(ApplicationProvider.getApplicationContext()) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
            }
        };

        ImageSliderAdapter.SliderAdapterViewHolder viewHolder = imageSliderAdapter.onCreateViewHolder(parent);

        assertNotNull("ViewHolder should not be null", viewHolder);
        assertNotNull("ImageView in ViewHolder should not be null", viewHolder.imageViewBackground);
    }

    // Helper method to create dummy data for testing
    private ArrayList<ImageSliderData> createDummyData() {
        ArrayList<ImageSliderData> dummyData = new ArrayList<>();
        dummyData.add(new ImageSliderData(Uri.parse("dummy_uri_1")));
        dummyData.add(new ImageSliderData(Uri.parse("dummy_uri_2")));
        dummyData.add(new ImageSliderData(Uri.parse("dummy_uri_3")));
        return dummyData;
    }
}
