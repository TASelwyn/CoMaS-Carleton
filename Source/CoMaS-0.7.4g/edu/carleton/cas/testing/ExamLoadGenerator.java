/*     */ package edu.carleton.cas.testing;
/*     */ 
/*     */ import edu.carleton.cas.constants.Shared;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
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
/*     */ public class ExamLoadGenerator
/*     */ {
/*     */   private static final String LOGGING_LEVEL = "FINE";
/*     */   private static final String VERSION = "0.6.5";
/*     */   
/*     */   public static void main(String[] args) {
/*  35 */     if (args.length < 4) {
/*  36 */       System.err.println("Usage: ExamLoadGenerator fileName course activity dir <start> <end>");
/*  37 */       System.err.println("       filename = name containing a class list; e.g. /Users/me/Desktop/classlist.csv");
/*  38 */       System.err.println("       course   = a registered course; e.g., comp4107");
/*  39 */       System.err.println("       activity = a course activity; e.g., exam");
/*  40 */       System.err.println("       dir      = a working directory; e.g., /Users/me/Desktop");
/*  41 */       System.err.println("       server   = name of a CoMaS server; e.g., comas.cogerent.com");
/*  42 */       System.err.println("       start    = an integer indicating the student to start simulating");
/*  43 */       System.err.println("       end      = an integer indicating the last student to simulate");
/*  44 */       System.err.println("Note:  start and end are optional");
/*  45 */       System.exit(-1);
/*     */     } 
/*  47 */     File file = new File(args[0]);
/*  48 */     if (!file.exists()) {
/*  49 */       System.err.println("File " + args[0] + " does not exist");
/*  50 */       System.exit(-2);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  56 */     int start = -1;
/*  57 */     int end = -1;
/*  58 */     if (args.length == 7) {
/*     */       try {
/*  60 */         start = Integer.parseInt(args[5]);
/*  61 */         end = Integer.parseInt(args[6]);
/*  62 */       } catch (NumberFormatException e) {
/*  63 */         System.err.println("Could not parse either " + args[5] + " or " + args[6]);
/*  64 */         start = -1;
/*  65 */         end = -1;
/*     */       } 
/*     */     }
/*     */     
/*  69 */     String course = args[1].trim();
/*  70 */     String activity = args[2].trim();
/*  71 */     String server = args[4].trim();
/*     */ 
/*     */ 
/*     */     
/*  75 */     String comasDirectory = String.valueOf(Shared.DIR) + File.separator;
/*  76 */     String dir = args[3];
/*  77 */     if (!(new File(dir)).exists()) {
/*  78 */       System.err.println("Cannot access working directory: " + dir);
/*  79 */       System.exit(-5);
/*     */     } 
/*     */     
/*  82 */     ArrayList<SimulatedStudentTask> students = new ArrayList<>();
/*  83 */     BufferedReader reader = null;
/*  84 */     int i = 0;
/*     */     try {
/*  86 */       reader = new BufferedReader(new FileReader(file));
/*     */ 
/*     */ 
/*     */       
/*  90 */       String line = reader.readLine();
/*     */ 
/*     */       
/*  93 */       while ((line = reader.readLine()) != null) {
/*  94 */         String[] token = line.split(",");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 102 */         if ((start == -1 && end == -1) || (i >= start && i <= end)) {
/* 103 */           String commandLine = String.format(
/* 104 */               "java -jar CoMaS-%s.jar -name %s %s -id %s -course %s -activity %s -comas %s%s -logging %s -server %s", new Object[] {
/* 105 */                 "0.6.5", token[2].toLowerCase().trim(), token[1].toLowerCase().trim(), token[0].toLowerCase().trim(), 
/* 106 */                 course, activity, comasDirectory, token[0].toLowerCase().trim(), 
/* 107 */                 "FINE", server });
/* 108 */           SimulatedStudentTask student = new SimulatedStudentTask(commandLine, dir);
/* 109 */           student.run();
/* 110 */           students.add(student);
/*     */         } 
/* 112 */         i++;
/*     */       } 
/* 114 */     } catch (FileNotFoundException e) {
/* 115 */       System.err.println("File " + args[0] + " could not be read");
/* 116 */       System.exit(-3);
/* 117 */     } catch (IOException e) {
/* 118 */       System.err.println("File " + args[0] + " could not be read: " + e.getMessage());
/* 119 */       System.exit(-4);
/*     */     } finally {
/* 121 */       if (reader != null) {
/*     */         try {
/* 123 */           reader.close();
/* 124 */         } catch (IOException iOException) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 134 */       System.out.print("Press Enter to stop ...");
/* 135 */       System.in.read();
/* 136 */     } catch (IOException iOException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 142 */     for (SimulatedStudentTask student : students) {
/* 143 */       student.end();
/*     */     }
/*     */     
/* 146 */     System.out.println("Simulated " + students.size() + " " + args[1] + "/" + args[2] + " exams");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class SimulatedStudentTask
/*     */   {
/*     */     private final String commandLine;
/*     */ 
/*     */     
/*     */     private final String dir;
/*     */ 
/*     */     
/*     */     private Process process;
/*     */ 
/*     */     
/*     */     public SimulatedStudentTask(String commandLine, String dir) {
/* 163 */       this.commandLine = commandLine;
/* 164 */       this.dir = dir;
/*     */     }
/*     */     
/*     */     public void run() {
/* 168 */       System.out.println(this.commandLine);
/*     */       try {
/* 170 */         this.process = Runtime.getRuntime().exec(this.commandLine, (String[])null, new File(this.dir));
/* 171 */       } catch (Exception e) {
/* 172 */         System.err.println("Error: " + e.getMessage());
/*     */       } 
/*     */     }
/*     */     
/*     */     public void end() {
/* 177 */       if (this.process != null)
/* 178 */         this.process.destroyForcibly(); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\testing\ExamLoadGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */