package ghasemi.abbas.abzaar.db;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ghasemi.abbas.abzaar.BuildVars;
import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.services.NightService;
import com.android.bahaar.TinyDB;
import com.android.bahaar.ToastActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.HelpAlert;
import static ghasemi.abbas.abzaar.Main.Shortcut;
import static ghasemi.abbas.abzaar.Main.playRingtone;

/**
 * on 06/07/2018.
 */

public class More extends AppCompatActivity {
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String NEW_BRIGHTNESS_PREFERENCE = "newBrightness";
    static TinyDB db;
    TextView tLight, tFlight, tLock;
    EditText etD;
    Switch sLiF, Ch;
    private SeekBar light, fLight;
    private int brightness;
    private ContentResolver cResolver;
    private Window window;
    private Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public static int getCurrentBrightness(ContentResolver cr) {
        int Brightness = 0;
        try {
            Brightness = System.getInt(cr, System.SCREEN_BRIGHTNESS);
        } catch (SettingNotFoundException e) {
            Log.e("Error", "Cannot access system brightness");
            e.printStackTrace();
        }
        return Brightness;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new TinyDB(getApplicationContext());
        setContentView(R.layout.more_activity);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        light = findViewById(R.id.sbL);
        fLight = findViewById(R.id.sbF);
        tLight = findViewById(R.id.textL);
        tFlight = findViewById(R.id.textF);
        tLock = findViewById(R.id.lock);
        etD = findViewById(R.id.etCh);
        sLiF = findViewById(R.id.sF);
        Ch = findViewById(R.id.sCh);

        cResolver = getContentResolver();
        window = getWindow();

        setChange();

        light.setMax(255);
        brightness = getCurrentBrightness(cResolver);
        light.setProgress(brightness);
        tLight.setText(String.format("%s %%", String.valueOf(brightness)));
        light.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    tLight.setText(String.format("%s %%", String.valueOf(progress)));
                    System.putInt(cResolver, System.SCREEN_BRIGHTNESS, progress);
                    LayoutParams layoutpars = window.getAttributes();
                    layoutpars.screenBrightness = progress / (float) 255;
                    window.setAttributes(layoutpars);
                } catch (Exception e) {
                    ToastActivity.Toast(More.this, "دسترسی های لازم داده نشده است!.", 1);
                    youDesirePermissionCode(More.this);
                }
            }
        });


        sLiF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(More.this)) {
                    Intent i = new Intent(getApplicationContext(), NightService.class);
                    NightService.FilterLight = isChecked;
                    if (isChecked) {
                        startService(i);
                        fLight.setEnabled(true);
                        tFlight.setText(String.valueOf(db.getInt(NEW_BRIGHTNESS_PREFERENCE, 50)) + " %");
                        sLiF.setText("غیر فعال سازی");
                    } else {
                        sLiF.setText("فعال سازی");
                        stopService(i);
                        fLight.setEnabled(false);
                    }
                } else {
                    sLiF.setChecked(false);
                    ToastActivity.Toast(More.this, "دسترسی های لازم داده نشده است!.", 1);
                    Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivity(myIntent);
                }
            }
        });
        fLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                db.putInt(NEW_BRIGHTNESS_PREFERENCE, progress);
                tFlight.setText(String.valueOf(progress) + " %");
                sendBroadcast();
            }
        });


        tLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpAlert(More.this, "چگونه تلفنم را با یک کلیک قفل کنم؟ \n برای انجام این کار فقط کافی هست ویجت برنامه را در یکی از صفحات تلفن فعال کنید و با کلیک بروی آن به راحتی تلفن را قفل کنید! \n\n مکان ویجت ها کجاست؟ \n با لمس طولانی بر فضای خالی در یکی از صفحات تلفن خود،پنجره جدیدی باز خواهد شد ، به ته برگ (ویجت ها) بروید و نام برنامه را پیدا کرده و آن را به صفحه گوشیتون بکشید.");
            }
        });


        Ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.putBoolean("isAlarmChargeActive", isChecked);
                if (isChecked) {
                    Ch.setText("غیر فعال سازی");
                    etD.setText(String.valueOf(db.getInt("AlarmChargeActive", 95)));
                    etD.setEnabled(true);
                } else {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    Ch.setText("فعال سازی");
                    etD.setEnabled(false);
                    try {
                        playRingtone.stop();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });
        etD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try {
                    if (!etD.getText().toString().equals("")) {
                        db.putInt("AlarmChargeActive", Integer.parseInt(etD.getText().toString()));
                    } else {
                        db.putInt("AlarmChargeActive", 95);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        getBatteryPercentage();
    }

    private void setChange() {
        if (NightService.FilterLight) {
            sLiF.setChecked(NightService.FilterLight);
            sLiF.setText("غیر فعال سازی");
            fLight.setEnabled(true);
        } else {
            fLight.setEnabled(NightService.FilterLight);
        }
        fLight.setProgress(db.getInt(NEW_BRIGHTNESS_PREFERENCE, 50));
        tFlight.setText(String.valueOf(db.getInt(NEW_BRIGHTNESS_PREFERENCE, 50)) + " %");
        if (db.getBoolean("isAlarmChargeActive", false)) {
            Ch.setChecked(true);
            Ch.setText("غیر فعال سازی");
            etD.setText(String.valueOf(db.getInt("AlarmChargeActive", 95)));
            etD.setEnabled(true);
        }
    }

    private void sendBroadcast() {
        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_SERVICE);
        sendBroadcast(new_intent);
    }

    private static void createNotification(Context c) {
        Intent notificationIntent = new Intent(c, More.class);
        PendingIntent contentIntent = PendingIntent.getActivity(c, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(c, BuildVars.createChannel(c,"in_app_service","فعالیت های درون برنامه ای"));
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.more)
                .setContentTitle("هشدار شارژ!")
                .setContentText("لطفا تلفن را از برق جدا کنید.")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
        notificationManager.notify(540, notification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_page, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position",0), More.class.getCanonicalName());
                break;
            case R.id.item_help:
                HelpAlert(this, "کاربرد فیلتر کردن نور: کاهش و تاثیر موثر در مصرف باطری - مطالعه در شب و عدم آسیب به چشم و ...\n\n کاربرد قفل صفحه: بدون نیاز به فشار دادن کلیدی دستگاه خود را قفل کنید! \n\n کاربرد هشدار شارژ: برای داشتن باطری با طول عمر بیشتر پیشنهاد شده باطری را قبل از 100 درصد شدن و در محدوده 92 تا 97 از برق جدا کرده و  قبل از رسیدن به کمتر از 10 درصد به برق متصل نمائید و همچنین به هیچ عنوان اجازه ندهید باطری بعد از پرشدن در برق به ماند!!(یکی از ناآگاهی ها) \n بعد از تنطیم نباید برنامه بسته شود یا از صفحه تنظیمات خارج شوید، می توانید با فیلتر نور صفحه را کم کنید و یا بدون خارج شدن تلفن را قفل کنید البته توجه کنید از برنامه های مدیرت مصرف منابع استفاده نمی کنید در غیر این صورت برنامه عملکردی ندارد. \n پس از رسیده شدن به درصد مشخص شده یک نوفتیکیشن در اعلانات ظاهر می شود و همچنین زنگ تماس شما به صدا در می آید در 2 حالت مطمئن شوید ، مجوز اعلان فعال است و صدای تلفن شما 0 نشده باشد.");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class BatteryLevel extends BroadcastReceiver {
        int mBatteryLevel;

        public void onReceive(Context mContext, Intent mIntent) {
            int mCurrentLevel = mIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int mScale = mIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (mCurrentLevel >= 0 && mScale > 0) {
                mBatteryLevel = (mCurrentLevel * 100) / mScale;
            }
            if (mBatteryLevel == db.getInt("AlarmChargeActive", 95) && db.getBoolean("isAlarmChargeActive", false)) {
                createNotification(mContext);
                try {
                    playRingtone = RingtoneManager.getRingtone(mContext, RingtoneManager.getDefaultUri(1));
                    if (playRingtone != null) {
                        if (!playRingtone.isPlaying()) {
                            playRingtone.play();
                        }
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (Exception e2) {
                    ToastActivity.Toast(mContext, "متاسفانه نتونستم آهنگی رو پخش کنم!!", Toast.LENGTH_SHORT);
                    e2.printStackTrace();
                }

            }
        }
    }


    public void getBatteryPercentage() {
        registerReceiver(new More.BatteryLevel(), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public static void youDesirePermissionCode(Activity context) {
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (!permission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, 5);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SETTINGS}, 5);
            }
        }
    }

}