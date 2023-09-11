
package s29;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;


/** This is only for pedagogical use. 
 * Please do not consider this realization as a reasonable way to use the 
 * java.security infrastructure.
 */

public class CryptoUtils {
  
  private static final String RSA = "RSA";
  private static final String SHA256WITH_RSA = "SHA256withRSA";
  public static String TOKEN_SEPARATOR = "_";
    
  public static KeyPair newRsaKeyPair()  {
    KeyPairGenerator gen;
    try {
      gen=KeyPairGenerator.getInstance(RSA);
      return gen.generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }
  
  public static String stringFromPublicKey(PublicKey kPub) {   
    if(! (kPub instanceof RSAPublicKey)) throw new IllegalArgumentException();
    RSAPublicKey k = (RSAPublicKey) kPub;
    return k.getPublicExponent()+TOKEN_SEPARATOR+k.getModulus();
  }

  public static String stringFromPrivateKey(PrivateKey kPriv) {
    return hexFromBytes(kPriv.getEncoded());
  }
  
  public static PrivateKey privateKeyFromString(String hex)  {
    byte[] data = bytesFromHex(hex);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(data);
    try {
      return KeyFactory.getInstance(RSA).generatePrivate(spec);
    } catch (NoSuchAlgorithmException |InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static PublicKey publicKeyFromString(String s) {
    String[] parts = s.split(TOKEN_SEPARATOR);
    String exp = parts[0], mod = parts[1];
    RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(mod), new BigInteger(exp));
    try {
      return KeyFactory.getInstance(RSA).generatePublic(spec);
    } catch (NoSuchAlgorithmException |InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }

  public static String signatureFor(String data, PrivateKey kPriv) {
    Signature signer;
    try {
      signer=Signature.getInstance(SHA256WITH_RSA);
      signer.initSign(kPriv);
      signer.update(bytesFromString(data));
      byte[] signature = signer.sign();
      return hexFromBytes(signature);
    } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
      throw new IllegalStateException(e);
    }
  }
  
  public static boolean isSignatureValid(String signature, String data, PublicKey kPub) {
    Signature signer;
    try {
      signer=Signature.getInstance(SHA256WITH_RSA);
      signer.initVerify(kPub);
      signer.update(bytesFromString(data));
      return signer.verify(bytesFromHex(signature));
    } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
      throw new IllegalStateException(e);
    }
  }
  
  private static byte[] bytesFromHex(String hexString) {
    byte[] t = new byte[hexString.length()/2];
    for(int i=0; i<t.length; i++) {
      String s = hexString.substring(2*i, 2*i+2);
      t[i] = (byte)(Integer.parseInt(s, 16));
    }
    return t;
  }

  private static String hexFromBytes(byte[] data) {
    StringBuilder hexString = new StringBuilder();
    for(byte b: data)
      hexString.append(String.format("%02X", 0xff & b));
    return hexString.toString();
  }

  private static byte[] bytesFromString(String data) {
    return data.getBytes(StandardCharsets.UTF_8);
  }

  public static String sha256(String data) {
    byte[] inputBytes = bytesFromString(data);
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("SHA-256");
      inputBytes = data.getBytes(StandardCharsets.UTF_8);
    } catch(NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    byte[] hashBytes = digest.digest(inputBytes);
    return hexFromBytes(hashBytes);
  }

  //-------------------------------------------------------------
  public static void main(String[] args) {
    String data = "hello";
    System.out.println(sha256(data));
    KeyPair kp = newRsaKeyPair();
    System.out.println(kp.getPublic());
    String sign = signatureFor(data, kp.getPrivate());
    System.out.println("*** "+sign);
    System.out.println(isSignatureValid(sign, data, kp.getPublic()));
    String s = hexFromBytes(new byte[] {8,9,11});
    System.out.println();
    System.out.println(Arrays.toString(bytesFromHex(s)));
    String kPriv = stringFromPrivateKey(kp.getPrivate());
    String kPub  = stringFromPublicKey(kp.getPublic());
    System.out.println("kPriv: "+kPriv);
    System.out.println("kPub: "+kPub);
    System.out.println(isSignatureValid(sign, data, publicKeyFromString(kPub)));

    sign = signatureFor(data, privateKeyFromString(kPriv));
    System.out.println("*** "+sign);
    System.out.println(isSignatureValid(sign, data, publicKeyFromString(kPub)));
    System.out.println(isSignatureValid(sign, data, kp.getPublic()));
  }
}
