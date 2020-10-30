/*    */ package edu.carleton.cas.resources;
/*    */ 
/*    */ import edu.carleton.cas.constants.Shared;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ 
/*    */ public class UnixFileTask
/*    */   extends AbstractFileTask
/*    */ {
/*    */   private String userName;
/*    */   
/*    */   public UnixFileTask(Logger logger, ResourceMonitor monitor) {
/* 14 */     super(logger, monitor);
/*    */     
/* 16 */     this.userName = System.getProperty("user.name").toLowerCase();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isIllegal(String line) {
/* 24 */     if (line.contains("/System/Library"))
/* 25 */       return false; 
/* 26 */     if (line.contains("/private/"))
/* 27 */       return false; 
/* 28 */     if (line.contains("/Applications/"))
/* 29 */       return false; 
/* 30 */     if (line.contains(Shared.DIR)) {
/* 31 */       return false;
/*    */     }
/* 33 */     return super.isIllegal(line);
/*    */   }
/*    */ 
/*    */   
/*    */   public void run() {
/*    */     try {
/* 39 */       ProcessBuilder builder = new ProcessBuilder(new String[] { "lsof", "-u", this.userName });
/* 40 */       builder.redirectErrorStream(true);
/* 41 */       this.process = builder.start();
/*    */       
/* 43 */       InputStream stdout = this.process.getInputStream();
/* 44 */       BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
/*    */ 
/*    */       
/* 47 */       this.logger.begin(); String line;
/* 48 */       while ((line = reader.readLine()) != null) {
/* 49 */         this.logger.log(line);
/* 50 */         if (isIllegal(line)) {
/* 51 */           this.monitor.notifyListeners("file", line);
/*    */         }
/*    */       } 
/* 54 */       this.logger.end();
/* 55 */     } catch (Exception exception) {
/*    */     
/*    */     } finally {
/* 58 */       close();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\UnixFileTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */