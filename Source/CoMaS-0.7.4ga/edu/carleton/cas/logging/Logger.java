package edu.carleton.cas.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

public class Logger {
  private static java.util.logging.Logger logger;
  
  private static boolean DEBUG = false;
  
  public static void setup(Class<?> clazz, String name, String systemLogDir, Level level) throws IOException {
    if (logger == null) {
      File dir = new File(systemLogDir);
      if (dir.exists() && dir.isDirectory() && dir.canWrite()) {
        String base;
        logger = java.util.logging.Logger.getLogger(clazz.getName());
        Handler[] handlers = logger.getHandlers();
        for (int i = 0; i < handlers.length; i++)
          logger.removeHandler(handlers[i]); 
        logger.setUseParentHandlers(false);
        logger.setLevel(level);
        if (systemLogDir.endsWith(File.separator)) {
          base = String.valueOf(systemLogDir) + name;
        } else {
          base = String.valueOf(systemLogDir) + File.separator + name;
        } 
        FileHandler handler = new FileHandler(String.valueOf(base) + "-log.csv");
        handler.setFormatter(new CSVFormatter());
        logger.addHandler(handler);
        handler = new FileHandler(String.valueOf(base) + "-log.html");
        handler.setFormatter(new HtmlFormatter());
        logger.addHandler(handler);
      } else {
        log(Level.WARNING, "Cannot write to ", systemLogDir);
      } 
    } 
  }
  
  public static void setLevel(String level) {
    if (logger != null)
      logger.setLevel(Level.parse(level)); 
  }
  
  public static void log(Level level, String msg, Object obj) {
    if (logger != null) {
      logger.log(level, String.valueOf(msg) + obj.toString());
    } else {
      debug(level, msg, obj);
    } 
  }
  
  public static void debug(Level level, String msg, Object obj) {
    if (DEBUG)
      System.err.format("CoMaS[%s]: %s%s\n", new Object[] { level, msg, obj.toString() }); 
  }
  
  public static void debug(Level level, String msg) {
    if (DEBUG)
      System.err.format("CoMaS[%s]: %s\n", new Object[] { level, msg }); 
  }
  
  public static void output(String msg, Object obj) {
    if (DEBUG)
      System.out.format("CoMaS[INFO]: %s%s\n", new Object[] { msg, obj.toString() }); 
  }
  
  public static void output(String msg) {
    if (DEBUG)
      System.out.format("CoMaS[INFO]: %s\n", new Object[] { msg }); 
  }
  
  public void log(Level level, String msg) {
    if (logger != null)
      logger.log(level, msg); 
  }
}
