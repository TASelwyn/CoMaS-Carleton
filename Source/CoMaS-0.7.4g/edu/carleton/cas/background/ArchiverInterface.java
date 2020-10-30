package edu.carleton.cas.background;

import java.io.IOException;

public interface ArchiverInterface extends KeepAliveInterface {
  boolean doWork(Object paramObject) throws IOException;
  
  void put(Object paramObject);
}


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\background\ArchiverInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */