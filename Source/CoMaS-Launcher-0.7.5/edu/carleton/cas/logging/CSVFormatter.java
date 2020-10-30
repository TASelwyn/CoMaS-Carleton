/*    */ package edu.carleton.cas.logging;
/*    */ 
/*    */ import java.util.logging.Formatter;
/*    */ import java.util.logging.Handler;
/*    */ import java.util.logging.LogRecord;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class CSVFormatter
/*    */   extends Formatter
/*    */ {
/*    */   public String format(LogRecord rec) {
/* 16 */     StringBuffer buf = new StringBuffer(100);
/*    */     
/* 18 */     buf.append(rec.getLevel() + ",");
/* 19 */     buf.append(formatMessage(rec));
/* 20 */     buf.append("," + rec.getMillis() + "\n");
/* 21 */     return buf.toString();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getHead(Handler h) {
/* 27 */     return "LEVEL,LOG,TIME\n";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getTail(Handler h) {
/* 33 */     return "";
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\cas\logging\CSVFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */