package edu.carleton.cas.messaging.handlers;

import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.messaging.Message;
import edu.carleton.cas.messaging.MessageHandler;
import edu.carleton.cas.modules.foundation.JarClassLoader;
import edu.carleton.cas.modules.foundation.ModuleAction;
import edu.carleton.cas.modules.foundation.ModuleContainer;
import edu.carleton.cas.modules.foundation.ModuleManager;
import java.net.URL;
import java.util.logging.Level;

public class LoadMessageHandler implements MessageHandler {
  public void handleMessage(Message message) {
    String msg = message.getContentMessage();
    try {
      String[] token = msg.split(" ");
      ModuleManager manager = ModuleManager.getInstance();
      if (token == null)
        throw new NullPointerException("No tokens provided for load module message"); 
      if (token.length < 3)
        throw new RuntimeException("Insufficient number of tokens (< 3) provided for load module message"); 
      String name = token[0].trim();
      String className = token[1].trim();
      String url = token[2].trim();
      JarClassLoader jcl = new JarClassLoader(new URL(url), manager.getLoader(), manager.getToken());
      ModuleContainer mc = manager.load(name, className, jcl);
      if (mc != null) {
        manager.execute(mc, ModuleAction.start);
        Logger.log(Level.INFO, 
            String.format("Started module %s using %s", new Object[] { mc.getName(), mc.getModule().getClass() }), "");
      } else {
        Logger.log(Level.WARNING, String.format("Module called %s using %s could not be loaded", new Object[] { name, className }), "");
      } 
    } catch (Exception e) {
      Logger.log(Level.WARNING, "Load module message exception: ", e);
    } 
  }
}
