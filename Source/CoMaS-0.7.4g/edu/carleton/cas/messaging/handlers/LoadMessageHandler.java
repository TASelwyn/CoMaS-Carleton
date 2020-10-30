/*    */ package edu.carleton.cas.messaging.handlers;
/*    */ 
/*    */ import edu.carleton.cas.logging.Logger;
/*    */ import edu.carleton.cas.messaging.Message;
/*    */ import edu.carleton.cas.messaging.MessageHandler;
/*    */ import edu.carleton.cas.modules.foundation.ModuleAction;
/*    */ import edu.carleton.cas.modules.foundation.ModuleContainer;
/*    */ import edu.carleton.cas.modules.foundation.ModuleManager;
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
/*    */ public class LoadMessageHandler
/*    */   implements MessageHandler
/*    */ {
/*    */   public void handleMessage(Message message) {
/* 23 */     String msg = message.getContentMessage();
/*    */     try {
/* 25 */       String[] token = msg.split(" ");
/* 26 */       ModuleManager manager = ModuleManager.getInstance();
/* 27 */       if (token == null)
/* 28 */         throw new NullPointerException("No tokens provided for load module message"); 
/* 29 */       if (token.length < 2)
/* 30 */         throw new RuntimeException("Insufficient number of tokens (< 2) provided for load module message"); 
/* 31 */       String name = token[0].trim();
/* 32 */       String className = token[1].trim();
/* 33 */       ModuleContainer mc = manager.load(name, className);
/*    */ 
/*    */       
/* 36 */       if (mc != null)
/* 37 */       { manager.execute(mc, ModuleAction.start);
/* 38 */         Logger.log(Level.INFO, 
/* 39 */             String.format("Started module %s using %s", new Object[] { mc.getName(), mc.getModule().getClass() }), ""); }
/*    */       else
/* 41 */       { Logger.log(Level.WARNING, String.format("Module called %s using %s could not be loaded", new Object[] { name, className }), ""); } 
/* 42 */     } catch (Exception e) {
/* 43 */       Logger.log(Level.WARNING, "Load module message exception: ", e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\messaging\handlers\LoadMessageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */