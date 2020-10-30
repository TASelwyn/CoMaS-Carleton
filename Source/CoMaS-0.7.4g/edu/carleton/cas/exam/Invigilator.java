/*      */ package edu.carleton.cas.exam;
/*      */ 
/*      */ import com.github.sarxos.webcam.Webcam;
/*      */ import com.google.zxing.BarcodeFormat;
/*      */ import com.google.zxing.WriterException;
/*      */ import com.google.zxing.client.j2se.MatrixToImageWriter;
/*      */ import com.google.zxing.common.BitMatrix;
/*      */ import com.google.zxing.qrcode.QRCodeWriter;
/*      */ import edu.carleton.cas.background.KeepAliveInterface;
/*      */ import edu.carleton.cas.background.KeepAliveSentinel;
/*      */ import edu.carleton.cas.background.LogArchiver;
/*      */ import edu.carleton.cas.background.LoggerModuleBridge;
/*      */ import edu.carleton.cas.background.UploadArchiver;
/*      */ import edu.carleton.cas.constants.Shared;
/*      */ import edu.carleton.cas.file.Utils;
/*      */ import edu.carleton.cas.file.Zip;
/*      */ import edu.carleton.cas.jetty.embedded.ServletProcessor;
/*      */ import edu.carleton.cas.logging.Level;
/*      */ import edu.carleton.cas.logging.Logger;
/*      */ import edu.carleton.cas.messaging.Message;
/*      */ import edu.carleton.cas.messaging.MessageHandler;
/*      */ import edu.carleton.cas.messaging.handlers.AlertMessageHandler;
/*      */ import edu.carleton.cas.messaging.handlers.LoadMessageHandler;
/*      */ import edu.carleton.cas.messaging.handlers.LogMessageHandler;
/*      */ import edu.carleton.cas.messaging.handlers.PingMessageHandler;
/*      */ import edu.carleton.cas.messaging.handlers.ScreenShotFrequencyMessageHandler;
/*      */ import edu.carleton.cas.messaging.handlers.StopMessageHandler;
/*      */ import edu.carleton.cas.messaging.handlers.URLMessageHandler;
/*      */ import edu.carleton.cas.messaging.handlers.UnloadMessageHandler;
/*      */ import edu.carleton.cas.modules.foundation.ModuleClassLoader;
/*      */ import edu.carleton.cas.modules.foundation.ModuleManager;
/*      */ import edu.carleton.cas.resources.AbstractFileTask;
/*      */ import edu.carleton.cas.resources.BrowserTask;
/*      */ import edu.carleton.cas.resources.FileSystemMonitor;
/*      */ import edu.carleton.cas.resources.Resource;
/*      */ import edu.carleton.cas.resources.ResourceListener;
/*      */ import edu.carleton.cas.resources.ResourceMonitor;
/*      */ import edu.carleton.cas.ui.ExamDialog;
/*      */ import edu.carleton.cas.utility.ClientHelper;
/*      */ import edu.carleton.cas.utility.ClipboardManager;
/*      */ import edu.carleton.cas.utility.CustomResourceCreator;
/*      */ import edu.carleton.cas.utility.HTMLFileViewGenerator;
/*      */ import edu.carleton.cas.utility.IPAddressChecker;
/*      */ import edu.carleton.cas.utility.IconLoader;
/*      */ import edu.carleton.cas.utility.MacAddress;
/*      */ import edu.carleton.cas.utility.Named;
/*      */ import edu.carleton.cas.utility.Observable;
/*      */ import edu.carleton.cas.utility.ScreenShotCreator;
/*      */ import edu.carleton.cas.utility.Sleeper;
/*      */ import edu.carleton.cas.utility.VMDetector;
/*      */ import edu.carleton.cas.websocket.WebsocketClientEndpoint;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.StringReader;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URI;
/*      */ import java.net.URL;
/*      */ import java.net.UnknownHostException;
/*      */ import java.nio.file.CopyOption;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.Paths;
/*      */ import java.nio.file.StandardCopyOption;
/*      */ import java.text.NumberFormat;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Random;
/*      */ import java.util.logging.Level;
/*      */ import javax.imageio.ImageIO;
/*      */ import javax.json.Json;
/*      */ import javax.json.JsonObject;
/*      */ import javax.json.JsonReader;
/*      */ import javax.json.JsonString;
/*      */ import javax.json.JsonValue;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.ws.rs.client.Client;
/*      */ import javax.ws.rs.client.Entity;
/*      */ import javax.ws.rs.client.Invocation;
/*      */ import javax.ws.rs.client.WebTarget;
/*      */ import javax.ws.rs.core.Form;
/*      */ import javax.ws.rs.core.Response;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Invigilator
/*      */   extends Observable
/*      */   implements ResourceListener
/*      */ {
/*      */   public final String name;
/*      */   public final String id;
/*      */   public final String course;
/*      */   public final String activity;
/*      */   protected String state;
/*      */   public String alert;
/*      */   protected boolean done;
/*      */   protected Thread thread;
/*  119 */   protected final Random r = new Random();
/*      */ 
/*      */   
/*      */   public boolean isAllowedToUpload;
/*      */ 
/*      */   
/*      */   public UploadArchiver screenShotArchiver;
/*      */ 
/*      */   
/*      */   public LogArchiver logArchiver;
/*      */   
/*      */   public UploadArchiver examArchiver;
/*      */   
/*      */   public KeepAliveSentinel sentinel;
/*      */   
/*      */   private FileSystemMonitor fileSystemMonitor;
/*      */   
/*  136 */   private int current = Shared.MAX_INTERVAL;
/*  137 */   private int failures = 0;
/*  138 */   private long failureStartTime = 0L;
/*  139 */   private long actualStartTime = 0L;
/*      */ 
/*      */   
/*      */   private ResourceMonitor fileResource;
/*      */   
/*      */   private ResourceMonitor networkResource;
/*      */   
/*  146 */   private HashMap<String, String> me = null; private WebsocketClientEndpoint endpoint; private Properties properties; private boolean endedSession; private boolean initHasRun; boolean endPointFailed; long wakeup; private boolean screenSizeCheckLogged; private float alpha; private boolean screenShotProblem; private Webcam webcam; private BufferedImage qrCode; private boolean archiveProblem; public synchronized void endTheSession() { if (!this.endedSession) { this.endedSession = true; ExamDialog.setStatus("0:stopping"); ExamDialog.updateProgress("Stopping", 0); Logger.log(Level.FINE, "Ending session ", ""); ExamDialog.setStatus("1:web cam"); if (this.initHasRun)
/*      */         takeWebcamImage();  ExamDialog.updateProgress("Web cam complete", 1); ExamDialog.setStatus("2:archiving"); if (this.initHasRun)
/*      */         createAndUploadArchive(true);  ExamDialog.updateProgress("Archived exam", 2); this.state = "Logged out"; authenticate(); ExamDialog.updateProgress("Logged out", 3); this.done = true; if (this.thread != null)
/*      */         this.thread.interrupt();  stop(); ExamDialog.setStatus("10:stopped"); ExamDialog.updateProgress("Stopped", 10); Logger.log(Level.FINE, "Ended session ", ""); Logger.output("Exam end " + new Date(System.currentTimeMillis())); if (this.initHasRun && this.actualStartTime > 0L) { NumberFormat nf = NumberFormat.getInstance(); Logger.output("Session duration: " + nf.format((System.currentTimeMillis() - this.actualStartTime) / 1000.0D) + " secs"); }
/*      */        }
/*      */      }
/*      */   private void hookRunTime() { Logger.log(Level.FINE, "Creating session end hook", ""); Thread hook = new Thread() { public void run() { Invigilator.this.endTheSession(); } }
/*      */       ; Runtime.getRuntime().addShutdownHook(hook); Logger.log(Level.FINE, "", "Session hook established"); }
/*      */   public boolean canRunAtThisIPAddress() { IPAddressChecker checker; try { checker = new IPAddressChecker(this.properties); }
/*      */     catch (UnknownHostException e) { String defaultCanRun = this.properties.getProperty("default", "true"); if (defaultCanRun.equals("true"))
/*      */         return true;  if (defaultCanRun.equals("false"))
/*      */         return false;  return false; }
/*      */      if (checker.deny())
/*      */       return false;  return checker.allow(); }
/*      */   public boolean isSane() { ExamDialog.setStatus("4:auth check"); if (!login() || !this.isAllowedToUpload)
/*      */       return false;  boolean sanity_check_required = Utils.getBooleanOrDefault(this.properties, "sanity_check", false); if (sanity_check_required) { ExamDialog.setStatus("5:log check"); if (!this.logArchiver.log(Level.OFF, "test")) { notifyObservers("Logging service not available"); return false; }
/*      */        ExamDialog.setStatus("6:upload check"); try { File test = File.createTempFile("test-", "-test"); FileOutputStream fos = new FileOutputStream(test); fos.write(42); fos.close(); if (Shared.AUTO_ARCHIVE && !this.examArchiver.uploadArchive(test, Shared.BASE_UPLOAD, "upload")) { notifyObservers("Upload services not available"); return false; }
/*      */          test.delete(); }
/*      */       catch (Exception e) { notifyObservers("Exam services not available"); return false; }
/*      */        }
/*      */      return true; }
/*      */   public boolean isExamOk() { ExamDialog.setStatus("7:exam check"); try { Client client = ClientHelper.createClient(Shared.PROTOCOL); WebTarget webTarget = client.target(Shared.BASE_EXAM).path("deployed").path("activity"); Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" }); invocationBuilder.accept(new String[] { "text/plain" }); Form form = new Form(); form.param("course", this.course); form.param("activity", this.activity); form.param("version", Shared.VERSION); form.param("passkey", Shared.PASSKEY_EXAM); Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded")); Logger.log(Level.INFO, String.format("[%s] %s", new Object[] { Integer.valueOf(response.getStatus()), "Exam check response" }), ""); return (response.getStatus() == 200); }
/*      */     catch (Exception e) { Logger.log(Level.WARNING, String.format("Exam check failure for %s: %s", new Object[] { Shared.BASE_EXAM, e.getMessage() }), ""); return false; }
/*      */      }
/*      */   public boolean isDone() { return this.done; }
/*      */   private void init() { String logDir = Shared.HOME; try { if (Shared.isWriteableDirectory(logDir)) { logDir = String.valueOf(Shared.HOME) + File.separator; }
/*      */       else { logDir = String.valueOf(Shared.getDesktopDirectory()) + File.separator; }
/*      */        Logger.setup(Invigilator.class, "comas-system", logDir, Shared.LOGGING_LEVEL); ExamDialog.setStatus("9:logger ok"); }
/*      */     catch (IOException e) { ExamDialog.setStatus("9:logger not ok"); }
/*      */      Logger.log(Level.FINE, "", "Initializing"); Logger.log(Level.FINEST, "login.ini ", this.properties.toString()); AbstractFileTask.configure(this.properties); Logger.log(Level.FINE, "Making ", "CoMaS directories"); boolean directoryOk = true; directoryOk = (directoryOk && makeDirectory(Shared.getScreensDirectory(this.course, this.activity))); directoryOk = (directoryOk && makeDirectory(Shared.getLogsDirectory(this.course, this.activity))); directoryOk = (directoryOk && makeDirectory(Shared.getToolsDirectory(this.course, this.activity))); directoryOk = (directoryOk && makeDirectory(Shared.getResourcesDirectory(this.course, this.activity))); if (Shared.AUTO_ARCHIVE)
/*      */       directoryOk = (directoryOk && makeDirectory(Shared.getArchivesDirectory(this.course, this.activity)));  if (!directoryOk) { notifyObservers("Could not create " + Shared.getActivityDirectory(this.course, this.activity) + " subdirectory.\n" + Shared.SUPPORT_MESSAGE); this.logArchiver.put(Level.WARNING, "Startup FAILED(directory creation) (version " + Shared.VERSION + ") for " + this.course + "/" + this.activity + " using java " + this.properties.getProperty("JAVA_VERSION") + " on " + this.properties.getProperty("OS")); Sleeper.sleepAndExit(5000, -2); }
/*      */      Logger.log(Level.FINE, "Completed ", " directory creation"); HTMLFileViewGenerator.create("CoMaS Logs", String.valueOf(logDir) + "comas-system-log.html", String.valueOf(Shared.getToolsDirectory(this.course, this.activity)) + File.separator + "logs.html"); Logger.log(Level.FINE, "Created log viewer in ", String.valueOf(Shared.getToolsDirectory(this.course, this.activity)) + File.separator + "logs.html"); try { createExam(); }
/*      */     catch (IOException e) { this.done = true; Logger.log(Level.SEVERE, "Could not create " + Shared.getExamDirectory(this.course, this.activity) + ": ", e.getMessage()); notifyObservers("Could not create " + Shared.getExamDirectory(this.course, this.activity) + ".\n" + Shared.SUPPORT_MESSAGE); this.logArchiver.put(Level.WARNING, "Startup FAILED(exam creation) (version " + Shared.VERSION + ") for " + this.course + "/" + this.activity + " using java " + this.properties.getProperty("JAVA_VERSION") + " on " + this.properties.getProperty("OS")); Sleeper.sleepAndExit(5000, -3); }
/*      */      createResources(); try { createOStools(); }
/*      */     catch (IOException e)
/*      */     { this.done = true; Logger.log(Level.SEVERE, "Could not create OS tools: ", e.getMessage()); notifyObservers("Could not create OS tools: " + e.getMessage() + ".\n" + Shared.SUPPORT_MESSAGE); this.logArchiver.put(Level.WARNING, "Startup FAILED(OS tools creation) (version " + Shared.VERSION + ") for " + this.course + "/" + this.activity + " using java " + this.properties.getProperty("JAVA_VERSION") + " on " + this.properties.getProperty("OS")); Sleeper.sleepAndExit(5000, -4); }
/*      */      createStudentNotes(); hookRunTime(); ExamDialog.setStatus("13:hook ok"); setupDirectoryWatching(); ExamDialog.setStatus("14:dir ok"); if (Shared.BLUETOOTH_MONITORING)
/*      */       Logger.log(Level.CONFIG, "Bluetooth monitoring is enabled", "");  this.fileResource = new ResourceMonitor("file.txt", "file", Shared.getActivityDirectory(this.course, this.activity), this.properties); this.fileResource.addListener(this); this.fileResource.open(); ExamDialog.setStatus("15:file ok"); this.networkResource = new ResourceMonitor("network.txt", "network", Shared.getActivityDirectory(this.course, this.activity), this.properties); this.networkResource.addListener(this); this.networkResource.open(); ExamDialog.setStatus("16:network ok"); ServletProcessor.getInstance(this.course, this.activity); ExamDialog.setStatus("17:web server ok"); try {
/*      */       ModuleManager mmi = ModuleManager.getInstance(); mmi.addSharedProperty("course", this.course); mmi.addSharedProperty("activity", this.activity); mmi.addSharedProperty("name", this.name); mmi.addSharedProperty("id", this.id); mmi.addSharedProperty("directory", Shared.getBaseDirectory(this.course, this.activity)); mmi.addSharedProperty("host", Shared.CMS_HOST); mmi.addSharedProperty("port", Shared.PORT); mmi.addSharedProperty("protocol", Shared.PROTOCOL); mmi.addSharedProperty("logger", new LoggerModuleBridge(this.logArchiver)); mmi.configure(this.properties);
/*      */     } catch (Exception e1) {
/*      */       this.logArchiver.put(Level.WARNING, "Module configuration exception " + e1);
/*      */     }  setupWebcamDriver(); takeWebcamImage(); ExamDialog.setStatus("18:webcam"); if (Shared.USE_SCREEN_SHOTS)
/*      */       Logger.log(Level.CONFIG, "Screen shots are enabled", "");  if (Shared.USE_WEB_CAM || Shared.USE_WEB_CAM_ON_SCREEN_SHOT)
/*      */       Logger.log(Level.CONFIG, "Web cam is enabled", "");  try {
/*      */       String contents = ClipboardManager.getContents(); ClipboardManager.setContents("Emptied by Invigilator"); this.logArchiver.put(Level.INFO, "CLIPBOARD: " + contents);
/*      */     } catch (Exception exception) {} ExamDialog.setStatus("19:clipboard"); try {
/*      */       Logger.setup(Invigilator.class, "comas-system", Shared.getActivityDirectory(this.course, this.activity), Shared.LOGGING_LEVEL);
/*      */     } catch (IOException e) {
/*      */       Logger.log(Level.WARNING, "Could not create log file in ", Shared.getActivityDirectory(this.course, this.activity));
/*      */     }  this.logArchiver.put(Level.INFO, "Startup successful (version 0.7.5) for " + this.course + "/" + this.activity + " using java version " + this.properties.getProperty("JAVA_VERSION") + " on " + this.properties.getProperty("OS")); ExamDialog.setStatus("20:startup ok"); ExamDialog.instance.setTitleStatus(true); this.actualStartTime = System.currentTimeMillis(); this.properties.setProperty("ACTUAL_START_TIME", this.actualStartTime); this.initHasRun = true; Logger.output("Exam start " + (new Date(this.actualStartTime)).toString()); if (Shared.STARTUP_MESSAGE.length() > 0)
/*      */       JOptionPane.showMessageDialog((Component)ExamDialog.instance, Shared.STARTUP_MESSAGE, "CoMaS Startup", 1, IconLoader.getDefaultIcon());  }
/*      */   private void setupWebcamDriver() { String webcamDriverClass = this.properties.getProperty("webcam.driver"); if (webcamDriverClass == null)
/*      */       return;  try {
/*      */       Class<?> clazz = Class.forName(webcamDriverClass); if (clazz != null)
/*      */         Webcam.setDriver(clazz.getConstructor(new Class[0]).newInstance(new Object[0]));  Logger.log(Level.CONFIG, "Webcam is using: ", webcamDriverClass);
/*      */     } catch (Exception e1) {
/*      */       Logger.log(Level.WARNING, "Webcam driver exception: ", e1);
/*      */     }  }
/*      */   private void createOStools() throws MalformedURLException, IOException { String os = System.getProperty("os.name").toLowerCase(); Logger.log(Level.FINE, "Creating ", String.valueOf(os) + " tools"); if (os.startsWith("win"))
/*      */       Utils.getAndStoreURL(new URL(Shared.HANDLE_EXE), new File(Shared.DOWNLOADS_DIR));  ExamDialog.setStatus("12:OS tools"); Logger.log(Level.FINE, "Created ", String.valueOf(os) + " tools"); }
/*  206 */   public Invigilator(String id, String name, String course, String activity, Properties properties) { this.endedSession = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  387 */     this.initHasRun = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1299 */     this.endPointFailed = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1420 */     this.wakeup = 9223372036854655807L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1468 */     this.screenSizeCheckLogged = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1489 */     this.alpha = 0.9F;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1558 */     this.screenShotProblem = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1790 */     this.archiveProblem = false; if (id == null || name == null || course == null) throw new RuntimeException("Illegal Invigilator parameters");  this.name = name; this.id = id; this.course = course; this.activity = activity; this.state = "Unknown"; this.done = false; this.properties = properties; this.isAllowedToUpload = true; this.sentinel = new KeepAliveSentinel(); this.screenShotArchiver = new UploadArchiver(this, Shared.BASE_VIDEO, "video", Shared.PASSKEY_VIDEO, "Screen upload"); this.logArchiver = new LogArchiver(this, Shared.LOG_URL, "SEVERE", "Logging"); if (Shared.AUTO_ARCHIVE) this.examArchiver = new UploadArchiver(this, Shared.BASE_UPLOAD, "upload", Shared.PASSKEY_FILE_UPLOAD, "Archive upload");  this.sentinel.register((KeepAliveInterface)this.screenShotArchiver); this.sentinel.register((KeepAliveInterface)this.logArchiver); if (Shared.AUTO_ARCHIVE) this.sentinel.register((KeepAliveInterface)this.examArchiver);  this.sentinel.start(); this.screenShotArchiver.start(); this.logArchiver.start(); if (Shared.AUTO_ARCHIVE) this.examArchiver.start();  this.alert = ""; } private void createStudentNotes() { String studentNotesFileName = this.properties.getProperty("STUDENT_NOTES_FILE_NAME"); if (studentNotesFileName != null) { File studentNotes = new File(studentNotesFileName); Logger.log(Level.FINE, "Notes selected ", studentNotes.getAbsoluteFile()); try { FileInputStream fis = new FileInputStream(studentNotes); String resourcesDir = Shared.getResourcesDirectory(this.course, this.activity); if (studentNotesFileName.endsWith(".zip")) { Utils.unpackArchive(fis, new File(resourcesDir)); } else { File dest = new File(String.valueOf(resourcesDir) + File.separator + studentNotes.getName()); Files.copy(fis, dest.toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING }); }  Logger.log(Level.INFO, "Notes chosen were ", studentNotes.getAbsoluteFile() + ", available in " + resourcesDir); } catch (IOException e) { this.logArchiver.put(Level.WARNING, "Could not access notes " + studentNotes.getAbsolutePath()); }  }  } private void createResources() { synchronized (getClass()) { Logger.log(Level.FINE, "Creating ", "resources: " + Shared.getResourcesDirectory(this.course, this.activity)); try { Client client = ClientHelper.createClient(Shared.PROTOCOL); WebTarget webTarget = client.target(Shared.BASE_EXAM).path("deployed").path("resources"); Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" }); invocationBuilder.accept(new String[] { "application/zip" }); Form form = new Form(); form.param("course", this.course); form.param("activity", this.activity); form.param("version", Shared.VERSION); form.param("passkey", Shared.PASSKEY_EXAM); Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded")); InputStream is = (InputStream)response.readEntity(InputStream.class); File zip = Utils.unpackArchive(is, new File(Shared.getResourcesDirectory(this.course, this.activity))); is.close(); Logger.log(Level.INFO, "", "Resources can be found in " + Shared.getResourcesDirectory(this.course, this.activity)); notifyObservers("11:resources ok"); zip.delete(); } catch (Exception e) { Logger.log(Level.INFO, "", "No " + Shared.getResourcesDirectory(this.course, this.activity) + "resources.zip"); File zip = new File(String.valueOf(Shared.getResourcesDirectory(this.course, this.activity)) + "resources.zip"); zip.delete(); zip = new File(String.valueOf(Shared.getResourcesDirectory(this.course, this.activity)) + "arc.zip"); zip.delete(); File resourcesDirectory = new File(Shared.getResourcesDirectory(this.course, this.activity)); File[] files = resourcesDirectory.listFiles(); if (files == null || files.length == 0) resourcesDirectory.delete();  }  ExamDialog.setStatus("11:resources ok"); Logger.log(Level.FINE, "Created ", "resources: " + Shared.getResourcesDirectory(this.course, this.activity)); }  } private void createExam() throws MalformedURLException, IOException { if (this.properties.containsKey("NO_DOWNLOAD_REQUIRED")) { Logger.log(Level.INFO, "", "No download was required, exam directory not overwritten"); return; }  synchronized (getClass()) { Logger.log(Level.FINE, "Processing ", "exam: " + Shared.getExamDirectory(this.course, this.activity)); Client client = ClientHelper.createClient(Shared.PROTOCOL); WebTarget webTarget = client.target(Shared.BASE_EXAM).path("deployed").path("activity"); Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" }); invocationBuilder.accept(new String[] { "application/zip" }); Form form = new Form(); form.param("course", this.course); form.param("activity", this.activity); form.param("version", Shared.VERSION); form.param("passkey", Shared.PASSKEY_EXAM); Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded")); InputStream is = (InputStream)response.readEntity(InputStream.class); File examDirectory = new File(Shared.getExamDirectory(this.course, this.activity)); File zip = Utils.unpackArchive(is, examDirectory); is.close(); ExamDialog.setStatus("10:exam ok"); zip.delete(); if (examDirectory.exists()) { File[] examFiles = examDirectory.listFiles(); if (examFiles == null || examFiles.length == 0) { examDirectory.delete(); } else { Logger.log(Level.INFO, "", "Exam can be found in " + Shared.getExamDirectory(this.course, this.activity)); }  }  Logger.log(Level.FINE, "Processed ", "exam: " + Shared.getExamDirectory(this.course, this.activity)); }  } private boolean makeDirectory(String dirname) { File file = new File(dirname); if (!file.exists()) { if (!file.mkdirs()) { this.done = true; Logger.log(Level.SEVERE, "", "Could not make directory " + dirname); notifyObservers("Could not make directory " + dirname); return false; }  Logger.log(Level.CONFIG, "", "Directory made: " + dirname); } else if (!file.isDirectory()) { this.done = true; Logger.log(Level.SEVERE, "", String.valueOf(dirname) + " is not a directory"); notifyObservers(String.valueOf(dirname) + " is not a directory"); return false; }  return true; } private void setupDirectoryWatching() { Logger.log(Level.FINE, "", "Setting up directory monitoring"); this.fileSystemMonitor = new FileSystemMonitor(this.logArchiver, this.course, this.activity); this.fileSystemMonitor.addListener(this); this.fileSystemMonitor.open(); Logger.log(Level.FINE, "", "Set up directory monitoring"); } private void stop() { Logger.log(Level.INFO, "", "Services stopping"); ModuleManager.getInstance().stop(); if (this.fileSystemMonitor != null) { ExamDialog.setStatus("3:file monitor"); this.fileSystemMonitor.close(); this.fileSystemMonitor = null; ExamDialog.updateProgress("File system monitoring ended", 3); }  if (this.sentinel != null) this.sentinel.stop();  if (this.screenShotArchiver != null) { ExamDialog.setStatus("4:screen shots"); this.screenShotArchiver.stop(); this.screenShotArchiver = null; ExamDialog.updateProgress("Screen shot monitoring ended", 4); }  if (this.examArchiver != null) { ExamDialog.setStatus("5:archiver"); this.examArchiver.stop(); this.examArchiver = null; ExamDialog.updateProgress("Archiving ended", 5); }  if (this.logArchiver != null) { ExamDialog.setStatus("6:logs"); this.logArchiver.stop(); this.logArchiver = null; ExamDialog.updateProgress("Logging ended", 6); }  if (this.fileResource != null) { ExamDialog.setStatus("7:files"); this.fileResource.close(); this.fileResource = null; ExamDialog.updateProgress("Open file monitoring ended", 7); }  if (this.networkResource != null) { ExamDialog.setStatus("8:network"); this.networkResource.close(); this.networkResource = null; ExamDialog.updateProgress("Network monitoring ended", 8); }  if (this.endpoint != null) { ExamDialog.setStatus("9:web"); this.endpoint.close(); this.endpoint = null; ExamDialog.updateProgress("Session control ended", 9); }  Logger.log(Level.INFO, "", "Services stopped"); copyLogFile(); } public static Response activities(String course) { Client client = ClientHelper.createClient(Shared.PROTOCOL); WebTarget webTarget = client.target(Shared.BASE_LOGIN).path("startable-activities"); Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" }); invocationBuilder.accept(new String[] { "application/json" }); Form form = new Form(); form.param("course", course); form.param("version", Shared.VERSION); form.param("passkey", Shared.PASSKEY_DIRECTORY); Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded")); return response; }
/*      */   public static Response activity(String course, String activity, String studentName) { Client client = ClientHelper.createClient(Shared.PROTOCOL); WebTarget webTarget = client.target(Shared.BASE_LOGIN).path("activities").path(activity); Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" }); invocationBuilder.accept(new String[] { "application/json" }); Form form = new Form(); form.param("course", course); form.param("version", Shared.VERSION); form.param("passkey", Shared.PASSKEY_DIRECTORY); form.param("name", studentName); Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded")); return response; }
/*      */   public Response authenticate() { return authenticate(true); }
/* 1793 */   public boolean createAndUploadArchive(boolean upload) { if (!this.initHasRun)
/* 1794 */       return true; 
/* 1795 */     if (!Shared.AUTO_ARCHIVE) {
/* 1796 */       return true;
/*      */     }
/* 1798 */     boolean rtn = true;
/*      */     
/* 1800 */     String canonical_name = Named.canonical(this.name);
/* 1801 */     String archive_name = String.valueOf(canonical_name) + "-" + "exam.zip";
/* 1802 */     String archive_directory = Shared.getArchivesDirectory(this.course, this.activity);
/* 1803 */     File archiveDirectoryFile = new File(archive_directory);
/* 1804 */     if (!archiveDirectoryFile.exists() || !archiveDirectoryFile.isDirectory() || !archiveDirectoryFile.canWrite()) {
/* 1805 */       if (this.logArchiver != null) {
/* 1806 */         this.logArchiver.put(Level.WARNING, "Cannot access or write to archive directory: " + archive_directory);
/*      */       } else {
/* 1808 */         Logger.log(Level.WARNING, "", "Cannot access or write to archive directory: " + archive_directory);
/* 1809 */       }  this.state = "Issue:Archive";
/* 1810 */       this.archiveProblem = true;
/* 1811 */       rtn = false;
/*      */     } 
/*      */     
/* 1814 */     String archive = String.valueOf(archive_directory) + archive_name;
/* 1815 */     File file = new File(archive);
/*      */ 
/*      */ 
/*      */     
/* 1819 */     if (file.exists()) {
/* 1820 */       archive_name = String.valueOf(canonical_name) + "-" + (new Date()).getTime() + "-" + "exam.zip";
/* 1821 */       file.renameTo(new File(String.valueOf(archive_directory) + archive_name));
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1827 */       Zip.pack(Shared.getExamDirectory(this.course, this.activity), archive);
/* 1828 */       Logger.log(Level.INFO, "", "Archive created: " + archive);
/* 1829 */     } catch (IOException e) {
/* 1830 */       this.state = "Issue:Archive";
/* 1831 */       this.archiveProblem = true;
/* 1832 */       ExamDialog.instance.setTitleStatus(false);
/* 1833 */       String msg = "Could not create archive: " + archive + " {" + e.getClass().getName() + "}";
/* 1834 */       if (this.logArchiver != null) {
/* 1835 */         this.logArchiver.put(Level.WARNING, msg);
/*      */       } else {
/* 1837 */         Logger.log(Level.WARNING, "", msg);
/* 1838 */       }  rtn = false;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1843 */     if (rtn && upload) {
/* 1844 */       this.examArchiver.put(archive);
/* 1845 */       notifyObservers("Archived");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1851 */     if (this.archiveProblem && rtn) {
/* 1852 */       ExamDialog.instance.setTitleStatus(true);
/* 1853 */       String msg = "Archive creation is now okay";
/* 1854 */       this.archiveProblem = false;
/* 1855 */       if (this.logArchiver != null) {
/* 1856 */         this.logArchiver.put(Level.INFO, msg);
/*      */       } else {
/* 1858 */         Logger.log(Level.INFO, "", msg);
/*      */       } 
/* 1860 */     }  return rtn; }
/*      */   public Response authenticate(boolean normalAuthentication) { if (normalAuthentication) {
/*      */       Client client = ClientHelper.createClient(Shared.PROTOCOL); WebTarget webTarget = client.target(Shared.BASE_LOGIN).path("authenticate"); Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" }); invocationBuilder.accept(new String[] { "application/json" }); Form form = new Form(); form.param("course", this.course); form.param("activity", this.activity); form.param("version", Shared.VERSION); form.param("passkey", Shared.PASSKEY_DIRECTORY); form.param("name", this.name); form.param("url", String.valueOf(Shared.VIDEO_URL) + "/" + this.course + "/" + this.activity + "/" + this.name); form.param("type", this.state); form.param("description", this.id); Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded")); return response;
/*      */     }  return authenticateByPassCode(); }
/*      */   public Response authenticateByPassCode() { Client client = ClientHelper.createClient(Shared.PROTOCOL); WebTarget webTarget = client.target(Shared.BASE_LOGIN).path("authenticateByPassCode"); Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" }); invocationBuilder.accept(new String[] { "application/json" }); Form form = new Form(); form.param("passcode", ""); form.param("version", Shared.VERSION); form.param("passkey", Shared.PASSKEY_DIRECTORY); form.param("name", this.name); form.param("url", String.valueOf(Shared.VIDEO_URL) + "/" + this.course + "/" + this.activity + "/" + this.name); form.param("type", this.state); form.param("description", this.id); Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded")); System.out.println(webTarget.getUri()); return response; }
/*      */   private String activityKey(String activity, String key) { return String.valueOf(activity) + "-" + key; }
/*      */   public int canStart() { try {
/*      */       Response response = authenticate(); String rtn = (String)response.readEntity(String.class); if (rtn.equalsIgnoreCase("{\"DOES NOT EXIST\"}"))
/*      */         return -1;  if (rtn.equalsIgnoreCase("{\"ILLEGAL VERSION\"}"))
/*      */         return -2;  if (rtn.equalsIgnoreCase("{\"STOPPED\"}"))
/*      */         return -3;  JsonReader reader = Json.createReader(new StringReader(rtn)); JsonObject meAsJson = reader.readObject(); if (meAsJson != null) {
/*      */         String nowAsString = meAsJson.getJsonString(activityKey(this.activity, "ACCESS")).getString(); this.properties.setProperty("CURRENT_TIME", nowAsString); String startAsString = getJsonString(meAsJson, "START_MSECS", "0"); this.properties.setProperty("student.directory.ALLOWED_START_TIME", getJsonString(meAsJson, "START", "1970/01/01 7:00 AM")); String endAsString = getJsonString(meAsJson, "END_MSECS", "9223372036854775807"); this.properties.setProperty("student.directory.ALLOWED_END_TIME", getJsonString(meAsJson, "END", "2099/01/01 7:00 AM")); String durationAsString = getJsonString(meAsJson, activityKey(this.activity, "DURATION"), "10000");
/*      */         this.properties.setProperty("student.directory.ALLOWED_DURATION", durationAsString);
/*      */         long now = Long.parseLong(nowAsString);
/*      */         long start = Long.parseLong(startAsString);
/*      */         long end = Long.parseLong(endAsString);
/*      */         if (now < start)
/*      */           return 0; 
/*      */         if (now > end)
/*      */           return 0; 
/*      */         return 1;
/*      */       } 
/*      */       return 0;
/*      */     } catch (Exception e) {
/*      */       Logger.log(Level.WARNING, "Start exam time check error: ", e.getMessage());
/*      */       return 0;
/* 1886 */     }  } public void resourceEvent(Resource resource, String type, String description) { if (resource instanceof FileSystemMonitor)
/* 1887 */     { if (type.equals("ALERT"))
/* 1888 */       { alert(description);
/* 1889 */         this.logArchiver.put(Level.WARNING, description); }
/* 1890 */       else if (type.equals("CLOSE"))
/* 1891 */       { if (!this.endedSession) {
/* 1892 */           this.state = "Issue:Files";
/* 1893 */           this.logArchiver.put(Level.SEVERE, description);
/* 1894 */           this.fileSystemMonitor.restart();
/*      */         }  }
/*      */       else
/* 1897 */       { this.logArchiver.put(Level.parse(type), description); }  }
/* 1898 */     else if (resource instanceof ResourceMonitor)
/* 1899 */     { this.logArchiver.put((Level)Level.LOGGED, description); }
/* 1900 */     else if (resource instanceof WebsocketClientEndpoint)
/* 1901 */     { if (description.equals("open"))
/*      */       
/* 1903 */       { Logger.log(Level.FINE, "CCI: ", "open"); }
/* 1904 */       else if (description.equals("close") || description.equals("error"))
/* 1905 */       { this.endpoint = null;
/* 1906 */         this.endPointFailed = true;
/* 1907 */         Logger.log(Level.FINE, "CCI: ", description); }  }  }
/*      */   String getJsonString(JsonObject o, String key, String defaultValue) { JsonString jsonString = o.getJsonString(activityKey(this.activity, key)); if (jsonString == null) return defaultValue;  return jsonString.getString(); }
/*      */   public boolean login() { try { Response response = authenticate(); String rtn = (String)response.readEntity(String.class); createRuntimeEnvironment(rtn); createCommandAndControlChannel(); this.isAllowedToUpload = true; if (rtn.equalsIgnoreCase("{\"DOES NOT EXIST\"}")) { this.done = true; this.isAllowedToUpload = false; notifyObservers("You are not registered. Please quit now."); } else if (rtn.equalsIgnoreCase("{\"ILLEGAL VERSION\"}")) { this.done = true; this.logArchiver.put(Level.SEVERE, "Illegal version detected (" + Shared.VERSION + ")"); this.isAllowedToUpload = false; notifyObservers("Illegal version detected (" + Shared.VERSION + ").\n" + Shared.SUPPORT_MESSAGE); System.exit(0); } else if (rtn.equalsIgnoreCase("{\"STOPPED\"}")) { this.done = true; Logger.log(Level.INFO, "", "Session is over, please wait for archive upload"); notifyObservers("Session is over, please wait for archive upload"); System.exit(0); } else { Logger.log(Level.FINE, "[" + response.getStatus() + "] ", "Login"); }  boolean status = (response.getStatus() < 204); if (!this.done) if (status) { notifyObservers("Logged in"); } else { notifyObservers("Not logged in (" + this.failures + ")"); }   return status; } catch (Exception e) { Logger.log(Level.INFO, "", "Could not login: " + e.getMessage()); notifyObservers("Not logged in (" + this.failures + ")"); return false; }  }
/*      */   private void createRuntimeEnvironment(String json) { synchronized (getClass()) { if (this.me == null && this.state.equals("Login")) try { File zip = Utils.unpackArchive(new URL(Shared.TOOLS_ZIP), new File(Shared.getToolsDirectory(this.course, this.activity))); Logger.log(Level.INFO, "", "Tools can be found in " + Shared.getToolsDirectory(this.course, this.activity)); notifyObservers("Tools ok"); zip.delete(); JsonReader reader = Json.createReader(new StringReader(json)); JsonObject meAsJson = reader.readObject(); this.me = new HashMap<>(); reader.close(); for (Map.Entry<String, JsonValue> entry : (Iterable<Map.Entry<String, JsonValue>>)meAsJson.entrySet()) { if (!((String)entry.getKey()).contains("-")) { String key = entry.getKey(); String value = ((JsonValue)entry.getValue()).toString(); this.me.put(key, value); this.properties.setProperty("student.directory." + key, Named.unquoted(value)); }  }  Logger.debug(Level.INFO, this.properties.toString()); this.me.put("directory_host", Named.quoted(Shared.DIRECTORY_HOST)); this.me.put("log_host", Named.quoted(Shared.LOG_HOST)); this.me.put("upload_host", Named.quoted(Shared.UPLOAD_HOST)); this.me.put("video_host", Named.quoted(Shared.VIDEO_HOST)); this.me.put("cms_host", Named.quoted(Shared.CMS_HOST)); this.me.put("protocol", Named.quoted(Shared.PROTOCOL)); this.me.put("ws_protocol", Named.quoted(Shared.WS_PROTOCOL)); this.me.put("port", Shared.PORT); this.me.put("course", Named.quoted(this.course)); this.me.put("activity", Named.quoted(this.activity)); try { File tools_dir = new File(Shared.getToolsDirectory(this.course, this.activity)); File[] files = tools_dir.listFiles(); byte b; int i; File[] arrayOfFile1; for (i = (arrayOfFile1 = files).length, b = 0; b < i; ) { File file = arrayOfFile1[b]; Logger.log(Level.CONFIG, "", String.valueOf(file.getName()) + " in " + Shared.getToolsDirectory(this.course, this.activity)); CustomResourceCreator.generate(file, this.me.entrySet(), tools_dir); b++; }  BrowserTask bt = new BrowserTask(tools_dir, this.properties); bt.start(); } catch (IOException e) { Logger.log(Level.WARNING, "", "Could not process a tool directory file: " + e.getMessage()); }  } catch (Exception e) { Logger.log(Level.WARNING, "", "Could not process " + Shared.TOOLS_ZIP); notifyObservers("No tools"); }   }  }
/*      */   private void createCommandAndControlChannel() { synchronized (getClass()) { if (this.endpoint == null && this.me != null) try { Logger.log(Level.FINE, "", "Creating CCI"); String sUri = Shared.service(Shared.WS_PROTOCOL, Shared.WEBSOCKET_HOST, Shared.PORT, "/WebSocket/channel/" + this.course + "/" + this.activity + "/"); String endpointName = Named.unquoted(this.me.get("ID")); sUri = String.valueOf(sUri) + Named.canonical(this.name) + '-' + endpointName + "/" + Named.unquoted(this.properties.getProperty("student.directory.PASSWORD")); this.endpoint = new WebsocketClientEndpoint(new URI(sUri)); this.endpoint.addListener(this); this.endpoint.addMessageHandler("stop", (MessageHandler)new StopMessageHandler()); this.endpoint.addMessageHandler("unload", (MessageHandler)new UnloadMessageHandler()); this.endpoint.addMessageHandler("load", (MessageHandler)new LoadMessageHandler()); this.endpoint.addMessageHandler("url", (MessageHandler)new URLMessageHandler()); this.endpoint.addMessageHandler("alert", (MessageHandler)new AlertMessageHandler()); this.endpoint.addMessageHandler("ping", (MessageHandler)new PingMessageHandler()); this.endpoint.addMessageHandler("level", (MessageHandler)new LogMessageHandler()); this.endpoint.addMessageHandler("screenShot", (MessageHandler)new ScreenShotFrequencyMessageHandler()); configureExtendedProtocolHandlers(); this.endpoint.open(); this.endPointFailed = false; } catch (Exception e) { this.endpoint = null; this.endPointFailed = true; Logger.log(Level.WARNING, "CCI: ", e.getMessage()); }   }  }
/*      */   private void configureExtendedProtocolHandlers() { if (this.endpoint != null) { int i = 1; String urlProp = this.properties.getProperty("protocol.handler.url"); if (urlProp == null) { Logger.log(Level.FINE, "No CCI protocol handlers loaded", ""); return; }  if (urlProp.startsWith("/")) urlProp = Shared.service(Shared.PROTOCOL, Shared.CMS_HOST, Shared.PORT, urlProp);  ModuleClassLoader classLoader = new ModuleClassLoader(new URL[0], MessageHandler.class.getClassLoader()); try { classLoader.addURL(new URL(urlProp)); String handlerString = this.properties.getProperty("protocol.handler." + i); while (handlerString != null) { String[] tokens = handlerString.split(","); if (tokens != null) { if (tokens.length == 2) { String type = tokens[0].toLowerCase().trim(); String messageHandlerClass = tokens[1].trim(); try { Class<?> clazz = Class.forName(messageHandlerClass, true, (ClassLoader)classLoader); this.endpoint.addMessageHandler(type, clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0])); Logger.log(Level.INFO, String.format("CCI protocol handler loaded for %s type from %s", new Object[] { type, messageHandlerClass }), ""); } catch (Exception e) { Logger.log(Level.WARNING, String.format("CCI protocol handler creation error for %s: ", new Object[] { messageHandlerClass }), e); }  } else { Logger.log(Level.WARNING, "CCI protocol handler had too few (<2) tokens: ", handlerString); }  } else { Logger.log(Level.WARNING, "No CCI protocol handler defined for: ", handlerString); }  i++; handlerString = this.properties.getProperty("protocol.handler." + i); }  } catch (MalformedURLException urlException) { Logger.log(Level.WARNING, String.format("protocol.handler.url has an illegal URL format: %s", new Object[] { urlProp }), ""); } finally { try { classLoader.close(); } catch (IOException iOException) {} }  }  }
/*      */   public Thread runExam() { this.thread = new Thread(new Runnable() {
/*      */           public void run() { Invigilator.this.init(); Invigilator.this.monitorUser(); Logger.log(Level.INFO, "", "Session has ended"); }
/*      */         }); this.thread.start(); return this.thread; }
/*      */   private void monitorUser() { int i = 0; while (continueSession()) { ping(); checkScreenSize(); takeScreenShot(); try { long timeToSleep = (1000 * randomInt(Shared.MIN_INTERVAL, this.current)); this.wakeup = System.currentTimeMillis() + timeToSleep; Thread.sleep(timeToSleep); } catch (InterruptedException interruptedException) {} if (++i % Shared.AUTO_ARCHIVE_FREQUENCY == 0 && Shared.AUTO_ARCHIVE) createAndUploadArchive(true);  }  }
/*      */   public synchronized int randomInt(int bottom, int top) { return bottom + this.r.nextInt(top); }
/*      */   private void checkScreenSize() { if (!this.screenSizeCheckLogged && !VMDetector.isStdResolution()) { this.screenSizeCheckLogged = true; this.logArchiver.put(Level.WARNING, "Non-standard screen size detected, possible virtual machine?"); }  } private boolean continueSession() { boolean okay = login(); if (!okay) { if (this.failures == 0) { this.failureStartTime = System.currentTimeMillis(); String failureMessage = String.format("Login failed %.02f seconds into session", new Object[] { Double.valueOf((this.failureStartTime - this.actualStartTime) / 1000.0D) }); if (this.logArchiver != null) { this.logArchiver.put(Level.WARNING, failureMessage); } else { Logger.log(Level.WARNING, failureMessage, ""); }  }  this.failures++; this.current = (int)(this.current * this.alpha); if (this.current < Shared.MIN_INTERVAL) this.current = Shared.MIN_INTERVAL;  if (this.failures % Shared.ALERT_FAILURE_FREQUENCY == 0) alert("Failed to login " + this.failures + " times.\n" + Shared.SUPPORT_MESSAGE);  } else { if (this.failures > 0) { this.state = "Login (" + this.failures + ")"; long failureInterval = (System.currentTimeMillis() - this.failureStartTime) / 1000L; String failureMessage = String.format("Login successful after %d failures in %d seconds", new Object[] { Integer.valueOf(this.failures), Long.valueOf(failureInterval) }); if (this.logArchiver != null) { this.logArchiver.put(Level.INFO, failureMessage); } else { Logger.log(Level.INFO, "", failureMessage); }  } else { this.state = "Login"; }  this.failures = 0; this.current = (int)(this.current / this.alpha); if (this.current > Shared.MAX_INTERVAL)
/* 1919 */         this.current = Shared.MAX_INTERVAL;  }  return (this.failures < Shared.MAX_SESSION_FAILURES && !this.done); } public void alert(String description) { this.alert = description;
/* 1920 */     notifyObservers(); } private void ping() { if (this.endpoint != null) try { this.endpoint.sendMessage(new Message()); } catch (IOException|javax.websocket.EncodeException iOException) {}  }
/*      */   public Webcam getWebcam() { if (this.webcam == null) this.webcam = Webcam.getDefault();  return this.webcam; }
/*      */   private BufferedImage getQRCode(String barcodeText) throws WriterException { QRCodeWriter barcodeWriter = new QRCodeWriter(); BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 50, 50); return MatrixToImageWriter.toBufferedImage(bitMatrix); }
/*      */   private void takeScreenShot() { if (!Shared.USE_SCREEN_SHOTS) return;  try { String screensDirString = Shared.getScreensDirectory(this.course, this.activity); File screensDir = new File(screensDirString); if (screensDir.exists() && screensDir.isDirectory() && screensDir.canWrite()) { BufferedImage[] screenFullImage = ScreenShotCreator.getImages(); if (System.currentTimeMillis() > this.wakeup + Math.max(60000, Shared.MAX_INTERVAL * 1000)) { this.logArchiver.log(Level.INFO, String.format("Machine was possibly suspended (%d)", new Object[] { Long.valueOf(this.wakeup) })); try { if (this.webcam != null) this.webcam.close();  } catch (Exception exception) {} }  long timeInMsec = (new Date()).getTime(); for (int indexOfScreenShot = 0; indexOfScreenShot < screenFullImage.length; indexOfScreenShot++) { BufferedImage webcamImage = null; if (Shared.USE_WEB_CAM_ON_SCREEN_SHOT) { if (this.webcam == null) this.webcam = Webcam.getDefault();  if (this.webcam != null) { if (!this.webcam.isOpen()) { Dimension[] sizes = this.webcam.getViewSizes(); byte b; int i; Dimension[] arrayOfDimension1; for (i = (arrayOfDimension1 = sizes).length, b = 0; b < i; ) { Dimension size = arrayOfDimension1[b]; if (size.getWidth() * 1.0D / screenFullImage[indexOfScreenShot].getWidth() > 0.05D) { this.webcam.setViewSize(size); break; }  b++; }  this.webcam.open(); }  if (this.webcam.isOpen()) { webcamImage = this.webcam.getImage(); if (webcamImage != null) { if (screenFullImage != null) { Graphics g = screenFullImage[indexOfScreenShot].getGraphics(); g.drawImage(webcamImage, 0, 0, null); g.dispose(); }  } else { this.logArchiver.put(Level.WARNING, "Cannot acquire a webcam image"); }  }  } else { this.logArchiver.put(Level.WARNING, "Cannot find a webcam"); }  }  if (screenFullImage[indexOfScreenShot] != null) { if (this.qrCode == null && Shared.SCREEN_SHOT_QR_CODE_REQUIRED) try { String qrLabel, host = this.properties.getProperty("LOCAL_ADDRESS"); String macAddress = MacAddress.getMACAddresses(); if (host != null && macAddress != null) { qrLabel = String.format("%s/%s/%s/%s/%s/%s", new Object[] { this.course, this.activity, this.name, this.id, host, macAddress }); } else { qrLabel = String.format("%s/%s/%s/%s/%s", new Object[] { this.course, this.activity, this.name, this.id, host }); }  this.qrCode = getQRCode(qrLabel); } catch (Exception exception) {}  Graphics g = screenFullImage[indexOfScreenShot].getGraphics(); if (this.qrCode != null) g.drawImage(this.qrCode, screenFullImage[indexOfScreenShot].getWidth() - this.qrCode.getWidth(), 0, null);  if (Shared.SCREEN_SHOT_TIMESTAMP_REQUIRED) { int x = Math.round(screenFullImage[indexOfScreenShot].getWidth() * Shared.SCREEN_SHOT_TIMESTAMP_WIDTH) - 150; if (x < 0) x = 0;  if (x > screenFullImage[indexOfScreenShot].getWidth() - 320)
/*      */                 x = screenFullImage[indexOfScreenShot].getWidth() - 320;  int y = Math.round(screenFullImage[indexOfScreenShot].getHeight() * Shared.SCREEN_SHOT_TIMESTAMP_HEIGHT); if (y > screenFullImage[indexOfScreenShot].getHeight() - 50)
/*      */                 y = screenFullImage[indexOfScreenShot].getHeight() - 50;  g.clearRect(x, y, 320, 50); g.setFont(g.getFont().deriveFont(20.0F)); if (this.state.equals("Login")) { g.setColor(Color.white); } else if (this.state.contains("Issue:")) { g.setColor(Color.ORANGE); }  if (this.endPointFailed)
/*      */                 g.setColor(Color.RED);  g.drawString((new Date()).toString(), x + 10, y + 25); }  g.dispose(); String fileName = String.valueOf(screensDirString) + this.name + "-" + (timeInMsec + indexOfScreenShot) + "." + "jpg"; ImageIO.write(screenFullImage[indexOfScreenShot], "jpg", new File(fileName)); this.screenShotArchiver.put(fileName); this.state = "Login"; if (this.screenShotProblem) { this.screenShotProblem = false; ExamDialog.instance.setTitleStatus(true); String msg = "Screen shot saving is now okay"; if (this.logArchiver != null) { this.logArchiver.put(Level.INFO, msg); } else { Logger.log(Level.INFO, "", msg); }  }  } else { throw new IOException("Full screen image cannot be acquired"); }  }  } else { throw new IOException(String.valueOf(screensDirString) + " cannot be accessed"); }  } catch (Exception e) { String msg; Throwable reason = e.getCause(); if (reason != null) { boolean stopWebCam = false; if (reason instanceof com.github.sarxos.webcam.WebcamException)
/*      */           stopWebCam = true;  reason = reason.getCause(); if ((reason != null && reason instanceof com.github.sarxos.webcam.WebcamException) || e.getMessage().contains("execute task"))
/*      */           stopWebCam = true;  if (stopWebCam && !Shared.WEB_CAM_MANDATORY) { Shared.USE_WEB_CAM_ON_SCREEN_SHOT = false; this.logArchiver.put(Level.INFO, String.valueOf(e.getCause().toString()) + ", turned off web cam"); }  }  this.screenShotProblem = true; ExamDialog.instance.setTitleStatus(false); this.state = "Issue:Screen"; if (e.getMessage() != null) { msg = "Cannot save screen: " + e.getMessage(); } else { msg = "Cannot save screen: " + e; }  if (this.logArchiver != null) { this.logArchiver.put(Level.WARNING, msg); } else { Logger.log(Level.WARNING, "", msg); }  }  }
/*      */   private void takeWebcamImage() { if (Shared.USE_WEB_CAM) { Shared.USE_WEB_CAM_ON_SCREEN_SHOT = true; takeScreenShot(); Shared.USE_WEB_CAM_ON_SCREEN_SHOT = false; }  }
/* 1930 */   public void notifyObservers(Object arg) { setChanged();
/* 1931 */     super.notifyObservers(arg); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void copyLogFile() {
/*      */     try {
/* 1943 */       Path pathToLogFile = Paths.get(Shared.DIR, new String[] { this.course, this.activity, "logs", "comas-system-log.html" });
/* 1944 */       File logFile = pathToLogFile.toFile();
/* 1945 */       if (logFile.exists()) {
/* 1946 */         String savedName = "comas-system-" + System.currentTimeMillis() + "-log.html";
/* 1947 */         logFile.renameTo(Paths.get(Shared.DIR, new String[] { this.course, this.activity, "logs", savedName }).toFile());
/*      */       } 
/*      */       
/* 1950 */       String logDir = Shared.HOME;
/* 1951 */       if (Shared.isWriteableDirectory(logDir)) {
/* 1952 */         logDir = Shared.HOME;
/*      */       } else {
/* 1954 */         logDir = Shared.getDesktopDirectory();
/* 1955 */       }  Files.copy(Paths.get(logDir, new String[] { "comas-system-log.html" }), pathToLogFile, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
/* 1956 */     } catch (IOException e) {
/* 1957 */       Logger.log(Level.WARNING, "", "Could not copy log file");
/*      */     } 
/*      */   }
/*      */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\exam\Invigilator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */