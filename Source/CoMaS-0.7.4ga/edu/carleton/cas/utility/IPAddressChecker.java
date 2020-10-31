package edu.carleton.cas.utility;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPAddressChecker {
  public static final String address = "address.";
  
  public static final String defaultKey = "default";
  
  public static final String allow = "allow.";
  
  public static final String deny = "deny.";
  
  private static boolean default_allow = true;
  
  private static boolean default_deny = false;
  
  private static final String ip_address_regex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
  
  private Properties config;
  
  private ArrayList<String> deny_addresses;
  
  private ArrayList<String> allow_addresses;
  
  private String localHost;
  
  public IPAddressChecker(Properties config) throws UnknownHostException {
    this.config = config;
    this.allow_addresses = getAddresses("allow.");
    this.deny_addresses = getAddresses("deny.");
    String value = config.getProperty(getDefault(), "true");
    if (value.equals("true")) {
      default_allow = true;
    } else if (value.equals("false")) {
      default_allow = false;
    } 
    String address = config.getProperty("LOCAL_ADDRESS");
    if (address == null) {
      address = config.getProperty("LOCAL_HOST");
      if (address == null) {
        this.localHost = InetAddress.getLocalHost().getHostAddress();
      } else if (Pattern.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$", address)) {
        this.localHost = address;
      } else {
        this.localHost = InetAddress.getLocalHost().getHostAddress();
      } 
    } else if (Pattern.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$", address)) {
      this.localHost = address;
    } else {
      this.localHost = InetAddress.getLocalHost().getHostAddress();
    } 
  }
  
  public static final String getDefault() {
    return "address.default";
  }
  
  ArrayList<String> getAddresses(String type) {
    int i = 1;
    ArrayList<String> addresses = new ArrayList<>();
    String base = "address." + type;
    String value = this.config.getProperty(String.valueOf(base) + i);
    while (value != null) {
      addresses.add(value.trim());
      i++;
      value = this.config.getProperty(String.valueOf(base) + i);
    } 
    return addresses;
  }
  
  public boolean allow() {
    for (String address : this.allow_addresses) {
      if (match(address))
        return true; 
    } 
    return default_allow;
  }
  
  public boolean deny() {
    for (String address : this.deny_addresses) {
      if (match(address))
        return true; 
    } 
    return default_deny;
  }
  
  public boolean match(String address) {
    if (address.charAt(0) == '^' && address.charAt(address.length() - 1) == '$') {
      Pattern p = Pattern.compile(address);
      Matcher m = p.matcher(this.localHost);
      return m.matches();
    } 
    return this.localHost.equals(address);
  }
}
