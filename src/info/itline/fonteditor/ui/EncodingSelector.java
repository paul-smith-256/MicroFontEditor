package info.itline.fonteditor.ui;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.SortedMap;
import javax.swing.DefaultListModel;

public class EncodingSelector extends OkCancelDialog {
    
    public EncodingSelector(java.awt.Frame parent, Charset currentCharset) {
        super(parent);
        initComponents();
        fillCharsetList(currentCharset);
        pack();
    }
    
    private void fillCharsetList(Charset currentCharset) {
        SortedMap<String, Charset> charsets = Charset.availableCharsets();
        int charsetCount = charsets.size();
        mCharsetNames = new String[charsetCount];
        charsets.keySet().toArray(mCharsetNames);
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String name: mCharsetNames) {
            model.addElement(name);
        }
        int selectedIndex = Arrays.binarySearch(mCharsetNames, currentCharset.name());
        mEncodingList.setModel(model);
        mEncodingList.setSelectedIndex(selectedIndex);
    }
    
    public Charset getSelectedCharset() {
        return mSelectedCharset;
    }
    
    @Override
    protected void finish(boolean approved) {
        mSelectedCharset = Charset.forName(mCharsetNames[mEncodingList.getSelectedIndex()]);
        super.finish(approved);
    } 
    
    private String[] mCharsetNames;
    private Charset mSelectedCharset;
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        mEncodingList = new javax.swing.JList();
        mCancelButton = new javax.swing.JButton();
        javax.swing.JButton mOkButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select encoding");
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);

        mEncodingList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(mEncodingList);

        mCancelButton.setText("Cancel");
        mCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mCancelButtonActionPerformed(evt);
            }
        });

        mOkButton.setText("OK");
        mOkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mOkButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(mOkButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mCancelButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mCancelButton)
                    .addComponent(mOkButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mOkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mOkButtonActionPerformed
        finish(true);
    }//GEN-LAST:event_mOkButtonActionPerformed

    private void mCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mCancelButtonActionPerformed
        finish(false);
    }//GEN-LAST:event_mCancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton mCancelButton;
    private javax.swing.JList mEncodingList;
    // End of variables declaration//GEN-END:variables
}
