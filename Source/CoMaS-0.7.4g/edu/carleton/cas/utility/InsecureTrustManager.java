/*    */ package edu.carleton.cas.utility;
/*    */ 
/*    */ import java.security.cert.CertificateException;
/*    */ import java.security.cert.X509Certificate;
/*    */ import javax.net.ssl.X509TrustManager;
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
/*    */ public class InsecureTrustManager
/*    */   implements X509TrustManager
/*    */ {
/*    */   public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
/*    */   
/*    */   public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
/*    */   
/*    */   public X509Certificate[] getAcceptedIssuers() {
/* 29 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\InsecureTrustManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */