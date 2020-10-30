/*    */ package edu.carleton.cas.utility;
/*    */ 
/*    */ import edu.carleton.cas.logging.Logger;
/*    */ import java.io.BufferedWriter;
/*    */ import java.io.File;
/*    */ import java.io.FileWriter;
/*    */ import java.io.IOException;
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
/*    */ public class HTMLFileViewGenerator
/*    */ {
/*    */   public static void create(String title, String fileToView, String htmlFile) {
/* 22 */     String newline = System.getProperty("line.separator");
/* 23 */     File file = new File(htmlFile);
/* 24 */     FileWriter fw = null;
/* 25 */     BufferedWriter bw = null;
/*    */     try {
/* 27 */       fw = new FileWriter(file);
/* 28 */       bw = new BufferedWriter(fw);
/* 29 */       bw.write("<html><head><title>");
/* 30 */       bw.write(title);
/* 31 */       bw.write("</title></head>");
/* 32 */       bw.write(String.valueOf(newline) + "<script>" + newline);
/* 33 */       bw.write("   window.location.href = ");
/* 34 */       bw.write("\"file:///" + convert(fileToView) + "\"");
/* 35 */       bw.write(String.valueOf(newline) + "</script>" + newline + "</body>" + newline + "</html>");
/* 36 */     } catch (IOException e) {
/* 37 */       Logger.log(Level.WARNING, "", "Could not create " + fileToView + " viewer");
/*    */     } finally {
/*    */       try {
/* 40 */         if (bw != null)
/* 41 */           bw.close(); 
/* 42 */         if (fw != null)
/* 43 */           fw.close(); 
/* 44 */       } catch (IOException iOException) {}
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static String convert(String name) {
/* 51 */     StringBuffer b = new StringBuffer();
/* 52 */     for (int i = 0; i < name.length(); i++) {
/* 53 */       char c = name.charAt(i);
/* 54 */       if (c == '\\') {
/* 55 */         b.append("\\\\");
/*    */       } else {
/* 57 */         b.append(c);
/*    */       } 
/* 59 */     }  return b.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\HTMLFileViewGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */