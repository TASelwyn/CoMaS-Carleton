/*     */ package edu.carleton.cas.resources;
/*     */ 
/*     */ import edu.carleton.cas.background.LogArchiver;
/*     */ import edu.carleton.cas.constants.Shared;
/*     */ import edu.carleton.cas.logging.Logger;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.ClosedWatchServiceException;
/*     */ import java.nio.file.FileSystems;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardWatchEventKinds;
/*     */ import java.nio.file.WatchEvent;
/*     */ import java.nio.file.WatchKey;
/*     */ import java.nio.file.WatchService;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
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
/*     */ public class FileSystemMonitor
/*     */   extends AbstractResourceMonitor
/*     */ {
/*     */   private WatchService watchService;
/*     */   private Timer watchServiceTimer;
/*  34 */   private long exam_directory_events = 0L;
/*     */   
/*     */   private int numberOfTimesEnded;
/*     */   protected Thread fileSystemWatcherThread;
/*     */   private LogArchiver logArchiver;
/*     */   private File[] examFiles;
/*     */   private File[] resourceFiles;
/*     */   private String course;
/*     */   private String activity;
/*     */   private String baseDirectory;
/*     */   private String examDirectory;
/*     */   private String resourcesDirectory;
/*     */   private String desktopDirectory;
/*     */   private String comasDirectory;
/*     */   private String courseDirectory;
/*     */   
/*     */   public FileSystemMonitor(LogArchiver logArchiver, String course, String activity) {
/*  51 */     this.type = "fileSystemMonitor";
/*  52 */     this.course = course;
/*  53 */     this.activity = activity;
/*  54 */     this.numberOfTimesEnded = 0;
/*  55 */     this.desktopDirectory = Shared.getDesktopDirectory();
/*  56 */     this.comasDirectory = Shared.DIR;
/*  57 */     this.courseDirectory = Shared.getCourseDirectory(course);
/*  58 */     this.baseDirectory = Shared.getBaseDirectory(course, activity);
/*  59 */     this.examDirectory = String.valueOf(this.baseDirectory) + "exam";
/*  60 */     this.resourcesDirectory = String.valueOf(this.baseDirectory) + "resources";
/*  61 */     this.logArchiver = logArchiver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void restart() {
/*  68 */     if (this.numberOfTimesEnded > Shared.MAX_NUMBER_OF_FILE_WATCHING_FAILURES) {
/*  69 */       this.logArchiver.put(Level.WARNING, "File monitoring restart limit exceeded (" + Shared.MAX_NUMBER_OF_FILE_WATCHING_FAILURES + ")");
/*     */       return;
/*     */     } 
/*  72 */     close();
/*  73 */     open();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void open() {
/*     */     try {
/*  90 */       this.watchService = FileSystems.getDefault().newWatchService();
/*  91 */       Logger.log(Level.FINE, "", "Directory watching setup.");
/*  92 */     } catch (IOException e) {
/*  93 */       Logger.log(Level.SEVERE, "", "Could not start watch service.");
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 100 */     final File examDirectory = new File(this.baseDirectory, "exam");
/* 101 */     if (examDirectory.exists()) {
/* 102 */       this.examFiles = examDirectory.listFiles();
/*     */     } else {
/* 104 */       this.examFiles = null;
/* 105 */     }  final File resourcesDirectory = new File(this.baseDirectory, "resources");
/* 106 */     if (resourcesDirectory.exists()) {
/* 107 */       this.resourceFiles = resourcesDirectory.listFiles();
/*     */     } else {
/* 109 */       this.resourceFiles = null;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 118 */     File desktopDirectoryFile = new File(this.desktopDirectory);
/* 119 */     setupDirectoryMonitoring(desktopDirectoryFile.getParent(), desktopDirectoryFile.getName());
/* 120 */     setupDirectoryMonitoring(this.desktopDirectory, Shared.DESKTOP_DIR);
/* 121 */     setupDirectoryMonitoring(this.comasDirectory, this.course);
/* 122 */     setupDirectoryMonitoring(this.courseDirectory, this.activity);
/* 123 */     if (examDirectory.exists())
/* 124 */       setupDirectoryMonitoring(this.baseDirectory, "exam"); 
/* 125 */     setupDirectoryMonitoring(this.baseDirectory, "screens");
/* 126 */     setupDirectoryMonitoring(this.baseDirectory, "logs");
/* 127 */     setupDirectoryMonitoring(this.baseDirectory, "tools");
/*     */     
/* 129 */     if (this.resourceFiles != null)
/* 130 */       setupDirectoryMonitoring(this.baseDirectory, "resources"); 
/* 131 */     if (Shared.AUTO_ARCHIVE) {
/* 132 */       setupDirectoryMonitoring(this.baseDirectory, "archives");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 137 */     this.fileSystemWatcherThread = new Thread()
/*     */       {
/*     */         public void run() {
/*     */           try {
/* 141 */             boolean okay = true;
/*     */             
/* 143 */             while (okay) {
/*     */               try {
/* 145 */                 WatchKey key = FileSystemMonitor.this.watchService.take();
/* 146 */                 for (WatchEvent<?> event : key.pollEvents()) {
/* 147 */                   WatchEvent.Kind<?> kind = event.kind();
/* 148 */                   WatchEvent<Path> ev = (WatchEvent)event;
/* 149 */                   if (kind != StandardWatchEventKinds.OVERFLOW) {
/* 150 */                     Path filename = ev.context();
/* 151 */                     Logger.log(Level.FINE, "", 
/* 152 */                         String.valueOf(kind.toString()) + " " + filename.getFileName() + " on " + key.watchable());
/* 153 */                     String watchableString = key.watchable().toString();
/*     */                     
/* 155 */                     if (ev.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
/* 156 */                       if (FileSystemMonitor.this.specialFile(filename, watchableString))
/* 157 */                       { FileSystemMonitor.this.notifyListeners("ALERT", 
/* 158 */                             "Deleted a CoMaS exam file: " + filename.getFileName()); }
/*     */                       else
/* 160 */                       { FileSystemMonitor.this.logArchiver.put(Level.WARNING, String.valueOf(kind.toString()) + " " + 
/* 161 */                             filename.getFileName() + " from " + key.watchable()); } 
/* 162 */                     } else if (watchableString.equals(resourcesDirectory)) {
/* 163 */                       FileSystemMonitor.this.logArchiver.put(Level.WARNING, String.valueOf(kind.toString()) + " " + filename.getFileName() + 
/* 164 */                           " in " + key.watchable());
/* 165 */                       if (ev.kind() == StandardWatchEventKinds.ENTRY_CREATE)
/*     */                       {
/* 167 */                         (new File(watchableString, filename.getFileName().toString())).delete();
/*     */                       }
/* 169 */                     } else if (ev.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
/* 170 */                       String subdirectory = filename.getFileName().toString();
/* 171 */                       if (FileSystemMonitor.this.isSpecialDirectory(subdirectory, watchableString)) {
/* 172 */                         FileSystemMonitor.this.processSubdirectory(subdirectory, watchableString);
/*     */                       }
/*     */                     } 
/*     */ 
/*     */ 
/*     */                     
/* 178 */                     if (watchableString.equals(examDirectory)) {
/* 179 */                       FileSystemMonitor.this.exam_directory_events = FileSystemMonitor.this.exam_directory_events + 1L;
/*     */                     }
/*     */                     continue;
/*     */                   } 
/* 183 */                   FileSystemMonitor.this.logArchiver.log(Level.WARNING, 
/* 184 */                       String.valueOf(kind.toString()) + " of " + ev.count() + " events on " + key.watchable());
/*     */                 } 
/*     */                 
/* 187 */                 boolean valid = key.reset();
/* 188 */                 if (!valid) {
/*     */                   
/* 190 */                   FileSystemMonitor.this.logArchiver.put(Level.SEVERE, 
/* 191 */                       "Watch service no longer has access to " + key.watchable());
/* 192 */                   FileSystemMonitor.this.notifyListeners("ALERT", 
/* 193 */                       "CoMaS directory access problem for \n" + key.watchable() + ".\n\n" + Shared.SUPPORT_MESSAGE);
/* 194 */                   key.cancel();
/*     */                 } 
/* 196 */               } catch (InterruptedException interruptedException) {
/*     */               
/* 198 */               } catch (ClosedWatchServiceException e1) {
/* 199 */                 okay = false;
/*     */               } 
/*     */             } 
/*     */           } finally {
/* 203 */             FileSystemMonitor.this.numberOfTimesEnded = FileSystemMonitor.this.numberOfTimesEnded + 1;
/* 204 */             FileSystemMonitor.this.fileSystemWatcherThread = null;
/* 205 */             FileSystemMonitor.this.notifyListeners("CLOSE", "file system monitoring ended");
/*     */           } 
/*     */         }
/*     */       };
/*     */     
/* 210 */     this.fileSystemWatcherThread.start();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 218 */     if (Shared.FREQUENCY_TO_CHECK_EXAM_DIRECTORY > 0) {
/* 219 */       this.watchServiceTimer = new Timer();
/* 220 */       TimerTask task = new TimerTask() {
/*     */           public void run() {
/* 222 */             if (FileSystemMonitor.this.exam_directory_events < Shared.MIN_EVENTS_IN_EXAM_DIRECTORY) {
/* 223 */               FileSystemMonitor.this.notifyListeners("ALERT", "No activity in " + examDirectory + " for " + (
/* 224 */                   Shared.FREQUENCY_TO_CHECK_EXAM_DIRECTORY / 60000) + " mins");
/*     */             }
/* 226 */             FileSystemMonitor.this.exam_directory_events = 0L;
/*     */           }
/*     */         };
/*     */       
/* 230 */       this.watchServiceTimer.scheduleAtFixedRate(task, Shared.FREQUENCY_TO_CHECK_EXAM_DIRECTORY, 
/* 231 */           Shared.FREQUENCY_TO_CHECK_EXAM_DIRECTORY);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 239 */     if (this.watchService != null) {
/*     */       try {
/* 241 */         this.watchService.close();
/* 242 */         if (this.fileSystemWatcherThread != null)
/* 243 */           this.fileSystemWatcherThread.interrupt(); 
/* 244 */       } catch (IOException e) {
/* 245 */         Logger.log(Level.WARNING, "", "Failed to close watch service.");
/*     */       } 
/* 247 */       this.watchService = null;
/*     */     } 
/* 249 */     if (this.watchServiceTimer != null) {
/* 250 */       this.watchServiceTimer.cancel();
/* 251 */       this.watchServiceTimer = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private WatchKey setupDirectoryMonitoring(String base, String dir) {
/*     */     try {
/* 260 */       Path path = FileSystems.getDefault().getPath(base, new String[] { dir });
/* 261 */       WatchKey key = path.register(this.watchService, (WatchEvent.Kind<?>[])new WatchEvent.Kind[] { StandardWatchEventKinds.ENTRY_CREATE, 
/* 262 */             StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY });
/* 263 */       Logger.log(Level.FINE, "", "Monitoring " + dir);
/* 264 */       return key;
/* 265 */     } catch (IOException e) {
/* 266 */       this.logArchiver.put(Level.SEVERE, "Could not monitor " + dir);
/*     */       
/* 268 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean specialFile(Path file, String dir) {
/* 277 */     if (dir.equals(this.examDirectory) && this.examFiles != null) {
/* 278 */       String name = file.getFileName().toString(); byte b; int i; File[] arrayOfFile;
/* 279 */       for (i = (arrayOfFile = this.examFiles).length, b = 0; b < i; ) { File afile = arrayOfFile[b];
/* 280 */         if (afile.getName().equals(name))
/* 281 */           return true;  b++; }
/*     */     
/*     */     } 
/* 284 */     if (dir.equals(this.resourcesDirectory) && this.resourceFiles != null) {
/* 285 */       String name = file.getFileName().toString(); byte b; int i; File[] arrayOfFile;
/* 286 */       for (i = (arrayOfFile = this.resourceFiles).length, b = 0; b < i; ) { File afile = arrayOfFile[b];
/* 287 */         if (afile.getName().equals(name))
/* 288 */           return true;  b++; }
/*     */     
/*     */     } 
/* 291 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isSpecialDirectory(String file, String dir) {
/* 302 */     if (this.desktopDirectory.startsWith(dir) && file.equals(Shared.DESKTOP_DIR))
/* 303 */       return true; 
/* 304 */     if (this.comasDirectory.startsWith(dir) && file.equals(this.course))
/* 305 */       return true; 
/* 306 */     if (this.courseDirectory.startsWith(dir) && file.equals(this.activity)) {
/* 307 */       return true;
/*     */     }
/* 309 */     if (this.baseDirectory.startsWith(dir)) {
/* 310 */       if (file.equals("archives"))
/* 311 */         return true; 
/* 312 */       if (file.equals("exam"))
/* 313 */         return true; 
/* 314 */       if (file.equals("resources"))
/* 315 */         return true; 
/* 316 */       if (file.equals("logs"))
/* 317 */         return true; 
/* 318 */       if (file.equals("tools"))
/* 319 */         return true; 
/* 320 */       if (file.equals("screens"))
/* 321 */         return true; 
/*     */     } 
/* 323 */     return false;
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
/*     */ 
/*     */ 
/*     */   
/*     */   private void processSubdirectory(String subdir, String dir) {
/* 338 */     WatchKey newKey = setupDirectoryMonitoring(dir, subdir);
/* 339 */     if (newKey != null) {
/* 340 */       this.logArchiver.put(Level.INFO, "Watch service can now access " + newKey.watchable());
/*     */     } else {
/* 342 */       this.logArchiver.put(Level.INFO, "Watch service monitoring for " + subdir + " of " + dir + " failed");
/* 343 */     }  File newWatchedSubdirectory = new File(dir, subdir);
/* 344 */     File[] otherDirectories = newWatchedSubdirectory.listFiles(new FileFilter() {
/*     */           public boolean accept(File file) {
/* 346 */             return file.isDirectory();
/*     */           }
/*     */         });
/* 349 */     if (otherDirectories != null) {
/* 350 */       byte b; int i; File[] arrayOfFile; for (i = (arrayOfFile = otherDirectories).length, b = 0; b < i; ) { File otherDirectory = arrayOfFile[b];
/* 351 */         processSubdirectory(otherDirectory.getName(), newWatchedSubdirectory.getAbsolutePath());
/*     */         b++; }
/*     */     
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\FileSystemMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */