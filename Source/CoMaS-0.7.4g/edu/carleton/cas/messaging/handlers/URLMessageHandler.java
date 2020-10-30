/*    */ package edu.carleton.cas.messaging.handlers;
/*    */ 
/*    */ import edu.carleton.cas.constants.Shared;
/*    */ import edu.carleton.cas.logging.Logger;
/*    */ import edu.carleton.cas.messaging.Message;
/*    */ import edu.carleton.cas.messaging.MessageHandler;
/*    */ import edu.carleton.cas.modules.foundation.ModuleManager;
/*    */ import java.net.URL;
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
/*    */ 
/*    */ public class URLMessageHandler
/*    */   implements MessageHandler
/*    */ {
/*    */   public void handleMessage(Message message) {
/* 24 */     String msg = message.getContentMessage();
/*    */     try {
/* 26 */       String[] token = msg.split(" ");
/* 27 */       ModuleManager manager = ModuleManager.getInstance();
/* 28 */       if (token.length < 1)
/* 29 */         throw new RuntimeException("Insufficient number of tokens (< 1) provided for URL message"); 
/* 30 */       String urlProp = token[0].trim();
/* 31 */       if (urlProp.startsWith("/")) {
/* 32 */         urlProp = Shared.service(Shared.PROTOCOL, Shared.CMS_HOST, Shared.PORT, urlProp);
/*    */       }
/* 34 */       manager.addURL(new URL(urlProp));
/* 35 */       Logger.log(Level.INFO, "New module loading URL added: ", urlProp);
/* 36 */     } catch (Exception e) {
/* 37 */       Logger.log(Level.WARNING, "URL message exception: ", e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\messaging\handlers\URLMessageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */