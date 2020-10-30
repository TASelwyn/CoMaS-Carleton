/*    */ package org.eclipse.jdt.internal.jarinjarloader;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
/*    */ import java.net.URLStreamHandler;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RsrcURLStreamHandler
/*    */   extends URLStreamHandler
/*    */ {
/*    */   private ClassLoader classLoader;
/*    */   
/*    */   public RsrcURLStreamHandler(ClassLoader classLoader) {
/* 34 */     this.classLoader = classLoader;
/*    */   }
/*    */   
/*    */   protected URLConnection openConnection(URL u) throws IOException {
/* 38 */     return new RsrcURLConnection(u, this.classLoader);
/*    */   }
/*    */   
/*    */   protected void parseURL(URL url, String spec, int start, int limit) {
/*    */     String file;
/* 43 */     if (spec.startsWith("rsrc:")) {
/* 44 */       file = spec.substring(5);
/* 45 */     } else if (url.getFile().equals("./")) {
/* 46 */       file = spec;
/* 47 */     } else if (url.getFile().endsWith("/")) {
/* 48 */       file = String.valueOf(url.getFile()) + spec;
/* 49 */     } else if ("#runtime".equals(spec)) {
/* 50 */       file = url.getFile();
/*    */     } else {
/* 52 */       file = spec;
/* 53 */     }  setURL(url, "rsrc", "", -1, null, null, file, null, null);
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\org\eclipse\jdt\internal\jarinjarloader\RsrcURLStreamHandler.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */