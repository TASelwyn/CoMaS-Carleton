/*    */ package edu.carleton.cas.logging;
/*    */ 
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ import java.util.logging.Formatter;
/*    */ import java.util.logging.Handler;
/*    */ import java.util.logging.LogRecord;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class HtmlFormatter
/*    */   extends Formatter
/*    */ {
/*    */   public String format(LogRecord rec) {
/* 20 */     StringBuffer buf = new StringBuffer(1000);
/* 21 */     buf.append("<tr>\n");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 34 */     buf.append("\t<td>");
/* 35 */     buf.append(rec.getLevel());
/*    */ 
/*    */     
/* 38 */     buf.append("</td>\n");
/* 39 */     buf.append("\t<td>");
/* 40 */     buf.append(calcDate(rec.getMillis()));
/* 41 */     buf.append("</td>\n");
/* 42 */     buf.append("\t<td>");
/* 43 */     buf.append(formatMessage(rec));
/* 44 */     buf.append("</td>\n");
/* 45 */     buf.append("</tr>\n");
/*    */     
/* 47 */     return buf.toString();
/*    */   }
/*    */   
/*    */   private String calcDate(long millisecs) {
/* 51 */     SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
/* 52 */     Date resultdate = new Date(millisecs);
/* 53 */     return date_format.format(resultdate);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getHead(Handler h) {
/* 59 */     return "<!DOCTYPE html>\n<head><meta http-equiv=\"refresh\" content=\"30\">\n<style>\ntable { width: 100% }\nth { font:bold 10pt Tahoma; }\ntd { font:normal 10pt Tahoma; }\nh1 {font:normal 11pt Tahoma;}\n</style>\n</head>\n<body>\n<h1>" + 
/*    */       
/* 61 */       new Date() + "</h1>\n" + 
/* 62 */       "<table border=\"0\" cellpadding=\"5\" cellspacing=\"3\">\n" + "<tr align=\"left\">\n" + 
/* 63 */       "\t<th style=\"width:10%\">Log Level</th>\n" + "\t<th style=\"width:15%\">Time</th>\n" + 
/* 64 */       "\t<th style=\"width:75%\">Log Message</th>\n" + "</tr>\n";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getTail(Handler h) {
/* 70 */     return "</table>\n</body>\n</html>";
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\cas\logging\HtmlFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */