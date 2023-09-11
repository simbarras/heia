package s14;

import java.io.*;

// Produces a file one bit at a time, (most significant bit first)  
// filling the last byte with zeros if necessary
public class BitWriter {
    private final FileOutputStream fos;
    private int counter;
    private byte elt;

    public BitWriter(String filename) throws IOException {
        fos = new FileOutputStream(filename);
        counter = 0;
        elt = 0;
    }

    public void close() throws IOException {
        while (counter > 0) {
            put(false);
        }
        fos.close();
    }

    public void put(boolean b) throws IOException {
        elt <<= 1;
        if (b) {
            elt += 1;
        }
        counter = ++counter % 8;
        if (counter == 0) {
            fos.write(elt);
        }
    }

    //-------------------------------------------------------
    // Tiny demo...
    public static void main(String[] args) {
        String filename = "a.txt";
        String bytes = "01000001 01000010 010001";
        //                 A        B        D (01000100)
        try {
            BitWriter b = new BitWriter(filename);
            for (int i = 0; i < bytes.length(); i++) {
                if (bytes.charAt(i) == '1') b.put(true);
                if (bytes.charAt(i) == '0') b.put(false);
            }
            b.close();
        } catch (IOException e) {
            throw new RuntimeException("" + e);
        }
    }
}
