package edu.carleton.cas.utility;

import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public abstract class IconLoader {
  public static ImageIcon getIcon(String root, String size) {
    String resource = String.format("/images/%s-icon-%s.png", new Object[] { root, size });
    return new ImageIcon(IconLoader.class.getResource(resource));
  }
  
  public static ImageIcon getDefaultIcon(String root) {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.startsWith("mac os x"))
      return getIcon(root, "64x64"); 
    if (os.indexOf("win") > -1)
      return getIcon(root, "48x48"); 
    return getIcon(root, "64x64");
  }
  
  public static ImageIcon getDefaultIcon() {
    return getDefaultIcon("social-sharing");
  }
  
  public static ImageIcon getIcon(int optionType) {
    if (optionType == 2)
      return getDefaultIcon("warning"); 
    if (optionType == 0)
      return getDefaultIcon("error"); 
    return getDefaultIcon();
  }
  
  public static ArrayList<Image> getImages() {
    ArrayList<Image> images = new ArrayList<>();
    String[] sizes = { "16x16", "24x24", "32x32", "48x48", "64x64", "72x72", "96x96", "128x128" };
    byte b;
    int i;
    String[] arrayOfString1;
    for (i = (arrayOfString1 = sizes).length, b = 0; b < i; ) {
      String imageSize = arrayOfString1[b];
      images.add(getIcon("social-sharing", imageSize).getImage());
      b++;
    } 
    return images;
  }
}


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\ca\\utility\IconLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */