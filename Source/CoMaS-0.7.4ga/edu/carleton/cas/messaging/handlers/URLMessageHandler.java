package edu.carleton.cas.messaging.handlers;

import edu.carleton.cas.constants.Shared;
import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.messaging.Message;
import edu.carleton.cas.messaging.MessageHandler;
import edu.carleton.cas.modules.foundation.ModuleManager;
import java.net.URL;
import java.util.logging.Level;

public class URLMessageHandler implements MessageHandler {
  public void handleMessage(Message message) {
    String msg = message.getContentMessage();
    try {
      String[] token = msg.split(" ");
      ModuleManager manager = ModuleManager.getInstance();
      if (token.length < 1)
        throw new RuntimeException("Insufficient number of tokens (< 1) provided for URL message"); 
      String urlProp = token[0].trim();
      if (urlProp.startsWith("/"))
        urlProp = Shared.service(Shared.PROTOCOL, Shared.CMS_HOST, Shared.PORT, urlProp); 
      manager.addURL(new URL(urlProp));
      Logger.log(Level.INFO, "New module loading URL added: ", urlProp);
    } catch (Exception e) {
      Logger.log(Level.WARNING, "URL message exception: ", e);
    } 
  }
}
