/*    */ package edu.carleton.cas.messaging;
/*    */ 
/*    */ import com.google.gson.Gson;
/*    */ import javax.websocket.DecodeException;
/*    */ import javax.websocket.Decoder;
/*    */ import javax.websocket.EndpointConfig;
/*    */ 
/*    */ public class MessageDecoder
/*    */   implements Decoder.Text<Message>
/*    */ {
/* 11 */   private static Gson gson = new Gson();
/*    */ 
/*    */   
/*    */   public Message decode(String s) throws DecodeException {
/* 15 */     return (Message)gson.fromJson(s, Message.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean willDecode(String s) {
/* 20 */     return (s != null);
/*    */   }
/*    */   
/*    */   public void init(EndpointConfig endpointConfig) {}
/*    */   
/*    */   public void destroy() {}
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\messaging\MessageDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */