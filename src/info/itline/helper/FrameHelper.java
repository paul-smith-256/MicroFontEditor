package info.itline.helper;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

public final class FrameHelper {
    
    private FrameHelper() {
    }
    
    public static void moveToScreenCenter(Frame f) {
        Toolkit t = Toolkit.getDefaultToolkit();
        Dimension screenSize = t.getScreenSize();
        Dimension frameSize = f.getSize();
        f.setLocation((int) (screenSize.getWidth() - frameSize.getWidth()) / 2,
                (int) (screenSize.getHeight() - frameSize.getHeight()) / 2);
    }
}
