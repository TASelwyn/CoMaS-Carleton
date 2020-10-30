/*     */ package edu.carleton.cas.resources;
/*     */ 
/*     */ import edu.carleton.cas.constants.Shared;
/*     */ import edu.carleton.cas.utility.ClientHelper;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractNetworkTask
/*     */   extends AbstractTask
/*     */ {
/*     */   protected String localHost;
/*     */   protected HashSet<String> hosts;
/*     */   protected HashSet<String> corporations;
/*     */   protected HashMap<String, String> cache;
/*     */   
/*     */   public AbstractNetworkTask(Logger logger, ResourceMonitor monitor) {
/*  39 */     super(logger, monitor);
/*     */     try {
/*  41 */       this.localHost = InetAddress.getLocalHost().getHostAddress();
/*  42 */       this.hosts = new HashSet<>();
/*  43 */       addHost(Shared.EXAM_HOST);
/*  44 */       addHost(Shared.DIRECTORY_HOST);
/*  45 */       addHost(Shared.UPLOAD_HOST);
/*  46 */       addHost(Shared.VIDEO_HOST);
/*  47 */       addHost(Shared.LOG_HOST);
/*  48 */       addHost(Shared.CMS_HOST);
/*     */       
/*  50 */       addAllHosts();
/*     */     }
/*  52 */     catch (UnknownHostException e) {
/*  53 */       this.localHost = "127.0.0.1";
/*     */     } 
/*     */     try {
/*  56 */       this.corporations = new HashSet<>();
/*  57 */       addAllCorporations();
/*  58 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/*  61 */     this.cache = new HashMap<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addHost(String host) throws UnknownHostException {
/*  70 */     InetAddress[] addresses = InetAddress.getAllByName(host); byte b; int i; InetAddress[] arrayOfInetAddress1;
/*  71 */     for (i = (arrayOfInetAddress1 = addresses).length, b = 0; b < i; ) { InetAddress address = arrayOfInetAddress1[b];
/*     */       
/*  73 */       this.hosts.add(address.getHostAddress().replace('.', '-'));
/*  74 */       this.hosts.add(address.getHostAddress());
/*  75 */       this.hosts.add(address.getHostName());
/*  76 */       this.hosts.add(address.getCanonicalHostName());
/*     */       b++; }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAllHosts() throws UnknownHostException {
/*  87 */     int i = 1;
/*  88 */     String allowedHost = this.monitor.getProperty("host.allow." + i);
/*  89 */     while (allowedHost != null) {
/*     */       
/*  91 */       this.hosts.add(allowedHost.trim());
/*  92 */       i++;
/*  93 */       allowedHost = this.monitor.getProperty("host.allow." + i);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAllCorporations() throws UnknownHostException {
/* 104 */     int i = 1;
/* 105 */     String allowedCorporation = this.monitor.getProperty("corporation.allow." + i);
/* 106 */     while (allowedCorporation != null) {
/*     */       
/* 108 */       this.corporations.add(allowedCorporation.trim());
/* 109 */       i++;
/* 110 */       allowedCorporation = this.monitor.getProperty("corporation.allow." + i);
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
/*     */   protected boolean isAllowed(String host) {
/* 124 */     if (this.hosts.contains(host))
/* 125 */       return true; 
/* 126 */     String corporation = this.cache.get(host);
/* 127 */     if (corporation != null) {
/* 128 */       if (this.corporations.contains(corporation))
/* 129 */         return true; 
/*     */     } else {
/* 131 */       corporation = askLogService(host);
/* 132 */       if (corporation != null) {
/*     */         
/* 134 */         this.cache.put(host, corporation);
/* 135 */         return this.corporations.contains(corporation);
/*     */       } 
/*     */     } 
/* 138 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String askLogService(String host) {
/* 148 */     Client client = ClientHelper.createClient(Shared.PROTOCOL);
/* 149 */     WebTarget webTarget = client.target(Shared.BASE_LOG).path("lookup");
/* 150 */     Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" });
/* 151 */     invocationBuilder.accept(new String[] { "application/json" });
/* 152 */     Form form = new Form();
/* 153 */     form.param("passkey", Shared.PASSKEY_LOG);
/* 154 */     form.param("name", host);
/* 155 */     Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded"));
/*     */     
/* 157 */     return (String)response.readEntity(String.class);
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\AbstractNetworkTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */