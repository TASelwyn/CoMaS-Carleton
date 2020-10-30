package edu.carleton.cas.ui;

import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.utility.IconLoader;
import java.awt.Desktop;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.simplericity.macify.eawt.DefaultApplication;

public class Exam {
  public static boolean isMac;
  
  public static DefaultApplication app;
  
  private static void macSetup(String appName) {
    String os = System.getProperty("os.name").toLowerCase();
    isMac = os.startsWith("mac os x");
    if (!isMac)
      return; 
    System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
    System.setProperty("apple.awt.application.name", appName);
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
  }
  
  public static void main(final String[] args) throws Exception {
    macSetup("CoMaS");
    UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
    String lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
    byte b;
    int i;
    UIManager.LookAndFeelInfo[] arrayOfLookAndFeelInfo1;
    for (i = (arrayOfLookAndFeelInfo1 = info).length, b = 0; b < i; ) {
      UIManager.LookAndFeelInfo lafi = arrayOfLookAndFeelInfo1[b];
      if (lafi.getName().startsWith("Nimbus"))
        lookAndFeelClassName = lafi.getClassName(); 
      b++;
    } 
    Logger.output("Look and feel class is " + lookAndFeelClassName);
    UIManager.setLookAndFeel(lookAndFeelClassName);
    SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            ExamDialog main = new ExamDialog("CoMaS", args);
            if (main.versionOfJava != 8 || !Exam.isMac)
              if (main.versionOfJava > 8)
                Desktop.isDesktopSupported();  
            main.setIconImages(IconLoader.getImages());
            main.pack();
            main.setLocationRelativeTo(null);
            main.setVisible(true);
          }
        });
  }
}
