/*    */ package edu.carleton.cas.utility;
/*    */ 
/*    */ import edu.carleton.cas.constants.Shared;
/*    */ import edu.carleton.cas.logging.Logger;
/*    */ import edu.carleton.cas.security.CryptoException;
/*    */ import java.security.KeyFactory;
/*    */ import java.security.PublicKey;
/*    */ import java.security.SignatureException;
/*    */ import java.security.cert.Certificate;
/*    */ import java.security.spec.X509EncodedKeySpec;
/*    */ import java.util.Base64;
/*    */ import java.util.logging.Level;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class CodeVerifier
/*    */ {
/*    */   public static void verify(Class<?> clazz) throws Throwable {
/* 32 */     if (Shared.CODE_MUST_BE_SIGNED || Shared.VERIFY_CODE_SIGNATURE) {
/* 33 */       Certificate[] certs = clazz.getProtectionDomain().getCodeSource().getCertificates();
/* 34 */       if (certs == null) {
/* 35 */         Logger.log(Level.INFO, "No signature for ", clazz.getCanonicalName());
/* 36 */         if (Shared.CODE_MUST_BE_SIGNED)
/* 37 */           throw new SignatureException("No signature found for " + clazz.getCanonicalName()); 
/* 38 */       } else if (Shared.VERIFY_CODE_SIGNATURE) {
/* 39 */         if (Shared.PUBLIC_KEY != null) {
/* 40 */           certs[0].verify(getKey(Shared.PUBLIC_KEY));
/*    */         } else {
/* 42 */           certs[0].verify(certs[0].getPublicKey());
/* 43 */         }  Logger.log(Level.CONFIG, "Code signature was verified for ", clazz.getCanonicalName());
/*    */       } 
/*    */     } 
/*    */   }
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
/*    */   public static PublicKey getKey(String key) throws CryptoException {
/*    */     try {
/* 59 */       byte[] byteKey = Base64.getDecoder().decode(key.getBytes());
/* 60 */       X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
/* 61 */       KeyFactory kf = KeyFactory.getInstance("RSA");
/*    */       
/* 63 */       return kf.generatePublic(X509publicKey);
/* 64 */     } catch (Exception e) {
/* 65 */       throw new CryptoException("Cannot obtain public key: ", e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\ca\\utility\CodeVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */