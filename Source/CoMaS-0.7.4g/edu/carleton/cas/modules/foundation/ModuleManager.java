package edu.carleton.cas.modules.foundation;

import edu.carleton.cas.constants.Shared;
import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.modules.CommunicatingModule;
import edu.carleton.cas.modules.Module;
import edu.carleton.cas.modules.ModuleConfigurationFactory;
import edu.carleton.cas.modules.ModuleManagerInterface;
import edu.carleton.cas.modules.exceptions.ModuleException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ModuleManager implements ModuleManagerInterface {
  private static ModuleManager instance = null;
  
  private final ConcurrentHashMap<String, ModuleContainer> modules;
  
  private final ModuleClassLoader loader;
  
  private final ModuleConfigurationImplementation configuration;
  
  private final ThreadPoolExecutor tpe;
  
  private boolean actionInBackground;
  
  public ModuleManager() {
    this(new URL[0]);
  }
  
  public ModuleManager(URL[] urls) {
    this.configuration = new ModuleConfigurationImplementation();
    this.loader = new ModuleClassLoader(urls, Module.class.getClassLoader());
    this.modules = new ConcurrentHashMap<>();
    this.tpe = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.HOURS, new LinkedBlockingQueue<>());
    this.actionInBackground = false;
  }
  
  public static ModuleManager getInstance() {
    if (instance == null) {
      instance = new ModuleManager();
      ModuleConfigurationFactory.setDefault(instance.configuration);
    } 
    return instance;
  }
  
  public void stop() {
    try {
      this.loader.close();
    } catch (IOException iOException) {}
    Enumeration<ModuleContainer> emc = this.modules.elements();
    while (emc.hasMoreElements()) {
      ModuleContainer mc = emc.nextElement();
      try {
        execute(mc, ModuleAction.stop);
        Logger.log(Level.CONFIG, 
            String.format("Stopped module %s using %s", new Object[] { mc.getName(), mc.getModule().getClass() }), "");
      } catch (Exception e) {
        Logger.log(Level.WARNING, String.format("Exception while stopping module %s using %s ", new Object[] { mc.getName(), 
                mc.getModule().getClass() }), e.toString());
      } 
    } 
    this.tpe.shutdownNow();
  }
  
  public ModuleContainer load(String name, String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    if (this.modules.contains(name)) {
      Logger.log(Level.WARNING, String.format("Module named %s is already defined. It cannot be instantiated for %s", new Object[] { name, 
              className }), "");
      throw new InstantiationException(String.format("Module named %s already defined", new Object[] { name }));
    } 
    ModuleContainer module = null;
    Class<?> clazz = this.loader.findClass(className);
    if (clazz != null) {
      module = instantiate(name, clazz);
      this.modules.put(name, module);
    } else {
      ClassNotFoundException cnfe = new ClassNotFoundException("Unable to load " + className);
      Logger.log(Level.WARNING, String.format("Unable to load %s using %s: ", new Object[] { name, 
              className }), cnfe.getClass().getSimpleName());
      throw cnfe;
    } 
    return module;
  }
  
  public boolean unload(ModuleContainer module) {
    try {
      execute(module, ModuleAction.stop);
      this.modules.remove(module.getName());
      return true;
    } catch (Exception e) {
      Logger.log(Level.WARNING, String.format("Exception while unloading module %s using %s: ", new Object[] { module.getName(), 
              module.getModule().getClass() }), e.toString());
      return false;
    } 
  }
  
  public boolean unload(String name) {
    ModuleContainer mc = this.modules.get(name);
    if (mc != null)
      return unload(mc); 
    return false;
  }
  
  private ModuleContainer instantiate(String name, Class<?> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    ModuleContainer module = new ModuleContainer(name, clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]), this);
    execute(module, ModuleAction.init);
    return module;
  }
  
  public void addURL(URL url) {
    this.loader.addURL(url);
  }
  
  public void addSharedProperty(String name, Object value) {
    this.configuration.setProperty(name, value);
  }
  
  public void execute(ModuleContainer moduleContainer, ModuleAction action) {
    if (this.actionInBackground) {
      this.tpe.execute(new ModuleContainerProcessor(moduleContainer, action));
    } else {
      try {
        moduleContainer.execute(action);
      } catch (Exception e) {
        Logger.log(Level.FINE, String.format("%s module %s action exception: ", new Object[] { moduleContainer.getName(), action }), e);
      } 
    } 
  }
  
  public void configure(Properties properties) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    Logger.log(Level.INFO, "Loading modules", "");
    String number = properties.getProperty("module.pool.size", "1");
    try {
      int actualNumber = Integer.parseInt(number.trim());
      if (actualNumber < 1)
        actualNumber = 1; 
      this.tpe.setMaximumPoolSize(actualNumber);
    } catch (NumberFormatException e) {
      Logger.log(Level.WARNING, String.format("module.pool.size has an illegal number format: %s", new Object[] { number }), "");
    } 
    number = properties.getProperty("module.pool.core", "1");
    try {
      int actualNumber = Integer.parseInt(number.trim());
      if (actualNumber > this.tpe.getMaximumPoolSize())
        actualNumber = this.tpe.getMaximumPoolSize(); 
      if (actualNumber < 1)
        actualNumber = 1; 
      this.tpe.setCorePoolSize(actualNumber);
    } catch (NumberFormatException e) {
      Logger.log(Level.WARNING, String.format("module.pool.core has an illegal number format: %s", new Object[] { number }), "");
    } 
    number = properties.getProperty("module.pool.timeout", "60");
    try {
      int actualNumber = Integer.parseInt(number.trim());
      if (actualNumber < 0)
        actualNumber = 60; 
      this.tpe.setKeepAliveTime(actualNumber, TimeUnit.MINUTES);
    } catch (NumberFormatException e) {
      Logger.log(Level.WARNING, String.format("module.pool.timeout has an illegal number format: %s", new Object[] { number }), "");
    } 
    number = properties.getProperty("module.background", "false").trim();
    if (number.equalsIgnoreCase("true")) {
      this.actionInBackground = true;
    } else if (number.equalsIgnoreCase("false")) {
      this.actionInBackground = false;
    } else {
      Logger.log(Level.WARNING, String.format("module.background has an illegal boolean format: %s", new Object[] { number }), "");
    } 
    for (Map.Entry<Object, Object> entry : properties.entrySet())
      this.configuration.setProperty((String)entry.getKey(), entry.getValue()); 
    this.configuration.setProperty("manager", new ModuleManagerBridge(this));
    int i = 1;
    String urlProp = properties.getProperty("module.load.url." + i);
    while (urlProp != null) {
      urlProp = urlProp.trim();
      if (urlProp.startsWith("/"))
        urlProp = Shared.service(Shared.PROTOCOL, Shared.CMS_HOST, Shared.PORT, urlProp); 
      try {
        addURL(new URL(urlProp));
      } catch (MalformedURLException urlException) {
        Logger.log(Level.WARNING, String.format("module.load.url.%d has an illegal URL format: %s", new Object[] { Integer.valueOf(i), urlProp }), "");
        throw urlException;
      } 
      i++;
      urlProp = properties.getProperty("module.load.url." + i);
    } 
    i = 1;
    String hiddenClassProp = properties.getProperty("module.load.hidden." + i);
    Pattern p = Pattern.compile("([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*");
    while (hiddenClassProp != null) {
      hiddenClassProp = hiddenClassProp.trim();
      Matcher m = p.matcher(hiddenClassProp);
      if (m.matches()) {
        this.loader.addHiddenClass(hiddenClassProp);
      } else {
        Logger.log(Level.WARNING, String.format("module.load.hidden.%d has an illegal class name format: %s", new Object[] { Integer.valueOf(i), hiddenClassProp }), "");
      } 
      i++;
      hiddenClassProp = properties.getProperty("module.load.hidden." + i);
    } 
    i = 1;
    String module = properties.getProperty("module.load." + i);
    while (module != null) {
      ModuleContainer mc;
      module = module.trim();
      String[] definition = module.split(",");
      if (definition != null && definition.length == 1) {
        mc = load(definition[0].trim(), definition[0].trim());
      } else if (definition != null && definition.length == 2) {
        mc = load(definition[0].trim(), definition[1].trim());
      } else {
        Logger.log(Level.WARNING, String.format("module.load.%d has an illegal specification format: %s", new Object[] { Integer.valueOf(i), module }), "");
        throw new InstantiationException("Module properties are incorrectly defined: " + module);
      } 
      if (mc != null) {
        execute(mc, ModuleAction.start);
        Logger.log(Level.INFO, 
            String.format("Started module %s using %s", new Object[] { mc.getName(), mc.getModule().getClass() }), "");
      } 
      i++;
      module = properties.getProperty("module.load." + i);
    } 
  }
  
  public Module find(String name) {
    ModuleContainer mc = this.modules.get(name);
    if (mc != null)
      return mc.getModule(); 
    return null;
  }
  
  public void send(String from, String to, String message) throws ModuleException {
    ModuleContainer mc = this.modules.get(to);
    if (mc != null) {
      Class<?> clazz = mc.getModule().getClass();
      if (CommunicatingModule.class.isAssignableFrom(clazz)) {
        CommunicatingModule cm = (CommunicatingModule)mc.getModule();
        try {
          this.tpe.execute(new MessageProcessor(cm, from, message));
        } catch (Exception e) {
          String msg = String.format("Send from %s to %s error: ", new Object[] { from, to });
          Logger.log(Level.WARNING, msg, e);
          throw new ModuleException(msg, e);
        } 
      } else {
        Logger.log(Level.WARNING, to, " is not a communicating module");
        throw new ModuleException(String.valueOf(to) + " is not a communicating module", new ClassCastException(clazz.getSimpleName()));
      } 
    } else {
      Logger.log(Level.FINE, to, " not found");
      throw new ModuleException(String.valueOf(to) + " not found", new NullPointerException());
    } 
  }
  
  private class MessageProcessor implements Runnable {
    private CommunicatingModule module;
    
    private String from;
    
    private String message;
    
    MessageProcessor(CommunicatingModule module, String from, String message) {
      this.module = module;
      this.from = from;
      this.message = message;
    }
    
    public void run() {
      try {
        this.module.receive(this.from, this.message);
      } catch (Exception e) {
        Logger.log(Level.FINE, String.format("%s %s: ", new Object[] { this.from, this.message }), e);
      } 
    }
  }
  
  private class ModuleContainerProcessor implements Runnable {
    private ModuleContainer moduleContainer;
    
    private ModuleAction action;
    
    ModuleContainerProcessor(ModuleContainer moduleContainer, ModuleAction action) {
      this.moduleContainer = moduleContainer;
      this.action = action;
    }
    
    public void run() {
      try {
        this.moduleContainer.execute(this.action);
      } catch (Exception e) {
        Logger.log(Level.FINE, String.format("%s module %s action exception: ", new Object[] { this.moduleContainer.getName(), this.action }), e);
      } 
    }
  }
}
