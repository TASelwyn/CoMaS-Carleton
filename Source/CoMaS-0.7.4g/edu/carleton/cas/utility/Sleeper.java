/*    */ package edu.carleton.cas.utility;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Sleeper
/*    */ {
/*    */   public static void sleep(int millis) {
/* 10 */     long now = System.currentTimeMillis();
/* 11 */     long end = now + millis;
/* 12 */     while (end - now > 0L) {
/*    */       try {
/* 14 */         Thread.sleep(end - now);
/* 15 */       } catch (InterruptedException interruptedException) {
/*    */         continue;
/*    */       } finally {
/* 18 */         now = System.currentTimeMillis();
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public static void sleepAndExit(int millis, int status) {
/* 24 */     sleep(millis);
/* 25 */     System.exit(status);
/*    */   }
/*    */   
/*    */   public static void sleepAndRun(int millis, Runnable runnable) {
/* 29 */     sleep(millis);
/* 30 */     runnable.run();
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\Sleeper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */