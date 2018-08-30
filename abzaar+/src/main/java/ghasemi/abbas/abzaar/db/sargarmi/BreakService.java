package ghasemi.abbas.abzaar.db.sargarmi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ghasemi.abbas.abzaar.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * on 07/28/2018.
 */

public class BreakService extends Service implements View.OnTouchListener {
    WindowManager.LayoutParams params;
    BreakView breakView;
    int type;
    ArrayList<Float> mXPointList;
    ArrayList<Float> mYPointList;
    MediaPlayer soot;
    ImageView filterView;
    WindowManager windowManager;
    Bitmap mBitmap;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("self service", "onCreate");
        breakView = new BreakView(this);
        filterView = new ImageView(this);
        filterView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.screen);
        soot = MediaPlayer.create(getApplicationContext(), R.raw.crack_sound);
        soot.setLooping(false);
        soot.setVolume(100, 100);
        filterView.setOnTouchListener(this);
        mXPointList = new ArrayList<>();
        mYPointList = new ArrayList<>();

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
        if (filterView != null)
            try {
                windowManager.removeView(filterView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        if (breakView != null)
            try {
                windowManager.removeView(breakView);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    private void createFilter() {
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT
        );
        windowManager.addView(filterView, params);
        Random random = new Random();
        type = random.nextInt(2);
    }

    @Override
    public boolean onTouch(View view, MotionEvent arg1) {

        switch (arg1.getAction()) {
            case MotionEvent.ACTION_DOWN:
                windowManager.removeView(filterView);
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        PixelFormat.TRANSLUCENT
                );
                mXPointList.add(arg1.getX());
                mYPointList.add(arg1.getY());
                if (type == 0) {
                    soot.start();
                    windowManager.addView(breakView, params);
                } else {
                    Random random = new Random();
                    if (random.nextInt(2) == 0)
                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.screen_1);
                    else
                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.screen_2);
                    filterView.setImageBitmap(mBitmap);
                    filterView.setScaleType(ImageView.ScaleType.FIT_XY);
                    soot.start();
                    windowManager.addView(filterView, params);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                break;
        }
        return true;
    }


    class BreakView extends View {
        public BreakView(Context context) {
            super(context);
            invalidate();
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (int i = 0; i < mXPointList.size(); ++i) {
                canvas.drawBitmap(mBitmap, mXPointList.get(i) - mBitmap.getWidth() / 2, mYPointList.get(i) - mBitmap.getHeight() / 2, null);
            }
        }
    }
}