package edu.carleton.cas.utility;

import edu.carleton.cas.constants.ClientShared;
import edu.carleton.cas.file.Utils;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Properties;

public class ClientConfiguration {
  public static final String COMAS_DOT = "comas.";
  
  public static int MAX_HOSTS = 5;
  
  private String name;
  
  private Properties configuration;
  
  public ClientConfiguration(String name) {
    this.configuration = new Properties();
    this.name = name.trim();
  }
  
  public boolean delete() {
    File f = new File(this.name);
    if (f.exists()) {
      f.deleteOnExit();
      return true;
    } 
    return false;
  }
  
  public void remove() {
    File f = new File(this.name);
    File dir = f.getParentFile();
    File[] comasDotJar = dir.listFiles(new FileFilter() {
          public boolean accept(File file) {
            String name = file.getName();
            if (name.equals("CoMaS.jar"))
              return false; 
            return (name.startsWith("CoMaS") && name.endsWith(".jar"));
          }
        });
    if (comasDotJar != null)
      for (int i = 0; i < comasDotJar.length; i++)
        comasDotJar[i].deleteOnExit();  
  }
  
  public boolean load() {
    Properties p = Utils.getPropertiesFromFile(this.name);
    if (p == null)
      return false; 
    this.configuration = p;
    if (this.configuration.containsKey("first_name"))
      ClientShared.STUDENT_FIRST_NAME = this.configuration.getProperty("first_name").trim(); 
    if (this.configuration.containsKey("last_name"))
      ClientShared.STUDENT_LAST_NAME = this.configuration.getProperty("last_name").trim(); 
    if (this.configuration.containsKey("id"))
      ClientShared.STUDENT_ID = this.configuration.getProperty("id").trim(); 
    return !this.configuration.isEmpty();
  }
  
  public boolean save(String comments) {
    return Utils.savePropertiesToFile(this.configuration, comments, this.name);
  }
  
  private String key(int i) {
    return "comas." + i;
  }
  
  public boolean hasOneHost() {
    if (!hasHost())
      return false; 
    for (int i = 2; i < MAX_HOSTS; i++) {
      if (hasHost(i))
        return false; 
    } 
    return true;
  }
  
  public boolean hasHost() {
    return hasHost(1);
  }
  
  public boolean hasHost(int i) {
    return this.configuration.containsKey(key(i));
  }
  
  public String getHost() {
    return getHost(1);
  }
  
  public String getHost(int i) {
    return this.configuration.getProperty(key(i));
  }
  
  public Object[] getHosts() {
    int i = 1;
    ArrayList<String> possibilities = new ArrayList<>();
    if (ClientShared.LOOK_FOR_SERVICES)
      possibilities.add(ClientShared.DIRECTORY_HOST); 
    while (hasHost(i)) {
      String host = getHost(i);
      if (!possibilities.contains(host))
        possibilities.add(host.trim()); 
      i++;
    } 
    return possibilities.toArray();
  }
  
  public void setHost(String host) {
    setHost(1, host);
  }
  
  public void setHost(int i, String host) {
    if (i > 0 && i <= MAX_HOSTS && host != null)
      this.configuration.setProperty(key(i), host); 
  }
  
  public void setRecentHost(String host) {
    for (int i = MAX_HOSTS; i > 1; i--)
      setHost(i, getHost(i - 1)); 
    setHost(host);
    Object[] hosts = getHosts();
    int j;
    for (j = 0; j < hosts.length; j++)
      setHost(j + 1, hosts[j].toString()); 
    for (j = hosts.length; j <= MAX_HOSTS; j++)
      this.configuration.remove(key(j + 1)); 
  }
}
