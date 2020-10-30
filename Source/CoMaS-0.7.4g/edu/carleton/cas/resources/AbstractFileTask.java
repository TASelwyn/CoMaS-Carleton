/*    */ package edu.carleton.cas.resources;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Properties;
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
/*    */ public abstract class AbstractFileTask
/*    */   extends AbstractTask
/*    */ {
/* 20 */   protected static ArrayList<String> FILE_TYPES = new ArrayList<>(); static {
/* 21 */     FILE_TYPES.add(".pdf");
/* 22 */     FILE_TYPES.add(".txt");
/* 23 */     FILE_TYPES.add(".doc");
/* 24 */     FILE_TYPES.add(".docx");
/* 25 */     FILE_TYPES.add(".xls");
/* 26 */     FILE_TYPES.add(".xlsx");
/* 27 */     FILE_TYPES.add(".ppt");
/* 28 */     FILE_TYPES.add(".pptx");
/* 29 */     FILE_TYPES.add(".py");
/* 30 */     FILE_TYPES.add(".java");
/* 31 */     FILE_TYPES.add(".html");
/* 32 */     FILE_TYPES.add(".htm");
/*    */   }
/*    */   
/*    */   public AbstractFileTask(Logger logger, ResourceMonitor monitor) {
/* 36 */     super(logger, monitor);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isIllegal(String line) {
/* 42 */     if (line.endsWith("exam-system-log.html"))
/* 43 */       return false; 
/* 44 */     if (line.endsWith("comas-system-log.html")) {
/* 45 */       return false;
/*    */     }
/*    */     
/* 48 */     for (String type : FILE_TYPES) {
/* 49 */       if (line.endsWith(type)) {
/* 50 */         return true;
/*    */       }
/*    */     } 
/* 53 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void configure(Properties properties) {
/* 62 */     int i = 1;
/* 63 */     String ftype = properties.getProperty("monitor.file.type." + i);
/* 64 */     while (ftype != null) {
/* 65 */       ftype = ftype.trim();
/* 66 */       if (!FILE_TYPES.contains(ftype))
/* 67 */         FILE_TYPES.add(ftype); 
/* 68 */       i++;
/* 69 */       ftype = properties.getProperty("monitor.file.type." + i);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\AbstractFileTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */