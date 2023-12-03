package com.example.sweethome;

/* necessary imports */
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @class Item
 *
 * <p>This class models an item in SweetHome. Item objects
 * contain information on their name, their make, their model
 * an optional serial number, their estimated value, the date
 * they were purchased/acquired, and an optional comment on
 * the item. These fields are the attributes of this class and
 * they can be accessed through getters and setters only to
 * encourage encapsulation.</p>
 *
 * @date <p>October 28, 2023</p>
 */
public class Item implements Serializable {
    /* attributes of this class */
    private String name;
    private String description;
    private String make;
    private String model;
    private String serialNumber;
    private double estimatedValue;
    private transient Timestamp purchaseDate;
    private String comment;
    private Boolean selected = false;
    private ArrayList<String> photos;
    private String id;
    private ArrayList<String> tags;
    private String username;

    /* constructor for this class */
    public Item(String id, String name, String description, String make, String model, String serialNumber, double estimatedValue, Timestamp purchaseDate, String comment, ArrayList<String> photos, ArrayList<String> tags, String username) {
        this.id = id;
        this.name = name;
        this.make = make;
        this.description = description;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estimatedValue = estimatedValue;
        this.purchaseDate = purchaseDate;
        this.comment = comment;
        this.photos = new ArrayList<>(photos);
        this.tags = new ArrayList<>(tags);
        this.username = username;
    }

    public Item(String id, String name, String description, String make, String model, String serialNumber, double estimatedValue, Timestamp purchaseDate, String comment, ArrayList<String> photos, ArrayList<String> tags, Boolean selected, String username) {
        this.id = id;
        this.name = name;
        this.make = make;
        this.description = description;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estimatedValue = estimatedValue;
        this.purchaseDate = purchaseDate;
        this.comment = comment;
        this.photos = new ArrayList<>(photos);
        this.selected = selected;
        this.tags = new ArrayList<>(tags);
        this.username = username;
    }

    /* no-arg constructor for this class */
    public Item(){
        //no argument constructor is necessary for using toObject in firestore
    }

    /* getters and setters for each attribute in this class */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public double getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(double estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public Timestamp getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Timestamp purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getItemId() {
        return id;
    }

    public void setItemId(String id) {
        this.id = id;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = new ArrayList<>(photos);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ArrayList<String> getTags() {
        return this.tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}