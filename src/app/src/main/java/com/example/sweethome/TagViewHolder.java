package com.example.sweethome;

/* necessary imports */
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @class ItemViewHolder
 *
 * <p>This class implements the viewholder for each tag entries on the
 * list found inside {@link com.example.sweethome.CreateApplyTagFragment}</p>
 *
 * @date <p>December 4, 2023</p>
 *
 * @source UI Guide on Create dynamic lists with RecyclerView.
 * The guide was most recently updated (2023, November 30).
 * Android Developers. The content of the guide on Android
 * Developers is licensed under the Apache 2.0 license.
 * @link https://developer.android.com/develop/ui/views/layout/recyclerview
 */
public class TagViewHolder extends RecyclerView.ViewHolder {
    /* attributes of this class */
    ImageView removeTagIcon;
    TextView tagName;
    public TagViewHolder(@NonNull View tagView) {
        super(tagView);
        removeTagIcon = tagView.findViewById(R.id.remove_tag_from_list);
        tagName = tagView.findViewById(R.id.tag_list_name);
        ColorGenerator newColor = new ColorGenerator();
        int tagColor = newColor.getColorCode();
        int textColor = newColor.getTextColorCode();
        getTagNameView().setBackgroundColor(tagColor);
        getTagNameView().setTextColor(textColor);
    }

    public TextView getTagNameView() {
        return tagName;
    }
    public ImageView getRemoveTagIconView(){
        return removeTagIcon;
    }

}
