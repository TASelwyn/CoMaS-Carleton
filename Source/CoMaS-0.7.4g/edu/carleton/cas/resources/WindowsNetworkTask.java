/*    */ package edu.carleton.cas.resources;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ 
/*    */ public class WindowsNetworkTask
/*    */   extends AbstractNetworkTask
/*    */ {
/* 10 */   int indexOfForeign = -1;
/*    */   boolean indexOk = false;
/*    */   
/*    */   public WindowsNetworkTask(Logger logger, ResourceMonitor monitor) {
/* 14 */     super(logger, monitor);
/*    */   }
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
/*    */   public boolean isIllegal(String line) {
/* 27 */     if (!this.indexOk) {
/* 28 */       this.indexOfForeign = line.indexOf("Foreign");
/* 29 */       this.indexOk = (this.indexOfForeign > -1);
/*    */     } 
/*    */     
/* 32 */     if (this.indexOk && line.contains("ESTABLISHED")) {
/* 33 */       int end = line.indexOf(":", this.indexOfForeign);
/* 34 */       String remoteHost = line.substring(this.indexOfForeign, end);
/*    */       
/* 36 */       if (this.hosts.contains(remoteHost))
/*    */       {
/* 38 */         return false;
/*    */       }
/* 40 */       if (remoteHost.contains("akamaitechnologies")) {
/* 41 */         return false;
/*    */       }
/*    */ 
/*    */ 
/*    */       
/* 46 */       if (line.contains("127.0.0.1"))
/*    */       {
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
/* 58 */         return false;
/*    */       }
/*    */       
/* 61 */       if (remoteHost.startsWith("dhcp-"))
/* 62 */         return false; 
/* 63 */       if (remoteHost.startsWith("localhost"))
/* 64 */         return false; 
/* 65 */       if (remoteHost.startsWith(this.localHost))
/* 66 */         return false; 
/* 67 */       if (isAllowed(remoteHost)) {
/* 68 */         return false;
/*    */       }
/* 70 */       return true;
/*    */     } 
/*    */     
/* 73 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public void run() {
/*    */     try {
/* 79 */       ProcessBuilder builder = new ProcessBuilder(new String[] { "netstat" });
/* 80 */       builder.redirectErrorStream(true);
/* 81 */       this.process = builder.start();
/*    */       
/* 83 */       InputStream stdout = this.process.getInputStream();
/* 84 */       BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
/*    */ 
/*    */       
/* 87 */       this.logger.begin(); String line;
/* 88 */       while ((line = reader.readLine()) != null) {
/* 89 */         this.logger.log(line);
/* 90 */         if (isIllegal(line))
/* 91 */           this.monitor.notifyListeners("network", line); 
/*    */       } 
/* 93 */       this.logger.end();
/* 94 */     } catch (Exception exception) {
/*    */     
/*    */     } finally {
/* 97 */       close();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\WindowsNetworkTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */