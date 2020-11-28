package edu.carleton.cas.ui;

import edu.carleton.cas.constants.ClientShared;
import edu.carleton.cas.exam.Invigilator;
import edu.carleton.cas.file.DirectoryUtils;
import edu.carleton.cas.file.Utils;
import edu.carleton.cas.logging.Level;
import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.resources.Resource;
import edu.carleton.cas.resources.ResourceListener;
import edu.carleton.cas.resources.VMCheck;
import edu.carleton.cas.security.Checksum;
import edu.carleton.cas.security.IllegalVersionException;
import edu.carleton.cas.utility.ClientHelper;
import edu.carleton.cas.utility.DriveSpace;
import edu.carleton.cas.utility.IconLoader;
import edu.carleton.cas.utility.Named;
import edu.carleton.cas.utility.Observable;
import edu.carleton.cas.utility.Observer;
import edu.carleton.cas.utility.Password;
import edu.carleton.cas.utility.Sleeper;
import edu.carleton.cas.utility.VMDetector;
import edu.carleton.services.ServiceBrowser;
import edu.carleton.services.ServiceListener;
import edu.carleton.services.ServiceLocator;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import javax.bluetooth.LocalDevice;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

public class ExamDialog extends JFrame implements Observer {
  private static final long serialVersionUID = 1L;
  
  private static final String COMAS_ALERT = "CoMaS Alert!";
  
  private static final boolean PASSWORD_INCLUDED = false;
  
  private static final int MAXIMUM_SERVER_RETRIES = 10;
  
  private static final int SERVER_RETRY_SLEEP = 3000;
  
  private static final int SERVER_AUTHENTICATION_RETRY_SLEEP = 2000;
  
  private static final int SERVER_RANDOM_RETRY_SLEEP = 1000;
  
  public static ExamDialog instance;
  
  private WindowAdapter windowAdapter = null;
  
  public JTextField passwordField;
  
  public JTextField studentID;
  
  public JTextField firstName;
  
  public JTextField lastName;
  
  public JButton loginButton;
  
  public JTextField status;
  
  public JTextField message;
  
  public Invigilator login;
  
  public Timer timer = null;
  
  public int versionOfJava;
  
  public String course;
  
  public String activity;
  
  private Properties config;
  
  private Icon icon = null;
  
  private Thread prepareThread;
  
  private ServiceLocator serviceLocator;
  
  private ServiceBrowser serviceBrowser;
  
  private static ProgressMonitor pm;
  
  public ExamDialog(String name, final String[] args) {
    super(name);
    this.versionOfJava = checkVersionOfJava(false);
    processCommandLineArgs(args);
    this.icon = IconLoader.getDefaultIcon();
    lookForServices();
    this.course = askForCourse();
    ClientShared.setupURLs(this.course);
    initFrame();
    this.studentID = new JTextField();
    this.firstName = new JTextField();
    this.lastName = new JTextField();
    this.loginButton = new JButton("Start");
    this.status = new JTextField();
    this.message = new JTextField();
    GridLayout layout = new GridLayout(0, 2);
    JPanel panel = new JPanel();
    panel.setLayout(layout);
    JLabel label = new JLabel(" First name: ");
    panel.add(label);
    panel.add(this.firstName);
    label = new JLabel(" Last name: ");
    panel.add(label);
    panel.add(this.lastName);
    label = new JLabel(" Student ID: ");
    panel.add(label);
    panel.add(this.studentID);
    label = new JLabel(" Status: ");
    panel.add(label);
    panel.add(this.status);
    panel.add(this.loginButton);
    panel.add(this.message);
    getContentPane().add(panel);
    setTitle("CoMaS");
    if (ClientShared.STUDENT_ID != null) {
      this.studentID.setText(ClientShared.STUDENT_ID);
      this.studentID.setEditable(false);
    } 
    if (ClientShared.STUDENT_FIRST_NAME != null) {
      this.firstName.setText(ClientShared.STUDENT_FIRST_NAME);
      this.firstName.setEditable(false);
    } 
    if (ClientShared.STUDENT_LAST_NAME != null) {
      this.lastName.setText(ClientShared.STUDENT_LAST_NAME);
      this.lastName.setEditable(false);
    } 
    this.status.setEditable(false);
    this.message.setEditable(false);
    SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss a zzz");
    String time = ft.format(new Date());
    this.message.setText(time);
    this.loginButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String first = ExamDialog.this.firstName.getText().trim();
            String last = ExamDialog.this.lastName.getText().trim();
            final String name = ExamDialog.this.getName();
            if (ExamDialog.this.loginButton.getText().equals("Start")) {
              if (ExamDialog.this.isIdOkay(ExamDialog.this.studentID.getText()) && ExamDialog.this.isNameOkay(first, last)) {
                Thread loginProtocol = new Thread(new Runnable() {
                      public void run() {
                        if (ClientShared.AUTO_ARCHIVE)
                          (ExamDialog.null.access$0(ExamDialog.null.this)).loginButton.setText("Upload"); 
                        (ExamDialog.null.access$0(ExamDialog.null.this)).loginButton.setEnabled(false);
                        ExamDialog.setStatus("Initializing ...");
                        (ExamDialog.null.access$0(ExamDialog.null.this)).message.setText("");
                        (ExamDialog.null.access$0(ExamDialog.null.this)).firstName.setEditable(false);
                        (ExamDialog.null.access$0(ExamDialog.null.this)).lastName.setEditable(false);
                        (ExamDialog.null.access$0(ExamDialog.null.this)).studentID.setEditable(false);
                        Response response = ExamDialog.null.access$0(ExamDialog.null.this).authenticate(10);
                        if (response != null && response.getStatusInfo() == Response.Status.OK) {
                          boolean hasVMtechnology;
                          String rtn = (String)response.readEntity(String.class);
                          if (rtn.equalsIgnoreCase("{\"DOES NOT EXIST\"}")) {
                            ExamDialog.null.access$0(ExamDialog.null.this).toFront();
                            ExamDialog.null.access$0(ExamDialog.null.this).repaint();
                            String msg = String.format("%s is not registered in %s", new Object[] { this.val$name, (ExamDialog.null.access$0(this.this$1)).course });
                            JOptionPane.showMessageDialog(ExamDialog.null.access$0(ExamDialog.null.this), msg, "CoMaS Alert!", 
                                0, 
                                IconLoader.getIcon(0));
                            System.exit(-1);
                          } 
                          if (rtn.equalsIgnoreCase("{\"STOPPED\"}")) {
                            String msg = String.format("The %s server is unavailable", new Object[] { ClientShared.DIRECTORY_HOST });
                            ExamDialog.null.access$0(ExamDialog.null.this).toFront();
                            ExamDialog.null.access$0(ExamDialog.null.this).repaint();
                            JOptionPane.showMessageDialog(ExamDialog.null.access$0(ExamDialog.null.this), msg, "CoMaS Alert!", 
                                0, 
                                IconLoader.getIcon(0));
                            System.exit(-1);
                          } 
                          JsonReader reader = Json.createReader(new StringReader(rtn));
                          JsonObject meAsJson = reader.readObject();
                          String tokenAsString = meAsJson.getJsonString("TOKEN")
                            .getString();
                          (ExamDialog.null.access$0(ExamDialog.null.this)).config = ClientShared.getClientLoginProperties((ExamDialog.null.access$0(ExamDialog.null.this)).course, tokenAsString);
                          (ExamDialog.null.access$0(ExamDialog.null.this)).config.setProperty("TOKEN", tokenAsString);
                          ExamDialog.null.access$0(ExamDialog.null.this).runVMCheck((ExamDialog.null.access$0(ExamDialog.null.this)).config);
                          ExamDialog.null.access$0(ExamDialog.null.this).checkVersionOfJava(true);
                          (ExamDialog.null.access$0(ExamDialog.null.this)).config.setProperty("JAVA_VERSION", String.valueOf((ExamDialog.null.access$0(ExamDialog.null.this)).versionOfJava));
                          (ExamDialog.null.access$0(ExamDialog.null.this)).config.setProperty("OS", System.getProperty("os.name"));
                          ExamDialog.null.access$0(ExamDialog.null.this).checkDiskSpace();
                          ExamDialog.null.access$0(ExamDialog.null.this).processCommandLineArgs(args);
                          ExamDialog.setStatus("1:config ok");
                          ExamDialog.setStatus("2:activity ...");
                          (ExamDialog.null.access$0(ExamDialog.null.this)).activity = ExamDialog.null.access$0(ExamDialog.null.this).askForActivity((ExamDialog.null.access$0(ExamDialog.null.this)).course, tokenAsString);
                          ExamDialog.setStatus("2:" + (ExamDialog.null.access$0(ExamDialog.null.this)).activity);
                          ExamDialog.null.access$0(ExamDialog.null.this).getActivity((ExamDialog.null.access$0(ExamDialog.null.this)).course, (ExamDialog.null.access$0(ExamDialog.null.this)).activity, name, tokenAsString);
                          ExamDialog.null.access$0(ExamDialog.null.this).checkActivityPassCodeOrExit();
                          ExamDialog.null.access$0(ExamDialog.null.this).setTitle(String.valueOf((ExamDialog.null.access$0(ExamDialog.null.this)).course) + "/" + (ExamDialog.null.access$0(ExamDialog.null.this)).activity);
                          try {
                            hasVMtechnology = VMDetector.isVMMac();
                          } catch (HeadlessException|java.net.SocketException e2) {
                            hasVMtechnology = false;
                          } 
                          ExamDialog.null.access$0(ExamDialog.null.this).overwriteExistingActivityDirectoryOrConfirmNoDownload();
                          ExamDialog.null.access$0(ExamDialog.null.this).getFileWithKnownHashFromStudent();
                          (ExamDialog.null.access$0(ExamDialog.null.this)).login = new Invigilator((ExamDialog.null.access$0(ExamDialog.null.this)).studentID.getText(), name, (ExamDialog.null.access$0(ExamDialog.null.this)).course, (ExamDialog.null.access$0(ExamDialog.null.this)).activity, (ExamDialog.null.access$0(ExamDialog.null.this)).config);
                          (ExamDialog.null.access$0(ExamDialog.null.this)).login.addObserver(ExamDialog.null.access$0(ExamDialog.null.this));
                          if (hasVMtechnology)
                            (ExamDialog.null.access$0(ExamDialog.null.this)).login.logArchiver.put((Level)Level.NOTED, 
                                "Virtual machine technology detected"); 
                          if (ClientShared.BLUETOOTH_MONITORING) {
                            Thread BTthread = new Thread(new Runnable() {
                                  public void run() {
                                    ExamDialog.setStatus("2:BT check");
                                    if (LocalDevice.isPowerOn()) {
                                      (ExamDialog.null.access$0(ExamDialog.null.null.access$0(ExamDialog.null.null.this))).login.logArchiver.log((Level)Level.NOTED, 
                                          "Bluetooth power on detected");
                                      (ExamDialog.null.access$0(ExamDialog.null.null.access$0(ExamDialog.null.null.this))).login.alert("Please switch Bluetooth off!");
                                    } 
                                  }
                                });
                            BTthread.start();
                          } 
                          ExamDialog.setStatus("3:ip check");
                          if (!(ExamDialog.null.access$0(ExamDialog.null.this)).login.canRunAtThisIPAddress()) {
                            ExamDialog.null.access$0(ExamDialog.null.this).toFront();
                            ExamDialog.null.access$0(ExamDialog.null.this).repaint();
                            JOptionPane.showMessageDialog(ExamDialog.null.access$0(ExamDialog.null.this), 
                                "Session can't be run from this address", "CoMaS Alert!", 
                                0, 
                                IconLoader.getIcon(0));
                            System.exit(-1);
                          } 
                          if (ExamDialog.null.access$0(ExamDialog.null.this).isWebcamRequired() && 
                            (ExamDialog.null.access$0(ExamDialog.null.this)).login.getWebcam() == null) {
                            ExamDialog.null.access$0(ExamDialog.null.this).toFront();
                            ExamDialog.null.access$0(ExamDialog.null.this).repaint();
                            JOptionPane.showMessageDialog(ExamDialog.null.access$0(ExamDialog.null.this), 
                                "Webcam required but one cannot be accessed", "CoMaS Alert!", 
                                0, 
                                IconLoader.getIcon(0));
                            System.exit(-1);
                          } 
                          ExamDialog.null.access$0(ExamDialog.null.this).prepareToRunExam();
                          if (ClientShared.AUTO_ARCHIVE) {
                            (ExamDialog.null.access$0(ExamDialog.null.this)).timer = new Timer();
                            (ExamDialog.null.access$0(ExamDialog.null.this)).timer.schedule(new ExamDialog.ExamTask(null), ClientShared.MIN_MSECS_BETWEEN_USER_UPLOADS);
                          } 
                        } else {
                          String msg = String.format("%s is not responding", new Object[] { ClientShared.DIRECTORY_HOST });
                          ExamDialog.null.access$0(ExamDialog.null.this).toFront();
                          ExamDialog.null.access$0(ExamDialog.null.this).repaint();
                          JOptionPane.showMessageDialog(ExamDialog.null.access$0(ExamDialog.null.this), msg, "CoMaS Alert!", 
                              0, IconLoader.getIcon(0));
                          System.exit(-1);
                        } 
                      }
                    });
                loginProtocol.start();
              } 
            } else {
              SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss a zzz");
              String time = ft.format(new Date());
              ExamDialog.this.loginButton.setEnabled(false);
              if (ExamDialog.this.login.createAndUploadArchive(true)) {
                ExamDialog.this.message.setText(time);
                ExamDialog.this.timer.schedule(new ExamDialog.ExamTask(null), ClientShared.MIN_MSECS_BETWEEN_USER_UPLOADS);
              } else {
                ExamDialog.this.loginButton.setEnabled(true);
                JOptionPane.showMessageDialog(ExamDialog.this, "Archive upload failed at " + time, "CoMaS Alert!", 
                    0, IconLoader.getIcon(0));
              } 
            } 
          }
        });
    instance = this;
  }
  
  public String getName() {
    String first = this.firstName.getText().trim();
    String last = this.lastName.getText().trim();
    return (String.valueOf(first) + " " + last).toLowerCase();
  }
  
  public Response authenticate(int retries) {
    Response response;
    setStatus("0:auth check");
    int authentication_tries = 0;
    do {
      try {
        response = authenticate();
        authentication_tries++;
      } catch (Exception e) {
        response = null;
        Sleeper.sleep(2000, 1000);
      } 
    } while (response == null && authentication_tries < retries);
    return response;
  }
  
  public Response authenticate() {
    Client client = ClientHelper.createClient(ClientShared.PROTOCOL);
    WebTarget webTarget = client.target(ClientShared.BASE_LOGIN).path("authenticate");
    Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" });
    invocationBuilder.accept(new String[] { "application/json" });
    Form form = new Form();
    form.param("course", this.course);
    form.param("activity", "default");
    form.param("version", ClientShared.VERSION);
    form.param("passkey", ClientShared.PASSKEY_DIRECTORY);
    String name = getName();
    form.param("name", name);
    form.param("url", String.valueOf(ClientShared.VIDEO_URL) + "/" + this.course + "/" + this.activity + "/" + name);
    form.param("type", "Logging in");
    form.param("description", this.studentID.getText().trim());
    Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded"));
    return response;
  }
  
  public void setTitleStatus(final int status) {
    SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if (status == 1) {
              ExamDialog.this.setTitle(String.valueOf(ExamDialog.this.course) + "/" + ExamDialog.this.activity + " ✅");
            } else if (status == -1) {
              ExamDialog.this.setTitle(String.valueOf(ExamDialog.this.course) + "/" + ExamDialog.this.activity + " ❌");
            } else if (status == 0) {
              ExamDialog.this.setTitle(String.valueOf(ExamDialog.this.course) + "/" + ExamDialog.this.activity + " 🕐");
            } else if (status == -2) {
              ExamDialog.this.setTitle(String.valueOf(ExamDialog.this.course) + "/" + ExamDialog.this.activity + "");
            } 
          }
        });
  }
  
  private void checkActivityPassCodeOrExit() {
    if (!ClientShared.USE_ACTIVITY_CODES)
      return; 
    String saltAsString = this.config.getProperty("SALT");
    String passcode = this.config.getProperty("PASSCODE");
    if (saltAsString == null || passcode == null)
      return; 
    byte[] salt = Password.stringToByte(saltAsString);
    String studentPassCode = null;
    while (studentPassCode == null) {
      studentPassCode = (String)JOptionPane.showInputDialog(this, 
          "Enter " + this.activity + " passcode or press Cancel to exit", "Enter " + this.activity + " code", 
          -1, IconLoader.getIcon(-1), null, null);
      if (studentPassCode == null)
        System.exit(-6); 
      String securePassCode = Password.getSecurePassword(studentPassCode, salt);
      if (securePassCode.equals(passcode))
        return; 
      studentPassCode = null;
    } 
  }
  
  private void processCommandLineArgs(String[] args) {
    System.setProperty("java.locale.providers", "COMPAT,CLDR");
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-find")) {
        ClientShared.LOOK_FOR_SERVICES = true;
      } else if (args[i].equals("-comas")) {
        if (i + 1 < args.length) {
          ClientShared.DIR = args[i + 1].trim();
          Logger.output("CoMaS DIRECTORY = " + ClientShared.DIR);
        } 
      } else if (args[i].equals("-downloads")) {
        if (i + 1 < args.length) {
          ClientShared.DOWNLOADS_DIR = args[i + 1].trim();
          Logger.output("Downloads DIRECTORY = " + ClientShared.DOWNLOADS_DIR);
        } 
      } else if (args[i].equals("-logging")) {
        if (i + 1 < args.length) {
          try {
            ClientShared.LOGGING_LEVEL = Level.parse(args[i + 1].trim());
          } catch (Exception e) {
            ClientShared.LOGGING_LEVEL = Level.INFO;
          } 
          Logger.output("Logging level = " + ClientShared.LOGGING_LEVEL);
        } 
      } else if (args[i].equals("-course")) {
        if (i + 1 < args.length) {
          ClientShared.STUDENT_COURSE = args[i + 1].trim();
          Logger.output("Course = " + ClientShared.STUDENT_COURSE);
        } 
      } else if (args[i].equals("-activity")) {
        if (i + 1 < args.length) {
          ClientShared.STUDENT_ACTIVITY = args[i + 1].trim();
          Logger.output("Activity = " + ClientShared.STUDENT_ACTIVITY);
        } 
      } else if (args[i].equals("-name")) {
        if (i + 2 < args.length) {
          ClientShared.STUDENT_FIRST_NAME = args[i + 1].trim().toLowerCase();
          ClientShared.STUDENT_LAST_NAME = args[i + 2].trim().toLowerCase();
          Logger.output("Name = " + ClientShared.STUDENT_FIRST_NAME + " " + ClientShared.STUDENT_LAST_NAME);
        } 
      } else if (args[i].equals("-id")) {
        if (i + 1 < args.length) {
          ClientShared.STUDENT_ID = args[i + 1].trim();
          Logger.output("ID = " + ClientShared.STUDENT_ID);
        } 
      } else if (args[i].equals("-server") && 
        i + 1 < args.length) {
        ClientShared.SERVER_CHOSEN = args[i + 1].trim();
        Logger.output("Server = " + ClientShared.SERVER_CHOSEN);
      } 
    } 
  }
  
  private void prepareToRunExam() {
    this.prepareThread = new Thread(new Runnable() {
          public void run() {
            int times = 0;
            ExamDialog.this.setTitleStatus(0);
            Logger.output("Preparing to run session");
            boolean timeCheck = false;
            while (!timeCheck) {
              int rtnCode = ExamDialog.this.login.canStart();
              if (rtnCode < 0) {
                String msg = String.format("This session cannot continue, server return code: %d.\n" + 
                    ClientShared.SUPPORT_MESSAGE, new Object[] { Integer.valueOf(rtnCode) });
                JOptionPane.showMessageDialog(null, msg, "CoMaS Alert!", 0, 
                    IconLoader.getIcon(0));
                Logger.output(msg);
              } 
              timeCheck = (rtnCode > 0);
              if (timeCheck)
                timeCheck = ExamDialog.this.isActivityStartable(); 
              if (!timeCheck)
                ExamDialog.this.doWait(++times); 
            } 
            Logger.output("Session can start");
            while (!ExamDialog.this.login.isExamOk())
              ExamDialog.this.doWait(++times); 
            Logger.output("Activity is deployed");
            ExamDialog.this.login.runExam();
          }
        });
    this.prepareThread.start();
  }
  
  private void doWait(int times) {
    SwingUtilities.invokeLater(new WaitNotification(times));
    try {
      Thread.sleep(ClientShared.RETRY_TIME);
    } catch (InterruptedException interruptedException) {}
  }
  
  public static ExamDialog getInstance() {
    return instance;
  }
  
  private void initFrame() {
    setFonts();
    setSize(250, 200);
    this.windowAdapter = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          super.windowClosing(e);
          int res = JOptionPane.showConfirmDialog(ExamDialog.this, "Are you sure you want to end session?", 
              "End CoMaS session?", 0, 2, 
              IconLoader.getIcon(2));
          if (res == 0) {
            Thread t = new Thread() {
                public void run() {
                  if ((ExamDialog.null.access$0(ExamDialog.null.this)).login != null)
                    (ExamDialog.null.access$0(ExamDialog.null.this)).login.endTheSession(); 
                  ExamDialog.null.access$0(ExamDialog.null.this).dispose();
                  System.exit(0);
                }
              };
            t.start();
          } 
        }
        
        public void windowClosed(WindowEvent e) {
          super.windowClosed(e);
          if (ExamDialog.this.timer != null)
            ExamDialog.this.timer.cancel(); 
          System.exit(0);
        }
      };
    setDefaultCloseOperation(0);
    addWindowListener(this.windowAdapter);
  }
  
  private void overwriteExistingActivityDirectoryOrConfirmNoDownload() {
    String activityDirectory = ClientShared.getActivityDirectory(this.course, this.activity);
    File archives = new File(activityDirectory);
    boolean exists = archives.exists();
    if (!isResumable()) {
      if (exists) {
        int res = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to overwrite " + activityDirectory + "?", "Overwrite activity?", 
            0, 2, 
            IconLoader.getIcon(2));
        if (res != 0) {
          System.exit(0);
        } else {
          try {
            DirectoryUtils.destroyDirectory(activityDirectory);
          } catch (IOException e1) {
            JOptionPane.showMessageDialog(this, "Failed to delete " + activityDirectory + ". Exiting ...", 
                "CoMaS Alert!", 0, IconLoader.getIcon(0));
            System.exit(-5);
          } 
        } 
      } 
    } else if (exists) {
      File[] archiveFiles = archives.listFiles();
      if (archiveFiles != null && archiveFiles.length > 0) {
        int res = JOptionPane.showConfirmDialog(this, 
            "Do you want to download a new copy of the activity?\nThis will overwrite your work.", 
            "New activity download?", 0, 2, 
            IconLoader.getIcon(2));
        if (res == 1) {
          Logger.output("No download required");
          this.config.setProperty("NO_DOWNLOAD_REQUIRED", "true");
        } else {
          Logger.output("Overwriting " + activityDirectory + File.separator + "exam");
          this.config.remove("NO_DOWNLOAD_REQUIRED");
          try {
            DirectoryUtils.destroyDirectory(String.valueOf(activityDirectory) + File.separator + "exam");
          } catch (IOException e1) {
            toFront();
            repaint();
            JOptionPane.showMessageDialog(this, 
                "Failed to delete " + activityDirectory + File.separator + "exam" + ". Exiting ...", 
                "CoMaS Alert!", 0, 
                IconLoader.getIcon(0));
            System.exit(-5);
          } 
        } 
      } 
    } 
  }
  
  private String askForCourse() {
    String course = null;
    if (ClientShared.STUDENT_COURSE == null) {
      ServerChooser server = new ServerChooser(this);
      server.open();
      course = chooseCourse(server.select());
      server.close();
    } else {
      course = ClientShared.STUDENT_COURSE;
      if (ClientShared.SERVER_CHOSEN != null)
        chooseCourse(ClientShared.SERVER_CHOSEN); 
    } 
    if (course == null)
      System.exit(-1); 
    return course;
  }
  
  private String chooseCourse(String host) {
    if (host == null)
      return null; 
    String course = null;
    Properties configs = null;
    ClientShared.EXAM_CONFIGURATION_FILE = ClientShared.examDotIni(host);
    configs = Utils.getProperties(ClientShared.EXAM_CONFIGURATION_FILE);
    if (configs == null || configs.isEmpty())
      configs = Utils.getProperties(ClientShared.LOCAL_EXAM_CONFIGURATION_FILE); 
    Logger.output("Server is " + host);
    Logger.output("Configuration is " + configs);
    if (ClientShared.SERVER_CHOSEN == null || ClientShared.STUDENT_COURSE == null) {
      if (configs != null && !configs.isEmpty()) {
        ClientShared.CONFIGS = configs;
        String courses = configs.getProperty("courses");
        if (courses != null) {
          String[] possibilities = courses.split(",");
          if (possibilities.length == 0) {
            noCourseError(host);
          } else if (possibilities.length == 1) {
            course = possibilities[0].trim();
            if (course.length() == 0) {
              noCourseError(host);
              course = null;
            } 
          } else if (possibilities.length > 1) {
            for (int i = 0; i < possibilities.length; i++)
              possibilities[i] = possibilities[i].trim(); 
            course = (String)JOptionPane.showInputDialog(this, "Please choose course:", 
                "Course Choice Dialog", -1, this.icon, (Object[])possibilities, 
                possibilities[0]);
          } 
        } else {
          noCourseError(host);
        } 
      } else {
        noCourseError(host);
      } 
      return course;
    } 
    return ClientShared.STUDENT_COURSE;
  }
  
  private void noCourseError(String host) {
    JOptionPane.showMessageDialog(this, 
        String.format("No courses are currently defined on %s.\n" + ClientShared.SUPPORT_MESSAGE, new Object[] { host }), "CoMaS Alert!", 0, IconLoader.getIcon(0));
  }
  
  private void getActivity(String course, String activity, String name, String token) {
    int tries = 0;
    while (tries < 10) {
      tries++;
      try {
        Response response = Invigilator.activity(course, activity, name, token);
        String activitiesAsJson = (String)response.readEntity(String.class);
        if (activitiesAsJson.equals("{\"ILLEGAL VERSION\"}"))
          throw new IllegalVersionException(ClientShared.VERSION); 
        JsonReader reader = Json.createReader(new StringReader(activitiesAsJson));
        JsonObject activityAsJson = reader.readObject();
        for (Map.Entry<String, JsonValue> entry : (Iterable<Map.Entry<String, JsonValue>>)activityAsJson.entrySet()) {
          String key = Named.unquoted(((String)entry.getKey()).toString());
          String value = Named.unquoted(((JsonValue)entry.getValue()).toString());
          if (!key.equals("DESCRIPTION"))
            this.config.setProperty(key, value); 
        } 
        tries = 10;
      } catch (IllegalVersionException e1) {
        JOptionPane.showMessageDialog(this, 
            "Illegal version detected (" + e1.getMessage() + ").\nPlease quit and download new version", 
            "CoMaS Alert!", 0, IconLoader.getIcon(0));
        tries = 10;
      } catch (Exception e) {
        Sleeper.sleep(3000, 1000);
      } 
    } 
    Logger.output("Session Configuration:");
    Logger.output(this.config.toString());
  }
  
  private boolean isWebcamRequired() {
    return (ClientShared.WEB_CAM_MANDATORY && (ClientShared.USE_WEB_CAM || ClientShared.USE_WEB_CAM_ON_SCREEN_SHOT));
  }
  
  private boolean isResumable() {
    String value = this.config.getProperty("RESUMABLE", "no");
    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"))
      return true; 
    if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no"))
      return false; 
    return false;
  }
  
  public void getFileWithKnownHashFromStudent() {
    final String checksumOfKnownFile = this.config.getProperty("STUDENT_NOTES_FILE_CHECKSUM");
    if (checksumOfKnownFile != null) {
      final CountDownLatch countDownLatch = new CountDownLatch(1);
      SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              JFileChooser jfc = new JFileChooser();
              jfc.setDialogTitle("Select an authorized archive");
              jfc.setAcceptAllFileFilterUsed(false);
              FileNameExtensionFilter filter = new FileNameExtensionFilter("ZIP or PDF files", new String[] { "zip", "pdf" });
              jfc.addChoosableFileFilter(filter);
              while (true) {
                int returnValue = jfc.showOpenDialog(ExamDialog.instance);
                if (returnValue == 0) {
                  File selectedFile = jfc.getSelectedFile();
                  try {
                    String checksum = Checksum.getSHA256Checksum(selectedFile.getAbsolutePath());
                    if (checksumOfKnownFile.equals(checksum)) {
                      Logger.log(Level.INFO, "Notes file ", 
                          String.valueOf(selectedFile.getAbsolutePath()) + " SHA-256: (" + checksum + ")");
                      ExamDialog.this.config.setProperty("STUDENT_NOTES_FILE_NAME", 
                          selectedFile.getAbsolutePath());
                    } else {
                      JOptionPane.showMessageDialog(ExamDialog.instance, 
                          "This file was not the one authorized: " + selectedFile.getAbsolutePath(), 
                          "CoMaS Alert!", 2, 
                          IconLoader.getIcon(2));
                      returnValue = -1;
                    } 
                  } catch (Exception e) {
                    Logger.log(Level.WARNING, "Checksum failure for ", selectedFile.getAbsolutePath());
                  } 
                } else if (returnValue == 1) {
                  countDownLatch.countDown();
                  return;
                } 
                if (returnValue == 0) {
                  countDownLatch.countDown();
                  return;
                } 
              } 
            }
          });
      try {
        countDownLatch.await();
      } catch (InterruptedException interruptedException) {}
    } 
  }
  
  private boolean isRunnableInVM() {
    String value = this.config.getProperty("vm_enabled");
    if (value == null)
      value = this.config.getProperty("virtual_machine", "yes"); 
    value = value.trim();
    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"))
      return true; 
    if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no"))
      return false; 
    return true;
  }
  
  private void runVMCheck(Properties properties) {
    Logger.output("Running VM check");
    VMCheck vmCheck = new VMCheck(properties);
    vmCheck.open();
    vmCheck.addListener(new ResourceListener() {
          public void resourceEvent(Resource resource, String type, String description) {
            if (type.equals("exception") && !ExamDialog.this.isRunnableInVM()) {
              ExamDialog.this.toFront();
              ExamDialog.this.repaint();
              JOptionPane.showMessageDialog(ExamDialog.this, 
                  "Unable to run virtual machine detection check.\nCheck: " + description + "\n" + 
                  ClientShared.SUPPORT_MESSAGE, 
                  "CoMaS Alert!", 0, IconLoader.getIcon(0));
              System.exit(-1);
            } else {
              Logger.log(Level.WARNING, "Session running in a virtual machine (", String.valueOf(description.trim()) + ")");
              if (!ExamDialog.this.isRunnableInVM()) {
                ExamDialog.this.toFront();
                ExamDialog.this.repaint();
                JOptionPane.showMessageDialog(ExamDialog.this, "Session can't run in a virtual machine", 
                    "CoMaS Alert!", 0, IconLoader.getIcon(0));
                System.exit(-1);
              } 
            } 
          }
        });
    setStatus("2:vm check");
  }
  
  private boolean isActivityStartable() {
    return isActivityStartableUsingTimeInMsecs();
  }
  
  private boolean isActivityStartableUsingTimeInMsecs() {
    boolean rtn = false;
    if (this.config.containsKey("START_MSECS") && this.config.containsKey("END_MSECS") && 
      this.config.containsKey("CURRENT_TIME")) {
      String startAsString = this.config.getProperty("START_MSECS");
      String endAsString = this.config.getProperty("END_MSECS");
      String nowAsString = this.config.getProperty("CURRENT_TIME");
      long start = Long.parseLong(startAsString);
      long end = Long.parseLong(endAsString);
      long now = Long.parseLong(nowAsString);
      rtn = (start <= now && now <= end);
    } 
    return rtn;
  }
  
  private String askForActivity(String course, String token) {
    String activity = null;
    if (ClientShared.STUDENT_ACTIVITY == null) {
      String[] possibilities = null;
      int tries = 0;
      while (tries < 10 && possibilities == null) {
        try {
          tries++;
          Response response = Invigilator.activities(course, token);
          String activitiesAsJson = (String)response.readEntity(String.class);
          if (activitiesAsJson.equals("{\"ILLEGAL VERSION\"}"))
            throw new IllegalVersionException(ClientShared.VERSION); 
          Logger.output("Activities:");
          Logger.output(activitiesAsJson);
          JsonReader reader = Json.createReader(new StringReader(activitiesAsJson));
          JsonArray meAsJson = reader.readArray();
          possibilities = new String[meAsJson.size()];
          for (int i = 0; i < meAsJson.size(); i++)
            possibilities[i] = Named.unquoted(meAsJson.getJsonString(i).toString()); 
        } catch (IllegalVersionException e1) {
          JOptionPane.showMessageDialog(this, 
              "Illegal version detected (" + e1.getMessage() + ").\nPlease quit and download new version", 
              "CoMaS Alert!", 0, IconLoader.getIcon(0));
        } catch (Exception e) {
          Logger.debug(Level.WARNING, "No activities: " + e.getMessage());
          if (tries == 10) {
            toFront();
            repaint();
            JOptionPane.showMessageDialog(this, 
                "The list of activities for " + course + " cannot be obtained", "CoMaS Alert!", 
                0, IconLoader.getIcon(0));
            System.exit(-1);
            continue;
          } 
          Sleeper.sleep(3000, 1000);
        } 
      } 
      if (possibilities != null) {
        if (possibilities.length == 0) {
          toFront();
          repaint();
          Logger.debug(Level.WARNING, "No activities defined for " + course);
          JOptionPane.showMessageDialog(this, "No activities available for " + course, "CoMaS Alert!", 
              0, IconLoader.getIcon(0));
          System.exit(-1);
        } else if (possibilities.length == 1) {
          activity = possibilities[0];
        } else if (possibilities.length > 1) {
          while (activity == null) {
            activity = (String)JOptionPane.showInputDialog(this, "Please choose activity:", 
                "Activity Choice Dialog", -1, this.icon, (Object[])possibilities, 
                possibilities[0]);
            if (activity == null)
              System.exit(-1); 
          } 
        } else {
          activity = "default";
        } 
      } else {
        activity = "default";
      } 
    } else {
      activity = ClientShared.STUDENT_ACTIVITY;
    } 
    return activity;
  }
  
  private void setFonts() {
    UIManager.put("Button.font", new FontUIResource(new Font("Helvetica", 1, 16)));
    UIManager.put("TextField.font", new FontUIResource(new Font("Helvetica", 1, 16)));
    UIManager.put("Label.font", new FontUIResource(new Font("Helvetica", 1, 16)));
  }
  
  private boolean isIdOkay(String id) {
    boolean rtn = true;
    if (this.config != null) {
      String checker = this.config.getProperty("student.id.pattern");
      String description = this.config.getProperty("student.id.description");
      if (checker != null && 
        !this.studentID.getText().matches(checker)) {
        if (description == null)
          description = "Student ID must conform to this pattern:\n" + checker; 
        JOptionPane.showMessageDialog(this, description, "CoMaS Alert!", 2, 
            IconLoader.getIcon(2));
        rtn = false;
      } 
      if (checker != null) {
        Logger.output("Student ID check: " + rtn);
        return rtn;
      } 
    } 
    if (id.length() != 9) {
      JOptionPane.showMessageDialog(this, "Student ID should be 1XXXXXXXX (9 characters)", "CoMaS Alert!", 
          2, IconLoader.getIcon(2));
      rtn = false;
    } 
    if (id.length() > 1 && !id.startsWith("10")) {
      JOptionPane.showMessageDialog(this, "Student ID should start with a \"10\"", "CoMaS Alert!", 
          2, IconLoader.getIcon(2));
      rtn = false;
    } 
    if (id.length() > 2 && id.charAt(2) != '1' && id.charAt(2) != '0') {
      JOptionPane.showMessageDialog(this, "Student ID should hava a '1' or a '0' as the 3rd character", 
          "CoMaS Alert!", 2, IconLoader.getIcon(2));
      rtn = false;
    } 
    for (int i = 0; i < id.length(); i++) {
      char c = id.charAt(i);
      if (c < '0' || c > '9') {
        JOptionPane.showMessageDialog(this, "Illegal character '" + c + "' in student ID", "CoMaS Alert!", 
            2, IconLoader.getIcon(2));
        rtn = false;
      } 
    } 
    return rtn;
  }
  
  private boolean isNameOkay(String first, String last) {
    boolean rtn = true;
    if (this.config == null)
      return (isNameStringOkay("First", first) && isNameStringOkay("Last", last)); 
    String checkerFirst = this.config.getProperty("student.name.first.pattern");
    String description = this.config.getProperty("student.name.first.description");
    if (checkerFirst != null && 
      !first.matches(checkerFirst)) {
      if (description == null)
        description = "Student first name must conform to this pattern:\n" + checkerFirst; 
      JOptionPane.showMessageDialog(this, description, "CoMaS Alert!", 2, 
          IconLoader.getIcon(2));
      rtn = false;
    } 
    String checkerLast = this.config.getProperty("student.name.last.pattern");
    description = this.config.getProperty("student.name.last.description");
    if (checkerLast != null && 
      !last.matches(checkerLast)) {
      if (description == null)
        description = "Student last name must confirm to this pattern:\n" + checkerLast; 
      JOptionPane.showMessageDialog(this, description, "CoMaS Alert!", 2, 
          IconLoader.getIcon(2));
      rtn = false;
    } 
    if (checkerFirst != null || checkerLast != null) {
      Logger.output("Student name check: " + rtn);
      return rtn;
    } 
    return (isNameStringOkay("First", first) && isNameStringOkay("Last", last));
  }
  
  private boolean isNameStringOkay(String type, String name) {
    boolean rtn = true;
    if (name.length() == 0) {
      JOptionPane.showMessageDialog(this, String.valueOf(type) + " name must be entered", "CoMaS Alert!", 
          2, IconLoader.getIcon(2));
      rtn = false;
    } 
    if (name.length() > 32) {
      JOptionPane.showMessageDialog(this, String.valueOf(type) + " name length must be less than 32 characters", "CoMaS Alert!", 
          2, IconLoader.getIcon(2));
      rtn = false;
    } 
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (c >= '0' && c <= '9') {
        JOptionPane.showMessageDialog(this, String.valueOf(type) + " name cannot contain a number", "CoMaS Alert!", 
            2, IconLoader.getIcon(2));
        rtn = false;
      } 
    } 
    return rtn;
  }
  
  private void lookForServices() {
    if (ClientShared.LOOK_FOR_SERVICES) {
      Logger.log(Level.CONFIG, "Looking for CoMaS services", "");
      ServiceListener listener = new ServiceListener() {
          public void onRemove(String arg0, String arg1) {
            if (arg0.equals("*")) {
              Logger.log(Level.FINE, "Unsubscribed from all services", "");
            } else if (arg0 != null && arg1 != null) {
              Logger.log(Level.CONFIG, String.valueOf(arg0) + " service unpublished ", arg1);
            } 
          }
          
          public void onAdd(String arg0, String arg1) {
            if (arg1 != null) {
              Logger.log(Level.CONFIG, String.valueOf(arg0) + " service added ", arg1);
            } else {
              Logger.log(Level.CONFIG, String.valueOf(arg0) + " service added ", " with unknown endpoint");
            } 
            try {
              URL url = new URL(arg1);
              if (arg0.contains("EDIR")) {
                ClientShared.DIRECTORY_HOST = url.getHost();
                ClientShared.PROTOCOL = url.getProtocol();
                ClientShared.PORT = String.valueOf(url.getPort());
              } else if (arg0.contains("ELOG")) {
                ClientShared.LOG_HOST = url.getHost();
                ClientShared.PROTOCOL = url.getProtocol();
                ClientShared.PORT = String.valueOf(url.getPort());
              } else if (arg0.contains("EVID")) {
                ClientShared.VIDEO_HOST = url.getHost();
                ClientShared.PROTOCOL = url.getProtocol();
                ClientShared.PORT = String.valueOf(url.getPort());
              } else if (arg0.contains("EFUP")) {
                ClientShared.UPLOAD_HOST = url.getHost();
                ClientShared.PROTOCOL = url.getProtocol();
                ClientShared.PORT = String.valueOf(url.getPort());
              } else if (arg0.contains("ECMS")) {
                ClientShared.CMS_HOST = url.getHost();
                ClientShared.PROTOCOL = url.getProtocol();
                ClientShared.PORT = String.valueOf(url.getPort());
              } else if (arg0.contains("EXAM")) {
                ClientShared.EXAM_HOST = url.getHost();
                ClientShared.PROTOCOL = url.getProtocol();
                ClientShared.PORT = String.valueOf(url.getPort());
              } 
              ClientShared.updateURLs();
            } catch (MalformedURLException e) {
              Logger.log(Level.CONFIG, "Illegal CoMaS service URL found: ", e.getMessage());
            } 
          }
        };
      try {
        this.serviceLocator = new ServiceLocator(listener);
        this.serviceBrowser = new ServiceBrowser(listener);
        this.serviceLocator.open();
        this.serviceLocator.subscribe("EDIR");
        this.serviceLocator.subscribe("EVID");
        this.serviceLocator.subscribe("EFUP");
        this.serviceLocator.subscribe("EXAM");
        this.serviceLocator.subscribe("ELOG");
        this.serviceLocator.subscribe("ECMS");
      } catch (Exception e1) {
        Logger.log(Level.WARNING, "Could not find CoMaS services using service location: ", e1.getMessage());
      } 
    } 
  }
  
  private void checkDiskSpace() {
    double free = DriveSpace.free();
    if (free < ClientShared.MIN_DRIVE_SPACE_THRESHOLD_PERCENTAGE) {
      NumberFormat nf = NumberFormat.getNumberInstance();
      JOptionPane.showMessageDialog(this, 
          "Warning! Low drive space (" + nf.format(free) + "%), " + 
          ClientShared.MIN_DRIVE_SPACE_THRESHOLD_PERCENTAGE + "% suggested.", 
          "CoMaS Alert!", 2, IconLoader.getIcon(2));
    } 
    free = DriveSpace.freeMB();
    if (free < ClientShared.MIN_DRIVE_SPACE_THRESHOLD_MB) {
      NumberFormat nf = NumberFormat.getNumberInstance();
      JOptionPane.showMessageDialog(this, 
          "Warning! Low drive space (" + nf.format(free) + "MB), " + ClientShared.MIN_DRIVE_SPACE_THRESHOLD_MB + 
          "MB suggested.", 
          "CoMaS Alert!", 2, IconLoader.getIcon(2));
    } 
  }
  
  private int checkVersionOfJava(boolean debug) {
    int versionFound;
    String version = "unknown";
    String javaVersion = System.getProperty("java.version");
    if (javaVersion == null)
      javaVersion = System.getProperty("java.runtime.version"); 
    boolean javaIsOk = false;
    if (javaVersion != null) {
      if (javaVersion.startsWith("1.8")) {
        javaIsOk = version8Check(javaVersion, debug);
        if (javaIsOk) {
          version = "8";
        } else {
          version = javaVersion;
        } 
      } else if (javaVersion.startsWith("9")) {
        javaIsOk = true;
        version = "9";
      } else if (javaVersion.startsWith("10")) {
        javaIsOk = true;
        version = "10";
      } else if (javaVersion.startsWith("11")) {
        javaIsOk = true;
        version = "11";
      } else if (javaVersion.startsWith("12")) {
        javaIsOk = true;
        version = "12";
      } else if (javaVersion.startsWith("13")) {
        javaIsOk = true;
        version = "13";
      } else {
        javaIsOk = false;
        version = javaVersion.substring(0, 2);
      } 
    } else {
      try {
        ProcessBuilder builder = new ProcessBuilder(new String[] { "java", "-version" });
        builder.redirectErrorStream(true);
        Process process = builder.start();
        InputStream stdout = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.contains("version \"1.8")) {
            javaIsOk = version8Check(line, debug);
            if (javaIsOk) {
              version = "8";
              continue;
            } 
            version = line;
            continue;
          } 
          if (line.contains(" version \"9")) {
            javaIsOk = true;
            version = "9";
            continue;
          } 
          if (line.contains(" version \"10")) {
            javaIsOk = true;
            version = "10";
            continue;
          } 
          if (line.contains(" version \"11")) {
            javaIsOk = true;
            version = "11";
            continue;
          } 
          if (line.contains(" version \"12")) {
            javaIsOk = true;
            version = "12";
            continue;
          } 
          if (line.contains(" version \"13")) {
            javaIsOk = true;
            version = "13";
          } 
        } 
        process.destroy();
      } catch (Exception exception) {}
    } 
    if (debug) {
      Logger.output("CoMaS version is " + ClientShared.VERSION);
      Logger.output("Java version is " + version);
      Logger.output("OS is " + System.getProperty("os.name"));
    } 
    try {
      versionFound = Integer.parseInt(version);
      if (versionFound > ClientShared.MAX_SUPPORTED_JAVA_VERSION) {
        javaIsOk = false;
      } else if (versionFound < ClientShared.MIN_SUPPORTED_JAVA_VERSION) {
        javaIsOk = false;
      } else {
        javaIsOk = true;
      } 
    } catch (NumberFormatException e) {
      versionFound = 0;
    } 
    if (!javaIsOk) {
      toFront();
      repaint();
      JOptionPane.showMessageDialog(this, "Java version " + version + " is unsupported.", "CoMaS Alert!", 
          0, IconLoader.getIcon(0));
    } 
    if (versionFound < ClientShared.MIN_SUPPORTED_JAVA_VERSION)
      System.exit(-6); 
    return versionFound;
  }
  
  private boolean version8Check(String line, boolean debug) {
    if (debug)
      Logger.output("Check java 8 patch level: " + line); 
    int index = line.indexOf("_");
    if (index < 0)
      return false; 
    String s_patchLevel = line.substring(index + 1);
    if (debug)
      Logger.output("Patch level: " + s_patchLevel); 
    try {
      int patchLevel = Integer.parseInt(s_patchLevel);
      if (patchLevel < 211)
        return false; 
    } catch (NumberFormatException e) {
      return false;
    } 
    return true;
  }
  
  public static void main(String[] args) {
    ExamDialog d = new ExamDialog("CoMaS", args);
    d.pack();
    d.setLocationRelativeTo((Component)null);
    d.setVisible(true);
  }
  
  public synchronized void update(Observable o, final Object arg) {
    SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if (ExamDialog.this.login != null && ExamDialog.this.login.isDone()) {
              ExamDialog.this.loginButton.setEnabled(false);
              if (ExamDialog.this.timer != null)
                ExamDialog.this.timer.cancel(); 
              ExamDialog.this.toFront();
              ExamDialog.this.repaint();
              if (arg != null)
                JOptionPane.showMessageDialog(ExamDialog.this, arg.toString(), "CoMaS Alert!", 
                    1, ExamDialog.this.icon); 
            } else if (arg != null) {
              String msg = arg.toString();
              ExamDialog.this.status.setText(msg);
              if (msg.startsWith("Archive")) {
                SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss a zzz");
                String time = ft.format(new Date());
                ExamDialog.this.message.setText(time);
              } 
            } else {
              ExamDialog.this.toFront();
              ExamDialog.this.repaint();
              JOptionPane.showMessageDialog(ExamDialog.this, ExamDialog.this.login.alert, "CoMaS Alert!", 
                  1, ExamDialog.this.icon);
            } 
          }
        });
  }
  
  public static synchronized void setStatus(final String msg) {
    SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            ExamDialog.instance.status.setText(msg);
          }
        });
  }
  
  public static void updateProgress(final String note, final int value) {
    SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if (ExamDialog.pm == null) {
              UIManager.put("ProgressMonitor.progressText", "CoMaS Shutdown Progress");
              String startMsg = String.format("Shut down for %s %s", new Object[] { ExamDialog.instance.firstName.getText(), 
                    ExamDialog.instance.lastName.getText() });
              ExamDialog.pm = new ProgressMonitor(ExamDialog.getInstance(), startMsg, "", 0, 10);
              ExamDialog.pm.setMillisToDecideToPopup(100);
              ExamDialog.pm.setMillisToPopup(100);
            } 
            ExamDialog.pm.setNote(note);
            ExamDialog.pm.setProgress(value);
          }
        });
  }
  
  private class ExamTask extends TimerTask {
    private ExamTask() {}
    
    public void run() {
      SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              if ((ExamDialog.ExamTask.access$1(ExamDialog.ExamTask.this)).login != null && 
                !(ExamDialog.ExamTask.access$1(ExamDialog.ExamTask.this)).login.isDone())
                (ExamDialog.ExamTask.access$1(ExamDialog.ExamTask.this)).loginButton.setEnabled(true); 
            }
          });
    }
  }
  
  private class WaitNotification implements Runnable {
    private final int times;
    
    WaitNotification(int times) {
      this.times = times;
    }
    
    public void run() {
      ExamDialog.this.status.setText(String.format("Waiting (%d) ...", new Object[] { Integer.valueOf(this.times) }));
    }
  }
}
