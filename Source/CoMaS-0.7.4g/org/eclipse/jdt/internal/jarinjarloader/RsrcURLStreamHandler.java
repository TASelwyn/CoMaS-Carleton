package org.eclipse.jdt.internal.jarinjarloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class RsrcURLStreamHandler extends URLStreamHandler {
  private ClassLoader classLoader;
  
  public RsrcURLStreamHandler(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }
  
  protected URLConnection openConnection(URL u) throws IOException {
    return new RsrcURLConnection(u, this.classLoader);
  }
  
  protected void parseURL(URL url, String spec, int start, int limit) {
    String file;
    if (spec.startsWith("rsrc:")) {
      file = spec.substring(5);
    } else if (url.getFile().equals("./")) {
      file = spec;
    } else if (url.getFile().endsWith("/")) {
      file = String.valueOf(url.getFile()) + spec;
    } else if ("#runtime".equals(spec)) {
      file = url.getFile();
    } else {
      file = spec;
    } 
    setURL(url, "rsrc", "", -1, null, null, file, null, null);
  }
}


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\org\eclipse\jdt\internal\jarinjarloader\RsrcURLStreamHandler.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */