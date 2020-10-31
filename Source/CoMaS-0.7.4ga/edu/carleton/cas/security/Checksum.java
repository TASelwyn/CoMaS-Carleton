package edu.carleton.cas.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class Checksum {
  public static byte[] createChecksum(String filename, String algorithm) throws Exception {
    int numRead;
    InputStream fis = new FileInputStream(filename);
    byte[] buffer = new byte[1024];
    MessageDigest complete = MessageDigest.getInstance(algorithm);
    do {
      numRead = fis.read(buffer);
      if (numRead <= 0)
        continue; 
      complete.update(buffer, 0, numRead);
    } while (numRead != -1);
    fis.close();
    return complete.digest();
  }
  
  private static String byteToString(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bytes.length; i++)
      sb.append(Integer.toString((bytes[i] & 0xFF) + 256, 16).substring(1)); 
    return sb.toString();
  }
  
  public static String getMD5Checksum(String filename) throws Exception {
    byte[] b = createChecksum(filename, "MD5");
    return byteToString(b);
  }
  
  public static String getSHA1Checksum(String filename) throws Exception {
    byte[] b = createChecksum(filename, "SHA-1");
    return byteToString(b);
  }
  
  public static String getSHA256Checksum(String filename) throws Exception {
    byte[] b = createChecksum(filename, "SHA-256");
    return byteToString(b);
  }
  
  public static String getSHA512Checksum(String filename) throws Exception {
    byte[] b = createChecksum(filename, "SHA-512");
    return byteToString(b);
  }
  
  public static void main(String[] args) throws Exception {
    String file;
    if (args.length > 0) {
      file = args[0];
    } else {
      file = String.valueOf(System.getProperty("user.home")) + File.separator + "Desktop" + File.separator + "workspace-latest.zip";
    } 
    System.out.println("Computing hash for " + file + " (" + (new File(file)).length() + ")");
    System.out.println("    MD5: " + getMD5Checksum(file));
    System.out.println("  SHA-1: " + getSHA1Checksum(file));
    System.out.println("SHA-256: " + getSHA256Checksum(file));
    System.out.println("SHA-512: " + getSHA512Checksum(file));
  }
}
