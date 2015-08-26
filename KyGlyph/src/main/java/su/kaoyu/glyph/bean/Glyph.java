package su.kaoyu.glyph.bean;

import android.graphics.Matrix;
import android.graphics.Path;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static su.kaoyu.glyph.bean.Glyph.GlyphPoint.A;
import static su.kaoyu.glyph.bean.Glyph.GlyphPoint.B;
import static su.kaoyu.glyph.bean.Glyph.GlyphPoint.C;
import static su.kaoyu.glyph.bean.Glyph.GlyphPoint.D;
import static su.kaoyu.glyph.bean.Glyph.GlyphPoint.E;
import static su.kaoyu.glyph.bean.Glyph.GlyphPoint.F;
import static su.kaoyu.glyph.bean.Glyph.GlyphPoint.G;
import static su.kaoyu.glyph.bean.Glyph.GlyphPoint.H;
import static su.kaoyu.glyph.bean.Glyph.GlyphPoint.I;
import static su.kaoyu.glyph.bean.Glyph.GlyphPoint.J;
import static su.kaoyu.glyph.bean.Glyph.GlyphPoint.K;
import static su.kaoyu.glyph.bean.Glyph.GlyphPoint.valueOf;

public class Glyph {
    private static final Map<String, Glyph> glyphs = new HashMap<>();
    private Set<String> glyphOrderSet;

    private static final List<Character> glyphChars = Arrays.asList(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k');

    private static final List<List<GlyphPoint>> lines = Arrays.asList(
            Arrays.asList(A, K, D),
            Arrays.asList(B, H, K, J, E),
            Arrays.asList(C, I, K, G, F),
            Arrays.asList(A, H, C),
            Arrays.asList(A, G, E),
            Arrays.asList(B, I, D),
            Arrays.asList(D, J, F));

    protected static final float DOTS_PADDING = 1 / 15f;
    protected static final float DOTS_SIZE = 0.04f;
    private static final float INPUT_DOTS_SIZE = 0.06f;
    private static final Path outerGlyphPath = new Path();
    private static final Path dotsPath = new Path();
    private static final Path inputDotsPath = new Path();

    static {
        outerGlyphPath.moveTo(A.x, A.y);
        outerGlyphPath.lineTo(B.x, B.y);
        outerGlyphPath.lineTo(C.x, C.y);
        outerGlyphPath.lineTo(D.x, D.y);
        outerGlyphPath.lineTo(E.x, E.y);
        outerGlyphPath.lineTo(F.x, F.y);
        outerGlyphPath.close();
        for (GlyphPoint glyphPoint : GlyphPoint.values()) {
            dotsPath.addCircle(glyphPoint.x, glyphPoint.y, DOTS_SIZE, Path.Direction.CCW);
            inputDotsPath.addCircle(glyphPoint.x, glyphPoint.y, INPUT_DOTS_SIZE, Path.Direction.CCW);
        }
    }

    protected Path glyphPath;

    public static Glyph getGlyph(String glyphOrder) {
        if (!glyphs.containsKey(glyphOrder)) {
            glyphs.put(glyphOrder, new Glyph(glyphOrder));
        }
        return glyphs.get(glyphOrder);
    }

    protected Glyph() {
    }

    protected Glyph(String glyphOrder) {
        init(glyphOrder);
    }

    protected void init(String glyphOrder) {
        if (glyphPath != null) {
            return;
        }
        if (!TextUtils.isEmpty(glyphOrder)) {
            glyphPath = new Path();
            glyphOrderSet = new TreeSet<>();
            glyphOrder = glyphOrder.toLowerCase();
            for (char c : glyphOrder.toCharArray()) {
                if (!glyphChars.contains(c)) {
                    throw new IllegalArgumentException("");
                }
            }

            if (glyphOrder.length() == 1) {
                GlyphPoint p = valueOf(glyphOrder.toUpperCase());
                glyphPath.moveTo(p.x, p.y);
                glyphOrderSet.add(glyphOrder);
            }

            for (int i = 0; i < glyphOrder.length() - 1; i++) {
                char c1 = glyphOrder.charAt(i);
                char c2 = glyphOrder.charAt(i + 1);

                GlyphPoint p1 = valueOf(c1);
                GlyphPoint p2 = valueOf(c2);


                boolean isLine = true;

                for (List<GlyphPoint> line : lines) {
                    if (line.containsAll(Arrays.asList(p1, p2))) {
                        int i1 = line.indexOf(p1);
                        int i2 = line.indexOf(p2);
                        if (Math.abs(i1 - i2) > 1) {
                            isLine = false;
                            for (int j = Math.min(i1, i2); j < Math.max(i1, i2); j++) {
                                c1 = line.get(j).pointName;
                                c2 = line.get(j + 1).pointName;
                                if (c1 > c2) {
                                    char tmp = c2;
                                    c2 = c1;
                                    c1 = tmp;
                                }
                                String s = new String(new char[]{c1, c2});
                                if (!glyphOrderSet.contains(s)) {
                                    glyphOrderSet.add(s);
                                }
                            }
                        }
                    }
                }

                if (i == 0) {
                    glyphPath.moveTo(p1.x, p1.y);
                }
                if (isLine) {
                    if (c1 > c2) {
                        char tmp = c2;
                        c2 = c1;
                        c1 = tmp;
                    } else if (c1 == c2) {
                        continue;
                    }
                    String s = new String(new char[]{c1, c2});
                    if (!glyphOrderSet.contains(s)) {
                        glyphOrderSet.add(s);
                    }

                    glyphPath.lineTo(p2.x, p2.y);
                } else {
                    float lx = Math.abs(p1.x - p2.x);
                    float ly = Math.abs(p1.y - p2.y);
                    double hypotenuse = Math.sqrt(Math.pow(lx, 2) + Math.pow(ly, 2));
                    double l = 0.2d;
                    float vx = (float) (l * ly / hypotenuse);
                    float vy = (float) (l * lx / hypotenuse);
                    if ((p1.x - p2.x) * (p1.y - p2.y) > 0) {
                        glyphPath.quadTo((p1.x + p2.x) * 0.5f - vx, (p1.y + p2.y) * 0.5f + vy, p2.x, p2.y);
                    } else {
                        glyphPath.quadTo((p1.x + p2.x) * 0.5f + vx, (p1.y + p2.y) * 0.5f + vy, p2.x, p2.y);
                    }
                }
            }
        } else {
            glyphPath = new Path();
            glyphOrderSet = new TreeSet<>();
        }
    }

    protected static Path scalePath(Path path, float size, float padding) {
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(size - 2 * padding, size - 2 * padding, 0, 0);
        path.transform(scaleMatrix);
        Matrix translateMatrix = new Matrix();
        translateMatrix.setTranslate(size / 2f, (float) (size / Math.sqrt(3d)));
        path.transform(translateMatrix);
        return path;
    }

    public static Path getOuterPath(int size) {
        return scalePath(new Path(outerGlyphPath), size, 0);
    }

    public static Path getDotsPath(int size) {
        return scalePath(new Path(dotsPath), size, size * DOTS_PADDING);
    }

    public static Path getInputDotsPath(int size, int padding) {
        return scalePath(new Path(inputDotsPath), size, size * INPUT_DOTS_SIZE + padding);
    }

    public Path getGlyphPath(int size) {
        return scalePath(new Path(glyphPath), size, size * DOTS_PADDING);
    }

    public Path getInputGlyphPath(int size, int padding) {
        return scalePath(new Path(glyphPath), size, size * INPUT_DOTS_SIZE + padding);
    }

    public static String checkPoint(int size, int padding, float x, float y) {
        for (GlyphPoint glyphPoint : GlyphPoint.values()) {
            if (glyphPoint.isInCircle(size, padding, x, y)) {
                return glyphPoint.name().toLowerCase();
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Glyph && glyphOrderSet.equals(((Glyph) o).glyphOrderSet);
    }

    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        for (String s : glyphOrderSet) {
            sb.append(s);
        }
        return sb.toString().hashCode();
    }

    protected enum GlyphPoint {
        A(1.0F, 90.0F),
        B(1.0F, 30.0F),
        C(1.0F, 330.0F),
        D(1.0F, 270.0F),
        E(1.0F, 210.0F),
        F(1.0F, 150.0F),
        G(0.5F, 150.0F),
        H(0.5F, 30.0F),
        I(0.5F, 330.0F),
        J(0.5F, 210.0F),
        K(0.0F, 0.0F);

        private final char pointName;
        private final float x;
        private final float y;

        GlyphPoint(float length, float angle) {
            this.pointName = name().toLowerCase().charAt(0);
            this.x = (float) (length / Math.sqrt(3d) * Math.cos(Math.toRadians(angle)));
            this.y = (float) (-length / Math.sqrt(3d) * Math.sin(Math.toRadians(angle)));
        }

        protected static GlyphPoint valueOf(char c) {
            return valueOf(String.format("%s", c).toUpperCase());
        }

        protected boolean isInCircle(int size, int padding, float x, float y) {
            float[] point = {x, y};
            Matrix translateMatrix = new Matrix();
            translateMatrix.setTranslate(-size / 2f, (float) (-size / Math.sqrt(3d)));
            translateMatrix.mapPoints(point);
            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(1f / (size - padding * 2), 1f / (size - padding * 2), 0, 0);
            scaleMatrix.mapPoints(point);
            return isInCircle(point[0], point[1]);
        }

        protected boolean isInCircle(double x, double y) {
            float[] point = {this.x, this.y};
            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(1f - INPUT_DOTS_SIZE * 2, 1f - INPUT_DOTS_SIZE * 2, 0, 0);
            scaleMatrix.mapPoints(point);
            return Math.sqrt(Math.pow(point[0] - x, 2) + Math.pow(point[1] - y, 2)) <= INPUT_DOTS_SIZE;
        }
    }
}
