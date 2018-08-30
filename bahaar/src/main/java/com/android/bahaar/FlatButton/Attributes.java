package com.android.bahaar.FlatButton;


import android.content.res.Resources;
import android.graphics.Color;

import com.android.bahaar.R;


/**
 * This class holds the values of the common attributes.
 */
public class Attributes {

    public static int DEFAULT_THEME = R.array.demo;
    public static final int DEFAULT_TOUCH_EFFECT = 0;

    public static final int RIPPLE_TOUCH_EFFECT = 2;



    public static int DEFAULT_RADIUS_PX = 8;
    public static int DEFAULT_BORDER_WIDTH_PX = 0;


    /**
     * Color related fields
     */
    private int[] colors;
    private int theme = -1;
    private int touchEffect = DEFAULT_TOUCH_EFFECT;


    /**
     * Size related fields
     */
    private int radius = DEFAULT_RADIUS_PX;
    private int borderWidth = DEFAULT_BORDER_WIDTH_PX;

    /**
     * Attribute change listener. Used to redraw the view when attributes are changed.
     */
    private AttributeChangeListener attributeChangeListener;

    public Attributes(AttributeChangeListener attributeChangeListener, Resources resources) {
        this.attributeChangeListener = attributeChangeListener;
        setThemeSilent(DEFAULT_THEME, resources);
    }

    public int getTheme() {
        return theme;
    }

    public void setThemeSilent(int theme, Resources resources) {
        try {
            this.theme = theme;
            colors = resources.getIntArray(theme);
        } catch (Resources.NotFoundException e) {

            // setting theme blood if exception occurs (especially used for preview rendering by IDE)
            colors = new int[]{Color.parseColor("#077c9c"), Color.parseColor("#077c9c"),
                    Color.parseColor("#86d21c"), Color.parseColor("#86d21c")};
        }
    }

    public int getColor(int colorPos) {
        return colors[colorPos];
    }


    public int getRadius() {
        return radius;
    }

    public float[] getOuterRadius() {
        return new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }



    public int getTouchEffect() {
        return touchEffect;
    }

    public void setTouchEffect(int touchEffect) {
        this.touchEffect = touchEffect;
    }

    public boolean hasTouchEffect() {
        return this.touchEffect != Attributes.DEFAULT_TOUCH_EFFECT;
    }

    public interface AttributeChangeListener {
        void onThemeChange();
    }

}