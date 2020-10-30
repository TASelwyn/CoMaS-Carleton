package edu.carleton.cas.modules.foundation;

import edu.carleton.cas.logging.Logger;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class ModuleClassLoader extends URLClassLoader {
  private static HashSet<String> hiddenClasses = new HashSet<>();
  
  private final ConcurrentHashMap<String, Class<?>> classes;
  
  static {
    hiddenClasses.add("edu.carleton.cas.exam");
    hiddenClasses.add("edu.carleton.cas.constants");
    hiddenClasses.add("edu.carleton.cas.resources");
    hiddenClasses.add("edu.carleton.cas.modules.foundation");
  }
  
  public ModuleClassLoader(URL[] urls, ClassLoader cl) {
    super(urls, cl);
    this.classes = new ConcurrentHashMap<>();
  }
  
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    for (String hiddenClassName : hiddenClasses) {
      if (name.startsWith(hiddenClassName))
        throw new ClassNotFoundException(String.format("Illegal class access: %s", new Object[] { name })); 
    } 
    return super.loadClass(name, resolve);
  }
  
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    if (this.classes.containsKey(name)) {
      Logger.log(Level.FINE, "Loading: " + name, " -- cached");
      return this.classes.get(name);
    } 
    Logger.log(Level.FINE, "Loading: " + name, " -- remote");
    Class<?> clazz = super.findClass(name);
    this.classes.put(name, clazz);
    return clazz;
  }
  
  public void addURL(URL url) {
    super.addURL(url);
  }
  
  public void addHiddenClass(String name) {
    hiddenClasses.add(name);
  }
}
