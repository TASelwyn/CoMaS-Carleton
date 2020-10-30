/*     */ package org.eclipse.jdt.internal.jarinjarloader;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.Manifest;
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
/*     */ public class JarRsrcLoader
/*     */ {
/*     */   static Class class$0;
/*     */   
/*     */   private static class ManifestInfo
/*     */   {
/*     */     String rsrcMainClass;
/*     */     String[] rsrcClassPath;
/*     */     
/*     */     private ManifestInfo() {}
/*     */     
/*     */     ManifestInfo(ManifestInfo param1ManifestInfo) {
/*  37 */       this();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void main(String[] args) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException, IOException {
/*  43 */     ManifestInfo mi = getManifestInfo();
/*  44 */     ClassLoader cl = Thread.currentThread().getContextClassLoader();
/*  45 */     URL.setURLStreamHandlerFactory(new RsrcURLStreamHandlerFactory(cl));
/*  46 */     URL[] rsrcUrls = new URL[mi.rsrcClassPath.length];
/*  47 */     for (int i = 0; i < mi.rsrcClassPath.length; i++) {
/*  48 */       String rsrcPath = mi.rsrcClassPath[i];
/*  49 */       if (rsrcPath.endsWith("/")) {
/*  50 */         rsrcUrls[i] = new URL("rsrc:" + rsrcPath);
/*     */       } else {
/*  52 */         rsrcUrls[i] = new URL("jar:rsrc:" + rsrcPath + "!/");
/*     */       } 
/*  54 */     }  ClassLoader jceClassLoader = new URLClassLoader(rsrcUrls, getParentClassLoader());
/*  55 */     Thread.currentThread().setContextClassLoader(jceClassLoader);
/*  56 */     Class c = Class.forName(mi.rsrcMainClass, true, jceClassLoader);
/*  57 */     Method main = c.getMethod("main", new Class[] { args.getClass() });
/*  58 */     main.invoke(null, new Object[] { args });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ClassLoader getParentClassLoader() throws InvocationTargetException, IllegalAccessException {
/*     */     try {
/*  68 */       if (class$0 == null) try {  } catch (ClassNotFoundException classNotFoundException) { throw new NoClassDefFoundError(null.getMessage()); }   Method platformClassLoader = (class$0 = Class.forName("java.lang.ClassLoader")).getMethod("getPlatformClassLoader", null);
/*  69 */       return (ClassLoader)platformClassLoader.invoke(null, null);
/*  70 */     } catch (NoSuchMethodException noSuchMethodException) {
/*     */       
/*  72 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static ManifestInfo getManifestInfo() throws IOException {
/*  78 */     Enumeration resEnum = Thread.currentThread().getContextClassLoader().getResources("META-INF/MANIFEST.MF");
/*  79 */     while (resEnum.hasMoreElements()) {
/*     */       try {
/*  81 */         URL url = resEnum.nextElement();
/*  82 */         InputStream is = url.openStream();
/*  83 */         if (is != null) {
/*  84 */           ManifestInfo result = new ManifestInfo(null);
/*  85 */           Manifest manifest = new Manifest(is);
/*  86 */           Attributes mainAttribs = manifest.getMainAttributes();
/*  87 */           result.rsrcMainClass = mainAttribs.getValue("Rsrc-Main-Class");
/*  88 */           String rsrcCP = mainAttribs.getValue("Rsrc-Class-Path");
/*  89 */           if (rsrcCP == null)
/*  90 */             rsrcCP = ""; 
/*  91 */           result.rsrcClassPath = splitSpaces(rsrcCP);
/*  92 */           if (result.rsrcMainClass != null && !result.rsrcMainClass.trim().equals("")) {
/*  93 */             return result;
/*     */           }
/*     */         } 
/*  96 */       } catch (Exception exception) {}
/*     */     } 
/*     */ 
/*     */     
/* 100 */     System.err.println("Missing attributes for JarRsrcLoader in Manifest (Rsrc-Main-Class, Rsrc-Class-Path)");
/* 101 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String[] splitSpaces(String line) {
/* 112 */     if (line == null)
/* 113 */       return null; 
/* 114 */     List result = new ArrayList();
/* 115 */     int firstPos = 0;
/* 116 */     while (firstPos < line.length()) {
/* 117 */       int lastPos = line.indexOf(' ', firstPos);
/* 118 */       if (lastPos == -1)
/* 119 */         lastPos = line.length(); 
/* 120 */       if (lastPos > firstPos) {
/* 121 */         result.add(line.substring(firstPos, lastPos));
/*     */       }
/* 123 */       firstPos = lastPos + 1;
/*     */     } 
/* 125 */     return result.<String>toArray(new String[result.size()]);
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\org\eclipse\jdt\internal\jarinjarloader\JarRsrcLoader.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */