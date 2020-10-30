/*    */ package edu.carleton.cas.messaging.handlers;
/*    */ 
/*    */ import edu.carleton.cas.logging.Logger;
/*    */ import edu.carleton.cas.messaging.Message;
/*    */ import edu.carleton.cas.messaging.MessageHandler;
/*    */ import edu.carleton.cas.ui.ExamDialog;
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
/*    */ public class StopMessageHandler
/*    */   implements MessageHandler
/*    */ {
/*    */   public void handleMessage(Message message) {
/* 21 */     Logger.log(Level.WARNING, "Administrator stopped the session", "");
/* 22 */     (ExamDialog.getInstance()).login.endTheSession();
/* 23 */     System.exit(0);
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\messaging\handlers\StopMessageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */