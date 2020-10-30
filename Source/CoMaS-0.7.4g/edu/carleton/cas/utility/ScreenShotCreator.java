/*     */ package edu.carleton.cas.utility;
/*     */ 
/*     */ import edu.carleton.cas.constants.Shared;
/*     */ import java.awt.AWTException;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Robot;
/*     */ import java.awt.image.BufferedImage;
/*     */ import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ScreenShotCreator
/*     */ {
/*     */   public static synchronized BufferedImage getImage() throws AWTException {
/*  34 */     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*  35 */     GraphicsDevice gd = ge.getDefaultScreenDevice();
/*  36 */     return getImage(gd, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized BufferedImage getImage(GraphicsDevice screen, int x) throws AWTException {
/*  50 */     Robot robotForScreen = new Robot(screen);
/*  51 */     JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
/*  52 */     jpegParams.setCompressionMode(2);
/*  53 */     jpegParams.setCompressionQuality(Shared.IMAGE_COMPRESSION);
/*  54 */     Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
/*     */ 
/*     */ 
/*     */     
/*  58 */     screenBounds.x = x;
/*  59 */     screenBounds.y = 0;
/*  60 */     screenBounds.setSize(screen.getDisplayMode().getWidth(), screen.getDisplayMode().getHeight());
/*  61 */     BufferedImage screenShot = robotForScreen.createScreenCapture(screenBounds);
/*     */     
/*  63 */     return screenShot;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BufferedImage[] getImages() throws AWTException {
/*  75 */     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*  76 */     GraphicsDevice[] gDevs = ge.getScreenDevices();
/*  77 */     BufferedImage[] images = new BufferedImage[gDevs.length];
/*     */     
/*  79 */     int x_pos = 0;
/*  80 */     int x_neg = 0;
/*  81 */     for (int i = 0; i < gDevs.length; i++) {
/*     */       
/*  83 */       if ((gDevs[i].getDefaultConfiguration().getBounds()).x == 0) {
/*  84 */         images[i] = getImage(gDevs[i], 0);
/*  85 */         x_pos += gDevs[i].getDisplayMode().getWidth();
/*  86 */       } else if ((gDevs[i].getDefaultConfiguration().getBounds()).x > 0) {
/*  87 */         images[i] = getImage(gDevs[i], x_pos);
/*  88 */         x_pos += gDevs[i].getDisplayMode().getWidth();
/*     */       } else {
/*  90 */         x_neg -= gDevs[i].getDisplayMode().getWidth();
/*  91 */         images[i] = getImage(gDevs[i], x_neg);
/*     */       } 
/*     */     } 
/*  94 */     return images;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BufferedImage getSingleImage() throws AWTException {
/* 108 */     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 109 */     GraphicsDevice[] screens = ge.getScreenDevices();
/*     */     
/* 111 */     Rectangle allScreenBounds = new Rectangle((screens[0].getDefaultConfiguration().getBounds()).x, 0); byte b; int i; GraphicsDevice[] arrayOfGraphicsDevice1;
/* 112 */     for (i = (arrayOfGraphicsDevice1 = screens).length, b = 0; b < i; ) { GraphicsDevice screen = arrayOfGraphicsDevice1[b];
/* 113 */       Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
/*     */       
/* 115 */       allScreenBounds.width += screenBounds.width;
/* 116 */       allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
/*     */       b++; }
/*     */     
/* 119 */     Robot robot = new Robot();
/* 120 */     BufferedImage screenShot = robot.createScreenCapture(allScreenBounds);
/* 121 */     return screenShot;
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\ScreenShotCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */