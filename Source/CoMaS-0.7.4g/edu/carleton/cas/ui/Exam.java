/*    */ package edu.carleton.cas.ui;
/*    */ 
/*    */ import edu.carleton.cas.logging.Logger;
/*    */ import edu.carleton.cas.utility.IconLoader;
/*    */ import java.awt.Desktop;
/*    */ import javax.swing.SwingUtilities;
/*    */ import javax.swing.UIManager;
/*    */ import org.simplericity.macify.eawt.DefaultApplication;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Exam
/*    */ {
/*    */   public static boolean isMac;
/*    */   public static DefaultApplication app;
/*    */   
/*    */   private static void macSetup(String appName) {
/* 29 */     String os = System.getProperty("os.name").toLowerCase();
/* 30 */     isMac = os.startsWith("mac os x");
/*    */ 
/*    */ 
/*    */     
/* 34 */     if (!isMac) {
/*    */       return;
/*    */     }
/* 37 */     System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
/* 38 */     System.setProperty("apple.awt.application.name", appName);
/* 39 */     System.setProperty("apple.laf.useScreenMenuBar", "true");
/* 40 */     System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
/*    */   }
/*    */   
/*    */   public static void main(final String[] args) throws Exception {
/* 44 */     macSetup("CoMaS");
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 49 */     UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
/* 50 */     String lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName(); byte b; int i; UIManager.LookAndFeelInfo[] arrayOfLookAndFeelInfo1;
/* 51 */     for (i = (arrayOfLookAndFeelInfo1 = info).length, b = 0; b < i; ) { UIManager.LookAndFeelInfo lafi = arrayOfLookAndFeelInfo1[b];
/*    */       
/* 53 */       if (lafi.getName().startsWith("Nimbus"))
/* 54 */         lookAndFeelClassName = lafi.getClassName(); 
/*    */       b++; }
/*    */     
/* 57 */     Logger.output("Look and feel class is " + lookAndFeelClassName);
/* 58 */     UIManager.setLookAndFeel(lookAndFeelClassName);
/*    */     
/* 60 */     SwingUtilities.invokeLater(new Runnable()
/*    */         {
/*    */           public void run()
/*    */           {
/* 64 */             ExamDialog main = new ExamDialog("CoMaS", args);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */             
/* 70 */             if (main.versionOfJava != 8 || !Exam.isMac)
/*    */             {
/*    */               
/* 73 */               if (main.versionOfJava > 8) Desktop.isDesktopSupported();
/*    */             
/*    */             }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */             
/* 90 */             main.setIconImages(IconLoader.getImages());
/* 91 */             main.pack();
/* 92 */             main.setLocationRelativeTo(null);
/* 93 */             main.setVisible(true);
/*    */           }
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\ui\Exam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */