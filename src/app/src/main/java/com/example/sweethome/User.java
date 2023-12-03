package com.example.sweethome;

/**
 * @class User
 *
 * <p>This class represents a user with a corresponding username and email.</p>
 *
 * @date <p>December 1, 2023</p>
 */
public class User {
    /* attributes for this class */
    private String username;
    private String email;

    /* no-arg constructor for this class */
    public User(){
        //no argument constructor is necessary for using toObject in firestore
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

}
