package ghasemi.abbas.abzaar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.bahaar.TinyDB;
import com.android.bahaar.ToastActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.BuildVars.getAllApp;
import static ghasemi.abbas.abzaar.BuildVars.getRate;


public class Start extends AppCompatActivity {
    TinyDB db;
    int x = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("PrivateResource")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.background_material_light));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Main.activity = this;
        db = new TinyDB(getApplicationContext());

        setPushe();
        String position = getIntent().getStringExtra("shortcut");
        if (position != null) {
            try {
                Class c = Class.forName(position);
                startActivity(new Intent(this, c));
                finish();
            } catch (ClassNotFoundException e) {
                ToastActivity.Toast(this,"برنامه ی هدف پیدا نشد،لطفا مجددا میانبر را ایجاد کنید",1);
                callMain();
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    db.putInt("StarApp", db.getInt("StarApp", 0) + 1);
                    if (db.getInt("StarApp", 0) == 5 || db.getInt("StarApp", 0) == 25 || db.getInt("StarApp", 0) == 100) {
                        AlertDialog show = new AlertDialog.Builder(Start.this, R.style.Theme_Dialog_Alert)
                                .setTitle("امتیاز به برنامه")
                                .setMessage("با نظر و امتیاز پرستاره به برنامه،گامی بلند درجهت بهبود کیفیت آن بردارید")
                                .setCancelable(false)
                                .setPositiveButton(
                                        "نظر می دم",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int whichButton) {
                                                callMain();
                                                getRate(Start.this, BuildVars.getIntent());

                                            }
                                        })
                                .setNegativeButton(
                                        "محصولات دیگر",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int whichButton) {
                                                callMain();
                                                getAllApp(Start.this);
                                            }
                                        })
                                .setNeutralButton("لغو", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        callMain();
                                    }
                                })
                                .create();
                        show.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
                        show.show();
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && db.getBoolean("getPermission", true)) {
                        new AlertDialog.Builder(Start.this, R.style.Theme_Dialog_Alert).setCancelable(false).setMessage("ابزار برای خدمات دهی کامل نیاز به دسترسی به میکروفون،دوربین،حافظه دستگاه،مخاطبین و تلفن شما دارد.\n\n عدم اجازه دسترسی ها باعث مختل شدن بخش های مربوطه یا ایجاد خطا در برنامه میشود. لذا لطفا همه دسترسی ها را تایید نمائید.").setPositiveButton("باشه", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{"android.permission.CAMERA", "android.permission.CALL_PHONE",
                                        "android.permission.READ_CONTACTS", "android.permission.READ_PHONE_STATE", "android.permission.WRITE_CONTACTS", "android.permission.RECORD_AUDIO",
                                        "android.permission.READ_EXTERNAL_STORAGE",
                                        "android.permission.WRITE_EXTERNAL_STORAGE"}, 10);
                                db.putBoolean("getPermission", false);
                            }
                        }).show();
                    } else {
                        callMain();
                    }
                }
            }, 300);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void callMain() {
        if (x == 0) {
            Intent intent = new Intent(this, Main.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        callMain();
    }

    public void onBackPressed() {
        x++;
        finish();
    }

    private void setPushe() {

    }


}
