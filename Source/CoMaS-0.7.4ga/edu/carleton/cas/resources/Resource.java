package edu.carleton.cas.resources;

public interface Resource {
  void addListener(ResourceListener paramResourceListener);
  
  void removeListener(ResourceListener paramResourceListener);
  
  void open();
  
  void close();
  
  String getResourceType();
  
  void notifyListeners(String paramString1, String paramString2);
}
