package edu.carleton.cas.resources;

import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractResourceMonitor implements Resource {
  protected CopyOnWriteArraySet<ResourceListener> listeners = new CopyOnWriteArraySet<>();
  
  protected String type;
  
  public void notifyListeners(String type, String description) {
    for (ResourceListener l : this.listeners)
      l.resourceEvent(this, type, description); 
  }
  
  public void addListener(ResourceListener listener) {
    this.listeners.add(listener);
  }
  
  public void removeListener(ResourceListener listener) {
    this.listeners.remove(listener);
  }
  
  public String getResourceType() {
    return this.type;
  }
}
