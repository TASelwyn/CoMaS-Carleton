package edu.carleton.cas.utility;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

public class CustomResourceCreator {
  private static String INSERT = "===INSERT===";
  
  public static boolean generate(File file, Set<Map.Entry<String, String>> vars, File dir) throws IOException, FileNotFoundException {
    StringBuffer buff = new StringBuffer();
    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(new FileInputStream(file)));
      String inputLine;
      while ((inputLine = br.readLine()) != null) {
        if (inputLine.contains(INSERT)) {
          for (Map.Entry<String, String> var : vars) {
            String value = ((String)var.getValue()).toString();
            buff.append("var ");
            buff.append(var.getKey());
            buff.append(" = ");
            buff.append(value);
            buff.append(";\n");
          } 
          continue;
        } 
        buff.append(inputLine);
        buff.append("\n");
      } 
      br.close();
      File ofile = new File(dir, file.getName());
      OutputStream out = new BufferedOutputStream(new FileOutputStream(ofile));
      out.write(buff.toString().getBytes());
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    } 
    return true;
  }
  
  public static boolean generate(URL url, Set<Map.Entry<String, String>> vars, File dir) throws IOException, FileNotFoundException {
    StringBuffer buff = new StringBuffer();
    try {
      URLConnection conn = url.openConnection();
      BufferedReader br = new BufferedReader(
          new InputStreamReader(conn.getInputStream()));
      String inputLine;
      while ((inputLine = br.readLine()) != null) {
        if (inputLine.contains(INSERT)) {
          for (Map.Entry<String, String> var : vars) {
            String value = ((String)var.getValue()).toString();
            buff.append("var ");
            buff.append(var.getKey());
            buff.append(" = ");
            buff.append(value);
            buff.append(";\n");
          } 
          continue;
        } 
        buff.append(inputLine);
        buff.append("\n");
      } 
      br.close();
      File file = new File(dir, (new File(url.getFile())).getName());
      OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
      out.write(buff.toString().getBytes());
      out.close();
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    } 
    return true;
  }
}
