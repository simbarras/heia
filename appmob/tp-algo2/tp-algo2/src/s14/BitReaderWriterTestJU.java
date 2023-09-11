package s14;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

/**  @author alexandr.peclat  */
public class BitReaderWriterTestJU {

  /** Temporary files names */
  private static final String TMP_FOLDER="tmp";
  private static final String FILE_WRITE11 = TMP_FOLDER+"/write11";
  private static final String FILE_WRITE32 = TMP_FOLDER+"/write32";
  private static final String FILE_WRITE27 = TMP_FOLDER+"/write27";
  private static final String FILE_READ32 = TMP_FOLDER+"/read32";
  
  /** Tested content, array of 32 bits. */
  static final boolean[] CONTENT_BITS_ORIGINAL = new boolean[]{
    false, true, true, false, false, true, true, true, 
    false, true, false, true, true, true, false, true, 
    true, true, true, true, true, true, false, true, 
    false, false, true, false, true, true, false, true};

  /** Expected content after 32 bits written, without close operation.  */
  private static final byte[] CONTENT_BYTES_ORIGINAL = new byte[]{0x67, 0x5d, (byte) 0xfd, 0x2d};

  /** Expected content after 27 bits written, with close operation (last byte completed by zeros). */
  private static final byte[] CONTENT_BYTES_COMPLETED = new byte[]{0x67, 0x5d, (byte) 0xfd, 0x20};
  
  @BeforeClass public static void setUpBeforeClass() throws Exception {
    File tempDir = new File(TMP_FOLDER);
    tempDir.mkdir();
  }

  @Test public void testBitWriter32bits() throws IOException {
    //Write 32 bits to file
    BitWriter writer32 = new BitWriter(FILE_WRITE32);
    for(int i=0; i < 32; i++) writer32.put(CONTENT_BITS_ORIGINAL[i]);
    writer32.close();
    
    //Read bytes from file and compare with expected results
    byte[] bytes = Utils.readBytesFromFile(FILE_WRITE32);
    String msg="Wrong number of written bytes.";
    assertEquals(msg, CONTENT_BYTES_ORIGINAL.length, bytes.length);
    msg="Wrong written byte contents";
    assertArrayEquals(msg, CONTENT_BYTES_ORIGINAL, bytes);
  }
  
  @Test public void testBitWriter11zeroBits() throws IOException {
    //Write 16 bits to file
    boolean[] the11ZeroBits = {
        false, false, false, false, false, false, false, false,
        false, false, false
    };
    byte[] theTwoZeroBytes = {0,0};
    BitWriter writer11 = new BitWriter(FILE_WRITE11);
    for (boolean the11ZeroBit : the11ZeroBits) writer11.put(the11ZeroBit);
    writer11.close();
    
    //Read bytes from file and compare with expected results
    byte[] bytes = Utils.readBytesFromFile(FILE_WRITE11);
    String msg="Wrong number of written bytes.";
    assertEquals(msg, theTwoZeroBytes.length, bytes.length);
    msg="Wrong written byte contents";
    assertArrayEquals(msg, theTwoZeroBytes, bytes);
  }

  @Test public void testBitWriter27bits() throws IOException {
    //Write 27 bits to file
    BitWriter writer27 = new BitWriter(FILE_WRITE27);
    for(int i=0; i < 27; i++) 
      writer27.put(CONTENT_BITS_ORIGINAL[i]);
    writer27.close();
    //Read bytes from file and compare with expected results
    byte[]  bytes = Utils.readBytesFromFile(FILE_WRITE27);
    String msg="Wrong number of written bytes.";
    assertEquals(msg, CONTENT_BYTES_COMPLETED.length, bytes.length);
    msg="Wrong written byte contents";
    assertArrayEquals(msg, CONTENT_BYTES_COMPLETED, bytes);
  }
  
  /** Test method for {@link BitReader#next()}.
   *  @throws IOException  */
  @Test public void testBitReader() throws IOException {
    String msg="";
    //Write the 4 bytes test content array
    Utils.writeBytesToFile(FILE_READ32, CONTENT_BYTES_ORIGINAL);
    
    //Read the file with BitReader
    BitReader reader = new BitReader(FILE_READ32);
    boolean[] actuals = new boolean[CONTENT_BITS_ORIGINAL.length];
    //while(!reader.isOver()){
    for(int ir=0; ir<actuals.length; ir++) {
      assertFalse(reader.isOver());
      assertFalse(reader.isOver() || reader.isOver()); // just to see if isOver() changes the state...
      actuals[ir] = reader.next();
    }
    assertTrue(reader.isOver());
    assertTrue(reader.isOver() && reader.isOver()); 

    //Compare read bits with original content
    msg="Wrong read sequence";
    Utils.assertArrayEquals(msg, CONTENT_BITS_ORIGINAL, actuals);
  }
  
  //=====================================================================
  /**  Some utility methods used by the tests.
   *   @author alexandr.peclat  */
  static class Utils{
    
    /**
     * Read bytes of a given file.
     * @param file            Path to the file to read.
     * @return                Contained data as an array of bytes.
     * @throws IOException    If the file cannot be found or opened.
     */
    public static byte[] readBytesFromFile(String file) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      InputStream is = new FileInputStream(file);
      byte[] temp = new byte[1024];
      int read;
      while((read = is.read(temp)) > 0){
         buffer.write(temp, 0, read);
      }
      is.close();
      return buffer.toByteArray();
    }
    
    /**
     * Write a byte array to a given file.
     * @param file          Path to the file to write.
     * @param bytes         Bytes array to write.
     * @throws IOException  If the file cannot be found or written.
     */
    public static void writeBytesToFile(String file, byte[] bytes) throws IOException{
       FileOutputStream fos = new FileOutputStream(file);
       fos.write(bytes);
       fos.close();
    }
    
    /**
     * Asserts that two arrays of booleans are equals (same content). 
     * Replace the original method by displaying bit-content in the error message.
     * @param message     Displayed message if the assertion fails.
     * @param expecteds   Expected result array.
     * @param actuals     Actual result array.
     */
    public static void assertArrayEquals(String message, boolean[] expecteds, boolean[] actuals){
      String msg=message 
          + "; expected:<" + toStringBitwise(expecteds, " ") 
          + "> but was:<" + toStringBitwise(actuals, " ") + ">";
      assertTrue(msg, Arrays.equals(expecteds, actuals));
    }
    
    /**
     * Convert an array of booleans to a string with bitwise listed content.
     * @param bits              The array to convert.
     * @param bytesSeparator    Separator between bytes (each group of 8 bits). Can be empty but not null ("").
     * @return                  String listing the bitwise content of the array.
     */
    public static String toStringBitwise(boolean[] bits, String bytesSeparator){
      String s = "";
      for(int i=0; i < bits.length; i++){
        if(i % 8 == 0 && i != 0) s += bytesSeparator;
        s += bits[i] ? "1":"0";
      }
      return s;
    }

    /**
     * Asserts that two arrays of byte are equals (same content). 
     * Replace the original method by displaying hexadecimal content in the error message.
     * @param message     Displayed message if the assertion fails.
     * @param expecteds   Expected result array.
     * @param actuals     Actual result array.
     */
    public static void assertArrayEquals(String message, byte[] expecteds, byte[] actuals){
      String msg=message 
          + "; expected:<" + toStringHex(expecteds) 
          + "> but was:<" + toStringHex(actuals) + ">";
      assertTrue(msg, Arrays.equals(expecteds, actuals));
    }
    
    /**
     * Convert an array of byte to a string with content listed under hexadecimal form.
     * @param array   The array to convert.
     * @return        String listing the content of the array in hexadecimal.
     */
    public static String toStringHex(byte[] array){
      String s = "[";
      for(int i=0; i < array.length; i++){
        if(i < array.length - 1) s += ", ";
        s += String.format("%x", array[i]);
      }
      return s + "]";
    }
  }

}
