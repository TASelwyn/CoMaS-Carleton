package edu.carleton.cas.security;

public class IllegalVersionException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public IllegalVersionException(String msg) {
    super(msg);
  }
}
