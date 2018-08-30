package ghasemi.abbas.abzaar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.android.bahaar.TinyDB;
import com.android.bahaar.ToastActivity;
import com.pushpole.sdk.PushPole;

import java.util.Date;

import ghasemi.abbas.abzaar.db.Bmi;
import ghasemi.abbas.abzaar.db.BuyCharge;
import ghasemi.abbas.abzaar.db.Contact;
import ghasemi.abbas.abzaar.db.Countries;
import ghasemi.abbas.abzaar.db.DHA;
import ghasemi.abbas.abzaar.db.DownloaderInsta;
import ghasemi.abbas.abzaar.db.Flasher;
import ghasemi.abbas.abzaar.db.IranCity;
import ghasemi.abbas.abzaar.db.Magnetic;
import ghasemi.abbas.abzaar.db.MapsView;
import ghasemi.abbas.abzaar.db.MathBase;
import ghasemi.abbas.abzaar.db.More;
import ghasemi.abbas.abzaar.db.SalavatShomaar;
import ghasemi.abbas.abzaar.db.Shortlink;
import ghasemi.abbas.abzaar.db.Trafic;
import ghasemi.abbas.abzaar.db.Transfer;
import ghasemi.abbas.abzaar.db.NewTracking;
import ghasemi.abbas.abzaar.db.app.info.AppInfo;
import ghasemi.abbas.abzaar.db.barcode.BarcodeExampleActivity;
import ghasemi.abbas.abzaar.db.barcode.BarcodeScanner;
import ghasemi.abbas.abzaar.db.calendar.Convert;
import ghasemi.abbas.abzaar.db.camera.CameraZoom;
import ghasemi.abbas.abzaar.db.color.CameraColorActivity;
import ghasemi.abbas.abzaar.db.compass.CompassActivity;
import ghasemi.abbas.abzaar.db.fall.FalHafezActivity;
import ghasemi.abbas.abzaar.db.foucault.Foucault;
import ghasemi.abbas.abzaar.db.level.Level;
import ghasemi.abbas.abzaar.db.malii.MainActivityM;
import ghasemi.abbas.abzaar.db.mp3cutter.Activities.RingdroidSelectActivity;
import ghasemi.abbas.abzaar.db.my.phone.AboutPhone;
import ghasemi.abbas.abzaar.db.note.Notes;
import ghasemi.abbas.abzaar.db.sargarmi.MainActivityS;
import ghasemi.abbas.abzaar.db.soundmeter.Soundmeter;
import ghasemi.abbas.abzaar.db.speedmeter.SpeedMeter;
import ghasemi.abbas.abzaar.util.IabHelper;
import ghasemi.abbas.abzaar.util.IabResult;
import ghasemi.abbas.abzaar.util.Purchase;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.BuildVars.getAppUrl;
import static ghasemi.abbas.abzaar.BuildVars.getKeyMarket;
import static ghasemi.abbas.abzaar.BuildVars.getNameMarket;
import static ghasemi.abbas.abzaar.BuildVars.getPackageMarket;
import static ghasemi.abbas.abzaar.BuildVars.isPackageInstalled;

/**
 * on 04/19/2018.
 */

public class Main extends AppCompatActivity {
    public static final long TWO_SECOND = 2 * 1000;
    ////////////
    static final int RC_REQUEST = 1372;
    public static Ringtone playRingtone;
    public static MediaPlayer soot;
    @SuppressLint("StaticFieldLeak")
    public static Activity activity;
    static String[] array_main_txt;
    private static int[] array_main_pic;
    TinyDB db;
    RecyclerView list_main;
    long preTime;
    ////////////
    IabHelper mHelper;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                ToastActivity.Toast(Main.this, "خطا در انجام پرداخت", Toast.LENGTH_SHORT);
            } else /*if (purchase.getSku().equals(BuildVars.SKU_MARKET))*/ {
                ToastActivity.Toast(Main.this, "از حمایت شما سپاس گذاریم", Toast.LENGTH_SHORT);
                mHelper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {
                    @Override
                    public void onConsumeFinished(Purchase purchase, IabResult result) {

                    }
                });
            }
        }
    };
    View view;

    public static boolean CheckNetworkStatus(Activity a) {
        ConnectivityManager connectivityManager = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void HelpAlert(Context c, CharSequence message) {
        AlertDialog show = new AlertDialog.Builder(c, R.style.Theme_Dialog_Alert)
                .setTitle("راهنمائی")
                .setMessage(message)
                .setPositiveButton("باشه", null)
                .setCancelable(false)
                .create();
        show.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
        show.show();
    }

    public static void Shortcut(int position, String id) {
        if (BuildVars.ADAD_MARKET == 3 && position >= 9) {
            position--;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Intent shortcutIntent = new Intent(activity, Start.class);
            shortcutIntent.putExtra("shortcut", id);
            shortcutIntent.setAction(Intent.ACTION_MAIN);
            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, array_main_txt[position]);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(activity, array_main_pic[position]));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            addIntent.putExtra("duplicate", false);
            activity.sendBroadcast(addIntent);
        } else {
            try {
                Class cl = Class.forName(id);
                ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(activity, "" + position)
                        .setIntent(new Intent(activity, cl).setAction(Intent.ACTION_MAIN))
                        .setShortLabel(array_main_txt[position])
                        .setIcon(IconCompat.createWithResource(activity, array_main_pic[position]))
                        .build();
                ShortcutManagerCompat.requestPinShortcut(activity, shortcutInfo, null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void getMyClass(int position) {
        if (BuildVars.ADAD_MARKET == 3 && position >= 9) {
            position++;
        }
        Class c = null;
        Intent intent = null;
        switch (position) {
            case 0:
                c = AboutPhone.class;
                break;
            case 1:
                c = Notes.class;
                intent = new Intent(activity, Notes.class);
                intent.putExtra("title", "یادداشت ها");
                intent.putExtra("isFav", false);
                break;
            case 2:
                c = MainActivityM.class;
                break;
            case 3:
                c = Convert.class;
                break;
            case 4:
                c = AppInfo.class;
                break;
            case 5:
                c = Transfer.class;
                break;
            case 6:
                c = DownloaderInsta.class;
                break;
            case 7:
                c = Bmi.class;
                break;
            case 8:
                c = MathBase.class;
                break;
            case 9:
                c = BuyCharge.class;
                break;
            case 10:
                c = Foucault.class;
                break;
            case 11:
                c = RingdroidSelectActivity.class;
                break;
            case 12:
                c = FalHafezActivity.class;
                break;
            case 13:
                c = Shortlink.class;
                break;
            case 14:
                c = Level.class;
                break;
            case 15:
                c = DHA.class;
                break;
            case 16:
                c = More.class;
                break;
            case 17:
                c = MainActivityS.class;
                break;
            case 18:
                c = Flasher.class;
                break;
            case 19:
                c = CompassActivity.class;
                break;
            case 20:
                c = Contact.class;
                break;
            case 21:
                c = BarcodeExampleActivity.class;
                break;
            case 22:
                intent = new Intent(activity, MapsView.class);
                intent.putExtra("NAME_ACTIVITY", array_main_txt[position]);
                intent.putExtra("PATCH", "https://www.dl.dropboxusercontent.com/s/ae502qkb54x389y/METRO.jpg");
                break;
            case 23:
                intent = new Intent(activity, MapsView.class);
                intent.putExtra("NAME_ACTIVITY", array_main_txt[position]);
                intent.putExtra("PATCH", "https://www.dl.dropboxusercontent.com/s/n6xystkzlxk2w5i/BRT.jpg");
                break;
            case 24:
                c = CameraColorActivity.class;
                break;
            case 25:
                intent = new Intent(activity, CameraZoom.class);
                intent.putExtra("NAME_ACTIVITY", array_main_txt[position]);
                intent.putExtra("IS_CAMERA_FRONT", false);
                break;
            case 26:
                intent = new Intent(activity, CameraZoom.class);
                intent.putExtra("NAME_ACTIVITY", array_main_txt[position]);
                intent.putExtra("IS_CAMERA_FRONT", true);
                break;
            case 27:
                c = Trafic.class;
                break;
            case 28:
                c = Magnetic.class;
                break;
            case 29:
                c = Soundmeter.class;
                break;
            case 30:
                c = SpeedMeter.class;
                break;
            case 31:
                c = BarcodeScanner.class;
                break;
            case 32:
                c = SalavatShomaar.class;
                break;
            case 33:
                c = IranCity.class;
                break;
            case 34:
                c = Countries.class;
                break;
            case 35:
                c = NewTracking.class;
                break;
        }
        if (intent == null) {
            intent = new Intent(activity, c);
        }
        intent.putExtra("position", position);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ParsCustomContent.initialize(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushPole.initialize(this,false);
        activity = this;
        if (BuildVars.ADAD_MARKET == 3) {
            array_main_txt = new String[]{
                    "اطلاعات دستگاه", "یادداشت ها", "امور مالی", "تبدیل تاریخ", "مدیریت برنامه ها", "دیکشنری",
                    "دانلودر اینستا", "شاخص توده بدنی", "مبدل واحد", "فضول گیر", "برش آهنگ", "فال حافظ", "شورت لینک",
                    "تراز", "دفع حشرات",
                    "تنظیمات", "سرگرمی", "چراغ قوه", "قطب نما", "مخفی سازی مخاطبین", "بارکد ساز", "نقشه مترو",
                    "نقشه BRT", "رنگ شناسی", "ذره بین", "آینه", "ترافیک راه ها",
                    "فلزیاب", "صدا سنج", "سرعت سنج", "بارکد خوان", "ذکر شمار", "پیش شماره شهرها", "پیش شماره کشورها",
                    "استعلام مرسولات پستی"
            };
            array_main_pic = new int[]{
                    R.drawable.info_phone, R.drawable.note, R.drawable.malii, R.drawable.date,
                    R.drawable.share, R.drawable.tarnsfer, R.drawable.dl_insta, R.drawable.bmi, R.drawable.adad, R.drawable.fozolgir,
                    R.drawable.mp3_cutter, R.drawable.fall, R.drawable.shortlink, R.drawable.level, R.drawable.dafe_h, R.drawable.more,
                    R.drawable.sargarni, R.drawable.flasher, R.drawable.compass,
                    R.drawable.contact, R.drawable.barcode_sconer, R.drawable.metro, R.drawable.brt, R.drawable.color, R.drawable.zarebin,
                    R.drawable.ayne, R.drawable.trafic, R.drawable.metal, R.drawable.sound,
                    R.drawable.speed, R.drawable.barcode_reader, R.drawable.salavat, R.drawable.ic_telephone, R.drawable.ic_world,
                    R.drawable.ic_post_office
            };
        }else {
            array_main_txt = new String[]{
                    "اطلاعات دستگاه", "یادداشت ها", "امور مالی", "تبدیل تاریخ", "مدیریت برنامه ها", "دیکشنری",
                    "دانلودر اینستا", "شاخص توده بدنی", "مبدل واحد", "کافه شارژ", "فضول گیر", "برش آهنگ", "فال حافظ", "شورت لینک",
                    "تراز", "دفع حشرات",
                    "تنظیمات", "سرگرمی", "چراغ قوه", "قطب نما", "مخفی سازی مخاطبین", "بارکد ساز", "نقشه مترو",
                    "نقشه BRT", "رنگ شناسی", "ذره بین", "آینه", "ترافیک راه ها",
                    "فلزیاب", "صدا سنج", "سرعت سنج", "بارکد خوان", "ذکر شمار", "پیش شماره شهرها", "پیش شماره کشورها",
                    "استعلام مرسولات پستی"
            };
            array_main_pic = new int[]{
                    R.drawable.info_phone, R.drawable.note, R.drawable.malii, R.drawable.date,
                    R.drawable.share, R.drawable.tarnsfer, R.drawable.dl_insta, R.drawable.bmi, R.drawable.adad, R.drawable.charge, R.drawable.fozolgir,
                    R.drawable.mp3_cutter, R.drawable.fall, R.drawable.shortlink, R.drawable.level, R.drawable.dafe_h, R.drawable.more,
                    R.drawable.sargarni, R.drawable.flasher, R.drawable.compass,
                    R.drawable.contact, R.drawable.barcode_sconer, R.drawable.metro, R.drawable.brt, R.drawable.color, R.drawable.zarebin,
                    R.drawable.ayne, R.drawable.trafic, R.drawable.metal, R.drawable.sound,
                    R.drawable.speed, R.drawable.barcode_reader, R.drawable.salavat, R.drawable.ic_telephone, R.drawable.ic_world,
                    R.drawable.ic_post_office
            };
        }
        db = new TinyDB(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        list_main = findViewById(R.id.list_main);
        list_main.hasFixedSize();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int x = (int) (displayMetrics.widthPixels / displayMetrics.density / 120);
        list_main.setLayoutManager(new GridLayoutManager(Main.this, x));
        list_main.setAdapter(new Adapter_Main(Main.this));
        ToolBarIni();
        setPay();

//        String CHANGE_LIST_APP_ABZAAR = "change_list_app_abzaar_pluss";
//        if (db.getInt(CHANGE_LIST_APP_ABZAAR, 0) == 0) {
//            getChange();
//            db.putInt(CHANGE_LIST_APP_ABZAAR, 1);
//        }

        RelativeLayout relative = findViewById(R.id.relative);
        ImageView imageView = new ImageView(this);
        relative.addView(imageView);
        imageView.setImageResource(R.drawable.abzaar);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    private void Donate() {
        if (BuildVars.ADAD_MARKET == 0 || BuildVars.ADAD_MARKET == 3) {
            BuildVars.getRate(this, BuildVars.getIntent());
            return;
        }

        try {
//            List<Info> list = new ApkInfoExtractor(Main.this).GetAllInstalledApkInfo();
            if (!Main.CheckNetworkStatus(Main.this)) {
                ToastActivity.Toast(Main.this, "دستگاه شما به اینترنت متصل نیست.", Toast.LENGTH_SHORT);
                return;
            } else if (!isPackageInstalled(getPackageMarket(), Main.this)) {
                ToastActivity.Toast(Main.this, "مارکت" + getNameMarket() + "بر روی دستگاه شما نصب نیست!", Toast.LENGTH_SHORT);
                return;
            }
// else if (isPackageLucky(list)) {
//                ToastActivity.Toast(Main.this, "وجود برنامه های مخرب در دستگاه شما شناسایی شد!", Toast.LENGTH_SHORT);
//                return;
//            }
            View view = getLayoutInflater().inflate(R.layout.dialog_sheet_donate, null);
            final BottomSheetDialog dialog = new BottomSheetDialog(this);
            dialog.setContentView(view);
            dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(new ColorDrawable(Color.TRANSPARENT));
            view.findViewById(R.id.one_donate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    StartPay("abzaar1");
                }
            });
            view.findViewById(R.id.two_donate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    StartPay("abzaar2");
                }
            });
            view.findViewById(R.id.three_donate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    StartPay("abzaar3");
                }
            });
            dialog.show();
        } catch (Exception e) {
            Log.e(BuildVars.TAG, "isPackageLucky");
            ToastActivity.Toast(Main.this, "خطا ، لطفا مجددا تلاش نمائید", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_about:
                AlertDialog show = new AlertDialog.Builder(this, R.style.Theme_Dialog_Alert)
                        .setTitle("درباره ما")
                        .setMessage(Html.fromHtml("برنامه نویس: عباس قاسمی<br>ایمیل(نقد و نظرات سازنده شما):<br><a href=\"mailto:abbasghasemi729@gmail.com\">abbasghasemi@mail.com</a><br><br>سال تولید اردیبهشت (V:1.0):<br>2019 Tools, All Right Reserved.<br><br> توجه: \n ابزار رایگان میباشد و با پیشرفت خود رایگان باقی می ماند،برای تولید برنامه وقت زیادی صرف شده است با اشتراک لینک دانلود برنامه یا نظرات پرستاره ، علاوه بر دل گرمی ، ما را در بهبود و تقویت برنامه های رایگان یاری رسانید.<br>"))
                        .setPositiveButton("باشه", null)
                        .create();
                show.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
                show.show();
                break;
            case R.id.item_change:
                getChange();
                break;
            case R.id.item_donate:
                Donate();
                break;
            case R.id.item_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, getAppUrl());
                intent.setType("text/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "حمایت با ارسال برنامه برای دوستان"));
                break;
            case R.id.item_other_apps:
                BuildVars.getAllApp(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getChange() {
        AlertDialog show = new AlertDialog.Builder(this, R.style.Theme_Dialog_Alert)
                .setTitle("تغییرات نسخه 1.0")
                .setMessage("- بازطراحی برنامه" +
                        "\n" +
                        "- رفع اشکالات برنامه" +
                        "\n" +
                        "*- ترافیک راه ها (جدبد)" + "\n" +
                        "*- فلزیاب  (جدبد)" + "\n" +
                        "*- صداسنج (جدبد)" + "\n" +
                        "*- سرعت (جدبد)" + "\n" +
                        "*- تبدیل تاریخ (جدبد)" + "\n" +
                        "*- کمیسیون املاک (جدبد)" + "\n" +
                        "*- درصد (جدبد)" +
                        "\n" +
                        "\n\n" +
                        "تغییرات نسخه 0.52-Beta" +
                        "\n" +
                        "رفع مشکلات اندروید 9 به بالا \n" +
                        "\n\n" +
                        "تغییرات نسخه 0.50-Beta" +
                        "\n" +
                        "- بهبود عملکرد برنامه\n " +
                        "" +
                        "- امکان پشتیبانی گیری از دفترچه یادداشت\n " +
                        "- بهبود بخش فلاشر\n " +
                        "- تعمیر بخش شورت لینک\n " +
                        "- بهبود و ارتقا بخش فضولگیر\n " +
                        "\n" +
                        "تغییرات نسخه 0.40-Beta" +
                        "\n" +
                        "- بهبود عملکرد برنامه\n " +
                        "- اضافه شدن دفترچه یادداشت\n " +
                        "- رفع مشکلات نسخه پیشین و اعمال تغییرات جزئی\n " +
                        "\n" +
                        "* اضافه شدن 6 بخش جدید به برنامه (قطب نما،فلاش سلفی،مخفی سازی مخاطبین،آینه،ذره بین،رنگ شناسی،بارکد ساز،نقشه مترو و BRT) \n" +
                        "- رفع مشکل در قسمت پشتیبانی گرفتن از برنامه ها و امکان مدیریت برنامه های پشتیبانی گرفته شده در برنامه\n" +
                        "- رفع مشکل در قسمت اطلاعات تلفن \n" +
                        "- بهبود بخش شکستن صفحه در قسمت سرگرمی\n" +
                        "- رفع مشکلات و تغییرات جزئی در برنامه\n\n" +
                        "تغییرات نسخه 0.25-Beta" +
                        "\n" +
                        "- بهبود عملکرد بخش اطلاعات دستگاه\n " +
                        "- امکان کپی کردن شناسه خوانده شده بارکد\n" +
                        "- بهبود عملکرد مدیریت برنامه و امکان پشتیبانی گیری از اپ ها\n" +
                        "- رفع خطا در قسمت دیشکنری + امکان تبدیل گفتار به نوشتار\n" +
                        "- رفع باگ در مبدل واحد و برش آهنگ با بهبود عملکرد آنها\n\n" +
                        "*- بخش جدید فال نامه حضرت حافظ\n" +
                        "*- بخش جدید شورت لینک\n" +
                        "*- بخش جدید مالی\n" +
                        "*- بخش جدید دفع حشرات\n" +
                        "*- بخش جدید سرگرمی(شکستن صفحه و تصویر زمینه)\n" +
                        "*- بخش جدید تنظیمات با امکانات کاملا کاربردی\n" +
                        "*- باز شدن بخش جدید آنتی ویروس")
                .setPositiveButton("باشه", null)
                .setCancelable(false)
                .create();
        show.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
        show.show();
    }

    public void onBackPressed() {
        long currentTime = new Date().getTime();
        if ((currentTime - preTime) > TWO_SECOND) {
            ToastActivity.Toast(this, "برای خروج مجددا سعی نمایید", Toast.LENGTH_SHORT);
            preTime = currentTime;
        } else {
            finish();
        }
    }

    private void ToolBarIni() {
    }

    ////////////////// PAY MARKET ///////////////////////////////
    private void StartPay(String key) {
        if (BuildVars.ADAD_MARKET == 0 || BuildVars.ADAD_MARKET == 3) {
            return;
        }
        try {
            mHelper.launchPurchaseFlow(Main.this, key, RC_REQUEST, mPurchaseFinishedListener, "payload-string");
        } catch (Exception e) {
            ToastActivity.Toast(Main.this, "لطفا یکبار مارکت مورد نظر را باز و بسته کرده و سپس مجددا برنامه را اجرا و تلاش نمایید.", Toast.LENGTH_LONG);
        }
    }

    private void setPay() {
        if (BuildVars.ADAD_MARKET == 0 || BuildVars.ADAD_MARKET == 3) {
            return;
        }
        try {
            mHelper = new IabHelper(this, getKeyMarket());
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        ToastActivity.Toast(Main.this, "خطا در انجام پرداخت", Toast.LENGTH_SHORT);
                    }
                }
            });
        } catch (Exception e) {
            //
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mHelper.handleActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        try {
            if (mHelper != null) mHelper.dispose();
            mHelper = null;
        } catch (Exception e) {
            //
        }
        super.onDestroy();
    }

    class Adapter_Main extends RecyclerView.Adapter<Adapter_Main.contentViewHolder> {
        Context context;

        Adapter_Main(Context c) {
            context = c;
        }

        @Override
        public contentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main, parent, false);

            return new contentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(contentViewHolder holder, final int position) {
            holder.row_main_txt.setText(array_main_txt[position]);
            Glide.with(Main.this)
                    .load(array_main_pic[position])
//                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.row_main_img);
            holder.row_main_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getMyClass(position);
                }
            });


            if ((position == 24 && TinyDB.getInstance(getApplicationContext()).getInt("version_" + CameraColorActivity.class.getCanonicalName(), 0) == 0) ||
                    (position == 2 && TinyDB.getInstance(getApplicationContext()).getInt("version_" + MainActivityM.class.getCanonicalName(), 0) == 0) ||
                    position == 17 && TinyDB.getInstance(getApplicationContext()).getInt("version_" + MainActivityS.class.getCanonicalName(), 0) == 0) {
                holder.row_main_type.setImageResource(R.drawable.edit_item);
            } else if ((position == 34 && TinyDB.getInstance(getApplicationContext()).getInt("version_" + Countries.class.getCanonicalName(), 0) == 0) ||
                    (position == 35 && TinyDB.getInstance(getApplicationContext()).getInt("version_" + NewTracking.class.getCanonicalName(), 0) == 0) ||
                    (position == 33 && TinyDB.getInstance(getApplicationContext()).getInt("version_" + IranCity.class.getCanonicalName(), 0) == 0)) {
                holder.row_main_type.setImageResource(R.drawable.new_item);
            } else {
                holder.row_main_type.setImageDrawable(null);
            }
        }


        @Override
        public int getItemCount() {
            return array_main_txt.length;
        }

        public class contentViewHolder extends RecyclerView.ViewHolder {

            CardView row_main_card;
            ImageView row_main_img, row_main_type;
            TextView row_main_txt;

            contentViewHolder(View itemView) {
                super(itemView);

                row_main_card = itemView.findViewById(R.id.row_main_card);
                row_main_img = itemView.findViewById(R.id.row_main_img);
                row_main_txt = itemView.findViewById(R.id.row_main_txt);
                row_main_type = itemView.findViewById(R.id.row_main_type);

            }
        }

    }


}



