package info.itline.fonteditor.ui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

public class OkCancelDialog extends JDialog {

    public OkCancelDialog(Frame parent) {
        super(parent, true);
        setLocationRelativeTo(parent);
        addKeyboardListener(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), false);
        addKeyboardListener(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), true);
    }
    
    public boolean isApproved() {
        return mApproved;
    }
    
    protected void setApproved(boolean v) {
        mApproved = v;
    }
    
    protected void finish(boolean approved) {
        setApproved(approved);
        setVisible(false);
        dispose();
    }
    
    private void addKeyboardListener(KeyStroke ks, final boolean approved) {
        ActionListener listener = new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                finish(approved);
            }
        };

        getRootPane().registerKeyboardAction(listener, ks,
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    private boolean mApproved;
}
