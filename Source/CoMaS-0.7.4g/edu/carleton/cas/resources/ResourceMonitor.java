package edu.carleton.cas.resources;

import edu.carleton.cas.constants.Shared;
import edu.carleton.cas.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.logging.Level;

public class ResourceMonitor extends AbstractResourceMonitor {
  Timer timer;
  
  boolean osIsWindows;
  
  String name;
  
  Logger logger;
  
  AbstractTask task;
  
  Properties properties;
  
  public ResourceMonitor(String name, String type) {
    this.name = name;
    this.type = type;
    this.timer = new Timer();
    this.properties = new Properties();
    String os = System.getProperty("os.name").toLowerCase();
    this.osIsWindows = (os.indexOf("win") > -1);
  }
  
  public ResourceMonitor(String name, String type, String activityDirectoryName, Properties properties) {
    this.properties = properties;
    this.name = name;
    this.type = type;
    String os = System.getProperty("os.name").toLowerCase();
    this.osIsWindows = (os.indexOf("win") > -1);
    this.timer = new Timer();
    try {
      this.logger = new Logger();
      this.logger.open(new File(String.valueOf(activityDirectoryName) + File.separator + "logs" + File.separator + name));
    } catch (Exception e) {
      this.logger = null;
    } 
  }
  
  public void open() {
    this.task = null;
    if (this.name.startsWith("network") && Shared.NETWORK_MONITORING) {
      if (this.osIsWindows) {
        this.task = new WindowsNetworkTask(this.logger, this);
      } else {
        this.task = new UnixNetworkTask(this.logger, this);
      } 
      Logger.log(Level.CONFIG, "Network monitoring is enabled", "");
    } 
    if (this.name.startsWith("file") && Shared.FILE_MONITORING) {
      if (this.osIsWindows) {
        this.task = new WindowsFileTask(this.logger, this);
      } else {
        this.task = new UnixFileTask(this.logger, this);
      } 
      Logger.log(Level.CONFIG, "File monitoring is enabled", "");
    } 
    if (this.task != null)
      this.timer.scheduleAtFixedRate(this.task, 10000L, 60000L); 
  }
  
  public void close() {
    try {
      this.logger.close();
    } catch (IOException iOException) {}
    if (this.task != null)
      this.task.close(); 
    this.timer.cancel();
  }
  
  public String getProperty(String name) {
    return this.properties.getProperty(name);
  }
  
  public String getPropertyOrDefault(String name, String defaultValue) {
    return this.properties.getProperty(name, defaultValue);
  }
}
