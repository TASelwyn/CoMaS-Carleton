package edu.carleton.cas.background;

import java.util.logging.Level;

public class LoggerModuleBridge implements Logger {
  private final LogArchiver log;
  
  public LoggerModuleBridge(LogArchiver log) {
    this.log = log;
  }
  
  public void put(Level level, String description) {
    if (level == null || description == null)
      return; 
    if (description.length() > 1024)
      return; 
    this.log.put(level, description);
  }
}
