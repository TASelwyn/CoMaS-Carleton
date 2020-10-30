/*   */ package edu.carleton.cas.utility;
/*   */ 
/*   */ import javax.net.ssl.HostnameVerifier;
/*   */ import javax.net.ssl.SSLSession;
/*   */ 
/*   */ public class InsecureHostnameVerifier
/*   */   implements HostnameVerifier {
/*   */   public boolean verify(String hostname, SSLSession session) {
/* 9 */     return true;
/*   */   }
/*   */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\InsecureHostnameVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */