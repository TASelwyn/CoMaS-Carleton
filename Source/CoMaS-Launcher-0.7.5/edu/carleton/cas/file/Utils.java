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
/*     */   
/*     */   public static void copyInputStream(InputStream in, OutputStream out) throws IOException {
/* 152 */     byte[] buffer = new byte[65536];
/* 153 */     int len = in.read(buffer);
/* 154 */     while (len >= 0) {
/* 155 */       out.write(buffer, 0, len);
/* 156 */       len = in.read(buffer);
/*     */     } 
/* 158 */     in.close();
/* 159 */     out.close();
/*     */   }
/*     */   
/*     */   public static File getAndStoreURL(URL url, File targetDir) throws IOException {
/* 163 */     String name = (new File(url.getFile())).getName();
/* 164 */     Logger.output("Saved " + name + " in " + targetDir);
/* 165 */     BufferedInputStream in = new BufferedInputStream(url.openStream(), 65536);
/* 166 */     File file = new File(targetDir, name);
/* 167 */     OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
/* 168 */     copyInputStream(in, out);
/*     */     
/* 170 */     return file;
/*     */   }
/*     */   
/*     */   public static String getURL(URL url) {
/* 174 */     StringBuffer buff = new StringBuffer();
/*     */     try {
/* 176 */       URLConnection conn = url.openConnection();
/*     */       
/* 178 */       BufferedReader br = new BufferedReader(
/* 179 */           new InputStreamReader(conn.getInputStream()));
/*     */       String inputLine;
/* 181 */       while ((inputLine = br.readLine()) != null) {
/* 182 */         buff.append(inputLine);
/*     */       }
/* 184 */       br.close();
/* 185 */     } catch (MalformedURLException e) {
/* 186 */       Logger.debug(Level.WARNING, "UTILS " + url.toString() + ":" + e.getMessage());
/* 187 */     } catch (IOException e) {
/* 188 */       Logger.debug(Level.WARNING, "UTILS " + url.toString() + ":" + e.getMessage());
/*     */     } 
/* 190 */     return buff.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public static String getURL(String sURL) {
/* 195 */     StringBuffer buff = new StringBuffer();
/*     */     
/*     */     try {
/* 198 */       URL url = new URL(sURL);
/* 199 */       URLConnection conn = url.openConnection();
/*     */       
/* 201 */       BufferedReader br = new BufferedReader(
/* 202 */           new InputStreamReader(conn.getInputStream()));
/*     */       String inputLine;
/* 204 */       while ((inputLine = br.readLine()) != null) {
/* 205 */         buff.append(inputLine);
/*     */       }
/* 207 */       br.close();
/* 208 */     } catch (MalformedURLException e) {
/* 209 */       Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
/* 210 */     } catch (IOException e) {
/* 211 */       Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
/*     */     } 
/* 213 */     return buff.toString();
/*     */   }
/*     */   
/*     */   public static Properties getPropertiesFromFile(String name) {
/*     */     Properties p;
/* 218 */     FileInputStream fis = null;
/*     */     try {
/* 220 */       fis = new FileInputStream(new File(name));
/* 221 */       p = new Properties();
/* 222 */       p.load(fis);
/* 223 */     } catch (IOException e) {
/* 224 */       p = null;
/*     */     } finally {
/*     */       try {
/* 227 */         if (fis != null)
/* 228 */           fis.close(); 
/* 229 */       } catch (IOException iOException) {}
/*     */     } 
/*     */ 
/*     */     
/* 233 */     return p;
/*     */   }
/*     */   
/*     */   public static boolean savePropertiesToFile(Properties p, String comments, String name) {
/*     */     boolean rtn;
/* 238 */     FileOutputStream fos = null;
/*     */     try {
/* 240 */       fos = new FileOutputStream(new File(name));
/* 241 */       p.store(fos, comments);
/* 242 */       rtn = true;
/* 243 */     } catch (IOException e) {
/* 244 */       rtn = false;
/*     */     } finally {
/*     */       try {
/* 247 */         if (fos != null)
/* 248 */           fos.close(); 
/* 249 */       } catch (IOException iOException) {}
/*     */     } 
/*     */     
/* 252 */     return rtn;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Properties getProperties(String sURL) {
/* 257 */     Properties properties = new Properties();
/*     */     try {
/* 259 */       URL url = new URL(sURL);
/* 260 */       URLConnection conn = url.openConnection();
/* 261 */       InputStream is = conn.getInputStream();
/* 262 */       properties.load(is);
/* 263 */       is.close();
/* 264 */     } catch (MalformedURLException e) {
/* 265 */       Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
/* 266 */     } catch (IOException e) {
/* 267 */       Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
/*     */     } 
/* 269 */     return properties;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Properties getProperties(String sURL, String key) {
/* 274 */     Properties properties = new Properties();
/*     */     try {
/* 276 */       URL url = new URL(sURL);
/* 277 */       URLConnection conn = url.openConnection();
/* 278 */       ByteArrayOutputStream bos = new ByteArrayOutputStream(65536);
/* 279 */       InputStream is = conn.getInputStream();
/* 280 */       CryptoUtils.decrypt(key, is, bos);
/* 281 */       is.close();
/* 282 */       properties.load(new ByteArrayInputStream(bos.toByteArray()));
/* 283 */     } catch (MalformedURLException e) {
/* 284 */       Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
/* 285 */     } catch (IOException e) {
/* 286 */       Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
/* 287 */     } catch (CryptoException e) {
/* 288 */       Logger.debug(Level.WARNING, "UTILS " + sURL + ":" + e.getMessage());
/*     */     } 
/* 290 */     return properties;
/*     */   }
/*     */   
/*     */   public static boolean buildDirectory(File file) {
/* 294 */     return !(!file.exists() && !file.mkdirs());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isURLOk(String seURL, String type) {
/*     */     try {
/* 304 */       HttpURLConnection url = (HttpURLConnection)(new URL(seURL)).openConnection();
/* 305 */       url.setRequestMethod("HEAD");
/*     */       
/* 307 */       url.connect();
/*     */ 
/*     */       
/* 310 */       int responseCode = url.getResponseCode();
/*     */       
/* 312 */       if (type == null) {
/* 313 */         return (responseCode == 200);
/*     */       }
/* 315 */       return (responseCode == 200 && type.equals(url.getContentType()));
/* 316 */     } catch (Exception e) {
/* 317 */       return false;
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
/* 333 */     int value = getIntegerOrDefault(config, key, defaultValue);
/* 334 */     if (value < min || value > max) {
/* 335 */       String msg = String.format("%s outside range (%d,%d), using %d", new Object[] { key, Integer.valueOf(min), Integer.valueOf(max), Integer.valueOf(defaultValue) });
/* 336 */       Logger.log(Level.CONFIG, msg, "");
/* 337 */       return defaultValue;
/*     */     } 
/* 339 */     return value;
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
/* 351 */     String valueAsString = config.getProperty(key);
/* 352 */     if (valueAsString == null) {
/* 353 */       return defaultValue;
/*     */     }
/*     */     
/*     */     try {
/* 357 */       value = Integer.parseInt(valueAsString);
/* 358 */     } catch (NumberFormatException e) {
/* 359 */       value = defaultValue;
/* 360 */       String msg = String.format("%s is not an integer (%s), using %d", new Object[] { key, valueAsString, Integer.valueOf(defaultValue) });
/* 361 */       Logger.log(Level.CONFIG, msg, "");
/*     */     } 
/* 363 */     return value;
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
/* 378 */     float value = getFloatOrDefault(config, key, defaultValue);
/* 379 */     if (value < min || value > max) {
/* 380 */       String msg = String.format("%s outside range (%.02f,%.02f), using %.02f", new Object[] { key, Float.valueOf(min), Float.valueOf(max), Float.valueOf(defaultValue) });
/* 381 */       Logger.log(Level.CONFIG, msg, "");
/* 382 */       return defaultValue;
/*     */     } 
/* 384 */     return value;
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
/* 395 */     String valueAsString = config.getProperty(key);
/* 396 */     if (valueAsString == null) {
/* 397 */       return defaultValue;
/*     */     }
/*     */     
/*     */     try {
/* 401 */       value = Float.parseFloat(valueAsString);
/* 402 */     } catch (NumberFormatException e) {
/* 403 */       value = defaultValue;
/* 404 */       String msg = String.format("%s is not a real number (%s), using %.02f", new Object[] { key, valueAsString, Float.valueOf(defaultValue) });
/* 405 */       Logger.log(Level.CONFIG, msg, "");
/*     */     } 
/* 407 */     return value;
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
/* 419 */     String valueAsString = config.getProperty(key);
/* 420 */     if (valueAsString == null) {
/* 421 */       return defaultValue;
/*     */     }
/* 423 */     return valueAsString.trim();
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
/* 436 */     String valueAsString = config.getProperty(key);
/* 437 */     if (valueAsString == null) {
/* 438 */       return defaultValue;
/*     */     }
/* 440 */     valueAsString = valueAsString.trim().toLowerCase();
/* 441 */     if (valueAsString.equals("true"))
/* 442 */       return true; 
/* 443 */     if (valueAsString.equals("false")) {
/* 444 */       return false;
/*     */     }
/* 446 */     String msg = String.format("%s not a boolean (%s), using %b", new Object[] { key, valueAsString, Boolean.valueOf(defaultValue) });
/* 447 */     Logger.log(Level.CONFIG, msg, "");
/* 448 */     return defaultValue;
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\cas\file\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */