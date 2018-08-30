package ghasemi.abbas.abzaar.db;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.android.bahaar.ToastActivity;
import com.android.bahaar.TouchImageView;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.core.app.ActivityCompat;
import ghasemi.abbas.abzaar.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * on 07/24/2018.
 */

public class MapsView extends AppCompatActivity {
    LinearLayout linearLayout;
    TextView textView;
    Toolbar toolbar;
    String SITE = Environment.getExternalStorageDirectory() + "/abz/.DataAbz/Images/";
    String name, patch, _image_name;
    TouchImageView touchImageView;
    AVLoadingIndicatorView progress;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ToastActivity.Toast(this,"دسترسی به حافظه به برنامه داده نشده است.",0);
            finish();
            return;
        }
        Intent intent = getIntent();
        name = intent.getExtras().getString("NAME_ACTIVITY");
        patch = intent.getExtras().getString("PATCH");
        setContentView(R.layout.base_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        textView = findViewById(R.id.activity_name);
        textView.setText(name);
        linearLayout = findViewById(R.id.linearLayout);

        touchImageView = new TouchImageView(this);
        touchImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        progress = new AVLoadingIndicatorView(this);
        progress.setIndicatorColor(Color.BLACK);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) getResources().getDimension(R.dimen.w_p), (int) getResources().getDimension(R.dimen.h_p));
        layoutParams.gravity = Gravity.CENTER;
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.addView(progress,layoutParams);
        linearLayout.addView(frameLayout,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        String[] n = patch.split("/");
        _image_name = n[n.length - 1];
        getInfo();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getInfo() {
        File f = new File(SITE + _image_name);
        Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
        if (bmp != null) {
            progress.show();
            linearLayout.removeView(progress);
            linearLayout.addView(touchImageView);
            touchImageView.setImageBitmap(bmp);
        } else {
            ToastActivity.Toast(this, "درحال بارگیری و ذخیره جدیدترین نقشه ، چند لحظه منتظر باشید.", Toast.LENGTH_LONG);
            Glide.with(this).load(patch).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    progress.hide();
                    linearLayout.removeView(progress);
                    linearLayout.addView(touchImageView);
                    touchImageView.setImageDrawable(resource);
                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                    File file = new File(SITE + _image_name);
                    File nomedia = new File(SITE + ".nomedia");
                    FileOutputStream ostream = null;
                    try {
                        if (!file.exists()) {
                            file.mkdirs();
                            file.delete();
                        }
                        nomedia.createNewFile();
                        file.createNewFile();
                        ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                        ostream.flush();
                        ostream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            ostream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }

        /*
        Save images by Picasso and OkHttp
        Picasso mBuilder;
        mBuilder = new Picasso.Builder(this)
                .loggingEnabled(BuildConfig.DEBUG)
                .indicatorsEnabled(BuildConfig.DEBUG)
                .downloader(new OkHttpDownloader(this, 1000))
                .build();
        mBuilder.load(patch).into(getTarget);
         */
    }
}
