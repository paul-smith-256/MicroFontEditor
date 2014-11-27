package info.itline.fonteditor.ui;

import info.itline.fonteditor.Glyph;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JPanel;

public class GlyphEditor extends javax.swing.JPanel {
    
    public GlyphEditor() {
        initComponents();
    }
    
    public static interface GlyphEditorEventListener {
        void onGlyphCreated();
    }
    
    public void setEventListener(GlyphEditorEventListener listener) {
        mEventListener = listener;
    }
    
    public void delEventListener() {
        mEventListener= null;
    }
    
    private void glyphModified() {
        mModified = true;
        mDrawingPanel.repaint();
    }
    
    public void moveGlyphUp() {
        if (mGlyph != null) {
            mGlyph.moveUp();
            glyphModified();
        }
    }
    
    public void moveGlyphDown() {
        if (mGlyph != null) {
            mGlyph.moveDown();
            glyphModified();
        }
    }
    
    private void updateControls() {
        boolean isNull = mGlyph == null;
        mCreateResizeButton.setText(isNull ? "Create" : "Change width");
        mGlyphWidthSpinner.setValue(isNull ? 1 : mGlyph.getWidth());
        mMoveLeftButton.setVisible(!isNull);
        mMoveRightButton.setVisible(!isNull);
    }
    
    public void setNewGlyphHeight(int h) {
        mNewGlyphHeight = h;
    }
    
    public int getGlyphIndex() {
        return mGlyphIndex;
    }
    
    public void removeGlyph() {
        setGlyph(null, mGlyph != null);
    }
    
    public void setGlyph(int idx, Glyph g) {
        setGlyph(g, false);
        mGlyphIndex = idx;
    }
    
    private void setGlyph(Glyph g, boolean modified) {
        mGlyph = g;
        updateControls();
        mModified = modified;
        mDrawingPanel.repaint();
    }
    
    public Glyph getGlyph() {
        return mGlyph;
    }
    
    public boolean isModified() {
        return mModified;
    }
    
    public void clearModifiedFlag() {
        mModified = false;
    }
    
    private Glyph mGlyph;
    private boolean mModified;
    private int mGlyphIndex;
    private int mNewGlyphHeight;
    private GlyphEditorEventListener mEventListener;
    
    private class GlyphDrawingPanel extends JPanel {
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (mGlyph != null) {
                mViewport = findViewport();
                mGlyph.render(g, mViewport.x, mViewport.y, getGlyphScale(), PIXEL_BORDER);
            }
        }
        
        private Rectangle findViewport() {
            int pixelSizeX = getWidth() / mGlyph.getWidth();
            int pixelSizeY = getHeight() / mGlyph.getHeight();
            int pixelSize = Math.min(pixelSizeX, pixelSizeY);
            int viewportWidth = pixelSize * mGlyph.getWidth();
            int viewportHeight = pixelSize * mGlyph.getHeight();
            return new Rectangle(
                    (getWidth() - viewportWidth) / 2, 
                    (getHeight() - viewportHeight) / 2,
                    viewportWidth,
                    viewportHeight);
            
        }
        
        private int getGlyphScale(){
            return mViewport.width / mGlyph.getWidth();
        }

        public Rectangle getViewport() {
            return mViewport;
        }
        
        private Rectangle mViewport;
        private static final int PIXEL_BORDER = 5;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        mCreateResizeButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        mGlyphWidthSpinner = new javax.swing.JSpinner();
        mDrawingPanel = new GlyphDrawingPanel();
        mMoveLeftButton = new javax.swing.JButton();
        mMoveRightButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        mCreateResizeButton.setText("Create");
        mCreateResizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mCreateResizeButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Width");

        mDrawingPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mDrawingPanelMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout mDrawingPanelLayout = new javax.swing.GroupLayout(mDrawingPanel);
        mDrawingPanel.setLayout(mDrawingPanelLayout);
        mDrawingPanelLayout.setHorizontalGroup(
            mDrawingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        mDrawingPanelLayout.setVerticalGroup(
            mDrawingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 291, Short.MAX_VALUE)
        );

        mMoveLeftButton.setText("<<");
        mMoveLeftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mMoveLeftButtonActionPerformed(evt);
            }
        });

        mMoveRightButton.setText(">>");
        mMoveRightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mMoveRightButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mDrawingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(mGlyphWidthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mCreateResizeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mMoveLeftButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mMoveRightButton)
                        .addGap(0, 168, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mGlyphWidthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mCreateResizeButton)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mMoveLeftButton)
                    .addComponent(mMoveRightButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mDrawingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void mDrawingPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mDrawingPanelMouseReleased
        if (mGlyph == null) {
            return;
        }
        Rectangle v = ((GlyphDrawingPanel) mDrawingPanel).getViewport();
        int x = evt.getX();
        int y = evt.getY();
        if (!v.contains(x, y)) {
            return;
        }
        x -= v.x;
        y -= v.y;
        try {
            int pixelX = x / (v.width / mGlyph.getWidth());
            int pixelY = mGlyph.getHeight() - y / (v.height / mGlyph.getHeight()) - 1;
            mGlyph.setPixelEnabled(pixelX, pixelY, !mGlyph.isPixelEnabled(pixelX, pixelY));
            mModified = true;
            mDrawingPanel.repaint();
        }
        catch (ArithmeticException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_mDrawingPanelMouseReleased

    private void mCreateResizeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mCreateResizeButtonActionPerformed
        int newWidth = (Integer) mGlyphWidthSpinner.getValue();
        if (mGlyph == null) {
            setGlyph(new Glyph(newWidth, mNewGlyphHeight), true);
            if (mEventListener != null) {
                mEventListener.onGlyphCreated();
            }
        }
        else {
            mGlyph.stretch(newWidth);
            glyphModified();
        }
    }//GEN-LAST:event_mCreateResizeButtonActionPerformed

    private void mMoveLeftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mMoveLeftButtonActionPerformed
        mGlyph.moveLeft();
        glyphModified();
    }//GEN-LAST:event_mMoveLeftButtonActionPerformed

    private void mMoveRightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mMoveRightButtonActionPerformed
        mGlyph.moveRight();
        glyphModified();
    }//GEN-LAST:event_mMoveRightButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton mCreateResizeButton;
    private javax.swing.JPanel mDrawingPanel;
    private javax.swing.JSpinner mGlyphWidthSpinner;
    private javax.swing.JButton mMoveLeftButton;
    private javax.swing.JButton mMoveRightButton;
    // End of variables declaration//GEN-END:variables
}
