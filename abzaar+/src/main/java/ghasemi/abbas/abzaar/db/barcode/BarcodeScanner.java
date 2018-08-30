/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ghasemi.abbas.abzaar.db.barcode;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;

import androidx.core.app.ActivityCompat;
import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.barcode.a.BarcodeRetriever;
import ghasemi.abbas.abzaar.db.barcode.b.BarcodeCapture;
import ghasemi.abbas.abzaar.db.barcode.b.BarcodeGraphic;
import ghasemi.abbas.abzaar.db.barcode.b.camera.CameraSource;
import com.android.bahaar.TinyDB;
import com.android.bahaar.ToastActivity;

import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.HelpAlert;
import static ghasemi.abbas.abzaar.Main.Shortcut;
import static ghasemi.abbas.abzaar.R.id.barcode;


/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class BarcodeScanner extends AppCompatActivity implements BarcodeRetriever {

    // use a compound button so either checkbox or switch widgets work.


    private static final String TAG = "BarcodeMain";
    TinyDB db;
    CheckBox pause;
    SwitchCompat drawRect, autoFocus, supportMultiple, touchBack, drawText, flash, frontCam;
    LinearLayout action, action2;
    BarcodeCapture barcodeCapture;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ToastActivity.Toast(this,"دسترسی به دوربین به برنامه داده نشده است.",0);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.barcode_scanner);
        db = new TinyDB(getApplicationContext());
        if (db.getBoolean("BS-HelpAlert", true)) {
            HelpAlert(this, "برای اعمال شدن هر تنطیم ، باید دکمه فعال سازی را لمس نمائید. \n در صورت وجود همزمان چندین بارکد می توانید قابیلت چندگانه را فعال نمائید. \n با لمس طولانی دکمه فعال سازی می توانید تنطیمات را محو کنید.");
            db.putBoolean("BS-HelpAlert", false);
        }
        barcodeCapture = (BarcodeCapture) getSupportFragmentManager().findFragmentById(barcode);
        barcodeCapture.setRetrieval(this);

        pause = findViewById(R.id.pause);
        drawRect = findViewById(R.id.draw_rect);
        autoFocus = findViewById(R.id.focus);
        supportMultiple = findViewById(R.id.support_multiple);
        touchBack = findViewById(R.id.touch_callback);
        drawText = findViewById(R.id.draw_text);
        flash = findViewById(R.id.on_flash);
        frontCam = findViewById(R.id.front_cam);

        action = findViewById(R.id.actions);
        action2 = findViewById(R.id.action);
        findViewById(R.id.mianbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shortcut(getIntent().getIntExtra("position",0), BarcodeScanner.class.getCanonicalName());
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeCapture.stopScanning();
            }
        });

        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pause.isChecked() && touchBack.isChecked()) {
                    ToastActivity.Toast(BarcodeScanner.this,"خطا! نمی توان دو حالت لمس و نمایش خودکار همزمان فعال باشند.", Toast.LENGTH_LONG);
                    return;
                } else if (supportMultiple.isChecked() || pause.isChecked() && supportMultiple.isChecked()) {
                    touchBack.setChecked(true);
                    pause.setChecked(false);
                } else if (!pause.isChecked() && !touchBack.isChecked()) {
                    if (supportMultiple.isChecked()) {
                        touchBack.setChecked(true);
                    } else {
                        pause.setChecked(true);
                    }
                }
                barcodeCapture.setShowDrawRect(drawRect.isChecked())
                        .setSupportMultipleScan(supportMultiple.isChecked())
                        .setTouchAsCallback(touchBack.isChecked())
                        .shouldAutoFocus(autoFocus.isChecked())
                        .setShowFlash(flash.isChecked())
                        .setBarcodeFormat(Barcode.ALL_FORMATS)
                        .setCameraFacing(frontCam.isChecked() ? CameraSource.CAMERA_FACING_FRONT : CameraSource.CAMERA_FACING_BACK)
                        .setShouldShowText(drawText.isChecked());
                if (pause.isChecked())
                    barcodeCapture.resume();
                else
                    barcodeCapture.pause();
                barcodeCapture.refresh(true);


            }
        });
        findViewById(R.id.refresh).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                action.setVisibility(View.GONE);
                action2.setVisibility(View.VISIBLE);
                return false;
            }
        });
        findViewById(R.id.type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.setVisibility(View.VISIBLE);
                action2.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRetrieved(final Barcode barcode) {
        Log.d(TAG, "Barcode read: " + barcode.displayValue);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog show =  new AlertDialog.Builder(BarcodeScanner.this,R.style.Theme_Dialog_Alert)
                        .setTitle("کد خوانده شده")
                        .setMessage(barcode.displayValue)
                        .setNegativeButton("برداشت", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                copyText(BarcodeScanner.this,barcode.displayValue);
                            }
                        })
                      .create();
                show.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
                show.show();
            }
        });
        barcodeCapture.stopScanning();


    }

    @Override
    public void onRetrievedMultiple(final Barcode closetToClick, final List<BarcodeGraphic> barcodeGraphics) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = "تمامی کدها به ترتیب : \n";
                for (int index = 0; index < barcodeGraphics.size(); index++) {
                    Barcode barcode = barcodeGraphics.get(index).getBarcode();
                    message += (index + 1) + ". " + barcode.displayValue + "\n";
                }
                final String finalMessage = message;
                AlertDialog show =  new AlertDialog.Builder(BarcodeScanner.this,R.style.Theme_Dialog_Alert)
                        .setTitle("کد خوانده شده")
                        .setMessage(finalMessage)
                        .setNegativeButton("برداشت", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                copyText(BarcodeScanner.this, finalMessage);
                            }
                        })
                       .create();
                show.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
                show.show();
            }
        });

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            Barcode barcode = sparseArray.valueAt(i);
            Log.e("value", barcode.displayValue);
        }

    }

    @Override
    public void onRetrievedFailed(String reason) {

    }

    @Override
    public void onPermissionRequestDenied() {

    }

    private void copyText(Context context, String s) {
        ((ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE)).setText(s);
        ToastActivity.Toast(context,"کپی شد.", Toast.LENGTH_SHORT);

    }


}
