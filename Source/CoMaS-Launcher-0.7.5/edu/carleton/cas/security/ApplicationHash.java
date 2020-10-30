/*    */ package edu.carleton.cas.security;
/*    */ 
/*    */ import edu.carleton.cas.constants.Shared;
/*    */ import edu.carleton.cas.file.Utils;
/*    */ import java.io.File;
/*    */ import java.net.URL;
/*    */ import java.util.Properties;
/*    */ 
/*    */ 
/*    */ public class ApplicationHash
/*    */ {
/* 12 */   static Properties properties = new Properties();
/* 13 */   static String host = "comas.cogerent.com";
/*    */ 
/*    */   
/*    */   public static void main(String[] args) throws Exception {
/* 17 */     Shared.VERSION = "0.7.4d";
/*    */     
/* 19 */     String jarFileToBeLoaded = String.format(Shared.COMAS_DOT_JAR_FORMAT, new Object[] {
/* 20 */           Utils.getStringOrDefault(properties, "version", Shared.VERSION)
/*    */         });
/* 22 */     System.out.println("Jar: " + jarFileToBeLoaded);
/* 23 */     String url = Shared.service(Shared.PROTOCOL, host, Shared.PORT, 
/* 24 */         "/CMS/rest/exam/" + jarFileToBeLoaded);
/* 25 */     File dir = new File(Shared.DIR);
/* 26 */     Utils.getAndStoreURL(new URL(url), dir);
/*    */     
/* 28 */     String hash = Checksum.getSHA256Checksum((new File(Shared.DIR, jarFileToBeLoaded)).getAbsolutePath());
/* 29 */     System.out.println(hash);
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\cas\security\ApplicationHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */