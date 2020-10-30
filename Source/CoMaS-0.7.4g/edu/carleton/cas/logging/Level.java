/*    */ package edu.carleton.cas.logging;
/*    */ 
/*    */ import java.util.logging.Level;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Level
/*    */   extends Level
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 16 */   public static final Level LOGGED = new Level("LOGGED", SEVERE.intValue() + 1);
/*    */   
/* 18 */   public static final Level NOTED = new Level("NOTED", WARNING.intValue() + 1);
/*    */   
/*    */   protected Level(String name, int value) {
/* 21 */     super(name, value);
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\logging\Level.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */