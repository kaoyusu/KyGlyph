package su.kaoyu.glyph.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import su.kaoyu.glyph.bean.Glyph;
import su.kaoyu.glyph.bean.GlyphSequencePair;
import su.kaoyu.glyph.dao.DBManager;
import su.kaoyu.glyph.dao.GlyphSequence;

public class GlyphSequencesView extends View implements GestureDetector.OnGestureListener, SharedPreferences.OnSharedPreferenceChangeListener {


    private Paint glyphLinePaint;
    private Paint glyphDotPaint;
    private Paint glyphBgPaint;

    private List<GlyphSequencePair> glyphSequencePairs = new ArrayList<>();
    private int rowSize;
    private int singleRowPosition;

    private int viewWidth;
    private float rowHeight;
    private float glyphPadding;
    private float glyphHeight;

    private GestureDetector gestureDetector;
    private boolean mTab;
    private float flingPosition;
    private WindowManager windowManager;
    private WindowManager.LayoutParams glyphSequencesParams;
    private boolean isAddedToWindow = false;
    private DBManager dbManager;
    private int glyphBgColor;
    private int glyphDotColor;
    private int glyphLineColor;

    public GlyphSequencesView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        dbManager = DBManager.getInstance();
        glyphBgColor = dbManager.getGlyphBgColor();
        glyphDotColor = dbManager.getGlyphDotColor();
        glyphLineColor = dbManager.getGlyphLineColor();

        glyphLinePaint = new Paint();
        glyphLinePaint.setAntiAlias(true);
        glyphLinePaint.setColor(glyphLineColor);
        glyphLinePaint.setStrokeJoin(Paint.Join.ROUND);
        glyphLinePaint.setStrokeCap(Paint.Cap.ROUND);
        glyphLinePaint.setStyle(Paint.Style.STROKE);

        glyphDotPaint = new Paint();
        glyphDotPaint.setAntiAlias(true);
        glyphDotPaint.setColor(glyphDotColor);
        glyphDotPaint.setStyle(Paint.Style.FILL);

        glyphBgPaint = new Paint();
        glyphBgPaint.setAntiAlias(true);
        glyphBgPaint.setColor(glyphBgColor);
        glyphBgPaint.setStyle(Paint.Style.FILL);
        gestureDetector = new GestureDetector(getContext(), this);

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        glyphSequencesParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        glyphSequencesParams.gravity = Gravity.CENTER | Gravity.TOP;

        dbManager.registerOnSharedPreferenceChangeListener(this);
    }

    public void addToWindow() {
        if (!isAddedToWindow) {
            windowManager.addView(this, glyphSequencesParams);
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
        if (!(isLockScreen && glyphSequencesParams.type == WindowManager.LayoutParams.TYPE_SYSTEM_ERROR)
                && !(!isLockScreen && glyphSequencesParams.type == WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)) {
            glyphSequencesParams.type = isLockScreen ? WindowManager.LayoutParams.TYPE_SYSTEM_ERROR : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            if (isAddedToWindow) {
                removeFromWindow();
                addToWindow();
            }
        }
    }

    public void setGlyphSequences(List<GlyphSequence> list) {
        if (list == null) {
            throw new IllegalArgumentException();
        }

        glyphSequencePairs.clear();
        List<GlyphSequence> tmpList = new ArrayList<>();
        tmpList.addAll(list);
        while (tmpList.size() > 0) {
            if (tmpList.size() == 1) {
                glyphSequencePairs.add(new GlyphSequencePair(tmpList.get(0), null));
                tmpList.remove(0);
                break;
            }
            int maxSameNum = 0;
            GlyphSequencePair glyphSequencePair = null;
            for (int i = 0; i < tmpList.size(); i++) {
                for (int j = i + 1; j < tmpList.size(); j++) {
                    GlyphSequencePair tmp = new GlyphSequencePair(tmpList.get(i), tmpList.get(j));
                    if (tmp.sameNum > maxSameNum) {
                        maxSameNum = tmp.sameNum;
                        glyphSequencePair = tmp;
                    }
                }
            }
            if (glyphSequencePair == null) {
                throw new IllegalArgumentException();
            }
            glyphSequencePairs.add(glyphSequencePair);
            tmpList.remove(glyphSequencePair.first);
            tmpList.remove(glyphSequencePair.second);
        }

        int size = glyphSequencePairs.size();
        rowSize = size / 2 + (size % 2 == 0 ? 0 : 1);
        singleRowPosition = (size % 2 == 1 ? size - 1 : -1);

        flingPosition = 0;

        requestLayout();
        invalidate();
        if (!list.isEmpty()) {
            mTab = list.size() > 1;
            removeCallbacks(clearRunnable);
            postDelayed(clearRunnable, list.size() == 1 ? 20000 : 10000);
        }
    }

    private final Runnable clearRunnable = new Runnable() {
        @Override
        public void run() {
            setGlyphSequences(new ArrayList<GlyphSequence>());
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        glyphLinePaint.setStrokeWidth(viewWidth * 0.004f);
        glyphPadding = viewWidth / 400f;
        float glyphWidth = viewWidth * 376f / 4000f;
        glyphHeight = (float) (glyphWidth * 2f / Math.sqrt(3));
        rowHeight = glyphHeight * 7f / 4f;
        if (glyphSequencePairs.size() == 0) {
            setMeasuredDimension(viewWidth, 1);
        } else if (glyphSequencePairs.size() == 1) {
            setMeasuredDimension(viewWidth, (int) (rowHeight));
        } else {
            setMeasuredDimension(viewWidth, (int) (rowHeight * rowSize + (singleRowPosition != -1 ? glyphHeight - rowHeight : 0)));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        for (int i = 0; i < glyphSequencePairs.size(); i++) {
            drawGlyphSequence(canvas, i);
        }
    }

    private void drawGlyphSequence(Canvas canvas, int position) {
        GlyphSequencePair glyphSequencePair = glyphSequencePairs.get(position);

        int size = glyphSequencePairs.size();

        double glyphStartX;
        double glyphStartY;
        double rowWidth;
        double rowHeight;
        double glyphPadding = this.glyphPadding;

        glyphStartY = position / 2 * this.rowHeight;
        if (singleRowPosition == position) {
            glyphStartX = 0;
            rowWidth = this.viewWidth;
            if (size == 1) {
                rowHeight = this.rowHeight;
                if (glyphSequencePair.sameNum == 0) {
                    glyphPadding = this.glyphPadding * 2;
                }
            } else if (glyphSequencePair.sameNum == 0) {
                rowHeight = this.glyphHeight;
            } else {
                rowHeight = this.rowHeight;
            }
        } else {
            glyphStartX = (position % 2) * (viewWidth / 2d);

            rowWidth = this.viewWidth / 2d;
            rowHeight = this.rowHeight;
        }

        drawGlyphSequence(canvas, glyphStartX + flingPosition, glyphStartY, rowWidth, rowHeight, glyphPadding, glyphSequencePair);
    }

    private void drawGlyphSequence(Canvas canvas, double glyphStartX, double glyphStartY, double rowWidth, double rowHeight, double glyphPadding, GlyphSequencePair glyphSequencePair) {
        double glyphWidth = rowHeight * Math.sqrt(3) / 2 / (glyphSequencePair.widthFactor / glyphSequencePair.glyphNum);
        glyphWidth = Math.min(glyphWidth,
                rowWidth / glyphSequencePair.widthFactor - glyphPadding * 2);
        double glyphHeight = glyphWidth * 2 / Math.sqrt(3);
        if (glyphSequencePair.widthFactor != glyphSequencePair.glyphNum) {
            glyphHeight = Math.min(glyphHeight, (rowHeight - glyphPadding * 2) * 4 / 7);
            glyphWidth = glyphHeight * Math.sqrt(3) / 2;
        }

        Glyph[] glyphs1 = glyphSequencePair.first.getGlyphSequence();
        Glyph[] glyphs2 = null;
        if (glyphSequencePair.sameNum != 0) {
            glyphs2 = glyphSequencePair.second.getGlyphSequence();
        }
        glyphStartX += (rowWidth - (glyphWidth * glyphSequencePair.widthFactor + glyphPadding * 2 * glyphSequencePair.glyphNum)) / 2 + glyphPadding;
        for (int i = 0; i < glyphSequencePair.glyphNum; i++) {
            if (glyphSequencePair.sameNum == 0 || glyphSequencePair.isSame(i)) {
                drawGlyph(canvas, glyphStartX, glyphStartY + (rowHeight - glyphHeight) / 2, glyphWidth, glyphs1[i]);
            } else {
                drawGlyph(canvas, glyphStartX, glyphStartY, glyphWidth, glyphs1[i]);
                assert glyphs2 != null;
                drawGlyph(canvas, glyphStartX + glyphWidth / 2, glyphStartY + (rowHeight - glyphHeight), glyphWidth, glyphs2[i]);
            }
            glyphStartX += glyphWidth * glyphSequencePair.getWidthFactor(i) + 2 * glyphPadding;
        }
    }

    private void drawGlyph(Canvas canvas, double x, double y, double size, Glyph glyph) {
        canvas.translate((float) x, (float) y);
        canvas.drawPath(Glyph.getOuterPath((int) size), glyphBgPaint);
        canvas.drawPath(Glyph.getDotsPath((int) size), glyphDotPaint);
        canvas.drawPath(glyph.getGlyphPath((int) size), glyphLinePaint);
        canvas.translate((float) -x, (float) -y);
    }

    @Override
    public boolean onTouchEvent(@SuppressWarnings("NullableProblems") MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (mTab) {
            final float x = e.getX();
            final float y = e.getY();
            for (int i = 1; i <= rowSize; i++) {
                if (y > (i - 1) * rowHeight && y < i * rowHeight) {
                    GlyphSequencePair glyphSequencePair;
                    if ((i == rowSize && singleRowPosition != -1) || x < viewWidth / 2) {
                        glyphSequencePair = glyphSequencePairs.get((i - 1) * 2);
                    } else {
                        glyphSequencePair = glyphSequencePairs.get((i - 1) * 2 + 1);
                    }
                    ArrayList<GlyphSequence> list = new ArrayList<>();
                    if (glyphSequencePair.sameNum == 0 || y < i * rowHeight - rowHeight / 2) {
                        list.add(glyphSequencePair.first);
                    } else {
                        list.add(glyphSequencePair.second);
                    }
                    setGlyphSequences(list);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX, float velocityY) {
        if (Math.abs(velocityX) > 1000) {
            final Runnable action = new Runnable() {
                @Override
                public void run() {
                    if (Math.abs(flingPosition) < viewWidth) {
                        flingPosition += flingPosition / 3 + velocityX / 1000;
                        invalidate();
                        postDelayed(this, 1);
                    } else {
                        post(clearRunnable);
                    }
                }
            };
            removeCallbacks(clearRunnable);
            postDelayed(action, 1);
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (glyphBgColor != dbManager.getGlyphBgColor()
                || glyphDotColor != dbManager.getGlyphDotColor()
                || glyphLineColor != dbManager.getGlyphLineColor()) {

            glyphBgColor = dbManager.getGlyphBgColor();
            glyphDotColor = dbManager.getGlyphDotColor();
            glyphLineColor = dbManager.getGlyphLineColor();
            glyphBgPaint.setColor(glyphBgColor);
            glyphLinePaint.setColor(glyphLineColor);
            glyphDotPaint.setColor(glyphDotColor);
            invalidate();
        }
    }
}
