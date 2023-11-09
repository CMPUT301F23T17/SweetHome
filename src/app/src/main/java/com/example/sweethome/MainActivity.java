package com.example.sweethome;
/*
 * MainActivity
 *
 * This class controls the main activity of our SweetHome app.
 *
 * October 28, 2023
 *
 * Sources:
 *
 */

/* necessary imports */

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements Filterable{
    /* attributes of this class */
    private ArrayList<Item> itemList;
    private ListView itemListView;
    private TextView totalEstimatedValue;
    private ItemsCustomAdapter itemAdapter;
    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    private Spinner sortSpinner;
    private Button addItemButton;
    private ArrayAdapter<String> sortAdapter;
    /* constants */
    private final long ONE_DAY = 86400000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* set up a connection to our db and a reference to the items collection */
        db = FirebaseFirestore.getInstance();
        itemsRef = db.collection("items");

        /* set up our list of items, find the list on our frontend layout, and set the corresponding array adapter */
        itemList = new ArrayList<Item>();
        itemListView = findViewById(R.id.item_list);
        itemAdapter = new ItemsCustomAdapter(this, itemList);
        itemListView.setAdapter(itemAdapter);

        /* setup the sort spinner */
        sortSpinner = findViewById(R.id.spinner_sort_options);
        sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sort_options));
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        /* spinner selection listener */
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSortOption = parentView.getItemAtPosition(position).toString(); // get the selected sort option
                sortDataList(selectedSortOption); //handle sorting based on selection
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //do nothing here
            }
        });

        /* find our add item button on the frontend and set an onclicklistener for it */
        addItemButton = findViewById(R.id.add_button);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast add = Toast.makeText(MainActivity.this, R.string.no_adding_msg, Toast.LENGTH_LONG); //make a little temporary message on the bottom to tell users we aren't adding right now
                add.show(); //show the message
            }
        });

        /* listen for changes in the collection and update our list of items accordingly */
        itemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore",error.toString()); //if there was any error, log it
                }
                if (value != null) {
                    getAllItemsFromDatabase(); //otherwise get all items currently in the items collection and display them in our list
                }
            }
        });
    }



    /**
     * Adds a new item to the items collection
     * @param item
     */
    private void addItem(Item item){
        itemsRef.add(item) //add the item to our items collection
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("Firestore", "db write succeeded"); //log if we were successful in adding the new item
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "db write fails"); //log if we were unsuccessful in adding the new item
                    }
                });
    }

    /*
     * Gets all of the items in the items collection from the db
     * and updates the frontend to display them in the list
     */
    private void getAllItemsFromDatabase(){
        itemsRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        itemList.clear(); //clear whatever data we currently have stored in our item list
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots){ //get everything that is stored in our db at the moment
                            Item item = doc.toObject(Item.class); //convert the contents of each document in the items collection to an item object
                            Log.i("Firestore", String.format("Item %s fetched", item.getName())); //log the name of the item we successfully got from the db
                            itemList.add(item); //add the item object to our item list
                        }
                        itemAdapter.notifyDataSetChanged(); //notify changes were made to update frontend
                        calculateTotalEstimatedValue(); //recalculate and display the total estimated value
                    }
                });
    }

    /*
     * Given a selected sorting option, sorts the current item
     * list according to the selected criteria.
     */
    private void sortDataList(String selectedSortOption) {
        if (selectedSortOption.equals(this.getString(R.string.sort_least_recent))) { //if we are sorting items by oldest to newest acquired
            itemList.sort((item1, item2) -> item1.getPurchaseDate().compareTo(item2.getPurchaseDate()));
        }
        else if (selectedSortOption.equals(this.getString(R.string.sort_most_recent))) { //if we are sorting items by newest to oldest acquired
            itemList.sort((item1, item2) -> item2.getPurchaseDate().compareTo(item1.getPurchaseDate()));
        }
        else if (selectedSortOption.equals(this.getString(R.string.sort_highest_value))) { //if we are sorting items by highest to lowest value
            itemList.sort((item1, item2) -> Double.compare(item2.getEstimatedValue(), item1.getEstimatedValue()));
        }
        else if (selectedSortOption.equals(this.getString(R.string.sort_lowest_value))) { //if we are sorting items by lowest to highest value
            itemList.sort((item1, item2) -> Double.compare(item1.getEstimatedValue(), item2.getEstimatedValue()));
        }
        else if (selectedSortOption.equals(this.getString(R.string.sort_make_az))) { //if we are sorting items by make alphabetically
            itemList.sort((item1, item2) -> item1.getMake().compareTo(item2.getMake()));
        }
        else if (selectedSortOption.equals(this.getString(R.string.sort_make_za))) { //if we are sorting items by make reverse alphabetically
            itemList.sort((item1, item2) -> item2.getMake().compareTo(item1.getMake()));
        }
        else if (selectedSortOption.equals(this.getString(R.string.sort_description_az))) { //if we are sorting items by description reverse alphabetically
            itemList.sort((item1, item2) -> item1.getDescription().compareTo(item2.getDescription()));
        }
        else if (selectedSortOption.equals(this.getString(R.string.sort_description_za))) { //if we are sorting items by description reverse alphabetically
            itemList.sort((item1, item2) -> item2.getDescription().compareTo(item1.getDescription()));
        }
        else { //if they want to sort by tags let them know this function is not yet available
            Toast.makeText(MainActivity.this, R.string.no_tag_sort_msg, Toast.LENGTH_SHORT).show();
        }
        itemAdapter.notifyDataSetChanged(); //notify changes were made to update frontend
    }

    /*
     * Given a start date and end date, filters the current item list
     * accordingly (ie. keeps items between start and end INCLUSIVE).
     */
    public void filterByDate(Date startDate, Date endDate) {
        Date inclusiveStart = new Date(startDate.getTime() - ONE_DAY); //remove a day from the start date so we filter inclusively
        ArrayList<Item> filteredList = new ArrayList<Item>(); //a new list to store the items that are being filtered out
        for (int i = 0; i < itemList.size(); i++) { //for every item in the current list
            Item item = itemList.get(i); //get the item
            Date purchaseDate = item.getPurchaseDate(); //get the purchase date of the item
            if (purchaseDate.before(inclusiveStart) || item.getPurchaseDate().after(endDate)) { //if the purchase date does not fall within the given date range
                filteredList.add(item); //add it to the filtered list
            }
        }
        itemList.removeAll(filteredList); //remove all items that are to be filtered out from our current list ie. were not purchased in the provided time frame
        itemAdapter.notifyDataSetChanged(); //notify changes were made to update frontend
        calculateTotalEstimatedValue(); //recalculate and display the total estimated value
    }

    /*
     * Given a make, filters the current item list
     * accordingly (ie. keeps items with the specified make).
     */
    public void filterByMake(String make) {
        ArrayList<Item> filteredList = new ArrayList<Item>(); //a new list to store the items that are being filtered out
        for (int i = 0; i < itemList.size(); i++) { //for every item in the current list
            Item item = itemList.get(i); //get the item
            String itemMake = item.getMake(); //get the make of the item
            if (!itemMake.equalsIgnoreCase(make)) { //if it DOES NOT match the given make (case insensitive)
                filteredList.add(item); //add it to the filtered list
            }
        }
        itemList.removeAll(filteredList); //remove all items that are to be filtered out from our current list ie. don't match the given make
        itemAdapter.notifyDataSetChanged(); //notify changes were made to update frontend
        calculateTotalEstimatedValue(); //recalculate and display the total estimated value
    }

    /*
     * Given a description keyword, filters the current item list
     * accordingly (ie. keeps items with the specified keyword).
     */
    public void filterbyKeyword(String keyword) {
        keyword = keyword.toLowerCase(); //change the keyword to lowercase so we can be case insensitive
        ArrayList<Item> filteredList = new ArrayList<Item>(); //a new list to store the items that are being filtered out
        for (int i = 0; i < itemList.size(); i++) { //for every item in the current list
            Item item = itemList.get(i); //get the item
            String description = item.getDescription().toLowerCase(); //get the description of the item (also lowercase)
            if (!description.contains(keyword)) { //if it DOES NOT contain the given keyword
                filteredList.add(item); //add it to the filtered list
            }
        }
        itemList.removeAll(filteredList); //remove all items from the current list that are to be filtered out ie. don't contain the given keyword in their description
        itemAdapter.notifyDataSetChanged(); //notify changes were made to update frontend
        calculateTotalEstimatedValue(); //recalculate and display the total estimated value
    }

    /*
     * Given a tag, filters the current item list accordingly
     * (ie. keeps items associated with the specified tag).
     */
    public void filterByTag(String tag) { //currently unavailable so let them know
        Toast.makeText(MainActivity.this, R.string.no_tag_filter_msg, Toast.LENGTH_SHORT).show();
    }

    /*
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
}