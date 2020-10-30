/*    */ package edu.carleton.cas.modules.foundation;
/*    */ 
/*    */ import edu.carleton.cas.modules.ModuleConfiguration;
/*    */ import java.util.concurrent.ConcurrentHashMap;
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
/*    */ public final class ModuleConfigurationImplementation
/*    */   implements ModuleConfiguration
/*    */ {
/* 23 */   private final ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
/*    */ 
/*    */ 
/*    */   
/*    */   public String getStringProperty(String name) {
/* 28 */     return (String)this.map.get(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object getObjectProperty(String name) {
/* 33 */     return this.map.get(name);
/*    */   }
/*    */   
/*    */   public void setProperty(String name, Object value) {
/* 37 */     this.map.put(name, value);
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\modules\foundation\ModuleConfigurationImplementation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */