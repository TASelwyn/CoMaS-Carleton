package edu.carleton.cas.resources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UnixNetworkTask extends AbstractNetworkTask {
  public UnixNetworkTask(Logger logger, ResourceMonitor monitor) {
    super(logger, monitor);
  }
  
  public boolean isIllegal(String line) {
    if (line.startsWith("com.apple"))
      return false; 
    if (line.startsWith("Mail"))
      return false; 
    if (line.startsWith("Dropbox"))
      return false; 
    if (line.startsWith("cloudd"))
      return false; 
    if (line.contains("akamaitechnologies"))
      return false; 
    if (line.contains("ubuntu"))
      return false; 
    if (line.contains("(ESTABLISHED)")) {
      int index = line.indexOf("->");
      int end = line.indexOf(":", index);
      String remoteHost = line.substring(index + 2, end);
      if (isAllowed(remoteHost))
        return false; 
      if (remoteHost.startsWith("dhcp-"))
        return false; 
      if (remoteHost.equals("localhost"))
        return false; 
      if (remoteHost.equals(this.localHost))
        return false; 
      return true;
    } 
    return false;
  }
  
  public void run() {
    try {
      ProcessBuilder builder = new ProcessBuilder(new String[] { "lsof", "-i" });
      builder.redirectErrorStream(true);
      this.process = builder.start();
      InputStream stdout = this.process.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
      this.logger.begin();
      String line;
      while ((line = reader.readLine()) != null) {
        this.logger.log(line);
        if (isIllegal(line))
          this.monitor.notifyListeners("network", line); 
      } 
      this.logger.end();
    } catch (Exception exception) {
    
    } finally {
      close();
    } 
  }
}
