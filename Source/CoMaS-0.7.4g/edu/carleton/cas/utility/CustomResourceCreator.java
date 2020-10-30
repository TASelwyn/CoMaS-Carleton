/*     */ package edu.carleton.cas.utility;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class CustomResourceCreator
/*     */ {
/*  29 */   private static String INSERT = "===INSERT===";
/*     */   
/*     */   public static boolean generate(File file, Set<Map.Entry<String, String>> vars, File dir) throws IOException, FileNotFoundException {
/*  32 */     StringBuffer buff = new StringBuffer();
/*     */     try {
/*  34 */       BufferedReader br = new BufferedReader(
/*  35 */           new InputStreamReader(new FileInputStream(file)));
/*     */       String inputLine;
/*  37 */       while ((inputLine = br.readLine()) != null) {
/*  38 */         if (inputLine.contains(INSERT)) {
/*  39 */           for (Map.Entry<String, String> var : vars) {
/*  40 */             String value = ((String)var.getValue()).toString();
/*  41 */             buff.append("var ");
/*  42 */             buff.append(var.getKey());
/*  43 */             buff.append(" = ");
/*  44 */             buff.append(value);
/*  45 */             buff.append(";\n");
/*     */           }  continue;
/*     */         } 
/*  48 */         buff.append(inputLine);
/*  49 */         buff.append("\n");
/*     */       } 
/*     */       
/*  52 */       br.close();
/*     */       
/*  54 */       File ofile = new File(dir, file.getName());
/*  55 */       OutputStream out = new BufferedOutputStream(new FileOutputStream(ofile));
/*  56 */       out.write(buff.toString().getBytes());
/*  57 */       out.close();
/*  58 */     } catch (IOException e) {
/*  59 */       e.printStackTrace();
/*  60 */       return false;
/*     */     } 
/*  62 */     return true;
/*     */   }
/*     */   
/*     */   public static boolean generate(URL url, Set<Map.Entry<String, String>> vars, File dir) throws IOException, FileNotFoundException {
/*  66 */     StringBuffer buff = new StringBuffer();
/*     */     try {
/*  68 */       URLConnection conn = url.openConnection();
/*  69 */       BufferedReader br = new BufferedReader(
/*  70 */           new InputStreamReader(conn.getInputStream()));
/*     */       String inputLine;
/*  72 */       while ((inputLine = br.readLine()) != null) {
/*  73 */         if (inputLine.contains(INSERT)) {
/*  74 */           for (Map.Entry<String, String> var : vars) {
/*  75 */             String value = ((String)var.getValue()).toString();
/*  76 */             buff.append("var ");
/*  77 */             buff.append(var.getKey());
/*  78 */             buff.append(" = ");
/*  79 */             buff.append(value);
/*  80 */             buff.append(";\n");
/*     */           }  continue;
/*     */         } 
/*  83 */         buff.append(inputLine);
/*  84 */         buff.append("\n");
/*     */       } 
/*     */       
/*  87 */       br.close();
/*     */       
/*  89 */       File file = new File(dir, (new File(url.getFile())).getName());
/*  90 */       OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
/*  91 */       out.write(buff.toString().getBytes());
/*  92 */       out.close();
/*  93 */     } catch (MalformedURLException e) {
/*  94 */       e.printStackTrace();
/*  95 */       return false;
/*  96 */     } catch (IOException e) {
/*  97 */       e.printStackTrace();
/*  98 */       return false;
/*     */     } 
/* 100 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\CustomResourceCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */