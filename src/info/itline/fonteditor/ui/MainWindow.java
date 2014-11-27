package info.itline.fonteditor.ui;

import info.itline.fonteditor.Font;
import info.itline.fonteditor.FontIO;
import info.itline.fonteditor.Glyph;
import info.itline.helper.DialogHelper;
import info.itline.helper.FrameHelper;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class MainWindow 
        extends javax.swing.JFrame
        implements 
            GlyphListView.SelectionListener
            , GlyphEditor.GlyphEditorEventListener {
    
    public MainWindow() {
        initComponents();
        mGlyphListView.setSelectionListener(this);
        mGlyphEditor.setEventListener(this);
        restoreState();
    }
    
    private void setGlyphs(Font f) {
        int idx = 0;
        Glyph g = f.getGlyph(idx);
        mGlyphListView.setGlyphs(f);
        mGlyphListView.setSelectedGlyphIndex(idx);
        mGlyphEditor.setNewGlyphHeight(f.getHeight());
        changeSelectedGlyph(idx, g);
        updateTitle(f);
    }
    
    private void setFontFileName(String name) {
        mFontFileName = name;
        updateTitle(mGlyphListView.getGlyphs());
    }
    
    private String getFontFileName() {
        return mFontFileName;
    }
    
    private void updateTitle(Font f) {
        String title = TITLE + ": " + f.getName() + " [" + f.getHeight() + "]";
        title += ", " + (getFontFileName() != null ? 
                new File(getFontFileName()).getName() : "New file");
        setTitle(title);
    }
    
    private void saveGlyphFromEditorToList() {
        if (mGlyphEditor.isModified()) {
            mGlyphListView.putGlyph(mGlyphEditor.getGlyphIndex(), mGlyphEditor.getGlyph());
            mGlyphEditor.clearModifiedFlag();
        }
    }
    
    private void changeSelectedGlyph(int idx, Glyph g) {
        mGlyphEditor.setGlyph(idx, g != null ? g.clone() : null);
        mGlyphMenu.setEnabled(g != null);
    }
    
    @Override
    public boolean onItemSelected(int idx, Glyph glyph) {
        if (mGlyphEditor.isModified()) {
            int choise = DialogHelper.confirmYesNoCancel(this, "Save changes?");
            if (choise == DialogHelper.YES) {
                saveGlyphFromEditorToList();
            }
            else if (choise == DialogHelper.CANCEL) {
                return false;
            }
        }
        changeSelectedGlyph(idx, glyph);
        return true;
    }
    
    @Override
    public void onGlyphCreated() {
        mGlyphMenu.setEnabled(true);
    }
    
    private void finish() {
        saveState();
        System.exit(0);
    }
    
    private FileChooserDialogResult showFileChooserDialogAndSavePath(int dialogType, String approveText) {
        JFileChooser d = new JFileChooser();
        d.setDialogType(dialogType);
        if (mLastDirectory != null) {
            d.setCurrentDirectory(new File(mLastDirectory));
        }
        int r = d.showDialog(this, approveText);
        if (r != JFileChooser.APPROVE_OPTION) {
            return new FileChooserDialogResult(false, null);
        }
        File f = d.getSelectedFile();
        mLastDirectory = f.getParentFile().getAbsolutePath();
        return new FileChooserDialogResult(true, f);
    }
    
    private FileChooserDialogResult showSaveDialogAndSavePath() {
        return showFileChooserDialogAndSavePath(JFileChooser.SAVE_DIALOG, "Save");
    }
    
    private FileChooserDialogResult showOpenDialogAndSavePath() {
        return showFileChooserDialogAndSavePath(JFileChooser.OPEN_DIALOG, "Open");
    }
    
    private boolean saveFontToItsFile() {
        if (getFontFileName() != null) {
            return writeFont(getFontFileName());
        }
        else {
            return false;
        }
    }
    
    private boolean saveFontToAnotherFile() {
        FileChooserDialogResult r = showSaveDialogAndSavePath();
        if (!r.isApproved) {
            return true;
        }
        String f = r.selectedFile.getAbsolutePath();
        if (!f.toLowerCase().endsWith(FontIO.FONT_FILE_EXTENSION)) {
            f += FontIO.FONT_FILE_EXTENSION;
        }
        if (writeFont(f)) {
            setFontFileName(f);
            return true;
        }
        else {
            return false;
        }
    }
    
    private boolean writeFont(String filename) {
        try {
            FontIO.writeFont(mGlyphListView.getGlyphs(), filename);
            return true;
        }
        catch (IOException e) {
            DialogHelper.showErrorMessage(this, "Cannot save file");
            return false;
        }
    }
    
    public static Charset getDefaultEncoding() {
        /* try {
            return Charset.forName(DEFAULT_ENCODING_NAME);
        }
        catch (UnsupportedCharsetException e) {
            return Charset.forName("US-ASCII");
        } */
        return Charset.forName("US-ASCII");
    }
    
    private void loadFont(String filename) throws IOException {
        Font f = FontIO.readFont(filename);
        try {
            f.getCharset();
        }
        catch (UnsupportedCharsetException e) {
            throw new IOException("Font encoding is not supported");
        }
        if (f.getName() == null) {
            throw new IOException("Font name is undefined");
        }
        setGlyphs(f);
        setFontFileName(filename);
    }
    
    private boolean loadFontSilent(String filename) {
        try {
            loadFont(filename);
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void restoreState() {
        String fontName = null;
        int selectedGlyphIndex;
        try {
            XMLConfiguration cfg = new XMLConfiguration(STATE_FILE);
            int width = cfg.getInt(WINDOW_WIDTH_PROP, -1);
            int height = cfg.getInt(WINDOW_HEIGHT_PROP, -1);
            int x = cfg.getInt(WINDOW_X_PROP, -1);
            int y = cfg.getInt(WINDOW_Y_PROP, -1);
            if (width == -1 || height == -1 || x == -1 || y == -1) {
                FrameHelper.moveToScreenCenter(this);
            }
            else {
                setLocation(x, y);
                setSize(width, height);
            }
            int dividerPosition = cfg.getInt(DIVIDER_POSITION_PROP, DEFAULT_DIVIDER_POSITION);
            mSplitPane.setDividerLocation(dividerPosition);
            fontName = cfg.getString(FONT_FILE_PROP);
            mLastDirectory = cfg.getString(LAST_DIRECTORY_PROP);
            selectedGlyphIndex = cfg.getInt(SELECTED_GLYPH_INDEX_PROP, 0);
            mGlyphListView.setSelectedGlyphIndex(selectedGlyphIndex);
        }
        catch (ConfigurationException e) {
            FrameHelper.moveToScreenCenter(this);
        }
        boolean fontLoaded = fontName != null && loadFontSilent(fontName);
        if (!fontLoaded) {
            setGlyphs(new Font(DEFAULT_FONT_SIZE, DEFAULT_FONT_NAME, getDefaultEncoding()));
        }
    }
    
    private void saveState() {
        try {
            XMLConfiguration cfg = new XMLConfiguration();
            cfg.addProperty(WINDOW_WIDTH_PROP, getWidth());
            cfg.addProperty(WINDOW_HEIGHT_PROP, getHeight());
            Point p = getLocation();
            cfg.addProperty(WINDOW_X_PROP, p.x);
            cfg.addProperty(WINDOW_Y_PROP, p.y);
            cfg.addProperty(DIVIDER_POSITION_PROP, mSplitPane.getDividerLocation());
            if (getFontFileName() != null) {
                cfg.addProperty(FONT_FILE_PROP, getFontFileName());
            }
            if (mLastDirectory != null) {
                cfg.addProperty(LAST_DIRECTORY_PROP, mLastDirectory);
            }
            cfg.addProperty(SELECTED_GLYPH_INDEX_PROP, mGlyphListView.getSelectedGlyphIndex());
            cfg.save(STATE_FILE);
        }
        catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
    
    private String mFontFileName;
    private String mLastDirectory;
    
    private static final String
            WINDOW_WIDTH_PROP = "windowWidth",
            WINDOW_HEIGHT_PROP = "windowHeight",
            WINDOW_X_PROP = "windowX",
            WINDOW_Y_PROP = "windowY",
            DIVIDER_POSITION_PROP = "dividerPosition",
            FONT_FILE_PROP = "fontFile",
            LAST_DIRECTORY_PROP = "lastDirectory",
            SELECTED_GLYPH_INDEX_PROP = "selectedGlyphIndex";
    private static final int DEFAULT_DIVIDER_POSITION = 200;
    private static final String STATE_FILE = "state.xml";
    
    private static final String DEFAULT_ENCODING_NAME = "CP1251";
    private static final int DEFAULT_FONT_SIZE = 16;
    private static final String DEFAULT_FONT_NAME = "Font";
    
    private static final String TITLE = "Font Editor";
    
    private static class FileChooserDialogResult {
        
        public FileChooserDialogResult(boolean isApproved, File selectedFile) {
            this.isApproved = isApproved;
            this.selectedFile = selectedFile;
        }
        
        final boolean isApproved;
        final File selectedFile;
    }
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JMenu jMenu1 = new javax.swing.JMenu();
        mSplitPane = new javax.swing.JSplitPane();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        mGlyphEditor = new info.itline.fonteditor.ui.GlyphEditor();
        mGlyphListView = new info.itline.fonteditor.ui.GlyphListView();
        javax.swing.JMenuBar jMenuBar1 = new javax.swing.JMenuBar();
        javax.swing.JMenu mFileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem mNewFileItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem mOpenFileItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem mSaveFileItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem mSaveFileAsButton = new javax.swing.JMenuItem();
        javax.swing.JMenuItem mExportItem = new javax.swing.JMenuItem();
        mGlyphMenu = new javax.swing.JMenu();
        mRemoveGlyphItem = new javax.swing.JMenuItem();
        mMoveGlyphDownItem = new javax.swing.JMenuItem();
        mMoveGlyphUpItem = new javax.swing.JMenuItem();
        javax.swing.JMenu mEncodingMenu = new javax.swing.JMenu();
        mSelectEncodingItem = new javax.swing.JMenuItem();

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Font Editor");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        mSplitPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        mSplitPane.setDividerLocation(250);
        mSplitPane.setResizeWeight(0.1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mGlyphEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mGlyphEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
                .addContainerGap())
        );

        mSplitPane.setRightComponent(jPanel1);
        mSplitPane.setLeftComponent(mGlyphListView);

        mFileMenu.setText("File");

        mNewFileItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        mNewFileItem.setText("New...");
        mNewFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mNewFileItemActionPerformed(evt);
            }
        });
        mFileMenu.add(mNewFileItem);

        mOpenFileItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mOpenFileItem.setText("Open...");
        mOpenFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mOpenFileItemActionPerformed(evt);
            }
        });
        mFileMenu.add(mOpenFileItem);

        mSaveFileItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mSaveFileItem.setText("Save");
        mSaveFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mSaveFileItemActionPerformed(evt);
            }
        });
        mFileMenu.add(mSaveFileItem);

        mSaveFileAsButton.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        mSaveFileAsButton.setText("Save as...");
        mSaveFileAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mSaveFileAsButtonActionPerformed(evt);
            }
        });
        mFileMenu.add(mSaveFileAsButton);

        mExportItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        mExportItem.setText("Export...");
        mExportItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mExportItemActionPerformed(evt);
            }
        });
        mFileMenu.add(mExportItem);

        jMenuBar1.add(mFileMenu);

        mGlyphMenu.setText("Glyph");

        mRemoveGlyphItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        mRemoveGlyphItem.setText("Remove");
        mRemoveGlyphItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mRemoveGlyphItemActionPerformed(evt);
            }
        });
        mGlyphMenu.add(mRemoveGlyphItem);

        mMoveGlyphDownItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        mMoveGlyphDownItem.setText("Shift down");
        mMoveGlyphDownItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mMoveGlyphDownItemActionPerformed(evt);
            }
        });
        mGlyphMenu.add(mMoveGlyphDownItem);

        mMoveGlyphUpItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        mMoveGlyphUpItem.setText("Shift up");
        mMoveGlyphUpItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mMoveGlyphUpItemActionPerformed(evt);
            }
        });
        mGlyphMenu.add(mMoveGlyphUpItem);

        jMenuBar1.add(mGlyphMenu);

        mEncodingMenu.setText("Encoding");

        mSelectEncodingItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        mSelectEncodingItem.setText("Select");
        mSelectEncodingItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mSelectEncodingItemActionPerformed(evt);
            }
        });
        mEncodingMenu.add(mSelectEncodingItem);

        jMenuBar1.add(mEncodingMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 844, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mSplitPane)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mSelectEncodingItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mSelectEncodingItemActionPerformed
        EncodingSelector es = new EncodingSelector(this, mGlyphListView.getFontEncoding());
        es.setVisible(true);
        if (es.isApproved()) {
            mGlyphListView.setFontEncoding(es.getSelectedCharset());
        }
    }//GEN-LAST:event_mSelectEncodingItemActionPerformed

    private void mRemoveGlyphItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mRemoveGlyphItemActionPerformed
        mGlyphEditor.removeGlyph();
        mGlyphMenu.setEnabled(false);
    }//GEN-LAST:event_mRemoveGlyphItemActionPerformed

    private void mMoveGlyphDownItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mMoveGlyphDownItemActionPerformed
        mGlyphEditor.moveGlyphDown();
    }//GEN-LAST:event_mMoveGlyphDownItemActionPerformed

    private void mMoveGlyphUpItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mMoveGlyphUpItemActionPerformed
        mGlyphEditor.moveGlyphUp();
    }//GEN-LAST:event_mMoveGlyphUpItemActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (mGlyphEditor.isModified() || mGlyphListView.isModified()) {
            int k = DialogHelper.confirmYesNoCancel(this, "Write changes to file?");
            if (k == DialogHelper.CANCEL) {
                return;
            }
            else if (k == DialogHelper.YES) {
                saveGlyphFromEditorToList();
                if (saveFontToItsFile()) {
                    finish();
                }
                while (!saveFontToAnotherFile())
                    ;
            }
        }
        finish();
    }//GEN-LAST:event_formWindowClosing

    private void mOpenFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mOpenFileItemActionPerformed
        FileChooserDialogResult r = showOpenDialogAndSavePath();
        if (r.isApproved) {
            File f = r.selectedFile;
            try {
                loadFont(f.getAbsoluteFile().toString());
            }
            catch (IOException e) {
                DialogHelper.showErrorMessage(this, "Cannot open file");
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_mOpenFileItemActionPerformed

    private void mNewFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mNewFileItemActionPerformed
        NewFontDialog d = new NewFontDialog(this);
        d.setVisible(true);
        if (d.isApproved()) {
            setGlyphs(new Font(d.getFontHeight(), d.getFontName(), getDefaultEncoding()));
            setFontFileName(null);
        }
    }//GEN-LAST:event_mNewFileItemActionPerformed

    private void mSaveFileAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mSaveFileAsButtonActionPerformed
        saveGlyphFromEditorToList();
        if (saveFontToAnotherFile()) {
            mGlyphListView.clearModifiedFlag();
        }
    }//GEN-LAST:event_mSaveFileAsButtonActionPerformed

    private void mSaveFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mSaveFileItemActionPerformed
        saveGlyphFromEditorToList();
        boolean saved = saveFontToItsFile();
        if (!saved) {
            saved = saveFontToAnotherFile();
        }
        if (saved) {
            mGlyphListView.clearModifiedFlag();
        }
    }//GEN-LAST:event_mSaveFileItemActionPerformed

    private void mExportItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mExportItemActionPerformed
        FileChooserDialogResult r = showSaveDialogAndSavePath();
        if (!r.isApproved) {
            return;
        }
        String f = r.selectedFile.getAbsolutePath();
        if (!f.toLowerCase().endsWith(FontIO.C_FILE_EXTENSION)) {
            f += FontIO.C_FILE_EXTENSION;
        }
        try {
            saveGlyphFromEditorToList();
            FontIO.exportAsCFile(f, mGlyphListView.getGlyphs());
        }
        catch (IOException e) {
            DialogHelper.showErrorMessage(this, "Cannot export file: " + e.getLocalizedMessage());
        }
    }//GEN-LAST:event_mExportItemActionPerformed

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | 
                InstantiationException | IllegalAccessException e) {    
        } 
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                MainWindow mainWindow = new MainWindow();
                mainWindow.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private info.itline.fonteditor.ui.GlyphEditor mGlyphEditor;
    private info.itline.fonteditor.ui.GlyphListView mGlyphListView;
    private javax.swing.JMenu mGlyphMenu;
    private javax.swing.JMenuItem mMoveGlyphDownItem;
    private javax.swing.JMenuItem mMoveGlyphUpItem;
    private javax.swing.JMenuItem mRemoveGlyphItem;
    private javax.swing.JMenuItem mSelectEncodingItem;
    private javax.swing.JSplitPane mSplitPane;
    // End of variables declaration//GEN-END:variables
}
