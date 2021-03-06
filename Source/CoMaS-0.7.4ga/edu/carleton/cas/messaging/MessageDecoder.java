package edu.carleton.cas.messaging;

import com.google.gson.Gson;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message> {
  private static Gson gson = new Gson();
  
  public Message decode(String s) throws DecodeException {
    return (Message)gson.fromJson(s, Message.class);
  }
  
  public boolean willDecode(String s) {
    return (s != null);
  }
  
  public void init(EndpointConfig endpointConfig) {}
  
  public void destroy() {}
}
