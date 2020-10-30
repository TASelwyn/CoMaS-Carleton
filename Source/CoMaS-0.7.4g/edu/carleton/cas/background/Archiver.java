/*     */ package edu.carleton.cas.background;
/*     */ 
/*     */ import edu.carleton.cas.constants.Shared;
/*     */ import edu.carleton.cas.exam.Invigilator;
/*     */ import edu.carleton.cas.logging.Logger;
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.logging.Level;
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
/*     */ public abstract class Archiver
/*     */   implements ArchiverInterface
/*     */ {
/*     */   protected final LinkedBlockingQueue<Object> queue;
/*     */   protected Thread thread;
/*     */   protected final AtomicBoolean stopped;
/*     */   protected final AtomicBoolean working;
/*     */   protected final Invigilator login;
/*     */   protected final String target;
/*     */   protected final String type;
/*     */   protected final String name;
/*     */   protected final ArchiverStatistics statistics;
/*     */   
/*     */   public Archiver(Invigilator login, String target, String type, String name) {
/*  44 */     this.statistics = new ArchiverStatistics();
/*  45 */     this.stopped = new AtomicBoolean(false);
/*  46 */     this.working = new AtomicBoolean(false);
/*  47 */     this.queue = new LinkedBlockingQueue();
/*  48 */     this.login = login;
/*  49 */     this.target = target;
/*  50 */     this.type = type;
/*  51 */     this.name = name;
/*  52 */     this.thread = null;
/*  53 */     keepAlive();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  62 */     return this.name;
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
/*     */   public synchronized boolean keepAlive() {
/*  74 */     if (this.thread != null)
/*  75 */       return false; 
/*  76 */     if (isProcessing()) {
/*  77 */       this.statistics.incrementStarts();
/*  78 */       this.thread = new Thread(new Runnable() {
/*     */             public void run() {
/*     */               try {
/*  81 */                 Logger.log(Level.INFO, Archiver.this.name, " service started");
/*  82 */                 Object item = "";
/*  83 */                 while (Archiver.this.isProcessing()) {
/*     */                   
/*  85 */                   try { if (!Archiver.this.working.get())
/*  86 */                       item = Archiver.this.queue.take(); 
/*  87 */                     Archiver.this.doTheWork(item); }
/*  88 */                   catch (InterruptedException interruptedException) {  }
/*  89 */                   catch (IOException e)
/*  90 */                   { Logger.log(Level.WARNING, e.getMessage(), item); }
/*  91 */                   catch (Exception e)
/*  92 */                   { Logger.log(Level.SEVERE, e.getMessage(), item); }
/*     */                 
/*     */                 } 
/*     */               } finally {
/*  96 */                 Archiver.this.logStatistics();
/*  97 */                 Archiver.this.thread = null;
/*     */               } 
/*     */             }
/*     */           });
/* 101 */       return true;
/*     */     } 
/* 103 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void logStatistics() {
/* 110 */     String msg = String.format("%s service stopped. Starts=%d, Processed=%d, Failures=%d", new Object[] { this.name, 
/* 111 */           Integer.valueOf(this.statistics.getTotalStarts()), Integer.valueOf(this.statistics.getTotalProcessed()), 
/* 112 */           Integer.valueOf(this.statistics.getTotalFailures()) });
/* 113 */     Logger.log(Level.INFO, msg, "");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isProcessing() {
/* 124 */     if (!this.stopped.get())
/* 125 */       return true; 
/* 126 */     if (this.working.get() || !this.queue.isEmpty())
/* 127 */       return true; 
/* 128 */     return false;
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
/*     */   private void doTheWork(Object item) throws IOException, InterruptedException {
/* 142 */     this.working.set(true);
/* 143 */     boolean ok = doWork(item);
/* 144 */     if (!ok && this.statistics.getFailures() < Shared.MAX_FAILURES) {
/*     */       try {
/* 146 */         this.statistics.incrementFailures();
/* 147 */         Thread.sleep(this.login.randomInt(0, Shared.RETRY_TIME));
/* 148 */       } catch (InterruptedException e) {
/* 149 */         if (this.stopped.get())
/* 150 */           throw e; 
/*     */       } 
/*     */     } else {
/* 153 */       this.statistics.incrementTotalProcessed();
/* 154 */       this.statistics.resetFailures();
/* 155 */       this.working.set(false);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void start() {
/* 165 */     this.stopped.set(false);
/* 166 */     if (this.thread != null) {
/* 167 */       this.thread.start();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void stop() {
/* 178 */     if (!this.stopped.get()) {
/* 179 */       this.stopped.set(true);
/*     */       
/* 181 */       if (this.queue.isEmpty() && !this.working.get()) {
/* 182 */         Logger.output(String.valueOf(this.name) + " queue is empty and no in-progress work. Failures: " + this.statistics.getTotalFailures());
/* 183 */         logStatistics();
/*     */         return;
/*     */       } 
/* 186 */       if (this.thread != null) {
/* 187 */         this.thread.interrupt();
/*     */         try {
/* 189 */           if (this.thread != null)
/* 190 */             this.thread.join(Shared.MAX_MSECS_TO_WAIT_TO_END); 
/* 191 */         } catch (InterruptedException interruptedException) {}
/*     */       } 
/*     */ 
/*     */       
/* 195 */       this.working.set(false);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void put(Object item) {
/* 205 */     if (this.stopped.get() || this.statistics.getFailures() > Shared.MAX_FAILURES)
/*     */       return; 
/*     */     try {
/* 208 */       this.queue.put(item);
/* 209 */     } catch (InterruptedException interruptedException) {}
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\background\Archiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */