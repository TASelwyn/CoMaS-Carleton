/*    */ package edu.carleton.cas.security;
/*    */ 
/*    */ import java.util.HashSet;
/*    */ import java.util.Random;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UniqueDigits
/*    */ {
/*    */   public HashSet<String> digits;
/*    */   public Random random;
/* 16 */   public static int UNIQUE_DIGITS_REQUIRED = 15;
/* 17 */   public static int LENGTH_REQUIRED = 4;
/*    */   private int len;
/*    */   
/*    */   public UniqueDigits() {
/* 21 */     this(LENGTH_REQUIRED);
/*    */   }
/*    */   
/*    */   public UniqueDigits(int len) {
/* 25 */     this.len = len;
/* 26 */     this.digits = new HashSet<>();
/* 27 */     this.random = new Random();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String unique() {
/* 35 */     String u = "";
/*    */     while (true) {
/* 37 */       u = "";
/* 38 */       for (int i = 0; i < this.len; i++) {
/* 39 */         u = String.valueOf(u) + this.random.nextInt(10);
/*    */       }
/* 41 */       if (!this.digits.contains(u)) {
/* 42 */         this.digits.add(u);
/* 43 */         return u;
/*    */       } 
/*    */     } 
/*    */   } public static void main(String[] args) {
/* 47 */     UniqueDigits u = new UniqueDigits();
/*    */     
/* 49 */     for (int i = 0; i < UNIQUE_DIGITS_REQUIRED; i++)
/* 50 */       System.out.println(u.unique()); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\security\UniqueDigits.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */