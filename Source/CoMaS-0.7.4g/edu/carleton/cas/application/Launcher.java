package edu.carleton.cas.application;

import edu.carleton.cas.constants.Shared;
import edu.carleton.cas.file.Utils;
import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.security.Checksum;
import edu.carleton.cas.ui.ServerChooser;
import edu.carleton.cas.utility.IconLoader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Launcher {
  private String host = "hostname";
  
  private Properties properties = new Properties();
  
  private JarClassLoader classLoader;
  
  private String jarFileToBeLoaded = String.format(Shared.COMAS_DOT_JAR_FORMAT, new Object[] { Utils.getStringOrDefault(this.properties, "version", Shared.VERSION) });
  
  public void init() {
    try {
      Logger.setup(Launcher.class, "comas-base", Shared.HOME, Level.CONFIG);
    } catch (IOException e) {
      System.err.println("Logging could not be initialized");
    } 
  }
  
  public void process() throws MalformedURLException {
    ServerChooser server = new ServerChooser(null);
    server.open();
    this.host = server.select();
    if (this.host == null)
      System.exit(-1); 
    loadCoMaSDotIni();
    processCoMaSDotIni();
  }
  
  public void close() throws IOException {}
  
  private void loadCoMaSDotIni() {
    Shared.LOGIN_CONFIGURATION_URL = Shared.service(Shared.PROTOCOL, this.host, Shared.PORT, 
        "/CMS/rest/exam/login.ini");
    this.properties = Shared.getLoginProperties("");
    if (this.properties == null || this.properties.isEmpty()) {
      JOptionPane.showMessageDialog(null, 
          String.format("%s is not accessible.\nPlease contact support.", new Object[] { Shared.LOGIN_CONFIGURATION_URL }), "CoMaS Launcher", 0, IconLoader.getIcon(0));
      System.exit(-2);
    } 
  }
  
  private void processCoMaSDotIni() throws MalformedURLException {
    checkDesktopDirectory();
    boolean ok = false;
    for (int i = 0; i < Shared.MAX_FAILURES && !ok; i++) {
      ok = processLocalCoMaSDotJar();
      if (!ok)
        processRemoteCoMaSDotJarAndHandleExceptions(i); 
    } 
    if (!ok) {
      JOptionPane.showMessageDialog(null, 
          String.format("%s could not be downloaded.\nPlease contact support.", new Object[] { this.jarFileToBeLoaded }), "CoMaS Launcher", 
          0, IconLoader.getIcon(0));
      System.exit(-2);
    } 
  }
  
  private void checkDesktopDirectory() {
    File desktopDirectory = new File(Shared.DIR);
    if (desktopDirectory.exists() && desktopDirectory.canWrite() && desktopDirectory.canRead())
      return; 
    desktopDirectory.mkdirs();
    if (!desktopDirectory.exists() || !desktopDirectory.canWrite() || !desktopDirectory.canRead()) {
      JOptionPane.showMessageDialog(null, 
          String.format("%s is not accessible.\nPlease contact support.", new Object[] { Shared.DIR }), "CoMaS Launcher", 
          0, IconLoader.getIcon(0));
      System.exit(-2);
    } 
  }
  
  private boolean processLocalCoMaSDotJar() throws MalformedURLException {
    this.jarFileToBeLoaded = String.format(Shared.COMAS_DOT_JAR_FORMAT, new Object[] { Utils.getStringOrDefault(this.properties, "version", Shared.VERSION) });
    File comasDotJar = new File(String.valueOf(Shared.DIR) + File.separator + this.jarFileToBeLoaded);
    String className = 
      Utils.getStringOrDefault(this.properties, "application.class", "edu.carleton.cas.ui.Exam").trim();
    String methodName = Utils.getStringOrDefault(this.properties, "application.method", "main").trim();
    if (comasDotJar.exists() && comasDotJar.canRead())
      try {
        String checkSum = Checksum.getSHA256Checksum(comasDotJar.getAbsolutePath());
        if (isLocalJarOkay(checkSum))
          try {
            Logger.log(Level.CONFIG, "Loading: ", comasDotJar.toURI().toURL());
            this.classLoader = new JarClassLoader(comasDotJar.toURI().toURL(), getClass().getClassLoader());
            String[] args = { "-server", this.host };
            this.classLoader.invokeMain(className, methodName, args);
            return true;
          } catch (Throwable e) {
            JOptionPane.showMessageDialog(null, String.format("%s.\nPlease contact support.", new Object[] { e }), "CoMaS Launcher", 0, 
                IconLoader.getIcon(0));
            System.exit(-3);
            return true;
          }  
        Logger.log(Level.INFO, String.format("Hash of %s was not correct. It will be downloaded again", new Object[] { comasDotJar.getAbsolutePath() }), "");
        return false;
      } catch (Exception e1) {
        Logger.log(Level.INFO, String.format("Problem with %s: %s", new Object[] { comasDotJar.getAbsolutePath(), e1 }), "");
        return false;
      }  
    Logger.log(Level.INFO, String.format("%s could not be found on this machine. It will be downloaded", new Object[] { comasDotJar.getAbsolutePath() }), "");
    return false;
  }
  
  private void processRemoteCoMaSDotJarAndHandleExceptions(int numberOfTries) {
    String url = Shared.service(Shared.PROTOCOL, this.host, Shared.PORT, 
        "/CMS/rest/exam/" + this.jarFileToBeLoaded);
    if (numberOfTries % 2 == 1) {
      int result = JOptionPane.showConfirmDialog(null, 
          String.format("Download has failed %d times. Would you like to continue to try downloading?", new Object[] { Integer.valueOf(numberOfTries) }), "CoMaS Download", 0, 2, 
          IconLoader.getDefaultIcon());
      if (result == 1)
        System.exit(-1); 
    } 
    try {
      Utils.getAndStoreURL(new URL(url), new File(Shared.DIR));
    } catch (IOException e) {
      Logger.log(Level.INFO, "Could not access " + url, 
          ", will try again in " + (Shared.RETRY_TIME / 1000) + " seconds");
      try {
        Thread.sleep(Shared.RETRY_TIME);
      } catch (InterruptedException interruptedException) {}
    } 
  }
  
  private boolean isLocalJarOkay(String hashOfJar) {
    if (this.properties.containsKey("application.hash"))
      return hashOfJar.equals(this.properties.getProperty("application.hash")); 
    return true;
  }
  
  public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
    String lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
    byte b;
    int i;
    UIManager.LookAndFeelInfo[] arrayOfLookAndFeelInfo1;
    for (i = (arrayOfLookAndFeelInfo1 = info).length, b = 0; b < i; ) {
      UIManager.LookAndFeelInfo lafi = arrayOfLookAndFeelInfo1[b];
      if (lafi.getName().startsWith("Nimbus"))
        lookAndFeelClassName = lafi.getClassName(); 
      b++;
    } 
    UIManager.setLookAndFeel(lookAndFeelClassName);
    Launcher l = new Launcher();
    l.init();
    l.process();
    l.close();
  }
}
