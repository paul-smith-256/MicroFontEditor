package info.itline.fonteditor;

import info.itline.helper.EncodingHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;

public final class FontIO {
    
    private FontIO() {
    }
    
    public static Font readFont(String filename) throws IOException {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(filename));
            Object o = in.readObject();
            if (o instanceof Font) {
                return (Font) o;
            }
            else {
                throw new IOException(INVALID_FILE_FORMAT_MESSAGE);
            }
        }
        catch (ClassNotFoundException e) {
            throw new IOException(INVALID_FILE_FORMAT_MESSAGE);
        }
        finally {
            if (in != null) {
                in.close();
            }
        }        
    }
    
    public static void writeFont(Font f, String filename) throws IOException {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(f);
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    public static void exportAsCFile(String filename, Font f) throws IOException {
        if (f.getName().length() > MAX_FONT_NAME_LENGTH) {
            throw new IOException("Font name too long");
        }
        writeCHeader(filename, f);
        writeCFile(filename, f);
    }
    
    private static void writeCHeader(String cFileName, Font f) throws IOException {
        FileWriter out = null;
        try {
            File outFile = new File(new File(cFileName).getParentFile(), 
                    f.getName() + C_HEADER_EXTENSION);
            out = new FileWriter(outFile);
            String guardName = "_" + f.getName().toUpperCase() + "_H_";
            out.append("#ifndef " + guardName + "\n");
            out.append("#define " + guardName + "\n\n");
            out.append("extern " + C_ARRAY_ELEMENT_TYPE + " " + f.getName() + "[];\n\n");
            out.append("#endif // " + guardName);
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    private static void writeCFile(String filename, Font f) throws IOException {
        FileWriter out = null;
        try {
            out = new FileWriter(filename);
        
            out.append("// Data format: little endian\n");
            out.append(C_ARRAY_ELEMENT_TYPE + " " + f.getName() + "[] = {\n");
            out.append("\n\t// Font height\n\t");
            writeCByte((byte) f.getHeight(), out);
            out.append("\n\t// Font version\n\t");
            writeCByte((byte) 1, out);
            out.append("\n\t// Glyph count\n\t");
            writeCShort((short) Font.MAX_GLYPH_COUNT, out);
            out.append("\n\t// Font name\n\t");
            writeCFontName(f, out);

            Glyph emptyGlyph = new Glyph(1, f.getHeight());

            String metadataDescription = 
                    "\n\t// METADATA\n" +
                    "\t// Each four bytes represents one glyph\n" +
                    "\t// Format:\n" +
                    "\t//\tfirst byte - glyph width (column count)\n" +
                    "\t// \tother three bytes - offset of the glyph bitmap (in bytes) in BITMAP section\n\n";
            out.append(metadataDescription);

            int offset = 0;
            int columnByteCount = getByteCountForColumnHeight(f.getHeight());
            Charset cs = f.getCharset();
            for (int i = 0; i < Font.MAX_GLYPH_COUNT; i++) {
                Glyph g = f.getGlyph(i) != null ? f.getGlyph(i) : emptyGlyph;
                out.append("\t/*" + String.format("%03d", i) + "*/ ");
                writeCGlyphMetadata(g, offset, out);
                out.append("\n");
                offset += g.getWidth() * columnByteCount;
            }

            String bitmapDescriptinon = 
                    "\n\t// BITMAP\n" +
                    "\t// Glyph bitmaps are stored column-by-column, each column can be up to 8\n" +
                    "\t// bytes long depending on the font height, first column is the leftmost one.\n" +
                    "\t// Least significant bit of the column represents bottom pixel. That is, first\n" +
                    "\t// bit of the bitmap represents bottom left corner of an image\n\n";
            out.append(bitmapDescriptinon);

            for (int i = 0; i < Font.MAX_GLYPH_COUNT; i++) {
                Glyph g = f.getGlyph(i) != null ? f.getGlyph(i) : emptyGlyph;
                out.append("\t");
                writeCCharRepresentationComment(cs, (byte) i, out);
                writeCGlyphBitmap(g, columnByteCount, out);
                for (int j = g.getHeight() - 1; j >= 0; j--) {
                    out.append("\t// " + g.getRowTextRepresentation(j) + "\n");
                }
                out.append("\n");
            }
            out.append("};\n");
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    private static void writeCByte(byte b, FileWriter w) throws IOException {
        w.append(String.format("0x%02x, ", b));
    }
    
    private static void writeCShort(int v, FileWriter out) throws IOException {
        writeCByte((byte) (v & 0xFF), out);
        writeCByte((byte) ((v >> 8) & 0xFF), out);
    }
    
    private static void writeCFontName(Font f, FileWriter out) throws IOException {
        byte[] nameBytes = f.getName().getBytes(Charset.forName("US-ASCII"));
        for (int i = 0; i < nameBytes.length; i++) {
            writeCByte(nameBytes[i], out);
        }
        for (int i = nameBytes.length; i < MAX_FONT_NAME_LENGTH + 1; i++) {
            writeCByte((byte) 0, out);
        }
        out.append("\n");
    }
    
    private static void writeCGlyphMetadata(Glyph g, int offset, FileWriter out)
            throws IOException {
        writeCByte((byte) g.getWidth(), out);
        for (int i = 0; i < 3; i++) {
            writeCByte((byte) (offset & 0xFF), out);
            offset >>>= 8;
        }
    }
    
    private static void writeCCharRepresentationComment(Charset cs, byte code, FileWriter out)
            throws IOException {
        char c = EncodingHelper.getCharBy8BitCode(code, cs);
        String textRepr;
        if (Character.isISOControl(c)) {
            textRepr = Character.getName((int) c);
        }
        else if (c == '\\') {
            textRepr = "(backslash)";
        }
        else {
            textRepr = "' " + c + " '";
        }
        out.append("// " + textRepr + " (" + (((int) code) & 0xFF) + ")\n");
    }
    
    private static void writeCGlyphBitmap(Glyph g, int columByteCount, FileWriter out) throws IOException {
        for (int i = 0; i < g.getWidth(); i++) {
            out.write("\t");
            writeCGlyphColumn(g.getRow(i), columByteCount, out);
            out.write("\n");
        }
    }
    
    private static void writeCGlyphColumn(long row, int byteCount, FileWriter out) 
            throws IOException {
        for (int i = 0; i < byteCount; i++) {
            writeCByte((byte) (row & 0xFF), out);
            row >>>= 8;
        }
    }
    
    private static int getByteCountForColumnHeight(int height) {
        return height / 8 + (height % 8 != 0 ? 1 : 0);
    }
    
    private static final String C_ARRAY_ELEMENT_TYPE = "const uint8_t";
    private static final int MAX_FONT_NAME_LENGTH = 11;
    private static final String INVALID_FILE_FORMAT_MESSAGE = "Invalid input file format";
    public static final String FONT_FILE_EXTENSION = ".object";
    public static final String C_FILE_EXTENSION = ".c";
    public static final String C_HEADER_EXTENSION = ".h";
}
