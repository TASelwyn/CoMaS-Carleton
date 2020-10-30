/*    */ package edu.carleton.cas.messaging.handlers;
/*    */ 
/*    */ import edu.carleton.cas.logging.Logger;
/*    */ import edu.carleton.cas.messaging.Message;
/*    */ import edu.carleton.cas.messaging.MessageHandler;
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
/*    */ public class UnloadMessageHandler
/*    */   implements MessageHandler
/*    */ {
/*    */   public void handleMessage(Message message) {
/* 21 */     String msg = message.getContentMessage();
/*    */     try {
/* 23 */       String[] token = msg.split(" ");
/* 24 */       ModuleManager manager = ModuleManager.getInstance();
/* 25 */       if (token == null)
/* 26 */         throw new NullPointerException("No tokens provided for unload module message"); 
/* 27 */       if (token.length < 1)
/* 28 */         throw new RuntimeException("Insufficient number of tokens (< 1) provided for unload module message"); 
/* 29 */       String name = token[0].trim();
/* 30 */       boolean result = manager.unload(name);
/* 31 */       if (result)
/* 32 */       { Logger.log(Level.INFO, String.format("Unloaded module %s", new Object[] { name }), ""); }
/*    */       else
/* 34 */       { Logger.log(Level.WARNING, String.format("Module called %s could not be unloaded", new Object[] { name }), ""); } 
/* 35 */     } catch (Exception e) {
/* 36 */       Logger.log(Level.WARNING, "Unload module message exception: ", e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\messaging\handlers\UnloadMessageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */