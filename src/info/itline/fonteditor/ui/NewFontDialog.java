package info.itline.fonteditor.ui;

import info.itline.fonteditor.Font;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class NewFontDialog extends OkCancelDialog {

    public NewFontDialog(java.awt.Frame parent) {
        super(parent);
        initComponents();
        mFontNameField.getDocument().addDocumentListener(new Validator());
    }
    
    public String getFontName() {
        return mFontName;
    }

    public int getFontHeight() {
        return mFontHeight;
    }
    
    @Override
    protected void finish(boolean approved) {
        mFontHeight = (int) mFontHeightSpinner.getValue();
        mFontName = mFontNameField.getText();
        super.finish(approved);
    }
    
    private class Validator implements DocumentListener {
        
        @Override
        public void changedUpdate(DocumentEvent e) {
            handleChange();
        }
        
        @Override
        public void removeUpdate(DocumentEvent e) {
            handleChange();
        }
        
        @Override
        public void insertUpdate(DocumentEvent e) {
            handleChange();
        }
        
        private void handleChange() {
            mOkButton.setEnabled(!mFontNameField.getText().isEmpty());
        }
    }
    
    private String mFontName;
    private int mFontHeight;
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        mFontHeightSpinner = new javax.swing.JSpinner();
        mFontNameField = new javax.swing.JTextField();
        mCancelButton = new javax.swing.JButton();
        mOkButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New font");

        jLabel1.setText("Name");

        jLabel2.setText("Symbol height");

        mFontHeightSpinner.setModel(new SpinnerNumberModel(1, 1, Font.MAX_FONT_HEIGHT, 1));

        mCancelButton.setText("Cancel");
        mCancelButton.setToolTipText("");
        mCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mCancelButtonActionPerformed(evt);
            }
        });

        mOkButton.setText("OK");
        mOkButton.setEnabled(false);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mFontNameField)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 143, Short.MAX_VALUE)
                                .addComponent(mFontHeightSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(mOkButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mCancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(mFontNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(mFontHeightSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
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
    private javax.swing.JButton mCancelButton;
    private javax.swing.JSpinner mFontHeightSpinner;
    private javax.swing.JTextField mFontNameField;
    private javax.swing.JButton mOkButton;
    // End of variables declaration//GEN-END:variables
}
