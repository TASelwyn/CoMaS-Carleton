/*    */ package edu.carleton.cas.modules.foundation;
/*    */ 
/*    */ import edu.carleton.cas.logging.Logger;
/*    */ import java.net.URL;
/*    */ import java.net.URLClassLoader;
/*    */ import java.util.HashSet;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import java.util.logging.Level;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ModuleClassLoader
/*    */   extends URLClassLoader
/*    */ {
/* 30 */   private static HashSet<String> hiddenClasses = new HashSet<>(); static {
/* 31 */     hiddenClasses.add("edu.carleton.cas.exam");
/* 32 */     hiddenClasses.add("edu.carleton.cas.constants");
/* 33 */     hiddenClasses.add("edu.carleton.cas.resources");
/* 34 */     hiddenClasses.add("edu.carleton.cas.modules.foundation");
/*    */   }
/*    */   
/*    */   private final ConcurrentHashMap<String, Class<?>> classes;
/*    */   
/*    */   public ModuleClassLoader(URL[] urls, ClassLoader cl) {
/* 40 */     super(urls, cl);
/* 41 */     this.classes = new ConcurrentHashMap<>();
/*    */   }
/*    */   
/*    */   protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
/* 45 */     for (String hiddenClassName : hiddenClasses) {
/* 46 */       if (name.startsWith(hiddenClassName))
/* 47 */         throw new ClassNotFoundException(String.format("Illegal class access: %s", new Object[] { name })); 
/*    */     } 
/* 49 */     return super.loadClass(name, resolve);
/*    */   }
/*    */   
/*    */   protected Class<?> findClass(String name) throws ClassNotFoundException {
/* 53 */     if (this.classes.containsKey(name)) {
/* 54 */       Logger.log(Level.FINE, "Loading: " + name, " -- cached");
/* 55 */       return this.classes.get(name);
/*    */     } 
/* 57 */     Logger.log(Level.FINE, "Loading: " + name, " -- remote");
/* 58 */     Class<?> clazz = super.findClass(name);
/* 59 */     this.classes.put(name, clazz);
/* 60 */     return clazz;
/*    */   }
/*    */ 
/*    */   
/*    */   public void addURL(URL url) {
/* 65 */     super.addURL(url);
/*    */   }
/*    */   
/*    */   public void addHiddenClass(String name) {
/* 69 */     hiddenClasses.add(name);
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\modules\foundation\ModuleClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */