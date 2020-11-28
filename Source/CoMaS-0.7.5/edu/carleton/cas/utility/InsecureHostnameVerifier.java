package edu.carleton.cas.utility;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class InsecureHostnameVerifier implements HostnameVerifier {
  public boolean verify(String hostname, SSLSession session) {
    return true;
  }
}
