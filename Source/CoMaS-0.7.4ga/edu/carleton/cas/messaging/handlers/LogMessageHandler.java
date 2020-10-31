package edu.carleton.cas.messaging.handlers;

import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.messaging.Message;
import edu.carleton.cas.messaging.MessageHandler;
import java.util.logging.Level;

public class LogMessageHandler implements MessageHandler {
  public void handleMessage(Message message) {
    try {
      String levelString = message.getContentMessage().trim().toUpperCase();
      Logger.setLevel(levelString);
      Logger.log(Level.INFO, "Log level set to: ", levelString);
    } catch (Exception e) {
      Logger.log(Level.WARNING, "Log level message exception: ", e);
    } 
  }
}
