package edu.carleton.cas.utility;

import edu.carleton.cas.logging.Logger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

public class HTMLFileViewGenerator {
  public static void create(String title, String fileToView, String htmlFile) {
    String newline = System.getProperty("line.separator");
    File file = new File(htmlFile);
    FileWriter fw = null;
    BufferedWriter bw = null;
    try {
      fw = new FileWriter(file);
      bw = new BufferedWriter(fw);
      bw.write("<html><head><title>");
      bw.write(title);
      bw.write("</title></head>");
      bw.write(String.valueOf(newline) + "<script>" + newline);
      bw.write("   window.location.href = ");
      bw.write("\"file:///" + convert(fileToView) + "\"");
      bw.write(String.valueOf(newline) + "</script>" + newline + "</body>" + newline + "</html>");
    } catch (IOException e) {
      Logger.log(Level.WARNING, "", "Could not create " + fileToView + " viewer");
    } finally {
      try {
        if (bw != null)
          bw.close(); 
        if (fw != null)
          fw.close(); 
      } catch (IOException iOException) {}
    } 
  }
  
  private static String convert(String name) {
    StringBuffer b = new StringBuffer();
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (c == '\\') {
        b.append("\\\\");
      } else {
        b.append(c);
      } 
    } 
    return b.toString();
  }
}
