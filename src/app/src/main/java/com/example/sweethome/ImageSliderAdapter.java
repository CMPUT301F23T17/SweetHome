package com.example.sweethome;

/* necessary imports */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * @class ImageSliderAdapter
 *
 * <p>This class extends the SliderViewAdapter to use a custom adapter to populate image slider views
 * with image data. It imports the Glide library for loading and caching of images from the
 * provided image URIs.</p>
 * <p>This adapter is designed to work with the SliderView library and creates view holders and
 * binds image data to the corresponding view.</p>
 * <p>The adapter dynamically loads images using the Glide library and sets them to the ImageView
 * in the view holder. The view holder recycles view to optimize memory usage.</p>
 * <p>Note: The layout for each slider is defined in the “slider_image.xml” layout resource file,
 * reference by R.layout.slider_image identifier</p>
 *
 * @date <p>November 10, 2023</p>
 */
public class ImageSliderAdapter extends SliderViewAdapter<ImageSliderAdapter.SliderAdapterViewHolder> {
    /* attributes of this class */
    private List<ImageSliderData> imageUris;

    /* constructors for this class */
    public ImageSliderAdapter(Context context, ArrayList<ImageSliderData> sliderDataArrayList) {
        this.imageUris = sliderDataArrayList;
    }

    /**
     * Creates a new SliderAdapterViewHolder by inflating the slider_image layout.
     *
     * @param parent The ViewGroup into which the new View will be added
     * @return A new SliderAdapterViewHolder that holds a View representing an item in the slider
     */
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_image, null);
        return new SliderAdapterViewHolder(inflate);
    }

    /**
     * Binds the image data to the corresponding view in the slider.
     *
     * @param viewHolder The ViewHolder that holds a View representing an item in the slider
     * @param position   The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, int position) {
        ImageSliderData sliderItem = imageUris.get(position);
        Glide.with(viewHolder.itemView)
                .load(sliderItem.getImgUri())
                .fitCenter()
                .into(viewHolder.imageViewBackground);
    }

    /**
     * Gets the total number of items in the data set held by the adapter.
     *
     * @return The total number of items
     */
    @Override
    public int getCount() {
        return imageUris.size();
    }

    /**
     * ViewHolder class that holds references to the views within an item in the slider.
     */
    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        View itemView;
        ImageView imageViewBackground;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.slider_image);
            this.itemView = itemView;
        }
    }
}
