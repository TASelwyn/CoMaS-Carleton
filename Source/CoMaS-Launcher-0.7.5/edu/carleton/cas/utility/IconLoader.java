/*    */ package edu.carleton.cas.utility;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import java.util.ArrayList;
/*    */ import javax.swing.ImageIcon;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class IconLoader
/*    */ {
/*    */   public static ImageIcon getIcon(String root, String size) {
/* 28 */     String resource = String.format("/images/%s-icon-%s.png", new Object[] { root, size });
/* 29 */     return new ImageIcon(IconLoader.class.getResource(resource));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ImageIcon getDefaultIcon(String root) {
/* 40 */     String os = System.getProperty("os.name").toLowerCase();
/* 41 */     if (os.startsWith("mac os x"))
/* 42 */       return getIcon(root, "64x64"); 
/* 43 */     if (os.indexOf("win") > -1) {
/* 44 */       return getIcon(root, "48x48");
/*    */     }
/* 46 */     return getIcon(root, "64x64");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ImageIcon getDefaultIcon() {
/* 56 */     return getDefaultIcon("social-sharing");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ImageIcon getIcon(int optionType) {
/* 67 */     if (optionType == 2)
/* 68 */       return getDefaultIcon("warning"); 
/* 69 */     if (optionType == 0) {
/* 70 */       return getDefaultIcon("error");
/*    */     }
/* 72 */     return getDefaultIcon();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ArrayList<Image> getImages() {
/* 83 */     ArrayList<Image> images = new ArrayList<>();
/* 84 */     String[] sizes = { "16x16", "24x24", "32x32", "48x48", "64x64", "72x72", "96x96", "128x128" }; byte b; int i; String[] arrayOfString1;
/* 85 */     for (i = (arrayOfString1 = sizes).length, b = 0; b < i; ) { String imageSize = arrayOfString1[b];
/* 86 */       images.add(getIcon("social-sharing", imageSize).getImage()); b++; }
/*    */     
/* 88 */     return images;
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\ca\\utility\IconLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */