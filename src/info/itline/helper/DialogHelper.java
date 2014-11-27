package info.itline.helper;



import javax.swing.JFrame;
import javax.swing.JOptionPane;

public final class DialogHelper {
    
    private DialogHelper() {
        
    }
    
    public static void showErrorMessage(JFrame root, String text) {
        JOptionPane.showMessageDialog(root, text, "Message", JOptionPane.ERROR_MESSAGE);
    }
    
    public static int confirmYesNoCancel(JFrame root, String text) {
        return JOptionPane.showConfirmDialog(root, text, "Confirm", 
                JOptionPane.YES_NO_CANCEL_OPTION);
    }
    
    public static boolean confirmOkCancel(JFrame root, String text) {
        return JOptionPane.showConfirmDialog(root, text, "Confirm", 
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
    }
    
    public static final int 
            YES = JOptionPane.YES_OPTION,
            NO = JOptionPane.NO_OPTION,
            CANCEL = JOptionPane.CANCEL_OPTION;
}
