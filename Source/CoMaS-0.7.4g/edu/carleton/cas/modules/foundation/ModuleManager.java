/*     */ package edu.carleton.cas.modules.foundation;
/*     */ 
/*     */ import edu.carleton.cas.constants.Shared;
/*     */ import edu.carleton.cas.logging.Logger;
/*     */ import edu.carleton.cas.modules.CommunicatingModule;
/*     */ import edu.carleton.cas.modules.Module;
/*     */ import edu.carleton.cas.modules.ModuleConfigurationFactory;
/*     */ import edu.carleton.cas.modules.ModuleManagerInterface;
/*     */ import edu.carleton.cas.modules.exceptions.ModuleException;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.logging.Level;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ModuleManager
/*     */   implements ModuleManagerInterface
/*     */ {
/*  53 */   private static ModuleManager instance = null;
/*     */   private final ConcurrentHashMap<String, ModuleContainer> modules;
/*     */   private final ModuleClassLoader loader;
/*     */   private final ModuleConfigurationImplementation configuration;
/*     */   private final ThreadPoolExecutor tpe;
/*     */   private boolean actionInBackground;
/*     */   
/*     */   public ModuleManager() {
/*  61 */     this(new URL[0]);
/*     */   }
/*     */   
/*     */   public ModuleManager(URL[] urls) {
/*  65 */     this.configuration = new ModuleConfigurationImplementation();
/*  66 */     this.loader = new ModuleClassLoader(urls, Module.class.getClassLoader());
/*  67 */     this.modules = new ConcurrentHashMap<>();
/*  68 */     this.tpe = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.HOURS, new LinkedBlockingQueue<>());
/*  69 */     this.actionInBackground = false;
/*     */   }
/*     */   
/*     */   public static ModuleManager getInstance() {
/*  73 */     if (instance == null) {
/*  74 */       instance = new ModuleManager();
/*     */ 
/*     */       
/*  77 */       ModuleConfigurationFactory.setDefault(instance.configuration);
/*     */     } 
/*  79 */     return instance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/*     */     try {
/*  89 */       this.loader.close();
/*  90 */     } catch (IOException iOException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  97 */     Enumeration<ModuleContainer> emc = this.modules.elements();
/*  98 */     while (emc.hasMoreElements()) {
/*  99 */       ModuleContainer mc = emc.nextElement();
/*     */       try {
/* 101 */         execute(mc, ModuleAction.stop);
/* 102 */         Logger.log(Level.CONFIG, 
/* 103 */             String.format("Stopped module %s using %s", new Object[] { mc.getName(), mc.getModule().getClass() }), "");
/* 104 */       } catch (Exception e) {
/* 105 */         Logger.log(Level.WARNING, String.format("Exception while stopping module %s using %s ", new Object[] { mc.getName(), 
/* 106 */                 mc.getModule().getClass() }), e.toString());
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 111 */     this.tpe.shutdownNow();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ModuleContainer load(String name, String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
/* 135 */     if (this.modules.contains(name)) {
/* 136 */       Logger.log(Level.WARNING, String.format("Module named %s is already defined. It cannot be instantiated for %s", new Object[] { name, 
/* 137 */               className }), "");
/* 138 */       throw new InstantiationException(String.format("Module named %s already defined", new Object[] { name }));
/*     */     } 
/*     */     
/* 141 */     ModuleContainer module = null;
/* 142 */     Class<?> clazz = this.loader.findClass(className);
/*     */     
/* 144 */     if (clazz != null) {
/* 145 */       module = instantiate(name, clazz);
/* 146 */       this.modules.put(name, module);
/*     */     } else {
/* 148 */       ClassNotFoundException cnfe = new ClassNotFoundException("Unable to load " + className);
/* 149 */       Logger.log(Level.WARNING, String.format("Unable to load %s using %s: ", new Object[] { name, 
/* 150 */               className }), cnfe.getClass().getSimpleName());
/* 151 */       throw cnfe;
/*     */     } 
/*     */     
/* 154 */     return module;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean unload(ModuleContainer module) {
/*     */     try {
/* 167 */       execute(module, ModuleAction.stop);
/* 168 */       this.modules.remove(module.getName());
/* 169 */       return true;
/* 170 */     } catch (Exception e) {
/* 171 */       Logger.log(Level.WARNING, String.format("Exception while unloading module %s using %s: ", new Object[] { module.getName(), 
/* 172 */               module.getModule().getClass() }), e.toString());
/* 173 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean unload(String name) {
/* 183 */     ModuleContainer mc = this.modules.get(name);
/* 184 */     if (mc != null) {
/* 185 */       return unload(mc);
/*     */     }
/* 187 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ModuleContainer instantiate(String name, Class<?> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
/* 207 */     ModuleContainer module = new ModuleContainer(name, clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]), this);
/* 208 */     execute(module, ModuleAction.init);
/* 209 */     return module;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addURL(URL url) {
/* 218 */     this.loader.addURL(url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addSharedProperty(String name, Object value) {
/* 228 */     this.configuration.setProperty(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void execute(ModuleContainer moduleContainer, ModuleAction action) {
/* 238 */     if (this.actionInBackground) {
/* 239 */       this.tpe.execute(new ModuleContainerProcessor(moduleContainer, action));
/*     */     } else {
/*     */       try {
/* 242 */         moduleContainer.execute(action);
/* 243 */       } catch (Exception e) {
/* 244 */         Logger.log(Level.FINE, String.format("%s module %s action exception: ", new Object[] { moduleContainer.getName(), action }), e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void configure(Properties properties) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
/* 274 */     Logger.log(Level.INFO, "Loading modules", "");
/*     */     
/* 276 */     String number = properties.getProperty("module.pool.size", "1");
/*     */     
/*     */     try {
/* 279 */       int actualNumber = Integer.parseInt(number.trim());
/* 280 */       if (actualNumber < 1)
/* 281 */         actualNumber = 1; 
/* 282 */       this.tpe.setMaximumPoolSize(actualNumber);
/* 283 */     } catch (NumberFormatException e) {
/* 284 */       Logger.log(Level.WARNING, String.format("module.pool.size has an illegal number format: %s", new Object[] { number }), "");
/*     */     } 
/* 286 */     number = properties.getProperty("module.pool.core", "1");
/*     */     try {
/* 288 */       int actualNumber = Integer.parseInt(number.trim());
/* 289 */       if (actualNumber > this.tpe.getMaximumPoolSize())
/* 290 */         actualNumber = this.tpe.getMaximumPoolSize(); 
/* 291 */       if (actualNumber < 1)
/* 292 */         actualNumber = 1; 
/* 293 */       this.tpe.setCorePoolSize(actualNumber);
/* 294 */     } catch (NumberFormatException e) {
/* 295 */       Logger.log(Level.WARNING, String.format("module.pool.core has an illegal number format: %s", new Object[] { number }), "");
/*     */     } 
/* 297 */     number = properties.getProperty("module.pool.timeout", "60");
/*     */     try {
/* 299 */       int actualNumber = Integer.parseInt(number.trim());
/* 300 */       if (actualNumber < 0)
/* 301 */         actualNumber = 60; 
/* 302 */       this.tpe.setKeepAliveTime(actualNumber, TimeUnit.MINUTES);
/* 303 */     } catch (NumberFormatException e) {
/* 304 */       Logger.log(Level.WARNING, String.format("module.pool.timeout has an illegal number format: %s", new Object[] { number }), "");
/*     */     } 
/*     */     
/* 307 */     number = properties.getProperty("module.background", "false").trim();
/* 308 */     if (number.equalsIgnoreCase("true")) {
/* 309 */       this.actionInBackground = true;
/* 310 */     } else if (number.equalsIgnoreCase("false")) {
/* 311 */       this.actionInBackground = false;
/*     */     } else {
/* 313 */       Logger.log(Level.WARNING, String.format("module.background has an illegal boolean format: %s", new Object[] { number }), "");
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 320 */     for (Map.Entry<Object, Object> entry : properties.entrySet())
/*     */     {
/* 322 */       this.configuration.setProperty((String)entry.getKey(), entry.getValue());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 327 */     this.configuration.setProperty("manager", new ModuleManagerBridge(this));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 334 */     int i = 1;
/* 335 */     String urlProp = properties.getProperty("module.load.url." + i);
/* 336 */     while (urlProp != null) {
/* 337 */       urlProp = urlProp.trim();
/* 338 */       if (urlProp.startsWith("/")) {
/* 339 */         urlProp = Shared.service(Shared.PROTOCOL, Shared.CMS_HOST, Shared.PORT, urlProp);
/*     */       }
/*     */       try {
/* 342 */         addURL(new URL(urlProp));
/* 343 */       } catch (MalformedURLException urlException) {
/* 344 */         Logger.log(Level.WARNING, String.format("module.load.url.%d has an illegal URL format: %s", new Object[] { Integer.valueOf(i), urlProp }), "");
/* 345 */         throw urlException;
/*     */       } 
/* 347 */       i++;
/* 348 */       urlProp = properties.getProperty("module.load.url." + i);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 358 */     i = 1;
/* 359 */     String hiddenClassProp = properties.getProperty("module.load.hidden." + i);
/* 360 */     Pattern p = Pattern.compile("([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*");
/* 361 */     while (hiddenClassProp != null) {
/* 362 */       hiddenClassProp = hiddenClassProp.trim();
/* 363 */       Matcher m = p.matcher(hiddenClassProp);
/* 364 */       if (m.matches()) {
/* 365 */         this.loader.addHiddenClass(hiddenClassProp);
/*     */       } else {
/* 367 */         Logger.log(Level.WARNING, String.format("module.load.hidden.%d has an illegal class name format: %s", new Object[] { Integer.valueOf(i), hiddenClassProp }), "");
/* 368 */       }  i++;
/* 369 */       hiddenClassProp = properties.getProperty("module.load.hidden." + i);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 378 */     i = 1;
/* 379 */     String module = properties.getProperty("module.load." + i);
/*     */     
/* 381 */     while (module != null) {
/*     */       ModuleContainer mc;
/* 383 */       module = module.trim();
/* 384 */       String[] definition = module.split(",");
/* 385 */       if (definition != null && definition.length == 1) {
/* 386 */         mc = load(definition[0].trim(), definition[0].trim());
/* 387 */       } else if (definition != null && definition.length == 2) {
/* 388 */         mc = load(definition[0].trim(), definition[1].trim());
/*     */       } else {
/* 390 */         Logger.log(Level.WARNING, String.format("module.load.%d has an illegal specification format: %s", new Object[] { Integer.valueOf(i), module }), "");
/* 391 */         throw new InstantiationException("Module properties are incorrectly defined: " + module);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 398 */       if (mc != null) {
/* 399 */         execute(mc, ModuleAction.start);
/* 400 */         Logger.log(Level.INFO, 
/* 401 */             String.format("Started module %s using %s", new Object[] { mc.getName(), mc.getModule().getClass() }), "");
/*     */       } 
/* 403 */       i++;
/* 404 */       module = properties.getProperty("module.load." + i);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Module find(String name) {
/* 414 */     ModuleContainer mc = this.modules.get(name);
/* 415 */     if (mc != null) {
/* 416 */       return mc.getModule();
/*     */     }
/* 418 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void send(String from, String to, String message) throws ModuleException {
/* 427 */     ModuleContainer mc = this.modules.get(to);
/* 428 */     if (mc != null) {
/* 429 */       Class<?> clazz = mc.getModule().getClass();
/* 430 */       if (CommunicatingModule.class.isAssignableFrom(clazz)) {
/* 431 */         CommunicatingModule cm = (CommunicatingModule)mc.getModule();
/*     */         try {
/* 433 */           this.tpe.execute(new MessageProcessor(cm, from, message));
/* 434 */         } catch (Exception e) {
/* 435 */           String msg = String.format("Send from %s to %s error: ", new Object[] { from, to });
/* 436 */           Logger.log(Level.WARNING, msg, e);
/* 437 */           throw new ModuleException(msg, e);
/*     */         } 
/*     */       } else {
/* 440 */         Logger.log(Level.WARNING, to, " is not a communicating module");
/* 441 */         throw new ModuleException(String.valueOf(to) + " is not a communicating module", new ClassCastException(clazz.getSimpleName()));
/*     */       } 
/*     */     } else {
/* 444 */       Logger.log(Level.FINE, to, " not found");
/* 445 */       throw new ModuleException(String.valueOf(to) + " not found", new NullPointerException());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class MessageProcessor
/*     */     implements Runnable
/*     */   {
/*     */     private CommunicatingModule module;
/*     */     
/*     */     private String from;
/*     */     
/*     */     private String message;
/*     */ 
/*     */     
/*     */     MessageProcessor(CommunicatingModule module, String from, String message) {
/* 462 */       this.module = module;
/* 463 */       this.from = from;
/* 464 */       this.message = message;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/*     */       try {
/* 470 */         this.module.receive(this.from, this.message);
/* 471 */       } catch (Exception e) {
/* 472 */         Logger.log(Level.FINE, String.format("%s %s: ", new Object[] { this.from, this.message }), e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class ModuleContainerProcessor
/*     */     implements Runnable
/*     */   {
/*     */     private ModuleContainer moduleContainer;
/*     */ 
/*     */     
/*     */     private ModuleAction action;
/*     */ 
/*     */     
/*     */     ModuleContainerProcessor(ModuleContainer moduleContainer, ModuleAction action) {
/* 489 */       this.moduleContainer = moduleContainer;
/* 490 */       this.action = action;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/*     */       try {
/* 496 */         this.moduleContainer.execute(this.action);
/* 497 */       } catch (Exception e) {
/* 498 */         Logger.log(Level.FINE, String.format("%s module %s action exception: ", new Object[] { this.moduleContainer.getName(), this.action }), e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\modules\foundation\ModuleManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */