/*    */ package edu.carleton.cas.messaging.handlers;
/*    */ 
/*    */ import edu.carleton.cas.constants.Shared;
/*    */ import edu.carleton.cas.logging.Logger;
/*    */ import edu.carleton.cas.messaging.Message;
/*    */ import edu.carleton.cas.messaging.MessageHandler;
/*    */ import java.util.logging.Level;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ScreenShotFrequencyMessageHandler
/*    */   implements MessageHandler
/*    */ {
/*    */   public void handleMessage(Message message) {
/*    */     try {
/* 21 */       int noOfSeconds = Integer.parseInt(message.getContentMessage().trim());
/* 22 */       if (noOfSeconds >= Shared.ABSOLUTE_MIN_INTERVAL)
/* 23 */       { Shared.MAX_INTERVAL = noOfSeconds;
/* 24 */         Logger.log(Level.CONFIG, "Screen shot frequency set to ", Integer.valueOf(Shared.MAX_INTERVAL)); }
/*    */       else
/* 26 */       { Logger.log(Level.WARNING, "Screen shot frequency could not be changed to ", message.getContentMessage()); } 
/* 27 */     } catch (Exception e) {
/* 28 */       Logger.log(Level.WARNING, "Screen shot frequency parsing exception: ", e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\messaging\handlers\ScreenShotFrequencyMessageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */