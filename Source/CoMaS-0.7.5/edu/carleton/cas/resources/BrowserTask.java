package edu.carleton.cas.resources;

import edu.carleton.cas.logging.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;

public class BrowserTask extends Thread {
  private static final String[] browserStartCmd = new String[] { "open -a Safari %s", 
      "rundll32 url.dll,FileProtocolHandler %s", 
      "firefox %s", 
      "google-chrome %s" };
  
  private String cmd;
  
  private final String os;
  
  private final File folder;
  
  private Process process;
  
  private Properties properties;
  
  public BrowserTask(File folder, Properties properties) {
    this.properties = properties;
    this.os = System.getProperty("os.name").toLowerCase();
    Logger.debug(Level.INFO, this.os);
    if (this.os.startsWith("mac os x")) {
      this.cmd = browserStartCmd[0];
    } else if (this.os.indexOf("win") > -1) {
      this.cmd = browserStartCmd[1];
    } else {
      this.cmd = browserStartCmd[2];
    } 
    this.folder = folder;
  }
  
  public void run() {
    File[] files = this.folder.listFiles(new ToolFilter(null));
    if (this.os.startsWith("mac os x") || this.os.indexOf("win") > -1) {
      byte b;
      int i;
      File[] arrayOfFile;
      for (i = (arrayOfFile = files).length, b = 0; b < i; ) {
        File file = arrayOfFile[b];
        runCommand(file.getAbsolutePath());
        b++;
      } 
    } else if (this.os.indexOf("nix") >= 0 || this.os.indexOf("nux") >= 0) {
      if (!runLinuxCommand(files)) {
        this.cmd = browserStartCmd[3];
        runLinuxCommand(files);
      } 
    } 
  }
  
  private void runCommand(String command) {
    String cmdToRun = String.format(this.cmd, new Object[] { command });
    try {
      Logger.debug(Level.INFO, cmdToRun);
      this.process = Runtime.getRuntime().exec(cmdToRun);
      InputStream stdout = this.process.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
      String line;
      while ((line = reader.readLine()) != null)
        Logger.debug(Level.FINE, line); 
    } catch (Exception e) {
      Logger.log(Level.WARNING, "Failed to run " + cmdToRun + ": ", e);
    } finally {
      close();
    } 
  }
  
  private boolean runLinuxCommand(File[] files) {
    boolean returnValue;
    StringBuffer buff = new StringBuffer();
    byte b;
    int i;
    File[] arrayOfFile;
    for (i = (arrayOfFile = files).length, b = 0; b < i; ) {
      File file = arrayOfFile[b];
      buff.append(" file://");
      buff.append(file.getAbsolutePath());
      b++;
    } 
    String cmdToRun = String.format(this.cmd, new Object[] { buff.toString() });
    try {
      Logger.debug(Level.INFO, cmdToRun);
      this.process = Runtime.getRuntime().exec(new String[] { "sh", "-c", cmdToRun });
      InputStream stdout = this.process.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
      String line;
      while ((line = reader.readLine()) != null)
        Logger.debug(Level.FINE, line); 
      returnValue = true;
    } catch (Exception e) {
      Logger.log(Level.WARNING, "Failed to run " + cmdToRun + ": ", e);
      returnValue = false;
    } finally {
      close();
    } 
    return returnValue;
  }
  
  public void close() {}
  
  private class ToolFilter implements FileFilter {
    private ToolFilter() {}
    
    public boolean accept(File pathname) {
      int i = 1;
      String tool = BrowserTask.this.properties.getProperty("tool.load." + i);
      while (tool != null) {
        if (pathname.getAbsolutePath().endsWith(tool))
          return true; 
        i++;
        tool = BrowserTask.this.properties.getProperty("tool.load." + i);
      } 
      return false;
    }
  }
}
