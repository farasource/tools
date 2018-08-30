package ghasemi.abbas.abzaar.db.self;

/**
 * on 07/09/2018.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View
{
    private static final int DEFAULT_CIRCLE_BORDER = 50;
    private static final int DEFAULT_CIRCLE_SIZE = 2;
    private static final int DEFAULT_CIRCLE_SITE = 2;

    private int circleSite = DEFAULT_CIRCLE_SITE;
    private int circleSize = DEFAULT_CIRCLE_SIZE;
    private int circleBorder = DEFAULT_CIRCLE_BORDER;
    private Paint paint;

    public CircleView(Context context)
    {
        super(context);
        init(context, null);
        setWillNotDraw(false);
    }

    public CircleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
        setWillNotDraw(false);
    }

    private void init(Context context, AttributeSet attrs)
    {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void setCircleBorder(int circleBorder)
    {
        this.circleBorder = circleBorder;
        invalidate();
    }
    public void setCircleSize(int size)
    {
        this.circleSize = size;
      //  invalidate();
    }
    public void setCircleSite(int site)
    {
        this.circleSite = site;
      //  invalidate();
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        int pl = getPaddingLeft();
        int pr = getPaddingRight();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();

        int usableWidth = w - (pl + pr);
        int usableHeight = h - (pt + pb);

        int radius = Math.min(usableWidth, usableHeight) / circleSite;
        int cx = pl + (usableWidth / circleSize);
        int cy = pt + (usableHeight / circleSize);
        paint.setStrokeWidth(circleBorder);
        canvas.drawCircle(cx, cy, radius, paint);
    }
}