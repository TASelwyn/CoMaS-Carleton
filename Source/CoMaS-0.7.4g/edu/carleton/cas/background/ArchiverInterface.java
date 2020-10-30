package edu.carleton.cas.background;

import java.io.IOException;

public interface ArchiverInterface extends KeepAliveInterface {
  boolean doWork(Object paramObject) throws IOException;
  
  void put(Object paramObject);
}
