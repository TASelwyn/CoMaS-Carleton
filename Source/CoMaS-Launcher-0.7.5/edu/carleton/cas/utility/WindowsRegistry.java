package edu.carleton.cas.utility;

import edu.carleton.cas.logging.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Scanner;

public class WindowsRegistry {
  public static final String readRegistry(String location, String key) {
    try {
      Process process = Runtime.getRuntime().exec("reg query \"" + 
          location + "\" /v " + key);
      StreamReader reader = new StreamReader(process.getInputStream());
      reader.start();
      process.waitFor();
      reader.join();
      String output = reader.getResult();
      int index = output.indexOf(key);
      if (index < 0)
        return null; 
      String value = output.substring(index + key.length());
      Scanner scanner = new Scanner(value.trim());
      String reg_sz = scanner.next();
      reg_sz = reg_sz.trim();
      if (!reg_sz.equals("REG_SZ")) {
        value = null;
      } else {
        value = scanner.nextLine();
      } 
      scanner.close();
      Logger.output(value.trim());
      return value.trim();
    } catch (Exception e) {
      return null;
    } 
  }
  
  static class StreamReader extends Thread {
    private InputStream is;
    
    private StringWriter sw = new StringWriter();
    
    public StreamReader(InputStream is) {
      this.is = is;
    }
    
    public void run() {
      try {
        int c;
        while ((c = this.is.read()) != -1)
          this.sw.write(c); 
      } catch (IOException c) {
        IOException iOException;
      } 
    }
    
    public String getResult() {
      return this.sw.toString();
    }
  }
  
  public static void main(String[] args) {
    System.out.println("HOME=" + System.getenv("USERPROFILE"));
    String value = readRegistry(
        "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Personal");
    System.out.println("Personal=" + value + ".");
    value = readRegistry(
        "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Desktop");
    System.out.println("Desktop=" + value);
  }
}
