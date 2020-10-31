package edu.carleton.cas.utility;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.client.ClientConfig;

public class ClientHelper {
  public static Client createClient(String protocol) {
    if (protocol.equals("https")) {
      System.setProperty("jsse.enableSNIExtension", "false");
      TrustManager[] certs = { new InsecureTrustManager() };
      SSLContext ctx = null;
      try {
        ctx = SSLContext.getInstance("SSL");
        ctx.init(null, certs, new SecureRandom());
      } catch (GeneralSecurityException generalSecurityException) {}
      return ClientBuilder.newBuilder().withConfig((Configuration)new ClientConfig())
        .hostnameVerifier(new InsecureHostnameVerifier()).sslContext(ctx).build();
    } 
    return ClientBuilder.newClient();
  }
}
