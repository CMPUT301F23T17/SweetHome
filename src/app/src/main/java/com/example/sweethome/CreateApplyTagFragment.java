package com.example.sweethome;

/* necessary imports */
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @class CreateApplyTagFragment
 *
 * <p>This class implements the fragment for creating new tags
 * and deleting tags.</p>
 *
 * @date <p>December 4, 2023</p>
 *
 * @source <p>Guide on Fragments. The guide was most recently updated (2023, March 22).
 * Android Developers. The content of the guide on Android Developers is
 * licensed under the Apache 2.0 license.
 * @link https://developer.android.com/guide/fragments/. </p>
 */
public class CreateApplyTagFragment extends Fragment {
    /* attributes of this class */
    private CollectionReference itemsRef;
    private RecyclerView tagsRecyclerView;
    private View view;
    private String fragmentTitle;
    private String fragmentBodyTitle;

    // tags
    private ArrayList<Tag> tags;
    private ArrayList<Tag> allTags = new ArrayList<>();
    private LinearLayout spinnerContainer;
    private Spinner tagFilterSpinner;
    private ArrayAdapter<String> tagFilterAdapter;
    private ArrayList<String> tagsList = new ArrayList<>();

    private String username;
    private String purpose;

    private TextInputLayout textInputLayout;

    private TagsAdapter tagsAdapter;
    private Button applyButton;
    private ListView selectedItem;
    private TextView fragmentTitleName;
    private TextView fragmentBodyTitleName;
    private TextInputEditText tagInputEditText;
    private TextView tags_count_field;
    private String tagInput;
    private ArrayList<Item> itemList;
    private Map tagInfo;
    private Button createButton;
    private Button doneButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tagsRef = db.collection("tags");

    public CreateApplyTagFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.create_apply_tag_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            username = getArguments().getString("USER");
            purpose = getArguments().getString("PURPOSE");

            if (purpose == "apply_tag") {
                itemList = (ArrayList<Item>) getArguments().getSerializable("item_list");
                fragmentTitle = "Apply tags to items";
                fragmentBodyTitle = "Selected Items";
            } else {
                fragmentTitle = "Create a new tag";
                fragmentBodyTitle = "Existing tags";
            }
        }

        itemsRef = db.collection("items");
        /* listen for changes in the collection and update our list of items accordingly */
        itemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString()); //if there was any error, log it
                }
            }
        });
        tagsRef = db.collection("tags");
        tagsList = new ArrayList<>();
        tagFilterSpinner = view.findViewById(R.id.tag_filter_spinner);
        tagsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString()); //if there was any error, log it
                }
                if (value != null) {
                    getAllTagsFromDatabase(tagsRef,purpose); //otherwise get all relevant tags currently in the items collection and display them in our list
                    tagFilterAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, tagsList);
                    tagFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    tagFilterSpinner.setAdapter(tagFilterAdapter);
                    tagFilterAdapter.notifyDataSetChanged();

                }
            }
        });

        tagInputEditText = view.findViewById(R.id.tag_editable_input);
        tags_count_field = view.findViewById(R.id.tags_count_field);

        tagsRecyclerView = view.findViewById(R.id.tags_recycler_view);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        tags = new ArrayList<Tag>();
        tagsAdapter = new TagsAdapter(view.getContext(), tags, username);
        tagsRecyclerView.setAdapter(tagsAdapter);

        // Find views
        applyButton = view.findViewById(R.id.apply_new_tag_button);
        createButton = view.findViewById(R.id.create_new_tag_button);
        textInputLayout = view.findViewById(R.id.text_input_layout);

        // Initially, hide the Apply button and show the tag input wrapper
        applyButton.setVisibility(View.GONE);
        textInputLayout.setVisibility(View.VISIBLE);

        // Check if purpose is to apply tags
        if (purpose == "apply_tag") {
            // If arguments exist, hide the tag input wrapper
            textInputLayout.setVisibility(View.GONE);

            // Additionally, hide the RecyclerView
            tagsRecyclerView.setVisibility(View.GONE);

            // Show the Apply button
            applyButton.setVisibility(View.VISIBLE);
            if (tagsRef != null) {
                this.getAllTagsFromDatabase(tagsRef, purpose);
            } else {
                Log.e("CreateTagFragment", "tagsRef is null");
                // Handle the null reference case accordingly
            }
            ItemsApplyTagsAdapter itemsAdapter = new ItemsApplyTagsAdapter(view.getContext(), itemList);
            selectedItem = view.findViewById(R.id.selected_item_list);
            selectedItem.setVisibility(View.VISIBLE);
            selectedItem.setAdapter(itemsAdapter);
            createButton.setVisibility(View.GONE);

            if (itemList != null) {
                spinnerContainer = view.findViewById(R.id.spinner_container);
                spinnerContainer.setVisibility(View.VISIBLE);
                tagFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        if (parent.getCount() > 0) {
                            // Set selection to the item at index 0
                            parent.setSelection(0);
                            // Retrieve the selected item at index 0
                            String selectedTag = (String) parent.getItemAtPosition(0);
                        }
                    }
                });

                // Find the Apply button in the fragment layout and set an OnClickListener
                applyButton = view.findViewById(R.id.apply_new_tag_button);
                applyButton.setOnClickListener(v -> {
                    // Get the selected tag from the spinner
                    String selectedTag = (String) tagFilterSpinner.getSelectedItem();

                    // Check if a tag is selected
                    if (selectedTag != null && !selectedTag.isEmpty()) {
                        // Apply the selected tag to each item in the item list
                        applyTagToItemList(itemList, selectedTag);
                    } else {
                        // Show a message if no tag is selected
                        Toast.makeText(getContext(), "No tag selected", Toast.LENGTH_SHORT).show();
                    }
                });


            } else {
                Log.e("ItemListContents", "Item list is null");
            }
        } else {
            // Handle the case where getArguments() returns null
            applyButton.setVisibility(View.GONE);
            createButton.setVisibility(View.VISIBLE);

            Log.e("CreateTagFragment", "Arguments are null");
        }

        fragmentTitleName = view.findViewById(R.id.tag_fragment_title);
        fragmentBodyTitleName = view.findViewById(R.id.tags_count_field);
        // Set initial titles
        fragmentTitleName.setText(fragmentTitle);
        fragmentBodyTitleName.setText(fragmentBodyTitle);

        createButton.setOnClickListener(v -> {
            tagInput = tagInputEditText.getEditableText().toString();
            if(tagInput.length() < 20 && tagInput.length() > 0) {
                addTagToDatabase(tagInput);
                getAllTagsFromDatabase(tagsRef, purpose);
            } else if (tagInput.length() == 0) {
                Toast.makeText(getContext(),"No tag specified.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),"Tag name too long.", Toast.LENGTH_SHORT).show();
            }
        });

        doneButton = view.findViewById(R.id.done_create_button);
        doneButton.setOnClickListener(v -> {
            MainActivity activity = (MainActivity) getActivity();
            activity.closeFragment();
        });
    }
    /**
     * Retrieves all tags from the Firestore database and populates the local tags list.
     *
     * @param tagsRef Reference to the Firestore collection containing tags.
     */
    private void getAllTagsFromDatabase(CollectionReference tagsRef, String purpose) {
        tagsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (purpose == "create_tag") {
                    tags.clear();
                } else if (purpose == "apply_tag") {
                    tagsList.clear();
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    ArrayList<String> usernames = (ArrayList<String>) doc.get("usernames");
                    Tag tag = doc.toObject(Tag.class); //convert the contents of each document in the tags collection to an item object
                    tag.setId(doc.getId()); //set the item ID
                    tag.setName((String) doc.get("name"));
                    tag.setTimestamp((Timestamp) doc.get("timestamp"));
                    tag.setUsernames(usernames);
                    allTags.add(tag);
                    if (usernames.contains(username)) {
                        Log.i("Firestore", String.format("Tag %s fetched", tag.getTagName())); //log the name of the tag we successfully got from the db
                        if (purpose == "create_tag") {
                            tags.add(tag); //add the item object to our item list
                        } else if (purpose == "apply_tag") {
                            tagsList.add(tag.getTagName());
                        }
                    }
                }
                tags.sort((tag1, tag2) -> tag2.getTagTimestamp().compareTo(tag1.getTagTimestamp()));
                tagsAdapter.notifyDataSetChanged();
                tagFilterAdapter.notifyDataSetChanged();
                if (purpose == "create_tag") {
                    tags_count_field.setText("Existing tags (" + tagsAdapter.getItemCount() + ")");
                }
                Log.i("Item count", Integer.toString(tagsAdapter.getItemCount()));
            }
        });
    }

    /**
     * Applies the selected tag to the tag list of each item in the provided itemList if the tag
     * is not already present in the item's tag list. It updates the itemList in the database after
     * applying the tags.
     *
     * @param itemList    The list of Item objects to which the selected tag will be applied.
     * @param selectedTag The tag to be applied to the items.
     */
    private void applyTagToItemList(ArrayList<Item> itemList, String selectedTag) {
        for (Item item : itemList) {
            ArrayList<String> itemTags = item.getTags();
            if (!itemTags.contains(selectedTag)) {
                // Check if the item does not contain the selected tag and update it
                updateItemTags(item, selectedTag);
            }
        }
    }

    /**
     * Updates the Firestore database with a new tag for a specific item if the tag is not already present in the item's tags.
     *
     * @param item        The Item object to be updated.
     * @param selectedTag The tag to be added to the item's tags.
     */
    private void updateItemTags(Item item, String selectedTag) {
        DocumentReference itemRef = itemsRef.document(item.getItemId());

        // Fetch current tags from Firestore
        itemRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Get the current tags
                    ArrayList<String> currentTags = (ArrayList<String>) document.get("tags");

                    if (currentTags != null) {
                        // Check if the current tags of this specific item do not contain the selected tag
                        if (!currentTags.contains(selectedTag)) {
                            // Update the tags for this specific item
                            currentTags.add(selectedTag);

                            // Update the "tags" field in Firestore for this specific item
                            itemRef.update("tags", currentTags)
                                    .addOnSuccessListener(aVoid -> {
                                        // Handle success
                                        Log.d("Firestore", "Item tags updated successfully");
                                        Toast.makeText(getContext(), "Tag applied", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failure
                                         Log.e("Firestore", "Error updating item tags", e);
                                    });
                        }
                    }
                } else {
                    Log.d("Firestore", "No such document");
                }
            } else {
                Log.e("Firestore", "Error getting document", task.getException());
            }
        });
    }

    private void addTagToDatabase(String tagInput) {
        tagInput = tagInput.substring(0, 1).toUpperCase() + tagInput.substring(1);
        Tag existingTag = null;
        for (Tag tagObject : allTags) {
            if (tagObject.getTagName().equals(tagInput)) {
                existingTag = tagObject;
                break;
            }
        }
        tagInfo = new HashMap<>();
        tagInfo.put("name", tagInput);
        tagInfo.put("timestamp", Timestamp.now());
        if (existingTag == null) {
            ArrayList<String> usernames = new ArrayList<>();
            usernames.add(username);
            tagInfo.put("usernames", usernames);
            DocumentReference newDBTag = tagsRef.document();
            newDBTag.set(tagInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    tagInputEditText.setText("");
                    Toast.makeText(getContext(),"New tag created.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Tag creation failed.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ArrayList<String> existingUsernames = existingTag.getTagUsernames();
            if (!existingUsernames.contains(username)) {
                DocumentReference existingTagDoc = tagsRef.document(existingTag.getTagId());
                existingUsernames.add(username);
                tagInfo.put("usernames", existingUsernames);
                existingTagDoc.update(tagInfo);
                tagInputEditText.setText("");
                Toast.makeText(getContext(),"New tag created.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),"Tag already exists.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
