package edu.carleton.cas.resources;

import edu.carleton.cas.constants.Shared;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WindowsFileTask extends AbstractFileTask {
  private String cmd;
  
  public WindowsFileTask(Logger logger, ResourceMonitor monitor) {
    super(logger, monitor);
    this.cmd = String.valueOf(Shared.DOWNLOADS_DIR) + "handle.exe";
  }
  
  public boolean isIllegal(String line) {
    if (line.contains(":\\Windows"))
      return false; 
    if (line.contains(Shared.DIR))
      return false; 
    return super.isIllegal(line);
  }
  
  public void run() {
    try {
      ProcessBuilder builder = new ProcessBuilder(new String[] { this.cmd, "-accepteula" });
      builder.redirectErrorStream(true);
      this.process = builder.start();
      InputStream stdout = this.process.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
      this.logger.begin();
      String line;
      while ((line = reader.readLine()) != null) {
        this.logger.log(line);
        if (line.contains("File") && 
          isIllegal(line))
          this.monitor.notifyListeners("file", line); 
      } 
      this.logger.end();
    } catch (Exception exception) {
    
    } finally {
      close();
    } 
  }
}
