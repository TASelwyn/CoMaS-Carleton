/*    */ package edu.carleton.cas.resources;
/*    */ 
/*    */ import java.util.Properties;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VMCheck
/*    */   extends ResourceMonitor
/*    */ {
/*    */   private final VMCheckTask vmCheckTask;
/*    */   
/*    */   public VMCheck(Properties properties) {
/* 17 */     super("vmcheck", "vmcheck");
/* 18 */     this.vmCheckTask = new VMCheckTask(null, this, properties);
/*    */   }
/*    */ 
/*    */   
/*    */   public void open() {
/* 23 */     this.timer.schedule(this.vmCheckTask, 10000L);
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {
/*    */     try {
/* 29 */       super.close();
/* 30 */       this.timer.cancel();
/* 31 */       this.vmCheckTask.close();
/* 32 */     } catch (Exception exception) {}
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\VMCheck.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */