package com.example.sweethome;

/* necessary imports */
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import javax.annotation.Nullable;

/**
 * @class ScanningBarcodeActivity
 *
 * <p>The ScanningBarcodeActivity class represents the activity for scanning a barcode and
 * returning the extracted barcode to {@link com.example.sweethome.ManageItemActivity}.
 * The barcode is then processed in {@link com.example.sweethome.ManageItemActivity}
 * to get the associated product information.</p>
 *
 * @date <p>November 15, 2023</p>
 */
public class ScanningBarcodeActivity extends AppCompatActivity {
    /* attributes of this class */
    private String scannedBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    /**
     * Gets the result after scanning barcode and send it back to ManageItemActivity.
     *
     * @param requestCode The request code passed to startActivityForResult.
     * @param resultCode The result code returned by the child activity.
     * @param data The data returned by the child activity.
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentIntegrator.REQUEST_CODE && resultCode == RESULT_CANCELED) {
            onBackPressed();
            return;
        }

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            if (scanResult.getContents() != null) {
                scannedBarcode = scanResult.getContents();
                sendResultBack();
            }
        }
    }

    /**
     * Send extracted barcode back to ManageItemActivity.
     */
    private void sendResultBack() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SCANNED_BARCODE", scannedBarcode);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
