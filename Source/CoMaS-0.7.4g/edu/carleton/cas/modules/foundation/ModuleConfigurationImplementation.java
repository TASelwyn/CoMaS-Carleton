package edu.carleton.cas.modules.foundation;

import edu.carleton.cas.modules.ModuleConfiguration;
import java.util.concurrent.ConcurrentHashMap;

public final class ModuleConfigurationImplementation implements ModuleConfiguration {
  private final ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
  
  public String getStringProperty(String name) {
    return (String)this.map.get(name);
  }
  
  public Object getObjectProperty(String name) {
    return this.map.get(name);
  }
  
  public void setProperty(String name, Object value) {
    this.map.put(name, value);
  }
}
