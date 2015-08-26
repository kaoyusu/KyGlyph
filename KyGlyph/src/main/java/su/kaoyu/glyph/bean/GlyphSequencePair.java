package su.kaoyu.glyph.bean;

public class GlyphSequencePair {
    public int glyphNum;
    public int sameNum;
    public su.kaoyu.glyph.dao.GlyphSequence first;
    public su.kaoyu.glyph.dao.GlyphSequence second;
    public float widthFactor;
    private boolean[] isSame;

    public GlyphSequencePair(su.kaoyu.glyph.dao.GlyphSequence first, su.kaoyu.glyph.dao.GlyphSequence second) {
        if (first == null) {
            throw new IllegalArgumentException();
        }
        glyphNum = first.getGlyphNum();
        isSame = new boolean[glyphNum];
        this.first = first;
        this.second = second;
        sameNum = 0;
        if (this.second == null) {
            widthFactor = glyphNum;
            for (int i = 0; i < glyphNum; i++) {
                isSame[i] = false;
            }
        } else {
            if (first.getGlyphNum() == 0 || first.getGlyphNum() != second.getGlyphNum() || first.equals(second)) {
                throw new IllegalArgumentException();
            }
            boolean isLastSame = true;
            for (int i = 0; i < glyphNum; i++) {
                if (this.first.getGlyphSequence()[i].equals(second.getGlyphSequence()[i])) {
                    sameNum++;
                    widthFactor += isLastSame ? 1f : 1.5f;
                    isLastSame = true;
                    isSame[i] = true;
                } else {
                    if (i == glyphNum - 1) {
                        widthFactor += 1.5f;
                    } else {
                        widthFactor += 1f;
                    }
                    isLastSame = false;
                    isSame[i] = false;
                }
            }
        }
    }

    public float getWidthFactor(int position) {
        if (isSame[position]) {
            return 1f;
        } else {
            if (position == glyphNum - 1 && !isSame[position]) {
                return 1.5f;
            }
            return isSame[position + 1] ? 1.5f : 1f;
        }
    }

    public boolean isSame(int position) {
        return isSame[position];
    }
}
