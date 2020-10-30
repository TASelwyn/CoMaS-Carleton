/*    */ package edu.carleton.cas.background;
/*    */ 
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
/*    */ public class LoggerModuleBridge
/*    */   implements Logger
/*    */ {
/*    */   private final LogArchiver log;
/*    */   
/*    */   public LoggerModuleBridge(LogArchiver log) {
/* 20 */     this.log = log;
/*    */   }
/*    */ 
/*    */   
/*    */   public void put(Level level, String description) {
/* 25 */     if (level == null || description == null)
/*    */       return; 
/* 27 */     if (description.length() > 1024)
/*    */       return; 
/* 29 */     this.log.put(level, description);
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\background\LoggerModuleBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */