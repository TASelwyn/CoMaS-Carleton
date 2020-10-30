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
/*    */ 
/*    */ public class Logger
/*    */ {
/*    */   private static boolean DEBUG = false;
/*    */   private static java.util.logging.Logger logger;
/*    */   
/*    */   public static void setup(Class<?> clazz, String name, String systemLogDir, Level level) throws IOException {
/* 25 */     if (logger == null) {
/* 26 */       File dir = new File(systemLogDir);
/* 27 */       if (dir.exists() && dir.isDirectory() && dir.canWrite()) {
/* 28 */         String base; logger = java.util.logging.Logger.getLogger(clazz.getName());
/*    */         
/* 30 */         Handler[] handlers = logger.getHandlers();
/* 31 */         for (int i = 0; i < handlers.length; i++) {
/* 32 */           logger.removeHandler(handlers[i]);
/*    */         }
/* 34 */         logger.setUseParentHandlers(false);
/*    */ 
/*    */         
/* 37 */         logger.setLevel(level);
/*    */ 
/*    */         
/* 40 */         if (systemLogDir.endsWith(File.separator)) {
/* 41 */           base = String.valueOf(systemLogDir) + name;
/*    */         } else {
/* 43 */           base = String.valueOf(systemLogDir) + File.separator + name;
/*    */         } 
/* 45 */         FileHandler handler = new FileHandler(String.valueOf(base) + "-log.csv");
/* 46 */         handler.setFormatter(new CSVFormatter());
/* 47 */         logger.addHandler(handler);
/*    */         
/* 49 */         handler = new FileHandler(String.valueOf(base) + "-log.html");
/* 50 */         handler.setFormatter(new HtmlFormatter());
/* 51 */         logger.addHandler(handler);
/*    */       } else {
/* 53 */         log(Level.WARNING, "Cannot write to ", systemLogDir);
/*    */       } 
/*    */     } 
/*    */   }
/*    */   public static void setLevel(String level) {
/* 58 */     if (logger != null)
/* 59 */       logger.setLevel(Level.parse(level)); 
/*    */   }
/*    */   
/*    */   public static void log(Level level, String msg, Object obj) {
/* 63 */     if (logger != null) {
/* 64 */       logger.log(level, String.valueOf(msg) + obj.toString());
/*    */     } else {
/* 66 */       debug(level, msg, obj);
/*    */     } 
/*    */   }
/*    */   public static void debug(Level level, String msg, Object obj) {
/* 70 */     if (DEBUG)
/* 71 */       System.err.format("CoMaS[%s]: %s%s\n", new Object[] { level, msg, obj.toString() }); 
/*    */   }
/*    */   
/*    */   public static void debug(Level level, String msg) {
/* 75 */     if (DEBUG)
/* 76 */       System.err.format("CoMaS[%s]: %s\n", new Object[] { level, msg }); 
/*    */   }
/*    */   
/*    */   public static void output(String msg, Object obj) {
/* 80 */     if (DEBUG)
/* 81 */       System.out.format("CoMaS[INFO]: %s%s\n", new Object[] { msg, obj.toString() }); 
/*    */   }
/*    */   
/*    */   public static void output(String msg) {
/* 85 */     if (DEBUG)
/* 86 */       System.out.format("CoMaS[INFO]: %s\n", new Object[] { msg }); 
/*    */   }
/*    */   
/*    */   public void log(Level level, String msg) {
/* 90 */     if (logger != null)
/* 91 */       logger.log(level, msg); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\cas\logging\Logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */