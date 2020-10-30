package edu.carleton.cas.resources;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Logger {
  BufferedWriter writer;
  
  private static final boolean LOGGING = false;
  
  public void open(File file) throws FileNotFoundException {}
  
  public void open(String name) throws FileNotFoundException {}
  
  public void close() throws IOException {}
  
  public void begin() throws IOException {}
  
  public void log(String log) throws IOException {}
  
  public void end() throws IOException {}
}


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\Logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */