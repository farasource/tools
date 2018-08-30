package ghasemi.abbas.abzaar.db.foucault;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ghasemi.abbas.abzaar.BuildVars;
import ghasemi.abbas.abzaar.R;

import com.android.bahaar.TinyDB;
import com.android.bahaar.ToastActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.HelpAlert;
import static ghasemi.abbas.abzaar.Main.Shortcut;

/**
 * on 04/28/2018.
 */

public class Foucault extends AppCompatActivity {
    public static int KEY = 0;
    public ComponentName cn;
    public DevicePolicyManager dp;
    protected ActivityManager am;
    TinyDB db;
    Button Active;
    Button gallery;
    CheckBox pic, push, alarm, system;
    Switch history;
    TextView text;

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
        db = new TinyDB(getApplicationContext());
        setContentView(R.layout.activity_foucault);
        dp = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        cn = new ComponentName(this, LoginReceiver.class);
        if (KEY != 0) {
            getAdminPremision();
            db.putBoolean("AdminPremision", true);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Active = findViewById(R.id.active);
        gallery = findViewById(R.id.gallery);
        pic = findViewById(R.id.pic);
        push = findViewById(R.id.push);
        alarm = findViewById(R.id.alarm);
        system = findViewById(R.id.system);
        history = findViewById(R.id.history);
        text = findViewById(R.id.msg);
        SupportHistory();
        Active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dp.isAdminActive(cn)) {
                    removeActiveAdmin();
                    db.putBoolean("AdminPremision", false);
                    Active.setText("فعال سازی");
                } else {
                    getAdminPremision();
                    db.putBoolean("AdminPremision", true);
                    Active.setText("غیر فعال سازی");
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Foucault.this, Record.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        pic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (findFrontFacingCamera() <= 0) {
                    pic.setChecked(false);
                    ToastActivity.Toast(Foucault.this, "متاسفانه دستگاه شما این قابلیت را پشتیبانی نمی کند.", Toast.LENGTH_SHORT);
                } else {
                    db.putBoolean("picFoucault", b);
                }

            }
        });
        push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                db.putBoolean("pushFoucault", b);
            }
        });
        alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                db.putBoolean("alarmFoucault", b);
            }
        });
        system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                db.putBoolean("systemFoucault", b);
            }
        });
        history.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.putString("textHistory", "");
                db.putBoolean("historyFoucault", isChecked);
                if (isChecked) {
                    findViewById(R.id.fl_history).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.fl_history).setVisibility(View.GONE);
                }

            }
        });
    }

    private void SupportHistory() {

        if (db.getBoolean("AdminPremision", false) && dp.isAdminActive(cn)) {
            Active.setText("غیر فعال سازی");

        }
        if (db.getBoolean("picFoucault", false)) {
            pic.setChecked(true);
        }
        if (db.getBoolean("pushFoucault", false)) {
            push.setChecked(true);
        }
        if (db.getBoolean("alarmFoucault", false)) {
            alarm.setChecked(true);
        }
        if (!db.getBoolean("systemFoucault", true)) {
            system.setChecked(false);
        }
        if (!db.getBoolean("historyFoucault", true)) {
            history.setChecked(false);
            findViewById(R.id.fl_history).setVisibility(View.GONE);
        } else {
            if (!db.getString("textHistory").equals("")) {
                text.setText(db.getString("textHistory"));
            }
        }
    }

    public void getAdminPremision() {
        try {
            Intent intent = new Intent("android.app.action.ADD_DEVICE_ADMIN");
            intent.putExtra("android.app.extra.DEVICE_ADMIN", cn);
            intent.putExtra("android.app.extra.ADD_EXPLANATION", "برای انجام کاری که می خوای،حتما باید دسترسی را صادر کنی\nکنترل و امنیت دستگاه با ابزار");
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode != RESULT_OK) {
                db.putBoolean("AdminPremision", false);
                Active.setText("فعال سازی");
            }
        if (KEY != 0) {
            KEY = 0;
            try {
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void removeActiveAdmin() {
        dp.removeActiveAdmin(cn);
    }

    private int findFrontFacingCamera() {
        int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(i, info);
            if (info.facing == 1) {
                Log.d("Abc", "Camera found");
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_page, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_help:
                HelpAlert(this, "لطفا ابتدا دسترسی های لازم را به برنامه دهید تا درصورت بسته شدن برنامه نیز این قابلیت اجرا شود ، توجه داشته باشید در صورت عدم دادن این مجوز این بخش برنامه کار نخواهد کرد. \n\n روی تصاویر ضبط شده دو انگشت خود را برای زوم به سبک اینستاگرام حرکت دهید.");
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position", 0), Foucault.class.getCanonicalName());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class LoginReceiver extends DeviceAdminReceiver {
        private static Ringtone ringtone;

        private static MediaPlayer mediaPlayer;
        private DevicePolicyManager dp;
        //        lockNow
        TinyDB db;

        public void onEnabled(Context context, Intent intent) {
            db = new TinyDB(context);
            String textOld = db.getString("textHistory");
            db.putInt("PasswordFailed", 0);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            db.putString("textHistory", simpleDateFormat.format(new Date()) + " دسترسی کنترل صفحه قفل به برنامه داده شد.\n\n" + textOld);
        }

        public CharSequence onDisableRequested(Context context, Intent intent) {
            return "در صورت غیرفعال کردن این گزینه ، کارائی بهینه شده برنامه از کار خواهد افتاد.\n آیا مایل به حذف این دسترسی هستید؟";
        }

        public void onDisabled(Context context, Intent intent) {
            db = new TinyDB(context);
            String textOld = db.getString("textHistory");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            db.putString("textHistory", simpleDateFormat.format(new Date()) + " دسترسی کنترل صفحه قفل از برنامه سلب شد.\n\n" + textOld);
        }

        public void onPasswordChanged(Context context, Intent intent) {
            Log.d("Abc", "Changed");
            db = new TinyDB(context);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            String textOld = db.getString("textHistory");
            try {
                db.putString("textHistory", simpleDateFormat.format(new Date()) + " رمز عبور تلفن تغییر یافت.\n\n" + textOld);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public void onPasswordFailed(Context context, Intent intent) {

            db = new TinyDB(context);
            dp = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            String textOld = db.getString("textHistory");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            db.putString("textHistory", simpleDateFormat.format(new Date()) + " تلاش به ورود ناموفق صورت گرفت.\n\n" + textOld);
            if (db.getBoolean("picFoucault", false)) {
                Intent in = new Intent(context, Camera.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(in);
            }
            if (db.getBoolean("pushFoucault", false) && dp.getCurrentFailedPasswordAttempts() == 2) {
                NotificationManager notif = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notif.cancelAll();
                Intent i = new Intent(context, Foucault.class);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, BuildVars.createChannel(context, "unsuccess_alarm", "هشدار ورود ناموفق"));
                builder.setSmallIcon(R.drawable.fozolgir);
                builder.setContentIntent(PendingIntent.getActivity(context, 0, i, 0));
                builder.setContentTitle("هشدار!! ورود ناموفق");
                builder.setContentText("فردی در تلاش ورود به برنامه بود");
                builder.setSubText("برای بررسی لمس کنید");
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, builder.build());
            }
            if (db.getBoolean("alarmFoucault", false) && dp.getCurrentFailedPasswordAttempts() == 3) {
                if (db.getBoolean("systemFoucault", true)) {
                    try {
                        ringtone = RingtoneManager.getRingtone(context, RingtoneManager.getActualDefaultRingtoneUri(context, 1));
                        if (ringtone != null) {
                            ringtone.play();
                        }
                    } catch (Exception e2) {
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer = null;
                        }
                        mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
                        mediaPlayer.setLooping(true);
                        mediaPlayer.setVolume(100, 100);
                        mediaPlayer.start();
                    }
                } else {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer = null;
                    }
                    mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setVolume(100, 100);
                    mediaPlayer.start();
                }
                db.putInt("PasswordFailed", db.getInt("PasswordFailed", 0) + 1);
            }
        }

        public void onPasswordSucceeded(Context context, Intent intent) {
            db = new TinyDB(context);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            String textOld = db.getString("textHistory");
            db.putString("textHistory", simpleDateFormat.format(new Date()) + " قفل صفحه باز گردید.\n\n" + textOld);
            if (ringtone != null) {
                ringtone.stop();
                ringtone = null;
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer = null;
            }

//
//            try {
//                              if (PasswordFailed >= 1) {
//                    if (db.getBoolean("pushFoucault", false) || db.getBoolean("alarmFoucault", false) || db.getBoolean("picFoucault", false)) {
//                        try {
//                            db.putInt("PasswordFailed",0);
//                            Intent auditIntent = new Intent(context, Foucault.class);
//                            auditIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(auditIntent);
//                            return;
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                System.exit(-1);
//            } catch (Exception e2) {
//                e2.printStackTrace();
//            }
        }
    }
}
