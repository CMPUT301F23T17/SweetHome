package com.example.sweethome;

/* necessary imports */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @class NetworkChangeReceiver
 *
 * <p>This class is used in MainActivity and ManageItemActivity to continuously checks the internet
 * connection of the app.</p>
 *
 * @date <p>December 4, 2023</p>
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    public interface NetworkChangeListener {
        void onNetworkChanged(boolean isConnected);
    }

    private NetworkChangeListener listener;

    public void setListener(NetworkChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (listener != null) {
            listener.onNetworkChanged(isNetworkAvailable(context));
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
