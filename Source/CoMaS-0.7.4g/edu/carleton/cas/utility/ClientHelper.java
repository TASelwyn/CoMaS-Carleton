/*    */ package edu.carleton.cas.utility;
/*    */ 
/*    */ import java.security.GeneralSecurityException;
/*    */ import java.security.SecureRandom;
/*    */ import javax.net.ssl.SSLContext;
/*    */ import javax.net.ssl.TrustManager;
/*    */ import javax.ws.rs.client.Client;
/*    */ import javax.ws.rs.client.ClientBuilder;
/*    */ import javax.ws.rs.core.Configuration;
/*    */ import org.glassfish.jersey.client.ClientConfig;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ClientHelper
/*    */ {
/*    */   public static Client createClient(String protocol) {
/* 18 */     if (protocol.equals("https")) {
/* 19 */       System.setProperty("jsse.enableSNIExtension", "false");
/* 20 */       TrustManager[] certs = { new InsecureTrustManager() };
/* 21 */       SSLContext ctx = null;
/*    */       try {
/* 23 */         ctx = SSLContext.getInstance("SSL");
/* 24 */         ctx.init(null, certs, new SecureRandom());
/* 25 */       } catch (GeneralSecurityException generalSecurityException) {}
/*    */       
/* 27 */       return ClientBuilder.newBuilder().withConfig((Configuration)new ClientConfig())
/* 28 */         .hostnameVerifier(new InsecureHostnameVerifier()).sslContext(ctx).build();
/*    */     } 
/* 30 */     return ClientBuilder.newClient();
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\ClientHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */