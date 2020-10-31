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
