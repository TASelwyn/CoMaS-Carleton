/*    */ package edu.carleton.cas.file;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.zip.ZipEntry;
/*    */ import java.util.zip.ZipInputStream;
/*    */ 
/*    */ public class UnZip {
/* 12 */   private static final String BASE = String.valueOf(System.getProperty("user.home")) + File.separator + "scs" + File.separator;
/* 13 */   private static final String IFILE_CACHE = String.valueOf(BASE) + "cache" + File.separator;
/* 14 */   private static final String OFILE_CACHE = String.valueOf(BASE) + "exams" + File.separator;
/*    */   
/*    */   public static void main(String[] args) {
/* 17 */     UnZip unZip = new UnZip();
/* 18 */     unZip.doIt();
/*    */   }
/*    */   
/*    */   public int doIt() {
/* 22 */     System.out.println("Processing: " + IFILE_CACHE);
/* 23 */     File file = new File(IFILE_CACHE);
/* 24 */     File[] listOfFiles = file.listFiles();
/* 25 */     for (int i = 0; i < listOfFiles.length; i++) {
/* 26 */       String name = listOfFiles[i].getName();
/*    */       
/* 28 */       if (name.endsWith(".zip")) {
/* 29 */         String studentName = name.substring(0, name.length() - 4);
/* 30 */         if (!studentName.startsWith(".DS_S")) {
/* 31 */           System.out.println("Processing " + studentName + ": output in " + OFILE_CACHE + studentName);
/* 32 */           unZipIt(listOfFiles[i].getAbsolutePath(), String.valueOf(OFILE_CACHE) + studentName);
/*    */         } 
/*    */       } 
/*    */     } 
/* 36 */     return listOfFiles.length;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void unZipIt(String zipFile, String outputFolder) {
/* 46 */     byte[] buffer = new byte[1024];
/*    */ 
/*    */     
/*    */     try {
/* 50 */       File folder = new File(outputFolder);
/* 51 */       if (!folder.exists()) {
/* 52 */         folder.mkdirs();
/*    */       }
/*    */       
/* 55 */       ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
/* 56 */       ZipEntry ze = zis.getNextEntry();
/* 57 */       while (ze != null) {
/* 58 */         String ofileName = ze.getName();
/*    */ 
/*    */ 
/*    */         
/* 62 */         if (!ofileName.contains("_MACOSX")) {
/*    */           
/* 64 */           String fileName = ofileName.replace('\\', File.separatorChar);
/* 65 */           File newFile = new File(String.valueOf(outputFolder) + File.separator + fileName);
/* 66 */           (new File(newFile.getParent())).mkdirs();
/*    */           
/*    */           try {
/* 69 */             FileOutputStream fos = new FileOutputStream(newFile);
/*    */             
/*    */             int len;
/* 72 */             while ((len = zis.read(buffer)) > 0) {
/* 73 */               fos.write(buffer, 0, len);
/*    */             }
/* 75 */             fos.close();
/* 76 */           } catch (FileNotFoundException e) {
/* 77 */             System.err.println("Could not find: " + newFile.getAbsolutePath());
/*    */           } 
/*    */         } 
/* 80 */         ze = zis.getNextEntry();
/*    */       } 
/*    */       
/* 83 */       zis.closeEntry();
/* 84 */       zis.close();
/* 85 */       System.out.println("Finished processing: " + zipFile);
/* 86 */     } catch (IOException ex) {
/* 87 */       ex.printStackTrace();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\file\UnZip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */