/*    */ package edu.carleton.cas.utility;
/*    */ 
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.datatransfer.Clipboard;
/*    */ import java.awt.datatransfer.DataFlavor;
/*    */ import java.awt.datatransfer.StringSelection;
/*    */ import java.awt.datatransfer.Transferable;
/*    */ import java.awt.datatransfer.UnsupportedFlavorException;
/*    */ import java.io.IOException;
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
/*    */ public class ClipboardManager
/*    */ {
/*    */   public static void main(String[] args) throws UnsupportedFlavorException, IOException {
/* 23 */     if (args.length > 0) {
/* 24 */       setContents(args[0]);
/*    */     }
/* 26 */     System.out.println("===CLIPBOARD===\n" + getContents() + "\n===END===");
/* 27 */     System.exit(0);
/*    */   }
/*    */   
/*    */   public static void setContents(String contents) {
/* 31 */     Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
/*    */ 
/*    */     
/* 34 */     if (contents != null) {
/* 35 */       StringSelection data = new StringSelection(contents);
/* 36 */       c.setContents(data, data);
/*    */     } 
/*    */   }
/*    */   
/*    */   public static String getContents() throws UnsupportedFlavorException, IOException {
/* 41 */     Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
/* 42 */     Transferable t = c.getContents(null);
/*    */     
/* 44 */     if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
/* 45 */       String data = (String)t.getTransferData(DataFlavor.stringFlavor);
/* 46 */       return data;
/*    */     } 
/* 48 */     return "<empty>";
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\ClipboardManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */