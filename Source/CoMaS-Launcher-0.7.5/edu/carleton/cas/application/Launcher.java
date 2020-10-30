/*     */ package edu.carleton.cas.application;
/*     */ 
/*     */ import edu.carleton.cas.constants.Shared;
/*     */ import edu.carleton.cas.file.Utils;
/*     */ import edu.carleton.cas.logging.Logger;
/*     */ import edu.carleton.cas.security.Checksum;
/*     */ import edu.carleton.cas.ui.ServerChooser;
/*     */ import edu.carleton.cas.utility.IconLoader;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.Level;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.UnsupportedLookAndFeelException;
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
/*     */ public class Launcher
/*     */ {
/*  46 */   private String host = "hostname";
/*  47 */   private Properties properties = new Properties(); private JarClassLoader classLoader;
/*  48 */   private String jarFileToBeLoaded = String.format(Shared.COMAS_DOT_JAR_FORMAT, new Object[] {
/*  49 */         Utils.getStringOrDefault(this.properties, "version", Shared.VERSION)
/*     */       });
/*     */   
/*     */   public void init() {
/*     */     try {
/*  54 */       Logger.setup(Launcher.class, "comas-base", Shared.HOME, Level.CONFIG);
/*  55 */     } catch (IOException e) {
/*  56 */       System.err.println("Logging could not be initialized");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void process() throws MalformedURLException {
/*  67 */     ServerChooser server = new ServerChooser(null);
/*  68 */     server.open();
/*  69 */     this.host = server.select();
/*  70 */     if (this.host == null) {
/*  71 */       System.exit(-1);
/*     */     }
/*  73 */     loadCoMaSDotIni();
/*  74 */     processCoMaSDotIni();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {}
/*     */ 
/*     */ 
/*     */   
/*     */   private void loadCoMaSDotIni() {
/*  85 */     Shared.LOGIN_CONFIGURATION_URL = Shared.service(Shared.PROTOCOL, this.host, Shared.PORT, 
/*  86 */         "/CMS/rest/exam/login.ini");
/*  87 */     this.properties = Shared.getLoginProperties("");
/*  88 */     if (this.properties == null || this.properties.isEmpty()) {
/*  89 */       JOptionPane.showMessageDialog(null, 
/*  90 */           String.format("%s is not accessible.\nPlease contact support.", new Object[] { Shared.LOGIN_CONFIGURATION_URL
/*  91 */             }), "CoMaS Launcher", 0, IconLoader.getIcon(0));
/*  92 */       System.exit(-2);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void processCoMaSDotIni() throws MalformedURLException {
/* 103 */     checkDesktopDirectory();
/* 104 */     boolean ok = false;
/* 105 */     for (int i = 0; i < Shared.MAX_FAILURES && !ok; i++) {
/* 106 */       ok = processLocalCoMaSDotJar();
/* 107 */       if (!ok)
/* 108 */         processRemoteCoMaSDotJarAndHandleExceptions(i); 
/*     */     } 
/* 110 */     if (!ok) {
/* 111 */       JOptionPane.showMessageDialog(null, 
/* 112 */           String.format("%s could not be downloaded.\nPlease contact support.", new Object[] { this.jarFileToBeLoaded }), "CoMaS Launcher", 
/* 113 */           0, IconLoader.getIcon(0));
/* 114 */       System.exit(-2);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkDesktopDirectory() {
/* 122 */     File desktopDirectory = new File(Shared.DIR);
/* 123 */     if (desktopDirectory.exists() && desktopDirectory.canWrite() && desktopDirectory.canRead()) {
/*     */       return;
/*     */     }
/* 126 */     desktopDirectory.mkdirs();
/*     */     
/* 128 */     if (!desktopDirectory.exists() || !desktopDirectory.canWrite() || !desktopDirectory.canRead()) {
/* 129 */       JOptionPane.showMessageDialog(null, 
/* 130 */           String.format("%s is not accessible.\nPlease contact support.", new Object[] { Shared.DIR }), "CoMaS Launcher", 
/* 131 */           0, IconLoader.getIcon(0));
/* 132 */       System.exit(-2);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean processLocalCoMaSDotJar() throws MalformedURLException {
/* 152 */     this.jarFileToBeLoaded = String.format(Shared.COMAS_DOT_JAR_FORMAT, new Object[] {
/* 153 */           Utils.getStringOrDefault(this.properties, "version", Shared.VERSION) });
/* 154 */     File comasDotJar = new File(String.valueOf(Shared.DIR) + File.separator + this.jarFileToBeLoaded);
/*     */     
/* 156 */     String className = 
/* 157 */       Utils.getStringOrDefault(this.properties, "application.class", "edu.carleton.cas.ui.Exam").trim();
/* 158 */     String methodName = Utils.getStringOrDefault(this.properties, "application.method", "main").trim();
/*     */     
/* 160 */     if (comasDotJar.exists() && comasDotJar.canRead())
/*     */       
/*     */       try {
/*     */         
/* 164 */         String checkSum = Checksum.getSHA256Checksum(comasDotJar.getAbsolutePath());
/*     */         
/* 166 */         if (isLocalJarOkay(checkSum))
/*     */           try {
/* 168 */             Logger.log(Level.CONFIG, "Loading: ", comasDotJar.toURI().toURL());
/* 169 */             this.classLoader = new JarClassLoader(comasDotJar.toURI().toURL(), getClass().getClassLoader());
/* 170 */             String[] args = { "-server", this.host };
/* 171 */             this.classLoader.invokeMain(className, methodName, args);
/* 172 */             return true;
/* 173 */           } catch (Throwable e) {
/* 174 */             JOptionPane.showMessageDialog(null, String.format("%s.\nPlease contact support.", new Object[] { e
/* 175 */                   }), "CoMaS Launcher", 0, 
/* 176 */                 IconLoader.getIcon(0));
/* 177 */             System.exit(-3);
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
/*     */ 
/*     */             
/* 193 */             return true;
/*     */           }  
/*     */         Logger.log(Level.INFO, String.format("Hash of %s was not correct. It will be downloaded again", new Object[] { comasDotJar.getAbsolutePath() }), "");
/*     */         return false;
/*     */       } catch (Exception e1) {
/*     */         Logger.log(Level.INFO, String.format("Problem with %s: %s", new Object[] { comasDotJar.getAbsolutePath(), e1 }), "");
/*     */         return false;
/*     */       }  
/*     */     Logger.log(Level.INFO, String.format("%s could not be found on this machine. It will be downloaded", new Object[] { comasDotJar.getAbsolutePath() }), "");
/*     */     return false; } private void processRemoteCoMaSDotJarAndHandleExceptions(int numberOfTries) {
/* 203 */     String url = Shared.service(Shared.PROTOCOL, this.host, Shared.PORT, 
/* 204 */         "/CMS/rest/exam/" + this.jarFileToBeLoaded);
/*     */     
/* 206 */     if (numberOfTries % 2 == 1) {
/* 207 */       int result = JOptionPane.showConfirmDialog(null, 
/* 208 */           String.format("Download has failed %d times. Would you like to continue to try downloading?", new Object[] { Integer.valueOf(numberOfTries)
/* 209 */             }), "CoMaS Download", 0, 2, 
/* 210 */           IconLoader.getDefaultIcon());
/* 211 */       if (result == 1)
/* 212 */         System.exit(-1); 
/*     */     } 
/*     */     try {
/* 215 */       Utils.getAndStoreURL(new URL(url), new File(Shared.DIR));
/* 216 */     } catch (IOException e) {
/* 217 */       Logger.log(Level.INFO, "Could not access " + url, 
/* 218 */           ", will try again in " + (Shared.RETRY_TIME / 1000) + " seconds");
/*     */       try {
/* 220 */         Thread.sleep(Shared.RETRY_TIME);
/* 221 */       } catch (InterruptedException interruptedException) {}
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
/*     */ 
/*     */   
/*     */   private boolean isLocalJarOkay(String hashOfJar) {
/* 236 */     if (this.properties.containsKey("application.hash")) {
/* 237 */       return hashOfJar.equals(this.properties.getProperty("application.hash"));
/*     */     }
/* 239 */     return true;
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
/*     */ 
/*     */   
/*     */   public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
/* 257 */     UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
/* 258 */     String lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName(); byte b; int i; UIManager.LookAndFeelInfo[] arrayOfLookAndFeelInfo1;
/* 259 */     for (i = (arrayOfLookAndFeelInfo1 = info).length, b = 0; b < i; ) { UIManager.LookAndFeelInfo lafi = arrayOfLookAndFeelInfo1[b];
/*     */       
/* 261 */       if (lafi.getName().startsWith("Nimbus"))
/* 262 */         lookAndFeelClassName = lafi.getClassName();  b++; }
/*     */     
/* 264 */     UIManager.setLookAndFeel(lookAndFeelClassName);
/*     */     
/* 266 */     Launcher l = new Launcher();
/* 267 */     l.init();
/* 268 */     l.process();
/* 269 */     l.close();
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\cas\application\Launcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */