package ghasemi.abbas.abzaar.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import ghasemi.abbas.abzaar.R;

import com.android.bahaar.TouchImageView;
import com.wang.avi.AVLoadingIndicatorView;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.HelpAlert;
import static ghasemi.abbas.abzaar.Main.Shortcut;

public class Trafic extends AppCompatActivity {
    TouchImageView imageView;
    AVLoadingIndicatorView progress;
    View view;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView textView = findViewById(R.id.activity_name);
        textView.setText("ترافیک راه ها");

        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        FrameLayout frameLayout = new FrameLayout(this);

        WebView view = new WebView(getFixedContext(this));
        view.getSettings().setJavaScriptEnabled(true);
        frameLayout.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.addView(frameLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.loadUrl("file:///android_asset/iranmap/index.html");
        view.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Trafic.this.view.setVisibility(View.VISIBLE);
                progress.show();
                Glide.with(Trafic.this).load(url).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                        imageView.setVisibility(View.VISIBLE);
                        progress.hide();
                        progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        this.view = new View(this);
        this.view.setVisibility(View.GONE);
        this.view.setBackgroundColor(Color.WHITE);
        frameLayout.addView(this.view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        progress = new AVLoadingIndicatorView(this);
        progress.setIndicatorColor(Color.BLACK);
        progress.hide();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) getResources().getDimension(R.dimen.w_p), (int) getResources().getDimension(R.dimen.h_p));
        layoutParams.gravity = Gravity.CENTER;
        frameLayout.addView(progress, layoutParams);
        imageView = new TouchImageView(this);
        imageView.setVisibility(View.GONE);
        imageView.setBackgroundColor(Color.WHITE);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        frameLayout.addView(imageView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }


    @Override
    public void onBackPressed() {
        if (imageView.getVisibility() == View.VISIBLE) {
            imageView.setVisibility(View.GONE);
            this.view.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_page, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private Context getFixedContext(Context context) {
               return context;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position",0), Trafic.class.getCanonicalName());
                break;
            case R.id.item_help:
                HelpAlert(this, "برای نمایش ترافیک راه ها استان مورد نظر را لمس نمائید. \n\n" +
                        "توسط: مرکز مدیریت راه های کشور \n" +
                        "141.ir");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
