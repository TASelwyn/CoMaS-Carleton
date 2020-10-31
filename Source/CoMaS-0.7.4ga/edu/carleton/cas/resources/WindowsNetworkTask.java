package edu.carleton.cas.resources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WindowsNetworkTask extends AbstractNetworkTask {
  int indexOfForeign = -1;
  
  boolean indexOk = false;
  
  public WindowsNetworkTask(Logger logger, ResourceMonitor monitor) {
    super(logger, monitor);
  }
  
  public boolean isIllegal(String line) {
    if (!this.indexOk) {
      this.indexOfForeign = line.indexOf("Foreign");
      this.indexOk = (this.indexOfForeign > -1);
    } 
    if (this.indexOk && line.contains("ESTABLISHED")) {
      int end = line.indexOf(":", this.indexOfForeign);
      String remoteHost = line.substring(this.indexOfForeign, end);
      if (this.hosts.contains(remoteHost))
        return false; 
      if (remoteHost.contains("akamaitechnologies"))
        return false; 
      if (line.contains("127.0.0.1"))
        return false; 
      if (remoteHost.startsWith("dhcp-"))
        return false; 
      if (remoteHost.startsWith("localhost"))
        return false; 
      if (remoteHost.startsWith(this.localHost))
        return false; 
      if (isAllowed(remoteHost))
        return false; 
      return true;
    } 
    return false;
  }
  
  public void run() {
    try {
      ProcessBuilder builder = new ProcessBuilder(new String[] { "netstat" });
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
