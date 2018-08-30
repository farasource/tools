package ghasemi.abbas.abzaar.db.app.info;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.DialogTitle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ghasemi.abbas.abzaar.BuildConfig;
import ghasemi.abbas.abzaar.BuildVars;
import ghasemi.abbas.abzaar.Main;
import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.TabLayout;

import com.android.bahaar.RoundedImg.RoundedImageView;
import com.android.bahaar.ToastActivity;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;



import static ghasemi.abbas.abzaar.Main.Shortcut;
import static ghasemi.abbas.abzaar.db.app.info.AppInfo.AppInfoA.adapter_a;
import static ghasemi.abbas.abzaar.db.app.info.AppInfo.AppInfoB.adapter_b;


/**
 * on 04/22/2018.
 */

public class AppInfo extends AppCompatActivity {
    static List<Info> ais;
    static List<Info> backup;
    static List<Info> ai;
    private static ActivityManager acm = null;
    String[] TITLES = {"برنامه های کاربری", "برنامه های سیستمی"};
    BackupAdapter backupAdapter;
    TextView main_name;
    ViewPager pager;
    AlertDialog show;
    TabLayout smartTabLayout;
    private AVLoadingIndicatorView progress;


    private static void OpenApp(Context c, String pkg) {
        Intent intent = c.getPackageManager().getLaunchIntentForPackage(pkg);
        if (intent != null) {
            c.startActivity(intent);
        } else {
            ToastActivity.Toast(c, pkg + " Error, Please Try Again.", Toast.LENGTH_SHORT);
        }
    }

    private static void DeleteApp(Context c, String pkg) {
        Uri packageURI = Uri.parse("package:" + pkg);
        Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageURI);
        c.startActivity(uninstallIntent);
    }

    private static void DeleteApp(Context c, List<Info> all) {
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getIsSelected()) {
                Uri packageURI = Uri.parse("package:" + all.get(i).getIdApp());
                Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageURI);
                c.startActivity(uninstallIntent);
            }
        }
    }


    private static void ShareApp2(Context c, List<Info> all) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        StringBuilder te = new StringBuilder();
        int i = 1;
        for (Info info : all) {
            if (info.getIsSelected())
                te.append((i++)).append("- ").append(BuildVars.getMarketUri()).append(info.getIdApp()).append(" (").append(info.getAppName()).append(")\n");
        }
        if (te.toString().isEmpty()) {
            return;
        }
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, te.toString());
        c.startActivity(Intent.createChooser(intent, "برنامه ای را جهت ارسال انتخاب نمائید"));
    }

    private static void ShareApp(Context c, List<Info> all, int e) {
        Intent intent = new Intent();
        if (BuildVars.ADAD_MARKET == 1 && e == 0) {

        } else {
            try {
                int i = 0;
                ArrayList<Parcelable> arrayList = new ArrayList<>();
                while (i < all.size()) {
                    if (all.get(i).getIsSelected()) {
                        arrayList.add(FileProvider.getUriForFile(c, c.getPackageName() + ".provider", new File(all.get(i).getAppPath())));
                    }
                    i++;
                }
                if (arrayList.isEmpty()) return;
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.setType("*/*");
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayList);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                c.startActivity(Intent.createChooser(intent, "برنامه ای را جهت ارسال انتخاب نمائید"));
            } catch (Exception e1) {
                int i = 0;
                ArrayList<Parcelable> arrayList = new ArrayList<>();
                while (i < all.size()) {
                    if (all.get(i).getIsSelected()) {
                        arrayList.add(Uri.fromFile(new File(all.get(i).getAppPath())));
                    }
                    i++;
                }
                if (arrayList.isEmpty()) return;
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.setType("*/*");
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayList);
                c.startActivity(Intent.createChooser(intent, "برنامه ای را جهت ارسال انتخاب نمائید"));
            }
        }
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
        RelativeLayout relative = findViewById(R.id.relative);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.w_p), (int) getResources().getDimension(R.dimen.h_p));
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        progress = new AVLoadingIndicatorView(this);
        progress.setIndicatorColor(Color.BLACK);
        relative.addView(progress, layoutParams);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        main_name = findViewById(R.id.main_name);
        main_name.setText("مدیریت برنامه ها");
        ai = new ArrayList<>();
        ais = new ArrayList<>();
        pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);
        smartTabLayout = findViewById(R.id.smartTabLayout);
        pager.setAdapter(new AdapterPageMain(getSupportFragmentManager()));
        smartTabLayout.setupWithViewPager(pager);
        backup = new ArrayList<>();
        new ApkTask(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_page, menu);
        menu.findItem(R.id.item_help).setVisible(false);
        menu.findItem(R.id.item_app_backup).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_mbar:
                Shortcut(getIntent().getIntExtra("position",0), AppInfo.class.getCanonicalName());
                break;
            case R.id.item_app_backup:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ToastActivity.Toast(this,"دسترسی به حافظه به برنامه داده نشده است.",0);
                    break;
                }
                backup.clear();
                show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void show() {
        backup = BuildVars.loadBackupAPK(this);
        RecyclerView mRecyclerView = new RecyclerView(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        backupAdapter = new BackupAdapter();
        mRecyclerView.setAdapter(backupAdapter);
        show = new AlertDialog.Builder(this, R.style.Theme_Dialog_Alert)
                .setView(mRecyclerView)
                .create();
        show.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
        if (backup.isEmpty()) {
            ToastActivity.Toast(this, "لیست خالیست.", 1);
            return;
        }
        show.show();
    }

    public static class AppInfoA extends Fragment {
        static AppsAdapter adapter_a;
        View v;
        RecyclerView list;
        LinearLayout LinearLayoutAll;
        TextView delete, share, backup;
        private Context context;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            v = inflater.inflate(R.layout.list_phone, null);
            context = v.getContext();
            adapter_a = new AppsAdapter();
            list = v.findViewById(R.id.list_main);
            list.setLayoutManager(new LinearLayoutManager(Main.activity, LinearLayoutManager.VERTICAL, false));
            list.setAdapter(adapter_a);

            LinearLayoutAll = v.findViewById(R.id.all);
            LinearLayoutAll.setVisibility(View.VISIBLE);
            delete = v.findViewById(R.id.delete);
            share = v.findViewById(R.id.share);
            backup = v.findViewById(R.id.backup);
            return v;
        }

        class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

            @Override
            public AppsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view2 = LayoutInflater.from(context).inflate(R.layout.row_app, parent, false);
                return new ViewHolder(view2);
            }

            @Override
            public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
                viewHolder.itemView.setPadding(0, 0, 0, position == ai.size() - 1 ? (int) getResources().getDimension(R.dimen.bottom_app) : 0);
                final Info allList = ai.get(position);
                final String ApplicationPackageName = allList.getIdApp();
                final String ApplicationLabelName = allList.getAppName();
                Drawable drawable = allList.getAppIcon();
                final String ApplicationLabelPath = allList.getAppPath();
                final String AppVersion = allList.getAppVersion();

                viewHolder.appName.setText(ApplicationLabelName);
                viewHolder.packageName.setText(ApplicationPackageName);
                viewHolder.imageView.setImageDrawable(drawable);
                viewHolder.appSize.setText(Formatter.formatFileSize(context, new File(ApplicationLabelPath).length()) + "--V:" + AppVersion);
                viewHolder.row_app_card.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        allList.setIsSelected(!allList.getIsSelected());
                        viewHolder.Sends.setChecked(allList.getIsSelected());
                        return true;
                    }
                });
                viewHolder.row_app_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (allList.getIsSelected()) {
                            allList.setIsSelected(false);
                            viewHolder.Sends.setChecked(false);
                            return;
                        }
                        if (ApplicationPackageName.equals(BuildConfig.APPLICATION_ID)) {
                            allList.setIsSelected(true);
                            viewHolder.Sends.setChecked(true);
                            return;
                        }
                        View inflate = getLayoutInflater().inflate(R.layout.dialog_sheet_apps, null);
                        TextView title = inflate.findViewById(R.id.title);
                        title.setText(viewHolder.appName.getText());
                        RoundedImageView imageView = inflate.findViewById(R.id.icon);
                        imageView.setImageDrawable(viewHolder.imageView.getDrawable());
                        final BottomSheetDialog dialog = new BottomSheetDialog(context);
                        dialog.setContentView(inflate);
                        dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(new ColorDrawable(Color.TRANSPARENT));
                        inflate.findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                OpenApp(context, ApplicationPackageName);
                            }
                        });

                        inflate.findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                acm.killBackgroundProcesses(ApplicationPackageName);
                                ToastActivity.Toast(context, "انجام شد.", Toast.LENGTH_SHORT);
                            }
                        });
                        inflate.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                DeleteApp(context, ApplicationPackageName);
                            }
                        });
                        inflate.findViewById(R.id.select).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                allList.setIsSelected(true);
                                viewHolder.Sends.setChecked(true);
                            }
                        });
                        dialog.show();
                    }
                });
                viewHolder.Sends.setOnCheckedChangeListener(null);
                viewHolder.Sends.setChecked(allList.getIsSelected());
                viewHolder.Sends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        allList.setIsSelected(isChecked);
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeleteApp(context, ai);
                    }
                });
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShareApp2(context, ai);
                    }
                });
                backup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<Info> p = new ArrayList<>();
                        for (int i = 0; i < ai.size(); i++) {
                            if (ai.get(i).getIsSelected()) {
                                Info all = new Info();
                                all.setAppPath(ai.get(i).getAppPath());
                                all.setAppVersion(ai.get(i).getAppVersion());
                                all.setAppName(ai.get(i).getAppName());
                                p.add(all);
                            }
                        }
                        if (!p.isEmpty()) new FileSaveTask(context, p).execute();
                    }
                });
            }

            @Override
            public int getItemCount() {
                return ai.size();
            }

            public class ViewHolder extends RecyclerView.ViewHolder {
                CheckBox Sends;
                ImageView imageView;
                TextView appName, packageName, appSize;
                CardView row_app_card;

                public ViewHolder(View view) {
                    super(view);

                    row_app_card = view.findViewById(R.id.row_app_card);
                    appSize = view.findViewById(R.id.row_appSize);
                    imageView = view.findViewById(R.id.row_app_img);
                    appName = view.findViewById(R.id.row_appName);
                    packageName = view.findViewById(R.id.row_packageName);
                    Sends = view.findViewById(R.id.checkBoxSends);

                }
            }

        }

    }

    public static class AppInfoB extends Fragment {
        static AppsAdapter adapter_b;
        View v;
        RecyclerView list;
        LinearLayout LinearLayout, LinearLayoutAll;
        TextView delete, share, backup;
        private Context context;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            v = inflater.inflate(R.layout.list_phone, null);
            context = v.getContext();
            adapter_b = new AppsAdapter();
            list = v.findViewById(R.id.list_main);
            list.setLayoutManager(new LinearLayoutManager(Main.activity, LinearLayoutManager.VERTICAL, false));
            list.hasFixedSize();
            list.setAdapter(adapter_b);
            LinearLayoutAll = v.findViewById(R.id.all);
            LinearLayoutAll.setVisibility(View.VISIBLE);
            delete = v.findViewById(R.id.delete);
            share = v.findViewById(R.id.share);
            backup = v.findViewById(R.id.backup);
            return v;
        }

        class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

            @Override
            public AppsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view2 = LayoutInflater.from(context).inflate(R.layout.row_app, parent, false);
                return new ViewHolder(view2);
            }

            @Override
            public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
                viewHolder.itemView.setPadding(0, 0, 0, position == ais.size() - 1 ? (int) getResources().getDimension(R.dimen.bottom_app) : 0);
                final Info allList = ais.get(position);
                final String ApplicationPackageName = allList.getIdApp();
                final String ApplicationLabelName = allList.getAppName();
                Drawable drawable = allList.getAppIcon();
                final String ApplicationLabelPath = allList.getAppPath();
                final String AppVersion = allList.getAppVersion();
                viewHolder.appName.setText(ApplicationLabelName);
                viewHolder.packageName.setText(ApplicationPackageName);
                viewHolder.imageView.setImageDrawable(drawable);
                viewHolder.appSize.setText(Formatter.formatFileSize(context, new File(ApplicationLabelPath).length()) + "--V:" + AppVersion);
                viewHolder.row_app_card.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        allList.setIsSelected(!allList.getIsSelected());
                        viewHolder.Sends.setChecked(allList.getIsSelected());
                        return true;
                    }
                });
                viewHolder.row_app_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (allList.getIsSelected()) {
                            allList.setIsSelected(false);
                            viewHolder.Sends.setChecked(false);
                            return;
                        }
                        View inflate = getLayoutInflater().inflate(R.layout.dialog_sheet_apps, null);
                        TextView title = inflate.findViewById(R.id.title);
                        title.setText(viewHolder.appName.getText());
                        RoundedImageView imageView = inflate.findViewById(R.id.icon);
                        imageView.setImageDrawable(viewHolder.imageView.getDrawable());
                        final BottomSheetDialog dialog = new BottomSheetDialog(context);
                        dialog.setContentView(inflate);
                        dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(new ColorDrawable(Color.TRANSPARENT));
                        inflate.findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                OpenApp(context, ApplicationPackageName);
                            }
                        });
                        inflate.findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                acm.killBackgroundProcesses(ApplicationPackageName);
                                ToastActivity.Toast(context, "انجام شد.", Toast.LENGTH_SHORT);
                            }
                        });
                        inflate.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                DeleteApp(context, ApplicationPackageName);
                            }
                        });
                        inflate.findViewById(R.id.select).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                allList.setIsSelected(true);
                                viewHolder.Sends.setChecked(true);
                            }
                        });
                        dialog.show();
                    }
                });
                viewHolder.Sends.setOnCheckedChangeListener(null);
                viewHolder.Sends.setChecked(allList.getIsSelected());
                viewHolder.Sends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        allList.setIsSelected(isChecked);
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeleteApp(context, ais);
                    }
                });
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShareApp2(context, ais);
                    }
                });
                backup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<Info> p = new ArrayList<>();
                        for (int i = 0; i < ais.size(); i++) {
                            if (ais.get(i).getIsSelected()) {
                                Info all = new Info();
                                all.setAppPath(ais.get(i).getAppPath());
                                all.setAppVersion(ais.get(i).getAppVersion());
                                all.setAppName(ais.get(i).getAppName());
                                p.add(all);
                            }
                        }
                        if (!p.isEmpty()) new FileSaveTask(context, p).execute();
                    }
                });
            }

            @Override
            public int getItemCount() {
                return ais.size();
            }

            public class ViewHolder extends RecyclerView.ViewHolder {
                CheckBox Sends;
                ImageView imageView;
                TextView appName, packageName, appSize;
                CardView row_app_card;

                public ViewHolder(View view) {
                    super(view);

                    row_app_card = view.findViewById(R.id.row_app_card);
                    appSize = view.findViewById(R.id.row_appSize);
                    imageView = view.findViewById(R.id.row_app_img);
                    appName = view.findViewById(R.id.row_appName);
                    packageName = view.findViewById(R.id.row_packageName);
                    Sends = view.findViewById(R.id.checkBoxSends);
                }
            }

        }

    }

    public static class FileSaveTask extends AsyncTask<Void, Integer, File> {

        private ProgressDialog progress;
        private Context c;
        private List<Info> selected_app;

        FileSaveTask(Context context, List<Info> selected_app) {
            this.selected_app = selected_app;
            this.c = context;
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(c, R.style.Theme_Dialog_Alert);
            progress.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
            progress.setMessage("درحال ذخیره سازی ...");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setIndeterminate(false);
            progress.setProgress(0);
            progress.setSecondaryProgress(1);
            progress.setMax(selected_app.size());
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected File doInBackground(Void... params) {
            int i = 0;
            File outputFile = null;
            while (selected_app.size() > i) {
                String filename = selected_app.get(i).getAppName() + "__" + selected_app.get(i).getAppVersion() + ".apk";
                outputFile = new File(Environment.getExternalStorageDirectory() + File.separator + "/abz/Apps/");
                if (!outputFile.exists()) {
                    outputFile.mkdirs();
                }
                File apk = new File(outputFile.getPath() + "/" + filename);
                try {
                    apk.createNewFile();
                    InputStream in = new FileInputStream(selected_app.get(i).getAppPath());
                    OutputStream out = new FileOutputStream(apk);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                publishProgress(i);
                i++;
            }
            return outputFile;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress.setSecondaryProgress(values[0] + 1);
            progress.setProgress(values[0]);
            progress.setMessage("درحال ذخیره سازی " + selected_app.get(values[0]).getAppName());
        }

        @Override
        protected void onPostExecute(File result) {
            if (progress != null) {
                progress.dismiss();
            }
            if (result != null) {
                AlertDialog alert = new AlertDialog.Builder(c, R.style.Theme_Dialog_Alert)
                        .setCancelable(false)
                        .setTitle(R.string.app_name)
                        .setMessage("بکاپ با موفقیت انجام شد.")
                        .setPositiveButton("ممنون", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dg, int arg1) {
                                dg.dismiss();
                            }
                        })
                        .create();
                alert.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
                alert.show();
            } else {
                Toast.makeText(c, "App backup failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AdapterPageMain extends FragmentPagerAdapter {

        AdapterPageMain(FragmentManager fm) {
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
                return new AppInfoA();
            } else {
                return new AppInfoB();
            }

        }
    }

    private class ApkTask extends AsyncTask<Void, Void, Boolean> {

        private Context c;

        ApkTask(Context context) {
            this.c = context;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean is = false;
            try {
                ais = new ApkInfoExtractor(c).GetAllInstalledApkInfoSeystem();
                ai = new ApkInfoExtractor(c).GetAllInstalledApkInfo();
                is = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return is;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progress != null) {
                progress.hide();
            }
            if (result) {
                adapter_a.notifyDataSetChanged();
                adapter_b.notifyDataSetChanged();
            } else {
                onBackPressed();
                Toast.makeText(c, "App failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    class BackupAdapter extends RecyclerView.Adapter<BackupAdapter.ViewHolder> {

        @Override
        public BackupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view2 = LayoutInflater.from(AppInfo.this).inflate(R.layout.row_app, parent, false);
            return new ViewHolder(view2);
        }

        @Override
        public void onBindViewHolder(final BackupAdapter.ViewHolder holder, final int position) {
            Info c = backup.get(position);

            holder.appSize.setText(Formatter.formatFileSize(AppInfo.this, new File(c.getAppPath()).length()));
            holder.imageView.setImageDrawable(c.getAppIcon());
            holder.appName.setText(c.getAppName());
            holder.packageName.setText(c.getAppVersion());
        }

        @Override
        public int getItemCount() {
            return backup.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            CheckBox Sends;
            ImageView imageView;
            TextView appName, packageName, appSize;
            CardView row_app_card;

            public ViewHolder(View view) {
                super(view);

                row_app_card = view.findViewById(R.id.row_app_card);

                appSize = view.findViewById(R.id.row_appSize);
                imageView = view.findViewById(R.id.row_app_img);
                appName = view.findViewById(R.id.row_appName);
                packageName = view.findViewById(R.id.row_packageName);

                Sends = view.findViewById(R.id.checkBoxSends);
                row_app_card.setOnClickListener(this);
                Sends.setVisibility(View.GONE);
            }

            @Override
            public void onClick(View v) {

                final AlertDialog s = new AlertDialog.Builder(AppInfo.this, R.style.Theme_Dialog_Alert)
                        .setTitle(appName.getText())
                        .setIcon(imageView.getDrawable())
                        .setNegativeButton("نصب", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                                    intent.setType("application/vnd.android.package-archive");
                                    intent.setData(FileProvider.getUriForFile(AppInfo.this, getApplicationContext().getPackageName() + ".provider", new File(backup.get(getAdapterPosition()).getAppPath())));
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                                    intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                                    intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, getApplicationInfo().packageName);
                                    startActivity(intent);
                                } catch (Exception e) {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.fromFile(new File(backup.get(getAdapterPosition()).getAppPath())), "application/vnd.android.package-archive");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } catch (Exception e2) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        })
                        .setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File f = new File(backup.get(getAdapterPosition()).getAppPath());
                                f.delete();
                                backup.remove(getAdapterPosition());
                                backupAdapter.notifyDataSetChanged();

                                if (backup.isEmpty()) {
                                    show.dismiss();
                                }
                            }
                        })
                        .create();
                s.getWindow().getAttributes().windowAnimations = R.style.dialog_anim;
                s.show();
                DialogTitle title = s.findViewById(R.id.alertTitle);
                if (title != null) {
                    title.setGravity(Gravity.LEFT);
                }

            }
        }

    }

}