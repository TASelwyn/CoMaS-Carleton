/*     */ package edu.carleton.cas.utility;
/*     */ 
/*     */ import edu.carleton.cas.constants.Shared;
/*     */ import edu.carleton.cas.file.Utils;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Properties;
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
/*     */ public class ClientConfiguration
/*     */ {
/*     */   public static final String COMAS_DOT = "comas.";
/*  28 */   public static int MAX_HOSTS = 5;
/*     */   
/*     */   private String name;
/*     */   
/*     */   private Properties configuration;
/*     */   
/*     */   public ClientConfiguration(String name) {
/*  35 */     this.configuration = new Properties();
/*  36 */     this.name = name.trim();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean delete() {
/*  46 */     File f = new File(this.name);
/*     */     
/*  48 */     if (f.exists()) {
/*  49 */       f.deleteOnExit();
/*  50 */       return true;
/*     */     } 
/*  52 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove() {
/*  62 */     File f = new File(this.name);
/*  63 */     File dir = f.getParentFile();
/*  64 */     File[] loginDotJar = dir.listFiles(new FileFilter() {
/*     */           public boolean accept(File file) {
/*  66 */             String name = file.getName();
/*  67 */             if (name.equals("Login.jar")) {
/*  68 */               return false;
/*     */             }
/*  70 */             return (name.startsWith("Login") && name.endsWith(".jar"));
/*     */           }
/*     */         });
/*  73 */     if (loginDotJar != null) {
/*  74 */       for (int i = 0; i < loginDotJar.length; i++) {
/*  75 */         loginDotJar[i].deleteOnExit();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean load() {
/*  84 */     Properties p = Utils.getPropertiesFromFile(this.name);
/*  85 */     if (p == null) {
/*  86 */       return false;
/*     */     }
/*  88 */     this.configuration = p;
/*  89 */     return !this.configuration.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean save(String comments) {
/*  99 */     return Utils.savePropertiesToFile(this.configuration, comments, this.name);
/*     */   }
/*     */ 
/*     */   
/*     */   private String key(int i) {
/* 104 */     return "comas." + i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasOneHost() {
/* 112 */     if (!hasHost())
/* 113 */       return false; 
/* 114 */     for (int i = 2; i < MAX_HOSTS; i++) {
/* 115 */       if (hasHost(i))
/* 116 */         return false; 
/* 117 */     }  return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasHost() {
/* 127 */     return hasHost(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasHost(int i) {
/* 137 */     return this.configuration.containsKey(key(i));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHost() {
/* 145 */     return getHost(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHost(int i) {
/* 155 */     return this.configuration.getProperty(key(i));
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
/*     */   public Object[] getHosts() {
/* 168 */     int i = 1;
/* 169 */     ArrayList<String> possibilities = new ArrayList<>();
/*     */     
/* 171 */     if (Shared.LOOK_FOR_SERVICES) {
/* 172 */       possibilities.add(Shared.DIRECTORY_HOST);
/*     */     }
/* 174 */     while (hasHost(i)) {
/* 175 */       String host = getHost(i);
/* 176 */       if (!possibilities.contains(host))
/* 177 */         possibilities.add(host.trim()); 
/* 178 */       i++;
/*     */     } 
/* 180 */     return possibilities.toArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHost(String host) {
/* 189 */     setHost(1, host);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHost(int i, String host) {
/* 200 */     if (i > 0 && i <= MAX_HOSTS && host != null) {
/* 201 */       this.configuration.setProperty(key(i), host);
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
/*     */   public void setRecentHost(String host) {
/* 214 */     for (int i = MAX_HOSTS; i > 1; i--) {
/* 215 */       setHost(i, getHost(i - 1));
/*     */     }
/*     */     
/* 218 */     setHost(host);
/*     */ 
/*     */ 
/*     */     
/* 222 */     Object[] hosts = getHosts(); int j;
/* 223 */     for (j = 0; j < hosts.length; j++) {
/* 224 */       setHost(j + 1, hosts[j].toString());
/*     */     }
/* 226 */     for (j = hosts.length; j <= MAX_HOSTS; j++)
/* 227 */       this.configuration.remove(key(j + 1)); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\ca\\utility\ClientConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */