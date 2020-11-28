package edu.carleton.cas.resources;

import java.util.Properties;

public class VMCheck extends ResourceMonitor {
  private final VMCheckTask vmCheckTask;
  
  public VMCheck(Properties properties) {
    super("vmcheck", "vmcheck");
    this.vmCheckTask = new VMCheckTask(null, this, properties);
  }
  
  public void open() {
    this.timer.schedule(this.vmCheckTask, 10000L);
  }
  
  public void close() {
    try {
      super.close();
      this.timer.cancel();
      this.vmCheckTask.close();
    } catch (Exception exception) {}
  }
}
