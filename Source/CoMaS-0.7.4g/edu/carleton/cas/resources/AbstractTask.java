/*    */ package edu.carleton.cas.resources;
/*    */ 
/*    */ import java.util.TimerTask;
/*    */ 
/*    */ public abstract class AbstractTask
/*    */   extends TimerTask implements LegalityCheck {
/*    */   protected Logger logger;
/*    */   protected ResourceMonitor monitor;
/*    */   protected Process process;
/*    */   
/*    */   public AbstractTask(Logger logger, ResourceMonitor monitor) {
/* 12 */     this.logger = logger;
/* 13 */     this.monitor = monitor;
/*    */   }
/*    */   
/*    */   public void close() {
/* 17 */     if (this.process != null) {
/* 18 */       this.process.destroyForcibly();
/* 19 */       this.process = null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\AbstractTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */