/*     */ package edu.carleton.cas.utility;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.IOException;
/*     */ import java.util.Vector;
/*     */ import javax.media.MediaLocator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JpegMovieMaker
/*     */ {
/*     */   public static boolean makeVideo(String dir, String fileName, int screenWidth, int screenHeight, int interval) throws IOException {
/*     */     Dimension dim;
/*  27 */     Vector<String> imgLst = listOfJpegFiles(dir);
/*  28 */     if (imgLst.isEmpty()) {
/*  29 */       System.err.println("No jpg files in " + dir);
/*  30 */       return false;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  35 */     JpegImagesToMovie imageToMovie = new JpegImagesToMovie();
/*     */     MediaLocator oml;
/*  37 */     if ((oml = JpegImagesToMovie.createMediaLocator(fileName)) == null) {
/*  38 */       System.err.println("Cannot build media locator from: " + fileName);
/*  39 */       return false;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  45 */     if (screenWidth == 0 || screenHeight == 0) {
/*  46 */       dim = getImageDimension(imgLst.get(0));
/*     */     } else {
/*  48 */       dim = new Dimension(screenWidth, screenHeight);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  53 */     int framesPerSecond = Math.max(1, imgLst.size() / interval);
/*  54 */     return imageToMovie.doIt(dim.width, dim.height, framesPerSecond, imgLst, oml);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Vector<String> listOfJpegFiles(String dir) {
/*  61 */     File f = new File(dir);
/*  62 */     FileFilter filter = new FileFilter() {
/*     */         public boolean accept(File p) {
/*  64 */           return p.getName().endsWith(".jpg");
/*     */         }
/*     */       };
/*  67 */     Vector<String> v = new Vector<>();
/*  68 */     if (f.isDirectory()) {
/*  69 */       File[] acctFiles = f.listFiles(filter); byte b; int i; File[] arrayOfFile1;
/*  70 */       for (i = (arrayOfFile1 = acctFiles).length, b = 0; b < i; ) { File file = arrayOfFile1[b];
/*     */         try {
/*  72 */           if (VERBOSE)
/*  73 */             System.out.println("Processing " + file.getCanonicalPath()); 
/*  74 */         } catch (IOException e) {
/*  75 */           System.err.println("I/O error: " + e.getMessage());
/*     */         } 
/*  77 */         v.add(file.getAbsolutePath()); b++; }
/*     */     
/*     */     } 
/*  80 */     return v;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Dimension getImageDimension(String resourceFile) throws IOException {
/*  88 */     Exception exception1 = null, exception2 = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*     */     
/*     */     } finally {
/*  99 */       exception2 = null; if (exception1 == null) { exception1 = exception2; } else if (exception1 != exception2) { exception1.addSuppressed(exception2); }
/*     */     
/*     */     } 
/*     */ 
/*     */     
/* 104 */     throw new IOException("Could not get image size for " + resourceFile);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 113 */   private static final String INPUT_DIR = String.valueOf(System.getProperty("user.home")) + "/comp1405-exam-2017/";
/*     */ 
/*     */   
/*     */   private static boolean VERBOSE = false;
/*     */   
/*     */   private static boolean DELETE_JPG = true;
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/* 122 */     if (args.length > 0 && 
/* 123 */       args[1].equals("-v")) {
/* 124 */       VERBOSE = true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 137 */     int movies = 0;
/* 138 */     File file = new File(INPUT_DIR);
/* 139 */     File[] listOfFiles = file.listFiles();
/* 140 */     for (int i = 0; i < listOfFiles.length; i++) {
/* 141 */       String dir = String.valueOf(listOfFiles[i].getAbsolutePath()) + "/screens/";
/* 142 */       String movie = String.valueOf(dir) + listOfFiles[i].getName() + ".mov";
/*     */       try {
/* 144 */         if (!listOfFiles[i].getName().startsWith(".DS_")) {
/* 145 */           System.out.println("Processing " + listOfFiles[i].getName());
/* 146 */           if (makeVideo(dir, movie, 0, 0, 30))
/* 147 */             movies++; 
/* 148 */           if (DELETE_JPG) {
/*     */             
/* 150 */             File folder = new File(dir);
/* 151 */             File[] fList = folder.listFiles();
/* 152 */             for (int j = 0; j < fList.length; j++) {
/* 153 */               String pes = fList[j].getName();
/* 154 */               if (pes.endsWith(".jpg")) {
/* 155 */                 fList[j].delete();
/*     */               }
/*     */             } 
/*     */           } 
/*     */         } 
/* 160 */       } catch (IOException e) {
/* 161 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/* 164 */     System.out.println("There were " + movies + " movies produced");
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\JpegMovieMaker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */