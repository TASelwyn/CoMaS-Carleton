/*    */ package edu.carleton.cas.logging;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.util.logging.FileHandler;
/*    */ import java.util.logging.Handler;
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
/*    */ public class Logger
/*    */ {
/*    */   private static java.util.logging.Logger logger;
/*    */   private static boolean DEBUG = false;
/*    */   
/*    */   public static void setup(Class<?> clazz, String name, String systemLogDir, Level level) throws IOException {
/* 24 */     if (logger == null) {
/* 25 */       File dir = new File(systemLogDir);
/* 26 */       if (dir.exists() && dir.isDirectory() && dir.canWrite()) {
/* 27 */         String base; logger = java.util.logging.Logger.getLogger(clazz.getName());
/*    */         
/* 29 */         Handler[] handlers = logger.getHandlers();
/* 30 */         for (int i = 0; i < handlers.length; i++) {
/* 31 */           logger.removeHandler(handlers[i]);
/*    */         }
/* 33 */         logger.setUseParentHandlers(false);
/*    */ 
/*    */         
/* 36 */         logger.setLevel(level);
/*    */ 
/*    */         
/* 39 */         if (systemLogDir.endsWith(File.separator)) {
/* 40 */           base = String.valueOf(systemLogDir) + name;
/*    */         } else {
/* 42 */           base = String.valueOf(systemLogDir) + File.separator + name;
/*    */         } 
/* 44 */         FileHandler handler = new FileHandler(String.valueOf(base) + "-log.csv");
/* 45 */         handler.setFormatter(new CSVFormatter());
/* 46 */         logger.addHandler(handler);
/*    */         
/* 48 */         handler = new FileHandler(String.valueOf(base) + "-log.html");
/* 49 */         handler.setFormatter(new HtmlFormatter());
/* 50 */         logger.addHandler(handler);
/*    */       } else {
/* 52 */         log(Level.WARNING, "Cannot write to ", systemLogDir);
/*    */       } 
/*    */     } 
/*    */   }
/*    */   public static void setLevel(String level) {
/* 57 */     if (logger != null)
/* 58 */       logger.setLevel(Level.parse(level)); 
/*    */   }
/*    */   
/*    */   public static void log(Level level, String msg, Object obj) {
/* 62 */     if (logger != null) {
/* 63 */       logger.log(level, String.valueOf(msg) + obj.toString());
/*    */     } else {
/* 65 */       debug(level, msg, obj);
/*    */     } 
/*    */   }
/*    */   public static void debug(Level level, String msg, Object obj) {
/* 69 */     if (DEBUG)
/* 70 */       System.err.format("CoMaS[%s]: %s%s\n", new Object[] { level, msg, obj.toString() }); 
/*    */   }
/*    */   
/*    */   public static void debug(Level level, String msg) {
/* 74 */     if (DEBUG)
/* 75 */       System.err.format("CoMaS[%s]: %s\n", new Object[] { level, msg }); 
/*    */   }
/*    */   
/*    */   public static void output(String msg, Object obj) {
/* 79 */     if (DEBUG)
/* 80 */       System.out.format("CoMaS[INFO]: %s%s\n", new Object[] { msg, obj.toString() }); 
/*    */   }
/*    */   
/*    */   public static void output(String msg) {
/* 84 */     if (DEBUG)
/* 85 */       System.out.format("CoMaS[INFO]: %s\n", new Object[] { msg }); 
/*    */   }
/*    */   
/*    */   public void log(Level level, String msg) {
/* 89 */     if (logger != null)
/* 90 */       logger.log(level, msg); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\logging\Logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */