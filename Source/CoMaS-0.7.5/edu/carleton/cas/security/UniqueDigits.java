package edu.carleton.cas.security;

import java.util.HashSet;
import java.util.Random;

public class UniqueDigits {
  public HashSet<String> digits;
  
  public Random random;
  
  public static int UNIQUE_DIGITS_REQUIRED = 15;
  
  public static int LENGTH_REQUIRED = 4;
  
  private int len;
  
  public UniqueDigits() {
    this(LENGTH_REQUIRED);
  }
  
  public UniqueDigits(int len) {
    this.len = len;
    this.digits = new HashSet<>();
    this.random = new Random();
  }
  
  public String unique() {
    String u = "";
    while (true) {
      u = "";
      for (int i = 0; i < this.len; i++)
        u = String.valueOf(u) + this.random.nextInt(10); 
      if (!this.digits.contains(u)) {
        this.digits.add(u);
        return u;
      } 
    } 
  }
  
  public static void main(String[] args) {
    UniqueDigits u = new UniqueDigits();
    for (int i = 0; i < UNIQUE_DIGITS_REQUIRED; i++)
      System.out.println(u.unique()); 
  }
}
