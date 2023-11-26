/*
 * ItemViewHolder
 *
 * This class acts as view holder for each item entry on the list.
 * This is necessary to allow selecting multiple items at the same time.
 *
 * November 8, 2023
 *
 * Sources: https://windrealm.org/tutorials/android/listview-with-checkboxes.php
 *
 */

package com.example.sweethome;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CheckBox;

import com.smarteist.autoimageslider.SliderView;

public class ItemViewHolder {
    private TextView itemNameView;
    private TextView itemPurchaseDateView;
    private TextView itemValueView;
    private CheckBox itemCheckBox;
    private LinearLayout itemImageView;
    private LinearLayout description_container;
    private RelativeLayout noImagePlaceholder;
    private LinearLayout sliderViewFrame;
    private SliderView sliderView;
    private LinearLayout tags_container;

    public ItemViewHolder(TextView itemNameView, TextView itemPurchaseDateView, TextView itemValueView, CheckBox itemCheckBox, LinearLayout itemImageView, LinearLayout description_container, RelativeLayout noImagePlaceholder, LinearLayout sliderViewFrame, SliderView sliderView, LinearLayout tags_container) {
        this.itemNameView = itemNameView;
        this.itemPurchaseDateView = itemPurchaseDateView;
        this.itemValueView = itemValueView;
        this.itemCheckBox = itemCheckBox;
        this.itemImageView = itemImageView;
        this.description_container = description_container;
        this.noImagePlaceholder = noImagePlaceholder;
        this.sliderViewFrame = sliderViewFrame;
        this.sliderView = sliderView;
        this.tags_container = tags_container;
    }

    public ItemViewHolder() {}

    public ItemViewHolder(TextView nameView, TextView purchaseDateView, TextView estimatedValueView, LinearLayout imageView, LinearLayout descriptionContainer, RelativeLayout noImagePlaceholder, LinearLayout sliderViewFrame, SliderView sliderView, LinearLayout tagsContainer) {
        this.itemNameView = itemNameView;
        this.itemPurchaseDateView = itemPurchaseDateView;
        this.itemValueView = itemValueView;
        this.itemImageView = itemImageView;
        this.description_container = description_container;
        this.noImagePlaceholder = noImagePlaceholder;
        this.sliderViewFrame = sliderViewFrame;
        this.sliderView = sliderView;
        this.tags_container = tags_container;

    }

    public TextView getItemNameView() {
        return itemNameView;
    }

    public void setItemNameView(TextView itemNameView) {
        this.itemNameView = itemNameView;
    }

    public TextView getItemPurchaseDateView() {
        return itemPurchaseDateView;
    }

    public void setItemPurchaseDateView(TextView itemPurchaseDateView) {
        this.itemPurchaseDateView = itemPurchaseDateView;
    }


    public LinearLayout getItemImageView() {
        return itemImageView;
    }

    public void setItemImageView(LinearLayout itemImageView) {
        this.itemImageView = itemImageView;
    }

    public TextView getItemValueView() {
        return itemValueView;
    }

    public void setItemValueView(TextView itemValueView) {
        this.itemValueView = itemValueView;
    }

    public CheckBox getItemCheckBox() {
        return itemCheckBox;
    }

    public void setItemCheckBox(CheckBox itemCheckBox) {
        this.itemCheckBox = itemCheckBox;
    }

    public RelativeLayout getItemNoImagePlaceholder() {
        return noImagePlaceholder;
    }

    public LinearLayout getItemSliderViewFrame() {
        return sliderViewFrame;
    }

    public SliderView getItemSliderView() {
        return sliderView;
    }

    public LinearLayout getItemTagsContainer() {
        return tags_container;
    }

    public LinearLayout getItemDescriptionContainer() {
        return description_container;
    }
}
