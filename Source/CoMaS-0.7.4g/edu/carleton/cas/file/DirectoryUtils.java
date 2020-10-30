package edu.carleton.cas.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;

public abstract class DirectoryUtils {
  public static void destroyDirectory(String dir) throws IOException {
    Path directory = Paths.get(dir, new String[0]);
    Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
          }
          
          public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
          }
        });
  }
  
  public static void copyDirectory(String source, String target) throws IOException {
    final Path targetPath = Paths.get(target, new String[0]);
    final Path sourcePath = Paths.get(source, new String[0]);
    Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
          public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)), (FileAttribute<?>[])new FileAttribute[0]);
            return FileVisitResult.CONTINUE;
          }
          
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.copy(file, targetPath.resolve(sourcePath.relativize(file)), new java.nio.file.CopyOption[0]);
            return FileVisitResult.CONTINUE;
          }
        });
  }
}
