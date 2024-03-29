package su.kaoyu.glyph.dao;

import su.kaoyu.glyph.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import de.greenrobot.dao.AbstractDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "GLYPH_NAME".
 */
public class GlyphName {

    /** Not-null value. */
    private String glyphName;
    private long glyphId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient GlyphNameDao myDao;

    private Glyph glyph;
    private Long glyph__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public GlyphName() {
    }

    public GlyphName(String glyphName) {
        this.glyphName = glyphName;
    }

    public GlyphName(String glyphName, long glyphId) {
        this.glyphName = glyphName;
        this.glyphId = glyphId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getGlyphNameDao() : null;
    }

    /** Not-null value. */
    public String getGlyphName() {
        return glyphName;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setGlyphName(String glyphName) {
        this.glyphName = glyphName;
    }

    public long getGlyphId() {
        return glyphId;
    }

    public void setGlyphId(long glyphId) {
        this.glyphId = glyphId;
    }

    /** To-one relationship, resolved on first access. */
    public Glyph getGlyph() {
        long __key = this.glyphId;
        if (glyph__resolvedKey == null || !glyph__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GlyphDao targetDao = daoSession.getGlyphDao();
            Glyph glyphNew = targetDao.load(__key);
            synchronized (this) {
                glyph = glyphNew;
            	glyph__resolvedKey = __key;
            }
        }
        return glyph;
    }

    public void setGlyph(Glyph glyph) {
        if (glyph == null) {
            throw new DaoException("To-one property 'glyphId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.glyph = glyph;
            glyphId = glyph.getId();
            glyph__resolvedKey = glyphId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
