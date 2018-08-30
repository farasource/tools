package ghasemi.abbas.abzaar.db.app.info;

import android.graphics.drawable.Drawable;

/**
 * on 06/11/2018.
 */

public class Info {

    public static int isCheck=0;
    private Boolean isSystemPackage;
    private Boolean isSelected=false;
    private String appName;
    private String appId;
    private String appVersion;
    private String appPath;
    private Drawable appIcon;

    public Boolean getIsSystemPackage() {
        return isSystemPackage;
    }

    public void setIsSystemPackage(Boolean is) {
        isSystemPackage = is;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean is) {
        isSelected = is;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String name) {
        appName = name;
    }

    public String getIdApp() {
        return appId;
    }

    public void setIdApp(String name) {
        appId = name;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String name) {
        appVersion = name;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String name) {
        appPath = name;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable name) {
        appIcon = name;
    }
}
