package edu.carleton.cas.ui;

import edu.carleton.cas.constants.ClientShared;
import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.utility.IconLoader;
import java.awt.Desktop;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitResponse;
import java.awt.desktop.ScreenSleepEvent;
import java.awt.desktop.ScreenSleepListener;
import java.awt.desktop.SystemEventListener;
import java.awt.desktop.SystemSleepEvent;
import java.awt.desktop.SystemSleepListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Exam {
  public static boolean isMac;
  
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
            final ExamDialog main = new ExamDialog("CoMaS", args);
            if (Desktop.isDesktopSupported()) {
              Desktop desktop = Desktop.getDesktop();
              desktop.addAppEventListener((SystemEventListener)new SystemSleepListener() {
                    public void systemAboutToSleep(SystemSleepEvent e) {
                      if (main.login != null)
                        main.login.setStateAndAuthenticate("Login Sleeping"); 
                    }
                    
                    public void systemAwoke(SystemSleepEvent e) {
                      if (main.login != null)
                        main.login.setStateAndAuthenticate("Login Awake"); 
                    }
                  });
              desktop.addAppEventListener((SystemEventListener)new ScreenSleepListener() {
                    public void screenAboutToSleep(ScreenSleepEvent e) {
                      if (main.login != null)
                        main.login.setStateAndAuthenticate("Login Screen off"); 
                    }
                    
                    public void screenAwoke(ScreenSleepEvent e) {
                      if (main.login != null)
                        main.login.setStateAndAuthenticate("Login Screen on"); 
                    }
                  });
            } 
            if (main.versionOfJava > 8 && Desktop.isDesktopSupported() && Exam.isMac) {
              Desktop desktop = Desktop.getDesktop();
              desktop.setAboutHandler(e -> JOptionPane.showMessageDialog(null, "CoMaS " + ClientShared.VERSION, "About CoMaS", 1, IconLoader.getIcon(1)));
              desktop.setPreferencesHandler(e -> JOptionPane.showMessageDialog(null, "Context: " + param1ExamDialog.course + "/" + param1ExamDialog.activity, "CoMaS Context", 1, IconLoader.getIcon(1)));
              desktop.setQuitHandler((e, r) -> {
                    r.cancelQuit();
                    int res = JOptionPane.showConfirmDialog(param1ExamDialog, "Are you sure you want to end session?", "End CoMaS session?", 0, 2, IconLoader.getIcon(2));
                    if (res == 0) {
                      Thread t = new Thread() {
                          public void run() {
                            if (main.login != null)
                              main.login.endTheSession(); 
                            main.dispose();
                            System.exit(0);
                          }
                        };
                      t.start();
                    } 
                  });
            } 
            main.setIconImages(IconLoader.getImages());
            main.pack();
            main.setLocationRelativeTo(null);
            main.setVisible(true);
          }
        });
  }
}
