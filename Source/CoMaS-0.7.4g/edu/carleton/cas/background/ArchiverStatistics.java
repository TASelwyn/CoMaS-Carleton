/*    */ package edu.carleton.cas.background;
/*    */ 
/*    */ import java.util.concurrent.atomic.AtomicInteger;
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
/*    */ public class ArchiverStatistics
/*    */ {
/* 19 */   protected AtomicInteger failures = new AtomicInteger(0);
/* 20 */   protected AtomicInteger totalFailures = new AtomicInteger(0);
/* 21 */   protected AtomicInteger totalProcessed = new AtomicInteger(0);
/* 22 */   protected AtomicInteger totalStarts = new AtomicInteger(0);
/*    */ 
/*    */   
/*    */   protected int incrementFailures() {
/* 26 */     this.totalFailures.incrementAndGet();
/* 27 */     return this.failures.incrementAndGet();
/*    */   }
/*    */   
/*    */   protected int incrementStarts() {
/* 31 */     return this.totalStarts.incrementAndGet();
/*    */   }
/*    */   
/*    */   protected int incrementTotalProcessed() {
/* 35 */     return this.totalProcessed.incrementAndGet();
/*    */   }
/*    */   
/*    */   protected void resetFailures() {
/* 39 */     this.failures.set(0);
/*    */   }
/*    */   
/*    */   protected int getFailures() {
/* 43 */     return this.failures.get();
/*    */   }
/*    */   
/*    */   protected int getTotalFailures() {
/* 47 */     return this.totalFailures.get();
/*    */   }
/*    */   
/*    */   protected int getTotalProcessed() {
/* 51 */     return this.totalProcessed.get();
/*    */   }
/*    */   
/*    */   protected int getTotalStarts() {
/* 55 */     return this.totalStarts.get();
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\background\ArchiverStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */