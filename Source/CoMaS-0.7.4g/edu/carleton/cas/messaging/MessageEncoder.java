/*    */ package edu.carleton.cas.messaging;
/*    */ 
/*    */ import com.google.gson.Gson;
/*    */ import javax.websocket.EncodeException;
/*    */ import javax.websocket.Encoder;
/*    */ import javax.websocket.EndpointConfig;
/*    */ 
/*    */ public class MessageEncoder
/*    */   implements Encoder.Text<Message>
/*    */ {
/* 11 */   private static Gson gson = new Gson();
/*    */ 
/*    */   
/*    */   public String encode(Message message) throws EncodeException {
/* 15 */     return gson.toJson(message);
/*    */   }
/*    */   
/*    */   public void init(EndpointConfig endpointConfig) {}
/*    */   
/*    */   public void destroy() {}
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\messaging\MessageEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */