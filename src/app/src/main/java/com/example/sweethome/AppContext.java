package com.example.sweethome;

/* necessary imports */
import android.app.Application;

/**
 * @class AppContext
 *
 * <p>The AppContext class manages global variables that can be accessed
 * in any activity of the app.</p> <p>This class currently only manages
 * the global variable "username", which is the username of the
 * currently logged in user.</p>
 *
 * @date <p>Dec 1, 2023</p>
 */
public class AppContext extends Application {
    /* attributes of this class */
    public String username;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
