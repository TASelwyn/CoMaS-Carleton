/*     */ package edu.carleton.cas.utility;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Toolkit;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.SocketException;
/*     */ import java.util.Enumeration;
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
/*     */ public abstract class VMDetector
/*     */ {
/*  33 */   private static byte[][] invalidMacs = new byte[][] { { 0, 5, 105
/*  34 */       }, { 0, 28, 20
/*  35 */       }, { 0, 12, 41
/*  36 */       }, { 0, 80, 86
/*  37 */       }, { 8, 39
/*  38 */       }, { 10, 39
/*  39 */       }, { 0, 3, -1
/*  40 */       }, { 0, 21, 93 } };
/*     */ 
/*     */   
/*  43 */   private static Dimension[] knownResolutions = new Dimension[] { new Dimension(640, 360), new Dimension(800, 600), 
/*  44 */       new Dimension(1024, 768), new Dimension(1280, 720), new Dimension(1280, 800), new Dimension(1280, 1024), 
/*  45 */       new Dimension(1360, 768), new Dimension(1366, 768), new Dimension(1440, 900), new Dimension(1536, 864), 
/*  46 */       new Dimension(1600, 900), new Dimension(1680, 1050), new Dimension(1920, 1080), new Dimension(1920, 1200), 
/*  47 */       new Dimension(2048, 1152), new Dimension(2560, 1080), new Dimension(2560, 1440), new Dimension(3440, 1440), 
/*  48 */       new Dimension(3840, 2160), new Dimension(4096, 2304), new Dimension(5120, 2880), new Dimension(3072, 1920), 
/*     */       
/*  50 */       new Dimension(1680, 945), new Dimension(2048, 1152), new Dimension(2304, 1296), new Dimension(2560, 1440) };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isVM() throws SocketException {
/*  59 */     return !(!isVMMac() && isStdResolution());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isVMMac() throws SocketException {
/*  68 */     Enumeration<NetworkInterface> net = NetworkInterface.getNetworkInterfaces();
/*  69 */     while (net.hasMoreElements()) {
/*  70 */       NetworkInterface element = net.nextElement();
/*  71 */       if (isVMMac(element.getHardwareAddress()))
/*  72 */         return true; 
/*     */     } 
/*  74 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isVMMac(byte[] mac) {
/*  84 */     if (mac == null)
/*  85 */       return false; 
/*  86 */     if (mac.length < 3)
/*  87 */       return false;  byte b; int i;
/*     */     byte[][] arrayOfByte;
/*  89 */     for (i = (arrayOfByte = invalidMacs).length, b = 0; b < i; ) { byte[] invalid = arrayOfByte[b];
/*  90 */       if (invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2])
/*  91 */         return true; 
/*     */       b++; }
/*     */     
/*  94 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isStdResolution() {
/* 105 */     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
/* 106 */     return isStdResolution(screenSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isStdResolution(Dimension d) {
/*     */     byte b;
/*     */     int i;
/*     */     Dimension[] arrayOfDimension;
/* 117 */     for (i = (arrayOfDimension = knownResolutions).length, b = 0; b < i; ) { Dimension res = arrayOfDimension[b];
/* 118 */       if (res.equals(d))
/* 119 */         return true;  b++; }
/* 120 */      return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/* 128 */     System.out.println("VMDetector V1.0");
/* 129 */     if (!isStdResolution())
/* 130 */       System.out.println("Resolution is non-standard, possible VM"); 
/*     */     try {
/* 132 */       if (isVMMac())
/* 133 */         System.out.println("VM vendor MAC detected, possible VM"); 
/* 134 */     } catch (SocketException e) {
/* 135 */       System.out.println("Could not detect MAC hardware addresses");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\VMDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */