package ghasemi.abbas.abzaar.db.app.info;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import androidx.core.content.ContextCompat;

import ghasemi.abbas.abzaar.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * on 04/22/2018.
 */

public class ApkInfoExtractor {
    Context context1;

    public ApkInfoExtractor(Context context2) {
        context1 = context2;
    }

    public List<Info> GetAllInstalledApkInfoSeystem() {
        int i=0;
        List<Info> ApkPackageName = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        List<ResolveInfo> resolveInfoList = context1.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            String idApp = activityInfo.applicationInfo.packageName;
            Info app = getAllInfo(idApp,resolveInfo);
            Info add = new Info();
            add.setIdApp(idApp);
            add.setAppName(app.getAppName());
            add.setAppPath(app.getAppPath());
            add.setAppVersion(app.getAppVersion());
            add.setAppIcon(app.getAppIcon());
            add.setIsSystemPackage(app.getIsSystemPackage());
            if (app.getIsSystemPackage()){
                ApkPackageName.add(add);
                try {
                    if(ApkPackageName.get(i).getIdApp().equals(ApkPackageName.get(i-1).getIdApp())){
                        ApkPackageName.remove(i);
                        i = i-1;
                    }
                }catch (Exception e){

                }
                i++;
            }
        }
            return ApkPackageName;
    }

    public List<Info> GetAllInstalledApkInfo() {
        List<Info> ApkPackageName = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        List<ResolveInfo> resolveInfoList = context1.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            String idApp = activityInfo.applicationInfo.packageName;
            Info app = getAllInfo(idApp,resolveInfo);
            Info add = new Info();
            add.setIdApp(idApp);
            add.setAppName(app.getAppName());
            add.setAppPath(app.getAppPath());
            add.setAppVersion(app.getAppVersion());
            add.setAppIcon(app.getAppIcon());
            add.setIsSystemPackage(app.getIsSystemPackage());
            if (!app.getIsSystemPackage()){
                ApkPackageName.add(add);
            }
        }
        return ApkPackageName;
    }

    public String[] GetAllRunningApkInfo() {
        ActivityManager am = (ActivityManager) context1.getSystemService(context1.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        Iterator<ActivityManager.RunningAppProcessInfo> iterator = list.iterator();
        String AllRunningApk[] = null;
        ActivityManager.RunningAppProcessInfo runproInfo = iterator.next();
        AllRunningApk = runproInfo.pkgList;
        return AllRunningApk;
    }

    public Info getAllInfo(String ApkPackageName,ResolveInfo resolveInfo) {
        List<Info> all = new ArrayList<>();
        Info add = new Info();
        PackageInfo packageInfo;
        ApplicationInfo applicationInfo;
        PackageManager packageManager = context1.getPackageManager();
        try {
            packageInfo = packageManager.getPackageInfo(ApkPackageName,0);
            applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0);
            if (applicationInfo != null) {
                add.setAppName((String) packageManager.getApplicationLabel(applicationInfo));
                add.setAppPath(applicationInfo.sourceDir);
                add.setAppVersion(packageInfo.versionName);
                add.setAppIcon(context1.getPackageManager().getApplicationIcon(ApkPackageName));
                add.setIsSystemPackage((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            }
        } catch (PackageManager.NameNotFoundException e) {
            add.setAppIcon(ContextCompat.getDrawable(context1, R.mipmap.ic_launcher));
            e.printStackTrace();
        }
        all.add(add);
        return all.get(0);
    }

}