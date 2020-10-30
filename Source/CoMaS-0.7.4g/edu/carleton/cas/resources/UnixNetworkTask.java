/*    */ package edu.carleton.cas.resources;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ 
/*    */ public class UnixNetworkTask
/*    */   extends AbstractNetworkTask {
/*    */   public UnixNetworkTask(Logger logger, ResourceMonitor monitor) {
/* 10 */     super(logger, monitor);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isIllegal(String line) {
/* 21 */     if (line.startsWith("com.apple"))
/* 22 */       return false; 
/* 23 */     if (line.startsWith("Mail"))
/* 24 */       return false; 
/* 25 */     if (line.startsWith("Dropbox"))
/* 26 */       return false; 
/* 27 */     if (line.startsWith("cloudd"))
/* 28 */       return false; 
/* 29 */     if (line.contains("akamaitechnologies"))
/* 30 */       return false; 
/* 31 */     if (line.contains("ubuntu")) {
/* 32 */       return false;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 37 */     if (line.contains("(ESTABLISHED)")) {
/* 38 */       int index = line.indexOf("->");
/* 39 */       int end = line.indexOf(":", index);
/* 40 */       String remoteHost = line.substring(index + 2, end);
/*    */ 
/*    */ 
/*    */       
/* 44 */       if (isAllowed(remoteHost)) {
/* 45 */         return false;
/*    */       }
/* 47 */       if (remoteHost.startsWith("dhcp-"))
/* 48 */         return false; 
/* 49 */       if (remoteHost.equals("localhost"))
/* 50 */         return false; 
/* 51 */       if (remoteHost.equals(this.localHost))
/* 52 */         return false; 
/* 53 */       return true;
/*    */     } 
/*    */     
/* 56 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public void run() {
/*    */     try {
/* 62 */       ProcessBuilder builder = new ProcessBuilder(new String[] { "lsof", "-i" });
/* 63 */       builder.redirectErrorStream(true);
/* 64 */       this.process = builder.start();
/*    */       
/* 66 */       InputStream stdout = this.process.getInputStream();
/* 67 */       BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
/*    */ 
/*    */       
/* 70 */       this.logger.begin(); String line;
/* 71 */       while ((line = reader.readLine()) != null) {
/* 72 */         this.logger.log(line);
/* 73 */         if (isIllegal(line)) {
/* 74 */           this.monitor.notifyListeners("network", line);
/*    */         }
/*    */       } 
/* 77 */       this.logger.end();
/* 78 */     } catch (Exception exception) {
/*    */     
/*    */     } finally {
/* 81 */       close();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\UnixNetworkTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */