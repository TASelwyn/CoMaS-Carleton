package edu.carleton.cas.resources;

import java.util.TimerTask;

public abstract class AbstractTask extends TimerTask implements LegalityCheck {
  protected Logger logger;
  
  protected ResourceMonitor monitor;
  
  protected Process process;
  
  public AbstractTask(Logger logger, ResourceMonitor monitor) {
    this.logger = logger;
    this.monitor = monitor;
  }
  
  public void close() {
    if (this.process != null) {
      this.process.destroyForcibly();
      this.process = null;
    } 
  }
}
