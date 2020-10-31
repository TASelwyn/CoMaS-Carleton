package edu.carleton.cas.messaging.handlers;

import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.messaging.Message;
import edu.carleton.cas.messaging.MessageHandler;
import edu.carleton.cas.modules.foundation.ModuleManager;
import java.util.logging.Level;

public class UnloadMessageHandler implements MessageHandler {
  public void handleMessage(Message message) {
    String msg = message.getContentMessage();
    try {
      String[] token = msg.split(" ");
      ModuleManager manager = ModuleManager.getInstance();
      if (token == null)
        throw new NullPointerException("No tokens provided for unload module message"); 
      if (token.length < 1)
        throw new RuntimeException("Insufficient number of tokens (< 1) provided for unload module message"); 
      String name = token[0].trim();
      boolean result = manager.unload(name);
      if (result) {
        Logger.log(Level.INFO, String.format("Unloaded module %s", new Object[] { name }), "");
      } else {
        Logger.log(Level.WARNING, String.format("Module called %s could not be unloaded", new Object[] { name }), "");
      } 
    } catch (Exception e) {
      Logger.log(Level.WARNING, "Unload module message exception: ", e);
    } 
  }
}
