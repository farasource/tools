package com.android.bahaar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * on 04/14/2018.
 */

public class ToastActivity {
    public static void Toast(Context c, String txt,int i) {
        Toast toast;
        toast = Toast.makeText(c, txt, i);
        TextView txt_toast = toast.getView().findViewById(android.R.id.message);
        txt_toast.setTextColor(Color.WHITE);
        View V = toast.getView();
        V.setBackgroundResource(R.drawable.bk_toast);
        GradientDrawable bgShape;
        bgShape = (GradientDrawable) V.getBackground();
        bgShape.setColor(Color.parseColor("#dc130123"));
        toast.show();
    }
}
