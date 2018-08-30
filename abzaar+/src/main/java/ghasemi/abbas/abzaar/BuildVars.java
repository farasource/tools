package ghasemi.abbas.abzaar;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

import com.android.bahaar.ToastActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ghasemi.abbas.abzaar.db.app.info.Info;
import ghasemi.abbas.abzaar.support.customtabs.CustomTabsIntent;

/**
 * on 05/02/2018.
 */

public class BuildVars {

    public static final String TAG = "AbzaarPlus";
    public static int ADAD_MARKET = 1;

    static String getKeyMarket() {
        if (ADAD_MARKET == 1)
            return "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDREZTYyLNjELf2b5RxWJd0JSBPs8PxcnrutZXbX4CaeMcOoO6bekuiNZa3PDoJOEkeqLsiXyyCZ9xiom2XmNM7YzMCJ0Hh3bWpoF5HIVdl7Rzvwmk+TevHGKJJRVYhvw+r1FESV9Hj+ySHSOO8BYkJmMyAUNrMoexU6PT7gQ30dHuL13hL94oIOXWzBYP7CtIuJfCFCtkMBsKXAP6OVBEKSEMqlX2171gOqhqqDssCAwEAAQ==";
        if (ADAD_MARKET == 2)
            return "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoh76RIZwlvoAKY+r6PP8zDlanp33iZQO3dAimgWxpUzcpgMajCuselAWpeuhF8T/f8m0uRvJCkAsjLt4NewY1EQxpM2d13MfBwqdOBqszNiywOmrUGErGt+AeovcxC7NXS5ULnLVeIa/OSe/DSVsoYyhKzty7/fDsdLACLWLWmwIDAQAB";
        if (ADAD_MARKET == 3)
            return "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3TUNSWspnJN1z5aMNJXW45qH7bXbkHhiamNkna3Q2JrvTdpGZnPBqjfDEBuxOdf3SUJLUJEfzmeArje6DO0izAKIVfhIhwbPkXH+9Sbd/ZqGMYLc/0q5stta5ilV7lpwa5rSY0czpjOknsBisiBuNHYnlQzdvYy2X8jnHO8NjPQIDAQAB";
        return "";
    }

    public static String getIntentMarket() {
        if (ADAD_MARKET == 1) return "ir.cafebazaar.pardakht";
        if (ADAD_MARKET == 2) return "ir.mservices.market";
        if (ADAD_MARKET == 3) return "ir.tgbs.iranapps.billing";
        return "";
    }

    public static String getPackageMarket() {
        if (ADAD_MARKET == 1) return "com.farsitel.bazaar";
        if (ADAD_MARKET == 2) return "ir.mservices.market";
        if (ADAD_MARKET == 3) return "ir.tgbs.android.iranapp";
        return "com.android.vending";
    }

    public static String getNameMarket() {
        if (ADAD_MARKET == 1) return " کافه بازار ";
        if (ADAD_MARKET == 2) return " مایکت ";
        if (ADAD_MARKET == 3) return " ایران اپس ";
        return "";
    }

    public static String getAppUrl() {
        if (ADAD_MARKET == 1)
            return "ابزار ، برنامه ای جامع و پر کاربرد برای شما\n\nلینک دانلود رایگان:\n" + "https://cafebazaar.ir/app/ghasemi.abbas.abzaar";
        if (ADAD_MARKET == 2)
            return "ابزار ، برنامه ای جامع و پر کاربرد برای شما\n\nلینک دانلود رایگان:\n" + "https://myket.ir/app/ghasemi.abbas.abzaar";
        if (ADAD_MARKET == 3)
            return "ابزار ، برنامه ای جامع و پر کاربرد برای شما\n\nلینک دانلود رایگان:\n" + "http://iranapps.ir/app/ghasemi.abbas.abzaar";
        return "ابزار ، برنامه ای جامع و پر کاربرد برای شما\n\nلینک دانلود رایگان:\n" + "https://play.google.com/store/apps/details?id=ghasemi.abbas.abzaar";
    }

    public static boolean isPackageInstalled(String pack, Activity activity) {

        PackageManager pm = activity.getPackageManager();
        try {
            pm.getPackageInfo(pack, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isPackageLucky(List<Info> list) {
        List<Info> stringList = list;
        for (Info is : stringList) {
            if (is.getIdApp().startsWith("com.android.vending.billing.InAppBillingService")) {
                return true;
            } else if (is.getAppName().startsWith("لاکی پ")) {
                return true;
            } else if (is.getAppName().startsWith("Lucky Pa")) {
                return true;
            }
        }
        return false;
    }

    public static String getIntent() {
        if (ADAD_MARKET == 1) {
            return Intent.ACTION_EDIT;
        }
        return Intent.ACTION_VIEW;
    }

    public static String getUrlMarketDetails() {
        if (ADAD_MARKET == 0) {
            return "https://play.google.com/store/apps/details?id=ghasemi.abbas.abzaar";
        }
        if (ADAD_MARKET == 1) {
            return "bazaar://details?id=ghasemi.abbas.abzaar";
        }
        if (ADAD_MARKET == 2) {
            return "myket://comment?id=ghasemi.abbas.abzaar";
        }
        return "iranapps://app/ghasemi.abbas.abzaar?a=ابزار&r=5";
    }

    public static String getAllAppLink() {
        if (ADAD_MARKET == 0) {
            return "https://play.google.com/store/apps/details?id=ghasemi.abbas.abzaar";
        }
        if (ADAD_MARKET == 1) {
            return "bazaar://collection?slug=by_author&aid=abbasghasemi";
        }
        if (ADAD_MARKET == 2) {
            return "myket://developer/ghasemi.abbas.abzaar";
        }
        return "iranapps://user/abbasghasemi";
    }

    public static void getRate(Context context, String s) {
        try {
            Intent intent = new Intent(s);
            intent.setData(Uri.parse(getUrlMarketDetails()));
            intent.setPackage(getPackageMarket());
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastActivity.Toast(context, "به نظر میرسه شما" + getNameMarket() + "را نصب نداری!", Toast.LENGTH_SHORT);
        }
    }

    public static void getAllApp(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getAllAppLink()));
            intent.setPackage(getPackageMarket());
            context.startActivity(intent);
        } catch (Exception e) {
            ToastActivity.Toast(context, "به نظر میرسه شما" + getNameMarket() + "را نصب نداری!", Toast.LENGTH_SHORT);
        }
    }

    public static void setViewSite(Activity c, String url) {
        Uri uri = Uri.parse(url);
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(ContextCompat.getColor(c, R.color.white));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(c, R.color.white));
        intentBuilder.setStartAnimations(c, android.R.anim.slide_out_right, android.R.anim.slide_in_left);
        intentBuilder.setExitAnimations(c, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(c, uri);
    }

    public static String ReadFromFile(String fileName, Context context) {
        StringBuilder ReturnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                ReturnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return ReturnString.toString();
    }

    public static void BackupInfoToSD(String info, String name) {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            String path = Environment.getExternalStorageDirectory() + "/abz/.DataAbz/" + name + ".abz";
            new File(path).mkdirs();
            try {
                File f = new File(path);
                f.delete();
                f.createNewFile();
                BufferedWriter bwrite = new BufferedWriter(new FileWriter(f));
                bwrite.write(info);
                bwrite.flush();
                bwrite.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String ReadFromFileInSD(String name) {
        StringBuilder ReturnString = new StringBuilder();
        String path = Environment.getExternalStorageDirectory() + "/abz/.DataAbz/" + name + ".abz";
        BufferedReader input = null;
        try {
            File fIn = new File(path);
            BufferedReader read = new BufferedReader(new FileReader(fIn));
            input = new BufferedReader(read);
            String line = "";
            while ((line = input.readLine()) != null) {
                ReturnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return ReturnString.toString();
    }

    public static List<Info> loadBackupAPK(Context ctx) {
        List<Info> appList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory() + "/abz/Apps/";
        File root = new File(path);
        if (root.exists() && root.isDirectory()) {
            for (File f : root.listFiles()) {
                if (f.length() > 0 && f.getPath().endsWith(".apk")) {
                    String filePath = f.getPath();
                    String[] part = f.getPath().split("/");
                    String[] part2 = part[part.length - 1].split("__");
                    PackageInfo pk = ctx.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
                    if (pk != null && part2.length == 2) {
                        ApplicationInfo info = pk.applicationInfo;
                        info.sourceDir = filePath;
                        info.publicSourceDir = filePath;
                        Drawable icon = info.loadIcon(ctx.getPackageManager());
                        Info app = new Info();
                        app.setAppIcon(icon);
                        app.setAppPath(filePath);
                        app.setAppName(part2[0]);
                        app.setAppVersion(part2[1].replace(".apk", " (V) "));
                        appList.add(app);
                    }
                }
            }
        }
        return appList;
    }

    public static String createChannel(Context context, String id, String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            mChannel.setImportance(NotificationManager.IMPORTANCE_LOW);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
            return id;
        }
        return "";
    }

    public static String getMarketUri() {
        if (ADAD_MARKET == 1)
            return "https://cafebazaar.ir/app/";
        if (ADAD_MARKET == 2)
            return "https://myket.ir/app/";
        if (ADAD_MARKET == 3)
            return "https://iranapps.ir/app/";
        return "https://play.google.com/store/apps/details?id=";
    }
}
