/*    */ package edu.carleton.cas.utility;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ import java.net.NetworkInterface;
/*    */ import java.net.SocketException;
/*    */ import java.net.UnknownHostException;
/*    */ import java.util.Enumeration;
/*    */ 
/*    */ public class MacAddress
/*    */ {
/*    */   public static String getMACAddress() throws UnknownHostException, SocketException {
/* 12 */     InetAddress localHost = InetAddress.getLocalHost();
/* 13 */     NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
/* 14 */     if (ni != null) {
/* 15 */       byte[] hardwareAddress = ni.getHardwareAddress();
/* 16 */       return convertToMacAddress(hardwareAddress);
/*    */     } 
/* 18 */     return null;
/*    */   }
/*    */   
/*    */   public static String getMACAddress(String host) throws UnknownHostException, SocketException {
/* 22 */     InetAddress localHost = InetAddress.getByName(host);
/* 23 */     NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
/* 24 */     if (ni != null) {
/* 25 */       byte[] hardwareAddress = ni.getHardwareAddress();
/* 26 */       return convertToMacAddress(hardwareAddress);
/*    */     } 
/* 28 */     return null;
/*    */   }
/*    */   
/*    */   public static String getMACAddresses() throws SocketException {
/* 32 */     Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
/* 33 */     StringBuffer buf = new StringBuffer();
/* 34 */     while (ni.hasMoreElements()) {
/* 35 */       buf.append(convertToMacAddress(((NetworkInterface)ni.nextElement()).getHardwareAddress()));
/* 36 */       buf.append(" ");
/*    */     } 
/* 38 */     return buf.toString();
/*    */   }
/*    */   
/*    */   private static String convertToMacAddress(byte[] hardwareAddress) {
/* 42 */     if (hardwareAddress == null) {
/* 43 */       return "";
/*    */     }
/* 45 */     String[] hexadecimal = new String[hardwareAddress.length];
/* 46 */     for (int i = 0; i < hardwareAddress.length; i++) {
/* 47 */       hexadecimal[i] = String.format("%02X", new Object[] { Byte.valueOf(hardwareAddress[i]) });
/*    */     } 
/* 49 */     String macAddress = String.join("-", (CharSequence[])hexadecimal);
/* 50 */     return macAddress;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static void main(String[] args) throws SocketException {
/* 56 */     System.out.println(getMACAddresses());
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\MacAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */