package edu.carleton.cas.utility;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ClipboardManager {
  public static void main(String[] args) throws UnsupportedFlavorException, IOException {
    if (args.length > 0)
      setContents(args[0]); 
    System.out.println("===CLIPBOARD===\n" + getContents() + "\n===END===");
    System.exit(0);
  }
  
  public static void setContents(String contents) {
    Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
    if (contents != null) {
      StringSelection data = new StringSelection(contents);
      c.setContents(data, data);
    } 
  }
  
  public static String getContents() throws UnsupportedFlavorException, IOException {
    Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
    Transferable t = c.getContents(null);
    if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      String data = (String)t.getTransferData(DataFlavor.stringFlavor);
      return data;
    } 
    return "<empty>";
  }
}
