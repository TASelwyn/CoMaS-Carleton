/*    */ package edu.carleton.cas.utility;
/*    */ 
/*    */ import java.util.concurrent.CopyOnWriteArraySet;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Observable
/*    */ {
/*    */   private boolean changed = false;
/* 11 */   protected CopyOnWriteArraySet<Observer> listeners = new CopyOnWriteArraySet<>();
/*    */ 
/*    */   
/*    */   public boolean isChanged() {
/* 15 */     return this.changed;
/*    */   }
/*    */   
/*    */   public void setChanged() {
/* 19 */     this.changed = true;
/*    */   }
/*    */   
/*    */   public void notifyObservers() {
/* 23 */     notifyObservers(null);
/*    */   }
/*    */   
/*    */   public synchronized void notifyObservers(Object arg) {
/* 27 */     if (isChanged()) {
/* 28 */       for (Observer l : this.listeners) {
/* 29 */         l.update(this, arg);
/*    */       }
/*    */     }
/* 32 */     this.changed = false;
/*    */   }
/*    */   
/*    */   public void addObserver(Observer listener) {
/* 36 */     this.listeners.add(listener);
/*    */   }
/*    */   
/*    */   public void removeObserver(Observer listener) {
/* 40 */     this.listeners.remove(listener);
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\Observable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */