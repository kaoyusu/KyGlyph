package su.kaoyu.glyph.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;

import su.kaoyu.glyph.dao.GlyphSequence;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "GLYPH_SEQUENCE".
*/
public class GlyphSequenceDao extends AbstractDao<GlyphSequence, Long> {

    public static final String TABLENAME = "GLYPH_SEQUENCE";

    /**
     * Properties of entity GlyphSequence.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property GlyphNum = new Property(1, int.class, "glyphNum", false, "GLYPH_NUM");
        public final static Property GlyphId1 = new Property(2, long.class, "glyphId1", false, "GLYPH_ID1");
        public final static Property GlyphId2 = new Property(3, Long.class, "glyphId2", false, "GLYPH_ID2");
        public final static Property GlyphId3 = new Property(4, Long.class, "glyphId3", false, "GLYPH_ID3");
        public final static Property GlyphId4 = new Property(5, Long.class, "glyphId4", false, "GLYPH_ID4");
        public final static Property GlyphId5 = new Property(6, Long.class, "glyphId5", false, "GLYPH_ID5");
    };

    private DaoSession daoSession;


    public GlyphSequenceDao(DaoConfig config) {
        super(config);
    }
    
    public GlyphSequenceDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GLYPH_SEQUENCE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"GLYPH_NUM\" INTEGER NOT NULL ," + // 1: glyphNum
                "\"GLYPH_ID1\" INTEGER NOT NULL ," + // 2: glyphId1
                "\"GLYPH_ID2\" INTEGER," + // 3: glyphId2
                "\"GLYPH_ID3\" INTEGER," + // 4: glyphId3
                "\"GLYPH_ID4\" INTEGER," + // 5: glyphId4
                "\"GLYPH_ID5\" INTEGER);"); // 6: glyphId5
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GLYPH_SEQUENCE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, GlyphSequence entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getGlyphNum());
        stmt.bindLong(3, entity.getGlyphId1());
 
        Long glyphId2 = entity.getGlyphId2();
        if (glyphId2 != null) {
            stmt.bindLong(4, glyphId2);
        }
 
        Long glyphId3 = entity.getGlyphId3();
        if (glyphId3 != null) {
            stmt.bindLong(5, glyphId3);
        }
 
        Long glyphId4 = entity.getGlyphId4();
        if (glyphId4 != null) {
            stmt.bindLong(6, glyphId4);
        }
 
        Long glyphId5 = entity.getGlyphId5();
        if (glyphId5 != null) {
            stmt.bindLong(7, glyphId5);
        }
    }

    @Override
    protected void attachEntity(GlyphSequence entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public GlyphSequence readEntity(Cursor cursor, int offset) {
        GlyphSequence entity = new GlyphSequence( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // glyphNum
            cursor.getLong(offset + 2), // glyphId1
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // glyphId2
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // glyphId3
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // glyphId4
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6) // glyphId5
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, GlyphSequence entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setGlyphNum(cursor.getInt(offset + 1));
        entity.setGlyphId1(cursor.getLong(offset + 2));
        entity.setGlyphId2(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setGlyphId3(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setGlyphId4(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setGlyphId5(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(GlyphSequence entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(GlyphSequence entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getGlyphDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getGlyphDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T2", daoSession.getGlyphDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T3", daoSession.getGlyphDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T4", daoSession.getGlyphDao().getAllColumns());
            builder.append(" FROM GLYPH_SEQUENCE T");
            builder.append(" LEFT JOIN GLYPH T0 ON T.\"GLYPH_ID1\"=T0.\"ID\"");
            builder.append(" LEFT JOIN GLYPH T1 ON T.\"GLYPH_ID2\"=T1.\"ID\"");
            builder.append(" LEFT JOIN GLYPH T2 ON T.\"GLYPH_ID3\"=T2.\"ID\"");
            builder.append(" LEFT JOIN GLYPH T3 ON T.\"GLYPH_ID4\"=T3.\"ID\"");
            builder.append(" LEFT JOIN GLYPH T4 ON T.\"GLYPH_ID5\"=T4.\"ID\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected GlyphSequence loadCurrentDeep(Cursor cursor, boolean lock) {
        GlyphSequence entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Glyph glyph1 = loadCurrentOther(daoSession.getGlyphDao(), cursor, offset);
         if(glyph1 != null) {
            entity.setGlyph1(glyph1);
        }
        offset += daoSession.getGlyphDao().getAllColumns().length;

        Glyph glyph2 = loadCurrentOther(daoSession.getGlyphDao(), cursor, offset);
        entity.setGlyph2(glyph2);
        offset += daoSession.getGlyphDao().getAllColumns().length;

        Glyph glyph3 = loadCurrentOther(daoSession.getGlyphDao(), cursor, offset);
        entity.setGlyph3(glyph3);
        offset += daoSession.getGlyphDao().getAllColumns().length;

        Glyph glyph4 = loadCurrentOther(daoSession.getGlyphDao(), cursor, offset);
        entity.setGlyph4(glyph4);
        offset += daoSession.getGlyphDao().getAllColumns().length;

        Glyph glyph5 = loadCurrentOther(daoSession.getGlyphDao(), cursor, offset);
        entity.setGlyph5(glyph5);

        return entity;    
    }

    public GlyphSequence loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<GlyphSequence> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<GlyphSequence> list = new ArrayList<GlyphSequence>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<GlyphSequence> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<GlyphSequence> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}