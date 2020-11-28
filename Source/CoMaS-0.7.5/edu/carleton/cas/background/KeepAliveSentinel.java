package edu.carleton.cas.background;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class KeepAliveSentinel {
  public KeepAliveSentinel() {
    this(300000);
  }
  
  ArrayList<KeepAliveInterface> services = new ArrayList<>();
  
  Timer timer = new Timer();
  
  int timeInMillis;
  
  public KeepAliveSentinel(int timeInMillis) {
    if (timeInMillis < 300000)
      timeInMillis = 300000; 
    this.timeInMillis = timeInMillis;
  }
  
  public synchronized void register(KeepAliveInterface service) {
    this.services.add(service);
  }
  
  public synchronized void deregister(KeepAliveInterface service) {
    this.services.remove(service);
  }
  
  public void start() {
    this.timer.scheduleAtFixedRate(new SentinelTask(null), 60000L, this.timeInMillis);
  }
  
  public void stop() {
    this.timer.cancel();
  }
  
  private class SentinelTask extends TimerTask {
    private SentinelTask() {}
    
    public void run() {
      for (KeepAliveInterface s : KeepAliveSentinel.this.services) {
        if (s.keepAlive())
          s.start(); 
      } 
    }
  }
}
