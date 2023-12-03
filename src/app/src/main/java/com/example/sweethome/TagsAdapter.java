package com.example.sweethome;

/* necessary imports */
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * @class TagsAdapter
 *
 * <p>This class implements the adapter for tag entry views on the list
 * of existing tags found in CreateApplyTagFragment</p>
 *
 * @date <p>December 4, 2023</p>
 *
 * @source How to create RecyclerView in Android - Android Studio Tutorial.
 * (2020, November 29). [Video]. YouTube.
 * @link https://youtu.be/yTvwfEmzMZY
 */
public class TagsAdapter extends RecyclerView.Adapter<TagViewHolder> {
    /* attributes of this class */
    Context context;
    ArrayList<Tag> tags;
    FirebaseFirestore db;
    CollectionReference tagsRef;
    CollectionReference itemsRef;
    int positionToRemove;
    String username;

    public TagsAdapter(Context context, ArrayList<Tag> tags, String username) {
        this.context = context;
        this.tags = tags;
        this.username = username;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db = FirebaseFirestore.getInstance();
        tagsRef = db.collection("tags");
        itemsRef = db.collection("items");
        return new TagViewHolder(LayoutInflater.from(context).inflate(R.layout.tag_created, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.getTagNameView().setText(tags.get(position).getTagName());
        holder.getRemoveTagIconView().setOnClickListener(v -> {
            positionToRemove = holder.getAdapterPosition();
            String tagId = tags.get(positionToRemove).getTagId();
            // only delete a tag document if it's all yours and it's not associated with any item in db, otherwise just update the usernames list of the tag document
            tagsRef.document(tagId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot tagDocumentSnapshot) {
                    if (tagDocumentSnapshot.exists()) {
                        String tagName = tagDocumentSnapshot.getString("name");
                        List<String> tagUsernames = (List<String>) tagDocumentSnapshot.get("usernames");
                        itemsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                String itemName = "";
                                ArrayList<String> associatedWithItemsOf = new ArrayList<>();
                                for (QueryDocumentSnapshot itemDocumentSnapshot : queryDocumentSnapshots) {
                                    ArrayList<String> tags = (ArrayList<String>) itemDocumentSnapshot.get("tags");
                                    String itemUsername = itemDocumentSnapshot.getString("username");
                                    if (tags.contains(tagName) && !associatedWithItemsOf.contains(itemUsername)) {
                                        associatedWithItemsOf.add(itemUsername);
                                        if (itemName.length() < 1 && itemUsername.equals(username)) {
                                            itemName = itemDocumentSnapshot.getString("name");
                                        }
                                    }
                                }
                                if (tagUsernames.size() <= 1 && associatedWithItemsOf.size() < 1) {
                                    deleteTagFromDatabase(tagId);
                                } else {
                                    if (associatedWithItemsOf.contains(username)) {
                                        Toast.makeText(context,"Cannot remove tag. Tag belongs to item: '" + itemName + "'.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (tagUsernames.size() > 1) {
                                            tagUsernames.remove(username);
                                            tagsRef.document(tagId).update("usernames", tagUsernames);
                                            Toast.makeText(context,"Tag deleted.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            });
        });
    }

    public void deleteTagFromDatabase(String tagId) {
        tagsRef.document(tagId)
            .delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                    Toast.makeText(context,"Tag deleted.", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Firestore", "DocumentSnapshot deleted failed!");
                    Toast.makeText(context, "Failed to delete tag.", Toast.LENGTH_SHORT).show();
                }
            });
        tags.remove(positionToRemove);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }
}
