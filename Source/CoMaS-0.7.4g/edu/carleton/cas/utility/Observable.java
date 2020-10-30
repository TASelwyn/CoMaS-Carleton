package edu.carleton.cas.utility;

import java.util.concurrent.CopyOnWriteArraySet;

public class Observable {
  private boolean changed = false;
  
  protected CopyOnWriteArraySet<Observer> listeners = new CopyOnWriteArraySet<>();
  
  public boolean isChanged() {
    return this.changed;
  }
  
  public void setChanged() {
    this.changed = true;
  }
  
  public void notifyObservers() {
    notifyObservers(null);
  }
  
  public synchronized void notifyObservers(Object arg) {
    if (isChanged())
      for (Observer l : this.listeners)
        l.update(this, arg);  
    this.changed = false;
  }
  
  public void addObserver(Observer listener) {
    this.listeners.add(listener);
  }
  
  public void removeObserver(Observer listener) {
    this.listeners.remove(listener);
  }
}
