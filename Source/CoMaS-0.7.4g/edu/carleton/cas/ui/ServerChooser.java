/*     */ package edu.carleton.cas.ui;
/*     */ 
/*     */ import edu.carleton.cas.constants.Shared;
/*     */ import edu.carleton.cas.logging.Logger;
/*     */ import edu.carleton.cas.utility.ClientConfiguration;
/*     */ import edu.carleton.cas.utility.IconLoader;
/*     */ import java.awt.Component;
/*     */ import java.io.File;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JOptionPane;
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
/*     */ public class ServerChooser
/*     */ {
/*     */   private static boolean USE_ONLY_HOST_BY_DEFAULT = false;
/*     */   private ClientConfiguration config;
/*     */   private Component component;
/*     */   
/*     */   public ServerChooser(Component component) {
/*  38 */     this.config = new ClientConfiguration(String.valueOf(Shared.DIR) + File.separator + Shared.COMAS_DOT_INI);
/*  39 */     this.component = component;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void open() {
/*  46 */     this.config.load();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/*  53 */     this.config.save("CoMaS server location information. DO NOT EDIT");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String select() {
/*  64 */     String host = choose();
/*  65 */     if (host != null) {
/*  66 */       this.config.setRecentHost(host);
/*  67 */       this.config.save("CoMaS server location information. DO NOT EDIT");
/*     */     } else {
/*  69 */       askToDeleteConfiguration();
/*     */     } 
/*  71 */     return host;
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
/*     */   private String choose() {
/*  86 */     if (Shared.SERVER_CHOSEN != null) {
/*  87 */       return Shared.SERVER_CHOSEN;
/*     */     }
/*  89 */     if (!this.config.hasHost())
/*  90 */       return inputNewHost(); 
/*  91 */     if (USE_ONLY_HOST_BY_DEFAULT && this.config.hasOneHost()) {
/*  92 */       return this.config.getHost();
/*     */     }
/*  94 */     return askToSelectHost();
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
/*     */   private String askToSelectHost() {
/* 106 */     Object[] hosts = this.config.getHosts();
/*     */     
/* 108 */     ImageIcon icon = IconLoader.getDefaultIcon();
/* 109 */     String hostChosen = (String)JOptionPane.showInputDialog(this.component, "Please choose a CoMaS server. You may press Cancel to enter a new one:", 
/* 110 */         "CoMaS Server Choice", -1, icon, hosts, hosts[0]);
/* 111 */     if (hostChosen == null) {
/* 112 */       hostChosen = inputNewHost();
/*     */     }
/* 114 */     return hostChosen;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String inputNewHost() {
/* 124 */     String oldHost = this.config.getHost();
/* 125 */     if (oldHost == null)
/* 126 */       oldHost = Shared.DIRECTORY_HOST; 
/* 127 */     ImageIcon icon = IconLoader.getDefaultIcon();
/* 128 */     String newHost = (String)JOptionPane.showInputDialog(this.component, "Enter new CoMaS location or press Cancel to exit", 
/* 129 */         "CoMaS Server Input", -1, icon, null, oldHost);
/* 130 */     if (newHost != null) {
/* 131 */       return newHost.trim();
/*     */     }
/* 133 */     return newHost;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void askToDeleteConfiguration() {
/* 142 */     if (this.config.hasHost()) {
/*     */       
/* 144 */       int choice = JOptionPane.showConfirmDialog(this.component, "Delete current CoMaS configuration?", 
/* 145 */           "CoMaS Default", 0, 2, IconLoader.getIcon(2));
/* 146 */       if (choice == 0) {
/* 147 */         Logger.output("Deleting " + Shared.COMAS_DOT_INI);
/* 148 */         this.config.delete();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\ui\ServerChooser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */