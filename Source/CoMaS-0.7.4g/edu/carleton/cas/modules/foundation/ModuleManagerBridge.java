package edu.carleton.cas.modules.foundation;

import edu.carleton.cas.modules.Module;
import edu.carleton.cas.modules.ModuleManagerInterface;
import edu.carleton.cas.modules.exceptions.ModuleException;

public final class ModuleManagerBridge implements ModuleManagerInterface {
  private ModuleManager manager;
  
  public ModuleManagerBridge(ModuleManager manager) {
    this.manager = manager;
  }
  
  public Module find(String name) {
    return this.manager.find(name);
  }
  
  public void send(String from, String to, String message) throws ModuleException {
    this.manager.send(from, to, message);
  }
}
