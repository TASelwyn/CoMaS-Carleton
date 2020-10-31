package edu.carleton.cas.resources;

import java.util.ArrayList;
import java.util.Properties;

public abstract class AbstractFileTask extends AbstractTask {
  protected static ArrayList<String> FILE_TYPES = new ArrayList<>();
  
  static {
    FILE_TYPES.add(".pdf");
    FILE_TYPES.add(".txt");
    FILE_TYPES.add(".doc");
    FILE_TYPES.add(".docx");
    FILE_TYPES.add(".xls");
    FILE_TYPES.add(".xlsx");
    FILE_TYPES.add(".ppt");
    FILE_TYPES.add(".pptx");
    FILE_TYPES.add(".py");
    FILE_TYPES.add(".java");
    FILE_TYPES.add(".html");
    FILE_TYPES.add(".htm");
  }
  
  public AbstractFileTask(Logger logger, ResourceMonitor monitor) {
    super(logger, monitor);
  }
  
  public boolean isIllegal(String line) {
    if (line.endsWith("exam-system-log.html"))
      return false; 
    if (line.endsWith("comas-system-log.html"))
      return false; 
    for (String type : FILE_TYPES) {
      if (line.endsWith(type))
        return true; 
    } 
    return false;
  }
  
  public static void configure(Properties properties) {
    int i = 1;
    String ftype = properties.getProperty("monitor.file.type." + i);
    while (ftype != null) {
      ftype = ftype.trim();
      if (!FILE_TYPES.contains(ftype))
        FILE_TYPES.add(ftype); 
      i++;
      ftype = properties.getProperty("monitor.file.type." + i);
    } 
  }
}
