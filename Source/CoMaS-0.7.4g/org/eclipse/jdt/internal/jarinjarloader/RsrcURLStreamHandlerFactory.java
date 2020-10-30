package org.eclipse.jdt.internal.jarinjarloader;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class RsrcURLStreamHandlerFactory implements URLStreamHandlerFactory {
  private ClassLoader classLoader;
  
  private URLStreamHandlerFactory chainFac;
  
  public RsrcURLStreamHandlerFactory(ClassLoader cl) {
    this.classLoader = cl;
  }
  
  public URLStreamHandler createURLStreamHandler(String protocol) {
    if ("rsrc".equals(protocol))
      return new RsrcURLStreamHandler(this.classLoader); 
    if (this.chainFac != null)
      return this.chainFac.createURLStreamHandler(protocol); 
    return null;
  }
  
  public void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
    this.chainFac = fac;
  }
}


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\org\eclipse\jdt\internal\jarinjarloader\RsrcURLStreamHandlerFactory.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */