package su.kaoyu.glyph.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBOpenHelper extends DaoMaster.OpenHelper {
    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
