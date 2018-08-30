package ghasemi.abbas.abzaar.db.color;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.bahaar.TinyDB;
import com.android.bahaar.ToastActivity;

import androidx.core.app.ActivityCompat;
import ghasemi.abbas.abzaar.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.Shortcut;

public class CameraColorActivity extends AppCompatActivity implements Preview.PreviewListener, OnTouchListener {

    String currentColor, currentNamedColor;
    FrameLayout preview;
    LinearLayout centerLayout;
    private Preview mPreview;
    private boolean isPaused = false;
    private boolean isFlush = false;
    private boolean isImage = false;
    private TextView colorView1, colorView2;
    private ImageView play_pause, i, flush, camera;
    private ColorData cdata;
    private int radius = 5;
    private OutlineDrawableView centerView;
    private int[] pausedPixels = null;
    private int pausedWidth;
    private int pausedHeight;
    private int cX;
    private int cY;

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
        TinyDB.getInstance(this).putInt("version_" + CameraColorActivity.class.getCanonicalName(), 1);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        cdata = new ColorData(this);

        setContentView(R.layout.activity_camera_color);

        colorView1 = findViewById(R.id.camera_result1);
        colorView2 = findViewById(R.id.camera_result2);
        ImageView imageView = findViewById(R.id.mianbar);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Shortcut(getIntent().getIntExtra("position", 0), CameraColorActivity.class.getCanonicalName());
            }
        });

        colorView2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                copy();
            }
        });
        colorView1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                copy();
            }
        });

        radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radius, getResources().getDisplayMetrics());

        mPreview = new Preview(this);
        preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        centerLayout = findViewById(R.id.camera_activity_center);
        centerView = new OutlineDrawableView(this, radius);
        centerLayout.addView(centerView);
        centerLayout.setOnTouchListener(this);

        cX = 2 * radius;
        cY = 2 * radius;

        play_pause = findViewById(R.id.play_pause_button);
        play_pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                pause();
            }
        });
        ImageView gallery = findViewById(R.id.gallery_button);
        gallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isFlush) {
                    Flush();
                }
                isPaused = false;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "انتخاب تصویر"), 512);
            }
        });

        flush = findViewById(R.id.flush_button);
        flush.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Flush();
            }
        });

        camera = findViewById(R.id.camera_button);
        camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                isImage = false;
                preview.removeAllViews();
                preview.addView(mPreview);
                flush.setVisibility(View.VISIBLE);
                play_pause.setVisibility(View.VISIBLE);
                camera.setVisibility(View.GONE);
            }
        });

    }

    private void copy() {
        ToastActivity.Toast(CameraColorActivity.this, "کپی شد.", Toast.LENGTH_SHORT);
        ClipboardManager clipboardManager = (ClipboardManager) CameraColorActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
        final android.content.ClipData clipData = android.content.ClipData.newPlainText("label", colorView1.getText()
                .toString().replace("color: ", ""));
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }
    }

    private void Flush() {
        isFlush = !isFlush;
        if (isFlush) {
            flush.setColorFilter(0xffffbf00);
        } else {
            flush.setColorFilter(0xffffffff);
        }
        if (getBaseContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            mPreview.resetBuffer();
            mPreview.flash();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 512) {
            if (resultCode == RESULT_OK)
                setImage(data.getData());
        }
    }

    private void setImage(Uri uri) {
        isImage = true;
        i = new ImageView(this);
//        i.setAdjustViewBounds(true);
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        i.setImageURI(uri);
        preview.removeAllViews();
        preview.addView(i);
        flush.setVisibility(View.GONE);
        camera.setVisibility(View.VISIBLE);
        play_pause.setVisibility(View.GONE);
        i.setOnTouchListener(this);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        cX = centerView.getWidth() / 2;
        cY = centerView.getHeight() / 2;
        centerView.move(cX, cY);
        centerView.invalidate();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isPaused) {
            pause();
        }
    }


    private void updateColors() {
        int red = 0;
        int green = 0;
        int blue = 0;
        int cWidth = centerView.getWidth();
        int cHeight = centerView.getHeight();
        int scaleX = (int) ((1.0 * (cWidth - cX)) / cWidth * pausedHeight);
        int scaleY;
        if (mPreview.isFrontCamera()) {
            scaleY = (int) ((1.0 * (cHeight - cY)) / cHeight * pausedWidth);
        } else {
            scaleY = (int) ((1.0 * cY) / cHeight * pausedWidth);
        }
        for (int i = scaleX - radius; i < scaleX + radius; i++) {
            for (int j = scaleY - radius; j < scaleY + radius; j++) {
                int index = i * pausedWidth + j;
                int pixel = pausedPixels[index];
                red += Color.red(pixel);
                green += Color.green(pixel);
                blue += Color.blue(pixel);
            }
        }
        double pow = Math.pow((2.0 * radius), 2);
        int color = Color.rgb((int) (red / pow), (int) (green / pow), (int) (blue / pow));
        int[] col = {Color.red(color), Color.green(color), Color.blue(color)};
        currentColor = cdata.ColorToString(col);
        currentNamedColor = cdata.closestColor(col);
        boolean isDarkColor = cdata.isDarkColor(col);

        colorView1.setBackgroundColor(Color.parseColor(currentColor));
        colorView1.setText("color:" + " " + currentColor);

        colorView2.setBackgroundColor(Color.parseColor(currentColor));
        colorView2.setText(cdata.getColorName(currentNamedColor) + " = " + currentNamedColor);
        if (isDarkColor) {
            colorView1.setTextColor(Color.WHITE);
            colorView2.setTextColor(Color.WHITE);
        } else {
            colorView1.setTextColor(Color.BLACK);
            colorView2.setTextColor(Color.BLACK);
        }
    }


    @Override
    //TODO: it seems this is getting called once after pausing the preview
    public void OnPreviewUpdated(int[] pixels, int width, int height) {
        if (pixels != null) {
            this.pausedPixels = pixels;
            this.pausedWidth = width;
            this.pausedHeight = height;
            updateColors();
            if (!isPaused) {
                mPreview.resetBuffer();
            }
        }
    }

    public void pause() {
        isPaused = !isPaused;
        mPreview.pause(isPaused);
        if (isPaused) {
            play_pause.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
        } else {
            play_pause.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
            mPreview.resetBuffer();
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int numTouch = event.getPointerCount();
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (numTouch == 1) {
            if (x < 2 * radius) x = 2 * radius;
            else if (x > view.getWidth() - 2 * radius) x = view.getWidth() - 2 * radius;
            if (y < 2 * radius) y = radius;
            else if (y > view.getHeight() - 2 * radius) y = view.getHeight() - 2 * radius;
            cX = x;
            cY = y;
            centerView.move(cX, cY);
            centerView.invalidate();
            if (isImage) {
                UIC(x, y);
            } else if (isPaused) {
                updateColors();
            }
            return true;
        }
        return false;
    }

    private void UIC(int x, int y) {
        Bitmap bitmap = ((BitmapDrawable) i.getDrawable()).getBitmap();
        float yy = (float) y * 100 / i.getHeight();
        float xx = (float) x * 100 / i.getWidth();
        y = (int) (bitmap.getHeight() * yy / 100);
        x = (int) (bitmap.getWidth() * xx / 100);
        if (y < bitmap.getHeight() && x < bitmap.getWidth()) {
            int pixel = bitmap.getPixel(x, y);
            int[] col = {Color.red(pixel), Color.green(pixel), Color.blue(pixel)};
            currentColor = cdata.ColorToString(col);
            currentNamedColor = cdata.closestColor(col);
            boolean isDarkColor = cdata.isDarkColor(col);

            colorView1.setBackgroundColor(Color.parseColor(currentColor));
            colorView1.setText("color:" + " " + currentColor);

            colorView2.setBackgroundColor(Color.parseColor(currentColor));
            colorView2.setText(cdata.getColorName(currentNamedColor) + " = " + currentNamedColor);
            if (isDarkColor) {
                colorView1.setTextColor(Color.WHITE);
                colorView2.setTextColor(Color.WHITE);
            } else {
                colorView1.setTextColor(Color.BLACK);
                colorView2.setTextColor(Color.BLACK);
            }
        }
    }
}
