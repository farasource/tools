package ghasemi.abbas.abzaar.db.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

import ghasemi.abbas.abzaar.BuildVars;
import ghasemi.abbas.abzaar.R;
import ghasemi.abbas.abzaar.db.More;
import com.android.bahaar.TinyDB;


/**
 * Created by georgy on 08.09.2017.
 * gkgio
 */

public class NightService extends Service {
    String NIGHT_SERVICE = "night service";
    String ACTION_STRING_SERVICE = "ToService";
    String NEW_BRIGHTNESS_PREFERENCE = "newBrightness";
    public static boolean FilterLight = false;
    int NOTIFICATION_ID = 157;
    TinyDB db;
    private boolean isFilterView;

    private LinearLayout filterView;
    private WindowManager windowManager;

    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(NIGHT_SERVICE, "onReceive");
            int newSeekBarProgress =  db.getInt(NEW_BRIGHTNESS_PREFERENCE,50);
            createFilter(newSeekBarProgress);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new TinyDB(getApplicationContext());
        Log.d(NIGHT_SERVICE, "onCreate");
        // register the receiver
        if (serviceReceiver != null) {
            //Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_SERVICE"
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_SERVICE);
            //Map the intent filter to the receiver
            registerReceiver(serviceReceiver, intentFilter);
        }
        filterView = new LinearLayout(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(NIGHT_SERVICE, "onStartCommand");
        int oldBrightnessLevel =  db.getInt(NEW_BRIGHTNESS_PREFERENCE,50);
        createFilter(oldBrightnessLevel);
        createNotification();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service", "onDestroy");
        // Unregister the receiver
        unregisterReceiver(serviceReceiver);

        if (filterView != null) {
            windowManager.removeView(filterView);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll(); //clear notification when service stops
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    private void createFilter(int oldBrightnessLevel) {

        filterView.setBackgroundColor(getColorForFilter(oldBrightnessLevel));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );
        /*new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);*/

        if (isFilterView)
            windowManager.updateViewLayout(filterView, layoutParams);
        else {
            windowManager.addView(filterView, layoutParams);
            isFilterView = true;
        }
    }

    private int getColorForFilter(int alpha) {
        int red, green, blue;
        red = 0x00;
        green = 0x00;
        blue = 0x00;
        String hex = String.format("%02x%02x%02x%02x", -alpha + 200, red, green, blue);
        int color = (int) Long.parseLong(hex, 16);
        return color;
    }

    private void createNotification() {
        Intent notificationIntent = new Intent(this, More.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BuildVars.createChannel(this,"in_app_service","فعالیت های درون برنامه ای"));
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.more)
                .setContentTitle("کاهش نور صفحه فعال است")
                .setContentText("لمس برای باز کردن")
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}