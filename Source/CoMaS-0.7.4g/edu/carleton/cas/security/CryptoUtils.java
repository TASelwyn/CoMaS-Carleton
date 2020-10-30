package edu.carleton.cas.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
  private static final String ALGORITHM = "AES";
  
  private static final String TRANSFORMATION = "AES";
  
  public static void encrypt(String key, File inputFile, File outputFile) throws CryptoException {
    doCrypto(1, key, inputFile, outputFile);
  }
  
  public static void decrypt(String key, File inputFile, File outputFile) throws CryptoException {
    doCrypto(2, key, inputFile, outputFile);
  }
  
  public static void encrypt(String key, InputStream inputFile, OutputStream outputFile) throws CryptoException {
    doCrypto(1, key, inputFile, outputFile);
  }
  
  public static void decrypt(String key, InputStream inputFile, OutputStream outputFile) throws CryptoException {
    doCrypto(2, key, inputFile, outputFile);
  }
  
  private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws CryptoException {
    try {
      doCrypto(cipherMode, key, new FileInputStream(inputFile), new FileOutputStream(outputFile));
    } catch (FileNotFoundException|CryptoException ex) {
      throw new CryptoException("Error encrypting/decrypting file", ex);
    } 
  }
  
  public static void doCrypto(int cipherMode, String key, InputStream inputStream, OutputStream outputStream) throws CryptoException {
    try {
      Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(cipherMode, secretKey);
      byte[] inputBytes = new byte[inputStream.available()];
      inputStream.read(inputBytes);
      byte[] outputBytes = cipher.doFinal(inputBytes);
      outputStream.write(outputBytes);
      inputStream.close();
      outputStream.close();
    } catch (NoSuchPaddingException|java.security.NoSuchAlgorithmException|java.security.InvalidKeyException|javax.crypto.BadPaddingException|javax.crypto.IllegalBlockSizeException|java.io.IOException ex) {
      throw new CryptoException("Error encrypting/decrypting file", ex);
    } 
  }
}
