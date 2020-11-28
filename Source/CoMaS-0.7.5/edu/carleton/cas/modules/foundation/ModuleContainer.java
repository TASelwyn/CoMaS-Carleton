package edu.carleton.cas.modules.foundation;

import edu.carleton.cas.modules.Module;
import java.lang.reflect.InvocationTargetException;

public final class ModuleContainer implements Module {
  private ModuleState state;
  
  private final String name;
  
  private final Module module;
  
  private final ModuleManager manager;
  
  public ModuleContainer(String name, Module module, ModuleManager manager) {
    this.name = name;
    this.module = module;
    this.manager = manager;
    this.state = ModuleState.unknown;
  }
  
  public ModuleContainer load() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    return this.manager.load(this.name, this.module.getClass().getName(), (JarClassLoader)this.module.getClass().getClassLoader());
  }
  
  public void unload() {
    this.manager.unload(this);
  }
  
  public void reload() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    unload();
    load();
  }
  
  public String getName() {
    return this.name;
  }
  
  public Module getModule() {
    return this.module;
  }
  
  public ModuleManager getManager() {
    return this.manager;
  }
  
  public boolean execute(ModuleAction action) {
    if (action == ModuleAction.init)
      return init(); 
    if (action == ModuleAction.start)
      return start(); 
    if (action == ModuleAction.stop)
      return stop(); 
    return false;
  }
  
  public boolean init() {
    if (this.state == ModuleState.unknown) {
      boolean okay = this.module.init();
      if (okay)
        this.state = ModuleState.init; 
      return okay;
    } 
    return false;
  }
  
  public boolean start() {
    if (this.state == ModuleState.init || this.state == ModuleState.stopped) {
      boolean okay = this.module.start();
      if (okay)
        this.state = ModuleState.started; 
      return okay;
    } 
    return false;
  }
  
  public boolean stop() {
    if (this.state == ModuleState.init || this.state == ModuleState.started) {
      boolean okay = this.module.stop();
      if (okay)
        this.state = ModuleState.stopped; 
      return okay;
    } 
    return false;
  }
}
