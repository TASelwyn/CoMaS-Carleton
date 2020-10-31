package edu.carleton.cas.messaging.handlers;

import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.messaging.Message;
import edu.carleton.cas.messaging.MessageHandler;
import edu.carleton.cas.ui.ExamDialog;
import edu.carleton.cas.utility.IconLoader;
import java.awt.Component;
import java.util.logging.Level;
import javax.swing.JOptionPane;

public class AlertMessageHandler implements MessageHandler {
  public void handleMessage(Message message) {
    ExamDialog.getInstance().toFront();
    ExamDialog.getInstance().repaint();
    JOptionPane.showMessageDialog((Component)ExamDialog.getInstance(), message.getContentMessage(), "CoMaS Administrator Alert", 
        1, IconLoader.getDefaultIcon());
    Logger.log(Level.INFO, "Administrator message: ", message.getContentMessage());
  }
}
