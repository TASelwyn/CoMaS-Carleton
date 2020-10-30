/*    */ package edu.carleton.cas.modules.foundation;
/*    */ 
/*    */ import edu.carleton.cas.modules.Module;
/*    */ import edu.carleton.cas.modules.ModuleManagerInterface;
/*    */ import edu.carleton.cas.modules.exceptions.ModuleException;
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
/*    */ public final class ModuleManagerBridge
/*    */   implements ModuleManagerInterface
/*    */ {
/*    */   private ModuleManager manager;
/*    */   
/*    */   public ModuleManagerBridge(ModuleManager manager) {
/* 22 */     this.manager = manager;
/*    */   }
/*    */ 
/*    */   
/*    */   public Module find(String name) {
/* 27 */     return this.manager.find(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public void send(String from, String to, String message) throws ModuleException {
/* 32 */     this.manager.send(from, to, message);
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\modules\foundation\ModuleManagerBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */