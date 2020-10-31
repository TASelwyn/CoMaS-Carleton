package edu.carleton.cas.utility;

public abstract class Sleeper {
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
