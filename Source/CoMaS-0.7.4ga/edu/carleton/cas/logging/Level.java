package edu.carleton.cas.logging;

import java.util.logging.Level;

public class Level extends Level {
  private static final long serialVersionUID = 1L;
  
  public static final Level LOGGED = new Level("LOGGED", SEVERE.intValue() + 1);
  
  public static final Level NOTED = new Level("NOTED", WARNING.intValue() + 1);
  
  protected Level(String name, int value) {
    super(name, value);
  }
}
