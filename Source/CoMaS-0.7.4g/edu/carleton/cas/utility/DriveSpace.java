package edu.carleton.cas.utility;

import edu.carleton.cas.logging.Logger;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.logging.Level;

public class DriveSpace {
  public static void main(String[] args) {
    NumberFormat nf = NumberFormat.getNumberInstance();
    for (Path root : FileSystems.getDefault().getRootDirectories()) {
      System.out.print(root + ": ");
      try {
        FileStore store = Files.getFileStore(root);
        System.out.println("available=" + nf.format(store.getUsableSpace()) + 
            ", total=" + nf.format(store.getTotalSpace()) + 
            ", %free=" + 
            nf.format(store.getUsableSpace() * 100.0D / store.getTotalSpace()));
      } catch (IOException e) {
        System.out.println("error querying space: " + e.toString());
      } 
    } 
    System.out.println("\nCheck using DriveSpace.free(): " + nf.format(free()) + "%");
  }
  
  public static double free() {
    double min = 100.0D;
    for (Path root : FileSystems.getDefault().getRootDirectories()) {
      try {
        FileStore store = Files.getFileStore(root);
        double actual = store.getUsableSpace() * 100.0D / store.getTotalSpace();
        if (actual < min)
          min = actual; 
      } catch (IOException e) {
        Logger.log(Level.WARNING, "error querying " + root + " space: ", e.toString());
      } 
    } 
    return min;
  }
  
  public static double freeMB() {
    double min = 1.0E30D;
    for (Path root : FileSystems.getDefault().getRootDirectories()) {
      try {
        FileStore store = Files.getFileStore(root);
        double actual = store.getUsableSpace();
        if (actual < min)
          min = actual; 
      } catch (IOException e) {
        Logger.log(Level.WARNING, "error querying " + root + " space: ", e.toString());
      } 
    } 
    return min / 1000000.0D;
  }
}
