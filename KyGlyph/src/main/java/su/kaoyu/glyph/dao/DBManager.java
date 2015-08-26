package su.kaoyu.glyph.dao;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import com.larswerkman.holocolorpicker.ColorPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import su.kaoyu.glyph.GlyphReceiver;
import su.kaoyu.glyph.GlyphService;

public class DBManager {
    private static DBManager mDBManager = null;
    private Context mContext = null;
    private DaoSession daoSession;
    private SharedPreferences sPerf;

    public static DBManager getInstanceAndInit(Context context) {
        if (mDBManager == null) {
            mDBManager = new DBManager(context);
        }
        return mDBManager;
    }

    public static DBManager getInstance() {
        if (mDBManager == null) {
            throw new IllegalStateException("DBManager has not been init yet");
        }
        return mDBManager;
    }

    private DBManager(Context context) {
        this.mContext = context;
        DBOpenHelper helper = new DBOpenHelper(context, "databases.db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        sPerf = context.getSharedPreferences("settings", Context.MODE_MULTI_PROCESS);
    }

    public boolean isAccessibilityEnabled() {
        return isAccessibilityEnabled(mContext, AccessibilityServiceInfo.FEEDBACK_GENERIC, GlyphService.class);
    }

    public static boolean isAccessibilityEnabled(Context context, int feedbackTypeFlags,
                                                 Class accessibilityServiceClass) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (am.isEnabled()) {
            ComponentName componentName = new ComponentName(context, accessibilityServiceClass);
            for (AccessibilityServiceInfo accessibilityServiceInfo
                    : am.getEnabledAccessibilityServiceList(feedbackTypeFlags)) {
                if (componentName.equals(
                        ComponentName.unflattenFromString(accessibilityServiceInfo.getId()))) {
                    return true;
                }
            }
        }
        return false;
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sPerf.registerOnSharedPreferenceChangeListener(listener);
    }

    public void sendColorChanged(String name, int color) {
        if (TextUtils.isEmpty(name) && color != -1) {
            throw new IllegalArgumentException();
        }
        Intent intent = new Intent(GlyphReceiver.COLOR_CHANGED);
        intent.putExtra("name", name);
        intent.putExtra("color", color);
        mContext.sendBroadcast(intent);
    }

    public void updateColor(String name, int color) {
        if (TextUtils.isEmpty(name) && color != -1) {
            throw new IllegalArgumentException();
        }
        SharedPreferences.Editor edit = sPerf.edit();
        edit.putInt(name, color);
        edit.apply();
    }

    public int getInputYPosition() {
        return sPerf.getInt("InputY", 0);
    }

    public void setInputYPosition(int inputY) {
        SharedPreferences.Editor edit = sPerf.edit();
        edit.putInt("InputY", inputY);
        edit.apply();
    }

    public int getInputPadding() {
        return sPerf.getInt("InputPadding", 0);
    }

    public void setInputPadding(int inputPadding) {
        SharedPreferences.Editor edit = sPerf.edit();
        edit.putInt("InputPadding", inputPadding);
        edit.apply();
    }

    public int getMenuColor() {
        return mContext.getSharedPreferences("settings", Context.MODE_MULTI_PROCESS)
                .getInt("MenuColor", 0xaaffffff);
    }

    public void setMenuColor(int color) {
        sendColorChanged("MenuColor", color);
    }

    public void setInputBgColor(int color) {
        sendColorChanged("InputBgColor", color);
    }

    public int getInputBgColor() {
        return mContext.getSharedPreferences("settings", Context.MODE_MULTI_PROCESS)
                .getInt("InputBgColor", 0x2200ff00);
    }

    public void setInputColor(int color) {
        sendColorChanged("InputColor", color);
    }

    public int getInputColor() {
        return mContext.getSharedPreferences("settings", Context.MODE_MULTI_PROCESS)
                .getInt("InputColor", 0xaaffffff);
    }

    public void setGlyphBgColor(int color) {
        sendColorChanged("GlyphBgColor", color);
    }

    public int getGlyphBgColor() {
        return mContext.getSharedPreferences("settings", Context.MODE_MULTI_PROCESS)
                .getInt("GlyphBgColor", 0xffffffff);
    }

    public void setGlyphDotColor(int color) {
        sendColorChanged("GlyphDotColor", color);
    }

    public int getGlyphDotColor() {
        return mContext.getSharedPreferences("settings", Context.MODE_MULTI_PROCESS)
                .getInt("GlyphDotColor", 0xff888888);
    }

    public void setGlyphLineColor(int color) {
        sendColorChanged("GlyphLineColor", color);
    }

    public int getGlyphLineColor() {
        return mContext.getSharedPreferences("settings", Context.MODE_MULTI_PROCESS)
                .getInt("GlyphLineColor", 0xff000000);
    }

    public synchronized void updateDatabase(final OnDatabaseUpdatedListener listener) {
        TableVersionDao tableVersionDao = daoSession.getTableVersionDao();
        List<JSONObject> updateList = new ArrayList<>();
        try {
            for (String s : mContext.getAssets().list("db")) {
                try {
                    JSONObject jsonObject = new JSONObject(loadStringFromAsset("db/" + s));
                    String name = jsonObject.optString("name");
                    int version = jsonObject.optInt("version");
                    if (!TextUtils.isEmpty(name)
                            && (name.contains("Order") || name.contains("Sequence"))) {
                        TableVersion tableVersion = tableVersionDao.load(name);
                        if (tableVersion == null || tableVersion.getVersion() < version) {
                            if (name.contains("Order")) {
                                updateList.add(0, jsonObject);
                            } else if (name.contains("Sequence")) {
                                updateList.add(jsonObject);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (JSONObject jsonObject : updateList) {
            String name = jsonObject.optString("name");
            listener.onDatabaseUpdating(name);
            if (name.contains("Order")) {
                updateGlyphOrder(jsonObject.optJSONObject(name));
            } else if (name.contains("Sequence")) {
                updateGlyphSequences(jsonObject.optJSONArray(name));
            }
            tableVersionDao.insertOrReplace(
                    new TableVersion(jsonObject.optString("name"),
                            jsonObject.optInt("version")));
        }
        listener.onDatabaseUpdated();
    }

    private void updateGlyphOrder(JSONObject glyphOrderJsonObject) {
        Iterator<String> keys = glyphOrderJsonObject.keys();
        GlyphDao glyphDao = daoSession.getGlyphDao();
        GlyphNameDao glyphNameDao = daoSession.getGlyphNameDao();
        while (keys.hasNext()) {
            String next = keys.next();
            GlyphName glyphName = new GlyphName(next);
            Glyph glyph = new Glyph(glyphOrderJsonObject.optString(next));
            glyphDao.insertOrReplace(glyph);
            glyphName.setGlyph(glyph);
            glyphNameDao.insertOrReplace(glyphName);
        }
    }

    public void updateGlyphSequences(JSONArray glyphSequencesJsonArray) {
        GlyphNameDao glyphNameDao = daoSession.getGlyphNameDao();
        GlyphSequenceDao glyphSequenceDao = daoSession.getGlyphSequenceDao();
        for (int i = 0; i < glyphSequencesJsonArray.length(); i++) {
            JSONArray glyphSequenceJsonArray = glyphSequencesJsonArray.optJSONArray(i);
            if (glyphSequenceJsonArray == null) {
                continue;
            }
            GlyphSequence glyphSequence = new GlyphSequence();
            glyphSequence.setGlyphNum(glyphSequenceJsonArray.length());
            boolean isSkip = false;
            for (int i1 = 0; i1 < glyphSequenceJsonArray.length(); i1++) {
                if (glyphNameDao.load(glyphSequenceJsonArray.optString(i1)) == null) {
                    isSkip = true;
                    Log.e("KyGlyph[DBManager]", "Need glyphOrder for " + glyphSequenceJsonArray.optString(i1));
                }
            }
            if (isSkip) {
                continue;
            }
            try {
                switch (glyphSequenceJsonArray.length()) {
                    case 5:
                        long glyphId5 = glyphNameDao.load(glyphSequenceJsonArray.optString(4)).getGlyphId();
                        glyphSequence.setGlyphId5(glyphId5);
                    case 4:
                        long glyphId4 = glyphNameDao.load(glyphSequenceJsonArray.optString(3)).getGlyphId();
                        glyphSequence.setGlyphId4(glyphId4);
                    case 3:
                        long glyphId3 = glyphNameDao.load(glyphSequenceJsonArray.optString(2)).getGlyphId();
                        glyphSequence.setGlyphId3(glyphId3);
                    case 2:
                        long glyphId2 = glyphNameDao.load(glyphSequenceJsonArray.optString(1)).getGlyphId();
                        glyphSequence.setGlyphId2(glyphId2);
                    case 1:
                        long glyphId1 = glyphNameDao.load(glyphSequenceJsonArray.optString(0)).getGlyphId();
                        glyphSequence.setGlyphId1(glyphId1);
                    default:
                        break;
                }
            } catch (NullPointerException ignored) {
                Log.e("KyGlyph[DBManager]", "Error glyphSequence:" + glyphSequenceJsonArray.toString());
                continue;
            }
            QueryBuilder<GlyphSequence> glyphSequenceQueryBuilder = glyphSequenceDao.queryBuilder();
            GlyphSequence unique = glyphSequenceQueryBuilder.where(
                    GlyphSequenceDao.Properties.GlyphNum.eq(glyphSequence.getGlyphNum()),
                    GlyphSequenceDao.Properties.GlyphId1.eq(glyphSequence.getGlyphId1()),
                    glyphSequence.getGlyphId2() == null ? GlyphSequenceDao.Properties.GlyphId2.isNull()
                            : GlyphSequenceDao.Properties.GlyphId2.eq(glyphSequence.getGlyphId2()),
                    glyphSequence.getGlyphId3() == null ? GlyphSequenceDao.Properties.GlyphId3.isNull()
                            : GlyphSequenceDao.Properties.GlyphId3.eq(glyphSequence.getGlyphId3()),
                    glyphSequence.getGlyphId4() == null ? GlyphSequenceDao.Properties.GlyphId4.isNull()
                            : GlyphSequenceDao.Properties.GlyphId4.eq(glyphSequence.getGlyphId4()),
                    glyphSequence.getGlyphId5() == null ? GlyphSequenceDao.Properties.GlyphId5.isNull()
                            : GlyphSequenceDao.Properties.GlyphId5.eq(glyphSequence.getGlyphId5()))
                    .limit(1)
                    .unique();
            if (unique != null) {
                continue;
            }
            glyphSequenceDao.insertOrReplace(glyphSequence);
        }
    }

    public List<GlyphSequence> getGlyphSequence(int glyphNum, String glyph) {
        return daoSession.getGlyphSequenceDao().queryBuilder()
                .where(GlyphSequenceDao.Properties.GlyphNum.eq(glyphNum),
                        GlyphSequenceDao.Properties.GlyphId1.eq(new Glyph(glyph).hashCode()))
                .list();
    }


    private String loadStringFromAsset(String filePath) throws IOException {
        InputStream is = mContext.getAssets().open(filePath);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, "UTF-8");
    }

    public interface OnDatabaseUpdatedListener {
        void onDatabaseUpdating(String name);

        void onDatabaseUpdated();
    }
}
