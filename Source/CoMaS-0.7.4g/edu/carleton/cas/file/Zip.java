/*    */ package edu.carleton.cas.file;
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.Paths;
/*    */ import java.nio.file.attribute.FileAttribute;
/*    */ import java.util.zip.ZipEntry;
/*    */ import java.util.zip.ZipOutputStream;
/*    */ 
/*    */ public class Zip {
/*    */   public static void pack(String sourceDirPath, String zipFilePath) throws IOException {
/* 12 */     Path p = Files.createFile(Paths.get(zipFilePath, new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
/* 13 */     Exception exception1 = null, exception2 = null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\file\Zip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */