package su.kaoyu.glyph.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.larswerkman.holocolorpicker.ColorPicker;

import su.kaoyu.glyph.GlyphReceiver;
import su.kaoyu.glyph.bean.Glyph;
import su.kaoyu.glyph.dao.DBManager;

public class MenuView extends View implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context mContext;

    private float inRadius;
    private float outRadius;
    private Paint mFillPaint;
    private Paint mFillSelectedPaint;
    private Paint mLinePaint;
    private Paint mLineSelectedPaint;
    private Paint mTextPaint;
    private Paint mTextSelectedPaint;
    private Paint mRingPaint;
    private Paint mRingSelectedPaint;

    private RectF inRectF;
    private RectF outRectF;

    private Rect viewRect;

    private int viewPosition;
    private boolean isLongPress = false;
    private boolean isOutside = false;
    private boolean isResize = false;

    private WindowManager windowManager;
    private WindowManager.LayoutParams menuParams;
    private MenuItemsView menuItemsView;
    private WindowManager.LayoutParams menuItemsParams;

    private InputGlyphView inputGlyphView;

    private Point screenSize = new Point();
    private float mOffsetY;

    private String hoveredItem;
    private String selectedItem = "5";
    private boolean isAddedToWindow = false;
    private DBManager dbManager;

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        Typeface mTypeface = Typeface.createFromAsset(context.getAssets(), "font/KyGlyph.ttf");

        dbManager = DBManager.getInstance();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        inRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, displayMetrics);
        outRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, displayMetrics);

        viewPosition = (int) (outRadius - inRadius);

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        menuParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        menuParams.gravity = Gravity.START | Gravity.TOP;
        menuParams.x = 0;
        menuParams.y = viewPosition;

        menuItemsView = new MenuItemsView(context);
        menuItemsParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        menuItemsParams.gravity = Gravity.START | Gravity.TOP;
        menuItemsParams.x = (int) (inRadius * Math.sqrt(3) / 2);
        menuItemsParams.y = viewPosition - (int) (outRadius - inRadius);


        mFillPaint = new Paint();
        mFillPaint.setAntiAlias(true);
        mFillPaint.setStyle(Paint.Style.FILL);

        mFillSelectedPaint = new Paint();
        mFillSelectedPaint.setAntiAlias(true);
        mFillSelectedPaint.setStyle(Paint.Style.FILL);

        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth((outRadius - inRadius));

        mRingSelectedPaint = new Paint();
        mRingSelectedPaint.setAntiAlias(true);
        mRingSelectedPaint.setStyle(Paint.Style.STROKE);
        mRingSelectedPaint.setStrokeWidth((outRadius - inRadius));

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(inRadius);
        mTextPaint.setTypeface(mTypeface);

        mTextSelectedPaint = new Paint();
        mTextSelectedPaint.setAntiAlias(true);
        mTextSelectedPaint.setTextSize(inRadius);
        mTextSelectedPaint.setTypeface(mTypeface);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(1);

        mLineSelectedPaint = new Paint();
        mLineSelectedPaint.setAntiAlias(true);
        mLineSelectedPaint.setStyle(Paint.Style.STROKE);
        mLineSelectedPaint.setStrokeWidth(1);

        setPaintColor(dbManager.getMenuColor());

        inRectF = new RectF(-inRadius, outRadius - inRadius, inRadius, inRadius + outRadius);
        outRectF = new RectF(-outRadius + (outRadius - inRadius) / 2f, (outRadius - inRadius) / 2f,
                outRadius - (outRadius - inRadius) / 2f, inRadius + outRadius + (outRadius - inRadius) / 2f);

        inputGlyphView = new InputGlyphView(context);

        viewRect = new Rect(0, 0, (int) (inRadius * Math.sqrt(3)), (int) (inRadius * 2));

        dbManager.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) (inRadius * Math.sqrt(3)), (int) (inRadius * 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(Glyph.getOuterPath((int) (inRadius * Math.sqrt(3))), mFillPaint);
        if (isOutside) {
            canvas.drawText(hoveredItem, (float) (inRadius * Math.sqrt(3) / 2 - mTextPaint.measureText("r") / 2),
                    inRadius + mTextPaint.getTextSize() / 2, mTextPaint);
        } else if (isResize) {
            canvas.drawText("r", (float) (inRadius * Math.sqrt(3) / 2 - mTextPaint.measureText("r") / 2),
                    inRadius + mTextPaint.getTextSize() / 2, mTextPaint);
        } else {
            canvas.drawText(selectedItem, (float) (inRadius * Math.sqrt(3) / 2 - mTextPaint.measureText("5") / 2),
                    inRadius + mTextPaint.getTextSize() / 2, mTextPaint);
        }
    }

    @Override
    public boolean onTouchEvent(@SuppressWarnings("NullableProblems") MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        float rawY = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                windowManager.getDefaultDisplay().getSize(screenSize);

                mOffsetY = rawY - menuParams.y;

                isOutside = false;
                isLongPress = false;

                if (isResize) {
                    inputGlyphView.setResize(isResize = false);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isLongPress) {
                    updateViewPosition(rawY);
                } else if (isOutside) {
                    menuItemsView.addToWindow();
                    menuItemsView.selectItem(getSelectItem(x, y));
                    invalidate();
                } else if (!viewRect.contains((int) x, (int) y)) {
                    isOutside = true;
                } else if (event.getEventTime() - event.getDownTime() > 1000) {
                    isLongPress = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isLongPress) {
                    updateViewPosition(rawY);
                } else if (isOutside) {
                    int selectItem = getSelectItem(x, y);
                    if (selectItem != -1) {
                        MenuItems menuItems = menuItemsView.getMenuItems();
                        switch (menuItems.texts[selectItem]) {
                            case "q":
                                mContext.sendBroadcast(new Intent(GlyphReceiver.GLYPH_EXIT));
                                break;
                            case "4":
                                mContext.sendBroadcast(new Intent(GlyphReceiver.GLYPH_START)
                                        .putExtra("glyphNum", 4));
                                selectedItem = "4";
                                break;
                            case "5":
                                mContext.sendBroadcast(new Intent(GlyphReceiver.GLYPH_START)
                                        .putExtra("glyphNum", 5));
                                selectedItem = "5";
                                break;
                            case "r":
                                isResize = true;
                                inputGlyphView.addToWindow();
                                inputGlyphView.setResize(isResize);
                                break;
                        }
                    }
                    menuItemsView.removeFromWindow();

                } else {
                    inputGlyphView.toggleWindow();
                }

                isOutside = false;
                isLongPress = false;
                invalidate();
                break;
        }
        return true;
    }

    private int getSelectItem(float x, float y) {
        if (Math.sqrt(Math.pow(inRadius / 2 - x, 2) + Math.pow(inRadius - y, 2)) > inRadius
                && Math.sqrt(Math.pow(inRadius / 2 - x, 2) + Math.pow(inRadius - y, 2)) < outRadius) {
            double angel = Math.toDegrees(Math.atan2(y - inRadius, x - inRadius / 2));

            MenuItems menuItems = menuItemsView.getMenuItems();
            float sweepAngle = menuItems.sweepAngle / menuItems.texts.length;
            for (int i = 0; i < menuItems.texts.length; i++) {
                float startAngle = menuItems.startAngle + sweepAngle * i;
                if (angel >= startAngle && angel < startAngle + sweepAngle) {
                    hoveredItem = menuItems.texts[i];
                    return i;
                }
            }
        }
        hoveredItem = selectedItem;
        return -1;
    }

    public void hideInputGlyphView() {
        inputGlyphView.removeFromWindow();
    }

    public void setLockScreen(boolean isLockScreen) {
        if (!(isLockScreen && menuParams.type == WindowManager.LayoutParams.TYPE_SYSTEM_ERROR)
                && !(!isLockScreen && menuParams.type == WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)) {
            menuParams.type = isLockScreen ? WindowManager.LayoutParams.TYPE_SYSTEM_ERROR : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            if (isAddedToWindow) {
                removeFromWindow();
                addToWindow();
            }
        }

        inputGlyphView.setLockScreen(isLockScreen);
        menuItemsView.setLockScreen(isLockScreen);
    }

    public void addToWindow() {
        if (!isAddedToWindow) {
            windowManager.addView(this, menuParams);
            isAddedToWindow = true;
        }
    }

    private void removeFromWindow() {
        if (isAddedToWindow) {
            windowManager.removeView(this);
            isAddedToWindow = false;
        }
    }

    public void removeAllFromWindow() {
        removeFromWindow();
        menuItemsView.removeFromWindow();
        inputGlyphView.removeFromWindow();
    }

    private void updateViewPosition(float y) {
        if (y - mOffsetY < outRadius - inRadius) {
            menuParams.y = (int) (outRadius - inRadius);
        } else if (y - mOffsetY + (outRadius + inRadius) > screenSize.y) {
            menuParams.y = (int) (screenSize.y - (outRadius + inRadius));
        } else {
            menuParams.y = (int) (y - mOffsetY);
        }
        viewPosition = menuParams.y;
        windowManager.updateViewLayout(this, menuParams);

        menuItemsParams.y = viewPosition - (int) (outRadius - inRadius);
    }

    private void setPaintColor(int color) {
        mFillPaint.setColor(color);
        mFillSelectedPaint.setColor(0xffffffff - (color & 0xffffff));
        mRingPaint.setColor(color);
        mRingSelectedPaint.setColor(0xffffffff - (color & 0xffffff));
        mTextPaint.setColor(0xffffffff - (color & 0xffffff));
        mTextSelectedPaint.setColor(color);
        mLinePaint.setColor(0xffffffff - (color & 0xffffff));
        mLineSelectedPaint.setColor(color);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ("MenuColor".equals(key)) {
            setPaintColor(dbManager.getMenuColor());
            invalidate();
            menuItemsView.invalidate();
        }
    }

    public class MenuItems {
        public float startAngle;
        public float sweepAngle;
        public String[] texts;

        public MenuItems(float startAngle, float sweepAngle, String... texts) {
            if (texts == null || texts.length == 0) {
                throw new IllegalArgumentException();
            }
            this.startAngle = startAngle;
            this.sweepAngle = sweepAngle;
            this.texts = texts;
        }
    }

    private class MenuItemsView extends View {
        private int selectedItem = -1;
        private MenuItems menuItems;
        private boolean isAddedToWindow = false;

        public MenuItemsView(Context context) {
            super(context);
            init();
        }

        private void init() {
            if (DBManager.getInstance().isAccessibilityEnabled()) {
                setMenuItems(new MenuItems(-90, 180, "4", "5", "r"));
            } else {
                setMenuItems(new MenuItems(-90, 180, "q", "4", "5", "r"));
            }
        }

        public MenuItems getMenuItems() {
            return menuItems;
        }

        public void setMenuItems(MenuItems menuItems) {
            if (menuItems == null) {
                throw new IllegalArgumentException();
            }
            this.menuItems = menuItems;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension((int) outRadius, (int) (outRadius * 2));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float sweepAngle = menuItems.sweepAngle / menuItems.texts.length;
            for (int i = 0; i < menuItems.texts.length; i++) {
                float startAngle = menuItems.startAngle + sweepAngle * i;
                drawSectorWithText(canvas, startAngle, sweepAngle, menuItems.texts[i], false);
            }
            if (selectedItem >= 0 && selectedItem < menuItems.texts.length) {
                float startAngle = menuItems.startAngle + sweepAngle * selectedItem;
                drawSectorWithText(canvas, startAngle, sweepAngle, menuItems.texts[selectedItem], true);
            }
        }

        private void drawSectorWithText(Canvas canvas, float startAngle, float sweepAngle,
                                        String text, boolean isSelected) {
            canvas.drawArc(outRectF, startAngle, sweepAngle, false,
                    !isSelected ? mRingPaint : mRingSelectedPaint);
            canvas.drawArc(inRectF, startAngle, sweepAngle, false,
                    !isSelected ? mLinePaint : mLineSelectedPaint);
            canvas.drawLine(getX(inRadius, startAngle), getY(inRadius, startAngle) + outRadius,
                    getX(outRadius, startAngle), getY(outRadius, startAngle) + outRadius,
                    !isSelected ? mLinePaint : mLineSelectedPaint);
            canvas.drawLine(getX(inRadius, startAngle + sweepAngle),
                    getY(inRadius, startAngle + sweepAngle) + outRadius,
                    getX(outRadius, startAngle + sweepAngle),
                    getY(outRadius, startAngle + sweepAngle) + outRadius,
                    !isSelected ? mLinePaint : mLineSelectedPaint);

            float textWidth = mTextPaint.measureText(text);
            float textHeight = mTextPaint.getTextSize();
            float x = getX((inRadius + outRadius) / 2, startAngle + sweepAngle / 2);
            float y = getY((inRadius + outRadius) / 2, startAngle + sweepAngle / 2) + inRadius + outRadius;
            canvas.drawText(text, x - textWidth / 2, y - textHeight / 2,
                    !isSelected ? mTextPaint : mTextSelectedPaint);
        }

        public void selectItem(int selectedItem) {
            this.selectedItem = selectedItem;
            invalidate();
        }

        private float getX(float length, float angle) {
            return (float) (length * Math.cos(Math.toRadians(angle)));
        }

        private float getY(float length, float angle) {
            return (float) (length * Math.sin(Math.toRadians(angle)));
        }

        public void addToWindow() {
            if (!isAddedToWindow) {
                init();
                windowManager.addView(this, menuItemsParams);
                isAddedToWindow = true;
            }
        }

        public void removeFromWindow() {
            if (isAddedToWindow) {
                windowManager.removeView(this);
                isAddedToWindow = false;
            }
        }

        public void setLockScreen(boolean isLockScreen) {
            if (!(isLockScreen && menuItemsParams.type == WindowManager.LayoutParams.TYPE_SYSTEM_ERROR)
                    && !(!isLockScreen && menuItemsParams.type == WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)) {
                menuItemsParams.type = isLockScreen ? WindowManager.LayoutParams.TYPE_SYSTEM_ERROR : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                if (isAddedToWindow) {
                    removeFromWindow();
                    addToWindow();
                }
            }
        }
    }
}
