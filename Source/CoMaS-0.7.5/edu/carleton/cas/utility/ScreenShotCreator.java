package edu.carleton.cas.utility;

import edu.carleton.cas.constants.ClientShared;
import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

public abstract class ScreenShotCreator {
  public static synchronized BufferedImage getImage() throws AWTException {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    return getImage(gd, 0);
  }
  
  public static synchronized BufferedImage getImage(GraphicsDevice screen, int x) throws AWTException {
    Robot robotForScreen = new Robot(screen);
    JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
    jpegParams.setCompressionMode(2);
    jpegParams.setCompressionQuality(ClientShared.IMAGE_COMPRESSION);
    Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
    screenBounds.x = x;
    screenBounds.y = 0;
    screenBounds.setSize(screen.getDisplayMode().getWidth(), screen.getDisplayMode().getHeight());
    BufferedImage screenShot = robotForScreen.createScreenCapture(screenBounds);
    return screenShot;
  }
  
  public static BufferedImage[] getImages() throws AWTException {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gDevs = ge.getScreenDevices();
    BufferedImage[] images = new BufferedImage[gDevs.length];
    int x_pos = 0;
    int x_neg = 0;
    for (int i = 0; i < gDevs.length; i++) {
      if ((gDevs[i].getDefaultConfiguration().getBounds()).x == 0) {
        images[i] = getImage(gDevs[i], 0);
        x_pos += gDevs[i].getDisplayMode().getWidth();
      } else if ((gDevs[i].getDefaultConfiguration().getBounds()).x > 0) {
        images[i] = getImage(gDevs[i], x_pos);
        x_pos += gDevs[i].getDisplayMode().getWidth();
      } else {
        x_neg -= gDevs[i].getDisplayMode().getWidth();
        images[i] = getImage(gDevs[i], x_neg);
      } 
    } 
    return images;
  }
  
  public static BufferedImage getSingleImage() throws AWTException {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] screens = ge.getScreenDevices();
    Rectangle allScreenBounds = new Rectangle((screens[0].getDefaultConfiguration().getBounds()).x, 0);
    byte b;
    int i;
    GraphicsDevice[] arrayOfGraphicsDevice1;
    for (i = (arrayOfGraphicsDevice1 = screens).length, b = 0; b < i; ) {
      GraphicsDevice screen = arrayOfGraphicsDevice1[b];
      Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
      allScreenBounds.width += screenBounds.width;
      allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
      b++;
    } 
    Robot robot = new Robot();
    BufferedImage screenShot = robot.createScreenCapture(allScreenBounds);
    return screenShot;
  }
}
