package edu.carleton.cas.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

class HtmlFormatter extends Formatter {
  public String format(LogRecord rec) {
    StringBuffer buf = new StringBuffer(1000);
    buf.append("<tr>\n");
    buf.append("\t<td>");
    buf.append(rec.getLevel());
    buf.append("</td>\n");
    buf.append("\t<td>");
    buf.append(calcDate(rec.getMillis()));
    buf.append("</td>\n");
    buf.append("\t<td>");
    buf.append(formatMessage(rec));
    buf.append("</td>\n");
    buf.append("</tr>\n");
    return buf.toString();
  }
  
  private String calcDate(long millisecs) {
    SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
    Date resultdate = new Date(millisecs);
    return date_format.format(resultdate);
  }
  
  public String getHead(Handler h) {
    return "<!DOCTYPE html>\n<head><meta http-equiv=\"refresh\" content=\"30\">\n<style>\ntable { width: 100% }\nth { font:bold 10pt Tahoma; }\ntd { font:normal 10pt Tahoma; }\nh1 {font:normal 11pt Tahoma;}\n</style>\n</head>\n<body>\n<h1>" + 
      
      new Date() + "</h1>\n" + 
      "<table border=\"0\" cellpadding=\"5\" cellspacing=\"3\">\n" + "<tr align=\"left\">\n" + 
      "\t<th style=\"width:10%\">Log Level</th>\n" + "\t<th style=\"width:15%\">Time</th>\n" + 
      "\t<th style=\"width:75%\">Log Message</th>\n" + "</tr>\n";
  }
  
  public String getTail(Handler h) {
    return "</table>\n</body>\n</html>";
  }
}
