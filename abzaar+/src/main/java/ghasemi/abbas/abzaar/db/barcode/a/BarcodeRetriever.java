package ghasemi.abbas.abzaar.db.barcode.a;

import android.util.SparseArray;

import com.google.android.gms.vision.barcode.Barcode;
import ghasemi.abbas.abzaar.db.barcode.b.BarcodeGraphic;

import java.util.List;

/**
 * Created by zone2 on 10/11/16.
 */

public interface BarcodeRetriever {
    void onRetrieved(Barcode barcode);

    void onRetrievedMultiple(Barcode closetToClick, List<BarcodeGraphic> barcode);

    void onBitmapScanned(SparseArray<Barcode> sparseArray);

    void onRetrievedFailed(String reason);

    void onPermissionRequestDenied();
}
