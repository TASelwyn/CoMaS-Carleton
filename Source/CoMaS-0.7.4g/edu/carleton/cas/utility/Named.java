package edu.carleton.cas.utility;

public abstract class Named {
  public static String canonical(String str) {
    return str.replace(' ', '-');
  }
  
  public static String quoted(String str) {
    if (str.charAt(0) != '"')
      return String.format("\"%s\"", new Object[] { str }); 
    return str;
  }
  
  public static String unquoted(String str) {
    if (str.charAt(0) == '"')
      return str.substring(1, str.length() - 1); 
    return str;
  }
}
