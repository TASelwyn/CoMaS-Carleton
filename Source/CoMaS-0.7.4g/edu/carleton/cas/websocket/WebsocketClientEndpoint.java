/*     */ package edu.carleton.cas.websocket;
/*     */ 
/*     */ import edu.carleton.cas.logging.Logger;
/*     */ import edu.carleton.cas.messaging.Message;
/*     */ import edu.carleton.cas.messaging.MessageDecoder;
/*     */ import edu.carleton.cas.messaging.MessageEncoder;
/*     */ import edu.carleton.cas.messaging.MessageHandler;
/*     */ import edu.carleton.cas.resources.AbstractResourceMonitor;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.CopyOnWriteArraySet;
/*     */ import java.util.logging.Level;
/*     */ import javax.websocket.ClientEndpoint;
/*     */ import javax.websocket.CloseReason;
/*     */ import javax.websocket.ContainerProvider;
/*     */ import javax.websocket.EncodeException;
/*     */ import javax.websocket.OnClose;
/*     */ import javax.websocket.OnError;
/*     */ import javax.websocket.OnMessage;
/*     */ import javax.websocket.OnOpen;
/*     */ import javax.websocket.Session;
/*     */ import javax.websocket.WebSocketContainer;
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
/*     */ @ClientEndpoint(decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
/*     */ public class WebsocketClientEndpoint
/*     */   extends AbstractResourceMonitor
/*     */ {
/*  40 */   private Session userSession = null;
/*     */   private URI endpointURI;
/*     */   private ConcurrentHashMap<String, MessageHandler> messageHandlers;
/*     */   
/*     */   public WebsocketClientEndpoint(URI endpointURI) {
/*  45 */     this.endpointURI = endpointURI;
/*  46 */     this.messageHandlers = new ConcurrentHashMap<>();
/*  47 */     this.listeners = new CopyOnWriteArraySet();
/*  48 */     this.type = "websocket";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @OnOpen
/*     */   public void onOpen(Session userSession) throws IOException, EncodeException {
/*  60 */     this.userSession = userSession;
/*  61 */     notifyListeners(getResourceType(), "open");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @OnClose
/*     */   public void onClose(Session userSession, CloseReason reason) {
/*  72 */     this.userSession = null;
/*  73 */     notifyListeners(getResourceType(), "close");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @OnError
/*     */   public void onError(Session userSession, Throwable reason) {
/*  84 */     notifyListeners(getResourceType(), "error");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @OnMessage
/*     */   public void onMessage(Message message) {
/*  96 */     MessageHandler handler = this.messageHandlers.get(message.getType());
/*  97 */     if (handler != null) {
/*  98 */       handler.handleMessage(message);
/*     */     } else {
/* 100 */       Logger.log(Level.WARNING, "No handler for: ", message);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addMessageHandler(String type, MessageHandler msgHandler) {
/* 109 */     this.messageHandlers.put(type, msgHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean removeMessageHandler(String type, MessageHandler msgHandler) {
/* 119 */     return this.messageHandlers.remove(type, msgHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendMessage(Message message) throws IOException, EncodeException {
/* 130 */     if (message != null) {
/* 131 */       this.userSession.getAsyncRemote().sendObject(message);
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
/*     */   public void open() {
/* 149 */     if (this.userSession != null && 
/* 150 */       this.userSession.isOpen()) {
/*     */       return;
/*     */     }
/*     */     try {
/* 154 */       WebSocketContainer container = ContainerProvider.getWebSocketContainer();
/* 155 */       container.connectToServer(this, this.endpointURI);
/* 156 */     } catch (Exception e) {
/* 157 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 166 */     if (this.userSession != null)
/*     */       try {
/* 168 */         this.userSession.close();
/* 169 */       } catch (Exception e) {
/* 170 */         throw new RuntimeException(e);
/*     */       } finally {
/* 172 */         this.userSession = null;
/*     */       }  
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\websocket\WebsocketClientEndpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */