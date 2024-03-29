package com.android.bahaar.FlatButton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.android.bahaar.R;


/**
 * User: eluleci
 * Date: 23.10.2013
 * Time: 22:18
 */
public class FlatButton extends androidx.appcompat.widget.AppCompatButton implements Attributes.AttributeChangeListener {

    private Attributes attributes;

    //implementation
    private OnClickListener listener = null;

    public OnClickListener getListener() {
        return listener;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void setOnClickListener(OnClickListener listener){
        super.setOnClickListener(listener);
        setListener(listener);
    }
    //

    // default values of specific attributes
    private int bottom = 0;

    private TouchEffectAnimator touchEffectAnimator;

    public FlatButton(Context context) {
        super(context);
        init(null);
    }

    public FlatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FlatButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (attributes.hasTouchEffect() && touchEffectAnimator != null)
            touchEffectAnimator.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (attributes.hasTouchEffect() && touchEffectAnimator != null)
            touchEffectAnimator.onDraw(canvas);
        super.onDraw(canvas);
    }

    private void init(AttributeSet attrs) {

        // saving padding values for using them after setting background drawable
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingLeft = getPaddingLeft();
        final int paddingBottom = getPaddingBottom();

        if (attributes == null)
            attributes = new Attributes(this, getResources());

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.fl_FlatButton);

            // getting common attributes
            int customTheme = a.getResourceId(R.styleable.fl_FlatButton_fl_theme, Attributes.DEFAULT_THEME);
            attributes.setThemeSilent(customTheme, getResources());
            attributes.setTouchEffect(a.getInt(R.styleable.fl_FlatButton_fl_touchEffect, Attributes.DEFAULT_TOUCH_EFFECT));
            attributes.setRadius(a.getDimensionPixelSize(R.styleable.fl_FlatButton_fl_cornerRadius, Attributes.DEFAULT_RADIUS_PX));

            // getting view specific attributes
            bottom = a.getDimensionPixelSize(R.styleable.fl_FlatButton_fl_blockButtonEffectHeight, bottom);

            a.recycle();
        }

        if (attributes.hasTouchEffect()) {
            boolean hasRippleEffect = attributes.getTouchEffect() == Attributes.RIPPLE_TOUCH_EFFECT;
            touchEffectAnimator = new TouchEffectAnimator(this);
            touchEffectAnimator.setHasRippleEffect(hasRippleEffect);
            touchEffectAnimator.setEffectColor(attributes.getColor(1));
            touchEffectAnimator.setClipRadius(attributes.getRadius());
        }

        // creating normal state drawable
        ShapeDrawable normalFront = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        normalFront.getPaint().setColor(attributes.getColor(2));

        ShapeDrawable normalBack = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        normalBack.getPaint().setColor(attributes.getColor(1));

        normalBack.setPadding(0, 0, 0, bottom);

        Drawable[] d = {normalBack, normalFront};
        LayerDrawable normal = new LayerDrawable(d);

        // creating pressed state drawable
        ShapeDrawable pressedFront = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        pressedFront.getPaint().setColor(attributes.getColor(1));

        ShapeDrawable pressedBack = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        pressedBack.getPaint().setColor(attributes.getColor(0));
        if (bottom != 0) pressedBack.setPadding(0, 0, 0, bottom / 2);

        Drawable[] d2 = {pressedBack, pressedFront};
        LayerDrawable pressed = new LayerDrawable(d2);

        // creating disabled state drawable
        ShapeDrawable disabledFront = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        disabledFront.getPaint().setColor(attributes.getColor(3));
        disabledFront.getPaint().setAlpha(0xA0);

        ShapeDrawable disabledBack = new ShapeDrawable(new RoundRectShape(attributes.getOuterRadius(), null, null));
        disabledBack.getPaint().setColor(attributes.getColor(2));

        Drawable[] d3 = {disabledBack, disabledFront};
        LayerDrawable disabled = new LayerDrawable(d3);

        StateListDrawable states = new StateListDrawable();

        if (!attributes.hasTouchEffect())
            states.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        states.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, pressed);
        states.addState(new int[]{android.R.attr.state_enabled}, normal);
        states.addState(new int[]{-android.R.attr.state_enabled}, disabled);

        setBackgroundDrawable(states);
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        // check for IDE preview render

    }


    @Override
    public void onThemeChange() {
        init(null);
    }
}