/*     */ package edu.carleton.cas.utility;
/*     */ 
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Base64;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Password
/*     */ {
/*     */   public static void main(String[] args) throws NoSuchAlgorithmException {
/*  18 */     String passwordToHash = "password";
/*  19 */     byte[] salt = getSalt();
/*  20 */     String securePassword = getSecurePassword(passwordToHash, salt);
/*  21 */     System.out.println(securePassword);
/*  22 */     System.out.println(byteToString(salt));
/*     */     
/*  24 */     byte[] mySalt = stringToByte(byteToString(salt));
/*  25 */     String myPassword = getSecurePassword(passwordToHash, mySalt);
/*  26 */     System.out.println(securePassword.equals(myPassword));
/*  27 */     String ucPassword = getSecurePassword("PASSWORD", salt);
/*  28 */     System.out.println(String.valueOf(ucPassword) + " " + ucPassword.length());
/*  29 */     System.out.println(securePassword.equals(ucPassword));
/*  30 */     System.out.println(authenticator("userid", "password", "fishy@fishface.ca"));
/*     */ 
/*     */     
/*  33 */     System.out.println("Passcode = " + getPassCode(8));
/*  34 */     System.out.println("Default Passcode = " + getPassCode());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String authenticator(String userid, String passwordToHash) throws NoSuchAlgorithmException {
/*  47 */     byte[] salt = getSalt();
/*  48 */     String securePassword = getSecurePassword(passwordToHash, salt);
/*  49 */     return String.valueOf(userid) + "," + securePassword + "," + byteToString(salt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String authenticator(String userid, String passwordToHash, String other) throws NoSuchAlgorithmException {
/*  62 */     byte[] salt = getSalt();
/*  63 */     String securePassword = getSecurePassword(passwordToHash, salt);
/*  64 */     return String.valueOf(userid) + "," + securePassword + "," + other + "," + byteToString(salt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String byteToString(byte[] input) {
/*  73 */     return Base64.getEncoder().encodeToString(input);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] stringToByte(String input) {
/*  82 */     return Base64.getDecoder().decode(input);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getSecurePassword(String passwordToHash, byte[] salt) {
/*  92 */     String generatedPassword = null;
/*     */     try {
/*  94 */       MessageDigest md = MessageDigest.getInstance("SHA-512");
/*  95 */       md.update(salt);
/*  96 */       byte[] bytes = md.digest(passwordToHash.getBytes());
/*  97 */       StringBuilder sb = new StringBuilder();
/*  98 */       for (int i = 0; i < bytes.length; i++) {
/*  99 */         sb.append(Integer.toString((bytes[i] & 0xFF) + 256, 16).substring(1));
/*     */       }
/* 101 */       generatedPassword = sb.toString();
/* 102 */     } catch (NoSuchAlgorithmException e) {
/* 103 */       e.printStackTrace();
/*     */     } 
/* 105 */     return generatedPassword;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] getSalt() throws NoSuchAlgorithmException {
/* 114 */     SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
/* 115 */     byte[] salt = new byte[16];
/* 116 */     sr.nextBytes(salt);
/* 117 */     return salt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getPassCode() {
/*     */     try {
/* 129 */       return getPassCode(6);
/* 130 */     } catch (NoSuchAlgorithmException e) {
/* 131 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getPassCode(int length) throws NoSuchAlgorithmException {
/* 144 */     if (length < 0)
/* 145 */       return ""; 
/* 146 */     if (length > 32) {
/* 147 */       length = 32;
/*     */     }
/* 149 */     StringBuffer buf = new StringBuffer(length);
/* 150 */     SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
/*     */     
/* 152 */     int i = 0;
/*     */     
/* 154 */     while (i < length) {
/* 155 */       char value = (char)(48 + sr.nextInt(75));
/* 156 */       if (value <= '9' || (value >= 'a' && value <= 'z') || (value >= 'A' && value <= 'Z')) {
/* 157 */         buf.append(value);
/* 158 */         i++;
/*     */       } 
/*     */     } 
/* 161 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\Password.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */