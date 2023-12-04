package com.example.sweethome;

/* necessary imports */
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.SliderView;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * @class ManageItemActivity
 *
 * <p>The ManageItemActivity class represents the activity for managing items, including adding new
 * item or View/Edit existing ones. The activity includes functionalities for adding tags,
 * capturing and displaying images, and managing item details such as name, description, make,
 * model, serial number, estimated value, purchased date, and comments.</p>
 *
 * @date <p>November 10, 2023</p>
 */
public class ManageItemActivity extends AppCompatActivity implements BarcodeLookupApi.BarcodeLookupListener, NetworkChangeReceiver.NetworkChangeListener {
    /* attributes and variables of this class */
    private StorageReference photosStorageRef = FirebaseStorage.getInstance().getReference();
    private StorageReference photosRef = photosStorageRef.child("images");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference itemsCollection = db.collection("items");
    private CollectionReference tagsCollection = db.collection("tags");
    private CustomAddTagsField add_tags_field;
    private LinearLayout tags_container;
    private TextView date_field;
    private EditText value_field;
    private ImageView nav_back_button;
    private String itemId;
    private String name;
    private String description;
    private String make;
    private String model;
    private String serialNumber;
    private Double estimatedValue;
    private String purchaseDate;
    private Timestamp purchaseDateTS;
    private String comment;
    private ArrayList<Uri> photoUris = new ArrayList<>();
    private ArrayList<String> photoUrls = new ArrayList<>();
    private ArrayList<String> removedPhotoUrls = new ArrayList<>();
    private ArrayList<String> tags;
    private ArrayList<Tag> tagsList = new ArrayList<>();
    private CardView open_gallery_button;
    private CardView open_camera_button;
    private Uri imageUri;
    private ImageSliderAdapter adapter;
    private ArrayList<ImageSliderData> sliderDataArrayList;
    private SliderView sliderView;
    private LinearLayout sliderViewFrame;
    private TextView screen_name;
    private ImageView save_button;
    private EditText item_name_field;
    private EditText serial_number_field;
    private ImageView open_sn_scanner_button;
    private ImageView barcode_scan_icon;
    private EditText description_field;
    private EditText make_field;
    private EditText model_field;
    private EditText comment_field;
    private LinearLayout noImagePlaceholder;
    private ImageView remove_image_button;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private ArrayList<com.example.sweethome.Item> itemsList = new ArrayList<>();
    private String edit_screen_name = "View / Edit";
    private String add_screen_name = "Add Item";
    private int successfulUploads = 0;
    private boolean addedMorePhotos = false;
    private int numOfAddedPhotos = 0;
    private int numOfExistingPhotos = 0;
    private int maxNumOfPhotos = 5;
    private Map<String, Object> itemInfo;
    private Map<String, Object> tagInfo;
    private boolean saving = false;
    private static final int OPEN_GALLERY_REQUEST_CODE = 1;
    private static final int TAKE_PHOTO_REQUEST_CODE = 2;
    private static final int SCAN_BARCODE_REQUEST_CODE = 3;
    private String scannedBarcode;
    private static final int SCAN_SN_REQUEST_CODE = 4;
    public AppContext app;
    private NetworkChangeReceiver networkChangeReceiver;

    /**
     * Called when the activity is first created. Initializes UI components, sets up listeners,
     * and retrieves item information if editing an existing item.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_item);

        app = (AppContext) getApplication();

        // define attributes and variables.
        add_tags_field = findViewById(R.id.tag_input);
        tags_container = findViewById(R.id.tags_container);
        screen_name = findViewById(R.id.screen_name);
        nav_back_button = findViewById(R.id.nav_back_button);
        value_field = findViewById(R.id.value_field);
        date_field = findViewById(R.id.date_field);
        sliderViewFrame = findViewById(R.id.image_slider_frame);
        sliderView = findViewById(R.id.image_slider);
        noImagePlaceholder = findViewById(R.id.no_image_placeholder);
        open_gallery_button = findViewById(R.id.open_gallery_button);
        open_camera_button = findViewById(R.id.open_camera_button);
        save_button = findViewById(R.id.check_icon);
        item_name_field = findViewById(R.id.item_name_field);
        serial_number_field = findViewById(R.id.serial_number_field);
        open_sn_scanner_button = findViewById(R.id.open_sn_scanner_button);
        barcode_scan_icon = findViewById(R.id.barcode_scan_icon);
        description_field = findViewById(R.id.description_field);
        make_field = findViewById(R.id.make_field);
        model_field = findViewById(R.id.model_field);
        comment_field = findViewById(R.id.comment_field);
        remove_image_button = findViewById(R.id.remove_image_button);
        sliderDataArrayList = new ArrayList<>();
        adapter = new ImageSliderAdapter(this, sliderDataArrayList);
        Intent intent = getIntent();

        // get item's information sent from home screen.
        if (intent != null) {
            String screen = intent.getStringExtra("screen");
            screen_name.setText(screen);
            if (edit_screen_name.equals(screen)) {
                itemId = intent.getStringExtra("id");
                name = intent.getStringExtra("name");
                item_name_field.setText(name);
                description = intent.getStringExtra("description");
                description_field.setText(description);
                make = intent.getStringExtra("make");
                make_field.setText(make);
                model = intent.getStringExtra("model");
                model_field.setText(model);
                serialNumber = intent.getStringExtra("serialNumber");
                serial_number_field.setText(serialNumber);
                estimatedValue = intent.getDoubleExtra("estimatedValue", 0.0);
                value_field.setText(String.valueOf(estimatedValue));
                purchaseDate = dateFormat.format((Date) intent.getSerializableExtra("purchaseDate"));
                date_field.setText(purchaseDate);
                comment = intent.getStringExtra("comment");
                comment_field.setText(comment);
                photoUrls = (ArrayList<String>) intent.getSerializableExtra("photos");
                tags = (ArrayList<String>) intent.getSerializableExtra("tags");
                for (String tagName : tags) {
                    add_tags_field.addTag(tagName);
                }
            }
        }

        // populate the image slider if the item is associated with at least 1 photo.
        if (photoUrls.size() > 0) {
            for (int i = 0; i < photoUrls.size(); i++) {
                photoUris.add(Uri.parse(photoUrls.get(i)));
                numOfExistingPhotos += 1;
                sliderDataArrayList.add(new ImageSliderData(Uri.parse(photoUrls.get(i))));
            }
            adapter.notifyDataSetChanged();
            sliderView.setSliderAdapter(adapter);
            noImagePlaceholder.setVisibility(View.GONE);
            sliderViewFrame.setVisibility(View.VISIBLE);
        } else {
            sliderViewFrame.setVisibility(View.GONE);
            noImagePlaceholder.setVisibility(View.VISIBLE);
        }

        itemsCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                itemsList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String id = documentSnapshot.getId();
                    String name = documentSnapshot.getString("name");
                    String description = documentSnapshot.getString("description");
                    String make = documentSnapshot.getString("make");
                    String model = documentSnapshot.getString("model");
                    String serialNumber = documentSnapshot.getString("serialNumber");
                    Double estimatedValue = documentSnapshot.getDouble("estimatedValue");
                    Timestamp purchaseDate = documentSnapshot.getTimestamp("purchaseDate");
                    String comment = documentSnapshot.getString("comment");
                    ArrayList<String> tags = (ArrayList<String>) documentSnapshot.get("tags");
                    ArrayList<String> associatedPhotos = (ArrayList<String>) documentSnapshot.get("photos");
                    if (associatedPhotos == null) {
                        associatedPhotos = new ArrayList<>();
                    }
                    String username = documentSnapshot.getString("username");
                    Item newItem = new Item(id, name, description, make, model, serialNumber, estimatedValue, purchaseDate, comment, associatedPhotos, tags, username);
                    itemsList.add(newItem);
                }
            }
        });

        tagsCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                tagsList.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String id = documentSnapshot.getId();
                    String name = documentSnapshot.getString("name");
                    Timestamp timestamp = documentSnapshot.getTimestamp("timestamp");
                    ArrayList<String> usernames = (ArrayList<String>) documentSnapshot.get("usernames");
                    Tag newTag = new Tag(id, name, timestamp, usernames);
                    tagsList.add(newTag);
                }
            }
        });

        // listen to the user choosing the date in the date-picker and display the date in the format YYYY-MM-DD.
        date_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ManageItemActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                date_field.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                date_field.setTextColor(ContextCompat.getColor(ManageItemActivity.this, R.color.black));
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        // the 2 listeners below listen to the user's keyboard. Add a dollar sign at the front after they click the "Enter", "Done", "Next" or any unspecified key.
        value_field.setOnKeyListener((v, keyCode, event) -> {
            if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
                String input = value_field.getText().toString().replace("$", "");
                double inputNumber = Double.parseDouble(input);
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                String formattedNumber = decimalFormat.format(inputNumber);
                value_field.setText("$" + formattedNumber);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(value_field.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });
        value_field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    String input = value_field.getText().toString().replace("$", "");
                    double inputNumber = Double.parseDouble(input);
                    DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                    String formattedNumber = decimalFormat.format(inputNumber);
                    value_field.setText("$" + formattedNumber);
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(value_field.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });

        // navigate the user back to home screen when they click on the left arrow.
        nav_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // opens device's photo gallery when the user clicks on the "Gallery" button
        open_gallery_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });

        // save item's information and send them to the database
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!saving) {
                    saving = true;
                    save_button.setColorFilter(ContextCompat.getColor(ManageItemActivity.this, R.color.light_grey), PorterDuff.Mode.SRC_IN);
                    boolean isValid = isInputValid();
                    if (isValid) {
                        name = item_name_field.getText().toString();
                        name = name.substring(0, 1).toUpperCase() + name.substring(1);
                        description = description_field.getText().toString();
                        make = make_field.getText().toString();
                        model = model_field.getText().toString();
                        serialNumber = serial_number_field.getText().toString();
                        estimatedValue = Double.parseDouble(value_field.getText().toString().replace("$", ""));
                        purchaseDate = date_field.getText().toString();
                        comment = comment_field.getText().toString();
                        tags = add_tags_field.getAddedTagNames();
                        try {
                            Date parsedPurchaseDate = dateFormat.parse(purchaseDate);
                            purchaseDateTS = new Timestamp(parsedPurchaseDate);
                            itemInfo = new HashMap<>();
                            itemInfo.put("name", name);
                            itemInfo.put("description", description);
                            itemInfo.put("make", make);
                            itemInfo.put("model", model);
                            itemInfo.put("serialNumber", serialNumber);
                            itemInfo.put("estimatedValue", estimatedValue);
                            itemInfo.put("purchaseDate", purchaseDateTS);
                            itemInfo.put("comment", comment);
                            tags.sort((tag1, tag2) -> tag1.compareTo(tag2)); // sort in alphabetical order
                            itemInfo.put("tags", tags);
                            List<Uri> addedPhotoUris = photoUris.subList(numOfExistingPhotos, numOfExistingPhotos + numOfAddedPhotos);
                            if (addedMorePhotos) {
                                for (Uri photoUri : addedPhotoUris) {
                                    String timestamp = String.valueOf(System.currentTimeMillis());
                                    String fileName = "photo_" + timestamp + ".jpg";
                                    StorageReference photoRef = photosRef.child(fileName);
                                    photoRef.putFile(photoUri).addOnSuccessListener(taskSnapshot -> {
                                        photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            String downloadUrl = uri.toString();
                                            photoUrls.add(downloadUrl);
                                            successfulUploads++;
                                            if (successfulUploads == numOfAddedPhotos) {
                                                Log.d("FirebaseUpload", "Photo uploaded successfully: " + fileName);
                                                manageItem();
                                            }
                                        });
                                    }).addOnFailureListener(e -> {
                                        Log.e("FirebaseUpload", "Photo upload failed for: " + fileName, e);
                                    });
                                }
                            } else {
                                manageItem();
                            }
                        } catch (ParseException err) {
                            System.out.println("ERROR: invalid date format used.");
                        }
                    } else {
                        saving = false;
                        save_button.setColorFilter(ContextCompat.getColor(ManageItemActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
                    }
                } else {
                    Toast.makeText(ManageItemActivity.this, "This item is being saved.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // open device's camera to take a photo of the item when the user clicks on the open camera button
        open_camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoWithCamera();
            }
        });

        // open barcode scanner when the barcode icon in the top right corner is clicked
        barcode_scan_icon.setOnClickListener(v -> {
            Intent scannedBarcodeIntent = new Intent(this, ScanningBarcodeActivity.class);
            startActivityForResult(scannedBarcodeIntent, SCAN_BARCODE_REQUEST_CODE);
        });

        // open text recognizer to scan for serial number
        open_sn_scanner_button.setOnClickListener(v -> {
            Intent scanSerialNumberIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(scanSerialNumberIntent, SCAN_SN_REQUEST_CODE);
        });

        // remove an image in the slider
        remove_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = sliderView.getCurrentPagePosition();
                if (currentPosition >= 0 && currentPosition < photoUris.size()) {
                    photoUris.remove(currentPosition);
                    sliderDataArrayList.remove(currentPosition);
                    adapter.notifyDataSetChanged();
                    if (currentPosition <= (numOfExistingPhotos - 1)) {
                        numOfExistingPhotos -= 1;
                        removedPhotoUrls.add(photoUrls.get(currentPosition));
                        photoUrls.remove(currentPosition);
                    } else {
                        numOfAddedPhotos -= 1;
                        if (numOfAddedPhotos <= 0) {
                            addedMorePhotos = false;
                        }
                    }
                    if (sliderDataArrayList.size() <= 0) {
                        sliderViewFrame.setVisibility(View.GONE);
                        noImagePlaceholder.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeReceiver.setListener(this);
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
            Toast.makeText(ManageItemActivity.this, "Lost internet connection.", Toast.LENGTH_SHORT).show();
            // Redirect to the login screen
            Intent loginIntent = new Intent(ManageItemActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    @Override
    protected void onStop() {
        getApplicationContext().getCacheDir().delete();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        getApplicationContext().getCacheDir().delete();
        super.onDestroy();
    }

    /**
     * Opens the device's gallery to allow the user to select one or multiple images.
     */
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, OPEN_GALLERY_REQUEST_CODE);
    }

    /**
     * Opens the device's camera to allow the user to take a photo.
     */
    private void takePhotoWithCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE);
    }

    /**
     * Converts a photo's bitmap to an image URI to store in the sliderDataArrayList.
     *
     * @param bitmap The Bitmap representation of the photo.
     * @return The URI of the image.
     */
    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Call updateUIAfterBarcodeLookup after calling barcode lookup api.
     */
    @Override
    public void onBarcodeLookupComplete(ReturnedItemData result) {
        updateUIAfterBarcodeLookup(result);
    }

    /**
     * Handles the result after selecting photos from the gallery, taking a photo with the camera, scanning serial number and barcode.
     *
     * @param requestCode The request code passed to startActivityForResult.
     * @param resultCode The result code returned by the child activity.
     * @param data The data returned by the child activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if ((numOfAddedPhotos + numOfExistingPhotos) >= maxNumOfPhotos) {
                Toast.makeText(ManageItemActivity.this, "Cannot upload more photos.", Toast.LENGTH_SHORT).show();
            } else {
                addedMorePhotos = true;
                if (data.getData() != null) {
                    imageUri = data.getData();
                    numOfAddedPhotos += 1;
                    photoUris.add(imageUri);
                    sliderDataArrayList.add(new ImageSliderData(imageUri));
                } else if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    numOfAddedPhotos += count;
                    for (int i = 0; i < count; i++) {
                        imageUri = data.getClipData().getItemAt(i).getUri();
                        photoUris.add(imageUri);
                        sliderDataArrayList.add(new ImageSliderData(imageUri));
                    }
                }
                adapter.notifyDataSetChanged();
                sliderView.setSliderAdapter(adapter);
                noImagePlaceholder.setVisibility(View.GONE);
                sliderViewFrame.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == TAKE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if ((numOfAddedPhotos + numOfExistingPhotos) >= maxNumOfPhotos) {
                Toast.makeText(ManageItemActivity.this, "Cannot upload more photos.", Toast.LENGTH_SHORT).show();
            } else {
                Bundle extras = data.getExtras();
                addedMorePhotos = true;
                if (extras != null) {
                    Bitmap photo = (Bitmap) extras.get("data");
                    imageUri = getImageUriFromBitmap(photo);
                    if (imageUri != null) {
                        photoUris.add(imageUri);
                        numOfAddedPhotos += 1;
                        sliderDataArrayList.add(new ImageSliderData(imageUri));
                        adapter.notifyDataSetChanged();
                    }
                    sliderView.setSliderAdapter(adapter);
                    noImagePlaceholder.setVisibility(View.GONE);
                    sliderViewFrame.setVisibility(View.VISIBLE);
                }
            }
        } else if (requestCode == SCAN_BARCODE_REQUEST_CODE && data != null) {
            if (resultCode == RESULT_OK) {
                scannedBarcode = data.getStringExtra("SCANNED_BARCODE");
                BarcodeLookupApi barcodeLookupApi = new BarcodeLookupApi();
                barcodeLookupApi.setBarcodeLookupListener(this);
                barcodeLookupApi.execute(scannedBarcode);
            }
        } else if (requestCode == SCAN_SN_REQUEST_CODE && data != null) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getCloudTextRecognizer();
            Task<FirebaseVisionText> result = detector.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        serialNumber = firebaseVisionText.getText();
                        serial_number_field.setText(serialNumber);
                    }
                })
                .addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManageItemActivity.this, "Failed to scan serial number.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * save a new or updated item into firestore database.
     * save any newly added photos into firebase storage.
     * get downloadable urls for the photos and store them in an array field with the item in the database.
     * navigate back to MainActivity when it's done.
     * create new tag documents if not exist.
     */
    private void manageItem() {
        itemInfo.put("photos", photoUrls);
        for (String tag : tags) {
            Tag existingTag = null;
            for (Tag tagObject : tagsList) {
                if (tagObject.getTagName().equals(tag)) {
                    existingTag = tagObject;
                    break;
                }
            }
            String username = app.getUsername();
            tagInfo = new HashMap<>();
            tagInfo.put("name", tag);
            tagInfo.put("timestamp", Timestamp.now());
            if (existingTag == null) {
                ArrayList<String> usernames = new ArrayList<>();
                usernames.add(username);
                tagInfo.put("usernames", usernames);
                DocumentReference newTag = tagsCollection.document();
                newTag.set(tagInfo);
            } else {
                ArrayList<String> existingUsernames = existingTag.getTagUsernames();
                if (!existingUsernames.contains(username)) {
                    DocumentReference existingTagDoc = tagsCollection.document(existingTag.getTagId());
                    existingUsernames.add(username);
                    tagInfo.put("usernames", existingUsernames);
                    existingTagDoc.update(tagInfo);
                }
            }
        }
        if (add_screen_name.equals(screen_name.getText().toString())) {
            itemInfo.put("username", app.getUsername());
            DocumentReference newItem = itemsCollection.document();
            newItem.set(itemInfo);
            itemsList.add(new Item(newItem.getId(), name, description, make, model, serialNumber, estimatedValue, purchaseDateTS, comment, photoUrls, tags, app.getUsername()));
            Toast.makeText(ManageItemActivity.this, "Successfully added new item.", Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<String> removedTagNames = add_tags_field.getRemovedTagNames();
            removedTagNames.removeAll(tags);
            for (String removedTagName : removedTagNames) {
                int count = 0;
                for (Item itemObject : itemsList) {
                    if (itemObject.getTags().contains(removedTagName) && itemObject.getUsername().equals(app.getUsername())) {
                        count += 1;
                    }
                    if (count >= 2) {
                        break;
                    }
                }
                if (count < 2) {
                    tagsCollection
                        .whereEqualTo("name", removedTagName)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    List<String> usernames = (List<String>) document.get("usernames");
                                    if (usernames.size() <= 1) {
                                        tagsCollection.document(document.getId()).delete();
                                    } else {
                                        usernames.remove(app.getUsername());
                                        tagsCollection.document(document.getId()).update("usernames", usernames);
                                    }
                                }
                            }
                        });
                }
            }
            DocumentReference curItem = itemsCollection.document(itemId);
            curItem.update(itemInfo);
            for (Item item : itemsList) {
                if (item.getItemId().equals(itemId)) {
                    item.setName(name);
                    item.setDescription(description);
                    item.setMake(make);
                    item.setModel(model);
                    item.setSerialNumber(serialNumber);
                    item.setEstimatedValue(estimatedValue);
                    item.setPurchaseDate(purchaseDateTS);
                    item.setComment(comment);
                    item.setPhotos(photoUrls);
                    item.setTags(tags);
                    break;
                }
            }
            if (removedPhotoUrls.size() > 0) {
                for (String photoUrl : removedPhotoUrls) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl);
                    storageReference.delete().addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Photo successfully deleted!");
                    })
                    .addOnFailureListener(exception -> {
                        Log.e("Firestore", "Photo deleted failed!");
                    });
                }
            }
            Toast.makeText(ManageItemActivity.this, "Successfully updated item.", Toast.LENGTH_SHORT).show();
        }
        successfulUploads = 0;
        saving = false;
        save_button.setColorFilter(ContextCompat.getColor(ManageItemActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
        Intent intent = new Intent(ManageItemActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Fill out the item name, description and make fields after a barcode lookup.
     */
    private void updateUIAfterBarcodeLookup(ReturnedItemData result) {
        if (result != null) {
            name = result.getName();
            item_name_field.setText(name);
            description = result.getDescription();
            description_field.setText(description);
            make = result.getMake();
            make_field.setText(make);
        } else {
            Toast.makeText(ManageItemActivity.this, "Barcode " + scannedBarcode + " doesn't exist.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks whether the user input for item details is valid.
     *
     * @return True if the input is valid; false otherwise.
     */
    private boolean isInputValid() {
        boolean isValid = true;

        if (item_name_field.getText().toString().isEmpty()) {
            item_name_field.setError("Item name is required");
            isValid = false;
        }

        if (!serial_number_field.getText().toString().isEmpty()) {
            for (com.example.sweethome.Item item : itemsList) {
                String serialNumber = item.getSerialNumber();
                if (!item.getItemId().equals(itemId) && serialNumber.equals(serial_number_field.getText().toString())) {
                    isValid = false;
                    serial_number_field.setError("Serial number already exists.");
                    break;
                }
            }
        }

        if (tags_container.getChildCount() <= 0) {
            add_tags_field.setError("Tag is required");
            isValid = false;
        }

        if (description_field.getText().toString().isEmpty()) {
            description_field.setError("Description is required");
            isValid = false;
        }

        if (make_field.getText().toString().isEmpty()) {
            make_field.setError("Make is required");
            isValid = false;
        }

        if (model_field.getText().toString().isEmpty()) {
            model_field.setError("Model is required");
            isValid = false;
        }

        if (date_field.getText().toString().isEmpty()) {
            date_field.setError("Date is required");
            isValid = false;
        }

        if (value_field.getText().toString().isEmpty()) {
            value_field.setError("Value is required");
            isValid = false;
        }

        return isValid;
    }

    public LinearLayout getNoImagePlaceholder() {
        return this.noImagePlaceholder;
    }

    public LinearLayout getSliderViewFrame() {
        return sliderViewFrame;
    }

    public int getOpenGalleryRequestCode() {
        return this.OPEN_GALLERY_REQUEST_CODE;
    }
}
