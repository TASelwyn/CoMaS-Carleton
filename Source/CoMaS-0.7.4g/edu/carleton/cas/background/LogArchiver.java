/*     */ package edu.carleton.cas.background;
/*     */ 
/*     */ import edu.carleton.cas.constants.Shared;
/*     */ import edu.carleton.cas.exam.Invigilator;
/*     */ import edu.carleton.cas.logging.Logger;
/*     */ import edu.carleton.cas.utility.ClientHelper;
/*     */ import edu.carleton.cas.utility.Sleeper;
/*     */ import java.util.HashMap;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogRecord;
/*     */ import javax.ws.rs.client.Client;
/*     */ import javax.ws.rs.client.Entity;
/*     */ import javax.ws.rs.client.Invocation;
/*     */ import javax.ws.rs.client.WebTarget;
/*     */ import javax.ws.rs.core.Form;
/*     */ import javax.ws.rs.core.Response;
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
/*     */ public class LogArchiver
/*     */   extends Archiver
/*     */ {
/*     */   private HashMap<String, Long> logs;
/*     */   
/*     */   public LogArchiver(Invigilator login, String target, String type, String name) {
/*  34 */     super(login, target, type, name);
/*  35 */     this.logs = new HashMap<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void put(Level level, String description) {
/*  42 */     put(new LogRecord(level, description));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean doWork(Object obj) {
/*  51 */     LogRecord item = (LogRecord)obj;
/*  52 */     return log(item.getLevel(), item.getMessage());
/*     */   }
/*     */   
/*     */   public synchronized boolean log(Level severity, String description) {
/*  56 */     if (!this.login.isAllowedToUpload) {
/*  57 */       System.out.println("Not allowed to upload logs");
/*  58 */       return true;
/*     */     } 
/*     */     try {
/*  61 */       Long logged = Long.valueOf(((Long)this.logs.getOrDefault(description, Long.valueOf(0L))).longValue() + 1L);
/*     */       
/*  63 */       if (logged.longValue() % Shared.LOG_GENERATION_FREQUENCY == 1L) {
/*     */         String logMsg;
/*  65 */         if (logged.longValue() > 1L) {
/*  66 */           logMsg = String.valueOf(description) + " (" + logged + ")";
/*     */         } else {
/*  68 */           logMsg = description;
/*     */         } 
/*  70 */         Client client = ClientHelper.createClient(Shared.PROTOCOL);
/*  71 */         WebTarget webTarget = client.target(Shared.BASE_LOG).path(Shared.LOG_PATH);
/*  72 */         Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" });
/*  73 */         invocationBuilder.accept(new String[] { "application/json" });
/*  74 */         Form form = new Form();
/*  75 */         form.param("passkey", Shared.PASSKEY_LOG);
/*  76 */         form.param("name", this.login.name);
/*  77 */         form.param("id", this.login.id);
/*  78 */         form.param("course", this.login.course);
/*  79 */         form.param("activity", this.login.activity);
/*     */ 
/*     */         
/*  82 */         form.param("url", String.valueOf(Shared.LOG_URL) + "s/" + this.login.course + "/" + this.login.activity + "/" + this.login.name);
/*  83 */         form.param("severity", severity.getName());
/*  84 */         form.param("description", logMsg);
/*  85 */         form.param("time", System.currentTimeMillis());
/*  86 */         Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded"));
/*  87 */         Logger.log(severity, "", " [" + response.getStatus() + "] " + logMsg);
/*  88 */         String rtn = (String)response.readEntity(String.class);
/*  89 */         if (rtn.equals("{\"ILLEGAL VERSION\"}")) {
/*  90 */           this.login.alert("Illegal version detected (" + Shared.VERSION + ").\nEnding session");
/*  91 */           Sleeper.sleepAndExit(5000, -5);
/*     */         } 
/*  93 */         this.logs.put(description, logged);
/*  94 */         return (response.getStatus() < 204);
/*     */       } 
/*  96 */       this.logs.put(description, logged);
/*  97 */       return true;
/*     */     
/*     */     }
/* 100 */     catch (Exception e) {
/* 101 */       Logger.log(Level.WARNING, "Failed to log: ", description);
/* 102 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\background\LogArchiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */