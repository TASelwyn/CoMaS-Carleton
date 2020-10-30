/*     */ package edu.carleton.cas.resources;
/*     */ 
/*     */ import edu.carleton.cas.constants.Shared;
/*     */ import edu.carleton.cas.logging.Logger;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Properties;
/*     */ import java.util.Timer;
/*     */ import java.util.logging.Level;
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
/*     */ public class ResourceMonitor
/*     */   extends AbstractResourceMonitor
/*     */ {
/*     */   Timer timer;
/*     */   boolean osIsWindows;
/*     */   String name;
/*     */   Logger logger;
/*     */   AbstractTask task;
/*     */   Properties properties;
/*     */   
/*     */   public ResourceMonitor(String name, String type) {
/*  31 */     this.name = name;
/*  32 */     this.type = type;
/*  33 */     this.timer = new Timer();
/*  34 */     this.properties = new Properties();
/*  35 */     String os = System.getProperty("os.name").toLowerCase();
/*  36 */     this.osIsWindows = (os.indexOf("win") > -1);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResourceMonitor(String name, String type, String activityDirectoryName, Properties properties) {
/*  41 */     this.properties = properties;
/*  42 */     this.name = name;
/*  43 */     this.type = type;
/*  44 */     String os = System.getProperty("os.name").toLowerCase();
/*  45 */     this.osIsWindows = (os.indexOf("win") > -1);
/*  46 */     this.timer = new Timer();
/*     */     try {
/*  48 */       this.logger = new Logger();
/*  49 */       this.logger.open(new File(String.valueOf(activityDirectoryName) + File.separator + "logs" + File.separator + name));
/*  50 */     } catch (Exception e) {
/*  51 */       this.logger = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void open() {
/*  64 */     this.task = null;
/*  65 */     if (this.name.startsWith("network") && Shared.NETWORK_MONITORING) {
/*  66 */       if (this.osIsWindows) {
/*  67 */         this.task = new WindowsNetworkTask(this.logger, this);
/*     */       } else {
/*  69 */         this.task = new UnixNetworkTask(this.logger, this);
/*  70 */       }  Logger.log(Level.CONFIG, "Network monitoring is enabled", "");
/*     */     } 
/*  72 */     if (this.name.startsWith("file") && Shared.FILE_MONITORING) {
/*  73 */       if (this.osIsWindows) {
/*  74 */         this.task = new WindowsFileTask(this.logger, this);
/*     */       } else {
/*  76 */         this.task = new UnixFileTask(this.logger, this);
/*  77 */       }  Logger.log(Level.CONFIG, "File monitoring is enabled", "");
/*     */     } 
/*  79 */     if (this.task != null) {
/*  80 */       this.timer.scheduleAtFixedRate(this.task, 10000L, 60000L);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/*     */     try {
/*  90 */       this.logger.close();
/*  91 */     } catch (IOException iOException) {}
/*     */ 
/*     */     
/*  94 */     if (this.task != null)
/*  95 */       this.task.close(); 
/*  96 */     this.timer.cancel();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getProperty(String name) {
/* 105 */     return this.properties.getProperty(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPropertyOrDefault(String name, String defaultValue) {
/* 115 */     return this.properties.getProperty(name, defaultValue);
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\ResourceMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */