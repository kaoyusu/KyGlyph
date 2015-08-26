package su.kaoyu.glyph;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class GlyphReceiver extends BroadcastReceiver {

    public static final String GLYPH_START = "su.kaoyu.glyph.GLYPH_START";
    public static final String GLYPH_ADD = "su.kaoyu.glyph.GLYPH_ADD";
    public static final String GLYPH_EXIT = "su.kaoyu.glyph.GLYPH_EXIT";
    public static final String COLOR_CHANGED = "su.kaoyu.glyph.COLOR_CHANGED";

    private final GlyphService mGlyphService;

    @SuppressWarnings("unused")
    public GlyphReceiver() {
        this.mGlyphService = null;
    }

    public GlyphReceiver(GlyphService mGlyphService) {
        this.mGlyphService = mGlyphService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (mGlyphService == null || GLYPH_START.equals(action)) {
            context.startService(new Intent(context, GlyphService.class)
                    .putExtra("glyphNum", intent.getIntExtra("glyphNum", 0)));
        } else if (GLYPH_ADD.equals(action)) {
            String glyph = intent.getStringExtra("glyph");
            if (!TextUtils.isEmpty(glyph) && glyph.length() > 1) {
                mGlyphService.addGlyphView(glyph);
            }
        } else if (GLYPH_EXIT.equals(action)) {
            mGlyphService.stopSelf();
        } else if (Intent.ACTION_SCREEN_ON.equals(action) || Intent.ACTION_USER_PRESENT.equals(action)) {
            mGlyphService.updateScreen();
        } else if (COLOR_CHANGED.equals(action)) {
            String name = intent.getStringExtra("name");
            if (!TextUtils.isEmpty(name)) {
                int color = intent.getIntExtra("color", -1);
                mGlyphService.updateColor(name, color);
            }
        }

    }
}
