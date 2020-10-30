/*    */ package edu.carleton.cas.utility;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Named
/*    */ {
/*    */   public static String canonical(String str) {
/* 12 */     return str.replace(' ', '-');
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String quoted(String str) {
/* 23 */     if (str.charAt(0) != '"') {
/* 24 */       return String.format("\"%s\"", new Object[] { str });
/*    */     }
/* 26 */     return str;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String unquoted(String str) {
/* 36 */     if (str.charAt(0) == '"') {
/* 37 */       return str.substring(1, str.length() - 1);
/*    */     }
/* 39 */     return str;
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\Named.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */