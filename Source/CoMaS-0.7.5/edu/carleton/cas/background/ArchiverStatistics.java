package edu.carleton.cas.background;

import java.util.concurrent.atomic.AtomicInteger;

public class ArchiverStatistics {
  protected AtomicInteger failures = new AtomicInteger(0);
  
  protected AtomicInteger totalFailures = new AtomicInteger(0);
  
  protected AtomicInteger totalProcessed = new AtomicInteger(0);
  
  protected AtomicInteger totalStarts = new AtomicInteger(0);
  
  protected int incrementFailures() {
    this.totalFailures.incrementAndGet();
    return this.failures.incrementAndGet();
  }
  
  protected int incrementStarts() {
    return this.totalStarts.incrementAndGet();
  }
  
  protected int incrementTotalProcessed() {
    return this.totalProcessed.incrementAndGet();
  }
  
  protected void resetFailures() {
    this.failures.set(0);
  }
  
  protected int getFailures() {
    return this.failures.get();
  }
  
  protected int getTotalFailures() {
    return this.totalFailures.get();
  }
  
  protected int getTotalProcessed() {
    return this.totalProcessed.get();
  }
  
  protected int getTotalStarts() {
    return this.totalStarts.get();
  }
}
