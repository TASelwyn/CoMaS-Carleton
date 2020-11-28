package edu.carleton.cas.websocket;

import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.messaging.Message;
import edu.carleton.cas.messaging.MessageDecoder;
import edu.carleton.cas.messaging.MessageEncoder;
import edu.carleton.cas.messaging.MessageHandler;
import edu.carleton.cas.resources.AbstractResourceMonitor;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint(decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
public class WebsocketClientEndpoint extends AbstractResourceMonitor {
  private Session userSession = null;
  
  private URI endpointURI;
  
  private ConcurrentHashMap<String, MessageHandler> messageHandlers;
  
  public WebsocketClientEndpoint(URI endpointURI) {
    this.endpointURI = endpointURI;
    this.messageHandlers = new ConcurrentHashMap<>();
    this.listeners = new CopyOnWriteArraySet();
    this.type = "websocket";
  }
  
  @OnOpen
  public void onOpen(Session userSession) throws IOException, EncodeException {
    this.userSession = userSession;
    notifyListeners(getResourceType(), "open");
  }
  
  @OnClose
  public void onClose(Session userSession, CloseReason reason) {
    this.userSession = null;
    notifyListeners(getResourceType(), "close");
  }
  
  @OnError
  public void onError(Session userSession, Throwable reason) {
    notifyListeners(getResourceType(), "error");
  }
  
  @OnMessage
  public void onMessage(Message message) {
    MessageHandler handler = this.messageHandlers.get(message.getType());
    if (handler != null) {
      handler.handleMessage(message);
    } else {
      Logger.log(Level.WARNING, "No handler for: ", message);
    } 
  }
  
  public void addMessageHandler(String type, MessageHandler msgHandler) {
    this.messageHandlers.put(type, msgHandler);
  }
  
  public boolean removeMessageHandler(String type, MessageHandler msgHandler) {
    return this.messageHandlers.remove(type, msgHandler);
  }
  
  public void sendMessage(Message message) throws IOException, EncodeException {
    if (message != null)
      this.userSession.getAsyncRemote().sendObject(message); 
  }
  
  public void open() {
    if (this.userSession != null && 
      this.userSession.isOpen())
      return; 
    try {
      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      container.connectToServer(this, this.endpointURI);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public void close() {
    if (this.userSession != null)
      try {
        this.userSession.close();
      } catch (Exception e) {
        throw new RuntimeException(e);
      } finally {
        this.userSession = null;
      }  
  }
}
