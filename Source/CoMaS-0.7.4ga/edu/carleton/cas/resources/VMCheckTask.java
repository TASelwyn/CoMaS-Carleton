package edu.carleton.cas.resources;

import edu.carleton.cas.logging.Logger;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;

public class VMCheckTask extends AbstractTask {
  private String os;
  
  private final Properties properties;
  
  public VMCheckTask(Logger logger, ResourceMonitor monitor, Properties properties) {
    super(logger, monitor);
    this.properties = properties;
    this.os = System.getProperty("os.name").toLowerCase();
    if (this.os.startsWith("mac os x")) {
      this.os = "macOS";
    } else if (this.os.indexOf("win") > -1) {
      this.os = "windows";
    } else if (this.os.indexOf("nix") >= 0 || this.os.indexOf("nux") >= 0) {
      this.os = "linux";
    } else {
      this.os = "unknown";
    } 
  }
  
  public boolean isIllegal(String line) {
    if (line == null)
      return false; 
    String toCheck = line.toLowerCase();
    int i = 1;
    String vendor = this.properties.getProperty("vm.vendor." + i);
    while (vendor != null) {
      if (toCheck.contains(vendor.trim()))
        return true; 
      i++;
      vendor = this.properties.getProperty("vm.vendor." + i);
    } 
    return false;
  }
  
  public void run() {
    if (this.os.equals("unknown")) {
      this.monitor.notifyListeners("exception", "Unknown operating system detected");
    } else {
      int i = 1;
      String cmd = this.properties.getProperty("vm." + this.os + "." + i);
      while (cmd != null) {
        Logger.log(Level.FINE, "VM test: ", cmd);
        runTest(cmd.trim());
        i++;
        cmd = this.properties.getProperty("vm." + this.os + "." + i);
      } 
    } 
  }
  
  public void runTest(String cmd) {
    try {
      this.process = Runtime.getRuntime().exec(cmd);
      InputStream stdout = this.process.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
      String line;
      while ((line = reader.readLine()) != null) {
        if (isIllegal(line))
          this.monitor.notifyListeners("vm", line); 
      } 
    } catch (Exception e) {
      this.monitor.notifyListeners("exception", e.toString());
    } finally {
      close();
    } 
  }
}
