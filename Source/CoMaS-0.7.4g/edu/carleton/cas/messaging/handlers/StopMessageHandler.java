package edu.carleton.cas.messaging.handlers;

import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.messaging.Message;
import edu.carleton.cas.messaging.MessageHandler;
import edu.carleton.cas.ui.ExamDialog;
import java.util.logging.Level;

public class StopMessageHandler implements MessageHandler {
  public void handleMessage(Message message) {
    Logger.log(Level.WARNING, "Administrator stopped the session", "");
    (ExamDialog.getInstance()).login.endTheSession();
    System.exit(0);
  }
}
