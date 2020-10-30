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
/*  64 */     File[] comasDotJar = dir.listFiles(new FileFilter() {
/*     */           public boolean accept(File file) {
/*  66 */             String name = file.getName();
/*  67 */             if (name.equals("CoMaS.jar")) {
/*  68 */               return false;
/*     */             }
/*  70 */             return (name.startsWith("CoMaS") && name.endsWith(".jar"));
/*     */           }
/*     */         });
/*  73 */     if (comasDotJar != null) {
/*  74 */       for (int i = 0; i < comasDotJar.length; i++) {
/*  75 */         comasDotJar[i].deleteOnExit();
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
/*  89 */     if (this.configuration.containsKey("first_name"))
/*  90 */       Shared.STUDENT_FIRST_NAME = this.configuration.getProperty("first_name").trim(); 
/*  91 */     if (this.configuration.containsKey("last_name"))
/*  92 */       Shared.STUDENT_LAST_NAME = this.configuration.getProperty("last_name").trim(); 
/*  93 */     if (this.configuration.containsKey("id")) {
/*  94 */       Shared.STUDENT_ID = this.configuration.getProperty("id").trim();
/*     */     }
/*  96 */     return !this.configuration.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean save(String comments) {
/* 106 */     return Utils.savePropertiesToFile(this.configuration, comments, this.name);
/*     */   }
/*     */ 
/*     */   
/*     */   private String key(int i) {
/* 111 */     return "comas." + i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasOneHost() {
/* 119 */     if (!hasHost())
/* 120 */       return false; 
/* 121 */     for (int i = 2; i < MAX_HOSTS; i++) {
/* 122 */       if (hasHost(i))
/* 123 */         return false; 
/* 124 */     }  return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasHost() {
/* 134 */     return hasHost(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasHost(int i) {
/* 144 */     return this.configuration.containsKey(key(i));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHost() {
/* 152 */     return getHost(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHost(int i) {
/* 162 */     return this.configuration.getProperty(key(i));
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
/* 175 */     int i = 1;
/* 176 */     ArrayList<String> possibilities = new ArrayList<>();
/*     */     
/* 178 */     if (Shared.LOOK_FOR_SERVICES) {
/* 179 */       possibilities.add(Shared.DIRECTORY_HOST);
/*     */     }
/* 181 */     while (hasHost(i)) {
/* 182 */       String host = getHost(i);
/* 183 */       if (!possibilities.contains(host))
/* 184 */         possibilities.add(host.trim()); 
/* 185 */       i++;
/*     */     } 
/* 187 */     return possibilities.toArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHost(String host) {
/* 196 */     setHost(1, host);
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
/* 207 */     if (i > 0 && i <= MAX_HOSTS && host != null) {
/* 208 */       this.configuration.setProperty(key(i), host);
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
/* 221 */     for (int i = MAX_HOSTS; i > 1; i--) {
/* 222 */       setHost(i, getHost(i - 1));
/*     */     }
/*     */     
/* 225 */     setHost(host);
/*     */ 
/*     */ 
/*     */     
/* 229 */     Object[] hosts = getHosts(); int j;
/* 230 */     for (j = 0; j < hosts.length; j++) {
/* 231 */       setHost(j + 1, hosts[j].toString());
/*     */     }
/* 233 */     for (j = hosts.length; j <= MAX_HOSTS; j++)
/* 234 */       this.configuration.remove(key(j + 1)); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\ClientConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */