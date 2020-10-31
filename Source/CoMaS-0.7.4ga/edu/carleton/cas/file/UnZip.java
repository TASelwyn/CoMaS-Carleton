package edu.carleton.cas.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {
  private static final String BASE = String.valueOf(System.getProperty("user.home")) + File.separator + "scs" + File.separator;
  
  private static final String IFILE_CACHE = String.valueOf(BASE) + "cache" + File.separator;
  
  private static final String OFILE_CACHE = String.valueOf(BASE) + "exams" + File.separator;
  
  public static void main(String[] args) {
    UnZip unZip = new UnZip();
    unZip.doIt();
  }
  
  public int doIt() {
    System.out.println("Processing: " + IFILE_CACHE);
    File file = new File(IFILE_CACHE);
    File[] listOfFiles = file.listFiles();
    for (int i = 0; i < listOfFiles.length; i++) {
      String name = listOfFiles[i].getName();
      if (name.endsWith(".zip")) {
        String studentName = name.substring(0, name.length() - 4);
        if (!studentName.startsWith(".DS_S")) {
          System.out.println("Processing " + studentName + ": output in " + OFILE_CACHE + studentName);
          unZipIt(listOfFiles[i].getAbsolutePath(), String.valueOf(OFILE_CACHE) + studentName);
        } 
      } 
    } 
    return listOfFiles.length;
  }
  
  public void unZipIt(String zipFile, String outputFolder) {
    byte[] buffer = new byte[1024];
    try {
      File folder = new File(outputFolder);
      if (!folder.exists())
        folder.mkdirs(); 
      ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
      ZipEntry ze = zis.getNextEntry();
      while (ze != null) {
        String ofileName = ze.getName();
        if (!ofileName.contains("_MACOSX")) {
          String fileName = ofileName.replace('\\', File.separatorChar);
          File newFile = new File(String.valueOf(outputFolder) + File.separator + fileName);
          (new File(newFile.getParent())).mkdirs();
          try {
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0)
              fos.write(buffer, 0, len); 
            fos.close();
          } catch (FileNotFoundException e) {
            System.err.println("Could not find: " + newFile.getAbsolutePath());
          } 
        } 
        ze = zis.getNextEntry();
      } 
      zis.closeEntry();
      zis.close();
      System.out.println("Finished processing: " + zipFile);
    } catch (IOException ex) {
      ex.printStackTrace();
    } 
  }
}
