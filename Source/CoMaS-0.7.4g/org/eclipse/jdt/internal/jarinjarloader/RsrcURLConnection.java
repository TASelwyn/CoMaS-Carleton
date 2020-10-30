package org.eclipse.jdt.internal.jarinjarloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

public class RsrcURLConnection extends URLConnection {
  private ClassLoader classLoader;
  
  public RsrcURLConnection(URL url, ClassLoader classLoader) {
    super(url);
    this.classLoader = classLoader;
  }
  
  public void connect() throws IOException {}
  
  public InputStream getInputStream() throws IOException {
    String file = URLDecoder.decode(this.url.getFile(), "UTF-8");
    InputStream result = this.classLoader.getResourceAsStream(file);
    if (result == null)
      throw new MalformedURLException("Could not open InputStream for URL '" + this.url + "'"); 
    return result;
  }
}


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\org\eclipse\jdt\internal\jarinjarloader\RsrcURLConnection.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */