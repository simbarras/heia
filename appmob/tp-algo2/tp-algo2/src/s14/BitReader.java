package s14;

import java.io.*;

// Reads any file one bit at a time (most significant bit first)
public class BitReader {
    private final FileInputStream fis;
    private int counter;
    private byte elt;
    private int mask;

    public BitReader(String filename) throws IOException {
        fis = new FileInputStream(filename);
    }

    public void close() throws IOException {
        fis.close();
    }

    public boolean next() throws IOException {
        if (counter == 0) {
            mask = 128;
            elt = fis.readNBytes(1)[0];
        }
        counter = ++counter % 8;
        boolean result = (elt & mask) > 0;
        mask >>= 1;
        return result;
    }

    public boolean isOver() {
        try {
            return 0 >= fis.available() && counter == 0;
        } catch (Exception e) {
            return false;
        }
    }

    //-------------------------------------------
    // Tiny demo...
    public static void main(String[] args) {
        String filename = "a.txt";
        try {
            BitReader b = new BitReader(filename);
            int i = 0;
            while (!b.isOver()) {
                System.out.print(b.next() ? "1" : "0");
                i++;
                if (i % 8 == 0) System.out.print(" ");
                if (i % 80 == 0) System.out.println("");
            }
            b.close();
        } catch (IOException e) {
            throw new RuntimeException("" + e);
        }
    }
}
