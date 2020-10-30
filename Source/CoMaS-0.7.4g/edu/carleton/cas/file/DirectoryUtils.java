/*    */ package edu.carleton.cas.file;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.FileVisitResult;
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
/*    */ import java.nio.file.SimpleFileVisitor;
/*    */ import java.nio.file.attribute.BasicFileAttributes;
/*    */ import java.nio.file.attribute.FileAttribute;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class DirectoryUtils
/*    */ {
/*    */   public static void destroyDirectory(String dir) throws IOException {
/* 20 */     Path directory = Paths.get(dir, new String[0]);
/*    */     
/* 22 */     Files.walkFileTree(directory, new SimpleFileVisitor<Path>()
/*    */         {
/*    */           public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/* 25 */             Files.delete(file);
/* 26 */             return FileVisitResult.CONTINUE;
/*    */           }
/*    */ 
/*    */           
/*    */           public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
/* 31 */             Files.delete(dir);
/* 32 */             return FileVisitResult.CONTINUE;
/*    */           }
/*    */         });
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void copyDirectory(String source, String target) throws IOException {
/* 45 */     final Path targetPath = Paths.get(target, new String[0]);
/* 46 */     final Path sourcePath = Paths.get(source, new String[0]);
/* 47 */     Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>()
/*    */         {
/*    */           public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
/*    */           {
/* 51 */             Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)), (FileAttribute<?>[])new FileAttribute[0]);
/* 52 */             return FileVisitResult.CONTINUE;
/*    */           }
/*    */ 
/*    */           
/*    */           public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/* 57 */             Files.copy(file, targetPath.resolve(sourcePath.relativize(file)), new java.nio.file.CopyOption[0]);
/* 58 */             return FileVisitResult.CONTINUE;
/*    */           }
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\file\DirectoryUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */