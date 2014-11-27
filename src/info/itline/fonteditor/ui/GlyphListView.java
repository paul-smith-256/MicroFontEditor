package info.itline.fonteditor.ui;

import info.itline.helper.EncodingHelper;
import info.itline.fonteditor.Font;
import info.itline.fonteditor.Glyph;
import java.awt.Color;
import java.awt.Component;
import java.nio.charset.Charset;
import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GlyphListView extends javax.swing.JPanel {
    
    public GlyphListView() {
        initComponents();
        mList.setCellRenderer(new GlyphCellRenderer());
        mList.addListSelectionListener(mInternalSelectionListener);
    }
    
    public boolean isModified() {
        return mModified;
    }
    
    public void clearModifiedFlag() {
        mModified = false;
    }
    
    public void setSelectedGlyphIndex(int idx) {
        mList.setSelectedIndex(idx);
        mList.ensureIndexIsVisible(idx);
    }
    
    public int getSelectedGlyphIndex() {
        return mList.getSelectedIndex();
    }
    
    public void setGlyphs(Font f) {
        mFont = f;
        mList.setModel(new UpdatableListModel(f));
        mModified = false;
    }
    
    public void putGlyph(int idx, Glyph g) {
        mFont.setGlyph(idx, g);
        notifyGlyphChanged(idx);
        mModified = true;
    }
    
    public Font getGlyphs() {
        return mFont;
    }
    
    private void notifyGlyphChanged(int i) {
        ((UpdatableListModel) mList.getModel()).updateItem(i);
    }
    
    private void regenerateList() {
        ((UpdatableListModel) mList.getModel()).regenerate();
    }
    
    public void setFontEncoding(Charset c) {
        mFont.setCharset(c);
        mModified = true;
        regenerateList();
    }
    
    public Charset getFontEncoding() {
        return mFont.getCharset();
    }
    
    private class GlyphCellRenderer
            implements ListCellRenderer<Glyph> {
        
        @Override
        public Component getListCellRendererComponent(JList list, Glyph glyph, 
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel result = new JLabel();
            result.setBorder(new EmptyBorder(4, 0, 4, 0));
            result.setOpaque(true);
            result.setIconTextGap(10);
            char c = EncodingHelper.getCharBy8BitCode((byte) index, mFont.getCharset());
            String label = !Character.isISOControl(c) ? 
                    "' " + c + " '" : 
                    Character.getName((int) c);
            label += " (" + Integer.toString(index) + ")";
            result.setText(label);
            if (glyph == null) {
                result.setForeground(Color.RED);
            }
            if (glyph != null) {
                result.setIcon(new ImageIcon(glyph.render(GLYPH_RENDERING_SCALE, 2)));
            }
            if (isSelected) {
                result.setBackground(UIManager.getColor("List.selectionBackground"));
            }
            return result;
        }
        
        private static final int GLYPH_RENDERING_SCALE = 10;
    }
    
    private static class UpdatableListModel extends AbstractListModel<Glyph> {
        
        public UpdatableListModel(Font font) {
            mFont = font;
        }
        
        public void updateItem(int i) {
            fireContentsChanged(this, i, i);
        }
        
        public void regenerate() {
            fireContentsChanged(this, 0, getSize() - 1);
        }
        
        @Override
        public int getSize() {
            return mFont.getGlyphs().length;
        }
        
        @Override
        public Glyph getElementAt(int i) {
            return mFont.getGlyph(i);
        }
        
        private Font mFont;
    }
    
    public void setSelectionListener(SelectionListener sl) {
        mSelectionListener = sl;
    }
    
    public void removeSelectionListener() {
        mSelectionListener = null;
    }
    
    public static interface SelectionListener {
        boolean onItemSelected(int idx, Glyph g);
    }
    
    private Font mFont;
    private int mCurrentItem;
    private SelectionListener mSelectionListener;
    private boolean mModified;
    
    private ListSelectionListener mInternalSelectionListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            int selectedIndex = mList.getSelectedIndex();
            if (selectedIndex == -1) {
                return;
            }
            if (e.getValueIsAdjusting() || selectedIndex == mCurrentItem) {
                return;
            }
            if (mSelectionListener == null) {
                mCurrentItem = selectedIndex;
            }
            else {
                Glyph g = mFont.getGlyph(selectedIndex);
                boolean approved = mSelectionListener.onItemSelected(selectedIndex, g);
                if (!approved) {
                    mList.setSelectedIndex(mCurrentItem);
                }
                else {
                    mCurrentItem = selectedIndex;
                }
            }
        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        mList = new javax.swing.JList();

        mList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        mList.setVisibleRowCount(15);
        jScrollPane1.setViewportView(mList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList mList;
    // End of variables declaration//GEN-END:variables
}
