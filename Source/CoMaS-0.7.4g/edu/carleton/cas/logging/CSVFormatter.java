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


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\cas\logging\CSVFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */