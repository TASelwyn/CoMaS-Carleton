package edu.carleton.cas.resources;

public interface Resource {
  void addListener(ResourceListener paramResourceListener);
  
  void removeListener(ResourceListener paramResourceListener);
  
  void open();
  
  void close();
  
  String getResourceType();
  
  void notifyListeners(String paramString1, String paramString2);
}


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\resources\Resource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */