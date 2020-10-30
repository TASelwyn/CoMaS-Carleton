package edu.carleton.cas.utility;

import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Vector;
import javax.media.MediaLocator;

public class JpegMovieMaker {
  public static boolean makeVideo(String dir, String fileName, int screenWidth, int screenHeight, int interval) throws IOException {
    Dimension dim;
    Vector<String> imgLst = listOfJpegFiles(dir);
    if (imgLst.isEmpty()) {
      System.err.println("No jpg files in " + dir);
      return false;
    } 
    JpegImagesToMovie imageToMovie = new JpegImagesToMovie();
    MediaLocator oml;
    if ((oml = JpegImagesToMovie.createMediaLocator(fileName)) == null) {
      System.err.println("Cannot build media locator from: " + fileName);
      return false;
    } 
    if (screenWidth == 0 || screenHeight == 0) {
      dim = getImageDimension(imgLst.get(0));
    } else {
      dim = new Dimension(screenWidth, screenHeight);
    } 
    int framesPerSecond = Math.max(1, imgLst.size() / interval);
    return imageToMovie.doIt(dim.width, dim.height, framesPerSecond, imgLst, oml);
  }
  
  private static Vector<String> listOfJpegFiles(String dir) {
    File f = new File(dir);
    FileFilter filter = new FileFilter() {
        public boolean accept(File p) {
          return p.getName().endsWith(".jpg");
        }
      };
    Vector<String> v = new Vector<>();
    if (f.isDirectory()) {
      File[] acctFiles = f.listFiles(filter);
      byte b;
      int i;
      File[] arrayOfFile1;
      for (i = (arrayOfFile1 = acctFiles).length, b = 0; b < i; ) {
        File file = arrayOfFile1[b];
        try {
          if (VERBOSE)
            System.out.println("Processing " + file.getCanonicalPath()); 
        } catch (IOException e) {
          System.err.println("I/O error: " + e.getMessage());
        } 
        v.add(file.getAbsolutePath());
        b++;
      } 
    } 
    return v;
  }
  
  private static Dimension getImageDimension(String resourceFile) throws IOException {
    Exception exception1 = null, exception2 = null;
    try {
    
    } finally {
      exception2 = null;
      if (exception1 == null) {
        exception1 = exception2;
      } else if (exception1 != exception2) {
        exception1.addSuppressed(exception2);
      } 
    } 
    throw new IOException("Could not get image size for " + resourceFile);
  }
  
  private static final String INPUT_DIR = String.valueOf(System.getProperty("user.home")) + "/comp1405-exam-2017/";
  
  private static boolean VERBOSE = false;
  
  private static boolean DELETE_JPG = true;
  
  public static void main(String[] args) {
    if (args.length > 0 && 
      args[1].equals("-v"))
      VERBOSE = true; 
    int movies = 0;
    File file = new File(INPUT_DIR);
    File[] listOfFiles = file.listFiles();
    for (int i = 0; i < listOfFiles.length; i++) {
      String dir = String.valueOf(listOfFiles[i].getAbsolutePath()) + "/screens/";
      String movie = String.valueOf(dir) + listOfFiles[i].getName() + ".mov";
      try {
        if (!listOfFiles[i].getName().startsWith(".DS_")) {
          System.out.println("Processing " + listOfFiles[i].getName());
          if (makeVideo(dir, movie, 0, 0, 30))
            movies++; 
          if (DELETE_JPG) {
            File folder = new File(dir);
            File[] fList = folder.listFiles();
            for (int j = 0; j < fList.length; j++) {
              String pes = fList[j].getName();
              if (pes.endsWith(".jpg"))
                fList[j].delete(); 
            } 
          } 
        } 
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } 
    System.out.println("There were " + movies + " movies produced");
  }
}
