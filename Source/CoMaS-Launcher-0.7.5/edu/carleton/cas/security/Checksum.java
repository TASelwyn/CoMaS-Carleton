/*    */ package edu.carleton.cas.security;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.InputStream;
/*    */ import java.security.MessageDigest;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Checksum
/*    */ {
/*    */   public static byte[] createChecksum(String filename, String algorithm) throws Exception {
/*    */     int numRead;
/* 15 */     InputStream fis = new FileInputStream(filename);
/*    */     
/* 17 */     byte[] buffer = new byte[1024];
/* 18 */     MessageDigest complete = MessageDigest.getInstance(algorithm);
/*    */ 
/*    */     
/*    */     do {
/* 22 */       numRead = fis.read(buffer);
/* 23 */       if (numRead <= 0)
/* 24 */         continue;  complete.update(buffer, 0, numRead);
/*    */     }
/* 26 */     while (numRead != -1);
/*    */     
/* 28 */     fis.close();
/* 29 */     return complete.digest();
/*    */   }
/*    */   
/*    */   private static String byteToString(byte[] bytes) {
/* 33 */     StringBuilder sb = new StringBuilder();
/* 34 */     for (int i = 0; i < bytes.length; i++) {
/* 35 */       sb.append(Integer.toString((bytes[i] & 0xFF) + 256, 16).substring(1));
/*    */     }
/* 37 */     return sb.toString();
/*    */   }
/*    */   
/*    */   public static String getMD5Checksum(String filename) throws Exception {
/* 41 */     byte[] b = createChecksum(filename, "MD5");
/* 42 */     return byteToString(b);
/*    */   }
/*    */   
/*    */   public static String getSHA1Checksum(String filename) throws Exception {
/* 46 */     byte[] b = createChecksum(filename, "SHA-1");
/* 47 */     return byteToString(b);
/*    */   }
/*    */   
/*    */   public static String getSHA256Checksum(String filename) throws Exception {
/* 51 */     byte[] b = createChecksum(filename, "SHA-256");
/* 52 */     return byteToString(b);
/*    */   }
/*    */   
/*    */   public static String getSHA512Checksum(String filename) throws Exception {
/* 56 */     byte[] b = createChecksum(filename, "SHA-512");
/* 57 */     return byteToString(b);
/*    */   }
/*    */   
/*    */   public static void main(String[] args) throws Exception {
/*    */     String file;
/* 62 */     if (args.length > 0) {
/* 63 */       file = args[0];
/*    */     } else {
/* 65 */       file = String.valueOf(System.getProperty("user.home")) + File.separator + "Desktop" + File.separator + "workspace-latest.zip";
/*    */     } 
/* 67 */     System.out.println("Computing hash for " + file + " (" + (new File(file)).length() + ")");
/*    */     
/* 69 */     System.out.println("    MD5: " + getMD5Checksum(file));
/* 70 */     System.out.println("  SHA-1: " + getSHA1Checksum(file));
/* 71 */     System.out.println("SHA-256: " + getSHA256Checksum(file));
/* 72 */     System.out.println("SHA-512: " + getSHA512Checksum(file));
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\cas\security\Checksum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */