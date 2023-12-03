package com.example.sweethome;

/* necessary imports */
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smarteist.autoimageslider.SliderView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @class ItemsCustomAdapter
 *
 * <p>This class defines a custom list to hold Item objects
 * and display the content of them on our frontend layout.
 * This is used by our item adapter in the main activity.</p>
 *
 * @date <p>October 28, 2023</p>
 *
 * @source Some code has been adapted from the following source:
 * A ListView with Checkboxes (Using FragmentActivity).
 * homepage of andrew lim. Copyright © Andrew Lim Chong Liang.
 * Fair use for educational purposes.
 * @link https://windrealm.org/tutorials/android/listview-with-checkboxes.php
 */
public class ItemsCustomAdapter extends ArrayAdapter<Item> {
    /* attributes for this class */
    private ArrayList<Item> items;
    private Context context;

    /* constructor for this class */
    public ItemsCustomAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
    }

    // Inside your ItemsCustomAdapter
    public void updateItems(ArrayList<Item> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        /* get the item object in the current position */
        Item item = items.get(position);

        View view = convertView;
        LinearLayout imageView;
        LinearLayout description_container;
        TextView nameView;
        TextView purchaseDateView;
        TextView estimatedValueView;
        CheckBox itemCheckboxView;
        RelativeLayout noImagePlaceholder;
        LinearLayout sliderViewFrame;
        SliderView sliderView;
        ArrayList<ImageSliderData> sliderDataArrayList = new ArrayList<>();
        ImageSliderAdapter adapter;
        LinearLayout tags_container;

        /* view attached to the item list content layout that we created */
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_list_content, parent, false);

            /* find the text views inside the view */
            imageView = view.findViewById(R.id.item_image);
            description_container = view.findViewById(R.id.description_container);
            nameView = view.findViewById(R.id.item_name);
            purchaseDateView = view.findViewById(R.id.item_purchase_date);
            estimatedValueView = view.findViewById(R.id.item_estimated_value);
            itemCheckboxView = view.findViewById(R.id.item_checkBox);
            noImagePlaceholder = view.findViewById(R.id.item_no_image_placeholder);
            sliderViewFrame = view.findViewById(R.id.item_image_slider_frame);
            sliderView = view.findViewById(R.id.item_image_slider);
            tags_container = view.findViewById(R.id.tags_container);

            /* Tag row so that it's not necessary to call findViewById for the ItemViewHolder again when reusing the row. */
            view.setTag(new ItemViewHolder(nameView, purchaseDateView, estimatedValueView, itemCheckboxView, imageView, description_container, noImagePlaceholder, sliderViewFrame, sliderView, tags_container));

            itemCheckboxView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkbox = (CheckBox) v;
                    Item item = (Item) checkbox.getTag();
                    item.setSelected(checkbox.isChecked());
                }
            });

            description_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    LinearLayout description = (LinearLayout) v;
                    Item item = (Item) description.getTag();
                    Intent intent = new Intent(context, ManageItemActivity.class);
                    intent.putExtra("screen", "View / Edit");
                    intent.putExtra("id", item.getItemId());
                    intent.putExtra("name", item.getName());
                    intent.putExtra("description", item.getDescription());
                    intent.putExtra("make", item.getMake());
                    intent.putExtra("model", item.getModel());
                    intent.putExtra("serialNumber", item.getSerialNumber());
                    intent.putExtra("estimatedValue", item.getEstimatedValue());
                    intent.putExtra("purchaseDate", item.getPurchaseDate().toDate());
                    intent.putExtra("comment", item.getComment());
                    intent.putExtra("photos", item.getPhotos());
                    intent.putExtra("tags", item.getTags());
                    context.startActivity(intent);
                }
            });
        } else {
            ItemViewHolder itemViewHolder= (ItemViewHolder) view.getTag();
            nameView = itemViewHolder.getItemNameView();
            purchaseDateView = itemViewHolder.getItemPurchaseDateView();
            estimatedValueView = itemViewHolder.getItemValueView();
            itemCheckboxView = itemViewHolder.getItemCheckBox();
            noImagePlaceholder = itemViewHolder.getItemNoImagePlaceholder();
            sliderViewFrame = itemViewHolder.getItemSliderViewFrame();
            sliderView = itemViewHolder.getItemSliderView();
            tags_container = itemViewHolder.getItemTagsContainer();
            tags_container.removeAllViews();
            description_container = itemViewHolder.getItemDescriptionContainer();
        }

        /* set the views to be the correct corresponding value for each element of the item (ie. name, purchase date, estimated value) */
        ArrayList<String> photos = item.getPhotos();
        if (item.getPhotos().size() > 0) {
            for (int i = 0; i < photos.size(); i++) {
                sliderDataArrayList.add(new ImageSliderData(Uri.parse(photos.get(i))));
            }
            adapter = new ImageSliderAdapter(context, sliderDataArrayList);
            sliderView.setSliderAdapter(adapter);
            noImagePlaceholder.setVisibility(View.GONE);
            sliderViewFrame.setVisibility(View.VISIBLE);
        } else {
            sliderViewFrame.setVisibility(View.GONE);
            noImagePlaceholder.setVisibility(View.VISIBLE);
        }

        for (String tagName : item.getTags()) {
            View tag_item = LayoutInflater.from(getContext()).inflate(R.layout.tag_applied, null);
            TextView tag_name_field = tag_item.findViewById(R.id.tag_name);
            tag_name_field.setText(tagName);
            tags_container.addView(tag_item);
        }

        nameView.setText(item.getName());

        SimpleDateFormat df = new SimpleDateFormat(context.getString(R.string.date_format)); //create a new format for the date to be in YYYY/MM/DD format
        if (item.getPurchaseDate() != null) {
            String dateString = df.format(item.getPurchaseDate().toDate()); //convert the date to a string in the specified format
            purchaseDateView.setText(dateString);
        } else {
            purchaseDateView.setText("");
        }

        String value = String.format("%.2f", item.getEstimatedValue()); //ensure there are only 2 places after the decimal when formatting the string
        estimatedValueView.setText(context.getString(R.string.cad_currency) + value); //format the estimated value to include the currency ie. CAD$*.xx

        itemCheckboxView.setChecked(item.isSelected());

        itemCheckboxView.setTag(item);
        description_container.setTag(item);

        /* return the view we inflated */
        return view;
    }

    public void setCurrentItemList(ArrayList<Item> itemList) {
        this.items = new ArrayList<>(itemList);
    }

    public ArrayList<Item> getCurrentItemList() {
        return this.items;
    }
}
