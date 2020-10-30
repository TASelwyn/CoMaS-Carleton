package edu.carleton.cas.ui;

import edu.carleton.cas.constants.Shared;
import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.utility.ClientConfiguration;
import edu.carleton.cas.utility.IconLoader;
import java.awt.Component;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class ServerChooser {
  private static boolean USE_ONLY_HOST_BY_DEFAULT = false;
  
  private ClientConfiguration config;
  
  private Component component;
  
  public ServerChooser(Component component) {
    this.config = new ClientConfiguration(String.valueOf(Shared.DIR) + File.separator + Shared.COMAS_DOT_INI);
    this.component = component;
  }
  
  public void open() {
    this.config.load();
  }
  
  public void close() {
    this.config.save("CoMaS server location information. DO NOT EDIT");
  }
  
  public String select() {
    String host = choose();
    if (host != null) {
      this.config.setRecentHost(host);
      this.config.save("CoMaS server location information. DO NOT EDIT");
    } else {
      askToDeleteConfiguration();
    } 
    return host;
  }
  
  private String choose() {
    if (Shared.SERVER_CHOSEN != null)
      return Shared.SERVER_CHOSEN; 
    if (!this.config.hasHost())
      return inputNewHost(); 
    if (USE_ONLY_HOST_BY_DEFAULT && this.config.hasOneHost())
      return this.config.getHost(); 
    return askToSelectHost();
  }
  
  private String askToSelectHost() {
    Object[] hosts = this.config.getHosts();
    ImageIcon icon = IconLoader.getDefaultIcon();
    String hostChosen = (String)JOptionPane.showInputDialog(this.component, "Please choose a CoMaS server. You may press Cancel to enter a new one:", 
        "CoMaS Server Choice", -1, icon, hosts, hosts[0]);
    if (hostChosen == null)
      hostChosen = inputNewHost(); 
    return hostChosen;
  }
  
  private String inputNewHost() {
    String oldHost = this.config.getHost();
    if (oldHost == null)
      oldHost = Shared.DIRECTORY_HOST; 
    ImageIcon icon = IconLoader.getDefaultIcon();
    String newHost = (String)JOptionPane.showInputDialog(this.component, "Enter new CoMaS location or press Cancel to exit", 
        "CoMaS Server Input", -1, icon, null, oldHost);
    if (newHost != null)
      return newHost.trim(); 
    return newHost;
  }
  
  private void askToDeleteConfiguration() {
    if (this.config.hasHost()) {
      int choice = JOptionPane.showConfirmDialog(this.component, "Delete current CoMaS configuration?", 
          "CoMaS Default", 0, 2, IconLoader.getIcon(2));
      if (choice == 0) {
        Logger.output("Deleting " + Shared.COMAS_DOT_INI);
        this.config.delete();
      } 
    } 
  }
}
