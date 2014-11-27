package info.itline.helper;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

public final class EncodingHelper {

    private EncodingHelper() {
    }
    
    public static char getCharBy8BitCode(byte code, Charset charset) {
        try {
            return new String(new byte[] {code}, charset).charAt(0);
        }
        catch (UnsupportedCharsetException e) {
            return 0;
        }
    }
}
