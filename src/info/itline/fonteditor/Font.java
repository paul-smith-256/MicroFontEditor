package info.itline.fonteditor;

import java.io.Serializable;
import java.nio.charset.Charset;

public class Font implements Serializable {
    
    public Font(int height, String name, Charset charset) {
        mGlyphs = new Glyph[MAX_GLYPH_COUNT];
        mCharsetName = charset.name();
        assert(height > 0 && height <= MAX_FONT_HEIGHT);
        mHeight = height;
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Charset getCharset() {
        return Charset.forName(mCharsetName);
    }

    public void setCharset(Charset charset) {
        mCharsetName = charset.name();
    }
    
    public int getHeight() {
        return mHeight;
    }
    
    public Glyph getGlyph(int i) {
        return mGlyphs[i];
    }
    
    public void setGlyph(int i, Glyph g) {
        assert(g.getHeight() == getHeight());
        if (mGlyphs[i] == null) {
            mGlyphCount += 1;
        }
        mGlyphs[i] = g;
    }
    
    public void removeGlyph(int i) {
        if (mGlyphs[i] != null) {
            mGlyphCount -= 1;
        }
        mGlyphs[i] = null;
    }
    
    public int getGlyphCount() {
        return mGlyphCount;
    }
    
    public Glyph[] getGlyphs() {
        return mGlyphs;
    }
    
    private Glyph[] mGlyphs;
    private int mHeight;
    private int mGlyphCount;
    private String mCharsetName;
    private String mName;
    
    public static final int MAX_GLYPH_COUNT = 256;
    public static final int MAX_FONT_HEIGHT = 64;
    
    private static final long serialVersionUID = 1;
}
