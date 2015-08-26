package su.kaoyu.glyph.dao;

import su.kaoyu.glyph.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "TABLE_VERSION".
 */
public class TableVersion {

    /** Not-null value. */
    private String tableName;
    private Integer version;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient TableVersionDao myDao;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public TableVersion() {
    }

    public TableVersion(String tableName) {
        this.tableName = tableName;
    }

    public TableVersion(String tableName, Integer version) {
        this.tableName = tableName;
        this.version = version;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTableVersionDao() : null;
    }

    /** Not-null value. */
    public String getTableName() {
        return tableName;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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
