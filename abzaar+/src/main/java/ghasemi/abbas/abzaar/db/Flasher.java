package ghasemi.abbas.abzaar.db;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.self.SelfService;

import com.android.bahaar.TinyDB;
import com.android.bahaar.ToastActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.Shortcut;
import static ghasemi.abbas.abzaar.db.More.getCurrentBrightness;

/**
 * on 04/20/2018.
 */

public class Flasher extends AppCompatActivity {
    public static Camera cam = null;
    public static ContentResolver cResolver;
    public static int brightness;
    TinyDB db;
    TextView exit;
    int x = 0;
    Button b1, b2, b3;
    SeekBar s1, s2;
    String ACTION_STRING_SERVICE = "ToServiceSelf";
    Switch open;
    SeekBar border, size, site;
    private boolean flashLightOn, flasherLightOn;
    private int mBatteryLevel;
    private LinearLayout layoutAsli;
    private LinearLayout layoutFaree;
    private int brightness2;
    private int on, off;
    private Handler handler = new Handler();
    private Camera.Parameters p;

    Runnable onLight = new Runnable() {
        @Override
        public void run() {
            flashLightOn();
            handler.postDelayed(offLight, off);
        }
    }, offLight = new Runnable() {
        @Override
        public void run() {
            flashLightOff();
            if (flasherLightOn) {
                handler.postDelayed(onLight, on);
            }

        }
    };

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
        setContentView(R.layout.activity_light);
        db = new TinyDB(this);
        on = db.getInt("timeoutLight", 5);
        off = db.getInt("timeoutOff", 5);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        cResolver = getContentResolver();
        b1 = findViewById(R.id.button1);
        b2 = findViewById(R.id.button2);
        b3 = findViewById(R.id.button3);
        s1 = findViewById(R.id.seekBar1);
        s2 = findViewById(R.id.seekBar2);
        exit = findViewById(R.id.exit);
        layoutAsli = findViewById(R.id.LayoutAsli);
        layoutFaree = findViewById(R.id.LayoutFaree);
        layoutAsli.setVisibility(View.VISIBLE);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flasherLightOn) {
                    return;
                }
                if (flashLightOn) {
                    b1.setText("روشن کردن چراغ قوه");
                    flashLightOn = false;
                    b3.setEnabled(true);
                    flashLightOff();
                } else {
                    b1.setText("خاموش کردن چراغ قوه");
                    flashLightOn = true;
                    b3.setEnabled(false);
                    flashLightOn();
                }
            }
        });
        b1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ToastActivity.Toast(getApplicationContext(), "شارژ باطری " + mBatteryLevel + "٪", Toast.LENGTH_SHORT);
                return false;
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brightness = getCurrentBrightness(cResolver);
                layoutAsli.setVisibility(View.GONE);
                layoutFaree.setVisibility(View.VISIBLE);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashLightOn) {
                    return;
                }
                if (flasherLightOn) {
                    b3.setText("روشن کردن فلاشر");
                    flasherLightOn = false;
                    b1.setEnabled(true);
                } else {
                    b3.setText("خاموش کردن فلاشر");
                    flasherLightOn = true;
                    b1.setEnabled(false);
                    onLight.run();
                }
            }
        });
        s1.setProgress(getProgress(on));
        s2.setProgress(getProgress(off));
        s1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0:
                        on = 5;
                        break;
                    case 1:
                        on = 20;
                        break;
                    case 2:
                        on = 50;
                        break;
                    case 3:
                        on = 100;
                        break;
                    case 4:
                        on = 200;
                        break;
                    case 5:
                        on = 400;
                        break;
                    case 6:
                        on = 600;
                        break;
                    case 7:
                        on = 900;
                        break;
                    case 8:
                        on = 1000;
                }
                db.putInt("timeoutLight", on);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        s2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0:
                        off = 5;
                        break;
                    case 1:
                        off = 20;
                        break;
                    case 2:
                        off = 50;
                        break;
                    case 3:
                        off = 100;
                        break;
                    case 4:
                        off = 200;
                        break;
                    case 5:
                        off = 400;
                        break;
                    case 6:
                        off = 600;
                        break;
                    case 7:
                        off = 900;
                        break;
                    case 8:
                        off = 1000;
                }
                db.putInt("timeoutOff", off);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        layoutFaree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAsli.setVisibility(View.VISIBLE);
                layoutFaree.setVisibility(View.GONE);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness = brightness / (float) 255;
                getWindow().setAttributes(lp);

            }
        });
        cam = Camera.open();
        p = cam.getParameters();
        registerReceiver(new Flasher.BatteryLevel(), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        open = findViewById(R.id.selfFlash);

        border = findViewById(R.id.border);
        site = findViewById(R.id.site);
        size = findViewById(R.id.size);

        open.setChecked(SelfService.isSelectOpenSFF);
        open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (youDesirePermissionCode(Flasher.this)) {
                    SelfService.isSelectOpenSFF = isChecked;
                    Intent i = new Intent(getApplicationContext(), SelfService.class);
                    if (isChecked) {
                        brightness2 = getCurrentBrightness(cResolver);
                        startService(i);
                        open.setText("غیر فعال سازی");
                        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 250);
                    } else {
                        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness2);
                        open.setText("روشن کردن فلاش");
                        stopService(i);
                    }
                } else {
                    open.setChecked(false);
                }
            }
        });
        size.setProgress(db.getInt("newSelfCircleSize", 2) - 1);
        size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                db.putInt("newSelfCircleSize", progress + 1);
                sendBroadcast();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        site.setProgress(db.getInt("newSelfCircleSite", 2) - 1);
        site.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                db.putInt("newSelfCircleSite", progress + 1);
                sendBroadcast();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        border.setProgress(db.getInt("newSelfCircleBorder", 200));
        border.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                db.putInt("newSelfCircleBorder", progress);
                sendBroadcast();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private int getProgress(int timeoutLight) {
        switch (timeoutLight) {
            case 5:
                return 0;
            case 20:
                return 1;
            case 50:
                return 2;
            case 100:
                return 3;
            case 200:
                return 4;
            case 400:
                return 5;
            case 600:
                return 6;
            case 900:
                return 7;
            case 1000:
                return 8;
            default:
                return 0;
        }
    }

    public void flashLightOn() {
        try {
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(p);
            cam.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            ToastActivity.Toast(getApplicationContext(), "شما دسترسی برنامه به دوربین را محدود کرده اید!", Toast.LENGTH_SHORT);
        }
    }

    public void flashLightOff() {
        try {
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            cam.setParameters(p);
            cam.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_page, menu);
        menu.findItem(R.id.item_help).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position", 0), Flasher.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        flashLightOn = flasherLightOn = false;
        if (cam != null) {
            cam.stopPreview();
            cam.release();
            cam = null;
        }
        super.onDestroy();
    }

    private void sendBroadcast() {
        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_SERVICE);
        sendBroadcast(new_intent);
    }

    public boolean youDesirePermissionCode(Activity context) {
        boolean permission1 = true, permission2;
        if (Build.VERSION.SDK_INT >= 23) {
            permission1 = Settings.canDrawOverlays(context);
            if (!permission1) {
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                context.startActivity(myIntent);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission2 = Settings.System.canWrite(context);
        } else {
            permission2 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }

        if (!permission2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, 5);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SETTINGS}, 5);
            }
        }
        return permission1 && permission2;
    }

    class BatteryLevel extends BroadcastReceiver {
        public void onReceive(Context mContext, Intent mIntent) {
            int mCurrentLevel = mIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int mScale = mIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (mCurrentLevel >= 0 && mScale > 0) {
                Flasher.this.mBatteryLevel = (mCurrentLevel * 100) / mScale;
            }
            if (Flasher.this.mBatteryLevel <= 19 && x == 0) {
                ToastActivity.Toast(Flasher.this, "شارژ باطری کمتر از ۲۰ درصد است!", Toast.LENGTH_SHORT);
            }
            x++;
        }
    }
}
