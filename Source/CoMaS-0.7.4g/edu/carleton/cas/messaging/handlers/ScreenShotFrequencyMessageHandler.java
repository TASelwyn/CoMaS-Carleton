package edu.carleton.cas.messaging.handlers;

import edu.carleton.cas.constants.Shared;
import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.messaging.Message;
import edu.carleton.cas.messaging.MessageHandler;
import java.util.logging.Level;

public class ScreenShotFrequencyMessageHandler implements MessageHandler {
  public void handleMessage(Message message) {
    try {
      int noOfSeconds = Integer.parseInt(message.getContentMessage().trim());
      if (noOfSeconds >= Shared.ABSOLUTE_MIN_INTERVAL) {
        Shared.MAX_INTERVAL = noOfSeconds;
        Logger.log(Level.CONFIG, "Screen shot frequency set to ", Integer.valueOf(Shared.MAX_INTERVAL));
      } else {
        Logger.log(Level.WARNING, "Screen shot frequency could not be changed to ", message.getContentMessage());
      } 
    } catch (Exception e) {
      Logger.log(Level.WARNING, "Screen shot frequency parsing exception: ", e);
    } 
  }
}
