package com.example.sweethome;

/* necessary imports */
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @class MainActivity
 *
 * <p>This class extends the AppCompatActivity class and handles the main
 * screen of the SweetHome application. From here, users can
 * interact with various features in the app.</p>
 * <p>The “screen” extra can be set to “Add Item” or “View / Edit” to indicate the desired screen
 * mode in the {@link com.example.sweethome.ManageItemActivity}.</p>
 *
 * @date <p>December 4, 2023</p>
 *
 * @source How to Delete Data from Firebase Firestore in Android?
 * The article most-recently was contributed by chaitanyamunje and improved by simmytarika5,
 * gabaa406, abhishek0719kadiyan. (2022, December 22). GeeksForGeeks.
 * The content of the article on GeeksForGeeks is licensed under CCBY-SA.
 * @link https://www.geeksforgeeks.org/how-to-delete-data-from-firebase-firestore-in-android/
 */
public class MainActivity extends AppCompatActivity implements IFilterable, NetworkChangeReceiver.NetworkChangeListener {
    /* attributes of this class */
    private ArrayList<Item> itemList;  // do not ever mutate, except for sorting and populating
    private ListView itemListView;
    private TextView totalEstimatedValue;
    private ItemsCustomAdapter itemAdapter;
    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    private CollectionReference tagsRef;
    private Spinner sortSpinner;
    private Spinner tagFilterSpinner;
    private Button addItemButton;
    private ArrayAdapter<CharSequence> sortAdapter;
    private ArrayAdapter<String> tagFilterAdapter;
    private ArrayList<Item> selectedItems;
    final Context context = this;
    private FragmentContainerView defineTagsFragmentContainer;
    private LinearLayout filterPanel;
    private ImageView filterIcon;
    private Button filterApplyButton;
    private Button clear_date_button;
    private Button createTagButton;
    private Button addTagButton;
    private EditText keywordField;
    private EditText makeField;
    private TextView calendar_data;
    private Boolean datePickerShown = false;
    // Declare MaterialDatePicker as a field
    private MaterialDatePicker<Pair<Long, Long>> dateRangePicker;
    private Long selectedStartDate;
    private Long selectedEndDate;
    /* constants */
    private final long ONE_DAY = 86400000;
    private final long ONE_HOUR = 3600000;
    private final long ONE_SECOND = 1000;
    private static final int CAMERA_PERMISSION_REQUEST = 123;
    private AppContext app;
    private ArrayList<String> tagsList = new ArrayList<>();
    private String selectedTagForFiltering = "All";
    private TextView selected_filtering_tag_field;
    private String loggedInUsername;
    private EditText searchInput;
    private String searchText = "";
    private TextView noItemsFound;
    private TextView items_count_field;
    private CreateApplyTagFragment ctFragment;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the current user
        app = (AppContext) getApplication();
        Intent main = getIntent();
        loggedInUsername = main.getStringExtra("USERNAME");
        if (loggedInUsername != null) {
            app.setUsername(loggedInUsername);
        }

        items_count_field = findViewById(R.id.items_count);

        // check if the user has granted permission to access their camera
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }

        /* set up a connection to our db and references to the items and tags collection */
        db = FirebaseFirestore.getInstance();
        itemsRef = db.collection("items");
        tagsRef = db.collection("tags");

        /* set up our list of items, find the list on our frontend layout, and set the corresponding array adapter */
        itemList = new ArrayList<Item>();
        itemListView = findViewById(R.id.item_list);
        itemAdapter = new ItemsCustomAdapter(this, itemList);
        itemListView.setAdapter(itemAdapter);
        loggedInUsername = app.getUsername(); // Assuming `getUsername` fetches the current user's username

        /* Retrieve all existing items(if there are any) from Firestore Database */
        if (itemList.size() <= 0) {
            getAllItemsFromDatabase(itemsRef);
        }

        /* setup the sorting spinner */
        sortSpinner = findViewById(R.id.spinner_sort_options);
        sortAdapter = ArrayAdapter.createFromResource(this, R.array.sort_options, android.R.layout.simple_spinner_dropdown_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        /* sorting spinner selection listener */
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle sorting based on selection
                String selectedSortOption = parentView.getItemAtPosition(position).toString();
                sortDataList(selectedSortOption, getApplicationContext()); // Sort and load data based on the selected option
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //do nothing here
            }
        });

        /* Retrieve all existing tags(if there are any) from Firestore Database */
        if (tagsList.size() <= 0) {
            getAllTagsFromDatabase(tagsRef);
        }

        /* setup the filter by tag spinner */
        selected_filtering_tag_field = findViewById(R.id.selected_filtering_tag_field);
        tagFilterSpinner = findViewById(R.id.tag_filter_field);
        tagFilterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tagsList);
        tagFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagFilterSpinner.setAdapter(tagFilterAdapter);

        /* filter by tag spinner listener */
        tagFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle filtering by tag based on selection
                String selectedTagToFilterBy = parentView.getItemAtPosition(position).toString();
                selectedTagForFiltering = selectedTagToFilterBy;
                selected_filtering_tag_field.setText(selectedTagForFiltering);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //do nothing here
            }
        });

        addItemButton = findViewById(R.id.add_button);
        addItemButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ManageItemActivity.class);
            intent.putExtra("screen", "Add Item");
            startActivity(intent);
        });

        /* find our frontend elements for filtering */
        filterPanel = findViewById(R.id.filter_panel);
        filterIcon = findViewById(R.id.filter_button);
        filterApplyButton = findViewById(R.id.apply_filter_button);
        makeField = findViewById(R.id.make_field);
        keywordField = findViewById(R.id.keyword_field);
        calendar_data = findViewById(R.id.calendar_data);

        /* set the view of the filter panel and onClicklisteners for the icon and button */
        filterPanel.setVisibility(View.GONE); //should be invisible until the filterIcon is pressed
        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterPanel.getVisibility() == View.VISIBLE) { //if the panel is already visible
                    filterIcon.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.black), PorterDuff.Mode.SRC_IN);
                    filterPanel.setVisibility(View.GONE); //then make it invisible
                    /* clear the edit texts for the next time the user uses the panel */
                    makeField.setText("");
                    keywordField.setText("");
                    getAllItemsFromDatabase(itemsRef); //also clear all of the filters
                } else {
                    filterIcon.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.light_grey), PorterDuff.Mode.SRC_IN);
                    filterPanel.setVisibility(View.VISIBLE); //otherwise just show the panel since it must currently be invisible
                    selectedStartDate = 0L; //restart the start day
                    selectedEndDate = 0L; //restart the end day
                    calendar_data.setText(""); //clear the textview
                }
            }
        });
        filterApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeFilters();
            }
        });

        // Button to open Edit Item screen
        Button addItemButton = findViewById(R.id.add_button);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ManageItemActivity.class);
                intent.putExtra("screen", "Add Item");
                startActivity(intent);
            }
        });

        // Date range picker
        ImageView openCalendarButton = findViewById(R.id.calendar_button);

        // Set an OnClickListener to handle the button click
        openCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!datePickerShown) {
                    dateRangePicker = createMaterialDatePicker();
                    datePickerShown = true;
                    String uniqueTag = "MaterialDatePicker_" + System.currentTimeMillis();
                    dateRangePicker.show(getSupportFragmentManager(), uniqueTag);
                }
            }
        });

        final FloatingActionButton tagActionOnButton = findViewById(R.id.tag_action_on_button);
        final FloatingActionButton tagActionOffButton = findViewById(R.id.tag_action_off_button);
        LinearLayout action_panel = findViewById(R.id.action_panel);
        defineTagsFragmentContainer = findViewById(R.id.create_tag_fragment);
        createTagButton = action_panel.findViewById(R.id.create_tag_button);
        createTagButton.setOnClickListener(view -> {
            action_panel.setVisibility(View.GONE);
            defineTagsFragmentContainer.setVisibility(View.VISIBLE);
            Bundle args = new Bundle();
            args.putString("USER", app.getUsername());
            args.putString("PURPOSE", "create_tag");
            ctFragment = new CreateApplyTagFragment();
            ctFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.create_tag_fragment, ctFragment)
                    .commit();
            tagActionOnButton.setVisibility(View.VISIBLE);
        });
        tagActionOnButton.setOnClickListener(view -> {
            tagActionOnButton.setVisibility(View.GONE);
            action_panel.setVisibility(View.VISIBLE);
        });
        tagActionOffButton.setOnClickListener(view -> {
            tagActionOnButton.setVisibility(View.VISIBLE);
            action_panel.setVisibility(View.GONE);
        });

        /**
         * Sets an OnClickListener on the 'Add Tag' button. Upon clicking, it hides the panel, checks if at least one item is selected,
         * and if so, displays the fragment responsible for adding tags to the selected items.
         */
        addTagButton = action_panel.findViewById(R.id.add_tag_panel);
        addTagButton.setOnClickListener(view -> {
            /**
             * Retrieves the selected items from the item list view and initiates the process of adding tags to those selected items.
             */
            selectedItems = new ArrayList<Item>();
            for (int i = 0; i < itemListView.getCount(); i++) {
                Item item = itemAdapter.getItem(i);
                Boolean selected = item.isSelected();
                if (item != null && selected) {
                    selectedItems.add(item);
                }
            }
            if (selectedItems.size() < 1) {
                Toast.makeText(MainActivity.this, "Please select at least 1 item.", Toast.LENGTH_SHORT).show();
            } else {
                action_panel.setVisibility(View.GONE);
                defineTagsFragmentContainer.setVisibility(View.VISIBLE);
                // Prepare arguments to pass to the CreateApplyTagFragment
                Bundle args = new Bundle();
                args.putString("USER", app.getUsername());
                args.putString("PURPOSE", "apply_tag");
                args.putSerializable("item_list", selectedItems); // Selected items to add tags to

                ctFragment = new CreateApplyTagFragment();
                ctFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.create_tag_fragment, ctFragment)
                        .commit();
                tagActionOnButton.setVisibility(View.VISIBLE);
            }
        });

        final FloatingActionButton deleteActionButton = findViewById(R.id.delete_action_button);
        deleteActionButton.setOnClickListener(view -> {
            selectedItems = new ArrayList<Item>();
            for (int i = 0; i < itemListView.getCount(); i++) {
                Item item = itemAdapter.getItem(i);
                Boolean selected = item.isSelected();
                if (item != null && selected) {
                    selectedItems.add(item);
                }
            }
            final Dialog deleteDialog = new Dialog(context);
            deleteDialog.setContentView(R.layout.delete_popup);
            Button deleteButton = (Button) deleteDialog.findViewById(R.id.delete_button);
            Button cancelDeleteButton = (Button) deleteDialog.findViewById(R.id.cancel_delete);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItems(MainActivity.this, selectedItems, itemsRef);
                    if (filterPanel.getVisibility() == View.VISIBLE) { //if the filter panel is visible
                        filterPanel.setVisibility(View.GONE); //then make it invisible
                        /* clear the edit texts for the next time the user uses the panel */
                        makeField.setText("");
                        keywordField.setText("");
                        calendar_data.setText("");
                        getAllItemsFromDatabase(itemsRef); //also clear all of the filters
                    }
                    deleteDialog.dismiss();
                }
            });

            cancelDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                }
            });

            if (selectedItems.size() > 0) {
                deleteDialog.show();
            } else {
                Toast.makeText(MainActivity.this, "Please select at least 1 item.", Toast.LENGTH_SHORT).show();
            }
        });

        clear_date_button = findViewById(R.id.clear_date_button);
        clear_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedStartDate = 0L;
                selectedEndDate = 0L;
                calendar_data.setText("");
            }
        });

        /* listen for changes in the collection and update our list of items accordingly */
        itemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString()); //if there was any error, log it
                }
                if (value != null) {
                    getAllItemsFromDatabase(itemsRef); //otherwise get all items currently in the items collection and display them in our list
                }
            }
        });

        /* listen for changes in the collection and update our list of tags accordingly */
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

        /* logout popup that shows the current signed in username */
        ImageView logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog profileDialog = new Dialog(context);
                profileDialog.setContentView(R.layout.profile_popup);
                TextView profileUsername = (TextView) profileDialog.findViewById(R.id.profile_username);
                profileUsername.setText(app.getUsername() + "'s\t" + getString(R.string.app_name));
                Button dialogLogoutButton = (Button) profileDialog.findViewById(R.id.profile_logout);
                Button dialogCancelButton = (Button) profileDialog.findViewById(R.id.profile_cancel);
                profileDialog.show();
                dialogLogoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Clear any logged-in user data if necessary, like SharedPreferences
                        // Sign out
                        FirebaseAuth.getInstance().signOut();
                        profileDialog.dismiss();
                        // cache username
                        SharedPreferences preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("username", app.getUsername());
                        editor.apply();
                        // Start LoginActivity
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the activity stack
                        startActivity(intent);
                        finish(); // Close the current activity
                    }
                });
                dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileDialog.dismiss();
                    }
                });
            }
        });
        searchInput = findViewById(R.id.search_input);
        noItemsFound = findViewById(R.id.no_items_found);

        // Set a TextWatcher for the search input field
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the list as the user types
                String searchText = charSequence.toString().trim().toLowerCase();
                String make = makeField.getText().toString();
                String keyword = keywordField.getText().toString();
                if (searchText.length() > 0) {
                    performSearch(searchText);
                } else if ((searchText.length() <= 0) && (make.trim().isEmpty() && keyword.trim().isEmpty() && (selectedEndDate==0L || selectedStartDate==0L) && selectedTagForFiltering.equals("All"))) {
                    getAllItemsFromDatabase(itemsRef);
                } else if ((searchText.length() <= 0) && (!make.trim().isEmpty() || !keyword.trim().isEmpty() || (selectedEndDate!=0L && selectedStartDate!=0L) || !selectedTagForFiltering.equals("All"))) {
                    noItemsFound.setVisibility(View.GONE);
                    executeFilters();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeReceiver.setListener(this);
    }

    // Performs the search
    private void performSearch(String searchText) {
        ArrayList<Item> filteredItems = new ArrayList<>();
        ArrayList<Item> currentList = itemAdapter.getCurrentItemList();
        searchText = searchText.toLowerCase().trim();
        for (Item item : currentList) {
            String itemName = item.getName().toLowerCase();
            if (itemName.startsWith(searchText)) {
                filteredItems.add(item);
            }
        }
        itemAdapter = new ItemsCustomAdapter(this, filteredItems);
        itemListView.setAdapter(itemAdapter);
        calculateTotalEstimatedValue();
        if (filteredItems.isEmpty()) {
            items_count_field.setText("");
            noItemsFound.setVisibility(View.VISIBLE);
        } else {
            items_count_field.setText(String.valueOf(itemAdapter.getCurrentItemList().size()) + " items.");
            noItemsFound.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the receiver
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the receiver
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        if (!isConnected) {
            Toast.makeText(MainActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
            // Redirect to the login screen
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        getApplicationContext().getCacheDir().delete();
    }

    public void closeFragment() {
        defineTagsFragmentContainer.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        calendar_data.setText("");
        filterPanel.setVisibility(View.GONE);
        makeField.setText("");
        keywordField.setText("");
        getApplicationContext().getCacheDir().delete();
    }

    private void executeFilters() {
        String make = makeField.getText().toString();
        String keyword = keywordField.getText().toString();
        itemAdapter.setCurrentItemList(itemList);
        if (!make.trim().isEmpty()){ //apply filter by make if it was provided
            filterByMake(make);
        }
        if(!keyword.trim().isEmpty()) { //apply filter by keyword if it was provided
            filterByKeyword(keyword);
        }
        if(selectedEndDate!=0L && selectedStartDate!=0L) { //apply filter by date range if it was selected
            /* get the selected dates by the user, adding extra time due to epoch conversion error */
            Date dateStart = new Date(selectedStartDate + ONE_DAY - ((ONE_DAY / 4) *3) + ONE_HOUR);
            Date dateEnd = new Date(selectedEndDate + ONE_DAY + (ONE_DAY / 4) + ONE_HOUR - ONE_SECOND);
            /* convert them to timestamps like our items store purchaseDate as */
            Timestamp start = new Timestamp(dateStart);
            Timestamp end = new Timestamp(dateEnd);
            filterByDate(start, end);
        }
        if (!selectedTagForFiltering.equals("All")) {
            filterByTag();
        }
        if(make.trim().isEmpty() && keyword.trim().isEmpty() && (selectedEndDate==0L || selectedStartDate==0L) && selectedTagForFiltering.equals("All")) { //if apply filter was selected but nothing is inputted
            getAllItemsFromDatabase(itemsRef);
        }
    }

    // Create the MaterialDatePicker with optional initial range
    private MaterialDatePicker<Pair<Long, Long>> createMaterialDatePicker() {
        MaterialDatePicker<Pair<Long, Long>> builder;

        if (calendar_data.getText().toString().length() > 0 && selectedStartDate != 0 && selectedEndDate != 0) {
            builder = MaterialDatePicker.Builder.dateRangePicker()
                    .setSelection(Pair.create(selectedStartDate, selectedEndDate))
                    .build();
        } else {
            builder = MaterialDatePicker.Builder.dateRangePicker().build();
        }

        // Set a listener for when the user confirms the date range
        builder.addOnPositiveButtonClickListener(selection -> {
            datePickerShown = false;
            // Get the selected date range
            selectedStartDate = selection.first;
            selectedEndDate = selection.second;
            // Update the button text
            updateCalendar(selectedStartDate, selectedEndDate);
        });

        // Set a listener for when the user clicks on the cancel/close button
        builder.addOnNegativeButtonClickListener(dialog -> {
            datePickerShown = false;
        });
        return builder;
    }

    private void updateCalendar(Long startDate, Long endDate) {
        if (startDate != 0 && endDate != 0) {
            // Format the date range string
            String formattedDateRange = formatDateRange(startDate, endDate);
            calendar_data.setText(formattedDateRange);
        } else {
            calendar_data.setText("");
        }
    }

    private String formatDateRange(Long startDate, Long endDate) {
        // You can customize the date format as needed
        // This is just an example format, adjust it based on your preference
        return String.format("%tF - %tF", startDate, endDate);
    }

    /**
     * Delete item/items
     * @param items list of item to delete
     */
    public void deleteItems(final Context context, ArrayList<Item> items, CollectionReference itemsRef) {
        int totalItems = items.size();
        AtomicInteger deletedItemsCount = new AtomicInteger(0);
        for (Item item: items) {
            // remove associated photos
            ArrayList<String> photos = item.getPhotos();
            for (String photoUrl : photos) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl);
                storageReference.delete().addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", "Photo successfully deleted!");
                        })
                        .addOnFailureListener(exception -> {
                            Log.e("Firestore", "Photo deleted failed!");
                        });
            }
            // remove the username from the tag
            ArrayList<String> associatedTags = item.getTags();
            for (String associatedTag : associatedTags) {
                int count = 0;
                for (Item itemObject : itemList) {
                    if (itemObject.getTags().contains(associatedTag) && itemObject.getUsername().equals(app.getUsername())) {
                        count += 1;
                    }
                    if (count >= 2) {
                        break;
                    }
                }
                if (count < 2) {
                    tagsRef
                        .whereEqualTo("name", associatedTag)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    List<String> usernames = (List<String>) document.get("usernames");
                                    usernames.remove(app.getUsername());
                                    tagsRef.document(document.getId()).update("usernames", usernames);
                                }
                            }
                        });
                    tagsList.remove(associatedTag);
                    tagFilterAdapter.notifyDataSetChanged();
                    selectedTagForFiltering = "";
                    selected_filtering_tag_field.setText(selectedTagForFiltering);
                }
            }
            // remove the item
            itemsRef.document(item.getItemId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                        int count = deletedItemsCount.incrementAndGet();
                        if (count == totalItems) {
                            // All items are deleted, show toast
                            Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "DocumentSnapshot deleted failed!");
                        Toast.makeText(context, "Failed to delete.", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    /**
     * Given a selected sorting option, sorts the current item
     * list according to the selected criteria.
     * @param selectedSortOption
     * @param context
     */
    public void sortDataList(String selectedSortOption, Context context) {
        if (selectedSortOption.equals(context.getString(R.string.sort_least_recent))) { //if we are sorting items by oldest to newest acquired
            itemList.sort((item1, item2) -> item1.getPurchaseDate().compareTo(item2.getPurchaseDate()));
        }
        else if (selectedSortOption.equals(context.getString(R.string.sort_most_recent))) { //if we are sorting items by newest to oldest acquired
            itemList.sort((item1, item2) -> item2.getPurchaseDate().compareTo(item1.getPurchaseDate()));
        }
        else if (selectedSortOption.equals(context.getString(R.string.sort_highest_value))) { //if we are sorting items by highest to lowest value
            itemList.sort((item1, item2) -> Double.compare(item2.getEstimatedValue(), item1.getEstimatedValue()));
        }
        else if (selectedSortOption.equals(context.getString(R.string.sort_lowest_value))) { //if we are sorting items by lowest to highest value
            itemList.sort((item1, item2) -> Double.compare(item1.getEstimatedValue(), item2.getEstimatedValue()));
        }
        else if (selectedSortOption.equals(context.getString(R.string.sort_make_az))) { //if we are sorting items by make alphabetically
            itemList.sort((item1, item2) -> item1.getMake().compareTo(item2.getMake()));
        }
        else if (selectedSortOption.equals(context.getString(R.string.sort_make_za))) { //if we are sorting items by make reverse alphabetically
            itemList.sort((item1, item2) -> item2.getMake().compareTo(item1.getMake()));
        }
        else if (selectedSortOption.equals(context.getString(R.string.sort_description_az))) { //if we are sorting items by description reverse alphabetically
            itemList.sort((item1, item2) -> item1.getDescription().compareTo(item2.getDescription()));
        }
        else if (selectedSortOption.equals(context.getString(R.string.sort_description_za))) { //if we are sorting items by description reverse alphabetically
            itemList.sort((item1, item2) -> item2.getDescription().compareTo(item1.getDescription()));
        } else if (selectedSortOption.equals(context.getString(R.string.sort_tags_az))) {
            itemList.sort((item1, item2) -> item1.getTags().get(0).compareTo(item2.getTags().get(0)));
        } else if (selectedSortOption.equals(context.getString(R.string.sort_tags_za))) {
            itemList.sort((item1, item2) -> item2.getTags().get(0).compareTo(item1.getTags().get(0)));
        }
        itemAdapter = new ItemsCustomAdapter(MainActivity.this, itemList);
        itemListView.setAdapter(itemAdapter);
    }

    /**
     * Gets all of the items in the items collection
     * and updates the frontend to display them in the list
     * @param itemsRef
     */
    private void getAllItemsFromDatabase(CollectionReference itemsRef) {
        itemsRef.get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    itemList.clear(); //clear whatever data we currently have stored in our item list
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots){ //get everything that is stored in our db at the moment
                        Item item = doc.toObject(Item.class); //convert the contents of each document in the items collection to an item object
                        item.setItemId(doc.getId()); //set the item ID
                        Log.i("Firestore", String.format("Item %s fetched", item.getName())); //log the name of the item we successfully got from the db
                        if (doc.getString("username").equals(app.getUsername())) {
                            itemList.add(item); //add the item object to our item list if it belongs to the current user
                        }
                    }
                    items_count_field.setText(String.valueOf(itemList.size()) + " items.");
                    noItemsFound.setVisibility(View.GONE);
                    String currentSortOption = sortSpinner.getSelectedItem().toString(); //get the currently selected sort option
                    sortDataList(currentSortOption, getApplicationContext()); //sort the list accordingly and notify changes were made to update frontend
                    calculateTotalEstimatedValue(); //recalculate and display the total estimated value
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Firestore", "Error fetching data", e);
                }
            });
    }

    private void getAllTagsFromDatabase(CollectionReference tagsRef) {
        tagsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                tagsList.clear();
                tagsList.add("All");
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    ArrayList<String> usernames = (ArrayList<String>) documentSnapshot.get("usernames");
                    if (usernames.contains(app.getUsername())) {
                        String name = documentSnapshot.getString("name");
                        tagsList.add(name);
                    }
                }
                tagFilterAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Given a start date and end date, filters the current item list
     * accordingly (ie. keeps items between start and end INCLUSIVE).
     * @param startDate
     * @param endDate
     */
    public void filterByDate(Timestamp startDate, Timestamp endDate) {
        ArrayList<Item> filteredOutList = new ArrayList<Item>(); //a new list to store the items that are being filtered out
        ArrayList<Item> currentList = itemAdapter.getCurrentItemList();
        for (int i = 0; i < currentList.size(); i++) { //for every item in the current list
            Item item = currentList.get(i); //get the item
            Timestamp purchaseDate = item.getPurchaseDate(); //get the purchase date of the item
            if (purchaseDate.toDate().before(startDate.toDate()) || purchaseDate.toDate().after(endDate.toDate())) { //if the purchase date does not fall within the given date range
                filteredOutList.add(item); //add it to the filtered list
            }
        }
        ArrayList<Item> updatedList = new ArrayList<>(currentList);
        updatedList.removeAll(filteredOutList);
        itemAdapter = new ItemsCustomAdapter(MainActivity.this, updatedList);
        itemListView.setAdapter(itemAdapter);
        items_count_field.setText(String.valueOf(itemAdapter.getCurrentItemList().size()) + " items.");
        calculateTotalEstimatedValue(); //recalculate and display the total estimated value
    }

    /**
     * Given a make, filters the current item list
     * accordingly (ie. keeps items with the specified make).
     * @param make
     */
    public void filterByMake(String make) {
        make = make.toLowerCase();
        ArrayList<Item> filteredOutList = new ArrayList<Item>(); //a new list to store the items that are being filtered out
        ArrayList<Item> currentList = itemAdapter.getCurrentItemList();
        for (int i = 0; i < currentList.size(); i++) { //for every item in the current list
            Item item = currentList.get(i); //get the item
            String itemMake = item.getMake(); //get the make of the item
            if (!itemMake.toLowerCase().contains(make)) { //if it DOES NOT match the given make (case insensitive)
                filteredOutList.add(item); //add it to the filtered list
            }
        }
        ArrayList<Item> updatedList = new ArrayList<>(currentList);
        updatedList.removeAll(filteredOutList);
        itemAdapter = new ItemsCustomAdapter(MainActivity.this, updatedList);
        itemListView.setAdapter(itemAdapter);
        items_count_field.setText(String.valueOf(itemAdapter.getCurrentItemList().size()) + " items.");
        calculateTotalEstimatedValue(); //recalculate and display the total estimated value
    }

    /**
     * Given a description keyword, filters the current item list
     * accordingly (ie. keeps items with the specified keyword).
     * @param keyword
     */
    public void filterByKeyword(String keyword) {
        keyword = keyword.toLowerCase(); //change the keyword to lowercase so we can be case insensitive
        ArrayList<Item> filteredOutList = new ArrayList<Item>(); //a new list to store the items that are being filtered out
        ArrayList<Item> currentList = itemAdapter.getCurrentItemList();
        for (int i = 0; i < currentList.size(); i++) { //for every item in the current list
            Item item = currentList.get(i); //get the item
            String description = item.getDescription().toLowerCase(); //get the description of the item (also lowercase)
            if (!description.contains(keyword)) { //if it DOES NOT contain the given keyword
                filteredOutList.add(item); //add it to the filtered list
            }
        }
        ArrayList<Item> updatedList = new ArrayList<>(currentList);
        updatedList.removeAll(filteredOutList);
        itemAdapter = new ItemsCustomAdapter(MainActivity.this, updatedList);
        itemListView.setAdapter(itemAdapter);
        items_count_field.setText(String.valueOf(itemAdapter.getCurrentItemList().size()) + " items.");
        calculateTotalEstimatedValue(); //recalculate and display the total estimated value
    }

    /**
     * Given a tag, filters the current item list accordingly
     * (ie. keeps items associated with the specified tag).
     */
    public void filterByTag() {
        if (!selectedTagForFiltering.equals("All")) {
            ArrayList<Item> filteredOutList = new ArrayList<Item>();
            ArrayList<Item> currentList = itemAdapter.getCurrentItemList();
            for (int i = 0; i < currentList.size(); i++) {
                Item item = currentList.get(i);
                ArrayList<String> tagsOfItem = item.getTags();
                if (!tagsOfItem.contains(selectedTagForFiltering)) {
                    filteredOutList.add(item);
                }
            }
            ArrayList<Item> updatedList = new ArrayList<>(currentList);
            updatedList.removeAll(filteredOutList);
            itemAdapter = new ItemsCustomAdapter(MainActivity.this, updatedList);
            itemListView.setAdapter(itemAdapter);
            items_count_field.setText(String.valueOf(itemAdapter.getCurrentItemList().size()) + " items.");
            calculateTotalEstimatedValue();
        }
    }

    /**
     * Calculates the total estimated value of all the items currently in the list
     * and updates the frontend to display the correct total
     */
    private void calculateTotalEstimatedValue() {
        double total = 0.00; //initialize the total amount of the estimated value to zero
        for (int i = 0; i < itemList.size(); i++) { //for every item on our list
            Item item = (Item) itemList.get(i); //get the item
            total += item.getEstimatedValue(); //add the estimated value of the current item to the total
        }
        totalEstimatedValue = findViewById(R.id.total_estimated_value_footer); //find our total estimated value textview from our frontend layout
        String totalText = String.format("%.2f", total); //format the total we calculated as a string
        totalEstimatedValue.setText(this.getString(R.string.total) + totalText); //and updated our frontend to display the updated amount
    }

    /**
     * Handles the result of the permission request for accessing the camera.
     *
     * @param requestCode The request code passed to requestPermissions.
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please allow access to your camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}