/*     */ package edu.carleton.cas.modules.foundation;
/*     */ 
/*     */ import edu.carleton.cas.modules.Module;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ModuleContainer
/*     */   implements Module
/*     */ {
/*     */   private ModuleState state;
/*     */   private final String name;
/*     */   private final Module module;
/*     */   private final ModuleManager manager;
/*     */   
/*     */   public ModuleContainer(String name, Module module, ModuleManager manager) {
/*  22 */     this.name = name;
/*  23 */     this.module = module;
/*  24 */     this.manager = manager;
/*  25 */     this.state = ModuleState.unknown;
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
/*     */   public ModuleContainer load() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
/*  40 */     return this.manager.load(this.name, this.module.getClass().getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unload() {
/*  47 */     this.manager.unload(this);
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
/*     */   public void reload() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
/*  62 */     unload();
/*  63 */     load();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  72 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Module getModule() {
/*  81 */     return this.module;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ModuleManager getManager() {
/*  90 */     return this.manager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean execute(ModuleAction action) {
/* 100 */     if (action == ModuleAction.init)
/* 101 */       return init(); 
/* 102 */     if (action == ModuleAction.start)
/* 103 */       return start(); 
/* 104 */     if (action == ModuleAction.stop)
/* 105 */       return stop(); 
/* 106 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean init() {
/* 111 */     if (this.state == ModuleState.unknown) {
/* 112 */       boolean okay = this.module.init();
/* 113 */       if (okay)
/* 114 */         this.state = ModuleState.init; 
/* 115 */       return okay;
/*     */     } 
/* 117 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean start() {
/* 122 */     if (this.state == ModuleState.init || this.state == ModuleState.stopped) {
/* 123 */       boolean okay = this.module.start();
/* 124 */       if (okay)
/* 125 */         this.state = ModuleState.started; 
/* 126 */       return okay;
/*     */     } 
/* 128 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean stop() {
/* 133 */     if (this.state == ModuleState.init || this.state == ModuleState.started) {
/* 134 */       boolean okay = this.module.stop();
/* 135 */       if (okay)
/* 136 */         this.state = ModuleState.stopped; 
/* 137 */       return okay;
/*     */     } 
/* 139 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\modules\foundation\ModuleContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */