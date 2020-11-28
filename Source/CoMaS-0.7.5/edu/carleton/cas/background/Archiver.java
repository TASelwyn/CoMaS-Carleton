package edu.carleton.cas.background;

import edu.carleton.cas.constants.ClientShared;
import edu.carleton.cas.exam.Invigilator;
import edu.carleton.cas.logging.Logger;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public abstract class Archiver implements ArchiverInterface {
  protected final LinkedBlockingQueue<Object> queue;
  
  protected Thread thread;
  
  protected final AtomicBoolean stopped;
  
  protected final AtomicBoolean working;
  
  protected final Invigilator login;
  
  protected final String target;
  
  protected final String type;
  
  protected final String name;
  
  protected final ArchiverStatistics statistics;
  
  public Archiver(Invigilator login, String target, String type, String name) {
    this.statistics = new ArchiverStatistics();
    this.stopped = new AtomicBoolean(false);
    this.working = new AtomicBoolean(false);
    this.queue = new LinkedBlockingQueue();
    this.login = login;
    this.target = target;
    this.type = type;
    this.name = name;
    this.thread = null;
    keepAlive();
  }
  
  public String getName() {
    return this.name;
  }
  
  public synchronized boolean keepAlive() {
    if (this.thread != null)
      return false; 
    if (isProcessing()) {
      this.statistics.incrementStarts();
      this.thread = new Thread(new Runnable() {
            public void run() {
              try {
                Logger.log(Level.INFO, Archiver.this.name, " service started");
                Object item = "";
                while (Archiver.this.isProcessing()) {
                  try {
                    if (!Archiver.this.working.get())
                      item = Archiver.this.queue.take(); 
                    Archiver.this.doTheWork(item);
                  } catch (InterruptedException interruptedException) {
                  
                  } catch (IOException e) {
                    Logger.log(Level.WARNING, String.valueOf(e.toString()) + " ", item);
                  } catch (Exception e) {
                    Logger.log(Level.SEVERE, String.valueOf(e.toString()) + " ", item);
                  } 
                } 
              } finally {
                Archiver.this.logStatistics();
                Archiver.this.thread = null;
              } 
            }
          });
      return true;
    } 
    return false;
  }
  
  private void logStatistics() {
    String msg = String.format("%s service stopped. Starts=%d, Processed=%d, Failures=%d", new Object[] { this.name, 
          Integer.valueOf(this.statistics.getTotalStarts()), Integer.valueOf(this.statistics.getTotalProcessed()), 
          Integer.valueOf(this.statistics.getTotalFailures()) });
    Logger.log(Level.INFO, msg, "");
  }
  
  private boolean isProcessing() {
    if (!this.stopped.get())
      return true; 
    if (this.working.get() || !this.queue.isEmpty())
      return true; 
    return false;
  }
  
  private void doTheWork(Object item) throws IOException, InterruptedException {
    this.working.set(true);
    boolean ok = doWork(item);
    if (!ok && this.statistics.getFailures() < ClientShared.MAX_FAILURES) {
      try {
        this.statistics.incrementFailures();
        Thread.sleep(this.login.randomInt(0, ClientShared.RETRY_TIME));
      } catch (InterruptedException e) {
        if (this.stopped.get())
          throw e; 
      } 
    } else {
      this.statistics.incrementTotalProcessed();
      this.statistics.resetFailures();
      this.working.set(false);
    } 
  }
  
  public synchronized void start() {
    this.stopped.set(false);
    if (this.thread != null)
      this.thread.start(); 
  }
  
  public synchronized void stop() {
    if (!this.stopped.get()) {
      this.stopped.set(true);
      if (this.queue.isEmpty() && !this.working.get()) {
        Logger.output(String.valueOf(this.name) + " queue is empty and no in-progress work. Failures: " + this.statistics.getTotalFailures());
        logStatistics();
        return;
      } 
      if (this.thread != null) {
        this.thread.interrupt();
        try {
          if (this.thread != null)
            this.thread.join(ClientShared.MAX_MSECS_TO_WAIT_TO_END); 
        } catch (InterruptedException interruptedException) {}
      } 
      this.working.set(false);
    } 
  }
  
  public synchronized void put(Object item) {
    if (this.stopped.get() || this.statistics.getFailures() > ClientShared.MAX_FAILURES)
      return; 
    try {
      this.queue.put(item);
    } catch (InterruptedException interruptedException) {}
  }
}
