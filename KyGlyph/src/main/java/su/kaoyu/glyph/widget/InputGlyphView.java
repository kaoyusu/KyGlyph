package su.kaoyu.glyph.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import su.kaoyu.glyph.GlyphReceiver;
import su.kaoyu.glyph.bean.Glyph;
import su.kaoyu.glyph.dao.DBManager;

public class InputGlyphView extends View implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final DBManager dbManager;
    private int inputColor;
    private int inputBgColor;
    private int size;

    private StringBuilder inputGlyph;
    private Path inputPath;
    private Path inputDrawPath;
    private Paint inputDotPaint;
    private Paint inputLinePaint;
    private double lastDistance;

    private final WindowManager windowManager;
    private WindowManager.LayoutParams inputParams;

    private float lastRawY;

    private boolean isResize = false;
    private boolean isMultiTouch = false;

    private boolean isAddedToWindow = false;

    public InputGlyphView(Context context) {
        super(context);
        dbManager = DBManager.getInstance();

        inputBgColor = dbManager.getInputBgColor();

        inputDotPaint = new Paint();
        inputDotPaint.setAntiAlias(true);
        inputColor = dbManager.getInputColor();
        inputDotPaint.setColor(inputColor);
        inputDotPaint.setStyle(Paint.Style.FILL);

        inputLinePaint = new Paint();
        inputLinePaint.setAntiAlias(true);
        inputLinePaint.setColor(inputColor);
        inputLinePaint.setStyle(Paint.Style.STROKE);
        inputLinePaint.setStrokeJoin(Paint.Join.ROUND);
        inputLinePaint.setStrokeCap(Paint.Cap.ROUND);

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        inputParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        inputParams.gravity = Gravity.BOTTOM;
        inputParams.y = dbManager.getInputYPosition();
        setPadding(dbManager.getInputPadding(), 0, 0, 0);

        dbManager.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        size = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        inputLinePaint.setStrokeWidth((size - dbManager.getInputPadding() * 2) * 0.02f);
        setMeasuredDimension(size, (int) (size * 2f / Math.sqrt(3)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(dbManager.getInputBgColor());
        canvas.drawPath(Glyph.getInputDotsPath(size, getPaddingLeft()), inputDotPaint);
        if (inputDrawPath != null) {
            canvas.drawPath(inputDrawPath, inputLinePaint);
        }
    }

    public void addToWindow() {
        if (!isAddedToWindow) {
            windowManager.addView(this, inputParams);
            isAddedToWindow = true;
        }
    }

    public void removeFromWindow() {
        if (isAddedToWindow) {
            windowManager.removeView(this);
            isAddedToWindow = false;
        }
    }

    public void toggleWindow() {
        if (isAddedToWindow) {
            removeFromWindow();
        } else {
            addToWindow();
        }
    }

    @Override
    public boolean onTouchEvent(@SuppressWarnings("NullableProblems") MotionEvent event) {
        float rawY = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMultiTouch = false;
                if (isResize) {
                    lastDistance = 0;
                    lastRawY = rawY;
                } else {
                    inputGlyph = new StringBuilder();
                    inputPath = null;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isResize) {
                    float xPosition = event.getX();
                    float yPosition = event.getY();

                    String s = Glyph.checkPoint(size, getPaddingLeft(), xPosition, yPosition);
                    if (!TextUtils.isEmpty(s) && !inputGlyph.toString().endsWith(s)) {
                        inputGlyph.append(s);
                        inputPath = Glyph.getGlyph(inputGlyph.toString()).getInputGlyphPath(size, getPaddingLeft());
                    } else if (inputPath != null && !inputPath.isEmpty()) {
                        inputDrawPath = new Path(inputPath);
                        inputDrawPath.lineTo(xPosition, yPosition);
                    }
                    invalidate();
                } else {
                    if (!isMultiTouch && event.getPointerCount() == 1) {
                        updateViewPosition(rawY);
                        lastRawY = rawY;
                    } else if (event.getPointerCount() == 2) {
                        isMultiTouch = true;
                        double distance = distanceBetweenFingers(event);
                        if (lastDistance == 0) {
                            lastDistance = distance;
                        }
                        int paddingLeft = getPaddingLeft();
                        int left = paddingLeft - (int) (distance - lastDistance);
                        if (left < 0) {
                            left = 0;
                        } else if (left > getMeasuredWidth() / 6) {
                            left = getMeasuredWidth() / 6;
                        }
                        setPadding(left, 0, 0, 0);

                        lastDistance = distance;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isResize) {
                    if (inputGlyph != null && inputGlyph.toString().length() > 1) {
                        getContext().sendBroadcast(new Intent(GlyphReceiver.GLYPH_ADD)
                                .putExtra("glyph", inputGlyph.toString()));
                    }
                    inputGlyph = null;
                    inputPath = null;
                    inputDrawPath = null;
                    invalidate();
                } else {
                    dbManager.setInputYPosition(inputParams.y);
                    dbManager.setInputPadding(getPaddingLeft());
                }
                break;
            case MotionEvent.ACTION_OUTSIDE:
//                Toast.makeText(getContext(), "ACTION_OUTSIDE", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private double distanceBetweenFingers(MotionEvent event) {
        if (event.getPointerCount() < 2) {
            return 0;
        }
        return Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2));
    }

    private void updateViewPosition(float y) {
        inputParams.y -= (int) (y - lastRawY);
        windowManager.updateViewLayout(this, inputParams);
    }

    public void setResize(boolean isResize) {
        this.isResize = isResize;
    }

    public void setLockScreen(boolean isLockScreen) {
        if (!(isLockScreen && inputParams.type == WindowManager.LayoutParams.TYPE_SYSTEM_ERROR)
                && !(!isLockScreen && inputParams.type == WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)) {
            inputParams.type = isLockScreen ? WindowManager.LayoutParams.TYPE_SYSTEM_ERROR : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            if (isAddedToWindow) {
                removeFromWindow();
                addToWindow();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ("InputColor".equals(key) || "InputBgColor".equals(key)) {
            inputBgColor = dbManager.getInputBgColor();
            inputColor = dbManager.getInputColor();
            inputDotPaint.setColor(inputColor);
            inputLinePaint.setColor(inputColor);
            invalidate();
        }
    }
}