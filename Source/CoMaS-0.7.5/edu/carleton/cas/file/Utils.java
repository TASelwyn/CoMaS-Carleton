package edu.carleton.cas.file;

import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.security.CryptoException;
import edu.carleton.cas.security.CryptoUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Utils {
  public static File unpackArchiveOld(URL url, File targetDir) throws IOException {
    if (!targetDir.exists())
      targetDir.mkdirs(); 
    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
    InputStream in = connection.getInputStream();
    File zip = File.createTempFile("arc", ".zip", targetDir);
    OutputStream out = new BufferedOutputStream(new FileOutputStream(zip));
    copyInputStream(in, out);
    out.close();
    return unpackArchive(zip, targetDir);
  }
  
  public static File unpackArchive(URL url, File targetDir) throws IOException {
    if (!targetDir.exists())
      targetDir.mkdirs(); 
    String zip = targetDir + File.separator + "arc.zip";
    ReadableByteChannel in = Channels.newChannel(url.openStream());
    FileOutputStream os = new FileOutputStream(zip);
    FileChannel out = os.getChannel();
    out.transferFrom(in, 0L, Long.MAX_VALUE);
    os.close();
    out.close();
    File zipFile = new File(zip);
    zipFile.deleteOnExit();
    return unpackArchive(new File(zip), targetDir);
  }
  
  public static File unpackArchive(InputStream is, File targetDir) throws IOException {
    if (!targetDir.exists())
      targetDir.mkdirs(); 
    String zip = targetDir + File.separator + "arc.zip";
    ReadableByteChannel in = Channels.newChannel(is);
    FileOutputStream os = new FileOutputStream(zip);
    FileChannel out = os.getChannel();
    out.transferFrom(in, 0L, Long.MAX_VALUE);
    os.close();
    out.close();
    File zipFile = new File(zip);
    zipFile.deleteOnExit();
    return unpackArchive(new File(zip), targetDir);
  }
  
  public static File unpackArchive(File theFile, File targetDir) throws IOException {
    if (!theFile.exists())
      throw new IOException(String.valueOf(theFile.getAbsolutePath()) + " does not exist"); 
    if (!buildDirectory(targetDir))
      throw new IOException("Could not create directory: " + targetDir); 
    ZipFile zipFile = new ZipFile(theFile);
    for (Enumeration<?> entries = zipFile.entries(); entries.hasMoreElements(); ) {
      ZipEntry entry = (ZipEntry)entries.nextElement();
      if (!entry.getName().startsWith("__MACOSX")) {
        File file = new File(targetDir, String.valueOf(File.separator) + entry.getName());
        if (!buildDirectory(file.getParentFile())) {
          zipFile.close();
          throw new IOException("Could not create directory: " + file.getParentFile());
        } 
        if (!entry.isDirectory()) {
          copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(file)));
          continue;
        } 
        if (!buildDirectory(file)) {
          zipFile.close();
          throw new IOException("Could not create directory: " + file);
        } 
      } 
    } 
    zipFile.close();
    return theFile;
  }
  
  public static void copyInputStream(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[65536];
    int len = in.read(buffer);
    while (len >= 0) {
      out.write(buffer, 0, len);
      len = in.read(buffer);
    } 
    in.close();
    out.close();
  }
  
  public static File getAndStoreFile(InputStream is, String name, File targetDir) throws IOException {
    Logger.output("Saved " + name + " in " + targetDir);
    InputStream in = new BufferedInputStream(is, 65536);
    File file = new File(targetDir, name);
    OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
    copyInputStream(in, out);
    return file;
  }
  
  public static File getAndStoreURL(URL url, File targetDir) throws IOException {
    String name = (new File(url.getFile())).getName();
    Logger.output("Saved " + name + " in " + targetDir);
    InputStream in = new BufferedInputStream(url.openStream(), 65536);
    File file = new File(targetDir, name);
    OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
    copyInputStream(in, out);
    return file;
  }
  
  public static String getURL(URL url) {
    StringBuffer buff = new StringBuffer();
    try {
      URLConnection conn = url.openConnection();
      BufferedReader br = new BufferedReader(
          new InputStreamReader(conn.getInputStream()));
      String inputLine;
      while ((inputLine = br.readLine()) != null)
        buff.append(inputLine); 
      br.close();
    } catch (MalformedURLException e) {
      Logger.debug(Level.WARNING, "UTILS " + url.toString() + ":" + e.getMessage());
    } catch (IOException e) {
      Logger.debug(Level.WARNING, "UTILS " + url.toString() + ":" + e.getMessage());
    } 
    return buff.toString();
  }
  
  public static String getURL(String sURL) {
    StringBuffer buff = new StringBuffer();
    try {
      URL url = new URL(sURL);
      URLConnection conn = url.openConnection();
      BufferedReader br = new BufferedReader(
          new InputStreamReader(conn.getInputStream()));
      String inputLine;
      while ((inputLine = br.readLine()) != null)
        buff.append(inputLine); 
      br.close();
    } catch (MalformedURLException e) {
      Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
    } catch (IOException e) {
      Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
    } 
    return buff.toString();
  }
  
  public static Properties getPropertiesFromFile(String name) {
    Properties p;
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(new File(name));
      p = new Properties();
      p.load(fis);
    } catch (IOException e) {
      p = null;
    } finally {
      try {
        if (fis != null)
          fis.close(); 
      } catch (IOException iOException) {}
    } 
    return p;
  }
  
  public static boolean savePropertiesToFile(Properties p, String comments, String name) {
    boolean rtn;
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(new File(name));
      p.store(fos, comments);
      rtn = true;
    } catch (IOException e) {
      rtn = false;
    } finally {
      try {
        if (fos != null)
          fos.close(); 
      } catch (IOException iOException) {}
    } 
    return rtn;
  }
  
  public static Properties getProperties(String sURL, String property, String cookie) {
    Properties properties = new Properties();
    try {
      URL url = new URL(sURL);
      URLConnection conn = url.openConnection();
      conn.setRequestProperty(property, cookie);
      properties.load(conn.getInputStream());
    } catch (MalformedURLException e) {
      Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
    } catch (IOException e) {
      Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
    } 
    return properties;
  }
  
  public static Properties getProperties(String sURL) {
    Properties properties = new Properties();
    try {
      URL url = new URL(sURL);
      URLConnection conn = url.openConnection();
      properties.load(conn.getInputStream());
    } catch (MalformedURLException e) {
      Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
    } catch (IOException e) {
      Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
    } 
    return properties;
  }
  
  public static Properties getEncryptedProperties(String sURL, String key) {
    Properties properties = new Properties();
    try {
      URL url = new URL(sURL);
      URLConnection conn = url.openConnection();
      ByteArrayOutputStream bos = new ByteArrayOutputStream(65536);
      CryptoUtils.decrypt(key, conn.getInputStream(), bos);
      properties.load(new ByteArrayInputStream(bos.toByteArray()));
    } catch (MalformedURLException e) {
      Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
    } catch (IOException e) {
      Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
    } catch (CryptoException e) {
      Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
    } 
    return properties;
  }
  
  public static boolean buildDirectory(File file) {
    return !(!file.exists() && !file.mkdirs());
  }
  
  public static boolean isURLOk(String seURL, String type) {
    try {
      HttpURLConnection url = (HttpURLConnection)(new URL(seURL)).openConnection();
      url.setRequestMethod("HEAD");
      url.connect();
      int responseCode = url.getResponseCode();
      if (type == null)
        return (responseCode == 200); 
      return (responseCode == 200 && type.equals(url.getContentType()));
    } catch (Exception e) {
      return false;
    } 
  }
  
  public static int getIntegerOrDefaultInRange(Properties config, String key, int defaultValue, int min, int max) {
    int value = getIntegerOrDefault(config, key, defaultValue);
    if (value < min || value > max) {
      String msg = String.format("%s outside range (%d,%d), using %d", new Object[] { key, Integer.valueOf(min), Integer.valueOf(max), Integer.valueOf(defaultValue) });
      Logger.log(Level.CONFIG, msg, "");
      return defaultValue;
    } 
    return value;
  }
  
  public static int getIntegerOrDefault(Properties config, String key, int defaultValue) {
    int value;
    String valueAsString = config.getProperty(key);
    if (valueAsString == null)
      return defaultValue; 
    try {
      value = Integer.parseInt(valueAsString);
    } catch (NumberFormatException e) {
      value = defaultValue;
      String msg = String.format("%s is not an integer (%s), using %d", new Object[] { key, valueAsString, Integer.valueOf(defaultValue) });
      Logger.log(Level.CONFIG, msg, "");
    } 
    return value;
  }
  
  public static float getFloatOrDefaultInRange(Properties config, String key, float defaultValue, float min, float max) {
    float value = getFloatOrDefault(config, key, defaultValue);
    if (value < min || value > max) {
      String msg = String.format("%s outside range (%.02f,%.02f), using %.02f", new Object[] { key, Float.valueOf(min), Float.valueOf(max), Float.valueOf(defaultValue) });
      Logger.log(Level.CONFIG, msg, "");
      return defaultValue;
    } 
    return value;
  }
  
  public static float getFloatOrDefault(Properties config, String key, float defaultValue) {
    float value;
    String valueAsString = config.getProperty(key);
    if (valueAsString == null)
      return defaultValue; 
    try {
      value = Float.parseFloat(valueAsString);
    } catch (NumberFormatException e) {
      value = defaultValue;
      String msg = String.format("%s is not a real number (%s), using %.02f", new Object[] { key, valueAsString, Float.valueOf(defaultValue) });
      Logger.log(Level.CONFIG, msg, "");
    } 
    return value;
  }
  
  public static String getStringOrDefault(Properties config, String key, String defaultValue) {
    String valueAsString = config.getProperty(key);
    if (valueAsString == null)
      return defaultValue; 
    return valueAsString.trim();
  }
  
  public static boolean getBooleanOrDefault(Properties config, String key, boolean defaultValue) {
    String valueAsString = config.getProperty(key);
    if (valueAsString == null)
      return defaultValue; 
    valueAsString = valueAsString.trim().toLowerCase();
    if (valueAsString.equals("true"))
      return true; 
    if (valueAsString.equals("false"))
      return false; 
    String msg = String.format("%s not a boolean (%s), using %b", new Object[] { key, valueAsString, Boolean.valueOf(defaultValue) });
    Logger.log(Level.CONFIG, msg, "");
    return defaultValue;
  }
}
