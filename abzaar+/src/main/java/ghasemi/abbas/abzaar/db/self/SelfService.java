package ghasemi.abbas.abzaar.db.self;

/**
 * on 07/09/2018.
 */

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.android.bahaar.TinyDB;


public class SelfService extends Service {
    String ACTION_STRING_SERVICE = "ToServiceSelf";

    TinyDB db;
    private boolean isFilterView;
    public static boolean isSelectOpenSFF;

    private CircleView filterView;
    private WindowManager windowManager;

    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("self service", "onReceive");
            createFilter();
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
        Log.d("self service", "onCreate");
        if (serviceReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_SERVICE);
            registerReceiver(serviceReceiver, intentFilter);
        }
        filterView = new CircleView(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("self service", "onStartCommand");
        createFilter();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service", "onDestroy");
        unregisterReceiver(serviceReceiver);
        if (filterView != null) {
            windowManager.removeView(filterView);
        }
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    private void createFilter() {

        int border = db.getInt("newSelfCircleBorder",200);
        int Site = db.getInt("newSelfCircleSite",2);
        int Size = db.getInt("newSelfCircleSize",2);

        filterView.setCircleBorder(border);
        filterView.setCircleSite(Size);
        filterView.setCircleSize(Site);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );
        if (isFilterView)
            windowManager.updateViewLayout(filterView, layoutParams);
        else {
            windowManager.addView(filterView, layoutParams);
            isFilterView = true;
        }
    }


}