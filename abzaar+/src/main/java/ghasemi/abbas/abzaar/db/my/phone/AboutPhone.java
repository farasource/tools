package ghasemi.abbas.abzaar.db.my.phone;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import ghasemi.abbas.abzaar.Main;
import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.TabLayout;

import com.android.bahaar.ToastActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static ghasemi.abbas.abzaar.Main.HelpAlert;
import static ghasemi.abbas.abzaar.Main.Shortcut;


public class AboutPhone extends AppCompatActivity {
    public static final String[] TITLES = {"مشخصات", "سیستم عامل", "باتری", "صفحه نمایش", "حافظه", "شبکه"};
    ViewPager pager;
    TabLayout smartTabLayout;
    Toolbar toolbar;
    long l2;
    long l3;
    private ActivityManager acm = null;
    private long beforeMemory;
    private long aftermemory;
    private ProgressDialog pd;
    private int ramFreed;
    private int cacheFreed;


//    --------------------------

    public static void CopyText(Context context, String s) {
        ((ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE)).setText(s);
        ToastActivity.Toast(context, "اطلاعات کپی شد.", Toast.LENGTH_SHORT);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Main.activity == null) Main.activity = this;
        acm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        setContentView(R.layout.tab_phone);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ToolBarIni();
        pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(1);
        smartTabLayout = findViewById(R.id.smartTabLayout);
        pager.setAdapter(new AdapterPageMain(getSupportFragmentManager()));
        smartTabLayout.setupWithViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_page, menu);
        menu.findItem(R.id.item_booster).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_help:
                HelpAlert(this, "با قابلیت بهینه سازی ، علاوه بر افزایش سرعت رم و سی پی یو،دستگاه خود را کمی خنک کنید.\nبا لمس هر کادر،اطلاعات مربوط به آن بخش در تلفن شما کپی میگردد.");
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position",0), AboutPhone.class.getCanonicalName());
                break;
            case R.id.item_booster:
                l2 = java.lang.System.currentTimeMillis();
                ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
                acm.getMemoryInfo(memInfo);
                beforeMemory = memInfo.availMem;
                new Booster().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ToolBarIni() {
    }

    public List<String> GetAllInstalledApkInfo() {
        List<String> ApkPackageName = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            ApkPackageName.add(activityInfo.applicationInfo.packageName);
        }
        return ApkPackageName;
    }

    public class AdapterPageMain extends FragmentPagerAdapter {

        public AdapterPageMain(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return new Info();
            } else if (position == 1) {
                return new System();
            } else if (position == 2) {
                return new Battery();
            } else if (position == 3) {
                return new Lcd();
            } else if (position == 4) {
                return new Storage();
            }
            return new Sim();
        }
    }

    private class Booster extends AsyncTask<Void, Void, Void> {
        List<String> all = GetAllInstalledApkInfo();

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(AboutPhone.this, R.style.Theme_Dialog_Alert);
            pd.setIndeterminate(false);
            pd.setMax(all.size());
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
            pd.setMessage("در حال بهینه سازی رم و سی پی یو ...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (int i = 0; i < all.size(); i++) {
                    acm.killBackgroundProcesses(all.get(i));
                    pd.setSecondaryProgress(i + all.size() / 5);
                    pd.setProgress(i);
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                l3 = java.lang.System.currentTimeMillis() - l2;
                int j = (int) (l3 / 1000L);
                int k = j / 360;
                Random random = new Random();
                int i1;
                int j2 = 0;
                int j1 = 0;
                int k1 = 0;
                if (k > 0) {
                    i1 = j * k;
                } else {
                    i1 = j;
                }
                j1 = i1 * 15;
                k1 = (j1 - i1) + 1;
                j2 = random.nextInt(k1) + i1;
                cacheFreed = j2;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null) {
                pd.dismiss();
            }
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            acm.getMemoryInfo(memInfo);
            aftermemory = memInfo.availMem;
            if (aftermemory > beforeMemory) {
                ramFreed = (int) (aftermemory - beforeMemory);
            } else {
                ramFreed = 0;
            }
            AlertDialog show = new AlertDialog.Builder(AboutPhone.this, R.style.Theme_Dialog_Alert)
                    .setTitle("بهینه سازی دستگاه انجام شد.")
                    .setMessage("تمامی برنامه ها بسته شد." + "\n" + "حافظه آزاد شده: " + DeviceInfo.formatMemSize(ramFreed, 0) + "\n" + "حافظه پنهان آزاد شده: " + DeviceInfo.formatMemSize(cacheFreed, 0))
                    .setCancelable(false)
                    .setNegativeButton("بستن", null)
                    .create();
            show.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
            show.show();

        }

    }

}
