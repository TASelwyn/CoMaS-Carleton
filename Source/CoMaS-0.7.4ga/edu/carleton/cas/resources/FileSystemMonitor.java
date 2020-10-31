package edu.carleton.cas.resources;

import edu.carleton.cas.background.LogArchiver;
import edu.carleton.cas.constants.Shared;
import edu.carleton.cas.logging.Level;
import edu.carleton.cas.logging.Logger;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public class FileSystemMonitor extends AbstractResourceMonitor {
  private WatchService watchService;
  
  private Timer watchServiceTimer;
  
  private long exam_directory_events = 0L;
  
  private int numberOfTimesEnded;
  
  protected Thread fileSystemWatcherThread;
  
  private LogArchiver logArchiver;
  
  private File[] examFiles;
  
  private File[] resourceFiles;
  
  private String course;
  
  private String activity;
  
  private String baseDirectory;
  
  private String examDirectory;
  
  private String resourcesDirectory;
  
  private String desktopDirectory;
  
  private String comasDirectory;
  
  private String courseDirectory;
  
  public FileSystemMonitor(LogArchiver logArchiver, String course, String activity) {
    this.type = "fileSystemMonitor";
    this.course = course;
    this.activity = activity;
    this.numberOfTimesEnded = 0;
    this.desktopDirectory = Shared.getDesktopDirectory();
    this.comasDirectory = Shared.DIR;
    this.courseDirectory = Shared.getCourseDirectory(course);
    this.baseDirectory = Shared.getBaseDirectory(course, activity);
    this.examDirectory = String.valueOf(this.baseDirectory) + "exam";
    this.resourcesDirectory = String.valueOf(this.baseDirectory) + "resources";
    this.logArchiver = logArchiver;
  }
  
  public void restart() {
    if (this.numberOfTimesEnded > Shared.MAX_NUMBER_OF_FILE_WATCHING_FAILURES) {
      this.logArchiver.put(Level.WARNING, "File monitoring restart limit exceeded (" + Shared.MAX_NUMBER_OF_FILE_WATCHING_FAILURES + ")");
      return;
    } 
    close();
    open();
  }
  
  public void open() {
    try {
      this.watchService = FileSystems.getDefault().newWatchService();
      Logger.log(Level.FINE, "", "Directory watching setup.");
    } catch (IOException e) {
      Logger.log(Level.SEVERE, "", "Could not start watch service.");
    } 
    File examDirectoryFile = new File(this.baseDirectory, "exam");
    if (examDirectoryFile.exists()) {
      this.examFiles = examDirectoryFile.listFiles();
    } else {
      this.examFiles = null;
    } 
    File resourcesDirectoryFile = new File(this.baseDirectory, "resources");
    if (resourcesDirectoryFile.exists()) {
      this.resourceFiles = resourcesDirectoryFile.listFiles();
    } else {
      this.resourceFiles = null;
    } 
    File desktopDirectoryFile = new File(this.desktopDirectory);
    setupDirectoryMonitoring(desktopDirectoryFile.getParent(), desktopDirectoryFile.getName());
    setupDirectoryMonitoring(this.desktopDirectory, Shared.DESKTOP_DIR);
    setupDirectoryMonitoring(this.comasDirectory, this.course);
    setupDirectoryMonitoring(this.courseDirectory, this.activity);
    if (examDirectoryFile.exists())
      setupDirectoryMonitoring(this.baseDirectory, "exam"); 
    setupDirectoryMonitoring(this.baseDirectory, "screens");
    setupDirectoryMonitoring(this.baseDirectory, "logs");
    setupDirectoryMonitoring(this.baseDirectory, "tools");
    if (this.resourceFiles != null)
      setupDirectoryMonitoring(this.baseDirectory, "resources"); 
    if (Shared.AUTO_ARCHIVE)
      setupDirectoryMonitoring(this.baseDirectory, "archives"); 
    this.fileSystemWatcherThread = new Thread() {
        public void run() {
          try {
            boolean okay = true;
            while (okay) {
              try {
                WatchKey key = FileSystemMonitor.this.watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                  WatchEvent.Kind<?> kind = event.kind();
                  WatchEvent<Path> ev = (WatchEvent)event;
                  if (kind != StandardWatchEventKinds.OVERFLOW) {
                    Path filename = ev.context();
                    Logger.log(Level.FINE, "", 
                        String.valueOf(kind.toString()) + " " + filename.getFileName() + " on " + key.watchable());
                    String watchableString = key.watchable().toString();
                    if (ev.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                      if (FileSystemMonitor.this.specialFile(filename, watchableString)) {
                        FileSystemMonitor.this.notifyListeners("ALERT", 
                            "Deleted a CoMaS exam file: " + filename.getFileName());
                      } else {
                        FileSystemMonitor.this.logArchiver.put((Level)Level.LOGGED, String.valueOf(kind.toString()) + " " + 
                            filename.getFileName() + " from " + key.watchable());
                      } 
                    } else if (watchableString.equals(FileSystemMonitor.this.resourcesDirectory)) {
                      FileSystemMonitor.this.logArchiver.put((Level)Level.LOGGED, String.valueOf(kind.toString()) + " " + filename.getFileName() + 
                          " in " + key.watchable());
                      if (ev.kind() == StandardWatchEventKinds.ENTRY_CREATE)
                        (new File(watchableString, filename.getFileName().toString())).delete(); 
                    } else if (ev.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                      String subdirectory = filename.getFileName().toString();
                      if (FileSystemMonitor.this.isSpecialDirectory(subdirectory, watchableString))
                        FileSystemMonitor.this.processSubdirectory(subdirectory, watchableString); 
                    } 
                    if (watchableString.equals(FileSystemMonitor.this.examDirectory))
                      FileSystemMonitor.this.exam_directory_events = FileSystemMonitor.this.exam_directory_events + 1L; 
                    continue;
                  } 
                  FileSystemMonitor.this.logArchiver.log((Level)Level.NOTED, 
                      String.valueOf(kind.toString()) + " of " + ev.count() + " events on " + key.watchable());
                } 
                boolean valid = key.reset();
                if (!valid) {
                  FileSystemMonitor.this.logArchiver.put(Level.SEVERE, 
                      "Watch service no longer has access to " + key.watchable());
                  FileSystemMonitor.this.notifyListeners("ALERT", 
                      "CoMaS directory access problem for \n" + key.watchable() + ".\n\n" + Shared.SUPPORT_MESSAGE);
                  key.cancel();
                } 
              } catch (InterruptedException interruptedException) {
              
              } catch (ClosedWatchServiceException e1) {
                okay = false;
              } 
            } 
          } finally {
            FileSystemMonitor.this.numberOfTimesEnded = FileSystemMonitor.this.numberOfTimesEnded + 1;
            FileSystemMonitor.this.fileSystemWatcherThread = null;
            FileSystemMonitor.this.notifyListeners("CLOSE", "file system monitoring ended");
          } 
        }
      };
    this.fileSystemWatcherThread.start();
    if (Shared.FREQUENCY_TO_CHECK_EXAM_DIRECTORY > 0) {
      this.watchServiceTimer = new Timer();
      TimerTask task = new TimerTask() {
          public void run() {
            if (FileSystemMonitor.this.exam_directory_events < Shared.MIN_EVENTS_IN_EXAM_DIRECTORY)
              FileSystemMonitor.this.notifyListeners("ALERT", "No activity in " + FileSystemMonitor.this.examDirectory + " for " + (
                  Shared.FREQUENCY_TO_CHECK_EXAM_DIRECTORY / 60000) + " mins"); 
            FileSystemMonitor.this.exam_directory_events = 0L;
          }
        };
      this.watchServiceTimer.scheduleAtFixedRate(task, Shared.FREQUENCY_TO_CHECK_EXAM_DIRECTORY, 
          Shared.FREQUENCY_TO_CHECK_EXAM_DIRECTORY);
    } 
  }
  
  public void close() {
    if (this.watchService != null) {
      try {
        this.watchService.close();
        if (this.fileSystemWatcherThread != null)
          this.fileSystemWatcherThread.interrupt(); 
      } catch (IOException e) {
        Logger.log(Level.WARNING, "", "Failed to close watch service.");
      } 
      this.watchService = null;
    } 
    if (this.watchServiceTimer != null) {
      this.watchServiceTimer.cancel();
      this.watchServiceTimer = null;
    } 
  }
  
  private WatchKey setupDirectoryMonitoring(String base, String dir) {
    try {
      Path path = FileSystems.getDefault().getPath(base, new String[] { dir });
      WatchKey key = path.register(this.watchService, (WatchEvent.Kind<?>[])new WatchEvent.Kind[] { StandardWatchEventKinds.ENTRY_CREATE, 
            StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY });
      Logger.log(Level.FINE, "", "Monitoring " + dir);
      return key;
    } catch (IOException e) {
      this.logArchiver.put(Level.SEVERE, "Could not monitor " + dir);
      return null;
    } 
  }
  
  private boolean specialFile(Path file, String dir) {
    if (dir.equals(this.examDirectory) && this.examFiles != null) {
      String name = file.getFileName().toString();
      byte b;
      int i;
      File[] arrayOfFile;
      for (i = (arrayOfFile = this.examFiles).length, b = 0; b < i; ) {
        File afile = arrayOfFile[b];
        if (afile.getName().equals(name))
          return true; 
        b++;
      } 
    } 
    if (dir.equals(this.resourcesDirectory) && this.resourceFiles != null) {
      String name = file.getFileName().toString();
      byte b;
      int i;
      File[] arrayOfFile;
      for (i = (arrayOfFile = this.resourceFiles).length, b = 0; b < i; ) {
        File afile = arrayOfFile[b];
        if (afile.getName().equals(name))
          return true; 
        b++;
      } 
    } 
    return false;
  }
  
  private boolean isSpecialDirectory(String file, String dir) {
    if (this.desktopDirectory.startsWith(dir) && file.equals(Shared.DESKTOP_DIR))
      return true; 
    if (this.comasDirectory.startsWith(dir) && file.equals(this.course))
      return true; 
    if (this.courseDirectory.startsWith(dir) && file.equals(this.activity))
      return true; 
    if (this.baseDirectory.startsWith(dir)) {
      if (file.equals("archives"))
        return true; 
      if (file.equals("exam"))
        return true; 
      if (file.equals("resources"))
        return true; 
      if (file.equals("logs"))
        return true; 
      if (file.equals("tools"))
        return true; 
      if (file.equals("screens"))
        return true; 
    } 
    return false;
  }
  
  private void processSubdirectory(String subdir, String dir) {
    WatchKey newKey = setupDirectoryMonitoring(dir, subdir);
    if (newKey != null) {
      this.logArchiver.put(Level.INFO, "Watch service can now access " + newKey.watchable());
    } else {
      this.logArchiver.put(Level.INFO, "Watch service monitoring for " + subdir + " of " + dir + " failed");
    } 
    File newWatchedSubdirectory = new File(dir, subdir);
    File[] otherDirectories = newWatchedSubdirectory.listFiles(new FileFilter() {
          public boolean accept(File file) {
            return file.isDirectory();
          }
        });
    if (otherDirectories != null) {
      byte b;
      int i;
      File[] arrayOfFile;
      for (i = (arrayOfFile = otherDirectories).length, b = 0; b < i; ) {
        File otherDirectory = arrayOfFile[b];
        processSubdirectory(otherDirectory.getName(), newWatchedSubdirectory.getAbsolutePath());
        b++;
      } 
    } 
  }
}
