package edu.carleton.cas.utility;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class MacAddress {
  public static String getMACAddress() throws UnknownHostException, SocketException {
    InetAddress localHost = InetAddress.getLocalHost();
    NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
    if (ni != null) {
      byte[] hardwareAddress = ni.getHardwareAddress();
      return convertToMacAddress(hardwareAddress);
    } 
    return null;
  }
  
  public static String getMACAddress(String host) throws UnknownHostException, SocketException {
    InetAddress localHost = InetAddress.getByName(host);
    NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
    if (ni != null) {
      byte[] hardwareAddress = ni.getHardwareAddress();
      return convertToMacAddress(hardwareAddress);
    } 
    return null;
  }
  
  public static String getMACAddresses() throws SocketException {
    Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
    StringBuffer buf = new StringBuffer();
    while (ni.hasMoreElements()) {
      buf.append(convertToMacAddress(((NetworkInterface)ni.nextElement()).getHardwareAddress()));
      buf.append(" ");
    } 
    return buf.toString();
  }
  
  private static String convertToMacAddress(byte[] hardwareAddress) {
    if (hardwareAddress == null)
      return ""; 
    String[] hexadecimal = new String[hardwareAddress.length];
    for (int i = 0; i < hardwareAddress.length; i++) {
      hexadecimal[i] = String.format("%02X", new Object[] { Byte.valueOf(hardwareAddress[i]) });
    } 
    String macAddress = String.join("-", (CharSequence[])hexadecimal);
    return macAddress;
  }
  
  public static void main(String[] args) throws SocketException {
    System.out.println(getMACAddresses());
  }
}
