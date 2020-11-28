package edu.carleton.cas.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Password {
  public static void main(String[] args) throws NoSuchAlgorithmException {
    String passwordToHash = "password";
    byte[] salt = getSalt();
    String securePassword = getSecurePassword(passwordToHash, salt);
    System.out.println(securePassword);
    System.out.println(byteToString(salt));
    byte[] mySalt = stringToByte(byteToString(salt));
    String myPassword = getSecurePassword(passwordToHash, mySalt);
    System.out.println(securePassword.equals(myPassword));
    String ucPassword = getSecurePassword("PASSWORD", salt);
    System.out.println(String.valueOf(ucPassword) + " " + ucPassword.length());
    System.out.println(securePassword.equals(ucPassword));
    System.out.println(authenticator("userid", "password", "fishy@fishface.ca"));
    System.out.println("Passcode = " + getPassCode(8));
    System.out.println("Default Passcode = " + getPassCode());
  }
  
  public static String authenticator(String userid, String passwordToHash) throws NoSuchAlgorithmException {
    byte[] salt = getSalt();
    String securePassword = getSecurePassword(passwordToHash, salt);
    return String.valueOf(userid) + "," + securePassword + "," + byteToString(salt);
  }
  
  public static String authenticator(String userid, String passwordToHash, String other) throws NoSuchAlgorithmException {
    byte[] salt = getSalt();
    String securePassword = getSecurePassword(passwordToHash, salt);
    return String.valueOf(userid) + "," + securePassword + "," + other + "," + byteToString(salt);
  }
  
  public static String byteToString(byte[] input) {
    return Base64.getEncoder().encodeToString(input);
  }
  
  public static byte[] stringToByte(String input) {
    return Base64.getDecoder().decode(input);
  }
  
  public static String getSecurePassword(String passwordToHash, byte[] salt) {
    String generatedPassword = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      md.update(salt);
      byte[] bytes = md.digest(passwordToHash.getBytes());
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < bytes.length; i++)
        sb.append(Integer.toString((bytes[i] & 0xFF) + 256, 16).substring(1)); 
      generatedPassword = sb.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } 
    return generatedPassword;
  }
  
  public static byte[] getSalt() throws NoSuchAlgorithmException {
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    byte[] salt = new byte[16];
    sr.nextBytes(salt);
    return salt;
  }
  
  public static String getPassCode() {
    try {
      return getPassCode(6);
    } catch (NoSuchAlgorithmException e) {
      return null;
    } 
  }
  
  public static String getPassCode(int length) throws NoSuchAlgorithmException {
    if (length < 0)
      return ""; 
    if (length > 32)
      length = 32; 
    StringBuffer buf = new StringBuffer(length);
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    int i = 0;
    while (i < length) {
      char value = (char)(48 + sr.nextInt(75));
      if (value <= '9' || (value >= 'a' && value <= 'z') || (value >= 'A' && value <= 'Z')) {
        buf.append(value);
        i++;
      } 
    } 
    return buf.toString();
  }
}
