package ghasemi.abbas.abzaar.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import ghasemi.abbas.abzaar.R;

public class TextInputLayout extends com.google.android.material.textfield.TextInputLayout {
    public TextInputLayout(Context context) {
        this(context, null);
    }

    public TextInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint({"RestrictedApi", "PrivateResource"})
    public TextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextInputLayout, defStyleAttr, 0);
        if(a.hasValue(R.styleable.TextInputLayout_boxStrokeColor)){
            setDefaultHintTextColor(a.getColorStateList(R.styleable.TextInputLayout_boxStrokeColor));
        }
        a.recycle();
    }
}
