/*    */ package edu.carleton.cas.security;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.security.Key;
/*    */ import javax.crypto.Cipher;
/*    */ import javax.crypto.NoSuchPaddingException;
/*    */ import javax.crypto.spec.SecretKeySpec;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CryptoUtils
/*    */ {
/*    */   private static final String ALGORITHM = "AES";
/*    */   private static final String TRANSFORMATION = "AES";
/*    */   
/*    */   public static void encrypt(String key, File inputFile, File outputFile) throws CryptoException {
/* 30 */     doCrypto(1, key, inputFile, outputFile);
/*    */   }
/*    */   
/*    */   public static void decrypt(String key, File inputFile, File outputFile) throws CryptoException {
/* 34 */     doCrypto(2, key, inputFile, outputFile);
/*    */   }
/*    */   
/*    */   public static void encrypt(String key, InputStream inputFile, OutputStream outputFile) throws CryptoException {
/* 38 */     doCrypto(1, key, inputFile, outputFile);
/*    */   }
/*    */   
/*    */   public static void decrypt(String key, InputStream inputFile, OutputStream outputFile) throws CryptoException {
/* 42 */     doCrypto(2, key, inputFile, outputFile);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws CryptoException {
/*    */     try {
/* 50 */       doCrypto(cipherMode, key, new FileInputStream(inputFile), new FileOutputStream(outputFile));
/* 51 */     } catch (FileNotFoundException|CryptoException ex) {
/* 52 */       throw new CryptoException("Error encrypting/decrypting file", ex);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void doCrypto(int cipherMode, String key, InputStream inputStream, OutputStream outputStream) throws CryptoException {
/*    */     try {
/* 63 */       Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
/* 64 */       Cipher cipher = Cipher.getInstance("AES");
/* 65 */       cipher.init(cipherMode, secretKey);
/* 66 */       byte[] inputBytes = new byte[inputStream.available()];
/* 67 */       inputStream.read(inputBytes);
/* 68 */       byte[] outputBytes = cipher.doFinal(inputBytes);
/* 69 */       outputStream.write(outputBytes);
/* 70 */       inputStream.close();
/* 71 */       outputStream.close();
/*    */     }
/* 73 */     catch (NoSuchPaddingException|java.security.NoSuchAlgorithmException|java.security.InvalidKeyException|javax.crypto.BadPaddingException|javax.crypto.IllegalBlockSizeException|java.io.IOException ex) {
/* 74 */       throw new CryptoException("Error encrypting/decrypting file", ex);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\security\CryptoUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */