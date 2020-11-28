package edu.carleton.cas.background;

public interface ArchiverInterface extends KeepAliveInterface {
  boolean doWork(Object paramObject);
  
  void put(Object paramObject);
}
