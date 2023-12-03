package com.example.sweethome;

/* necessary imports */
import com.google.firebase.Timestamp;

import java.util.ArrayList;

/**
 * @class Tag
 *
 * <p>The Tag class represents a tag with a name and array of usernames.
 * Tags can be associated with items to provide additional categorization or information.</p>
 *
 * @date <p>November 10, 2023</p>
 */
public class Tag {
    /* attributes for this class */
    private String id;
    private String name;
    private Timestamp timestamp;
    private ArrayList<String> usernames;

    public Tag() {}

    // Constructor
    public Tag(String id, String name, Timestamp timestamp, ArrayList<String> usernames) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
        this.usernames = usernames;
    }

    // Getter for id
    public String getTagId() {
        return id;
    }

    // Setter for tag id
    public void setId(String id) {
        this.id = id;
    }

    // Setter for usernames
    public void setUsernames(ArrayList<String> usernames) {
        this.usernames = usernames;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Setter for timestamp
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    // Getter for tagName
    public String getTagName() {
        return name;
    }

    // Getter for timestamp
    public Timestamp getTagTimestamp() {
        return timestamp;
    }

    // Getter for users who have this tag
    public ArrayList<String> getTagUsernames() {
        return usernames;
    }
}
