/*    */ package edu.carleton.cas.messaging.handlers;
/*    */ 
/*    */ import edu.carleton.cas.logging.Logger;
/*    */ import edu.carleton.cas.messaging.Message;
/*    */ import edu.carleton.cas.messaging.MessageHandler;
/*    */ import edu.carleton.cas.ui.ExamDialog;
/*    */ import edu.carleton.cas.utility.IconLoader;
/*    */ import java.awt.Component;
/*    */ import java.util.logging.Level;
/*    */ import javax.swing.JOptionPane;
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
/*    */ public class AlertMessageHandler
/*    */   implements MessageHandler
/*    */ {
/*    */   public void handleMessage(Message message) {
/* 25 */     ExamDialog.getInstance().toFront();
/* 26 */     ExamDialog.getInstance().repaint();
/* 27 */     JOptionPane.showMessageDialog((Component)ExamDialog.getInstance(), message.getContentMessage(), "CoMaS Administrator Alert", 
/* 28 */         1, IconLoader.getDefaultIcon());
/* 29 */     Logger.log(Level.INFO, "Administrator message: ", message.getContentMessage());
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\messaging\handlers\AlertMessageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */