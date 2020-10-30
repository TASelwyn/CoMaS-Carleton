package edu.carleton.cas.security;

import edu.carleton.cas.constants.Shared;
import edu.carleton.cas.file.Utils;
import java.io.File;
import java.net.URL;
import java.util.Properties;

public class ApplicationHash {
  static Properties properties = new Properties();
  
  static String host = "comas.cogerent.com";
  
  public static void main(String[] args) throws Exception {
    Shared.VERSION = "0.7.4d";
    String jarFileToBeLoaded = String.format(Shared.COMAS_DOT_JAR_FORMAT, new Object[] { Utils.getStringOrDefault(properties, "version", Shared.VERSION) });
    System.out.println("Jar: " + jarFileToBeLoaded);
    String url = Shared.service(Shared.PROTOCOL, host, Shared.PORT, 
        "/CMS/rest/exam/" + jarFileToBeLoaded);
    File dir = new File(Shared.DIR);
    Utils.getAndStoreURL(new URL(url), dir);
    String hash = Checksum.getSHA256Checksum((new File(Shared.DIR, jarFileToBeLoaded)).getAbsolutePath());
    System.out.println(hash);
  }
}
