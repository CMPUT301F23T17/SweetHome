package com.example.sweethome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the fragment for creating new tags
 * and deleting tags
 *
 * Sources: https://developer.android.com/guide/fragments/
 *
 */
public class CreateTagFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private CollectionReference tagsRef;
    private RecyclerView tagsRecyclerView;
    private View view;
    private ArrayList<Tag> tags;
    private String username;
    private TagsAdapter tagsAdapter;
    private TextInputEditText tagInputEditText;
    private String tagInput;
    private Map tagInfo;
    private Button createButton;
    private Button doneButton;
    private FirebaseFirestore db;

    public CreateTagFragment() {
        // Required empty public constructor
        super(R.layout.create_tag_fragment);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param username name of the current user of the app.
     * @return A new instance of fragment CreateTagFragment.
     */

    public static CreateTagFragment newInstance(String username) {
        CreateTagFragment fragment = new CreateTagFragment();
        Bundle args = new Bundle();
        args.putString("USER", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            username = getArguments().getString("USER");
        }
        db = FirebaseFirestore.getInstance();
        tagsRef = db.collection("tags");
        tagsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString()); //if there was any error, log it
                }
                if (value != null) {
                    getAllTagsFromDatabase(tagsRef); //otherwise get all relevant tags currently in the items collection and display them in our list
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.create_tag_fragment, container, false);
        tagInputEditText = view.findViewById(R.id.tag_editable_input);

        tagsRecyclerView = view.findViewById(R.id.tags_recycler_view);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        tags = new ArrayList<Tag>();
        tagsAdapter = new TagsAdapter(view.getContext(), tags);
        tagsRecyclerView.setAdapter(tagsAdapter);

        createButton = view.findViewById(R.id.create_new_tag_button);
        createButton.setOnClickListener(view -> {
            tagInput = tagInputEditText.getEditableText().toString();
            if(tagInput.length() < 15 && tagInput.length() > 0) {
                addTagToDatabase(tagInput);
                getAllTagsFromDatabase(tagsRef);
            } else if (tagInput.length() == 0) {
                Toast.makeText(getContext(),"Tag name too short", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),"Tag name too long", Toast.LENGTH_SHORT).show();
            }

        });


        doneButton = view.findViewById(R.id.done_create_button);
        doneButton.setOnClickListener(view -> {
            MainActivity activity = (MainActivity) getActivity();
            activity.closeFragment();
        });

        return view;

    }

    private void getAllTagsFromDatabase(CollectionReference tagsRef) {
        tagsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                tags.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    ArrayList<String> usernames = (ArrayList<String>) doc.get("usernames");
                    if (usernames.contains(username)) {
                        Tag tag = doc.toObject(Tag.class); //convert the contents of each document in the tags collection to an item object
                        tag.setId(doc.getId()); //set the item ID
                        tag.setName((String) doc.get("name"));
                        tag.setUsernames(usernames);
                        Log.i("Firestore", String.format("Tag %s fetched", tag.getTagName())); //log the name of the tag we successfully got from the db
                        tags.add(tag); //add the item object to our item list
                    }
                }
                tagsAdapter.notifyDataSetChanged();
                Log.i("Item count", Integer.toString(tagsAdapter.getItemCount()));

            }
        });
    }

    private void addTagToDatabase(String tagInput) {
            Tag existingTag = null;
            for (Tag tagObject : tags) {
                if (tagObject.getTagName().equals(tagInput)) {
                    existingTag = tagObject;
                    break;
                }
            }
            tagInfo = new HashMap<>();
            tagInfo.put("name", tagInput);
            if (existingTag == null) {
                ArrayList<String> usernames = new ArrayList<>();
                usernames.add(username);
                tagInfo.put("usernames", usernames);
                DocumentReference newDBTag = tagsRef.document();
                newDBTag.set(tagInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(),"New tag created", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Tag creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                ArrayList<String> existingUsernames = existingTag.getTagUsernames();
                if (!existingUsernames.contains(username)) {
                    DocumentReference existingTagDoc = tagsRef.document(existingTag.getTagId());
                    existingUsernames.add(username);
                    tagInfo.put("usernames", existingUsernames);
                    existingTagDoc.update(tagInfo);
                }
            }
    }
}