package info.itline.fonteditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Glyph implements Serializable {
    
    public Glyph(long[] bitmap, int height) {
        mBitmap = bitmap;
        mHeight = height;
    }
    
    public Glyph(int width, int height) {
        mBitmap = new long[width];
        mHeight = height;
    }
    
    @Override
    public Glyph clone() {
        long[] bitmap = new long[getWidth()];
        System.arraycopy(mBitmap, 0, bitmap, 0, getWidth());
        return new Glyph(bitmap, getHeight());
    }
    
    public String getTextRepresentation() {
        StringBuilder buf = new StringBuilder();
        for (int i = getHeight() - 1; i >= 0; i--) {
            buf.append(getRowTextRepresentation(i));
            buf.append('\n');
        }
        return buf.toString();
    }
    
    public String getRowTextRepresentation(int r) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < getWidth(); i++) {
            buf.append(isPixelEnabled(i, r) ? '#' : '-');
        }
        return buf.toString();
    }
    
    public boolean isPixelEnabled(int x, int y) {
        return (mBitmap[x] & (1 << y)) != 0;
    }
    
    public BufferedImage render(int scale, int border) {
        int imageWidth = getWidth() * scale;
        int imageHeight = getHeight() * scale;
        BufferedImage result = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = result.createGraphics();
        render(g, 0, 0, scale, border);
        return result;
    }
    
    public void render(Graphics g, int offX, int offY, int scale, int border) {
        int imageWidth = getWidth() * scale;
        int imageHeight = getHeight() * scale;
        
        g.setColor(Color.WHITE);
        g.fillRect(offX, offY, imageWidth - 1, imageHeight - 1);
        
        g.setColor(Color.BLACK);
        int pixelSize = scale - 2 * border;
        for (int i = 0; i < getWidth(); i++) {
            int x = i * scale + border + offX;
            for (int j = 0; j < getHeight(); j++) {
                if (isPixelEnabled(i, j)) {
                    int y = (getHeight() - j - 1) * scale + border + offY;
                    g.fillOval(x, y, pixelSize, pixelSize);
                }
            }
        }
        
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 1; i < getWidth(); i++) {
            int x = i * scale + offX;
            g.drawLine(x, offY, x, offY + imageHeight);
        }
        for (int i = 1; i < getHeight(); i++) {
            int y = i * scale + offY;
            g.drawLine(offX, y, imageWidth + offX, y);
        }
        g.drawRect(offX, offY, imageWidth - 1, imageHeight - 1);
    }
    
    public void setPixelEnabled(int x, int y, boolean enabled) {
        if (enabled) {
            mBitmap[x] |= (1 << y);
        }
        else {
            mBitmap[x] &= ~ (1 << y);
        }
    }
    
    public int getHeight() {
        return mHeight;
    }
    
    public int getWidth() {
        return mBitmap.length;
    }
    
    public void stretch(int width) {
        assert(width > 1);
        if (width == getWidth()) {
            return;
        }
        int srcOff, dstOff;
        int count;
        if (width > getWidth()) {
            srcOff = 0;
            dstOff = (width - getWidth()) / 2;
            count = getWidth();
        }
        else {
            srcOff = (getWidth() - width) / 2;
            dstOff = 0;
            count = width;
        }
        long[] newBitmap = new long[width];
        System.arraycopy(mBitmap, srcOff, newBitmap, dstOff, count);
        mBitmap = newBitmap;
    }
    
    public void moveRight() {
        for (int i = getWidth() - 1; i >= 1; i--){
            mBitmap[i] = mBitmap[i - 1];
        }
        mBitmap[0] = 0;
    }
    
    public void moveLeft() {
        for (int i = 0; i < getWidth() - 1; i++) {
            mBitmap[i] = mBitmap[i + 1];
        }
        mBitmap[getWidth() - 1] = 0;
    }
    
    public void moveUp() {
        for (int i = 0; i < getWidth(); i++) {
            mBitmap[i] <<= 1;
        }
    }
    
    public void moveDown() {
        for (int i = 0; i < getWidth(); i++) {
            mBitmap[i] >>>= 1;
        }
    }
    
    long getRow(int r) {
        return mBitmap[r];
    }
    
    private long[] mBitmap;
    private int mHeight;
    
    private static final long serialVersionUID = 1;
}
