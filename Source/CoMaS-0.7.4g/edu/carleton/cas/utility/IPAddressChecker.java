/*     */ package edu.carleton.cas.utility;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Properties;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class IPAddressChecker
/*     */ {
/*     */   public static final String address = "address.";
/*     */   public static final String defaultKey = "default";
/*     */   public static final String allow = "allow.";
/*     */   public static final String deny = "deny.";
/*     */   private static boolean default_allow = true;
/*     */   private static boolean default_deny = false;
/*     */   private static final String ip_address_regex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
/*     */   private Properties config;
/*     */   private ArrayList<String> deny_addresses;
/*     */   private ArrayList<String> allow_addresses;
/*     */   private String localHost;
/*     */   
/*     */   public IPAddressChecker(Properties config) throws UnknownHostException {
/*  49 */     this.config = config;
/*  50 */     this.allow_addresses = getAddresses("allow.");
/*  51 */     this.deny_addresses = getAddresses("deny.");
/*     */ 
/*     */     
/*  54 */     String value = config.getProperty(getDefault(), "true");
/*  55 */     if (value.equals("true")) {
/*  56 */       default_allow = true;
/*  57 */     } else if (value.equals("false")) {
/*  58 */       default_allow = false;
/*     */     } 
/*     */ 
/*     */     
/*  62 */     String address = config.getProperty("LOCAL_ADDRESS");
/*  63 */     if (address == null) {
/*  64 */       address = config.getProperty("LOCAL_HOST");
/*  65 */       if (address == null) {
/*  66 */         this.localHost = InetAddress.getLocalHost().getHostAddress();
/*     */       }
/*  68 */       else if (Pattern.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$", address)) {
/*  69 */         this.localHost = address;
/*     */       } else {
/*  71 */         this.localHost = InetAddress.getLocalHost().getHostAddress();
/*     */       }
/*     */     
/*  74 */     } else if (Pattern.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$", address)) {
/*  75 */       this.localHost = address;
/*     */     } else {
/*  77 */       this.localHost = InetAddress.getLocalHost().getHostAddress();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static final String getDefault() {
/*  82 */     return "address.default";
/*     */   }
/*     */   
/*     */   ArrayList<String> getAddresses(String type) {
/*  86 */     int i = 1;
/*  87 */     ArrayList<String> addresses = new ArrayList<>();
/*  88 */     String base = "address." + type;
/*  89 */     String value = this.config.getProperty(String.valueOf(base) + i);
/*  90 */     while (value != null) {
/*  91 */       addresses.add(value.trim());
/*  92 */       i++;
/*  93 */       value = this.config.getProperty(String.valueOf(base) + i);
/*     */     } 
/*  95 */     return addresses;
/*     */   }
/*     */   
/*     */   public boolean allow() {
/*  99 */     for (String address : this.allow_addresses) {
/* 100 */       if (match(address))
/* 101 */         return true; 
/*     */     } 
/* 103 */     return default_allow;
/*     */   }
/*     */   
/*     */   public boolean deny() {
/* 107 */     for (String address : this.deny_addresses) {
/* 108 */       if (match(address))
/* 109 */         return true; 
/*     */     } 
/* 111 */     return default_deny;
/*     */   }
/*     */   
/*     */   public boolean match(String address) {
/* 115 */     if (address.charAt(0) == '^' && address.charAt(address.length() - 1) == '$') {
/* 116 */       Pattern p = Pattern.compile(address);
/* 117 */       Matcher m = p.matcher(this.localHost);
/* 118 */       return m.matches();
/*     */     } 
/* 120 */     return this.localHost.equals(address);
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\IPAddressChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */