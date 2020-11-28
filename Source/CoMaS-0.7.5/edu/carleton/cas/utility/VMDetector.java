package edu.carleton.cas.utility;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public abstract class VMDetector {
  private static byte[][] invalidMacs = new byte[][] { { 0, 5, 105 }, { 0, 28, 20 }, { 0, 12, 41 }, { 0, 80, 86 }, { 8, 39 }, { 10, 39 }, { 0, 3, -1 }, { 0, 21, 93 } };
  
  private static Dimension[] knownResolutions = new Dimension[] { 
      new Dimension(640, 360), new Dimension(800, 600), 
      new Dimension(1024, 768), new Dimension(1280, 720), new Dimension(1280, 800), new Dimension(1280, 1024), 
      new Dimension(1360, 768), new Dimension(1366, 768), new Dimension(1440, 900), new Dimension(1536, 864), 
      new Dimension(1600, 900), new Dimension(1680, 1050), new Dimension(1920, 1080), new Dimension(1920, 1200), 
      new Dimension(2048, 1152), new Dimension(2560, 1080), new Dimension(2560, 1440), new Dimension(3440, 1440), 
      new Dimension(3840, 2160), new Dimension(4096, 2304), 
      new Dimension(5120, 2880), new Dimension(3072, 1920), 
      
      new Dimension(1680, 945), new Dimension(2048, 1152), new Dimension(2304, 1296), new Dimension(2560, 1440) };
  
  public static boolean isVM() throws SocketException {
    return !(!isVMMac() && isStdResolution());
  }
  
  public static boolean isVMMac() throws SocketException {
    Enumeration<NetworkInterface> net = NetworkInterface.getNetworkInterfaces();
    while (net.hasMoreElements()) {
      NetworkInterface element = net.nextElement();
      if (isVMMac(element.getHardwareAddress()))
        return true; 
    } 
    return false;
  }
  
  public static boolean isVMMac(byte[] mac) {
    if (mac == null)
      return false; 
    if (mac.length < 3)
      return false; 
    byte b;
    int i;
    byte[][] arrayOfByte;
    for (i = (arrayOfByte = invalidMacs).length, b = 0; b < i; ) {
      byte[] invalid = arrayOfByte[b];
      if (invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2])
        return true; 
      b++;
    } 
    return false;
  }
  
  public static boolean isStdResolution() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    return isStdResolution(screenSize);
  }
  
  public static boolean isStdResolution(Dimension d) {
    byte b;
    int i;
    Dimension[] arrayOfDimension;
    for (i = (arrayOfDimension = knownResolutions).length, b = 0; b < i; ) {
      Dimension res = arrayOfDimension[b];
      if (res.equals(d))
        return true; 
      b++;
    } 
    return false;
  }
  
  public static void main(String[] args) {
    System.out.println("VMDetector V1.0");
    if (!isStdResolution())
      System.out.println("Resolution is non-standard, possible VM"); 
    try {
      if (isVMMac())
        System.out.println("VM vendor MAC detected, possible VM"); 
    } catch (SocketException e) {
      System.out.println("Could not detect MAC hardware addresses");
    } 
  }
}
