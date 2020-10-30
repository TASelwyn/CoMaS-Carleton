/*    */ package edu.carleton.cas.utility;
/*    */ 
/*    */ import edu.carleton.cas.logging.Logger;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.StringWriter;
/*    */ import java.util.Scanner;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WindowsRegistry
/*    */ {
/*    */   public static final String readRegistry(String location, String key) {
/*    */     try {
/* 25 */       Process process = Runtime.getRuntime().exec("reg query \"" + 
/* 26 */           location + "\" /v " + key);
/*    */       
/* 28 */       StreamReader reader = new StreamReader(process.getInputStream());
/* 29 */       reader.start();
/* 30 */       process.waitFor();
/* 31 */       reader.join();
/* 32 */       String output = reader.getResult();
/*    */       
/* 34 */       int index = output.indexOf(key);
/* 35 */       if (index < 0) {
/* 36 */         return null;
/*    */       }
/* 38 */       String value = output.substring(index + key.length());
/* 39 */       Scanner scanner = new Scanner(value.trim());
/* 40 */       String reg_sz = scanner.next();
/* 41 */       reg_sz = reg_sz.trim();
/* 42 */       if (!reg_sz.equals("REG_SZ")) {
/* 43 */         value = null;
/*    */       } else {
/* 45 */         value = scanner.nextLine();
/* 46 */       }  scanner.close();
/* 47 */       Logger.output(value.trim());
/* 48 */       return value.trim();
/* 49 */     } catch (Exception e) {
/* 50 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   static class StreamReader
/*    */     extends Thread {
/*    */     private InputStream is;
/* 57 */     private StringWriter sw = new StringWriter();
/*    */     
/*    */     public StreamReader(InputStream is) {
/* 60 */       this.is = is;
/*    */     }
/*    */     
/*    */     public void run() {
/*    */       try {
/*    */         int c;
/* 66 */         while ((c = this.is.read()) != -1)
/* 67 */           this.sw.write(c); 
/* 68 */       } catch (IOException c) {
/*    */         IOException iOException;
/*    */       } 
/*    */     }
/*    */     public String getResult() {
/* 73 */       return this.sw.toString();
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public static void main(String[] args) {
/* 79 */     System.out.println("HOME=" + System.getenv("USERPROFILE"));
/*    */ 
/*    */     
/* 82 */     String value = readRegistry(
/* 83 */         "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Personal");
/* 84 */     System.out.println("Personal=" + value + ".");
/*    */     
/* 86 */     value = readRegistry(
/* 87 */         "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Desktop");
/* 88 */     System.out.println("Desktop=" + value);
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\WindowsRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */