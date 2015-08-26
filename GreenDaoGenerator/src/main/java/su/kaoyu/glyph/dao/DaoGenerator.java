package su.kaoyu.glyph.dao;

import java.io.File;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class DaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "su.kaoyu.glyph.dao");
        schema.enableKeepSectionsByDefault();
        schema.enableActiveEntitiesByDefault();
        addGlyph(schema);
        addTableVersion(schema);

        File file = new File("KyGlyph/src-gen");
        if (!file.exists()) {
            file.mkdirs();
        }
        new de.greenrobot.daogenerator.DaoGenerator().generateAll(schema, "KyGlyph/src-gen");
    }

    private static void addTableVersion(Schema schema) {
        Entity tableVersion = schema.addEntity("TableVersion");
        tableVersion.addStringProperty("tableName").notNull().primaryKey();
        tableVersion.addIntProperty("version");
    }

    private static void addGlyph(Schema schema) {
        Entity glyph = schema.addEntity("Glyph");
        glyph.addImport("de.greenrobot.dao.AbstractDao");
        glyph.addImport("android.graphics.Path");
        glyph.addLongProperty("id").notNull().primaryKey();
        glyph.addStringProperty("glyphOrder").notNull();

        glyph.setSuperclass("su.kaoyu.glyph.bean.Glyph");
        glyph.setConstructors(false);

        addGlyphName(schema, glyph);

        addGlyphSequence(schema, glyph);
    }

    private static void addGlyphName(Schema schema, Entity glyph) {
        Entity glyphName = schema.addEntity("GlyphName");
        glyphName.addImport("de.greenrobot.dao.AbstractDao");
        glyphName.addStringProperty("glyphName").notNull().primaryKey();
        Property glyphId = glyphName.addLongProperty("glyphId").notNull().getProperty();
        glyphName.addToOne(glyph, glyphId);
    }

    private static void addGlyphSequence(Schema schema, Entity glyph) {
        Entity glyphSequence = schema.addEntity("GlyphSequence");
        glyphSequence.addImport("de.greenrobot.dao.AbstractDao");
        glyphSequence.addIdProperty();
        glyphSequence.addIntProperty("glyphNum").notNull();
        Property glyphId1 = glyphSequence.addLongProperty("glyphId1").notNull().getProperty();
        Property glyphId2 = glyphSequence.addLongProperty("glyphId2").getProperty();
        Property glyphId3 = glyphSequence.addLongProperty("glyphId3").getProperty();
        Property glyphId4 = glyphSequence.addLongProperty("glyphId4").getProperty();
        Property glyphId5 = glyphSequence.addLongProperty("glyphId5").getProperty();

        glyphSequence.addToOne(glyph, glyphId1, "glyph1");
        glyphSequence.addToOne(glyph, glyphId2, "glyph2");
        glyphSequence.addToOne(glyph, glyphId3, "glyph3");
        glyphSequence.addToOne(glyph, glyphId4, "glyph4");
        glyphSequence.addToOne(glyph, glyphId5, "glyph5");
    }
}
