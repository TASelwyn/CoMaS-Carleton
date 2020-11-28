package edu.carleton.cas.resources;

import edu.carleton.cas.constants.ClientShared;
import edu.carleton.cas.utility.ClientHelper;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

public abstract class AbstractNetworkTask extends AbstractTask {
  protected String localHost;
  
  protected HashSet<String> hosts;
  
  protected HashSet<String> corporations;
  
  protected HashMap<String, String> cache;
  
  public AbstractNetworkTask(Logger logger, ResourceMonitor monitor) {
    super(logger, monitor);
    try {
      this.localHost = InetAddress.getLocalHost().getHostAddress();
      this.hosts = new HashSet<>();
      addHost(ClientShared.EXAM_HOST);
      addHost(ClientShared.DIRECTORY_HOST);
      addHost(ClientShared.UPLOAD_HOST);
      addHost(ClientShared.VIDEO_HOST);
      addHost(ClientShared.LOG_HOST);
      addHost(ClientShared.CMS_HOST);
      addAllHosts();
    } catch (UnknownHostException e) {
      this.localHost = "127.0.0.1";
    } 
    try {
      this.corporations = new HashSet<>();
      addAllCorporations();
    } catch (Exception exception) {}
    this.cache = new HashMap<>();
  }
  
  private void addHost(String host) throws UnknownHostException {
    InetAddress[] addresses = InetAddress.getAllByName(host);
    byte b;
    int i;
    InetAddress[] arrayOfInetAddress1;
    for (i = (arrayOfInetAddress1 = addresses).length, b = 0; b < i; ) {
      InetAddress address = arrayOfInetAddress1[b];
      this.hosts.add(address.getHostAddress().replace('.', '-'));
      this.hosts.add(address.getHostAddress());
      this.hosts.add(address.getHostName());
      this.hosts.add(address.getCanonicalHostName());
      b++;
    } 
  }
  
  public void addAllHosts() throws UnknownHostException {
    int i = 1;
    String allowedHost = this.monitor.getProperty("host.allow." + i);
    while (allowedHost != null) {
      this.hosts.add(allowedHost.trim());
      i++;
      allowedHost = this.monitor.getProperty("host.allow." + i);
    } 
  }
  
  public void addAllCorporations() throws UnknownHostException {
    int i = 1;
    String allowedCorporation = this.monitor.getProperty("corporation.allow." + i);
    while (allowedCorporation != null) {
      this.corporations.add(allowedCorporation.trim());
      i++;
      allowedCorporation = this.monitor.getProperty("corporation.allow." + i);
    } 
  }
  
  protected boolean isAllowed(String host) {
    if (this.hosts.contains(host))
      return true; 
    String corporation = this.cache.get(host);
    if (corporation != null) {
      if (this.corporations.contains(corporation))
        return true; 
    } else {
      corporation = askLogService(host);
      if (corporation != null) {
        this.cache.put(host, corporation);
        return this.corporations.contains(corporation);
      } 
    } 
    return false;
  }
  
  private String askLogService(String host) {
    Client client = ClientHelper.createClient(ClientShared.PROTOCOL);
    WebTarget webTarget = client.target(ClientShared.BASE_LOG).path("lookup");
    Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" });
    invocationBuilder.accept(new String[] { "application/json" });
    Form form = new Form();
    form.param("passkey", ClientShared.PASSKEY_LOG);
    form.param("name", host);
    Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded"));
    return (String)response.readEntity(String.class);
  }
}
