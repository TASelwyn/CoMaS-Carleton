/*    */ package edu.carleton.cas.resources;
/*    */ 
/*    */ import edu.carleton.cas.constants.Shared;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ 
/*    */ public class WindowsFileTask
/*    */   extends AbstractFileTask
/*    */ {
/*    */   private String cmd;
/*    */   
/*    */   public WindowsFileTask(Logger logger, ResourceMonitor monitor) {
/* 14 */     super(logger, monitor);
/*    */     
/* 16 */     this.cmd = String.valueOf(Shared.DOWNLOADS_DIR) + "handle.exe";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isIllegal(String line) {
/* 25 */     if (line.contains(":\\Windows"))
/* 26 */       return false; 
/* 27 */     if (line.contains(Shared.DIR)) {
/* 28 */       return false;
/*    */     }
/*    */     
/* 31 */     return super.isIllegal(line);
/*    */   }
/*    */ 
/*    */   
/*    */   public void run() {
/*    */     try {
/* 37 */       ProcessBuilder builder = new ProcessBuilder(new String[] { this.cmd, "-accepteula" });
/* 38 */       builder.redirectErrorStream(true);
/* 39 */       this.process = builder.start();
/*    */       
/* 41 */       InputStream stdout = this.process.getInputStream();
/* 42 */       BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
/*    */ 
/*    */       
/* 45 */       this.logger.begin(); String line;
/* 46 */       while ((line = reader.readLine()) != null) {
/* 47 */         this.logger.log(line);
/* 48 */         if (line.contains("File") && 
/* 49 */           isIllegal(line))
/* 50 */           this.monitor.notifyListeners("file", line); 
/*    */       } 
/* 52 */       this.logger.end();
/* 53 */     } catch (Exception exception) {
/*    */     
/*    */     } finally {
/* 56 */       close();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\WindowsFileTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */