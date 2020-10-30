/*    */ package edu.carleton.cas.messaging;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public class Message {
/*    */   private String from;
/*    */   private String to;
/*    */   private String type;
/*    */   private HashMap<String, String> content;
/*    */   
/*    */   public Message() {
/* 12 */     this.to = "";
/* 13 */     this.from = "";
/* 14 */     this.type = "";
/* 15 */     this.content = new HashMap<>();
/*    */   }
/*    */   
/*    */   public Message(String to, String from, String type, HashMap<String, String> content) {
/* 19 */     this.to = to;
/* 20 */     this.from = from;
/* 21 */     this.type = type;
/* 22 */     this.content = content;
/*    */   }
/*    */   
/*    */   public Message(String to, String from, String type, String content) {
/* 26 */     this.to = to;
/* 27 */     this.from = from;
/* 28 */     this.type = type;
/* 29 */     this.content = new HashMap<>();
/* 30 */     this.content.put("content", content);
/*    */   }
/*    */   
/*    */   public String getFrom() {
/* 34 */     return this.from;
/*    */   }
/*    */   
/*    */   public void setFrom(String from) {
/* 38 */     this.from = from;
/*    */   }
/*    */   
/*    */   public String getTo() {
/* 42 */     return this.to;
/*    */   }
/*    */   
/*    */   public void setTo(String to) {
/* 46 */     this.to = to;
/*    */   }
/*    */   
/*    */   public String getType() {
/* 50 */     return this.type;
/*    */   }
/*    */   
/*    */   public void setType(String type) {
/* 54 */     this.type = type;
/*    */   }
/*    */   
/*    */   public HashMap<String, String> getContent() {
/* 58 */     return this.content;
/*    */   }
/*    */   
/*    */   public void setContent(HashMap<String, String> content) {
/* 62 */     this.content = content;
/*    */   }
/*    */   
/*    */   public void setContentMessage(String content) {
/* 66 */     this.content.put("content", content);
/*    */   }
/*    */   
/*    */   public String getContentMessage() {
/* 70 */     return this.content.get("content");
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\messaging\Message.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */