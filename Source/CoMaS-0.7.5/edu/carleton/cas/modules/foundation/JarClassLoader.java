package edu.carleton.cas.modules.foundation;

import edu.carleton.cas.utility.CodeVerifier;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarClassLoader extends ClassLoader {
  public static final String KEY_LOGGER = "JarClassLoader.logger";
  
  public static final String KEY_LOGGER_LEVEL = "JarClassLoader.logger.level";
  
  public static final String KEY_LOGGER_AREA = "JarClassLoader.logger.area";
  
  public static final String TMP_SUB_DIRECTORY = "JarClassLoader";
  
  private File dirTemp;
  
  private PrintStream logger;
  
  private List<JarFileInfo> lstJarFile;
  
  private Set<File> hsDeleteOnExit;
  
  private Map<String, Class<?>> hmClass;
  
  private LogLevel logLevel;
  
  private Set<LogArea> hsLogArea;
  
  private boolean bLogConsole;
  
  static int ______INIT;
  
  static int ______SHUTDOWN;
  
  static int ______ACCESS;
  
  static int ______OVERRIDE;
  
  static int ______HELPERS;
  
  public enum LogLevel {
    ERROR, WARN, INFO, DEBUG;
  }
  
  public enum LogArea {
    ALL, CONFIG, JAR, CLASS, RESOURCE, NATIVE;
  }
  
  public JarClassLoader(URL urlToBeLoaded, ClassLoader parent, String token) {
    super(parent);
    initLogger();
    this.hmClass = new HashMap<>();
    this.lstJarFile = new ArrayList<>();
    this.hsDeleteOnExit = new HashSet<>();
    String sUrlTopJar = null;
    ProtectionDomain pdTop = getClass().getProtectionDomain();
    CodeSource cs = pdTop.getCodeSource();
    URL urlTopJar = cs.getLocation();
    urlTopJar = urlToBeLoaded;
    String protocol = urlTopJar.getProtocol();
    JarFileInfo jarFileInfo = null;
    if ("http".equals(protocol) || "https".equals(protocol))
      try {
        urlTopJar = new URL("jar:" + urlTopJar + "!/");
        JarURLConnection jarCon = (JarURLConnection)urlTopJar.openConnection();
        if (token != null)
          jarCon.setRequestProperty("Cookie", "token=" + token); 
        JarFile jarFile = jarCon.getJarFile();
        jarFileInfo = new JarFileInfo(jarFile, jarFile.getName(), null, pdTop, null);
        logInfo(LogArea.JAR, "Loading from top JAR: '%s' PROTOCOL: '%s'", new Object[] { urlTopJar, protocol });
      } catch (Exception e) {
        logError(LogArea.JAR, "Failure to load HTTP JAR: %s %s", new Object[] { urlTopJar, e.toString() });
        return;
      }  
    if ("file".equals(protocol)) {
      try {
        sUrlTopJar = URLDecoder.decode(urlTopJar.getFile(), "UTF-8");
      } catch (UnsupportedEncodingException e) {
        logError(LogArea.JAR, "Failure to decode URL: %s %s", new Object[] { urlTopJar, e.toString() });
        return;
      } 
      File fileJar = new File(sUrlTopJar);
      if (fileJar.isDirectory()) {
        logInfo(LogArea.JAR, "Loading from exploded directory: %s", new Object[] { sUrlTopJar });
        return;
      } 
      try {
        jarFileInfo = new JarFileInfo(new JarFile(fileJar), fileJar.getName(), null, pdTop, null);
        logInfo(LogArea.JAR, "Loading from top JAR: '%s' PROTOCOL: '%s'", new Object[] { sUrlTopJar, protocol });
      } catch (IOException e) {
        logError(LogArea.JAR, "Not a JAR: %s %s", new Object[] { sUrlTopJar, e.toString() });
        return;
      } 
    } 
    try {
      if (jarFileInfo == null)
        throw new IOException(String.format(
              "Unknown protocol %s", new Object[] { protocol })); 
      loadJar(jarFileInfo);
    } catch (IOException e) {
      logError(LogArea.JAR, "Not valid URL: %s %s", new Object[] { urlTopJar, e.toString() });
      return;
    } 
    checkShading();
    Runtime.getRuntime().addShutdownHook(new Thread() {
          public void run() {
            JarClassLoader.this.shutdown();
          }
        });
  }
  
  private void initLogger() {
    this.bLogConsole = true;
    this.logger = System.out;
    this.logLevel = LogLevel.ERROR;
    this.hsLogArea = new HashSet<>();
    this.hsLogArea.add(LogArea.CONFIG);
    String sLogger = System.getProperty("JarClassLoader.logger");
    if (sLogger != null)
      try {
        this.logger = new PrintStream(sLogger);
        this.bLogConsole = false;
      } catch (FileNotFoundException e) {
        logError(LogArea.CONFIG, "Cannot create log file %s.", new Object[] { sLogger });
      }  
    String sLogLevel = System.getProperty("JarClassLoader.logger.level");
    if (sLogLevel != null)
      try {
        this.logLevel = LogLevel.valueOf(sLogLevel);
      } catch (Exception e) {
        logError(LogArea.CONFIG, "Not valid parameter in %s=%s", new Object[] { "JarClassLoader.logger.level", sLogLevel });
      }  
    String sLogArea = System.getProperty("JarClassLoader.logger.area");
    if (sLogArea != null) {
      String[] tokenAll = sLogArea.split(",");
      try {
        byte b;
        int i;
        String[] arrayOfString;
        for (i = (arrayOfString = tokenAll).length, b = 0; b < i; ) {
          String t = arrayOfString[b];
          this.hsLogArea.add(LogArea.valueOf(t));
          b++;
        } 
      } catch (Exception e) {
        logError(LogArea.CONFIG, "Not valid parameter in %s=%s", new Object[] { "JarClassLoader.logger.area", sLogArea });
      } 
    } 
    if (this.hsLogArea.size() == 1 && this.hsLogArea.contains(LogArea.CONFIG)) {
      byte b;
      int i;
      LogArea[] arrayOfLogArea;
      for (i = (arrayOfLogArea = LogArea.values()).length, b = 0; b < i; ) {
        LogArea la = arrayOfLogArea[b];
        this.hsLogArea.add(la);
        b++;
      } 
    } 
  }
  
  private File createTempFile(JarEntryInfo inf) throws JarClassLoaderException {
    if (this.dirTemp == null) {
      File dir = new File(System.getProperty("java.io.tmpdir"), "JarClassLoader");
      if (!dir.exists())
        dir.mkdir(); 
      chmod777(dir);
      if (!dir.exists() || !dir.isDirectory())
        throw new JarClassLoaderException(
            "Cannot create temp directory " + dir.getAbsolutePath()); 
      this.dirTemp = dir;
    } 
    File fileTmp = null;
    try {
      fileTmp = File.createTempFile(String.valueOf(inf.getName()) + ".", null, this.dirTemp);
      fileTmp.deleteOnExit();
      chmod777(fileTmp);
      byte[] a_by = inf.getJarBytes();
      BufferedOutputStream os = new BufferedOutputStream(
          new FileOutputStream(fileTmp));
      os.write(a_by);
      os.close();
      return fileTmp;
    } catch (IOException e) {
      throw new JarClassLoaderException(String.format(
            "Cannot create temp file '%s' for %s", new Object[] { fileTmp, inf.jarEntry }), e);
    } 
  }
  
  private void loadJar(JarFileInfo jarFileInfo) throws IOException {
    this.lstJarFile.add(jarFileInfo);
    try {
      Enumeration<JarEntry> en = jarFileInfo.jarFile.entries();
      String EXT_JAR = ".jar";
      while (en.hasMoreElements()) {
        JarEntry je = en.nextElement();
        if (je.isDirectory())
          continue; 
        String s = je.getName().toLowerCase();
        if (s.lastIndexOf(".jar") == s.length() - ".jar".length()) {
          JarEntryInfo inf = new JarEntryInfo(jarFileInfo, je);
          File fileTemp = createTempFile(inf);
          logInfo(LogArea.JAR, "Loading inner JAR %s from temp file %s", new Object[] { inf.jarEntry, getFilename4Log(fileTemp) });
          URL url = fileTemp.toURI().toURL();
          ProtectionDomain pdParent = jarFileInfo.pd;
          CodeSource csParent = pdParent.getCodeSource();
          Certificate[] certParent = csParent.getCertificates();
          CodeSource csChild = (certParent == null) ? new CodeSource(url, csParent.getCodeSigners()) : 
            new CodeSource(url, certParent);
          ProtectionDomain pdChild = new ProtectionDomain(csChild, 
              pdParent.getPermissions(), pdParent.getClassLoader(), pdParent.getPrincipals());
          loadJar(new JarFileInfo(
                new JarFile(fileTemp), inf.getName(), jarFileInfo, pdChild, fileTemp));
        } 
      } 
    } catch (JarClassLoaderException e) {
      throw new RuntimeException(
          "ERROR on loading inner JAR: " + e.getMessageAll());
    } 
  }
  
  private JarEntryInfo findJarEntry(String sName) {
    for (JarFileInfo jarFileInfo : this.lstJarFile) {
      JarFile jarFile = jarFileInfo.jarFile;
      JarEntry jarEntry = jarFile.getJarEntry(sName);
      if (jarEntry != null)
        return new JarEntryInfo(jarFileInfo, jarEntry); 
    } 
    return null;
  }
  
  private List<JarEntryInfo> findJarEntries(String sName) {
    List<JarEntryInfo> lst = new ArrayList<>();
    for (JarFileInfo jarFileInfo : this.lstJarFile) {
      JarFile jarFile = jarFileInfo.jarFile;
      JarEntry jarEntry = jarFile.getJarEntry(sName);
      if (jarEntry != null)
        lst.add(new JarEntryInfo(jarFileInfo, jarEntry)); 
    } 
    return lst;
  }
  
  private JarEntryInfo findJarNativeEntry(String sLib) {
    String sName = System.mapLibraryName(sLib);
    for (JarFileInfo jarFileInfo : this.lstJarFile) {
      JarFile jarFile = jarFileInfo.jarFile;
      Enumeration<JarEntry> en = jarFile.entries();
      while (en.hasMoreElements()) {
        JarEntry je = en.nextElement();
        if (je.isDirectory())
          continue; 
        String sEntry = je.getName();
        String[] token = sEntry.split("/");
        if (token.length > 0 && token[token.length - 1].equals(sName)) {
          logInfo(LogArea.NATIVE, "Loading native library '%s' found as '%s' in JAR %s", new Object[] { sLib, sEntry, jarFileInfo.simpleName });
          return new JarEntryInfo(jarFileInfo, je);
        } 
      } 
    } 
    return null;
  }
  
  private Class<?> findJarClass(String sClassName) throws JarClassLoaderException {
    Class<?> c = this.hmClass.get(sClassName);
    if (c != null)
      return c; 
    String sName = String.valueOf(sClassName.replace('.', '/')) + ".class";
    JarEntryInfo inf = findJarEntry(sName);
    String jarSimpleName = null;
    if (inf != null) {
      jarSimpleName = inf.jarFileInfo.simpleName;
      definePackage(sClassName, inf);
      byte[] a_by = inf.getJarBytes();
      try {
        c = defineClass(sClassName, a_by, 0, a_by.length, inf.jarFileInfo.pd);
      } catch (ClassFormatError e) {
        throw new JarClassLoaderException(null, e);
      } 
    } 
    if (c == null)
      throw new JarClassLoaderException(sClassName); 
    this.hmClass.put(sClassName, c);
    logInfo(LogArea.CLASS, "Loaded %s by %s from JAR %s", new Object[] { sClassName, getClass().getName(), jarSimpleName });
    return c;
  }
  
  private void checkShading() {
    if (this.logLevel.ordinal() < LogLevel.WARN.ordinal())
      return; 
    Map<String, JarFileInfo> hm = new HashMap<>();
    for (JarFileInfo jarFileInfo : this.lstJarFile) {
      JarFile jarFile = jarFileInfo.jarFile;
      Enumeration<JarEntry> en = jarFile.entries();
      while (en.hasMoreElements()) {
        JarEntry je = en.nextElement();
        if (je.isDirectory())
          continue; 
        String sEntry = je.getName();
        if ("META-INF/MANIFEST.MF".equals(sEntry))
          continue; 
        JarFileInfo jar = hm.get(sEntry);
        if (jar == null) {
          hm.put(sEntry, jarFileInfo);
          continue;
        } 
        logWarn(LogArea.JAR, "ENTRY %s IN %s SHADES %s", new Object[] { sEntry, jar.simpleName, jarFileInfo.simpleName });
      } 
    } 
  }
  
  private void shutdown() {
    for (JarFileInfo jarFileInfo : this.lstJarFile) {
      try {
        jarFileInfo.jarFile.close();
      } catch (IOException iOException) {}
      File file = jarFileInfo.fileDeleteOnExit;
      if (file != null && !file.delete())
        this.hsDeleteOnExit.add(file); 
    } 
    File fileCfg = new File(String.valueOf(System.getProperty("user.home")) + 
        File.separator + ".JarClassLoader");
    deleteOldTemp(fileCfg);
    persistNewTemp(fileCfg);
  }
  
  private void deleteOldTemp(File fileCfg) {
    BufferedReader reader = null;
    try {
      int count = 0;
      reader = new BufferedReader(new FileReader(fileCfg));
      String sLine;
      while ((sLine = reader.readLine()) != null) {
        File file = new File(sLine);
        if (!file.exists())
          continue; 
        if (file.delete()) {
          count++;
          continue;
        } 
        this.hsDeleteOnExit.add(file);
      } 
      logDebug(LogArea.CONFIG, "Deleted %d old temp files listed in %s", new Object[] { Integer.valueOf(count), fileCfg.getAbsolutePath() });
    } catch (IOException iOException) {
    
    } finally {
      if (reader != null)
        try {
          reader.close();
        } catch (IOException iOException) {} 
    } 
  }
  
  private void persistNewTemp(File fileCfg) {
    if (this.hsDeleteOnExit.size() == 0) {
      logDebug(LogArea.CONFIG, "No temp file names to persist on exit.", new Object[0]);
      fileCfg.delete();
      return;
    } 
    logDebug(LogArea.CONFIG, "Persisting %d temp file names into %s", new Object[] { Integer.valueOf(this.hsDeleteOnExit.size()), fileCfg.getAbsolutePath() });
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(fileCfg));
      for (File file : this.hsDeleteOnExit) {
        if (!file.delete()) {
          String f = file.getCanonicalPath();
          writer.write(f);
          writer.newLine();
          logWarn(LogArea.JAR, "JVM failed to release %s", new Object[] { f });
        } 
      } 
    } catch (IOException iOException) {
    
    } finally {
      if (writer != null)
        try {
          writer.close();
        } catch (IOException iOException) {} 
    } 
  }
  
  public boolean isLaunchedFromJar() {
    return (this.lstJarFile.size() > 0);
  }
  
  public String getManifestMainClass() {
    Attributes attr = null;
    if (isLaunchedFromJar())
      try {
        Manifest m = ((JarFileInfo)this.lstJarFile.get(0)).jarFile.getManifest();
        attr = m.getMainAttributes();
      } catch (IOException iOException) {} 
    return (attr == null) ? null : attr.getValue(Attributes.Name.MAIN_CLASS);
  }
  
  public void invokeMain(String sClass, String[] args) throws Throwable {
    invokeMain(sClass, "main", args);
  }
  
  public void invokeMain(String sClass, String methodName, String[] args) throws Throwable {
    Class<?> clazz = loadClass(sClass);
    CodeVerifier.verify(clazz);
    logInfo(LogArea.CONFIG, "Launch: %s.main(); Loader: %s", new Object[] { sClass, clazz.getClassLoader() });
    Class[] parameterTypes = { String[].class };
    Method method = clazz.getMethod(methodName, parameterTypes);
    boolean bValidModifiers = false;
    boolean bValidVoid = false;
    if (method != null) {
      method.setAccessible(true);
      int nModifiers = method.getModifiers();
      bValidModifiers = (Modifier.isPublic(nModifiers) && 
        Modifier.isStatic(nModifiers));
      Class<?> clazzRet = method.getReturnType();
      bValidVoid = (clazzRet == void.class);
    } 
    if (method == null || !bValidModifiers || !bValidVoid)
      throw new NoSuchMethodException(
          "The main(String[] args) method in class \"" + sClass + "\" not found."); 
    try {
      method.invoke((Object)null, new Object[] { args });
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    } 
  }
  
  protected synchronized Class<?> loadClass(String sClassName, boolean bResolve) throws ClassNotFoundException {
    logDebug(LogArea.CLASS, "LOADING %s (resolve=%b)", new Object[] { sClassName, Boolean.valueOf(bResolve) });
    Thread.currentThread().setContextClassLoader(this);
    Class<?> c = null;
    try {
      if (getClass().getName().equals(sClassName))
        return JarClassLoader.class; 
      if (isLaunchedFromJar())
        try {
          c = findJarClass(sClassName);
          return c;
        } catch (JarClassLoaderException e) {
          if (e.getCause() == null) {
            logDebug(LogArea.CLASS, "Not found %s in JAR by %s: %s", new Object[] { sClassName, getClass().getName(), e.getMessage() });
          } else {
            logDebug(LogArea.CLASS, "Error loading %s in JAR by %s: %s", new Object[] { sClassName, getClass().getName(), e.getCause() });
          } 
        }  
    } finally {
      if (c != null && bResolve)
        resolveClass(c); 
    } 
  }
  
  protected URL findResource(String sName) {
    logDebug(LogArea.RESOURCE, "findResource: %s", new Object[] { sName });
    if (isLaunchedFromJar()) {
      JarEntryInfo inf = findJarEntry(normalizeResourceName(sName));
      if (inf != null) {
        URL url = inf.getURL();
        logInfo(LogArea.RESOURCE, "found resource: %s", new Object[] { url });
        return url;
      } 
      logInfo(LogArea.RESOURCE, "not found resource: %s", new Object[] { sName });
      return null;
    } 
    return super.findResource(sName);
  }
  
  public Enumeration<URL> findResources(String sName) throws IOException {
    logDebug(LogArea.RESOURCE, "getResources: %s", new Object[] { sName });
    if (isLaunchedFromJar()) {
      List<JarEntryInfo> lstJarEntry = findJarEntries(normalizeResourceName(sName));
      List<URL> lstURL = new ArrayList<>();
      for (JarEntryInfo inf : lstJarEntry) {
        URL url = inf.getURL();
        if (url != null)
          lstURL.add(url); 
      } 
      return Collections.enumeration(lstURL);
    } 
    return super.findResources(sName);
  }
  
  protected String findLibrary(String sLib) {
    logDebug(LogArea.NATIVE, "findLibrary: %s", new Object[] { sLib });
    if (isLaunchedFromJar()) {
      JarEntryInfo inf = findJarNativeEntry(sLib);
      if (inf != null)
        try {
          File file = createTempFile(inf);
          logDebug(LogArea.NATIVE, "Loading native library %s from temp file %s", new Object[] { inf.jarEntry, getFilename4Log(file) });
          this.hsDeleteOnExit.add(file);
          return file.getAbsolutePath();
        } catch (JarClassLoaderException e) {
          logError(LogArea.NATIVE, "Failure to load native library %s: %s", new Object[] { sLib, e.toString() });
        }  
      return null;
    } 
    return super.findLibrary(sLib);
  }
  
  private void definePackage(String sClassName, JarEntryInfo inf) throws IllegalArgumentException {
    int pos = sClassName.lastIndexOf('.');
    String sPackageName = (pos > 0) ? sClassName.substring(0, pos) : "";
    if (getDefinedPackage(sPackageName) == null) {
      JarFileInfo jfi = inf.jarFileInfo;
      definePackage(sPackageName, 
          jfi.getSpecificationTitle(), jfi.getSpecificationVersion(), 
          jfi.getSpecificationVendor(), jfi.getImplementationTitle(), 
          jfi.getImplementationVersion(), jfi.getImplementationVendor(), 
          jfi.getSealURL());
    } 
  }
  
  private String normalizeResourceName(String sName) {
    return sName.replace('\\', '/');
  }
  
  private void chmod777(File file) {
    file.setReadable(true, false);
    file.setWritable(true, false);
    file.setExecutable(true, false);
  }
  
  private String getFilename4Log(File file) {
    if (this.logger != null)
      try {
        return file.getCanonicalPath();
      } catch (IOException e) {
        return file.getAbsolutePath();
      }  
    return null;
  }
  
  private void logDebug(LogArea area, String sMsg, Object... obj) {
    log(LogLevel.DEBUG, area, sMsg, obj);
  }
  
  private void logInfo(LogArea area, String sMsg, Object... obj) {
    log(LogLevel.INFO, area, sMsg, obj);
  }
  
  private void logWarn(LogArea area, String sMsg, Object... obj) {
    log(LogLevel.WARN, area, sMsg, obj);
  }
  
  private void logError(LogArea area, String sMsg, Object... obj) {
    log(LogLevel.ERROR, area, sMsg, obj);
  }
  
  private void log(LogLevel level, LogArea area, String sMsg, Object... obj) {
    if (level.ordinal() <= this.logLevel.ordinal() && (
      this.hsLogArea.contains(LogArea.ALL) || this.hsLogArea.contains(area)))
      this.logger.printf("JarClassLoader-" + level + ": " + sMsg + "\n", obj); 
    if (!this.bLogConsole && level == LogLevel.ERROR)
      System.out.printf("JarClassLoader-" + level + ": " + sMsg + "\n", obj); 
  }
  
  private static class JarFileInfo {
    JarFile jarFile;
    
    String simpleName;
    
    File fileDeleteOnExit;
    
    Manifest mf;
    
    ProtectionDomain pd;
    
    JarFileInfo(JarFile jarFile, String simpleName, JarFileInfo jarFileParent, ProtectionDomain pd, File fileDeleteOnExit) {
      this.simpleName = String.valueOf((jarFileParent == null) ? "" : (String.valueOf(jarFileParent.simpleName) + "!")) + simpleName;
      this.jarFile = jarFile;
      this.pd = pd;
      this.fileDeleteOnExit = fileDeleteOnExit;
      try {
        this.mf = jarFile.getManifest();
      } catch (IOException iOException) {}
      if (this.mf == null)
        this.mf = new Manifest(); 
    }
    
    String getSpecificationTitle() {
      return this.mf.getMainAttributes().getValue(Attributes.Name.SPECIFICATION_TITLE);
    }
    
    String getSpecificationVersion() {
      return this.mf.getMainAttributes().getValue(Attributes.Name.SPECIFICATION_VERSION);
    }
    
    String getSpecificationVendor() {
      return this.mf.getMainAttributes().getValue(Attributes.Name.SPECIFICATION_VENDOR);
    }
    
    String getImplementationTitle() {
      return this.mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_TITLE);
    }
    
    String getImplementationVersion() {
      return this.mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }
    
    String getImplementationVendor() {
      return this.mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
    }
    
    URL getSealURL() {
      String seal = this.mf.getMainAttributes().getValue(Attributes.Name.SEALED);
      if (seal != null)
        try {
          return new URL(seal);
        } catch (MalformedURLException malformedURLException) {} 
      return null;
    }
  }
  
  private static class JarEntryInfo {
    JarClassLoader.JarFileInfo jarFileInfo;
    
    JarEntry jarEntry;
    
    JarEntryInfo(JarClassLoader.JarFileInfo jarFileInfo, JarEntry jarEntry) {
      this.jarFileInfo = jarFileInfo;
      this.jarEntry = jarEntry;
    }
    
    URL getURL() {
      try {
        return new URL("jar:file:" + this.jarFileInfo.jarFile.getName() + "!/" + this.jarEntry);
      } catch (MalformedURLException e) {
        return null;
      } 
    }
    
    String getName() {
      return this.jarEntry.getName().replace('/', '_');
    }
    
    public String toString() {
      return "JAR: " + this.jarFileInfo.jarFile.getName() + " ENTRY: " + this.jarEntry;
    }
    
    byte[] getJarBytes() throws JarClassLoader.JarClassLoaderException {
      DataInputStream dis = null;
      byte[] a_by = null;
      try {
        long lSize = this.jarEntry.getSize();
        if (lSize <= 0L || lSize >= 2147483647L)
          throw new JarClassLoader.JarClassLoaderException(
              "Invalid size " + lSize + " for entry " + this.jarEntry); 
        a_by = new byte[(int)lSize];
        InputStream is = this.jarFileInfo.jarFile.getInputStream(this.jarEntry);
        dis = new DataInputStream(is);
        dis.readFully(a_by);
      } catch (IOException e) {
        throw new JarClassLoader.JarClassLoaderException(null, e);
      } finally {
        if (dis != null)
          try {
            dis.close();
          } catch (IOException iOException) {} 
      } 
      return a_by;
    }
  }
  
  private static class JarClassLoaderException extends Exception {
    JarClassLoaderException(String sMsg) {
      super(sMsg);
    }
    
    JarClassLoaderException(String sMsg, Throwable eCause) {
      super(sMsg, eCause);
    }
    
    String getMessageAll() {
      StringBuilder sb = new StringBuilder();
      for (Throwable e = this; e != null; e = e.getCause()) {
        if (sb.length() > 0)
          sb.append(" / "); 
        String sMsg = e.getMessage();
        if (sMsg == null || sMsg.length() == 0)
          sMsg = e.getClass().getSimpleName(); 
        sb.append(sMsg);
      } 
      return sb.toString();
    }
  }
}
