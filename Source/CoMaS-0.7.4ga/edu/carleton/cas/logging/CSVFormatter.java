package edu.carleton.cas.logging;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

class CSVFormatter extends Formatter {
  public String format(LogRecord rec) {
    StringBuffer buf = new StringBuffer(100);
    buf.append(rec.getLevel() + ",");
    buf.append(formatMessage(rec));
    buf.append("," + rec.getMillis() + "\n");
    return buf.toString();
  }
  
  public String getHead(Handler h) {
    return "LEVEL,LOG,TIME\n";
  }
  
  public String getTail(Handler h) {
    return "";
  }
}
