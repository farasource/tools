package ghasemi.abbas.abzaar.db.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.bahaar.ToastActivity;

import androidx.core.app.ActivityCompat;
import ghasemi.abbas.abzaar.R;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * on 07/27/2018.
 */

public class CameraZoom extends AppCompatActivity{
    LinearLayout linearLayout;
    TextView textView;
    String name;
    public static boolean isCameraFront;
    Toolbar toolbar;
    private CameraPreview mPreview;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ToastActivity.Toast(this,"دسترسی به دوربین به برنامه داده نشده است.",0);
            finish();
            return;
        }
        Intent i = getIntent();
        name = i.getExtras().getString("NAME_ACTIVITY");
        isCameraFront = i.getExtras().getBoolean("IS_CAMERA_FRONT");
        setContentView(R.layout.base_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        textView = (TextView) findViewById(R.id.activity_name);
        textView.setText(name);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        mPreview = new CameraPreview(this);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        frameLayout.addView(mPreview);
        linearLayout.addView(frameLayout);

        if (!isCameraFront) {
       //     mPreview.flash();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        onBackPressed();
        super.onPause();
    }

}
