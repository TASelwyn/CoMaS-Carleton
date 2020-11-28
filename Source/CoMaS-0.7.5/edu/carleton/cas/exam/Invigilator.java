package edu.carleton.cas.exam;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import edu.carleton.cas.background.KeepAliveInterface;
import edu.carleton.cas.background.KeepAliveSentinel;
import edu.carleton.cas.background.LogArchiver;
import edu.carleton.cas.background.LoggerModuleBridge;
import edu.carleton.cas.background.UploadArchiver;
import edu.carleton.cas.constants.ClientShared;
import edu.carleton.cas.file.Utils;
import edu.carleton.cas.file.Zip;
import edu.carleton.cas.jetty.embedded.ServletProcessor;
import edu.carleton.cas.logging.Level;
import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.messaging.Message;
import edu.carleton.cas.messaging.MessageHandler;
import edu.carleton.cas.messaging.handlers.AlertMessageHandler;
import edu.carleton.cas.messaging.handlers.LoadMessageHandler;
import edu.carleton.cas.messaging.handlers.LogMessageHandler;
import edu.carleton.cas.messaging.handlers.PingMessageHandler;
import edu.carleton.cas.messaging.handlers.ScreenShotFrequencyMessageHandler;
import edu.carleton.cas.messaging.handlers.StopMessageHandler;
import edu.carleton.cas.messaging.handlers.URLMessageHandler;
import edu.carleton.cas.messaging.handlers.UnloadMessageHandler;
import edu.carleton.cas.modules.foundation.ModuleClassLoader;
import edu.carleton.cas.modules.foundation.ModuleManager;
import edu.carleton.cas.resources.AbstractFileTask;
import edu.carleton.cas.resources.BrowserTask;
import edu.carleton.cas.resources.FileSystemMonitor;
import edu.carleton.cas.resources.Resource;
import edu.carleton.cas.resources.ResourceListener;
import edu.carleton.cas.resources.ResourceMonitor;
import edu.carleton.cas.ui.ExamDialog;
import edu.carleton.cas.utility.ClientHelper;
import edu.carleton.cas.utility.ClipboardManager;
import edu.carleton.cas.utility.CustomResourceCreator;
import edu.carleton.cas.utility.HTMLFileViewGenerator;
import edu.carleton.cas.utility.IPAddressChecker;
import edu.carleton.cas.utility.IconLoader;
import edu.carleton.cas.utility.MacAddress;
import edu.carleton.cas.utility.Named;
import edu.carleton.cas.utility.Observable;
import edu.carleton.cas.utility.ScreenShotCreator;
import edu.carleton.cas.utility.Sleeper;
import edu.carleton.cas.utility.VMDetector;
import edu.carleton.cas.websocket.WebsocketClientEndpoint;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.swing.JOptionPane;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

public class Invigilator extends Observable implements ResourceListener {
  public final String name;
  
  public final String id;
  
  public final String course;
  
  public final String activity;
  
  protected String state;
  
  public String alert;
  
  protected boolean done;
  
  protected Thread thread;
  
  protected final Random r = new Random();
  
  public boolean isAllowedToUpload;
  
  public UploadArchiver screenShotArchiver;
  
  public LogArchiver logArchiver;
  
  public UploadArchiver examArchiver;
  
  public KeepAliveSentinel sentinel;
  
  private FileSystemMonitor fileSystemMonitor;
  
  private int current = ClientShared.MAX_INTERVAL;
  
  private int failures = 0;
  
  private long failureStartTime = 0L;
  
  private long actualStartTime = 0L;
  
  private ResourceMonitor fileResource;
  
  private ResourceMonitor networkResource;
  
  private HashMap<String, String> me = null;
  
  private WebsocketClientEndpoint endpoint;
  
  private Properties properties;
  
  private boolean endedSession;
  
  private boolean initHasRun;
  
  boolean endPointFailed;
  
  long wakeup;
  
  private boolean screenSizeCheckLogged;
  
  private float alpha;
  
  private boolean screenShotProblem;
  
  private Webcam webcam;
  
  private BufferedImage qrCode;
  
  private boolean archiveProblem;
  
  public String getToken() {
    return this.properties.getProperty("TOKEN");
  }
  
  public void setStateAndAuthenticate(String state) {
    this.state = state;
    authenticate();
    this.logArchiver.put((Level)Level.LOGGED, String.valueOf(state) + " " + new Date());
  }
  
  public Invigilator(String id, String name, String course, String activity, Properties properties) {
    this.endedSession = false;
    this.initHasRun = false;
    this.endPointFailed = false;
    this.wakeup = 9223372036854655807L;
    this.screenSizeCheckLogged = false;
    this.alpha = 0.9F;
    this.screenShotProblem = false;
    this.archiveProblem = false;
    if (id == null || name == null || course == null)
      throw new RuntimeException("Illegal Invigilator parameters"); 
    this.name = name;
    this.id = id;
    this.course = course;
    this.activity = activity;
    this.state = "Unknown";
    this.done = false;
    this.properties = properties;
    this.isAllowedToUpload = true;
    this.sentinel = new KeepAliveSentinel();
    this.screenShotArchiver = new UploadArchiver(this, ClientShared.BASE_VIDEO, "video", ClientShared.PASSKEY_VIDEO, "Screen upload");
    this.logArchiver = new LogArchiver(this, ClientShared.LOG_URL, "SEVERE", "Logging");
    if (ClientShared.AUTO_ARCHIVE)
      this.examArchiver = new UploadArchiver(this, ClientShared.BASE_UPLOAD, "upload", ClientShared.PASSKEY_FILE_UPLOAD, "Archive upload"); 
    this.sentinel.register((KeepAliveInterface)this.screenShotArchiver);
    this.sentinel.register((KeepAliveInterface)this.logArchiver);
    if (ClientShared.AUTO_ARCHIVE)
      this.sentinel.register((KeepAliveInterface)this.examArchiver); 
    this.sentinel.start();
    this.screenShotArchiver.start();
    this.logArchiver.start();
    if (ClientShared.AUTO_ARCHIVE)
      this.examArchiver.start(); 
    this.alert = "";
  }
  
  public synchronized void endTheSession() {
    if (!this.endedSession) {
      this.endedSession = true;
      ExamDialog.instance.setTitleStatus(0);
      ExamDialog.setStatus("0:stopping");
      ExamDialog.updateProgress("Stopping", 0);
      Logger.log(Level.FINE, "Ending session ", "");
      ExamDialog.setStatus("1:web cam");
      if (this.initHasRun)
        takeWebcamImage(); 
      ExamDialog.updateProgress("Web cam complete", 1);
      if (ClientShared.AUTO_ARCHIVE)
        ExamDialog.setStatus("2:archiving"); 
      if (this.initHasRun)
        createAndUploadArchive(true); 
      if (ClientShared.AUTO_ARCHIVE)
        ExamDialog.updateProgress("Archived exam", 2); 
      this.done = true;
      if (this.thread != null)
        this.thread.interrupt(); 
      stop();
      ExamDialog.setStatus("10:stopped");
      ExamDialog.updateProgress("Stopped", 9);
      Logger.log(Level.FINE, "Ended session ", "");
      Logger.output("Exam end " + new Date(System.currentTimeMillis()));
      if (this.initHasRun && this.actualStartTime > 0L) {
        NumberFormat nf = NumberFormat.getInstance();
        Logger.output("Session duration: " + nf.format((System.currentTimeMillis() - this.actualStartTime) / 1000.0D) + " secs");
      } 
      this.state = "Logged out";
      authenticate();
      ExamDialog.updateProgress("Logged out", 10);
    } 
  }
  
  private void hookRunTime() {
    Logger.log(Level.FINE, "Creating session end hook", "");
    Thread hook = new Thread() {
        public void run() {
          Invigilator.this.endTheSession();
        }
      };
    Runtime.getRuntime().addShutdownHook(hook);
    Logger.log(Level.FINE, "", "Session hook established");
  }
  
  public boolean canRunAtThisIPAddress() {
    IPAddressChecker checker;
    try {
      checker = new IPAddressChecker(this.properties);
    } catch (UnknownHostException e) {
      String defaultCanRun = this.properties.getProperty("default", "true");
      if (defaultCanRun.equals("true"))
        return true; 
      if (defaultCanRun.equals("false"))
        return false; 
      return false;
    } 
    if (checker.deny())
      return false; 
    return checker.allow();
  }
  
  public boolean isExamOk() {
    ExamDialog.setStatus("4:exam check");
    try {
      Client client = ClientHelper.createClient(ClientShared.PROTOCOL);
      WebTarget webTarget = client.target(ClientShared.BASE_EXAM).path("deployed").path("activity");
      Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" });
      invocationBuilder.accept(new String[] { "text/plain" });
      invocationBuilder.cookie("token", getToken());
      Form form = new Form();
      form.param("course", this.course);
      form.param("activity", this.activity);
      form.param("version", ClientShared.VERSION);
      form.param("passkey", ClientShared.PASSKEY_EXAM);
      Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded"));
      Logger.log(Level.INFO, String.format("[%s] %s", new Object[] { Integer.valueOf(response.getStatus()), "Exam check response" }), "");
      return (response.getStatus() == 200);
    } catch (Exception e) {
      Logger.log(Level.WARNING, String.format("Exam check failure for %s: %s", new Object[] { ClientShared.BASE_EXAM, e.getMessage() }), "");
      return false;
    } 
  }
  
  public boolean isDone() {
    return this.done;
  }
  
  private void init() {
    String logDir = ClientShared.HOME;
    try {
      if (ClientShared.isWriteableDirectory(logDir)) {
        logDir = String.valueOf(ClientShared.HOME) + File.separator;
      } else {
        logDir = String.valueOf(ClientShared.getDesktopDirectory()) + File.separator;
      } 
      Logger.setup(Invigilator.class, "comas-system", logDir, ClientShared.LOGGING_LEVEL);
      ExamDialog.setStatus("5:logger ok");
    } catch (IOException e) {
      ExamDialog.setStatus("5:logger not ok");
    } 
    Logger.log(Level.FINE, "", "Initializing");
    Logger.log(Level.FINEST, "login.ini ", this.properties.toString());
    AbstractFileTask.configure(this.properties);
    Logger.log(Level.FINE, "Making ", "CoMaS directories");
    boolean directoryOk = true;
    directoryOk = (directoryOk && makeDirectory(ClientShared.getScreensDirectory(this.course, this.activity)));
    directoryOk = (directoryOk && makeDirectory(ClientShared.getLogsDirectory(this.course, this.activity)));
    directoryOk = (directoryOk && makeDirectory(ClientShared.getToolsDirectory(this.course, this.activity)));
    directoryOk = (directoryOk && makeDirectory(ClientShared.getResourcesDirectory(this.course, this.activity)));
    if (ClientShared.AUTO_ARCHIVE)
      directoryOk = (directoryOk && makeDirectory(ClientShared.getArchivesDirectory(this.course, this.activity))); 
    if (!directoryOk) {
      notifyObservers("Could not create " + ClientShared.getActivityDirectory(this.course, this.activity) + " subdirectory.\n" + ClientShared.SUPPORT_MESSAGE);
      this.logArchiver.put(Level.WARNING, "Startup FAILED(directory creation) (version " + ClientShared.VERSION + ") for " + this.course + "/" + this.activity + " using java " + this.properties.getProperty("JAVA_VERSION") + " on " + this.properties.getProperty("OS"));
      Sleeper.sleepAndExit(5000, -2);
    } 
    Logger.log(Level.FINE, "Completed ", " directory creation");
    boolean logsToBeViewable = Utils.getBooleanOrDefault(this.properties, "logs.view", true);
    if (logsToBeViewable) {
      HTMLFileViewGenerator.create("CoMaS Logs", String.valueOf(logDir) + "comas-system-log.html", String.valueOf(ClientShared.getToolsDirectory(this.course, this.activity)) + File.separator + "logs.html");
      Logger.log(Level.FINE, "Created log viewer in ", String.valueOf(ClientShared.getToolsDirectory(this.course, this.activity)) + File.separator + "logs.html");
    } 
    try {
      createExam();
    } catch (IOException e) {
      this.done = true;
      Logger.log(Level.SEVERE, "Could not create " + ClientShared.getExamDirectory(this.course, this.activity) + ": ", e.getMessage());
      notifyObservers("Could not create " + ClientShared.getExamDirectory(this.course, this.activity) + ".\n" + ClientShared.SUPPORT_MESSAGE);
      this.logArchiver.put(Level.WARNING, "Startup FAILED(exam creation) (version " + ClientShared.VERSION + ") for " + this.course + "/" + this.activity + " using java " + this.properties.getProperty("JAVA_VERSION") + " on " + this.properties.getProperty("OS"));
      Sleeper.sleepAndExit(5000, -3);
    } 
    createResources();
    try {
      createOStools();
    } catch (IOException e) {
      this.done = true;
      Logger.log(Level.SEVERE, "Could not create OS tools: ", e.getMessage());
      notifyObservers("Could not create OS tools: " + e.getMessage() + ".\n" + ClientShared.SUPPORT_MESSAGE);
      this.logArchiver.put(Level.WARNING, "Startup FAILED(OS tools creation) (version " + ClientShared.VERSION + ") for " + this.course + "/" + this.activity + " using java " + this.properties.getProperty("JAVA_VERSION") + " on " + this.properties.getProperty("OS"));
      Sleeper.sleepAndExit(5000, -4);
    } 
    createStudentNotes();
    hookRunTime();
    ExamDialog.setStatus("9:hook ok");
    setupDirectoryWatching();
    ExamDialog.setStatus("10:dir ok");
    if (ClientShared.BLUETOOTH_MONITORING)
      Logger.log(Level.CONFIG, "Bluetooth monitoring is enabled", ""); 
    this.fileResource = new ResourceMonitor("file.txt", "file", ClientShared.getActivityDirectory(this.course, this.activity), this.properties);
    this.fileResource.addListener(this);
    this.fileResource.open();
    ExamDialog.setStatus("11:file ok");
    this.networkResource = new ResourceMonitor("network.txt", "network", ClientShared.getActivityDirectory(this.course, this.activity), this.properties);
    this.networkResource.addListener(this);
    this.networkResource.open();
    ExamDialog.setStatus("12:network ok");
    ServletProcessor.getInstance(this.course, this.activity);
    ExamDialog.setStatus("13:web server ok");
    try {
      ModuleManager mmi = ModuleManager.getInstance();
      mmi.setToken(getToken());
      mmi.addSharedProperty("course", this.course);
      mmi.addSharedProperty("activity", this.activity);
      mmi.addSharedProperty("name", this.name);
      mmi.addSharedProperty("id", this.id);
      mmi.addSharedProperty("directory", ClientShared.getBaseDirectory(this.course, this.activity));
      mmi.addSharedProperty("host", ClientShared.CMS_HOST);
      mmi.addSharedProperty("port", ClientShared.PORT);
      mmi.addSharedProperty("protocol", ClientShared.PROTOCOL);
      mmi.addSharedProperty("token", getToken());
      mmi.addSharedProperty("logger", new LoggerModuleBridge(this.logArchiver));
      mmi.configure(this.properties);
    } catch (Exception e1) {
      this.logArchiver.put(Level.WARNING, "Module configuration exception " + e1);
    } 
    takeWebcamImage();
    ExamDialog.setStatus("14:webcam");
    if (ClientShared.USE_SCREEN_SHOTS)
      Logger.log(Level.CONFIG, "Screen shots are enabled", ""); 
    if (ClientShared.USE_WEB_CAM || ClientShared.USE_WEB_CAM_ON_SCREEN_SHOT)
      Logger.log(Level.CONFIG, "Web cam is enabled", ""); 
    try {
      String contents = ClipboardManager.getContents();
      ClipboardManager.setContents("Emptied by Invigilator");
      this.logArchiver.put(Level.INFO, "CLIPBOARD: " + contents);
    } catch (Exception exception) {}
    ExamDialog.setStatus("15:clipboard");
    try {
      Logger.setup(Invigilator.class, "comas-system", ClientShared.getActivityDirectory(this.course, this.activity), ClientShared.LOGGING_LEVEL);
    } catch (IOException e) {
      Logger.log(Level.WARNING, "Could not create log file in ", ClientShared.getActivityDirectory(this.course, this.activity));
    } 
    this.logArchiver.put(Level.INFO, "Startup successful (version 0.7.5) for " + this.course + "/" + this.activity + " using java version " + this.properties.getProperty("JAVA_VERSION") + " on " + this.properties.getProperty("OS"));
    ExamDialog.setStatus("16:startup ok");
    ExamDialog.instance.setTitleStatus(1);
    this.actualStartTime = System.currentTimeMillis();
    this.properties.setProperty("ACTUAL_START_TIME", this.actualStartTime);
    this.initHasRun = true;
    Logger.output("Exam start " + (new Date(this.actualStartTime)).toString());
    if (ClientShared.STARTUP_MESSAGE.length() > 0)
      JOptionPane.showMessageDialog((Component)ExamDialog.instance, ClientShared.STARTUP_MESSAGE, "CoMaS Startup", 1, IconLoader.getDefaultIcon()); 
  }
  
  private void createOStools() throws MalformedURLException, IOException {
    String os = System.getProperty("os.name").toLowerCase();
    Logger.log(Level.FINE, "Creating ", String.valueOf(os) + " tools");
    if (os.startsWith("win")) {
      Client client = ClientHelper.createClient(ClientShared.PROTOCOL);
      WebTarget webTarget = client.target(ClientShared.BASE_CMS).path("exam").path("handle.exe");
      Invocation.Builder invocationBuilder = webTarget.request();
      invocationBuilder.cookie("token", getToken());
      Response response = invocationBuilder.get();
      InputStream is = (InputStream)response.readEntity(InputStream.class);
      File downloadsDirectory = new File(ClientShared.DOWNLOADS_DIR);
      Utils.getAndStoreFile(is, "handle.exe", downloadsDirectory);
      is.close();
      File file = new File(ClientShared.DOWNLOADS_DIR, "handle.exe");
      file.deleteOnExit();
    } 
    ExamDialog.setStatus("8:OS tools");
    Logger.log(Level.FINE, "Created ", String.valueOf(os) + " tools");
  }
  
  private void createStudentNotes() {
    String studentNotesFileName = this.properties.getProperty("STUDENT_NOTES_FILE_NAME");
    if (studentNotesFileName != null) {
      File studentNotes = new File(studentNotesFileName);
      Logger.log(Level.FINE, "Notes selected ", studentNotes.getAbsoluteFile());
      try {
        FileInputStream fis = new FileInputStream(studentNotes);
        String resourcesDir = ClientShared.getResourcesDirectory(this.course, this.activity);
        if (studentNotesFileName.endsWith(".zip")) {
          Utils.unpackArchive(fis, new File(resourcesDir));
        } else {
          File dest = new File(String.valueOf(resourcesDir) + File.separator + studentNotes.getName());
          Files.copy(fis, dest.toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
        } 
        Logger.log(Level.INFO, "Notes chosen were ", studentNotes.getAbsoluteFile() + ", available in " + resourcesDir);
      } catch (IOException e) {
        this.logArchiver.put(Level.WARNING, "Could not access notes " + studentNotes.getAbsolutePath());
      } 
    } 
  }
  
  private void createResources() {
    synchronized (getClass()) {
      Logger.log(Level.FINE, "Creating ", "resources: " + ClientShared.getResourcesDirectory(this.course, this.activity));
      try {
        Client client = ClientHelper.createClient(ClientShared.PROTOCOL);
        WebTarget webTarget = client.target(ClientShared.BASE_EXAM).path("deployed").path("resources");
        Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" });
        invocationBuilder.accept(new String[] { "application/zip" });
        invocationBuilder.cookie("token", getToken());
        Form form = new Form();
        form.param("course", this.course);
        form.param("activity", this.activity);
        form.param("version", ClientShared.VERSION);
        form.param("passkey", ClientShared.PASSKEY_EXAM);
        Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded"));
        InputStream is = (InputStream)response.readEntity(InputStream.class);
        File zip = Utils.unpackArchive(is, new File(ClientShared.getResourcesDirectory(this.course, this.activity)));
        is.close();
        Logger.log(Level.INFO, "", "Resources can be found in " + ClientShared.getResourcesDirectory(this.course, this.activity));
        notifyObservers("11:resources ok");
        zip.delete();
      } catch (Exception e) {
        Logger.log(Level.INFO, "", "No " + ClientShared.getResourcesDirectory(this.course, this.activity) + "resources.zip");
        File zip = new File(String.valueOf(ClientShared.getResourcesDirectory(this.course, this.activity)) + "resources.zip");
        zip.delete();
        zip = new File(String.valueOf(ClientShared.getResourcesDirectory(this.course, this.activity)) + "arc.zip");
        zip.delete();
        File resourcesDirectory = new File(ClientShared.getResourcesDirectory(this.course, this.activity));
        File[] files = resourcesDirectory.listFiles();
        if (files == null || files.length == 0)
          resourcesDirectory.delete(); 
      } 
      ExamDialog.setStatus("7:resources ok");
      Logger.log(Level.FINE, "Created ", "resources: " + ClientShared.getResourcesDirectory(this.course, this.activity));
    } 
  }
  
  private void createExam() throws MalformedURLException, IOException {
    if (this.properties.containsKey("NO_DOWNLOAD_REQUIRED")) {
      Logger.log(Level.INFO, "", "No download was required, exam directory not overwritten");
      return;
    } 
    synchronized (getClass()) {
      Logger.log(Level.FINE, "Processing ", "exam: " + ClientShared.getExamDirectory(this.course, this.activity));
      Client client = ClientHelper.createClient(ClientShared.PROTOCOL);
      WebTarget webTarget = client.target(ClientShared.BASE_EXAM).path("deployed").path("activity");
      Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" });
      invocationBuilder.accept(new String[] { "application/zip" });
      invocationBuilder.cookie("token", getToken());
      Form form = new Form();
      form.param("course", this.course);
      form.param("activity", this.activity);
      form.param("version", ClientShared.VERSION);
      form.param("passkey", ClientShared.PASSKEY_EXAM);
      Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded"));
      InputStream is = (InputStream)response.readEntity(InputStream.class);
      File examDirectory = new File(ClientShared.getExamDirectory(this.course, this.activity));
      File zip = Utils.unpackArchive(is, examDirectory);
      is.close();
      ExamDialog.setStatus("6:exam ok");
      zip.delete();
      if (examDirectory.exists()) {
        File[] examFiles = examDirectory.listFiles();
        if (examFiles == null || examFiles.length == 0) {
          examDirectory.delete();
        } else {
          Logger.log(Level.INFO, "", "Exam can be found in " + ClientShared.getExamDirectory(this.course, this.activity));
        } 
      } 
      Logger.log(Level.FINE, "Processed ", "exam: " + ClientShared.getExamDirectory(this.course, this.activity));
    } 
  }
  
  private boolean makeDirectory(String dirname) {
    File file = new File(dirname);
    if (!file.exists()) {
      if (!file.mkdirs()) {
        this.done = true;
        Logger.log(Level.SEVERE, "", "Could not make directory " + dirname);
        notifyObservers("Could not make directory " + dirname);
        return false;
      } 
      Logger.log(Level.CONFIG, "", "Directory made: " + dirname);
    } else if (!file.isDirectory()) {
      this.done = true;
      Logger.log(Level.SEVERE, "", String.valueOf(dirname) + " is not a directory");
      notifyObservers(String.valueOf(dirname) + " is not a directory");
      return false;
    } 
    return true;
  }
  
  private void setupDirectoryWatching() {
    Logger.log(Level.FINE, "", "Setting up directory monitoring");
    this.fileSystemMonitor = new FileSystemMonitor(this.logArchiver, this.course, this.activity);
    this.fileSystemMonitor.addListener(this);
    this.fileSystemMonitor.open();
    Logger.log(Level.FINE, "", "Set up directory monitoring");
  }
  
  private void stop() {
    Logger.log(Level.INFO, "", "Services stopping");
    ModuleManager.getInstance().stop();
    if (this.fileSystemMonitor != null) {
      ExamDialog.setStatus("3:file monitor");
      this.fileSystemMonitor.close();
      this.fileSystemMonitor = null;
      ExamDialog.updateProgress("File system monitoring ended", 3);
    } 
    if (this.sentinel != null)
      this.sentinel.stop(); 
    if (this.screenShotArchiver != null) {
      ExamDialog.setStatus("4:screen shots");
      this.screenShotArchiver.stop();
      this.screenShotArchiver = null;
      ExamDialog.updateProgress("Screen shot monitoring ended", 4);
    } 
    if (this.examArchiver != null) {
      ExamDialog.setStatus("5:archiver");
      this.examArchiver.stop();
      this.examArchiver = null;
      ExamDialog.updateProgress("Archiving ended", 5);
    } 
    if (this.logArchiver != null) {
      ExamDialog.setStatus("6:logs");
      this.logArchiver.stop();
      this.logArchiver = null;
      ExamDialog.updateProgress("Logging ended", 6);
    } 
    if (this.fileResource != null) {
      ExamDialog.setStatus("7:files");
      this.fileResource.close();
      this.fileResource = null;
      ExamDialog.updateProgress("Open file monitoring ended", 7);
    } 
    if (this.networkResource != null) {
      ExamDialog.setStatus("8:network");
      this.networkResource.close();
      this.networkResource = null;
      ExamDialog.updateProgress("Network monitoring ended", 8);
    } 
    if (this.endpoint != null) {
      ExamDialog.setStatus("9:web");
      this.endpoint.close();
      this.endpoint = null;
      ExamDialog.updateProgress("Session control ended", 9);
    } 
    Logger.log(Level.INFO, "", "Services stopped");
    copyLogFile();
  }
  
  public static Response activities(String course, String token) {
    Client client = ClientHelper.createClient(ClientShared.PROTOCOL);
    WebTarget webTarget = client.target(ClientShared.BASE_LOGIN).path("startable-activities");
    Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" });
    invocationBuilder.accept(new String[] { "application/json" });
    if (token != null)
      invocationBuilder.cookie("token", token); 
    Form form = new Form();
    form.param("course", course);
    form.param("version", ClientShared.VERSION);
    form.param("passkey", ClientShared.PASSKEY_DIRECTORY);
    Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded"));
    return response;
  }
  
  public static Response activity(String course, String activity, String studentName, String token) {
    Client client = ClientHelper.createClient(ClientShared.PROTOCOL);
    WebTarget webTarget = client.target(ClientShared.BASE_LOGIN).path("activities").path(activity);
    Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" });
    invocationBuilder.accept(new String[] { "application/json" });
    if (token != null)
      invocationBuilder.cookie("token", token); 
    Form form = new Form();
    form.param("course", course);
    form.param("version", ClientShared.VERSION);
    form.param("passkey", ClientShared.PASSKEY_DIRECTORY);
    form.param("name", studentName);
    Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded"));
    return response;
  }
  
  public Response authenticate() {
    return authenticate("authenticate");
  }
  
  public Response authenticate(String service) {
    Client client = ClientHelper.createClient(ClientShared.PROTOCOL);
    WebTarget webTarget = client.target(ClientShared.BASE_LOGIN).path(service);
    Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" });
    invocationBuilder.accept(new String[] { "application/json" });
    Form form = new Form();
    form.param("course", this.course);
    form.param("activity", this.activity);
    form.param("version", ClientShared.VERSION);
    form.param("passkey", ClientShared.PASSKEY_DIRECTORY);
    form.param("name", this.name);
    form.param("url", String.valueOf(ClientShared.VIDEO_URL) + "/" + this.course + "/" + this.activity + "/" + this.name);
    form.param("type", this.state);
    form.param("description", this.id);
    String token = getToken();
    if (token != null) {
      invocationBuilder.cookie("token", token);
      form.param("token", token);
    } 
    Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded"));
    return response;
  }
  
  private String activityKey(String activity, String key) {
    return String.valueOf(activity) + "-" + key;
  }
  
  public int canStart() {
    try {
      Response response = authenticate();
      String rtn = (String)response.readEntity(String.class);
      if (rtn.equalsIgnoreCase("{\"DOES NOT EXIST\"}"))
        return -1; 
      if (rtn.equalsIgnoreCase("{\"ILLEGAL VERSION\"}"))
        return -2; 
      if (rtn.equalsIgnoreCase("{\"STOPPED\"}"))
        return -3; 
      JsonReader reader = Json.createReader(new StringReader(rtn));
      JsonObject meAsJson = reader.readObject();
      if (meAsJson != null) {
        String nowAsString = meAsJson.getJsonString(activityKey(this.activity, "ACCESS")).getString();
        this.properties.setProperty("CURRENT_TIME", nowAsString);
        String startAsString = getJsonString(meAsJson, "START_MSECS", "0");
        this.properties.setProperty("student.directory.ALLOWED_START_TIME", getJsonString(meAsJson, "START", "1970/01/01 7:00 AM"));
        String endAsString = getJsonString(meAsJson, "END_MSECS", "9223372036854775807");
        this.properties.setProperty("student.directory.ALLOWED_END_TIME", getJsonString(meAsJson, "END", "2099/01/01 7:00 AM"));
        String durationAsString = getJsonString(meAsJson, activityKey(this.activity, "DURATION"), "10000");
        this.properties.setProperty("student.directory.ALLOWED_DURATION", durationAsString);
        String tokenAsString = meAsJson.getJsonString("TOKEN").getString();
        this.properties.setProperty("TOKEN", tokenAsString);
        long now = Long.parseLong(nowAsString);
        long start = Long.parseLong(startAsString);
        long end = Long.parseLong(endAsString);
        if (now < start)
          return 0; 
        if (now > end)
          return 0; 
        return 1;
      } 
      return 0;
    } catch (Exception e) {
      Logger.log(Level.WARNING, "Start exam time check error: ", e.getMessage());
      return 0;
    } 
  }
  
  String getJsonString(JsonObject o, String key, String defaultValue) {
    JsonString jsonString = o.getJsonString(activityKey(this.activity, key));
    if (jsonString == null)
      return defaultValue; 
    return jsonString.getString();
  }
  
  public boolean login() {
    try {
      Response response = authenticate("authenticate");
      String rtn = (String)response.readEntity(String.class);
      createRuntimeEnvironment(rtn);
      createCommandAndControlChannel();
      this.isAllowedToUpload = true;
      if (rtn.equalsIgnoreCase("{\"DOES NOT EXIST\"}")) {
        this.done = true;
        this.isAllowedToUpload = false;
        notifyObservers("You are not registered. Please quit now.");
      } else if (rtn.equalsIgnoreCase("{\"ILLEGAL VERSION\"}")) {
        this.done = true;
        this.logArchiver.put(Level.SEVERE, "Illegal version detected (" + ClientShared.VERSION + ")");
        this.isAllowedToUpload = false;
        notifyObservers("Illegal version detected (" + ClientShared.VERSION + ").\n" + ClientShared.SUPPORT_MESSAGE);
        System.exit(0);
      } else if (rtn.equalsIgnoreCase("{\"STOPPED\"}")) {
        this.done = true;
        Logger.log(Level.INFO, "", "Session is over, please wait for archive upload");
        notifyObservers("Session is over, please wait for archive upload");
        System.exit(0);
      } else {
        JsonReader reader = Json.createReader(new StringReader(rtn));
        JsonObject meAsJson = reader.readObject();
        String tokenAsString = meAsJson.getJsonString("TOKEN").getString();
        if (tokenAsString != null)
          this.properties.setProperty("TOKEN", tokenAsString); 
        Logger.log(Level.FINE, "[" + response.getStatus() + "] ", "Login");
      } 
      boolean status = (response.getStatus() < 204);
      if (!this.done)
        if (status) {
          notifyObservers("Logged in");
          if (this.state.equals("Login"))
            ExamDialog.instance.setTitleStatus(1); 
        } else {
          notifyObservers("Not logged in (" + this.failures + ")");
          ExamDialog.instance.setTitleStatus(-1);
        }  
      return status;
    } catch (Exception e) {
      Logger.log(Level.INFO, "", "Could not login: " + e.getMessage());
      notifyObservers("Not logged in (" + this.failures + ")");
      ExamDialog.instance.setTitleStatus(-1);
      return false;
    } 
  }
  
  private void createRuntimeEnvironment(String json) {
    synchronized (getClass()) {
      if (this.me == null && this.state.equals("Login"))
        try {
          Logger.log(Level.FINE, "Processing ", "tools: " + ClientShared.getToolsDirectory(this.course, this.activity));
          Client client = ClientHelper.createClient(ClientShared.PROTOCOL);
          WebTarget webTarget = client.target(ClientShared.BASE_CMS).path("exam").path("tools.zip");
          Invocation.Builder invocationBuilder = webTarget.request();
          invocationBuilder.accept(new String[] { "application/zip" });
          invocationBuilder.cookie("token", getToken());
          Response response = invocationBuilder.get();
          InputStream is = (InputStream)response.readEntity(InputStream.class);
          File toolsDirectory = new File(ClientShared.getToolsDirectory(this.course, this.activity));
          File zip = Utils.unpackArchive(is, toolsDirectory);
          is.close();
          Logger.log(Level.INFO, "", "Tools can be found in " + ClientShared.getToolsDirectory(this.course, this.activity));
          notifyObservers("Tools ok");
          zip.delete();
          JsonReader reader = Json.createReader(new StringReader(json));
          JsonObject meAsJson = reader.readObject();
          this.me = new HashMap<>();
          reader.close();
          for (Map.Entry<String, JsonValue> entry : (Iterable<Map.Entry<String, JsonValue>>)meAsJson.entrySet()) {
            if (!((String)entry.getKey()).contains("-")) {
              String key = entry.getKey();
              String value = ((JsonValue)entry.getValue()).toString();
              this.me.put(key, value);
              this.properties.setProperty("student.directory." + key, Named.unquoted(value));
            } 
          } 
          Logger.debug(Level.INFO, this.properties.toString());
          this.me.put("directory_host", Named.quoted(ClientShared.DIRECTORY_HOST));
          this.me.put("log_host", Named.quoted(ClientShared.LOG_HOST));
          this.me.put("upload_host", Named.quoted(ClientShared.UPLOAD_HOST));
          this.me.put("video_host", Named.quoted(ClientShared.VIDEO_HOST));
          this.me.put("cms_host", Named.quoted(ClientShared.CMS_HOST));
          this.me.put("protocol", Named.quoted(ClientShared.PROTOCOL));
          this.me.put("ws_protocol", Named.quoted(ClientShared.WS_PROTOCOL));
          this.me.put("port", ClientShared.PORT);
          this.me.put("course", Named.quoted(this.course));
          this.me.put("activity", Named.quoted(this.activity));
          try {
            File tools_dir = new File(ClientShared.getToolsDirectory(this.course, this.activity));
            File[] files = tools_dir.listFiles();
            byte b;
            int i;
            File[] arrayOfFile1;
            for (i = (arrayOfFile1 = files).length, b = 0; b < i; ) {
              File file = arrayOfFile1[b];
              Logger.log(Level.CONFIG, "", String.valueOf(file.getName()) + " in " + ClientShared.getToolsDirectory(this.course, this.activity));
              CustomResourceCreator.generate(file, this.me.entrySet(), tools_dir);
              b++;
            } 
            BrowserTask bt = new BrowserTask(tools_dir, this.properties);
            bt.start();
          } catch (IOException e) {
            Logger.log(Level.WARNING, "", "Could not process a tool directory file: " + e.getMessage());
          } 
        } catch (Exception e) {
          Logger.log(Level.WARNING, "", "Could not process " + ClientShared.TOOLS_ZIP);
          notifyObservers("No tools");
        }  
    } 
  }
  
  private void createCommandAndControlChannel() {
    synchronized (getClass()) {
      if (this.endpoint == null && this.me != null)
        try {
          Logger.log(Level.FINE, "", "Creating CCI");
          String sUri = ClientShared.service(ClientShared.WS_PROTOCOL, ClientShared.WEBSOCKET_HOST, ClientShared.PORT, "/WebSocket/channel/" + this.course + "/" + this.activity + "/");
          String endpointName = Named.unquoted(this.me.get("ID"));
          sUri = String.valueOf(sUri) + Named.canonical(this.name) + '-' + endpointName + "/" + Named.unquoted(this.properties.getProperty("student.directory.PASSWORD"));
          this.endpoint = new WebsocketClientEndpoint(new URI(sUri));
          this.endpoint.addListener(this);
          this.endpoint.addMessageHandler("stop", (MessageHandler)new StopMessageHandler());
          this.endpoint.addMessageHandler("unload", (MessageHandler)new UnloadMessageHandler());
          this.endpoint.addMessageHandler("load", (MessageHandler)new LoadMessageHandler());
          this.endpoint.addMessageHandler("url", (MessageHandler)new URLMessageHandler());
          this.endpoint.addMessageHandler("alert", (MessageHandler)new AlertMessageHandler());
          this.endpoint.addMessageHandler("ping", (MessageHandler)new PingMessageHandler());
          this.endpoint.addMessageHandler("level", (MessageHandler)new LogMessageHandler());
          this.endpoint.addMessageHandler("screenShot", (MessageHandler)new ScreenShotFrequencyMessageHandler());
          configureExtendedProtocolHandlers();
          this.endpoint.open();
          this.endPointFailed = false;
        } catch (Exception e) {
          this.endpoint = null;
          this.endPointFailed = true;
          Logger.log(Level.WARNING, "CCI: ", e.getMessage());
        }  
    } 
  }
  
  private void configureExtendedProtocolHandlers() {
    if (this.endpoint != null) {
      int i = 1;
      String urlProp = this.properties.getProperty("protocol.handler.url");
      if (urlProp == null) {
        Logger.log(Level.FINE, "No CCI protocol handlers loaded", "");
        return;
      } 
      if (urlProp.startsWith("/"))
        urlProp = ClientShared.service(ClientShared.PROTOCOL, ClientShared.CMS_HOST, ClientShared.PORT, urlProp); 
      ModuleClassLoader classLoader = new ModuleClassLoader(new URL[0], MessageHandler.class.getClassLoader());
      try {
        classLoader.addURL(new URL(urlProp));
        String handlerString = this.properties.getProperty("protocol.handler." + i);
        while (handlerString != null) {
          String[] tokens = handlerString.split(",");
          if (tokens != null) {
            if (tokens.length == 2) {
              String type = tokens[0].toLowerCase().trim();
              String messageHandlerClass = tokens[1].trim();
              try {
                Class<?> clazz = Class.forName(messageHandlerClass, true, (ClassLoader)classLoader);
                this.endpoint.addMessageHandler(type, clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
                Logger.log(Level.INFO, String.format("CCI protocol handler loaded for %s type from %s", new Object[] { type, messageHandlerClass }), "");
              } catch (Exception e) {
                Logger.log(Level.WARNING, String.format("CCI protocol handler creation error for %s: ", new Object[] { messageHandlerClass }), e);
              } 
            } else {
              Logger.log(Level.WARNING, "CCI protocol handler had too few (<2) tokens: ", handlerString);
            } 
          } else {
            Logger.log(Level.WARNING, "No CCI protocol handler defined for: ", handlerString);
          } 
          i++;
          handlerString = this.properties.getProperty("protocol.handler." + i);
        } 
      } catch (MalformedURLException urlException) {
        Logger.log(Level.WARNING, String.format("protocol.handler.url has an illegal URL format: %s", new Object[] { urlProp }), "");
      } finally {
        try {
          classLoader.close();
        } catch (IOException iOException) {}
      } 
    } 
  }
  
  public Thread runExam() {
    this.thread = new Thread(new Runnable() {
          public void run() {
            Invigilator.this.init();
            Invigilator.this.monitorUser();
            Logger.log(Level.INFO, "", "Session has ended");
          }
        });
    this.thread.start();
    return this.thread;
  }
  
  private void monitorUser() {
    int i = 0;
    while (continueSession()) {
      ping();
      checkScreenSize();
      takeScreenShot();
      try {
        long timeToSleep = (1000 * randomInt(ClientShared.MIN_INTERVAL, this.current));
        this.wakeup = System.currentTimeMillis() + timeToSleep;
        Thread.sleep(timeToSleep);
      } catch (InterruptedException interruptedException) {}
      if (++i % ClientShared.AUTO_ARCHIVE_FREQUENCY == 0 && ClientShared.AUTO_ARCHIVE)
        createAndUploadArchive(true); 
    } 
  }
  
  public synchronized int randomInt(int bottom, int top) {
    return bottom + this.r.nextInt(top);
  }
  
  private void checkScreenSize() {
    if (!this.screenSizeCheckLogged && !VMDetector.isStdResolution()) {
      this.screenSizeCheckLogged = true;
      this.logArchiver.put(Level.WARNING, "Non-standard screen size detected, possible virtual machine?");
    } 
  }
  
  private boolean continueSession() {
    String currentState = this.state;
    if (this.failures > 0)
      this.state = "Logging in"; 
    boolean okay = login();
    if (this.failures > 0)
      this.state = currentState; 
    if (!okay) {
      if (this.failures == 0) {
        this.failureStartTime = System.currentTimeMillis();
        String failureMessage = String.format("Login failed %.02f seconds into session", new Object[] { Double.valueOf((this.failureStartTime - this.actualStartTime) / 1000.0D) });
        if (this.logArchiver != null) {
          this.logArchiver.put(Level.WARNING, failureMessage);
        } else {
          Logger.log(Level.WARNING, failureMessage, "");
        } 
      } 
      this.properties.remove("TOKEN");
      this.failures++;
      this.current = (int)(this.current * this.alpha);
      if (this.current < ClientShared.MIN_INTERVAL)
        this.current = ClientShared.MIN_INTERVAL; 
      if (this.failures % ClientShared.ALERT_FAILURE_FREQUENCY == 0)
        alert("Failed to login " + this.failures + " times.\n" + ClientShared.SUPPORT_MESSAGE); 
    } else {
      if (this.failures > 0) {
        this.state = "Login (" + this.failures + ")";
        long failureInterval = (System.currentTimeMillis() - this.failureStartTime) / 1000L;
        String failureMessage = String.format("Login successful after %d failures in %d seconds", new Object[] { Integer.valueOf(this.failures), Long.valueOf(failureInterval) });
        if (this.logArchiver != null) {
          this.logArchiver.put(Level.INFO, failureMessage);
        } else {
          Logger.log(Level.INFO, "", failureMessage);
        } 
      } else {
        this.state = "Login";
      } 
      this.failures = 0;
      this.current = (int)(this.current / this.alpha);
      if (this.current > ClientShared.MAX_INTERVAL)
        this.current = ClientShared.MAX_INTERVAL; 
    } 
    return (this.failures < ClientShared.MAX_SESSION_FAILURES && !this.done);
  }
  
  private void ping() {
    if (this.endpoint != null)
      try {
        this.endpoint.sendMessage(new Message());
      } catch (IOException|javax.websocket.EncodeException iOException) {} 
  }
  
  public Webcam getWebcam() {
    if (this.webcam == null)
      this.webcam = Webcam.getDefault(); 
    return this.webcam;
  }
  
  private BufferedImage getQRCode(String barcodeText) throws WriterException {
    QRCodeWriter barcodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 50, 50);
    return MatrixToImageWriter.toBufferedImage(bitMatrix);
  }
  
  private void takeScreenShot() {
    if (!ClientShared.USE_SCREEN_SHOTS)
      return; 
    try {
      String screensDirString = ClientShared.getScreensDirectory(this.course, this.activity);
      File screensDir = new File(screensDirString);
      if (screensDir.exists() && screensDir.isDirectory() && screensDir.canWrite()) {
        BufferedImage[] screenFullImage = ScreenShotCreator.getImages();
        if (System.currentTimeMillis() > this.wakeup + Math.max(60000, ClientShared.MAX_INTERVAL * 1000)) {
          this.logArchiver.log(Level.INFO, String.format("Machine was possibly suspended (%d)", new Object[] { Long.valueOf(this.wakeup) }));
          try {
            if (this.webcam != null)
              this.webcam.close(); 
          } catch (Exception exception) {}
        } 
        long timeInMsec = (new Date()).getTime();
        for (int indexOfScreenShot = 0; indexOfScreenShot < screenFullImage.length; indexOfScreenShot++) {
          BufferedImage webcamImage = null;
          if (ClientShared.USE_WEB_CAM_ON_SCREEN_SHOT) {
            if (this.webcam == null)
              this.webcam = Webcam.getDefault(); 
            if (this.webcam != null) {
              if (!this.webcam.isOpen()) {
                Dimension[] sizes = this.webcam.getViewSizes();
                byte b;
                int i;
                Dimension[] arrayOfDimension1;
                for (i = (arrayOfDimension1 = sizes).length, b = 0; b < i; ) {
                  Dimension size = arrayOfDimension1[b];
                  if (size.getWidth() * 1.0D / screenFullImage[indexOfScreenShot].getWidth() > 0.05D) {
                    this.webcam.setViewSize(size);
                    break;
                  } 
                  b++;
                } 
                this.webcam.open();
              } 
              if (this.webcam.isOpen()) {
                webcamImage = this.webcam.getImage();
                if (webcamImage != null) {
                  if (screenFullImage != null) {
                    Graphics g = screenFullImage[indexOfScreenShot].getGraphics();
                    g.drawImage(webcamImage, 0, 0, null);
                    g.dispose();
                  } 
                } else {
                  this.logArchiver.put(Level.WARNING, "Cannot acquire a webcam image");
                } 
              } 
            } else {
              this.logArchiver.put(Level.WARNING, "Cannot find a webcam");
            } 
          } 
          if (screenFullImage[indexOfScreenShot] != null) {
            if (this.qrCode == null && ClientShared.SCREEN_SHOT_QR_CODE_REQUIRED)
              try {
                String qrLabel, host = this.properties.getProperty("LOCAL_ADDRESS");
                String macAddress = MacAddress.getMACAddresses();
                if (host != null && macAddress != null) {
                  qrLabel = String.format("%s/%s/%s/%s/%s/%s", new Object[] { this.course, this.activity, this.name, this.id, host, macAddress });
                } else {
                  qrLabel = String.format("%s/%s/%s/%s/%s", new Object[] { this.course, this.activity, this.name, this.id, host });
                } 
                this.qrCode = getQRCode(qrLabel);
              } catch (Exception exception) {} 
            Graphics g = screenFullImage[indexOfScreenShot].getGraphics();
            if (this.qrCode != null)
              g.drawImage(this.qrCode, screenFullImage[indexOfScreenShot].getWidth() - this.qrCode.getWidth(), 0, null); 
            if (ClientShared.SCREEN_SHOT_TIMESTAMP_REQUIRED) {
              int x = Math.round(screenFullImage[indexOfScreenShot].getWidth() * ClientShared.SCREEN_SHOT_TIMESTAMP_WIDTH) - 150;
              if (x < 0)
                x = 0; 
              if (x > screenFullImage[indexOfScreenShot].getWidth() - 320)
                x = screenFullImage[indexOfScreenShot].getWidth() - 320; 
              int y = Math.round(screenFullImage[indexOfScreenShot].getHeight() * ClientShared.SCREEN_SHOT_TIMESTAMP_HEIGHT);
              if (y > screenFullImage[indexOfScreenShot].getHeight() - 50)
                y = screenFullImage[indexOfScreenShot].getHeight() - 50; 
              g.clearRect(x, y, 320, 50);
              g.setFont(g.getFont().deriveFont(20.0F));
              if (this.state.equals("Login")) {
                g.setColor(Color.white);
              } else if (this.state.contains("Issue:")) {
                g.setColor(Color.ORANGE);
              } 
              if (this.endPointFailed)
                g.setColor(Color.RED); 
              g.drawString((new Date()).toString(), x + 10, y + 25);
            } 
            g.dispose();
            String fileName = String.valueOf(screensDirString) + this.name + "-" + (timeInMsec + indexOfScreenShot) + "." + "jpg";
            ImageIO.write(screenFullImage[indexOfScreenShot], "jpg", new File(fileName));
            this.screenShotArchiver.put(fileName);
            this.state = "Login";
            if (this.screenShotProblem) {
              this.screenShotProblem = false;
              ExamDialog.instance.setTitleStatus(1);
              String msg = "Screen shot saving is now okay";
              if (this.logArchiver != null) {
                this.logArchiver.put(Level.INFO, msg);
              } else {
                Logger.log(Level.INFO, "", msg);
              } 
            } 
          } else {
            throw new IOException("Full screen image cannot be acquired");
          } 
        } 
      } else {
        throw new IOException(String.valueOf(screensDirString) + " cannot be accessed");
      } 
    } catch (Exception e) {
      String msg;
      Throwable reason = e.getCause();
      if (reason != null) {
        boolean stopWebCam = false;
        if (reason instanceof com.github.sarxos.webcam.WebcamException)
          stopWebCam = true; 
        reason = reason.getCause();
        if ((reason != null && reason instanceof com.github.sarxos.webcam.WebcamException) || e.getMessage().contains("execute task"))
          stopWebCam = true; 
        if (stopWebCam && !ClientShared.WEB_CAM_MANDATORY) {
          ClientShared.USE_WEB_CAM_ON_SCREEN_SHOT = false;
          this.logArchiver.put(Level.INFO, String.valueOf(e.getCause().toString()) + ", turned off web cam");
        } 
      } 
      this.screenShotProblem = true;
      ExamDialog.instance.setTitleStatus(-1);
      this.state = "Issue:Screen";
      if (e.getMessage() != null) {
        msg = "Cannot save screen: " + e.getMessage();
      } else {
        msg = "Cannot save screen: " + e;
      } 
      if (this.logArchiver != null) {
        this.logArchiver.put(Level.WARNING, msg);
      } else {
        Logger.log(Level.WARNING, "", msg);
      } 
    } 
  }
  
  private void takeWebcamImage() {
    if (ClientShared.USE_WEB_CAM) {
      ClientShared.USE_WEB_CAM_ON_SCREEN_SHOT = true;
      takeScreenShot();
      ClientShared.USE_WEB_CAM_ON_SCREEN_SHOT = false;
    } 
  }
  
  public boolean createAndUploadArchive(boolean upload) {
    if (!this.initHasRun)
      return true; 
    if (!ClientShared.AUTO_ARCHIVE)
      return true; 
    boolean rtn = true;
    String canonical_name = Named.canonical(this.name);
    String archive_name = String.valueOf(canonical_name) + "-" + "exam.zip";
    String archive_directory = ClientShared.getArchivesDirectory(this.course, this.activity);
    File archiveDirectoryFile = new File(archive_directory);
    if (!archiveDirectoryFile.exists() || !archiveDirectoryFile.isDirectory() || !archiveDirectoryFile.canWrite()) {
      if (this.logArchiver != null) {
        this.logArchiver.put(Level.WARNING, "Cannot access or write to archive directory: " + archive_directory);
      } else {
        Logger.log(Level.WARNING, "", "Cannot access or write to archive directory: " + archive_directory);
      } 
      this.state = "Issue:Archive";
      this.archiveProblem = true;
      rtn = false;
    } 
    String archive = String.valueOf(archive_directory) + archive_name;
    File file = new File(archive);
    if (file.exists()) {
      archive_name = String.valueOf(canonical_name) + "-" + (new Date()).getTime() + "-" + "exam.zip";
      file.renameTo(new File(String.valueOf(archive_directory) + archive_name));
    } 
    try {
      Zip.pack(ClientShared.getExamDirectory(this.course, this.activity), archive);
      Logger.log(Level.INFO, "", "Archive created: " + archive);
    } catch (IOException e) {
      this.state = "Issue:Archive";
      this.archiveProblem = true;
      ExamDialog.instance.setTitleStatus(-1);
      String msg = "Could not create archive: " + archive + " {" + e.getClass().getName() + "}";
      if (this.logArchiver != null) {
        this.logArchiver.put(Level.WARNING, msg);
      } else {
        Logger.log(Level.WARNING, "", msg);
      } 
      rtn = false;
    } 
    if (rtn && upload) {
      this.examArchiver.put(archive);
      notifyObservers("Archived");
    } 
    if (this.archiveProblem && rtn) {
      ExamDialog.instance.setTitleStatus(1);
      String msg = "Archive creation is now okay";
      this.archiveProblem = false;
      if (this.logArchiver != null) {
        this.logArchiver.put(Level.INFO, msg);
      } else {
        Logger.log(Level.INFO, "", msg);
      } 
    } 
    return rtn;
  }
  
  public void resourceEvent(Resource resource, String type, String description) {
    if (resource instanceof FileSystemMonitor) {
      if (type.equals("ALERT")) {
        alert(description);
        this.logArchiver.put(Level.WARNING, description);
      } else if (type.equals("CLOSE")) {
        if (!this.endedSession) {
          this.state = "Issue:Files";
          this.logArchiver.put(Level.SEVERE, description);
          this.fileSystemMonitor.restart();
        } 
      } else {
        this.logArchiver.put(Level.parse(type), description);
      } 
    } else if (resource instanceof ResourceMonitor) {
      this.logArchiver.put((Level)Level.LOGGED, description);
    } else if (resource instanceof WebsocketClientEndpoint) {
      if (description.equals("open")) {
        Logger.log(Level.FINE, "CCI: ", "open");
      } else if (description.equals("close") || description.equals("error")) {
        this.endpoint = null;
        this.endPointFailed = true;
        Logger.log(Level.FINE, "CCI: ", description);
      } 
    } 
  }
  
  public void alert(String description) {
    this.alert = description;
    notifyObservers();
  }
  
  public void notifyObservers(Object arg) {
    setChanged();
    super.notifyObservers(arg);
  }
  
  private void copyLogFile() {
    boolean logsToBeSaved = Utils.getBooleanOrDefault(this.properties, "logs.save", true);
    if (logsToBeSaved)
      try {
        Path pathToLogFile = Paths.get(ClientShared.DIR, new String[] { this.course, this.activity, "logs", "comas-system-log.html" });
        File logFile = pathToLogFile.toFile();
        if (logFile.exists()) {
          String savedName = "comas-system-" + System.currentTimeMillis() + "-log.html";
          logFile.renameTo(Paths.get(ClientShared.DIR, new String[] { this.course, this.activity, "logs", savedName }).toFile());
        } 
        String logDir = ClientShared.HOME;
        if (ClientShared.isWriteableDirectory(logDir)) {
          logDir = ClientShared.HOME;
        } else {
          logDir = ClientShared.getDesktopDirectory();
        } 
        Files.copy(Paths.get(logDir, new String[] { "comas-system-log.html" }), pathToLogFile, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
      } catch (IOException e) {
        Logger.log(Level.WARNING, "", "Could not copy log file");
      }  
  }
}
