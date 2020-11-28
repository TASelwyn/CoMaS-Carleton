package edu.carleton.cas.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {
  public static void pack(String sourceDirPath, String zipFilePath) throws IOException {
    Path p = Files.createFile(Paths.get(zipFilePath, new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
    Exception exception1 = null, exception2 = null;
  }
}
