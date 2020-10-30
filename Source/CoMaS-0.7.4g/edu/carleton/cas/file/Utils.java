/*     */ package edu.carleton.cas.file;
/*     */ 
/*     */ import edu.carleton.cas.logging.Logger;
/*     */ import edu.carleton.cas.security.CryptoException;
/*     */ import edu.carleton.cas.security.CryptoUtils;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.Level;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
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
/*     */ 
/*     */ public class Utils
/*     */ {
/*     */   public static File unpackArchiveOld(URL url, File targetDir) throws IOException {
/*  60 */     if (!targetDir.exists()) {
/*  61 */       targetDir.mkdirs();
/*     */     }
/*  63 */     HttpURLConnection connection = (HttpURLConnection)url.openConnection();
/*  64 */     InputStream in = connection.getInputStream();
/*     */ 
/*     */     
/*  67 */     File zip = File.createTempFile("arc", ".zip", targetDir);
/*  68 */     OutputStream out = new BufferedOutputStream(new FileOutputStream(zip));
/*  69 */     copyInputStream(in, out);
/*  70 */     out.close();
/*     */ 
/*     */     
/*  73 */     return unpackArchive(zip, targetDir);
/*     */   }
/*     */   
/*     */   public static File unpackArchive(URL url, File targetDir) throws IOException {
/*  77 */     if (!targetDir.exists()) {
/*  78 */       targetDir.mkdirs();
/*     */     }
/*  80 */     String zip = targetDir + File.separator + "arc.zip";
/*  81 */     ReadableByteChannel in = Channels.newChannel(url.openStream());
/*  82 */     FileOutputStream os = new FileOutputStream(zip);
/*  83 */     FileChannel out = os.getChannel();
/*  84 */     out.transferFrom(in, 0L, Long.MAX_VALUE);
/*  85 */     os.close();
/*  86 */     out.close();
/*  87 */     File zipFile = new File(zip);
/*  88 */     zipFile.deleteOnExit();
/*  89 */     return unpackArchive(new File(zip), targetDir);
/*     */   }
/*     */   
/*     */   public static File unpackArchive(InputStream is, File targetDir) throws IOException {
/*  93 */     if (!targetDir.exists()) {
/*  94 */       targetDir.mkdirs();
/*     */     }
/*  96 */     String zip = targetDir + File.separator + "arc.zip";
/*  97 */     ReadableByteChannel in = Channels.newChannel(is);
/*  98 */     FileOutputStream os = new FileOutputStream(zip);
/*  99 */     FileChannel out = os.getChannel();
/* 100 */     out.transferFrom(in, 0L, Long.MAX_VALUE);
/* 101 */     os.close();
/* 102 */     out.close();
/* 103 */     File zipFile = new File(zip);
/* 104 */     zipFile.deleteOnExit();
/* 105 */     return unpackArchive(new File(zip), targetDir);
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
/*     */   public static File unpackArchive(File theFile, File targetDir) throws IOException {
/* 117 */     if (!theFile.exists()) {
/* 118 */       throw new IOException(String.valueOf(theFile.getAbsolutePath()) + " does not exist");
/*     */     }
/* 120 */     if (!buildDirectory(targetDir)) {
/* 121 */       throw new IOException("Could not create directory: " + targetDir);
/*     */     }
/* 123 */     ZipFile zipFile = new ZipFile(theFile);
/*     */     
/* 125 */     for (Enumeration<?> entries = zipFile.entries(); entries.hasMoreElements(); ) {
/* 126 */       ZipEntry entry = (ZipEntry)entries.nextElement();
/*     */       
/* 128 */       if (!entry.getName().startsWith("__MACOSX")) {
/* 129 */         File file = new File(targetDir, String.valueOf(File.separator) + entry.getName());
/*     */         
/* 131 */         if (!buildDirectory(file.getParentFile())) {
/* 132 */           zipFile.close();
/* 133 */           throw new IOException("Could not create directory: " + file.getParentFile());
/*     */         } 
/* 135 */         if (!entry.isDirectory()) {
/* 136 */           copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(file))); continue;
/*     */         } 
/* 138 */         if (!buildDirectory(file)) {
/* 139 */           zipFile.close();
/* 140 */           throw new IOException("Could not create directory: " + file);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 146 */     zipFile.close();
/* 147 */     return theFile;
/*     */   }
/*     */   
/*     */   public static void copyInputStream(InputStream in, OutputStream out) throws IOException {
/* 151 */     byte[] buffer = new byte[65536];
/* 152 */     int len = in.read(buffer);
/* 153 */     while (len >= 0) {
/* 154 */       out.write(buffer, 0, len);
/* 155 */       len = in.read(buffer);
/*     */     } 
/* 157 */     in.close();
/* 158 */     out.close();
/*     */   }
/*     */   
/*     */   public static File getAndStoreURL(URL url, File targetDir) throws IOException {
/* 162 */     String name = (new File(url.getFile())).getName();
/* 163 */     Logger.output("Saved " + name + " in " + targetDir);
/* 164 */     InputStream in = new BufferedInputStream(url.openStream(), 65536);
/* 165 */     File file = new File(targetDir, name);
/* 166 */     OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
/* 167 */     copyInputStream(in, out);
/*     */     
/* 169 */     return file;
/*     */   }
/*     */   
/*     */   public static String getURL(URL url) {
/* 173 */     StringBuffer buff = new StringBuffer();
/*     */     try {
/* 175 */       URLConnection conn = url.openConnection();
/*     */       
/* 177 */       BufferedReader br = new BufferedReader(
/* 178 */           new InputStreamReader(conn.getInputStream()));
/*     */       String inputLine;
/* 180 */       while ((inputLine = br.readLine()) != null) {
/* 181 */         buff.append(inputLine);
/*     */       }
/* 183 */       br.close();
/* 184 */     } catch (MalformedURLException e) {
/* 185 */       Logger.debug(Level.WARNING, "UTILS " + url.toString() + ":" + e.getMessage());
/* 186 */     } catch (IOException e) {
/* 187 */       Logger.debug(Level.WARNING, "UTILS " + url.toString() + ":" + e.getMessage());
/*     */     } 
/* 189 */     return buff.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public static String getURL(String sURL) {
/* 194 */     StringBuffer buff = new StringBuffer();
/*     */     
/*     */     try {
/* 197 */       URL url = new URL(sURL);
/* 198 */       URLConnection conn = url.openConnection();
/*     */       
/* 200 */       BufferedReader br = new BufferedReader(
/* 201 */           new InputStreamReader(conn.getInputStream()));
/*     */       String inputLine;
/* 203 */       while ((inputLine = br.readLine()) != null) {
/* 204 */         buff.append(inputLine);
/*     */       }
/* 206 */       br.close();
/* 207 */     } catch (MalformedURLException e) {
/* 208 */       Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
/* 209 */     } catch (IOException e) {
/* 210 */       Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
/*     */     } 
/* 212 */     return buff.toString();
/*     */   }
/*     */   
/*     */   public static Properties getPropertiesFromFile(String name) {
/*     */     Properties p;
/* 217 */     FileInputStream fis = null;
/*     */     try {
/* 219 */       fis = new FileInputStream(new File(name));
/* 220 */       p = new Properties();
/* 221 */       p.load(fis);
/* 222 */     } catch (IOException e) {
/* 223 */       p = null;
/*     */     } finally {
/*     */       try {
/* 226 */         if (fis != null)
/* 227 */           fis.close(); 
/* 228 */       } catch (IOException iOException) {}
/*     */     } 
/*     */ 
/*     */     
/* 232 */     return p;
/*     */   }
/*     */   
/*     */   public static boolean savePropertiesToFile(Properties p, String comments, String name) {
/*     */     boolean rtn;
/* 237 */     FileOutputStream fos = null;
/*     */     try {
/* 239 */       fos = new FileOutputStream(new File(name));
/* 240 */       p.store(fos, comments);
/* 241 */       rtn = true;
/* 242 */     } catch (IOException e) {
/* 243 */       rtn = false;
/*     */     } finally {
/*     */       try {
/* 246 */         if (fos != null)
/* 247 */           fos.close(); 
/* 248 */       } catch (IOException iOException) {}
/*     */     } 
/*     */     
/* 251 */     return rtn;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Properties getProperties(String sURL) {
/* 256 */     Properties properties = new Properties();
/*     */     try {
/* 258 */       URL url = new URL(sURL);
/* 259 */       URLConnection conn = url.openConnection();
/* 260 */       properties.load(conn.getInputStream());
/* 261 */     } catch (MalformedURLException e) {
/* 262 */       Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
/* 263 */     } catch (IOException e) {
/* 264 */       Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
/*     */     } 
/* 266 */     return properties;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Properties getProperties(String sURL, String key) {
/* 271 */     Properties properties = new Properties();
/*     */     try {
/* 273 */       URL url = new URL(sURL);
/* 274 */       URLConnection conn = url.openConnection();
/* 275 */       ByteArrayOutputStream bos = new ByteArrayOutputStream(65536);
/* 276 */       CryptoUtils.decrypt(key, conn.getInputStream(), bos);
/* 277 */       properties.load(new ByteArrayInputStream(bos.toByteArray()));
/* 278 */     } catch (MalformedURLException e) {
/* 279 */       Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
/* 280 */     } catch (IOException e) {
/* 281 */       Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
/* 282 */     } catch (CryptoException e) {
/* 283 */       Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
/*     */     } 
/* 285 */     return properties;
/*     */   }
/*     */   
/*     */   public static boolean buildDirectory(File file) {
/* 289 */     return !(!file.exists() && !file.mkdirs());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isURLOk(String seURL, String type) {
/*     */     try {
/* 299 */       HttpURLConnection url = (HttpURLConnection)(new URL(seURL)).openConnection();
/* 300 */       url.setRequestMethod("HEAD");
/*     */       
/* 302 */       url.connect();
/*     */ 
/*     */       
/* 305 */       int responseCode = url.getResponseCode();
/*     */       
/* 307 */       if (type == null) {
/* 308 */         return (responseCode == 200);
/*     */       }
/* 310 */       return (responseCode == 200 && type.equals(url.getContentType()));
/* 311 */     } catch (Exception e) {
/* 312 */       return false;
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
/*     */   public static int getIntegerOrDefaultInRange(Properties config, String key, int defaultValue, int min, int max) {
/* 328 */     int value = getIntegerOrDefault(config, key, defaultValue);
/* 329 */     if (value < min || value > max) {
/* 330 */       String msg = String.format("%s outside range (%d,%d), using %d", new Object[] { key, Integer.valueOf(min), Integer.valueOf(max), Integer.valueOf(defaultValue) });
/* 331 */       Logger.log(Level.CONFIG, msg, "");
/* 332 */       return defaultValue;
/*     */     } 
/* 334 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getIntegerOrDefault(Properties config, String key, int defaultValue) {
/*     */     int value;
/* 346 */     String valueAsString = config.getProperty(key);
/* 347 */     if (valueAsString == null) {
/* 348 */       return defaultValue;
/*     */     }
/*     */     
/*     */     try {
/* 352 */       value = Integer.parseInt(valueAsString);
/* 353 */     } catch (NumberFormatException e) {
/* 354 */       value = defaultValue;
/* 355 */       String msg = String.format("%s is not an integer (%s), using %d", new Object[] { key, valueAsString, Integer.valueOf(defaultValue) });
/* 356 */       Logger.log(Level.CONFIG, msg, "");
/*     */     } 
/* 358 */     return value;
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
/*     */   public static float getFloatOrDefaultInRange(Properties config, String key, float defaultValue, float min, float max) {
/* 373 */     float value = getFloatOrDefault(config, key, defaultValue);
/* 374 */     if (value < min || value > max) {
/* 375 */       String msg = String.format("%s outside range (%.02f,%.02f), using %.02f", new Object[] { key, Float.valueOf(min), Float.valueOf(max), Float.valueOf(defaultValue) });
/* 376 */       Logger.log(Level.CONFIG, msg, "");
/* 377 */       return defaultValue;
/*     */     } 
/* 379 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float getFloatOrDefault(Properties config, String key, float defaultValue) {
/*     */     float value;
/* 390 */     String valueAsString = config.getProperty(key);
/* 391 */     if (valueAsString == null) {
/* 392 */       return defaultValue;
/*     */     }
/*     */     
/*     */     try {
/* 396 */       value = Float.parseFloat(valueAsString);
/* 397 */     } catch (NumberFormatException e) {
/* 398 */       value = defaultValue;
/* 399 */       String msg = String.format("%s is not a real number (%s), using %.02f", new Object[] { key, valueAsString, Float.valueOf(defaultValue) });
/* 400 */       Logger.log(Level.CONFIG, msg, "");
/*     */     } 
/* 402 */     return value;
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
/*     */   public static String getStringOrDefault(Properties config, String key, String defaultValue) {
/* 414 */     String valueAsString = config.getProperty(key);
/* 415 */     if (valueAsString == null) {
/* 416 */       return defaultValue;
/*     */     }
/* 418 */     return valueAsString.trim();
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
/*     */   public static boolean getBooleanOrDefault(Properties config, String key, boolean defaultValue) {
/* 431 */     String valueAsString = config.getProperty(key);
/* 432 */     if (valueAsString == null) {
/* 433 */       return defaultValue;
/*     */     }
/* 435 */     valueAsString = valueAsString.trim().toLowerCase();
/* 436 */     if (valueAsString.equals("true"))
/* 437 */       return true; 
/* 438 */     if (valueAsString.equals("false")) {
/* 439 */       return false;
/*     */     }
/* 441 */     String msg = String.format("%s not a boolean (%s), using %b", new Object[] { key, valueAsString, Boolean.valueOf(defaultValue) });
/* 442 */     Logger.log(Level.CONFIG, msg, "");
/* 443 */     return defaultValue;
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\file\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */