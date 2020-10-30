/*    */ package edu.carleton.cas.resources;
/*    */ 
/*    */ import java.util.concurrent.CopyOnWriteArraySet;
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
/*    */ public abstract class AbstractResourceMonitor
/*    */   implements Resource
/*    */ {
/* 17 */   protected CopyOnWriteArraySet<ResourceListener> listeners = new CopyOnWriteArraySet<>();
/*    */ 
/*    */   
/*    */   protected String type;
/*    */ 
/*    */ 
/*    */   
/*    */   public void notifyListeners(String type, String description) {
/* 25 */     for (ResourceListener l : this.listeners) {
/* 26 */       l.resourceEvent(this, type, description);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void addListener(ResourceListener listener) {
/* 36 */     this.listeners.add(listener);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void removeListener(ResourceListener listener) {
/* 45 */     this.listeners.remove(listener);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getResourceType() {
/* 50 */     return this.type;
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\AbstractResourceMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */