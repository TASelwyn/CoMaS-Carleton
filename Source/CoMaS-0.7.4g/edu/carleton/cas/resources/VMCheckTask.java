/*    */ package edu.carleton.cas.resources;
/*    */ 
/*    */ import edu.carleton.cas.logging.Logger;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.util.Properties;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VMCheckTask
/*    */   extends AbstractTask
/*    */ {
/*    */   private String os;
/*    */   private final Properties properties;
/*    */   
/*    */   public VMCheckTask(Logger logger, ResourceMonitor monitor, Properties properties) {
/* 28 */     super(logger, monitor);
/* 29 */     this.properties = properties;
/* 30 */     this.os = System.getProperty("os.name").toLowerCase();
/* 31 */     if (this.os.startsWith("mac os x")) {
/* 32 */       this.os = "macOS";
/* 33 */     } else if (this.os.indexOf("win") > -1) {
/* 34 */       this.os = "windows";
/* 35 */     } else if (this.os.indexOf("nix") >= 0 || this.os.indexOf("nux") >= 0) {
/* 36 */       this.os = "linux";
/*    */     } else {
/* 38 */       this.os = "unknown";
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isIllegal(String line) {
/* 44 */     if (line == null)
/* 45 */       return false; 
/* 46 */     String toCheck = line.toLowerCase();
/* 47 */     int i = 1;
/* 48 */     String vendor = this.properties.getProperty("vm.vendor." + i);
/* 49 */     while (vendor != null) {
/* 50 */       if (toCheck.contains(vendor.trim())) {
/* 51 */         return true;
/*    */       }
/* 53 */       i++;
/* 54 */       vendor = this.properties.getProperty("vm.vendor." + i);
/*    */     } 
/*    */     
/* 57 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void run() {
/* 63 */     if (this.os.equals("unknown")) {
/* 64 */       this.monitor.notifyListeners("exception", "Unknown operating system detected");
/*    */     } else {
/*    */       
/* 67 */       int i = 1;
/* 68 */       String cmd = this.properties.getProperty("vm." + this.os + "." + i);
/* 69 */       while (cmd != null) {
/* 70 */         Logger.log(Level.FINE, "VM test: ", cmd);
/* 71 */         runTest(cmd.trim());
/* 72 */         i++;
/* 73 */         cmd = this.properties.getProperty("vm." + this.os + "." + i);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void runTest(String cmd) {
/*    */     try {
/* 81 */       this.process = Runtime.getRuntime().exec(cmd);
/*    */       
/* 83 */       InputStream stdout = this.process.getInputStream();
/* 84 */       BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
/*    */       
/*    */       String line;
/* 87 */       while ((line = reader.readLine()) != null) {
/* 88 */         if (isIllegal(line))
/* 89 */           this.monitor.notifyListeners("vm", line); 
/*    */       } 
/* 91 */     } catch (Exception e) {
/* 92 */       this.monitor.notifyListeners("exception", e.toString());
/*    */     } finally {
/* 94 */       close();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\VMCheckTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */