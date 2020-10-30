package edu.carleton.cas.security;

public class CryptoException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public CryptoException() {}
  
  public CryptoException(String message) {
    super(message);
  }
  
  public CryptoException(String message, Throwable throwable) {
    super(message, throwable);
  }
}


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\cas\security\CryptoException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */