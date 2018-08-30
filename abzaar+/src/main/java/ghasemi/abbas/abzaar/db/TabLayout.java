package ghasemi.abbas.abzaar.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;

import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

public class TabLayout extends com.google.android.material.tabs.TabLayout {
    public TabLayout(Context context) {
        super(context);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final ViewGroup tabStrip = (ViewGroup) getChildAt(0);
        final int tabCount = tabStrip.getChildCount();
        ViewGroup tabView;
        int tabChildCount;
        View tabViewChild;
        @SuppressLint("DrawAllocation") TypedValue value = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, value, true);
        } else {
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, value, true);
        }
        for (int i = 0; i < tabCount; i++) {
            tabView = (ViewGroup) tabStrip.getChildAt(i);
//            tabView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            tabView.setBackgroundResource(value.resourceId);
            tabChildCount = tabView.getChildCount();
            for (int j = 0; j < tabChildCount; j++) {
                tabViewChild = tabView.getChildAt(j);
                if (tabViewChild instanceof AppCompatTextView) {
                    ((AppCompatTextView) tabViewChild).setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/sans.ttf"));
                }
            }
        }
    }
}
