/*    */ package edu.carleton.cas.utility;
/*    */ 
/*    */ import edu.carleton.cas.logging.Logger;
/*    */ import java.io.IOException;
/*    */ import java.nio.file.FileStore;
/*    */ import java.nio.file.FileSystems;
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.text.NumberFormat;
/*    */ import java.util.logging.Level;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DriveSpace
/*    */ {
/*    */   public static void main(String[] args) {
/* 23 */     NumberFormat nf = NumberFormat.getNumberInstance();
/* 24 */     for (Path root : FileSystems.getDefault().getRootDirectories()) {
/*    */       
/* 26 */       System.out.print(root + ": ");
/*    */       try {
/* 28 */         FileStore store = Files.getFileStore(root);
/* 29 */         System.out.println("available=" + nf.format(store.getUsableSpace()) + 
/* 30 */             ", total=" + nf.format(store.getTotalSpace()) + 
/* 31 */             ", %free=" + 
/* 32 */             nf.format(store.getUsableSpace() * 100.0D / store.getTotalSpace()));
/* 33 */       } catch (IOException e) {
/* 34 */         System.out.println("error querying space: " + e.toString());
/*    */       } 
/*    */     } 
/*    */     
/* 38 */     System.out.println("\nCheck using DriveSpace.free(): " + nf.format(free()) + "%");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static double free() {
/* 46 */     double min = 100.0D;
/* 47 */     for (Path root : FileSystems.getDefault().getRootDirectories()) {
/*    */       try {
/* 49 */         FileStore store = Files.getFileStore(root);
/* 50 */         double actual = store.getUsableSpace() * 100.0D / store.getTotalSpace();
/* 51 */         if (actual < min)
/* 52 */           min = actual; 
/* 53 */       } catch (IOException e) {
/* 54 */         Logger.log(Level.WARNING, "error querying " + root + " space: ", e.toString());
/*    */       } 
/*    */     } 
/* 57 */     return min;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static double freeMB() {
/* 65 */     double min = 1.0E30D;
/* 66 */     for (Path root : FileSystems.getDefault().getRootDirectories()) {
/*    */       try {
/* 68 */         FileStore store = Files.getFileStore(root);
/* 69 */         double actual = store.getUsableSpace();
/* 70 */         if (actual < min)
/* 71 */           min = actual; 
/* 72 */       } catch (IOException e) {
/* 73 */         Logger.log(Level.WARNING, "error querying " + root + " space: ", e.toString());
/*    */       } 
/*    */     } 
/* 76 */     return min / 1000000.0D;
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\DriveSpace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */