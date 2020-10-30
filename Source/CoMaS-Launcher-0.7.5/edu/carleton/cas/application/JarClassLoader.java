/*      */ package edu.carleton.cas.application;
/*      */ 
/*      */ import edu.carleton.cas.utility.CodeVerifier;
/*      */ import java.applet.AppletContext;
/*      */ import java.applet.AppletStub;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.FileReader;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.net.JarURLConnection;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.net.URLDecoder;
/*      */ import java.security.CodeSource;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.security.cert.Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.jar.Attributes;
/*      */ import java.util.jar.JarEntry;
/*      */ import java.util.jar.JarFile;
/*      */ import java.util.jar.Manifest;
/*      */ import javax.swing.JApplet;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class JarClassLoader
/*      */   extends ClassLoader
/*      */ {
/*      */   public static final String KEY_LOGGER = "JarClassLoader.logger";
/*      */   public static final String KEY_LOGGER_LEVEL = "JarClassLoader.logger.level";
/*      */   public static final String KEY_LOGGER_AREA = "JarClassLoader.logger.area";
/*      */   public static final String TMP_SUB_DIRECTORY = "JarClassLoader";
/*      */   private File dirTemp;
/*      */   private PrintStream logger;
/*      */   private List<JarFileInfo> lstJarFile;
/*      */   private Set<File> hsDeleteOnExit;
/*      */   private Map<String, Class<?>> hmClass;
/*      */   private LogLevel logLevel;
/*      */   private Set<LogArea> hsLogArea;
/*      */   private boolean bLogConsole;
/*      */   private JApplet applet;
/*      */   static int ______INIT;
/*      */   static int ______SHUTDOWN;
/*      */   static int ______ACCESS;
/*      */   static int ______OVERRIDE;
/*      */   static int ______HELPERS;
/*      */   
/*      */   public enum LogLevel
/*      */   {
/*  218 */     ERROR, WARN, INFO, DEBUG; }
/*      */   
/*  220 */   public enum LogArea { ALL,
/*      */     
/*  222 */     CONFIG,
/*      */     
/*  224 */     JAR,
/*      */     
/*  226 */     CLASS,
/*      */     
/*  228 */     RESOURCE,
/*      */     
/*  230 */     NATIVE; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JarClassLoader(URL urlToBeLoaded, ClassLoader parent) {
/*  272 */     super(parent);
/*  273 */     initLogger();
/*      */     
/*  275 */     this.hmClass = new HashMap<>();
/*  276 */     this.lstJarFile = new ArrayList<>();
/*  277 */     this.hsDeleteOnExit = new HashSet<>();
/*      */ 
/*      */     
/*  280 */     String sUrlTopJar = null;
/*  281 */     ProtectionDomain pdTop = getClass().getProtectionDomain();
/*  282 */     CodeSource cs = pdTop.getCodeSource();
/*  283 */     URL urlTopJar = cs.getLocation();
/*  284 */     urlTopJar = urlToBeLoaded;
/*  285 */     String protocol = urlTopJar.getProtocol();
/*      */ 
/*      */     
/*  288 */     JarFileInfo jarFileInfo = null;
/*  289 */     if ("http".equals(protocol) || "https".equals(protocol)) {
/*      */       
/*      */       try {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  296 */         urlTopJar = new URL("jar:" + urlTopJar + "!/");
/*  297 */         JarURLConnection jarCon = (JarURLConnection)urlTopJar.openConnection();
/*  298 */         JarFile jarFile = jarCon.getJarFile();
/*  299 */         jarFileInfo = new JarFileInfo(jarFile, jarFile.getName(), null, pdTop, null);
/*  300 */         logInfo(LogArea.JAR, "Loading from top JAR: '%s' PROTOCOL: '%s'", new Object[] {
/*  301 */               urlTopJar, protocol });
/*  302 */       } catch (Exception e) {
/*      */         
/*  304 */         logError(LogArea.JAR, "Failure to load HTTP JAR: %s %s", new Object[] { urlTopJar, e.toString() });
/*      */         return;
/*      */       } 
/*      */     }
/*  308 */     if ("file".equals(protocol)) {
/*      */ 
/*      */       
/*      */       try {
/*      */         
/*  313 */         sUrlTopJar = URLDecoder.decode(urlTopJar.getFile(), "UTF-8");
/*  314 */       } catch (UnsupportedEncodingException e) {
/*  315 */         logError(LogArea.JAR, "Failure to decode URL: %s %s", new Object[] { urlTopJar, e.toString() });
/*      */         return;
/*      */       } 
/*  318 */       File fileJar = new File(sUrlTopJar);
/*      */ 
/*      */       
/*  321 */       if (fileJar.isDirectory()) {
/*  322 */         logInfo(LogArea.JAR, "Loading from exploded directory: %s", new Object[] { sUrlTopJar });
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/*      */       try {
/*  328 */         jarFileInfo = new JarFileInfo(new JarFile(fileJar), fileJar.getName(), null, pdTop, null);
/*  329 */         logInfo(LogArea.JAR, "Loading from top JAR: '%s' PROTOCOL: '%s'", new Object[] { sUrlTopJar, protocol });
/*  330 */       } catch (IOException e) {
/*  331 */         logError(LogArea.JAR, "Not a JAR: %s %s", new Object[] { sUrlTopJar, e.toString() });
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/*      */     
/*      */     try {
/*  338 */       if (jarFileInfo == null) {
/*  339 */         throw new IOException(String.format(
/*  340 */               "Unknown protocol %s", new Object[] { protocol }));
/*      */       }
/*  342 */       loadJar(jarFileInfo);
/*  343 */     } catch (IOException e) {
/*  344 */       logError(LogArea.JAR, "Not valid URL: %s %s", new Object[] { urlTopJar, e.toString() });
/*      */       
/*      */       return;
/*      */     } 
/*  348 */     checkShading();
/*  349 */     Runtime.getRuntime().addShutdownHook(new Thread() {
/*      */           public void run() {
/*  351 */             JarClassLoader.this.shutdown();
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void initLogger() {
/*  361 */     this.bLogConsole = true;
/*  362 */     this.logger = System.out;
/*  363 */     this.logLevel = LogLevel.ERROR;
/*  364 */     this.hsLogArea = new HashSet<>();
/*  365 */     this.hsLogArea.add(LogArea.CONFIG);
/*      */ 
/*      */     
/*  368 */     String sLogger = System.getProperty("JarClassLoader.logger");
/*  369 */     if (sLogger != null) {
/*      */       try {
/*  371 */         this.logger = new PrintStream(sLogger);
/*  372 */         this.bLogConsole = false;
/*  373 */       } catch (FileNotFoundException e) {
/*  374 */         logError(LogArea.CONFIG, "Cannot create log file %s.", new Object[] { sLogger });
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*  379 */     String sLogLevel = System.getProperty("JarClassLoader.logger.level");
/*  380 */     if (sLogLevel != null) {
/*      */       try {
/*  382 */         this.logLevel = LogLevel.valueOf(sLogLevel);
/*  383 */       } catch (Exception e) {
/*  384 */         logError(LogArea.CONFIG, "Not valid parameter in %s=%s", new Object[] { "JarClassLoader.logger.level", sLogLevel });
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*  389 */     String sLogArea = System.getProperty("JarClassLoader.logger.area");
/*  390 */     if (sLogArea != null) {
/*  391 */       String[] tokenAll = sLogArea.split(","); try {
/*      */         byte b; int i; String[] arrayOfString;
/*  393 */         for (i = (arrayOfString = tokenAll).length, b = 0; b < i; ) { String t = arrayOfString[b];
/*  394 */           this.hsLogArea.add(LogArea.valueOf(t)); b++; }
/*      */       
/*  396 */       } catch (Exception e) {
/*  397 */         logError(LogArea.CONFIG, "Not valid parameter in %s=%s", new Object[] { "JarClassLoader.logger.area", sLogArea });
/*      */       } 
/*      */     } 
/*  400 */     if (this.hsLogArea.size() == 1 && this.hsLogArea.contains(LogArea.CONFIG)) {
/*  401 */       byte b; int i; LogArea[] arrayOfLogArea; for (i = (arrayOfLogArea = LogArea.values()).length, b = 0; b < i; ) { LogArea la = arrayOfLogArea[b];
/*  402 */         this.hsLogArea.add(la);
/*      */         b++; }
/*      */     
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private File createTempFile(JarEntryInfo inf) throws JarClassLoaderException {
/*  430 */     if (this.dirTemp == null) {
/*  431 */       File dir = new File(System.getProperty("java.io.tmpdir"), "JarClassLoader");
/*  432 */       if (!dir.exists()) {
/*  433 */         dir.mkdir();
/*      */       }
/*  435 */       chmod777(dir);
/*  436 */       if (!dir.exists() || !dir.isDirectory()) {
/*  437 */         throw new JarClassLoaderException(
/*  438 */             "Cannot create temp directory " + dir.getAbsolutePath());
/*      */       }
/*  440 */       this.dirTemp = dir;
/*      */     } 
/*  442 */     File fileTmp = null;
/*      */     try {
/*  444 */       fileTmp = File.createTempFile(String.valueOf(inf.getName()) + ".", null, this.dirTemp);
/*  445 */       fileTmp.deleteOnExit();
/*  446 */       chmod777(fileTmp);
/*  447 */       byte[] a_by = inf.getJarBytes();
/*  448 */       BufferedOutputStream os = new BufferedOutputStream(
/*  449 */           new FileOutputStream(fileTmp));
/*  450 */       os.write(a_by);
/*  451 */       os.close();
/*  452 */       return fileTmp;
/*  453 */     } catch (IOException e) {
/*  454 */       throw new JarClassLoaderException(String.format(
/*  455 */             "Cannot create temp file '%s' for %s", new Object[] { fileTmp, inf.jarEntry }), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void loadJar(JarFileInfo jarFileInfo) throws IOException {
/*  466 */     this.lstJarFile.add(jarFileInfo);
/*      */     try {
/*  468 */       Enumeration<JarEntry> en = jarFileInfo.jarFile.entries();
/*  469 */       String EXT_JAR = ".jar";
/*  470 */       while (en.hasMoreElements()) {
/*  471 */         JarEntry je = en.nextElement();
/*  472 */         if (je.isDirectory()) {
/*      */           continue;
/*      */         }
/*  475 */         String s = je.getName().toLowerCase();
/*  476 */         if (s.lastIndexOf(".jar") == s.length() - ".jar".length()) {
/*  477 */           JarEntryInfo inf = new JarEntryInfo(jarFileInfo, je);
/*  478 */           File fileTemp = createTempFile(inf);
/*  479 */           logInfo(LogArea.JAR, "Loading inner JAR %s from temp file %s", new Object[] {
/*  480 */                 inf.jarEntry, getFilename4Log(fileTemp)
/*      */               });
/*  482 */           URL url = fileTemp.toURI().toURL();
/*  483 */           ProtectionDomain pdParent = jarFileInfo.pd;
/*      */           
/*  485 */           CodeSource csParent = pdParent.getCodeSource();
/*  486 */           Certificate[] certParent = csParent.getCertificates();
/*  487 */           CodeSource csChild = (certParent == null) ? new CodeSource(url, csParent.getCodeSigners()) : 
/*  488 */             new CodeSource(url, certParent);
/*  489 */           ProtectionDomain pdChild = new ProtectionDomain(csChild, 
/*  490 */               pdParent.getPermissions(), pdParent.getClassLoader(), pdParent.getPrincipals());
/*  491 */           loadJar(new JarFileInfo(
/*  492 */                 new JarFile(fileTemp), inf.getName(), jarFileInfo, pdChild, fileTemp));
/*      */         } 
/*      */       } 
/*  495 */     } catch (JarClassLoaderException e) {
/*  496 */       throw new RuntimeException(
/*  497 */           "ERROR on loading inner JAR: " + e.getMessageAll());
/*      */     } 
/*      */   }
/*      */   
/*      */   private JarEntryInfo findJarEntry(String sName) {
/*  502 */     for (JarFileInfo jarFileInfo : this.lstJarFile) {
/*  503 */       JarFile jarFile = jarFileInfo.jarFile;
/*  504 */       JarEntry jarEntry = jarFile.getJarEntry(sName);
/*  505 */       if (jarEntry != null) {
/*  506 */         return new JarEntryInfo(jarFileInfo, jarEntry);
/*      */       }
/*      */     } 
/*  509 */     return null;
/*      */   }
/*      */   
/*      */   private List<JarEntryInfo> findJarEntries(String sName) {
/*  513 */     List<JarEntryInfo> lst = new ArrayList<>();
/*  514 */     for (JarFileInfo jarFileInfo : this.lstJarFile) {
/*  515 */       JarFile jarFile = jarFileInfo.jarFile;
/*  516 */       JarEntry jarEntry = jarFile.getJarEntry(sName);
/*  517 */       if (jarEntry != null) {
/*  518 */         lst.add(new JarEntryInfo(jarFileInfo, jarEntry));
/*      */       }
/*      */     } 
/*  521 */     return lst;
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
/*      */   private JarEntryInfo findJarNativeEntry(String sLib) {
/*  535 */     String sName = System.mapLibraryName(sLib);
/*  536 */     for (JarFileInfo jarFileInfo : this.lstJarFile) {
/*  537 */       JarFile jarFile = jarFileInfo.jarFile;
/*  538 */       Enumeration<JarEntry> en = jarFile.entries();
/*  539 */       while (en.hasMoreElements()) {
/*  540 */         JarEntry je = en.nextElement();
/*  541 */         if (je.isDirectory()) {
/*      */           continue;
/*      */         }
/*      */         
/*  545 */         String sEntry = je.getName();
/*      */ 
/*      */ 
/*      */         
/*  549 */         String[] token = sEntry.split("/");
/*  550 */         if (token.length > 0 && token[token.length - 1].equals(sName)) {
/*  551 */           logInfo(LogArea.NATIVE, "Loading native library '%s' found as '%s' in JAR %s", new Object[] {
/*  552 */                 sLib, sEntry, jarFileInfo.simpleName });
/*  553 */           return new JarEntryInfo(jarFileInfo, je);
/*      */         } 
/*      */       } 
/*      */     } 
/*  557 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Class<?> findJarClass(String sClassName) throws JarClassLoaderException {
/*  568 */     Class<?> c = this.hmClass.get(sClassName);
/*  569 */     if (c != null) {
/*  570 */       return c;
/*      */     }
/*      */     
/*  573 */     String sName = String.valueOf(sClassName.replace('.', '/')) + ".class";
/*  574 */     JarEntryInfo inf = findJarEntry(sName);
/*  575 */     String jarSimpleName = null;
/*  576 */     if (inf != null) {
/*  577 */       jarSimpleName = inf.jarFileInfo.simpleName;
/*  578 */       definePackage(sClassName, inf);
/*  579 */       byte[] a_by = inf.getJarBytes();
/*      */       try {
/*  581 */         c = defineClass(sClassName, a_by, 0, a_by.length, inf.jarFileInfo.pd);
/*  582 */       } catch (ClassFormatError e) {
/*  583 */         throw new JarClassLoaderException(null, e);
/*      */       } 
/*      */     } 
/*  586 */     if (c == null) {
/*  587 */       throw new JarClassLoaderException(sClassName);
/*      */     }
/*  589 */     this.hmClass.put(sClassName, c);
/*  590 */     logInfo(LogArea.CLASS, "Loaded %s by %s from JAR %s", new Object[] {
/*  591 */           sClassName, getClass().getName(), jarSimpleName });
/*  592 */     return c;
/*      */   }
/*      */   
/*      */   private void checkShading() {
/*  596 */     if (this.logLevel.ordinal() < LogLevel.WARN.ordinal()) {
/*      */       return;
/*      */     }
/*      */     
/*  600 */     Map<String, JarFileInfo> hm = new HashMap<>();
/*  601 */     for (JarFileInfo jarFileInfo : this.lstJarFile) {
/*  602 */       JarFile jarFile = jarFileInfo.jarFile;
/*  603 */       Enumeration<JarEntry> en = jarFile.entries();
/*  604 */       while (en.hasMoreElements()) {
/*  605 */         JarEntry je = en.nextElement();
/*  606 */         if (je.isDirectory()) {
/*      */           continue;
/*      */         }
/*  609 */         String sEntry = je.getName();
/*  610 */         if ("META-INF/MANIFEST.MF".equals(sEntry)) {
/*      */           continue;
/*      */         }
/*  613 */         JarFileInfo jar = hm.get(sEntry);
/*  614 */         if (jar == null) {
/*  615 */           hm.put(sEntry, jarFileInfo); continue;
/*      */         } 
/*  617 */         logWarn(LogArea.JAR, "ENTRY %s IN %s SHADES %s", new Object[] {
/*  618 */               sEntry, jar.simpleName, jarFileInfo.simpleName
/*      */             });
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
/*      */   
/*      */   private void shutdown() {
/*  641 */     for (JarFileInfo jarFileInfo : this.lstJarFile) {
/*      */       try {
/*  643 */         jarFileInfo.jarFile.close();
/*  644 */       } catch (IOException iOException) {}
/*      */ 
/*      */       
/*  647 */       File file = jarFileInfo.fileDeleteOnExit;
/*  648 */       if (file != null && !file.delete()) {
/*  649 */         this.hsDeleteOnExit.add(file);
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  656 */     File fileCfg = new File(String.valueOf(System.getProperty("user.home")) + 
/*  657 */         File.separator + ".JarClassLoader");
/*  658 */     deleteOldTemp(fileCfg);
/*  659 */     persistNewTemp(fileCfg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void deleteOldTemp(File fileCfg) {
/*  669 */     BufferedReader reader = null;
/*      */     try {
/*  671 */       int count = 0;
/*  672 */       reader = new BufferedReader(new FileReader(fileCfg));
/*      */       String sLine;
/*  674 */       while ((sLine = reader.readLine()) != null) {
/*  675 */         File file = new File(sLine);
/*  676 */         if (!file.exists()) {
/*      */           continue;
/*      */         }
/*  679 */         if (file.delete()) {
/*  680 */           count++;
/*      */           continue;
/*      */         } 
/*  683 */         this.hsDeleteOnExit.add(file);
/*      */       } 
/*      */       
/*  686 */       logDebug(LogArea.CONFIG, "Deleted %d old temp files listed in %s", new Object[] {
/*  687 */             Integer.valueOf(count), fileCfg.getAbsolutePath() });
/*  688 */     } catch (IOException iOException) {
/*      */     
/*      */     } finally {
/*  691 */       if (reader != null) {
/*  692 */         try { reader.close(); } catch (IOException iOException) {}
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
/*      */   private void persistNewTemp(File fileCfg) {
/*  705 */     if (this.hsDeleteOnExit.size() == 0) {
/*  706 */       logDebug(LogArea.CONFIG, "No temp file names to persist on exit.", new Object[0]);
/*  707 */       fileCfg.delete();
/*      */       return;
/*      */     } 
/*  710 */     logDebug(LogArea.CONFIG, "Persisting %d temp file names into %s", new Object[] {
/*  711 */           Integer.valueOf(this.hsDeleteOnExit.size()), fileCfg.getAbsolutePath() });
/*  712 */     BufferedWriter writer = null;
/*      */     try {
/*  714 */       writer = new BufferedWriter(new FileWriter(fileCfg));
/*  715 */       for (File file : this.hsDeleteOnExit) {
/*  716 */         if (!file.delete()) {
/*  717 */           String f = file.getCanonicalPath();
/*  718 */           writer.write(f);
/*  719 */           writer.newLine();
/*  720 */           logWarn(LogArea.JAR, "JVM failed to release %s", new Object[] { f });
/*      */         } 
/*      */       } 
/*  723 */     } catch (IOException iOException) {
/*      */     
/*      */     } finally {
/*  726 */       if (writer != null) {
/*  727 */         try { writer.close(); } catch (IOException iOException) {}
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
/*      */   public boolean isLaunchedFromJar() {
/*  741 */     return (this.lstJarFile.size() > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getManifestMainClass() {
/*  751 */     Attributes attr = null;
/*  752 */     if (isLaunchedFromJar()) {
/*      */       
/*      */       try {
/*  755 */         Manifest m = ((JarFileInfo)this.lstJarFile.get(0)).jarFile.getManifest();
/*  756 */         attr = m.getMainAttributes();
/*  757 */       } catch (IOException iOException) {}
/*      */     }
/*      */     
/*  760 */     return (attr == null) ? null : attr.getValue(Attributes.Name.MAIN_CLASS);
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
/*      */   public void invokeMain(String sClass, String[] args) throws Throwable {
/*  784 */     invokeMain(sClass, "main", args);
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
/*      */   public void invokeMain(String sClass, String methodName, String[] args) throws Throwable {
/*  796 */     Class<?> clazz = loadClass(sClass);
/*      */     
/*  798 */     CodeVerifier.verify(clazz);
/*      */     
/*  800 */     logInfo(LogArea.CONFIG, "Launch: %s.main(); Loader: %s", new Object[] { sClass, clazz.getClassLoader() });
/*  801 */     Class[] parameterTypes = { String[].class };
/*  802 */     Method method = clazz.getMethod(methodName, parameterTypes);
/*      */     
/*  804 */     boolean bValidModifiers = false;
/*  805 */     boolean bValidVoid = false;
/*      */     
/*  807 */     if (method != null) {
/*  808 */       method.setAccessible(true);
/*  809 */       int nModifiers = method.getModifiers();
/*  810 */       bValidModifiers = (Modifier.isPublic(nModifiers) && 
/*  811 */         Modifier.isStatic(nModifiers));
/*  812 */       Class<?> clazzRet = method.getReturnType();
/*  813 */       bValidVoid = (clazzRet == void.class);
/*      */     } 
/*  815 */     if (method == null || !bValidModifiers || !bValidVoid) {
/*  816 */       throw new NoSuchMethodException(
/*  817 */           "The main(String[] args) method in class \"" + sClass + "\" not found.");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  825 */       method.invoke((Object)null, new Object[] { args });
/*  826 */     } catch (InvocationTargetException e) {
/*  827 */       throw e.getTargetException();
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
/*      */   public void initApplet(String sClass, final JApplet appletParent) throws Throwable {
/*  844 */     Class<?> clazz = loadClass(sClass);
/*  845 */     logInfo(LogArea.CONFIG, "initApplet() --> %s.init(); Loader: %s", new Object[] { sClass, clazz.getClassLoader() });
/*  846 */     this.applet = (JApplet)clazz.newInstance();
/*  847 */     this.applet.setStub(new AppletStub()
/*      */         {
/*      */           public boolean isActive() {
/*  850 */             return appletParent.isActive();
/*      */           }
/*      */           
/*      */           public URL getDocumentBase() {
/*  854 */             return appletParent.getDocumentBase();
/*      */           }
/*      */           
/*      */           public URL getCodeBase() {
/*  858 */             return appletParent.getCodeBase();
/*      */           }
/*      */           
/*      */           public String getParameter(String name) {
/*  862 */             return appletParent.getParameter(name);
/*      */           }
/*      */           
/*      */           public AppletContext getAppletContext() {
/*  866 */             return appletParent.getAppletContext();
/*      */           }
/*      */           
/*      */           public void appletResize(int width, int height) {
/*  870 */             appletParent.resize(width, height);
/*      */           }
/*      */         });
/*  873 */     this.applet.init();
/*  874 */     appletParent.setContentPane(this.applet.getContentPane());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void startApplet() {
/*  882 */     checkApplet();
/*  883 */     logInfo(LogArea.CONFIG, "startApplet() --> %s.start()", new Object[] { this.applet.getClass().getName() });
/*  884 */     this.applet.start();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void stopApplet() {
/*  892 */     checkApplet();
/*  893 */     logInfo(LogArea.CONFIG, "stopApplet() --> %s.stop()", new Object[] { this.applet.getClass().getName() });
/*  894 */     this.applet.stop();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void destroyApplet() {
/*  902 */     checkApplet();
/*  903 */     logInfo(LogArea.CONFIG, "destroyApplet() --> %s.destroy()", new Object[] { this.applet.getClass().getName() });
/*  904 */     this.applet.destroy();
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
/*      */   protected synchronized Class<?> loadClass(String sClassName, boolean bResolve) throws ClassNotFoundException {
/*  920 */     logDebug(LogArea.CLASS, "LOADING %s (resolve=%b)", new Object[] { sClassName, Boolean.valueOf(bResolve) });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  953 */     Thread.currentThread().setContextClassLoader(this);
/*      */     
/*  955 */     Class<?> c = null;
/*      */     
/*      */     try {
/*  958 */       if (getClass().getName().equals(sClassName)) {
/*  959 */         return JarClassLoader.class;
/*      */       }
/*      */       
/*  962 */       if (isLaunchedFromJar()) {
/*      */         try {
/*  964 */           c = findJarClass(sClassName);
/*  965 */           return c;
/*  966 */         } catch (JarClassLoaderException e) {
/*  967 */           if (e.getCause() == null) {
/*  968 */             logDebug(LogArea.CLASS, "Not found %s in JAR by %s: %s", new Object[] {
/*  969 */                   sClassName, getClass().getName(), e.getMessage() });
/*      */           } else {
/*  971 */             logDebug(LogArea.CLASS, "Error loading %s in JAR by %s: %s", new Object[] {
/*  972 */                   sClassName, getClass().getName(), e.getCause()
/*      */                 });
/*      */ 
/*      */ 
/*      */           
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*      */     finally {
/*      */ 
/*      */ 
/*      */       
/*  996 */       if (c != null && bResolve) {
/*  997 */         resolveClass(c);
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
/*      */   protected URL findResource(String sName) {
/* 1010 */     logDebug(LogArea.RESOURCE, "findResource: %s", new Object[] { sName });
/* 1011 */     if (isLaunchedFromJar()) {
/* 1012 */       JarEntryInfo inf = findJarEntry(normalizeResourceName(sName));
/* 1013 */       if (inf != null) {
/* 1014 */         URL url = inf.getURL();
/* 1015 */         logInfo(LogArea.RESOURCE, "found resource: %s", new Object[] { url });
/* 1016 */         return url;
/*      */       } 
/* 1018 */       logInfo(LogArea.RESOURCE, "not found resource: %s", new Object[] { sName });
/* 1019 */       return null;
/*      */     } 
/* 1021 */     return super.findResource(sName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Enumeration<URL> findResources(String sName) throws IOException {
/* 1032 */     logDebug(LogArea.RESOURCE, "getResources: %s", new Object[] { sName });
/* 1033 */     if (isLaunchedFromJar()) {
/* 1034 */       List<JarEntryInfo> lstJarEntry = findJarEntries(normalizeResourceName(sName));
/* 1035 */       List<URL> lstURL = new ArrayList<>();
/* 1036 */       for (JarEntryInfo inf : lstJarEntry) {
/* 1037 */         URL url = inf.getURL();
/* 1038 */         if (url != null) {
/* 1039 */           lstURL.add(url);
/*      */         }
/*      */       } 
/* 1042 */       return Collections.enumeration(lstURL);
/*      */     } 
/* 1044 */     return super.findResources(sName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String findLibrary(String sLib) {
/* 1054 */     logDebug(LogArea.NATIVE, "findLibrary: %s", new Object[] { sLib });
/* 1055 */     if (isLaunchedFromJar()) {
/* 1056 */       JarEntryInfo inf = findJarNativeEntry(sLib);
/* 1057 */       if (inf != null) {
/*      */         try {
/* 1059 */           File file = createTempFile(inf);
/* 1060 */           logDebug(LogArea.NATIVE, "Loading native library %s from temp file %s", new Object[] {
/* 1061 */                 inf.jarEntry, getFilename4Log(file) });
/* 1062 */           this.hsDeleteOnExit.add(file);
/* 1063 */           return file.getAbsolutePath();
/* 1064 */         } catch (JarClassLoaderException e) {
/* 1065 */           logError(LogArea.NATIVE, "Failure to load native library %s: %s", new Object[] { sLib, e.toString() });
/*      */         } 
/*      */       }
/* 1068 */       return null;
/*      */     } 
/* 1070 */     return super.findLibrary(sLib);
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
/*      */   private void definePackage(String sClassName, JarEntryInfo inf) throws IllegalArgumentException {
/* 1091 */     int pos = sClassName.lastIndexOf('.');
/* 1092 */     String sPackageName = (pos > 0) ? sClassName.substring(0, pos) : "";
/* 1093 */     if (getPackage(sPackageName) == null) {
/* 1094 */       JarFileInfo jfi = inf.jarFileInfo;
/* 1095 */       definePackage(sPackageName, 
/* 1096 */           jfi.getSpecificationTitle(), jfi.getSpecificationVersion(), 
/* 1097 */           jfi.getSpecificationVendor(), jfi.getImplementationTitle(), 
/* 1098 */           jfi.getImplementationVersion(), jfi.getImplementationVendor(), 
/* 1099 */           jfi.getSealURL());
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
/*      */   private String normalizeResourceName(String sName) {
/* 1112 */     return sName.replace('\\', '/');
/*      */   }
/*      */   
/*      */   private void chmod777(File file) {
/* 1116 */     file.setReadable(true, false);
/* 1117 */     file.setWritable(true, false);
/* 1118 */     file.setExecutable(true, false);
/*      */   }
/*      */   
/*      */   private String getFilename4Log(File file) {
/* 1122 */     if (this.logger != null) {
/*      */       
/*      */       try {
/* 1125 */         return file.getCanonicalPath();
/* 1126 */       } catch (IOException e) {
/*      */         
/* 1128 */         return file.getAbsolutePath();
/*      */       } 
/*      */     }
/* 1131 */     return null;
/*      */   }
/*      */   
/*      */   private void checkApplet() {
/* 1135 */     if (this.applet == null) {
/* 1136 */       throw new IllegalStateException("Applet is not inited. Please call JarClassLoader.initApplet() first.");
/*      */     }
/*      */   }
/*      */   
/*      */   private void logDebug(LogArea area, String sMsg, Object... obj) {
/* 1141 */     log(LogLevel.DEBUG, area, sMsg, obj);
/*      */   }
/*      */   
/*      */   private void logInfo(LogArea area, String sMsg, Object... obj) {
/* 1145 */     log(LogLevel.INFO, area, sMsg, obj);
/*      */   }
/*      */   
/*      */   private void logWarn(LogArea area, String sMsg, Object... obj) {
/* 1149 */     log(LogLevel.WARN, area, sMsg, obj);
/*      */   }
/*      */   
/*      */   private void logError(LogArea area, String sMsg, Object... obj) {
/* 1153 */     log(LogLevel.ERROR, area, sMsg, obj);
/*      */   }
/*      */   
/*      */   private void log(LogLevel level, LogArea area, String sMsg, Object... obj) {
/* 1157 */     if (level.ordinal() <= this.logLevel.ordinal() && (
/* 1158 */       this.hsLogArea.contains(LogArea.ALL) || this.hsLogArea.contains(area))) {
/* 1159 */       this.logger.printf("JarClassLoader-" + level + ": " + sMsg + "\n", obj);
/*      */     }
/*      */     
/* 1162 */     if (!this.bLogConsole && level == LogLevel.ERROR) {
/* 1163 */       System.out.printf("JarClassLoader-" + level + ": " + sMsg + "\n", obj);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class JarFileInfo
/*      */   {
/*      */     JarFile jarFile;
/*      */ 
/*      */ 
/*      */     
/*      */     String simpleName;
/*      */ 
/*      */ 
/*      */     
/*      */     File fileDeleteOnExit;
/*      */ 
/*      */ 
/*      */     
/*      */     Manifest mf;
/*      */ 
/*      */     
/*      */     ProtectionDomain pd;
/*      */ 
/*      */ 
/*      */     
/*      */     JarFileInfo(JarFile jarFile, String simpleName, JarFileInfo jarFileParent, ProtectionDomain pd, File fileDeleteOnExit) {
/* 1192 */       this.simpleName = String.valueOf((jarFileParent == null) ? "" : (String.valueOf(jarFileParent.simpleName) + "!")) + simpleName;
/* 1193 */       this.jarFile = jarFile;
/* 1194 */       this.pd = pd;
/* 1195 */       this.fileDeleteOnExit = fileDeleteOnExit;
/*      */       try {
/* 1197 */         this.mf = jarFile.getManifest();
/* 1198 */       } catch (IOException iOException) {}
/*      */ 
/*      */       
/* 1201 */       if (this.mf == null)
/* 1202 */         this.mf = new Manifest(); 
/*      */     }
/*      */     
/*      */     String getSpecificationTitle() {
/* 1206 */       return this.mf.getMainAttributes().getValue(Attributes.Name.SPECIFICATION_TITLE);
/*      */     }
/*      */     String getSpecificationVersion() {
/* 1209 */       return this.mf.getMainAttributes().getValue(Attributes.Name.SPECIFICATION_VERSION);
/*      */     }
/*      */     String getSpecificationVendor() {
/* 1212 */       return this.mf.getMainAttributes().getValue(Attributes.Name.SPECIFICATION_VENDOR);
/*      */     }
/*      */     String getImplementationTitle() {
/* 1215 */       return this.mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_TITLE);
/*      */     }
/*      */     String getImplementationVersion() {
/* 1218 */       return this.mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
/*      */     }
/*      */     String getImplementationVendor() {
/* 1221 */       return this.mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
/*      */     }
/*      */     URL getSealURL() {
/* 1224 */       String seal = this.mf.getMainAttributes().getValue(Attributes.Name.SEALED);
/* 1225 */       if (seal != null) {
/*      */         try {
/* 1227 */           return new URL(seal);
/* 1228 */         } catch (MalformedURLException malformedURLException) {}
/*      */       }
/*      */ 
/*      */       
/* 1232 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static class JarEntryInfo
/*      */   {
/*      */     JarClassLoader.JarFileInfo jarFileInfo;
/*      */     JarEntry jarEntry;
/*      */     
/*      */     JarEntryInfo(JarClassLoader.JarFileInfo jarFileInfo, JarEntry jarEntry) {
/* 1243 */       this.jarFileInfo = jarFileInfo;
/* 1244 */       this.jarEntry = jarEntry;
/*      */     }
/*      */     URL getURL() {
/*      */       try {
/* 1248 */         return new URL("jar:file:" + this.jarFileInfo.jarFile.getName() + "!/" + this.jarEntry);
/* 1249 */       } catch (MalformedURLException e) {
/* 1250 */         return null;
/*      */       } 
/*      */     }
/*      */     String getName() {
/* 1254 */       return this.jarEntry.getName().replace('/', '_');
/*      */     }
/*      */     
/*      */     public String toString() {
/* 1258 */       return "JAR: " + this.jarFileInfo.jarFile.getName() + " ENTRY: " + this.jarEntry;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     byte[] getJarBytes() throws JarClassLoader.JarClassLoaderException {
/* 1269 */       DataInputStream dis = null;
/* 1270 */       byte[] a_by = null;
/*      */       try {
/* 1272 */         long lSize = this.jarEntry.getSize();
/* 1273 */         if (lSize <= 0L || lSize >= 2147483647L) {
/* 1274 */           throw new JarClassLoader.JarClassLoaderException(
/* 1275 */               "Invalid size " + lSize + " for entry " + this.jarEntry);
/*      */         }
/* 1277 */         a_by = new byte[(int)lSize];
/* 1278 */         InputStream is = this.jarFileInfo.jarFile.getInputStream(this.jarEntry);
/* 1279 */         dis = new DataInputStream(is);
/* 1280 */         dis.readFully(a_by);
/* 1281 */       } catch (IOException e) {
/* 1282 */         throw new JarClassLoader.JarClassLoaderException(null, e);
/*      */       } finally {
/* 1284 */         if (dis != null) {
/*      */           try {
/* 1286 */             dis.close();
/* 1287 */           } catch (IOException iOException) {}
/*      */         }
/*      */       } 
/*      */       
/* 1291 */       return a_by;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static class JarClassLoaderException
/*      */     extends Exception
/*      */   {
/*      */     JarClassLoaderException(String sMsg) {
/* 1301 */       super(sMsg);
/*      */     }
/*      */     JarClassLoaderException(String sMsg, Throwable eCause) {
/* 1304 */       super(sMsg, eCause);
/*      */     }
/*      */     String getMessageAll() {
/* 1307 */       StringBuilder sb = new StringBuilder();
/* 1308 */       for (Throwable e = this; e != null; e = e.getCause()) {
/* 1309 */         if (sb.length() > 0) {
/* 1310 */           sb.append(" / ");
/*      */         }
/* 1312 */         String sMsg = e.getMessage();
/* 1313 */         if (sMsg == null || sMsg.length() == 0) {
/* 1314 */           sMsg = e.getClass().getSimpleName();
/*      */         }
/* 1316 */         sb.append(sMsg);
/*      */       } 
/* 1318 */       return sb.toString();
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\cas\application\JarClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */