package com.example.sweethome;

/* necessary imports */
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;

/**
 * @class CustomAddTagsField
 *
 * <p>This class extends the AppCompatEdittext class to use EditText component for adding and
 * displaying tags. It includes addTag method to add tags when the user presses the Enter key or
 * when the input action is done. The added tags are displayed as individual items with distinctive
 * colors and can removed.</p>
 * <p>This class also maintains an internal list of added tag names, which can be retrieved
 * using the getAddedTagNames method.</p>
 * <p>This class utilizes a {@link ColorGenerator } to generate random background colors for the
 * tags and determine suitable text color that contrasts well with the background color. </p>
 * <p>Note: The layout for each tag is defined under the “tag_item.xml” layout resource file,
 * referenced by R.layout.tag_item identifier <p>
 *
 * @date <p>November 10, 2023</p>
 */
public class CustomAddTagsField extends AppCompatEditText {
    /* attributes of this class */
    private LinearLayout tags_container;
    private ArrayList<String> addedTagNames = new ArrayList<>();
    private ArrayList<String> removedTags = new ArrayList<>();

    /* constructors for this class */
    public CustomAddTagsField(Context context) {
        super(context);
        init();
    }

    public CustomAddTagsField(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomAddTagsField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /* initialize the CustomAddTagsField */
    private void init() {
        tags_container = new LinearLayout(getContext());
        tags_container.setOrientation(LinearLayout.HORIZONTAL);
        tags_container.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        tags_container.setPadding(0, 16, 0, 0);
        this.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                addTag(getText().toString().trim());
                setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                return true;
            }
            return false;
        });
        /* add an editor action listener for "Done" or "Next" actions */
        this.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    addTag(getText().toString().trim().replace(",", ""));
                    setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                    Context context = textView.getContext();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });
        /* add a text changed listener to change the typeface when the text is changed */
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }
            }
        });
    }

    /**
     * Add a tag to the CustomAddTagsField.
     *
     * @param tagName The name of the tag to be added.
     */
    public void addTag(String tagName) {
        this.setError(null);
        if (!tagName.isEmpty() && tagName.length() <= 20) {
            tagName = tagName.substring(0, 1).toUpperCase() + tagName.substring(1); // capitalize the first letter
            this.addedTagNames.add(tagName);
            setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            View tag_item = LayoutInflater.from(getContext()).inflate(R.layout.removable_tag, null);
            LinearLayout tag_wrapper = tag_item.findViewById(R.id.tag_wrapper);
            ColorGenerator newColor = new ColorGenerator();
            int tag_color = newColor.getColorCode();
            int tag_name_color = newColor.getTextColorCode();
            tag_wrapper.setBackgroundColor(tag_color);
            TextView tag_name_field = tag_item.findViewById(R.id.tag_name);
            ImageView remove_tag_button = tag_item.findViewById(R.id.remove_tag_button);
            tag_name_field.setText(tagName);
            tag_name_field.setTextColor(tag_name_color);
            remove_tag_button.setColorFilter(tag_name_color);
            tags_container = ((Activity) getContext()).findViewById(R.id.tags_container);
            if (tags_container != null) {
                tags_container.addView(tag_item);
            }
            String finalTagName = tagName;
            remove_tag_button.setOnClickListener(v -> {
                removeTag(finalTagName);
                tags_container.removeView(tag_item);
            });
            setText(null);
        } else if (tagName.length() > 20) {
            this.setError("Tag cannot be longer than 20 characters.");
        }
    }

    /**
     * Remove a tag name from the list.
     */
    public void removeTag(String tagName) {
        this.removedTags.add(tagName);
        this.addedTagNames.remove(tagName);
    }

    /**
     * Get the list of added tag names.
     *
     * @return ArrayList containing the added tag names.
     */
    public ArrayList<String> getAddedTagNames() {
        return this.addedTagNames;
    }

    /**
     * Return a list of removed tag names.
     */
    public ArrayList<String> getRemovedTagNames() {
        return this.removedTags;
    }
}