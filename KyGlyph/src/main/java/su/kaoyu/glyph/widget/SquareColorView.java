package su.kaoyu.glyph.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.larswerkman.holocolorpicker.OpacityGridBackgroundDrawable;

public class SquareColorView extends View {
    private Drawable mGridDrawable = new OpacityGridBackgroundDrawable();
    private int mColor;
    private Rect mRect = new Rect();
    private Paint mPaint = new Paint();

    public SquareColorView(Context context) {
        super(context);
        init();
    }

    public SquareColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SquareColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SquareColorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint.setColor(Color.TRANSPARENT);
    }

    public void setColor(int color) {
        this.mColor = color;
        mPaint.setColor(color);
        invalidate();
    }

    public int getColor() {
        return mColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mGridDrawable.setBounds(0, 0, right - left, bottom - top);
            mRect.set(0, 0, right - left, bottom - top);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mGridDrawable.draw(canvas);
        canvas.drawRect(mRect, mPaint);
    }
}
