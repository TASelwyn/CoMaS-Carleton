package edu.carleton.cas.resources;

import edu.carleton.cas.constants.ClientShared;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UnixFileTask extends AbstractFileTask {
  private String userName;
  
  public UnixFileTask(Logger logger, ResourceMonitor monitor) {
    super(logger, monitor);
    this.userName = System.getProperty("user.name").toLowerCase();
  }
  
  public boolean isIllegal(String line) {
    if (line.contains("/System/Library"))
      return false; 
    if (line.contains("/private/"))
      return false; 
    if (line.contains("/Applications/"))
      return false; 
    if (line.contains(ClientShared.DIR))
      return false; 
    return super.isIllegal(line);
  }
  
  public void run() {
    try {
      ProcessBuilder builder = new ProcessBuilder(new String[] { "lsof", "-u", this.userName });
      builder.redirectErrorStream(true);
      this.process = builder.start();
      InputStream stdout = this.process.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
      this.logger.begin();
      String line;
      while ((line = reader.readLine()) != null) {
        this.logger.log(line);
        if (isIllegal(line))
          this.monitor.notifyListeners("file", line); 
      } 
      this.logger.end();
    } catch (Exception exception) {
    
    } finally {
      close();
    } 
  }
}
