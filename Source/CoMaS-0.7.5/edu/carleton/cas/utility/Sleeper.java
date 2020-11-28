package edu.carleton.cas.utility;

import java.util.Random;

public abstract class Sleeper {
  private static Random random = new Random();
  
  public static void sleep(int millis, int extraRandomMillis) {
    int randomMillis = random.nextInt(extraRandomMillis);
    sleep(millis + randomMillis);
  }
  
  public static void sleep(int millis) {
    long now = System.currentTimeMillis();
    long end = now + millis;
    while (end - now > 0L) {
      try {
        Thread.sleep(end - now);
      } catch (InterruptedException interruptedException) {
        continue;
      } finally {
        now = System.currentTimeMillis();
      } 
    } 
  }
  
  public static void sleepAndExit(int millis, int status) {
    sleep(millis);
    System.exit(status);
  }
  
  public static void sleepAndRun(int millis, Runnable runnable) {
    sleep(millis);
    runnable.run();
  }
}
