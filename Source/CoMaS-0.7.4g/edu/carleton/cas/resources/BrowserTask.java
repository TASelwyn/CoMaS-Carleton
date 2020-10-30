/*     */ package edu.carleton.cas.resources;
/*     */ 
/*     */ import edu.carleton.cas.logging.Logger;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.util.Properties;
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
/*     */ 
/*     */ 
/*     */ public class BrowserTask
/*     */   extends Thread
/*     */ {
/*  26 */   private static final String[] browserStartCmd = new String[] {
/*  27 */       "open -a Safari %s", 
/*  28 */       "rundll32 url.dll,FileProtocolHandler %s", 
/*  29 */       "firefox %s", 
/*  30 */       "google-chrome %s" };
/*     */   private String cmd;
/*     */   private final String os;
/*     */   private final File folder;
/*     */   private Process process;
/*     */   private Properties properties;
/*     */   
/*     */   public BrowserTask(File folder, Properties properties) {
/*  38 */     this.properties = properties;
/*  39 */     this.os = System.getProperty("os.name").toLowerCase();
/*  40 */     Logger.debug(Level.INFO, this.os);
/*  41 */     if (this.os.startsWith("mac os x")) {
/*  42 */       this.cmd = browserStartCmd[0];
/*  43 */     } else if (this.os.indexOf("win") > -1) {
/*  44 */       this.cmd = browserStartCmd[1];
/*     */     } else {
/*  46 */       this.cmd = browserStartCmd[2];
/*  47 */     }  this.folder = folder;
/*     */   }
/*     */   
/*     */   public void run() {
/*  51 */     File[] files = this.folder.listFiles(new ToolFilter(null));
/*  52 */     if (this.os.startsWith("mac os x") || this.os.indexOf("win") > -1) {
/*  53 */       byte b; int i; File[] arrayOfFile; for (i = (arrayOfFile = files).length, b = 0; b < i; ) { File file = arrayOfFile[b];
/*  54 */         runCommand(file.getAbsolutePath()); b++; }
/*     */     
/*  56 */     } else if (this.os.indexOf("nix") >= 0 || this.os.indexOf("nux") >= 0) {
/*     */       
/*  58 */       if (!runLinuxCommand(files)) {
/*  59 */         this.cmd = browserStartCmd[3];
/*  60 */         runLinuxCommand(files);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void runCommand(String command) {
/*  66 */     String cmdToRun = String.format(this.cmd, new Object[] { command });
/*     */     try {
/*  68 */       Logger.debug(Level.INFO, cmdToRun);
/*     */ 
/*     */       
/*  71 */       this.process = Runtime.getRuntime().exec(cmdToRun);
/*     */       
/*  73 */       InputStream stdout = this.process.getInputStream();
/*  74 */       BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
/*     */       
/*     */       String line;
/*  77 */       while ((line = reader.readLine()) != null) {
/*  78 */         Logger.debug(Level.FINE, line);
/*     */       }
/*  80 */     } catch (Exception e) {
/*  81 */       Logger.log(Level.WARNING, "Failed to run " + cmdToRun + ": ", e);
/*     */     } finally {
/*  83 */       close();
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean runLinuxCommand(File[] files) {
/*     */     boolean returnValue;
/*  89 */     StringBuffer buff = new StringBuffer(); byte b; int i; File[] arrayOfFile;
/*  90 */     for (i = (arrayOfFile = files).length, b = 0; b < i; ) { File file = arrayOfFile[b];
/*  91 */       buff.append(" file://");
/*  92 */       buff.append(file.getAbsolutePath()); b++; }
/*     */     
/*  94 */     String cmdToRun = String.format(this.cmd, new Object[] { buff.toString() });
/*     */     try {
/*  96 */       Logger.debug(Level.INFO, cmdToRun);
/*     */ 
/*     */       
/*  99 */       this.process = Runtime.getRuntime().exec(new String[] { "sh", "-c", cmdToRun });
/*     */       
/* 101 */       InputStream stdout = this.process.getInputStream();
/* 102 */       BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
/*     */       
/*     */       String line;
/* 105 */       while ((line = reader.readLine()) != null) {
/* 106 */         Logger.debug(Level.FINE, line);
/*     */       }
/* 108 */       returnValue = true;
/* 109 */     } catch (Exception e) {
/* 110 */       Logger.log(Level.WARNING, "Failed to run " + cmdToRun + ": ", e);
/* 111 */       returnValue = false;
/*     */     } finally {
/* 113 */       close();
/*     */     } 
/* 115 */     return returnValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {}
/*     */ 
/*     */ 
/*     */   
/*     */   private class ToolFilter
/*     */     implements FileFilter
/*     */   {
/*     */     private ToolFilter() {}
/*     */ 
/*     */     
/*     */     public boolean accept(File pathname) {
/* 131 */       int i = 1;
/* 132 */       String tool = BrowserTask.this.properties.getProperty("tool.load." + i);
/* 133 */       while (tool != null) {
/* 134 */         if (pathname.getAbsolutePath().endsWith(tool))
/* 135 */           return true; 
/* 136 */         i++;
/* 137 */         tool = BrowserTask.this.properties.getProperty("tool.load." + i);
/*     */       } 
/*     */       
/* 140 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\BrowserTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */