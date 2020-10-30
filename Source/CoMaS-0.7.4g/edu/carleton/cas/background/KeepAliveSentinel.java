/*    */ package edu.carleton.cas.background;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Timer;
/*    */ import java.util.TimerTask;
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
/*    */ public class KeepAliveSentinel
/*    */ {
/*    */   public KeepAliveSentinel() {
/* 22 */     this(300000);
/*    */   }
/*    */ 
/*    */   
/* 26 */   ArrayList<KeepAliveInterface> services = new ArrayList<>();
/* 27 */   Timer timer = new Timer(); public KeepAliveSentinel(int timeInMillis) {
/* 28 */     if (timeInMillis < 300000)
/* 29 */       timeInMillis = 300000; 
/* 30 */     this.timeInMillis = timeInMillis;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   int timeInMillis;
/*    */ 
/*    */   
/*    */   public synchronized void register(KeepAliveInterface service) {
/* 39 */     this.services.add(service);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public synchronized void deregister(KeepAliveInterface service) {
/* 48 */     this.services.remove(service);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void start() {
/* 60 */     this.timer.scheduleAtFixedRate(new SentinelTask(null), 60000L, this.timeInMillis);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void stop() {
/* 68 */     this.timer.cancel();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private class SentinelTask
/*    */     extends TimerTask
/*    */   {
/*    */     private SentinelTask() {}
/*    */ 
/*    */ 
/*    */     
/*    */     public void run() {
/* 81 */       for (KeepAliveInterface s : KeepAliveSentinel.this.services) {
/* 82 */         if (s.keepAlive())
/* 83 */           s.start(); 
/*    */       } 
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\background\KeepAliveSentinel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */