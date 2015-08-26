package su.kaoyu.glyph;

import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;

import su.kaoyu.glyph.dao.DBManager;
import su.kaoyu.glyph.dao.GlyphSequence;
import su.kaoyu.glyph.widget.GlyphSequencesView;
import su.kaoyu.glyph.widget.MenuView;

public class GlyphService extends AccessibilityService {
    private KeyguardManager km;

    private boolean isViewShown = false;
    private MenuView menuView;
    private GlyphSequencesView mGlyphSequencesView;

    private GlyphReceiver receiver = new GlyphReceiver(this);
    private int glyphNum = 5;
    private DBManager dbManager;


    @Override
    public void onCreate() {
        super.onCreate();
        dbManager = DBManager.getInstanceAndInit(this);
        mGlyphSequencesView = new GlyphSequencesView(this);
        menuView = new MenuView(this);

        km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
    }


    public void updateColor(String name, int color) {
        dbManager.updateColor(name, color);
    }

    private void showView() {
        if (!isViewShown) {
            boolean isLocked = km.inKeyguardRestrictedInputMode();
            mGlyphSequencesView.setLockScreen(isLocked);
            mGlyphSequencesView.addToWindow();

            menuView.setLockScreen(isLocked);
            menuView.addToWindow();

            isViewShown = true;

            registerReceiver(receiver, new IntentFilter(GlyphReceiver.GLYPH_ADD));
            registerReceiver(receiver, new IntentFilter(GlyphReceiver.GLYPH_EXIT));
            registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
            registerReceiver(receiver, new IntentFilter(Intent.ACTION_USER_PRESENT));
            registerReceiver(receiver, new IntentFilter(GlyphReceiver.COLOR_CHANGED));
        }
    }

    private void hideView() {
        if (isViewShown) {
            unregisterReceiver(receiver);
            mGlyphSequencesView.removeFromWindow();
            menuView.removeAllFromWindow();
            isViewShown = false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int glyphNum = intent.getIntExtra("glyphNum", 0);
            if (glyphNum > 0 && glyphNum <= 5) {
                this.glyphNum = glyphNum;
            }
        }

        if (!dbManager.isAccessibilityEnabled()) {
            showView();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        hideView();
        super.onDestroy();
    }

    public void updateScreen() {
        boolean isLockScreen = km.inKeyguardRestrictedInputMode();

        mGlyphSequencesView.setLockScreen(isLockScreen);
        menuView.setLockScreen(isLockScreen);
    }

    public void addGlyphView(String glyph) {
        List<GlyphSequence> glyphSequence = DBManager.getInstance().getGlyphSequence(glyphNum, glyph);
        if (!glyphSequence.isEmpty()) {
            menuView.hideInputGlyphView();
            mGlyphSequencesView.setGlyphSequences(glyphSequence);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.isFullScreen()) {
                if ("com.nianticproject.ingress".equals(event.getPackageName())
                        || getApplication().getPackageName().equals(event.getPackageName())) {
                    showView();
                } else {
                    hideView();
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
