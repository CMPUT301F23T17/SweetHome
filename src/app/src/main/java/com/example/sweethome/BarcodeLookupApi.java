package com.example.sweethome;

/* necessary imports */
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @class BarcodeLookupApi
 *
 * <p>The BarcodeLookupApi class performs an api call to Go-UPC
 * backend to look up a barcode we have scanned.</p>
 * <p>The Api is called in another thread.</p>
 *
 * @date <p>November 18, 2023</p>
 */
public class BarcodeLookupApi extends AsyncTask<String, Void, ReturnedItemData> {
    /* attributes of this class */
    private static final String TAG = BarcodeLookupApi.class.getSimpleName();
    private String barcodeLookupApiKey = "8740f02750738893c6b4dc279ed33fdd8231474da1ebbfec45b328287c7f544c";
    private String barcodeLookupApiBaseUrl = "https://go-upc.com/api/v1/code/";
    private String name;
    private String description;
    private String make;
    private BarcodeLookupListener listener;

    /**
     * Make an http request to look up our barcode in Go-UPC's database.
     */
    @Override
    protected ReturnedItemData doInBackground(String... params) {
        if (params.length == 0) {
            Log.e(TAG, "No barcode scanned.");
            return null;
        }
        String scannedBarcode = params[0];
        String url = barcodeLookupApiBaseUrl + scannedBarcode;
        try {
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + barcodeLookupApiKey);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                String responseData = response.toString();
                JSONObject productData = new JSONObject(responseData);
                name = productData.getJSONObject("product").getString("name");
                description = productData.getJSONObject("product").getString("description");
                make = productData.getJSONObject("product").getString("brand");
                return new ReturnedItemData(name, description, make);
            } else {
                Log.e(TAG, "HTTP GET request failed with response code: " + responseCode);
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Execute an effect after calling the api.
     */
    @Override
    protected void onPostExecute(ReturnedItemData result) {
        if (listener != null) {
            listener.onBarcodeLookupComplete(result);
        }
    }

    /**
     * Execute an effect after calling the api.
     */
    public void setBarcodeLookupListener(BarcodeLookupListener listener) {
        this.listener = listener;
    }

    /**
     * A listener to know when the barcode look up api call has completed.
     * Execute the effect when it's completed.
     */
    public interface BarcodeLookupListener {
        void onBarcodeLookupComplete(ReturnedItemData result);
    }
}
