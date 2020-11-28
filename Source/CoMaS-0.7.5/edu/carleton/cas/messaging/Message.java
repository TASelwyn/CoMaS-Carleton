package edu.carleton.cas.messaging;

import java.util.HashMap;

public class Message {
  private String from;
  
  private String to;
  
  private String type;
  
  private HashMap<String, String> content;
  
  public Message() {
    this.to = "";
    this.from = "";
    this.type = "";
    this.content = new HashMap<>();
  }
  
  public Message(String to, String from, String type, HashMap<String, String> content) {
    this.to = to;
    this.from = from;
    this.type = type;
    this.content = content;
  }
  
  public Message(String to, String from, String type, String content) {
    this.to = to;
    this.from = from;
    this.type = type;
    this.content = new HashMap<>();
    this.content.put("content", content);
  }
  
  public String getFrom() {
    return this.from;
  }
  
  public void setFrom(String from) {
    this.from = from;
  }
  
  public String getTo() {
    return this.to;
  }
  
  public void setTo(String to) {
    this.to = to;
  }
  
  public String getType() {
    return this.type;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
  public HashMap<String, String> getContent() {
    return this.content;
  }
  
  public void setContent(HashMap<String, String> content) {
    this.content = content;
  }
  
  public void setContentMessage(String content) {
    this.content.put("content", content);
  }
  
  public String getContentMessage() {
    return this.content.get("content");
  }
}
