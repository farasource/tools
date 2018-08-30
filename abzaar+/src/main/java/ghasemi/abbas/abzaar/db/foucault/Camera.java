package ghasemi.abbas.abzaar.db.foucault;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Camera extends AppCompatActivity {
    FrontCamera frontCamera;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout relativeLayout = new FrameLayout(this);
        SurfaceView surfaceView = new SurfaceView(this);
        surfaceView.setLayoutParams(new LinearLayout.LayoutParams(2, 2));
        relativeLayout.addView(surfaceView);

        TextView textView = new TextView(this);
        textView.setText("درحال ذخیره تصاویر ، منتظر باشید ...");
        textView.setGravity(Gravity.CENTER);
        relativeLayout.addView(textView, new FrameLayout.LayoutParams(-1, -1));
        setContentView(relativeLayout);
        frontCamera = new FrontCamera(this, surfaceView);
    }

    @Override
    protected void onDestroy() {
        if (frontCamera != null) {
            frontCamera.stopCamera();
        }
        super.onDestroy();
    }
}
