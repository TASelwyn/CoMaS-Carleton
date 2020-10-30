/*      */ package edu.carleton.cas.ui;
/*      */ 
/*      */ import edu.carleton.cas.constants.Shared;
/*      */ import edu.carleton.cas.exam.Invigilator;
/*      */ import edu.carleton.cas.file.DirectoryUtils;
/*      */ import edu.carleton.cas.file.Utils;
/*      */ import edu.carleton.cas.logging.Logger;
/*      */ import edu.carleton.cas.resources.Resource;
/*      */ import edu.carleton.cas.resources.ResourceListener;
/*      */ import edu.carleton.cas.resources.VMCheck;
/*      */ import edu.carleton.cas.security.Checksum;
/*      */ import edu.carleton.cas.security.IllegalVersionException;
/*      */ import edu.carleton.cas.utility.DriveSpace;
/*      */ import edu.carleton.cas.utility.IconLoader;
/*      */ import edu.carleton.cas.utility.Named;
/*      */ import edu.carleton.cas.utility.Observable;
/*      */ import edu.carleton.cas.utility.Observer;
/*      */ import edu.carleton.cas.utility.Password;
/*      */ import edu.carleton.cas.utility.VMDetector;
/*      */ import edu.carleton.services.ServiceBrowser;
/*      */ import edu.carleton.services.ServiceListener;
/*      */ import edu.carleton.services.ServiceLocator;
/*      */ import java.awt.Component;
/*      */ import java.awt.Font;
/*      */ import java.awt.GridLayout;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.WindowAdapter;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.StringReader;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.text.NumberFormat;
/*      */ import java.text.ParseException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.Date;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Timer;
/*      */ import java.util.TimerTask;
/*      */ import java.util.concurrent.CountDownLatch;
/*      */ import java.util.logging.Level;
/*      */ import javax.bluetooth.LocalDevice;
/*      */ import javax.json.Json;
/*      */ import javax.json.JsonArray;
/*      */ import javax.json.JsonObject;
/*      */ import javax.json.JsonReader;
/*      */ import javax.json.JsonValue;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JFileChooser;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.ProgressMonitor;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.filechooser.FileNameExtensionFilter;
/*      */ import javax.swing.plaf.FontUIResource;
/*      */ import javax.ws.rs.core.Response;
/*      */ import org.simplericity.macify.eawt.ApplicationEvent;
/*      */ import org.simplericity.macify.eawt.ApplicationListener;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ExamDialog
/*      */   extends JFrame
/*      */   implements Observer
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*      */   private static final String COMAS_ALERT = "CoMaS Alert!";
/*      */   public static ExamDialog instance;
/*   95 */   private WindowAdapter windowAdapter = null;
/*   96 */   public ExamApplicationListener examlistener = null;
/*      */   
/*      */   public JTextField studentID;
/*      */   
/*      */   public JTextField firstName;
/*      */   public JTextField lastName;
/*      */   public JButton loginButton;
/*      */   public JTextField status;
/*      */   public JTextField message;
/*      */   public Invigilator login;
/*  106 */   public Timer timer = null;
/*      */   
/*      */   public int versionOfJava;
/*      */   
/*      */   public String course;
/*      */   public String activity;
/*      */   private Properties config;
/*  113 */   private Icon icon = null; private Thread prepareThread;
/*      */   
/*      */   public ExamDialog(String name, final String[] args) {
/*  116 */     super(name);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  121 */     this.versionOfJava = checkVersionOfJava(false);
/*      */ 
/*      */ 
/*      */     
/*  125 */     processCommandLineArgs(args);
/*      */ 
/*      */     
/*  128 */     this.icon = IconLoader.getDefaultIcon();
/*      */     
/*  130 */     lookForServices();
/*      */     
/*  132 */     this.course = askForCourse();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  139 */     Shared.setupURLs(this.course);
/*  140 */     this.config = Shared.getLoginProperties(this.course);
/*      */     
/*  142 */     initFrame();
/*      */     
/*  144 */     this.studentID = new JTextField();
/*  145 */     this.firstName = new JTextField();
/*  146 */     this.lastName = new JTextField();
/*  147 */     this.loginButton = new JButton("Start");
/*  148 */     this.status = new JTextField();
/*  149 */     this.message = new JTextField();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  154 */     GridLayout layout = new GridLayout(0, 2);
/*  155 */     JPanel panel = new JPanel();
/*  156 */     panel.setLayout(layout);
/*  157 */     JLabel label = new JLabel(" First name: ");
/*  158 */     panel.add(label);
/*  159 */     panel.add(this.firstName);
/*  160 */     label = new JLabel(" Last name: ");
/*  161 */     panel.add(label);
/*  162 */     panel.add(this.lastName);
/*  163 */     label = new JLabel(" Student ID: ");
/*  164 */     panel.add(label);
/*  165 */     panel.add(this.studentID);
/*  166 */     label = new JLabel(" Status: ");
/*  167 */     panel.add(label);
/*  168 */     panel.add(this.status);
/*  169 */     panel.add(this.loginButton);
/*  170 */     panel.add(this.message);
/*      */     
/*  172 */     getContentPane().add(panel);
/*  173 */     setTitle("CoMaS");
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  178 */     if (Shared.STUDENT_ID != null) {
/*  179 */       this.studentID.setText(Shared.STUDENT_ID);
/*  180 */       this.studentID.setEditable(false);
/*      */     } 
/*  182 */     if (Shared.STUDENT_FIRST_NAME != null) {
/*  183 */       this.firstName.setText(Shared.STUDENT_FIRST_NAME);
/*  184 */       this.firstName.setEditable(false);
/*      */     } 
/*  186 */     if (Shared.STUDENT_LAST_NAME != null) {
/*  187 */       this.lastName.setText(Shared.STUDENT_LAST_NAME);
/*  188 */       this.lastName.setEditable(false);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  194 */     this.status.setEditable(false);
/*  195 */     this.message.setEditable(false);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  203 */     SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss a zzz");
/*  204 */     String time = ft.format(new Date());
/*  205 */     this.message.setText(time);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  213 */     this.loginButton.addActionListener(new ActionListener() {
/*      */           public void actionPerformed(ActionEvent e) {
/*  215 */             String first = ExamDialog.this.firstName.getText().trim();
/*  216 */             String last = ExamDialog.this.lastName.getText().trim();
/*  217 */             final String name = (String.valueOf(first) + " " + last).toLowerCase();
/*  218 */             if (ExamDialog.this.loginButton.getText().equals("Start")) {
/*  219 */               if (ExamDialog.this.isIdOkay(ExamDialog.this.studentID.getText()) && ExamDialog.this.isNameOkay(first, last)) {
/*  220 */                 Thread loginProtocol = new Thread(new Runnable()
/*      */                     {
/*      */                       public void run()
/*      */                       {
/*      */                         boolean hasVMtechnology;
/*      */                         
/*  226 */                         if (Shared.AUTO_ARCHIVE)
/*  227 */                           (ExamDialog.null.access$0(ExamDialog.null.this)).loginButton.setText("Upload"); 
/*  228 */                         (ExamDialog.null.access$0(ExamDialog.null.this)).loginButton.setEnabled(false);
/*  229 */                         ExamDialog.setStatus("Initializing ...");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                         
/*  235 */                         (ExamDialog.null.access$0(ExamDialog.null.this)).message.setText("");
/*  236 */                         (ExamDialog.null.access$0(ExamDialog.null.this)).firstName.setEditable(false);
/*  237 */                         (ExamDialog.null.access$0(ExamDialog.null.this)).lastName.setEditable(false);
/*  238 */                         (ExamDialog.null.access$0(ExamDialog.null.this)).studentID.setEditable(false);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                         
/*  250 */                         ExamDialog.null.access$0(ExamDialog.null.this).runVMCheck((ExamDialog.null.access$0(ExamDialog.null.this)).config);
/*      */ 
/*      */ 
/*      */                         
/*  254 */                         ExamDialog.null.access$0(ExamDialog.null.this).checkVersionOfJava(true);
/*      */ 
/*      */                         
/*  257 */                         (ExamDialog.null.access$0(ExamDialog.null.this)).config.setProperty("JAVA_VERSION", String.valueOf((ExamDialog.null.access$0(ExamDialog.null.this)).versionOfJava));
/*  258 */                         (ExamDialog.null.access$0(ExamDialog.null.this)).config.setProperty("OS", System.getProperty("os.name"));
/*      */ 
/*      */                         
/*  261 */                         ExamDialog.null.access$0(ExamDialog.null.this).checkDiskSpace();
/*      */ 
/*      */                         
/*  264 */                         ExamDialog.null.access$0(ExamDialog.null.this).processCommandLineArgs(args);
/*  265 */                         ExamDialog.setStatus("0:config ok");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                         
/*  271 */                         ExamDialog.setStatus("1:activity ...");
/*  272 */                         (ExamDialog.null.access$0(ExamDialog.null.this)).activity = ExamDialog.null.access$0(ExamDialog.null.this).askForActivity((ExamDialog.null.access$0(ExamDialog.null.this)).course);
/*  273 */                         ExamDialog.setStatus("1:" + (ExamDialog.null.access$0(ExamDialog.null.this)).activity);
/*  274 */                         ExamDialog.null.access$0(ExamDialog.null.this).getActivity((ExamDialog.null.access$0(ExamDialog.null.this)).course, (ExamDialog.null.access$0(ExamDialog.null.this)).activity, name);
/*  275 */                         ExamDialog.null.access$0(ExamDialog.null.this).checkActivityPassCodeOrExit();
/*      */ 
/*      */ 
/*      */ 
/*      */                         
/*  280 */                         ExamDialog.null.access$0(ExamDialog.null.this).setTitle(String.valueOf((ExamDialog.null.access$0(ExamDialog.null.this)).course) + "/" + (ExamDialog.null.access$0(ExamDialog.null.this)).activity);
/*      */ 
/*      */ 
/*      */                         
/*      */                         try {
/*  285 */                           hasVMtechnology = VMDetector.isVMMac();
/*  286 */                         } catch (HeadlessException|java.net.SocketException e2) {
/*  287 */                           hasVMtechnology = false;
/*      */                         } 
/*      */ 
/*      */ 
/*      */                         
/*  292 */                         ExamDialog.null.access$0(ExamDialog.null.this).overwriteExistingActivityDirectoryOrConfirmNoDownload();
/*      */ 
/*      */ 
/*      */                         
/*  296 */                         ExamDialog.null.access$0(ExamDialog.null.this).getFileWithKnownHashFromStudent();
/*      */                         
/*  298 */                         (ExamDialog.null.access$0(ExamDialog.null.this)).login = new Invigilator((ExamDialog.null.access$0(ExamDialog.null.this)).studentID.getText(), name, (ExamDialog.null.access$0(ExamDialog.null.this)).course, (ExamDialog.null.access$0(ExamDialog.null.this)).activity, (ExamDialog.null.access$0(ExamDialog.null.this)).config);
/*  299 */                         (ExamDialog.null.access$0(ExamDialog.null.this)).login.addObserver(ExamDialog.null.access$0(ExamDialog.null.this));
/*      */ 
/*      */ 
/*      */ 
/*      */                         
/*  304 */                         if (hasVMtechnology) {
/*  305 */                           (ExamDialog.null.access$0(ExamDialog.null.this)).login.logArchiver.put(Level.WARNING, "Virtual machine technology detected");
/*      */                         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                         
/*  315 */                         if (Shared.BLUETOOTH_MONITORING) {
/*  316 */                           Thread BTthread = new Thread(new Runnable() {
/*      */                                 public void run() {
/*  318 */                                   ExamDialog.setStatus("0:BT check");
/*  319 */                                   if (LocalDevice.isPowerOn()) {
/*  320 */                                     (ExamDialog.null.access$0(ExamDialog.null.null.access$0(ExamDialog.null.null.this))).login.logArchiver.log(Level.WARNING, "Bluetooth power on detected");
/*  321 */                                     (ExamDialog.null.access$0(ExamDialog.null.null.access$0(ExamDialog.null.null.this))).login.alert("Please switch Bluetooth off!");
/*      */                                   } 
/*      */                                 }
/*      */                               });
/*  325 */                           BTthread.start();
/*      */                         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                         
/*  332 */                         ExamDialog.setStatus("3:ip check");
/*  333 */                         if (!(ExamDialog.null.access$0(ExamDialog.null.this)).login.canRunAtThisIPAddress()) {
/*  334 */                           JOptionPane.showMessageDialog(ExamDialog.null.access$0(ExamDialog.null.this), 
/*  335 */                               "Session can't be run at this address", "CoMaS Alert!", 
/*  336 */                               0, IconLoader.getIcon(0));
/*  337 */                           System.exit(-1);
/*      */                         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                         
/*  345 */                         if (ExamDialog.null.access$0(ExamDialog.null.this).isWebcamRequired() && 
/*  346 */                           (ExamDialog.null.access$0(ExamDialog.null.this)).login.getWebcam() == null) {
/*  347 */                           JOptionPane.showMessageDialog(ExamDialog.null.access$0(ExamDialog.null.this), 
/*  348 */                               "Webcam required but one cannot be accessed", "CoMaS Alert!", 
/*  349 */                               0, 
/*  350 */                               IconLoader.getIcon(0));
/*  351 */                           System.exit(-1);
/*      */                         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                         
/*  358 */                         if ((ExamDialog.null.access$0(ExamDialog.null.this)).login.isSane()) {
/*  359 */                           ExamDialog.null.access$0(ExamDialog.null.this).prepareToRunExam();
/*      */                         } else {
/*  361 */                           JOptionPane.showMessageDialog(ExamDialog.null.access$0(ExamDialog.null.this), "Session can't be run", "CoMaS Alert!", 
/*  362 */                               0, IconLoader.getIcon(0));
/*  363 */                           System.exit(-1);
/*      */                         } 
/*      */ 
/*      */ 
/*      */                         
/*  368 */                         if (Shared.AUTO_ARCHIVE) {
/*  369 */                           (ExamDialog.null.access$0(ExamDialog.null.this)).timer = new Timer();
/*  370 */                           (ExamDialog.null.access$0(ExamDialog.null.this)).timer.schedule(new ExamDialog.ExamTask(null), Shared.MIN_MSECS_BETWEEN_USER_UPLOADS);
/*      */                         } 
/*      */                       }
/*      */                     });
/*  374 */                 loginProtocol.start();
/*      */               } 
/*      */             } else {
/*  377 */               SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss a zzz");
/*  378 */               String time = ft.format(new Date());
/*      */ 
/*      */               
/*  381 */               ExamDialog.this.loginButton.setEnabled(false);
/*  382 */               if (ExamDialog.this.login.createAndUploadArchive(true)) {
/*  383 */                 ExamDialog.this.message.setText(time);
/*  384 */                 ExamDialog.this.timer.schedule(new ExamDialog.ExamTask(null), Shared.MIN_MSECS_BETWEEN_USER_UPLOADS);
/*      */               } else {
/*      */                 
/*  387 */                 ExamDialog.this.loginButton.setEnabled(true);
/*  388 */                 JOptionPane.showMessageDialog(ExamDialog.this, "Archive upload failed at " + time, "CoMaS Alert!", 
/*  389 */                     0, IconLoader.getIcon(0));
/*      */               } 
/*      */             } 
/*      */           }
/*      */         });
/*  394 */     instance = this;
/*      */   }
/*      */   
/*      */   public void setTitleStatus(boolean okay) {
/*  398 */     if (okay) {
/*  399 */       setTitle(String.valueOf(this.course) + "/" + this.activity + " ✅");
/*      */     } else {
/*  401 */       setTitle(String.valueOf(this.course) + "/" + this.activity + " ❌");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkActivityPassCodeOrExit() {
/*  410 */     if (!Shared.USE_ACTIVITY_CODES)
/*      */       return; 
/*  412 */     String saltAsString = this.config.getProperty("SALT");
/*  413 */     String passcode = this.config.getProperty("PASSCODE");
/*  414 */     if (saltAsString == null || passcode == null)
/*      */       return; 
/*  416 */     byte[] salt = Password.stringToByte(saltAsString);
/*      */     
/*  418 */     String studentPassCode = null;
/*  419 */     while (studentPassCode == null) {
/*  420 */       studentPassCode = (String)JOptionPane.showInputDialog(this, 
/*  421 */           "Enter " + this.activity + " passcode or press Cancel to exit", "Enter " + this.activity + " code", 
/*  422 */           -1, IconLoader.getIcon(-1), null, null);
/*  423 */       if (studentPassCode == null)
/*  424 */         System.exit(-6); 
/*  425 */       String securePassCode = Password.getSecurePassword(studentPassCode, salt);
/*  426 */       if (securePassCode.equals(passcode)) {
/*      */         return;
/*      */       }
/*  429 */       studentPassCode = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void processCommandLineArgs(String[] args) {
/*  445 */     System.setProperty("java.locale.providers", "COMPAT,CLDR");
/*      */ 
/*      */     
/*  448 */     for (int i = 0; i < args.length; i++) {
/*  449 */       if (args[i].equals("-find")) {
/*  450 */         Shared.LOOK_FOR_SERVICES = true;
/*  451 */       } else if (args[i].equals("-comas")) {
/*  452 */         if (i + 1 < args.length) {
/*  453 */           Shared.DIR = args[i + 1].trim();
/*  454 */           Logger.output("CoMaS DIRECTORY = " + Shared.DIR);
/*      */         } 
/*  456 */       } else if (args[i].equals("-downloads")) {
/*  457 */         if (i + 1 < args.length) {
/*  458 */           Shared.DOWNLOADS_DIR = args[i + 1].trim();
/*  459 */           Logger.output("Downloads DIRECTORY = " + Shared.DOWNLOADS_DIR);
/*      */         } 
/*  461 */       } else if (args[i].equals("-logging")) {
/*  462 */         if (i + 1 < args.length) {
/*      */           try {
/*  464 */             Shared.LOGGING_LEVEL = Level.parse(args[i + 1].trim());
/*  465 */           } catch (Exception e) {
/*  466 */             Shared.LOGGING_LEVEL = Level.INFO;
/*      */           } 
/*  468 */           Logger.output("Logging level = " + Shared.LOGGING_LEVEL);
/*      */         } 
/*  470 */       } else if (args[i].equals("-course")) {
/*  471 */         if (i + 1 < args.length) {
/*  472 */           Shared.STUDENT_COURSE = args[i + 1].trim();
/*  473 */           Logger.output("Course = " + Shared.STUDENT_COURSE);
/*      */         } 
/*  475 */       } else if (args[i].equals("-activity")) {
/*  476 */         if (i + 1 < args.length) {
/*  477 */           Shared.STUDENT_ACTIVITY = args[i + 1].trim();
/*  478 */           Logger.output("Activity = " + Shared.STUDENT_ACTIVITY);
/*      */         } 
/*  480 */       } else if (args[i].equals("-name")) {
/*  481 */         if (i + 2 < args.length) {
/*  482 */           Shared.STUDENT_FIRST_NAME = args[i + 1].trim().toLowerCase();
/*  483 */           Shared.STUDENT_LAST_NAME = args[i + 2].trim().toLowerCase();
/*  484 */           Logger.output("Name = " + Shared.STUDENT_FIRST_NAME + " " + Shared.STUDENT_LAST_NAME);
/*      */         } 
/*  486 */       } else if (args[i].equals("-id")) {
/*  487 */         if (i + 1 < args.length) {
/*  488 */           Shared.STUDENT_ID = args[i + 1].trim();
/*  489 */           Logger.output("ID = " + Shared.STUDENT_ID);
/*      */         } 
/*  491 */       } else if (args[i].equals("-server") && 
/*  492 */         i + 1 < args.length) {
/*  493 */         Shared.SERVER_CHOSEN = args[i + 1].trim();
/*  494 */         Logger.output("Server = " + Shared.SERVER_CHOSEN);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void prepareToRunExam() {
/*  510 */     this.prepareThread = new Thread(new Runnable() {
/*      */           public void run() {
/*  512 */             int times = 0;
/*  513 */             Logger.output("Preparing to run session");
/*  514 */             while (!ExamDialog.this.login.isExamOk()) {
/*  515 */               ExamDialog.this.doWait(++times);
/*      */             }
/*      */             
/*  518 */             Logger.output("Activity is deployed");
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  523 */             boolean timeCheck = false;
/*  524 */             while (!timeCheck) {
/*  525 */               int rtnCode = ExamDialog.this.login.canStart();
/*  526 */               if (rtnCode < 0) {
/*  527 */                 String msg = String.format(
/*  528 */                     "This session cannot continue, server return code: %d.\n" + Shared.SUPPORT_MESSAGE, new Object[] {
/*  529 */                       Integer.valueOf(rtnCode) });
/*  530 */                 JOptionPane.showMessageDialog(null, msg, "CoMaS Alert!", 0, 
/*  531 */                     IconLoader.getIcon(0));
/*  532 */                 Logger.output(msg);
/*      */               } 
/*  534 */               timeCheck = (rtnCode > 0);
/*  535 */               if (timeCheck)
/*  536 */                 timeCheck = ExamDialog.this.isActivityStartable(); 
/*  537 */               if (!timeCheck)
/*  538 */                 ExamDialog.this.doWait(++times); 
/*      */             } 
/*  540 */             Logger.output("Session can start");
/*  541 */             ExamDialog.this.login.runExam();
/*      */           }
/*      */         });
/*  544 */     this.prepareThread.start();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void doWait(int times) {
/*  553 */     SwingUtilities.invokeLater(new WaitNotification(times));
/*      */     try {
/*  555 */       Thread.sleep(Shared.RETRY_TIME);
/*  556 */     } catch (InterruptedException interruptedException) {}
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static ExamDialog getInstance() {
/*  562 */     return instance;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void initFrame() {
/*  572 */     if (isAlreadyRunning()) {
/*  573 */       JOptionPane.showMessageDialog(this, "Session is already running", "CoMaS Alert!", 2, 
/*  574 */           IconLoader.getIcon(2));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  580 */     setFonts();
/*  581 */     setSize(250, 200);
/*  582 */     this.examlistener = new ExamApplicationListener();
/*  583 */     this.windowAdapter = new WindowAdapter()
/*      */       {
/*      */         public void windowClosing(WindowEvent e)
/*      */         {
/*  587 */           super.windowClosing(e);
/*      */           
/*  589 */           int res = JOptionPane.showConfirmDialog(ExamDialog.this, "Are you sure you want to end session?", 
/*  590 */               "End CoMaS session?", 0, 2, 
/*  591 */               IconLoader.getIcon(2));
/*  592 */           if (res == 0) {
/*  593 */             Thread t = new Thread() {
/*      */                 public void run() {
/*  595 */                   if ((ExamDialog.null.access$0(ExamDialog.null.this)).login != null)
/*  596 */                     (ExamDialog.null.access$0(ExamDialog.null.this)).login.endTheSession(); 
/*  597 */                   ExamDialog.null.access$0(ExamDialog.null.this).dispose();
/*  598 */                   System.exit(0);
/*      */                 }
/*      */               };
/*  601 */             t.start();
/*      */           } 
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*      */         public void windowClosed(WindowEvent e) {
/*  608 */           super.windowClosed(e);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  614 */           if (ExamDialog.this.timer != null)
/*  615 */             ExamDialog.this.timer.cancel(); 
/*  616 */           System.exit(0);
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */     
/*  622 */     setDefaultCloseOperation(0);
/*      */     
/*  624 */     addWindowListener(this.windowAdapter);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void overwriteExistingActivityDirectoryOrConfirmNoDownload() {
/*  642 */     String activityDirectory = Shared.getActivityDirectory(this.course, this.activity);
/*  643 */     File archives = new File(activityDirectory);
/*  644 */     boolean exists = archives.exists();
/*  645 */     if (!isResumable()) {
/*  646 */       if (exists) {
/*  647 */         int res = JOptionPane.showConfirmDialog(this, 
/*  648 */             "Are you sure you want to overwrite " + activityDirectory + "?", "Overwrite activity?", 
/*  649 */             0, 2, 
/*  650 */             IconLoader.getIcon(2));
/*  651 */         if (res != 0) {
/*  652 */           System.exit(0);
/*      */         } else {
/*      */           try {
/*  655 */             DirectoryUtils.destroyDirectory(activityDirectory);
/*  656 */           } catch (IOException e1) {
/*  657 */             JOptionPane.showMessageDialog(this, "Failed to delete " + activityDirectory + ". Exiting ...", 
/*  658 */                 "CoMaS Alert!", 0, IconLoader.getIcon(0));
/*  659 */             System.exit(-5);
/*      */           }
/*      */         
/*      */         }
/*      */       
/*      */       } 
/*  665 */     } else if (exists) {
/*  666 */       File[] archiveFiles = archives.listFiles();
/*  667 */       if (archiveFiles != null && archiveFiles.length > 0) {
/*  668 */         int res = JOptionPane.showConfirmDialog(this, 
/*  669 */             "Do you want to download a new copy of the activity?\nThis will overwrite your work.", 
/*  670 */             "New activity download?", 0, 2, 
/*  671 */             IconLoader.getIcon(2));
/*  672 */         if (res == 1) {
/*  673 */           Logger.output("No download required");
/*  674 */           this.config.setProperty("NO_DOWNLOAD_REQUIRED", "true");
/*      */         } else {
/*  676 */           Logger.output("Overwriting " + activityDirectory + File.separator + "exam");
/*  677 */           this.config.remove("NO_DOWNLOAD_REQUIRED");
/*      */           try {
/*  679 */             DirectoryUtils.destroyDirectory(String.valueOf(activityDirectory) + File.separator + "exam");
/*  680 */           } catch (IOException e1) {
/*  681 */             JOptionPane.showMessageDialog(this, 
/*  682 */                 "Failed to delete " + activityDirectory + File.separator + "exam" + ". Exiting ...", 
/*  683 */                 "CoMaS Alert!", 0, 
/*  684 */                 IconLoader.getIcon(0));
/*  685 */             System.exit(-5);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String askForCourse() {
/*  708 */     String course = null;
/*  709 */     if (Shared.STUDENT_COURSE == null) {
/*  710 */       ServerChooser server = new ServerChooser(this);
/*  711 */       server.open();
/*  712 */       course = chooseCourse(server.select());
/*  713 */       server.close();
/*      */     } else {
/*  715 */       course = Shared.STUDENT_COURSE;
/*  716 */       if (Shared.SERVER_CHOSEN != null) {
/*  717 */         chooseCourse(Shared.SERVER_CHOSEN);
/*      */       }
/*      */     } 
/*      */     
/*  721 */     if (course == null)
/*  722 */       System.exit(-1); 
/*  723 */     return course;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String chooseCourse(String host) {
/*  735 */     if (host == null) {
/*  736 */       return null;
/*      */     }
/*  738 */     String course = null;
/*  739 */     Properties configs = null;
/*  740 */     Shared.EXAM_CONFIGURATION_FILE = Shared.examDotIni(host);
/*      */     
/*  742 */     configs = Utils.getProperties(Shared.EXAM_CONFIGURATION_FILE);
/*  743 */     if (configs == null || configs.isEmpty())
/*  744 */       configs = Utils.getProperties(Shared.LOCAL_EXAM_CONFIGURATION_FILE); 
/*  745 */     Logger.output("Server is " + host);
/*  746 */     Logger.output("Configuration is " + configs);
/*      */     
/*  748 */     if (Shared.SERVER_CHOSEN == null || Shared.STUDENT_COURSE == null) {
/*  749 */       if (configs != null && !configs.isEmpty())
/*      */       
/*  751 */       { Shared.CONFIGS = configs;
/*  752 */         String courses = configs.getProperty("courses");
/*  753 */         if (courses != null) {
/*  754 */           String[] possibilities = courses.split(",");
/*      */           
/*  756 */           if (possibilities.length == 0) {
/*  757 */             noCourseError(host);
/*  758 */           } else if (possibilities.length == 1) {
/*  759 */             course = possibilities[0].trim();
/*  760 */             if (course.length() == 0) {
/*  761 */               noCourseError(host);
/*  762 */               course = null;
/*      */             } 
/*  764 */           } else if (possibilities.length > 1) {
/*  765 */             for (int i = 0; i < possibilities.length; i++) {
/*  766 */               possibilities[i] = possibilities[i].trim();
/*      */             }
/*  768 */             course = (String)JOptionPane.showInputDialog(this, "Please choose course:", 
/*  769 */                 "Course Choice Dialog", -1, this.icon, (Object[])possibilities, 
/*  770 */                 possibilities[0]);
/*      */           } 
/*      */         } else {
/*  773 */           noCourseError(host);
/*      */         }  }
/*  775 */       else { noCourseError(host); }
/*  776 */        return course;
/*      */     } 
/*  778 */     return Shared.STUDENT_COURSE;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void noCourseError(String host) {
/*  788 */     JOptionPane.showMessageDialog(this, 
/*  789 */         String.format("No courses are currently defined on %s.\n" + Shared.SUPPORT_MESSAGE, new Object[] { host
/*  790 */           }), "CoMaS Alert!", 0, IconLoader.getIcon(0));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void getActivity(String course, String activity, String name) {
/*  801 */     Response response = Invigilator.activity(course, activity, name);
/*  802 */     String activitiesAsJson = (String)response.readEntity(String.class);
/*      */ 
/*      */     
/*      */     try {
/*  806 */       if (activitiesAsJson.equals("{\"ILLEGAL VERSION\"}")) {
/*  807 */         throw new IllegalVersionException(Shared.VERSION);
/*      */       }
/*  809 */       JsonReader reader = Json.createReader(new StringReader(activitiesAsJson));
/*  810 */       JsonObject activityAsJson = reader.readObject();
/*  811 */       for (Map.Entry<String, JsonValue> entry : (Iterable<Map.Entry<String, JsonValue>>)activityAsJson.entrySet()) {
/*  812 */         String key = Named.unquoted(((String)entry.getKey()).toString());
/*  813 */         String value = Named.unquoted(((JsonValue)entry.getValue()).toString());
/*  814 */         if (!key.equals("DESCRIPTION"))
/*  815 */           this.config.setProperty(key, value); 
/*      */       } 
/*  817 */     } catch (IllegalVersionException e1) {
/*  818 */       JOptionPane.showMessageDialog(this, 
/*  819 */           "Illegal version detected (" + e1.getMessage() + ").\nPlease quit and download new version", 
/*  820 */           "CoMaS Alert!", 0, IconLoader.getIcon(0));
/*  821 */     } catch (Exception exception) {}
/*      */ 
/*      */     
/*  824 */     Logger.output("Session Configuration:");
/*  825 */     Logger.output(this.config.toString());
/*      */   }
/*      */   
/*      */   private boolean isWebcamRequired() {
/*  829 */     return (Shared.WEB_CAM_MANDATORY && (Shared.USE_WEB_CAM || Shared.USE_WEB_CAM_ON_SCREEN_SHOT));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isResumable() {
/*  842 */     String value = this.config.getProperty("RESUMABLE", "no");
/*  843 */     if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"))
/*  844 */       return true; 
/*  845 */     if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no"))
/*  846 */       return false; 
/*  847 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void getFileWithKnownHashFromStudent() {
/*  860 */     final String checksumOfKnownFile = this.config.getProperty("STUDENT_NOTES_FILE_CHECKSUM");
/*  861 */     if (checksumOfKnownFile != null) {
/*  862 */       final CountDownLatch countDownLatch = new CountDownLatch(1);
/*  863 */       SwingUtilities.invokeLater(new Runnable()
/*      */           {
/*      */             public void run() {
/*  866 */               JFileChooser jfc = new JFileChooser();
/*      */               
/*  868 */               jfc.setDialogTitle("Select an authorized archive");
/*  869 */               jfc.setAcceptAllFileFilterUsed(false);
/*  870 */               FileNameExtensionFilter filter = new FileNameExtensionFilter("ZIP or PDF files", new String[] { "zip", "pdf" });
/*  871 */               jfc.addChoosableFileFilter(filter);
/*      */               
/*      */               while (true) {
/*  874 */                 int returnValue = jfc.showOpenDialog(ExamDialog.instance);
/*  875 */                 if (returnValue == 0) {
/*  876 */                   File selectedFile = jfc.getSelectedFile();
/*      */                   try {
/*  878 */                     String checksum = Checksum.getSHA256Checksum(selectedFile.getAbsolutePath());
/*      */                     
/*  880 */                     if (checksumOfKnownFile.equals(checksum)) {
/*  881 */                       Logger.log(Level.INFO, "Notes file ", 
/*  882 */                           String.valueOf(selectedFile.getAbsolutePath()) + " SHA-256: (" + checksum + ")");
/*  883 */                       ExamDialog.this.config.setProperty("STUDENT_NOTES_FILE_NAME", selectedFile.getAbsolutePath());
/*      */                     } else {
/*  885 */                       JOptionPane.showMessageDialog(ExamDialog.instance, 
/*  886 */                           "This file was not the one authorized: " + selectedFile.getAbsolutePath(), 
/*  887 */                           "CoMaS Alert!", 2, 
/*  888 */                           IconLoader.getIcon(2));
/*  889 */                       returnValue = -1;
/*      */                     } 
/*  891 */                   } catch (Exception e) {
/*  892 */                     Logger.log(Level.WARNING, "Checksum failure for ", selectedFile.getAbsolutePath());
/*      */                   }
/*      */                 
/*  895 */                 } else if (returnValue == 1) {
/*  896 */                   countDownLatch.countDown();
/*      */                   return;
/*      */                 } 
/*  899 */                 if (returnValue == 0) {
/*  900 */                   countDownLatch.countDown();
/*      */                   return;
/*      */                 } 
/*      */               } 
/*      */             }
/*      */           });
/*      */       try {
/*  907 */         countDownLatch.await();
/*  908 */       } catch (InterruptedException interruptedException) {}
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isRunnableInVM() {
/*  919 */     String value = this.config.getProperty("vm_enabled");
/*  920 */     if (value == null)
/*  921 */       value = this.config.getProperty("virtual_machine", "yes"); 
/*  922 */     value = value.trim();
/*  923 */     if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"))
/*  924 */       return true; 
/*  925 */     if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no"))
/*  926 */       return false; 
/*  927 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void runVMCheck(Properties properties) {
/*  936 */     Logger.output("Running VM check");
/*  937 */     VMCheck vmCheck = new VMCheck(properties);
/*  938 */     vmCheck.open();
/*  939 */     vmCheck.addListener(new ResourceListener()
/*      */         {
/*      */           public void resourceEvent(Resource resource, String type, String description)
/*      */           {
/*  943 */             if (type.equals("exception") && !ExamDialog.this.isRunnableInVM()) {
/*  944 */               ExamDialog.this.toFront();
/*  945 */               ExamDialog.this.repaint();
/*  946 */               JOptionPane.showMessageDialog(ExamDialog.this, 
/*  947 */                   "Unable to run virtual machine detection check.\n" + Shared.SUPPORT_MESSAGE, "CoMaS Alert!", 
/*  948 */                   0, IconLoader.getIcon(0));
/*  949 */               System.exit(-1);
/*      */             } else {
/*      */               
/*  952 */               Logger.log(Level.WARNING, "Session running in a virtual machine (", String.valueOf(description.trim()) + ")");
/*  953 */               if (!ExamDialog.this.isRunnableInVM()) {
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  958 */                 ExamDialog.this.toFront();
/*  959 */                 ExamDialog.this.repaint();
/*  960 */                 JOptionPane.showMessageDialog(ExamDialog.this, "Session can't run in a virtual machine", 
/*  961 */                     "CoMaS Alert!", 0, IconLoader.getIcon(0));
/*  962 */                 System.exit(-1);
/*      */               } 
/*      */             } 
/*      */           }
/*      */         });
/*  967 */     setStatus("2:vm check");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isActivityStartable() {
/* 1057 */     return isActivityStartableUsingTimeInMsecs();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isActivityStartableUsingTimeInMsecs() {
/* 1068 */     boolean rtn = false;
/* 1069 */     if (this.config.containsKey("START_MSECS") && this.config.containsKey("END_MSECS") && 
/* 1070 */       this.config.containsKey("CURRENT_TIME")) {
/*      */       
/* 1072 */       String startAsString = this.config.getProperty("START_MSECS");
/* 1073 */       String endAsString = this.config.getProperty("END_MSECS");
/* 1074 */       String nowAsString = this.config.getProperty("CURRENT_TIME");
/*      */       
/* 1076 */       long start = Long.parseLong(startAsString);
/* 1077 */       long end = Long.parseLong(endAsString);
/* 1078 */       long now = Long.parseLong(nowAsString);
/*      */       
/* 1080 */       rtn = !(start >= now && now <= end);
/*      */     } 
/*      */     
/* 1083 */     return rtn;
/*      */   }
/*      */   
/* 1086 */   static SimpleDateFormat df_alt = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
/* 1087 */   static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm a"); private ServiceLocator serviceLocator;
/*      */   
/*      */   public static Date getDate(String dateAsString, String nowAsString) {
/*      */     Date aDate;
/*      */     try {
/* 1092 */       aDate = df.parse(dateAsString);
/* 1093 */     } catch (ParseException e) {
/*      */       try {
/* 1095 */         aDate = df_alt.parse(dateAsString);
/* 1096 */       } catch (ParseException e1) {
/* 1097 */         aDate = new Date(Long.parseLong(nowAsString));
/*      */       } 
/*      */     } 
/* 1100 */     return aDate;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ServiceBrowser serviceBrowser;
/*      */ 
/*      */ 
/*      */   
/*      */   private static ProgressMonitor pm;
/*      */ 
/*      */ 
/*      */   
/*      */   private String askForActivity(String course) {
/* 1115 */     String activity = null;
/* 1116 */     if (Shared.STUDENT_ACTIVITY == null) {
/* 1117 */       String[] possibilities = null;
/*      */       
/*      */       try {
/* 1120 */         Response response = Invigilator.activities(course);
/* 1121 */         String activitiesAsJson = (String)response.readEntity(String.class);
/* 1122 */         if (activitiesAsJson.equals("{\"ILLEGAL VERSION\"}"))
/* 1123 */           throw new IllegalVersionException(Shared.VERSION); 
/* 1124 */         Logger.output("Activities:");
/* 1125 */         Logger.output(activitiesAsJson);
/* 1126 */         JsonReader reader = Json.createReader(new StringReader(activitiesAsJson));
/* 1127 */         JsonArray meAsJson = reader.readArray();
/* 1128 */         possibilities = new String[meAsJson.size()];
/* 1129 */         for (int i = 0; i < meAsJson.size(); i++) {
/* 1130 */           possibilities[i] = Named.unquoted(meAsJson.getJsonString(i).toString());
/*      */         }
/* 1132 */       } catch (IllegalVersionException e1) {
/* 1133 */         JOptionPane.showMessageDialog(this, 
/* 1134 */             "Illegal version detected (" + e1.getMessage() + ").\nPlease quit and download new version", 
/* 1135 */             "CoMaS Alert!", 0, IconLoader.getIcon(0));
/* 1136 */       } catch (Exception e) {
/* 1137 */         Logger.debug(Level.WARNING, "No activities: " + e.getMessage());
/* 1138 */         JOptionPane.showMessageDialog(this, "The list of activities for " + course + " cannot be obtained", 
/* 1139 */             "CoMaS Alert!", 0, IconLoader.getIcon(0));
/* 1140 */         System.exit(-1);
/*      */       } 
/* 1142 */       if (possibilities != null)
/* 1143 */       { if (possibilities.length == 0) {
/* 1144 */           Logger.debug(Level.WARNING, "No activities defined for " + course);
/* 1145 */           JOptionPane.showMessageDialog(this, "No activities available for " + course, "CoMaS Alert!", 
/* 1146 */               0, IconLoader.getIcon(0));
/* 1147 */           System.exit(-1);
/* 1148 */         } else if (possibilities.length == 1) {
/* 1149 */           activity = possibilities[0];
/* 1150 */         } else if (possibilities.length > 1) {
/* 1151 */           while (activity == null) {
/* 1152 */             activity = (String)JOptionPane.showInputDialog(this, "Please choose activity:", 
/* 1153 */                 "Activity Choice Dialog", -1, this.icon, (Object[])possibilities, 
/* 1154 */                 possibilities[0]);
/* 1155 */             if (activity == null)
/* 1156 */               System.exit(-1); 
/*      */           } 
/*      */         } else {
/* 1159 */           activity = "default";
/*      */         }  }
/* 1161 */       else { activity = "default"; }
/*      */     
/*      */     } else {
/* 1164 */       activity = Shared.STUDENT_ACTIVITY;
/* 1165 */     }  return activity;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setFonts() {
/* 1172 */     UIManager.put("Button.font", new FontUIResource(new Font("Helvetica", 1, 16)));
/* 1173 */     UIManager.put("TextField.font", new FontUIResource(new Font("Helvetica", 1, 16)));
/* 1174 */     UIManager.put("Label.font", new FontUIResource(new Font("Helvetica", 1, 16)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isIdOkay(String id) {
/* 1181 */     boolean rtn = true;
/*      */     
/* 1183 */     String checker = this.config.getProperty("student.id.pattern");
/* 1184 */     String description = this.config.getProperty("student.id.description");
/* 1185 */     if (checker != null && 
/* 1186 */       !this.studentID.getText().matches(checker)) {
/* 1187 */       if (description == null)
/* 1188 */         description = "Student ID must conform to this pattern:\n" + checker; 
/* 1189 */       JOptionPane.showMessageDialog(this, description, "CoMaS Alert!", 2, 
/* 1190 */           IconLoader.getIcon(2));
/* 1191 */       rtn = false;
/*      */     } 
/*      */ 
/*      */     
/* 1195 */     if (checker != null) {
/* 1196 */       Logger.output("Student ID check: " + rtn);
/* 1197 */       return rtn;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1204 */     if (id.length() != 9) {
/* 1205 */       JOptionPane.showMessageDialog(this, "Student ID should be 1XXXXXXXX (9 characters)", "CoMaS Alert!", 
/* 1206 */           2, IconLoader.getIcon(2));
/* 1207 */       rtn = false;
/*      */     } 
/*      */     
/* 1210 */     if (id.length() > 1 && !id.startsWith("10")) {
/* 1211 */       JOptionPane.showMessageDialog(this, "Student ID should start with a \"10\"", "CoMaS Alert!", 
/* 1212 */           2, IconLoader.getIcon(2));
/* 1213 */       rtn = false;
/*      */     } 
/*      */     
/* 1216 */     if (id.length() > 2 && id.charAt(2) != '1' && id.charAt(2) != '0') {
/* 1217 */       JOptionPane.showMessageDialog(this, "Student ID should hava a '1' or a '0' as the 3rd character", 
/* 1218 */           "CoMaS Alert!", 2, IconLoader.getIcon(2));
/* 1219 */       rtn = false;
/*      */     } 
/* 1221 */     for (int i = 0; i < id.length(); i++) {
/* 1222 */       char c = id.charAt(i);
/* 1223 */       if (c < '0' || c > '9') {
/* 1224 */         JOptionPane.showMessageDialog(this, "Illegal character '" + c + "' in student ID", "CoMaS Alert!", 
/* 1225 */             2, IconLoader.getIcon(2));
/* 1226 */         rtn = false;
/*      */       } 
/*      */     } 
/* 1229 */     return rtn;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isNameOkay(String first, String last) {
/* 1236 */     boolean rtn = true;
/* 1237 */     String checkerFirst = this.config.getProperty("student.name.first.pattern");
/* 1238 */     String description = this.config.getProperty("student.name.first.description");
/* 1239 */     if (checkerFirst != null && 
/* 1240 */       !first.matches(checkerFirst)) {
/* 1241 */       if (description == null)
/* 1242 */         description = "Student first name must conform to this pattern:\n" + checkerFirst; 
/* 1243 */       JOptionPane.showMessageDialog(this, description, "CoMaS Alert!", 2, 
/* 1244 */           IconLoader.getIcon(2));
/* 1245 */       rtn = false;
/*      */     } 
/*      */     
/* 1248 */     String checkerLast = this.config.getProperty("student.name.last.pattern");
/* 1249 */     description = this.config.getProperty("student.name.last.description");
/* 1250 */     if (checkerLast != null && 
/* 1251 */       !last.matches(checkerLast)) {
/* 1252 */       if (description == null)
/* 1253 */         description = "Student last name must confirm to this pattern:\n" + checkerLast; 
/* 1254 */       JOptionPane.showMessageDialog(this, description, "CoMaS Alert!", 2, 
/* 1255 */           IconLoader.getIcon(2));
/* 1256 */       rtn = false;
/*      */     } 
/*      */ 
/*      */     
/* 1260 */     if (checkerFirst != null || checkerLast != null) {
/* 1261 */       Logger.output("Student name check: " + rtn);
/* 1262 */       return rtn;
/*      */     } 
/* 1264 */     return (isNameStringOkay("First", first) && isNameStringOkay("Last", last));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isNameStringOkay(String type, String name) {
/* 1274 */     boolean rtn = true;
/* 1275 */     if (name.length() == 0) {
/* 1276 */       JOptionPane.showMessageDialog(this, String.valueOf(type) + " name must be entered", "CoMaS Alert!", 
/* 1277 */           2, IconLoader.getIcon(2));
/* 1278 */       rtn = false;
/*      */     } 
/* 1280 */     if (name.length() > 32) {
/* 1281 */       JOptionPane.showMessageDialog(this, String.valueOf(type) + " name length must be less than 32 characters", "CoMaS Alert!", 
/* 1282 */           2, IconLoader.getIcon(2));
/* 1283 */       rtn = false;
/*      */     } 
/*      */     
/* 1286 */     for (int i = 0; i < name.length(); i++) {
/* 1287 */       char c = name.charAt(i);
/* 1288 */       if (c >= '0' && c <= '9') {
/* 1289 */         JOptionPane.showMessageDialog(this, String.valueOf(type) + " name cannot contain a number", "CoMaS Alert!", 
/* 1290 */             2, IconLoader.getIcon(2));
/* 1291 */         rtn = false;
/*      */       } 
/*      */     } 
/* 1294 */     return rtn;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void lookForServices() {
/* 1310 */     if (Shared.LOOK_FOR_SERVICES) {
/* 1311 */       Logger.log(Level.CONFIG, "Looking for CoMaS services", "");
/*      */       
/* 1313 */       ServiceListener listener = new ServiceListener()
/*      */         {
/*      */           public void onRemove(String arg0, String arg1)
/*      */           {
/* 1317 */             if (arg0.equals("*")) {
/* 1318 */               Logger.log(Level.FINE, "Unsubscribed from all services", "");
/* 1319 */             } else if (arg0 != null && arg1 != null) {
/* 1320 */               Logger.log(Level.CONFIG, String.valueOf(arg0) + " service unpublished ", arg1);
/*      */             } 
/*      */           }
/*      */ 
/*      */           
/*      */           public void onAdd(String arg0, String arg1) {
/* 1326 */             if (arg1 != null) {
/* 1327 */               Logger.log(Level.CONFIG, String.valueOf(arg0) + " service added ", arg1);
/*      */             } else {
/* 1329 */               Logger.log(Level.CONFIG, String.valueOf(arg0) + " service added ", " with unknown endpoint");
/*      */             } 
/*      */             try {
/* 1332 */               URL url = new URL(arg1);
/* 1333 */               if (arg0.contains("EDIR")) {
/* 1334 */                 Shared.DIRECTORY_HOST = url.getHost();
/* 1335 */                 Shared.PROTOCOL = url.getProtocol();
/* 1336 */                 Shared.PORT = String.valueOf(url.getPort());
/* 1337 */               } else if (arg0.contains("ELOG")) {
/* 1338 */                 Shared.LOG_HOST = url.getHost();
/* 1339 */                 Shared.PROTOCOL = url.getProtocol();
/* 1340 */                 Shared.PORT = String.valueOf(url.getPort());
/* 1341 */               } else if (arg0.contains("EVID")) {
/* 1342 */                 Shared.VIDEO_HOST = url.getHost();
/* 1343 */                 Shared.PROTOCOL = url.getProtocol();
/* 1344 */                 Shared.PORT = String.valueOf(url.getPort());
/* 1345 */               } else if (arg0.contains("EFUP")) {
/* 1346 */                 Shared.UPLOAD_HOST = url.getHost();
/* 1347 */                 Shared.PROTOCOL = url.getProtocol();
/* 1348 */                 Shared.PORT = String.valueOf(url.getPort());
/* 1349 */               } else if (arg0.contains("ECMS")) {
/* 1350 */                 Shared.CMS_HOST = url.getHost();
/* 1351 */                 Shared.PROTOCOL = url.getProtocol();
/* 1352 */                 Shared.PORT = String.valueOf(url.getPort());
/* 1353 */               } else if (arg0.contains("EXAM")) {
/* 1354 */                 Shared.EXAM_HOST = url.getHost();
/* 1355 */                 Shared.PROTOCOL = url.getProtocol();
/* 1356 */                 Shared.PORT = String.valueOf(url.getPort());
/*      */               } 
/* 1358 */               Shared.updateURLs();
/* 1359 */             } catch (MalformedURLException e) {
/* 1360 */               Logger.log(Level.CONFIG, "Illegal CoMaS service URL found: ", e.getMessage());
/*      */             } 
/*      */           }
/*      */         };
/*      */ 
/*      */       
/*      */       try {
/* 1367 */         this.serviceLocator = new ServiceLocator(listener);
/* 1368 */         this.serviceBrowser = new ServiceBrowser(listener);
/*      */         
/* 1370 */         this.serviceLocator.open();
/*      */         
/* 1372 */         this.serviceLocator.subscribe("EDIR");
/* 1373 */         this.serviceLocator.subscribe("EVID");
/* 1374 */         this.serviceLocator.subscribe("EFUP");
/* 1375 */         this.serviceLocator.subscribe("EXAM");
/* 1376 */         this.serviceLocator.subscribe("ELOG");
/* 1377 */         this.serviceLocator.subscribe("ECMS");
/* 1378 */       } catch (Exception e1) {
/* 1379 */         Logger.log(Level.WARNING, "Could not find CoMaS services using service location: ", e1.getMessage());
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkDiskSpace() {
/* 1390 */     double free = DriveSpace.free();
/* 1391 */     if (free < Shared.MIN_DRIVE_SPACE_THRESHOLD_PERCENTAGE) {
/* 1392 */       NumberFormat nf = NumberFormat.getNumberInstance();
/* 1393 */       JOptionPane.showMessageDialog(this, 
/* 1394 */           "Warning! Low drive space (" + nf.format(free) + "%), " + 
/* 1395 */           Shared.MIN_DRIVE_SPACE_THRESHOLD_PERCENTAGE + "% suggested.", 
/* 1396 */           "CoMaS Alert!", 2, IconLoader.getIcon(2));
/*      */     } 
/* 1398 */     free = DriveSpace.freeMB();
/* 1399 */     if (free < Shared.MIN_DRIVE_SPACE_THRESHOLD_MB) {
/* 1400 */       NumberFormat nf = NumberFormat.getNumberInstance();
/* 1401 */       JOptionPane.showMessageDialog(this, 
/* 1402 */           "Warning! Low drive space (" + nf.format(free) + "MB), " + Shared.MIN_DRIVE_SPACE_THRESHOLD_MB + 
/* 1403 */           "MB suggested.", 
/* 1404 */           "CoMaS Alert!", 2, IconLoader.getIcon(2));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int checkVersionOfJava(boolean debug) {
/*      */     int versionFound;
/* 1415 */     String version = "unknown";
/* 1416 */     String javaVersion = System.getProperty("java.version");
/* 1417 */     if (javaVersion == null) {
/* 1418 */       javaVersion = System.getProperty("java.runtime.version");
/*      */     }
/* 1420 */     boolean javaIsOk = false;
/* 1421 */     if (javaVersion != null) {
/* 1422 */       if (javaVersion.startsWith("1.8")) {
/* 1423 */         javaIsOk = version8Check(javaVersion, debug);
/* 1424 */         if (javaIsOk)
/* 1425 */         { version = "8"; }
/*      */         else
/* 1427 */         { version = javaVersion; } 
/* 1428 */       } else if (javaVersion.startsWith("9")) {
/* 1429 */         javaIsOk = true;
/* 1430 */         version = "9";
/* 1431 */       } else if (javaVersion.startsWith("10")) {
/* 1432 */         javaIsOk = true;
/* 1433 */         version = "10";
/* 1434 */       } else if (javaVersion.startsWith("11")) {
/* 1435 */         javaIsOk = true;
/* 1436 */         version = "11";
/* 1437 */       } else if (javaVersion.startsWith("12")) {
/* 1438 */         javaIsOk = true;
/* 1439 */         version = "12";
/* 1440 */       } else if (javaVersion.startsWith("13")) {
/* 1441 */         javaIsOk = true;
/* 1442 */         version = "13";
/*      */       } else {
/* 1444 */         javaIsOk = false;
/* 1445 */         version = javaVersion.substring(0, 2);
/*      */       } 
/*      */     } else {
/*      */       try {
/* 1449 */         ProcessBuilder builder = new ProcessBuilder(new String[] { "java", "-version" });
/* 1450 */         builder.redirectErrorStream(true);
/* 1451 */         Process process = builder.start();
/*      */         
/* 1453 */         InputStream stdout = process.getInputStream();
/* 1454 */         BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
/*      */         
/*      */         String line;
/* 1457 */         while ((line = reader.readLine()) != null) {
/* 1458 */           if (line.contains("version \"1.8")) {
/* 1459 */             javaIsOk = version8Check(line, debug);
/* 1460 */             if (javaIsOk) {
/* 1461 */               version = "8"; continue;
/*      */             } 
/* 1463 */             version = line; continue;
/* 1464 */           }  if (line.contains(" version \"9")) {
/* 1465 */             javaIsOk = true;
/* 1466 */             version = "9"; continue;
/* 1467 */           }  if (line.contains(" version \"10")) {
/* 1468 */             javaIsOk = true;
/* 1469 */             version = "10"; continue;
/* 1470 */           }  if (line.contains(" version \"11")) {
/* 1471 */             javaIsOk = true;
/* 1472 */             version = "11"; continue;
/* 1473 */           }  if (line.contains(" version \"12")) {
/* 1474 */             javaIsOk = true;
/* 1475 */             version = "12"; continue;
/* 1476 */           }  if (line.contains(" version \"13")) {
/* 1477 */             javaIsOk = true;
/* 1478 */             version = "13";
/*      */           } 
/*      */         } 
/*      */         
/* 1482 */         process.destroy();
/* 1483 */       } catch (Exception exception) {}
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1488 */     if (debug) {
/* 1489 */       Logger.output("CoMaS version is " + Shared.VERSION);
/* 1490 */       Logger.output("Java version is " + version);
/* 1491 */       Logger.output("OS is " + System.getProperty("os.name"));
/*      */     } 
/*      */     
/*      */     try {
/* 1495 */       versionFound = Integer.parseInt(version);
/* 1496 */       if (versionFound > Shared.MAX_SUPPORTED_JAVA_VERSION)
/* 1497 */       { javaIsOk = false; }
/* 1498 */       else if (versionFound < Shared.MIN_SUPPORTED_JAVA_VERSION)
/* 1499 */       { javaIsOk = false; }
/*      */       else
/* 1501 */       { javaIsOk = true; } 
/* 1502 */     } catch (NumberFormatException e) {
/* 1503 */       versionFound = 0;
/*      */     } 
/*      */     
/* 1506 */     if (!javaIsOk) {
/* 1507 */       JOptionPane.showMessageDialog(this, "Java version " + version + " is unsupported.", "CoMaS Alert!", 
/* 1508 */           0, IconLoader.getIcon(0));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1515 */     if (versionFound < Shared.MIN_SUPPORTED_JAVA_VERSION)
/* 1516 */       System.exit(-6); 
/* 1517 */     return versionFound;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean version8Check(String line, boolean debug) {
/* 1530 */     if (debug)
/* 1531 */       Logger.output("Check java 8 patch level: " + line); 
/* 1532 */     int index = line.indexOf("_");
/* 1533 */     if (index < 0) {
/* 1534 */       return false;
/*      */     }
/* 1536 */     String s_patchLevel = line.substring(index + 1);
/* 1537 */     if (debug)
/* 1538 */       Logger.output("Patch level: " + s_patchLevel); 
/*      */     try {
/* 1540 */       int patchLevel = Integer.parseInt(s_patchLevel);
/* 1541 */       if (patchLevel < 211)
/* 1542 */         return false; 
/* 1543 */     } catch (NumberFormatException e) {
/* 1544 */       return false;
/*      */     } 
/*      */     
/* 1547 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void main(String[] args) {
/* 1554 */     ExamDialog d = new ExamDialog("CoMaS", args);
/* 1555 */     d.pack();
/* 1556 */     d.setLocationRelativeTo((Component)null);
/* 1557 */     d.setVisible(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void update(Observable o, final Object arg) {
/* 1567 */     SwingUtilities.invokeLater(new Runnable() {
/*      */           public void run() {
/* 1569 */             if (ExamDialog.this.login != null && ExamDialog.this.login.isDone()) {
/* 1570 */               ExamDialog.this.loginButton.setEnabled(false);
/* 1571 */               if (ExamDialog.this.timer != null)
/* 1572 */                 ExamDialog.this.timer.cancel(); 
/* 1573 */               ExamDialog.this.toFront();
/* 1574 */               ExamDialog.this.repaint();
/* 1575 */               if (arg != null) {
/* 1576 */                 JOptionPane.showMessageDialog(ExamDialog.this, arg.toString(), "CoMaS Alert!", 
/* 1577 */                     1, ExamDialog.this.icon);
/*      */               }
/* 1579 */             } else if (arg != null) {
/* 1580 */               String msg = arg.toString();
/* 1581 */               ExamDialog.this.status.setText(msg);
/*      */               
/* 1583 */               if (msg.startsWith("Archive")) {
/* 1584 */                 SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss a zzz");
/* 1585 */                 String time = ft.format(new Date());
/* 1586 */                 ExamDialog.this.message.setText(time);
/*      */               } 
/*      */             } else {
/* 1589 */               ExamDialog.this.toFront();
/* 1590 */               ExamDialog.this.repaint();
/* 1591 */               JOptionPane.showMessageDialog(ExamDialog.this, ExamDialog.this.login.alert, "CoMaS Alert!", 
/* 1592 */                   1, ExamDialog.this.icon);
/*      */             } 
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */   
/*      */   public static synchronized void setStatus(final String msg) {
/* 1600 */     SwingUtilities.invokeLater(new Runnable() {
/*      */           public void run() {
/* 1602 */             ExamDialog.instance.status.setText(msg);
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void updateProgress(final String note, final int value) {
/* 1611 */     SwingUtilities.invokeLater(new Runnable() {
/*      */           public void run() {
/* 1613 */             if (ExamDialog.pm == null) {
/* 1614 */               UIManager.put("ProgressMonitor.progressText", "CoMaS Shutdown Progress");
/* 1615 */               String startMsg = String.format("Shut down for %s %s", new Object[] { ExamDialog.instance.firstName.getText(), 
/* 1616 */                     ExamDialog.instance.lastName.getText() });
/* 1617 */               ExamDialog.pm = new ProgressMonitor(ExamDialog.getInstance(), startMsg, "", 0, 10);
/* 1618 */               ExamDialog.pm.setMillisToDecideToPopup(100);
/* 1619 */               ExamDialog.pm.setMillisToPopup(100);
/*      */             } 
/* 1621 */             ExamDialog.pm.setNote(note);
/* 1622 */             ExamDialog.pm.setProgress(value);
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean isAlreadyRunningCheck = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isAlreadyRunning() {
/* 1639 */     if (isAlreadyRunningCheck) {
/* 1640 */       File f = new File(String.valueOf(Shared.HOME) + File.separator + ".es");
/* 1641 */       if (f.exists()) {
/* 1642 */         f.delete();
/* 1643 */         return true;
/*      */       } 
/*      */       try {
/* 1646 */         f.createNewFile();
/* 1647 */         f.deleteOnExit();
/* 1648 */       } catch (IOException iOException) {}
/*      */       
/* 1650 */       return false;
/*      */     } 
/*      */     
/* 1653 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private class ExamTask
/*      */     extends TimerTask
/*      */   {
/*      */     private ExamTask() {}
/*      */ 
/*      */     
/*      */     public void run() {
/* 1665 */       SwingUtilities.invokeLater(new Runnable()
/*      */           {
/*      */             public void run()
/*      */             {
/* 1669 */               if ((ExamDialog.ExamTask.access$1(ExamDialog.ExamTask.this)).login != null && 
/* 1670 */                 !(ExamDialog.ExamTask.access$1(ExamDialog.ExamTask.this)).login.isDone()) {
/* 1671 */                 (ExamDialog.ExamTask.access$1(ExamDialog.ExamTask.this)).loginButton.setEnabled(true);
/*      */               }
/*      */             }
/*      */           });
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public class ExamApplicationListener
/*      */     implements ApplicationListener
/*      */   {
/*      */     private void handle(ApplicationEvent event, String message) {
/* 1686 */       JOptionPane.showMessageDialog(ExamDialog.this, message, "CoMaS Message", 1, 
/* 1687 */           ExamDialog.this.icon);
/* 1688 */       event.setHandled(true);
/*      */     }
/*      */ 
/*      */     
/*      */     public void handleAbout(ApplicationEvent event) {
/* 1693 */       handle(event, "CoMaS Runner " + Shared.VERSION);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void handleOpenApplication(ApplicationEvent event) {}
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void handleOpenFile(ApplicationEvent event) {}
/*      */ 
/*      */ 
/*      */     
/*      */     public void handlePreferences(ApplicationEvent event) {
/* 1709 */       JOptionPane.showMessageDialog(null, String.valueOf(ExamDialog.this.course) + " exam", "Preferences", 1, ExamDialog.this.icon);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void handlePrintFile(ApplicationEvent event) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void handleQuit(ApplicationEvent event) {
/* 1726 */       int res = JOptionPane.showConfirmDialog(ExamDialog.this, "Are you sure you want to end session?", 
/* 1727 */           "End session?", 0, 2, 
/* 1728 */           IconLoader.getIcon(2));
/* 1729 */       if (res == 0) {
/* 1730 */         if (ExamDialog.this.timer != null)
/* 1731 */           ExamDialog.this.timer.cancel(); 
/* 1732 */         if (ExamDialog.this.login != null)
/* 1733 */           ExamDialog.this.login.endTheSession(); 
/* 1734 */         System.exit(0);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void handleReOpenApplication(ApplicationEvent event) {}
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private class WaitNotification
/*      */     implements Runnable
/*      */   {
/*      */     private final int times;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     WaitNotification(int times) {
/* 1757 */       this.times = times;
/*      */     }
/*      */     
/*      */     public void run() {
/* 1761 */       ExamDialog.this.status.setText(String.format("Waiting (%d) ...", new Object[] { Integer.valueOf(this.times) }));
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\ui\ExamDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */