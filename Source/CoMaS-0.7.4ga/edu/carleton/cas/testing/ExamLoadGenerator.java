package edu.carleton.cas.testing;

import edu.carleton.cas.constants.Shared;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ExamLoadGenerator {
  private static final String LOGGING_LEVEL = "FINE";
  
  private static final String VERSION = "0.6.5";
  
  public static void main(String[] args) {
    if (args.length < 4) {
      System.err.println("Usage: ExamLoadGenerator fileName course activity dir <start> <end>");
      System.err.println("       filename = name containing a class list; e.g. /Users/me/Desktop/classlist.csv");
      System.err.println("       course   = a registered course; e.g., comp4107");
      System.err.println("       activity = a course activity; e.g., exam");
      System.err.println("       dir      = a working directory; e.g., /Users/me/Desktop");
      System.err.println("       server   = name of a CoMaS server; e.g., comas.cogerent.com");
      System.err.println("       start    = an integer indicating the student to start simulating");
      System.err.println("       end      = an integer indicating the last student to simulate");
      System.err.println("Note:  start and end are optional");
      System.exit(-1);
    } 
    File file = new File(args[0]);
    if (!file.exists()) {
      System.err.println("File " + args[0] + " does not exist");
      System.exit(-2);
    } 
    int start = -1;
    int end = -1;
    if (args.length == 7)
      try {
        start = Integer.parseInt(args[5]);
        end = Integer.parseInt(args[6]);
      } catch (NumberFormatException e) {
        System.err.println("Could not parse either " + args[5] + " or " + args[6]);
        start = -1;
        end = -1;
      }  
    String course = args[1].trim();
    String activity = args[2].trim();
    String server = args[4].trim();
    String comasDirectory = String.valueOf(Shared.DIR) + File.separator;
    String dir = args[3];
    if (!(new File(dir)).exists()) {
      System.err.println("Cannot access working directory: " + dir);
      System.exit(-5);
    } 
    ArrayList<SimulatedStudentTask> students = new ArrayList<>();
    BufferedReader reader = null;
    int i = 0;
    try {
      reader = new BufferedReader(new FileReader(file));
      String line = reader.readLine();
      while ((line = reader.readLine()) != null) {
        String[] token = line.split(",");
        if ((start == -1 && end == -1) || (i >= start && i <= end)) {
          String commandLine = String.format(
              "java -jar CoMaS-%s.jar -name %s %s -id %s -course %s -activity %s -comas %s%s -logging %s -server %s", new Object[] { "0.6.5", token[2].toLowerCase().trim(), token[1].toLowerCase().trim(), token[0].toLowerCase().trim(), 
                course, activity, comasDirectory, token[0].toLowerCase().trim(), 
                "FINE", server });
          SimulatedStudentTask student = new SimulatedStudentTask(commandLine, dir);
          student.run();
          students.add(student);
        } 
        i++;
      } 
    } catch (FileNotFoundException e) {
      System.err.println("File " + args[0] + " could not be read");
      System.exit(-3);
    } catch (IOException e) {
      System.err.println("File " + args[0] + " could not be read: " + e.getMessage());
      System.exit(-4);
    } finally {
      if (reader != null)
        try {
          reader.close();
        } catch (IOException iOException) {} 
    } 
    try {
      System.out.print("Press Enter to stop ...");
      System.in.read();
    } catch (IOException iOException) {}
    for (SimulatedStudentTask student : students)
      student.end(); 
    System.out.println("Simulated " + students.size() + " " + args[1] + "/" + args[2] + " exams");
  }
  
  public static class SimulatedStudentTask {
    private final String commandLine;
    
    private final String dir;
    
    private Process process;
    
    public SimulatedStudentTask(String commandLine, String dir) {
      this.commandLine = commandLine;
      this.dir = dir;
    }
    
    public void run() {
      System.out.println(this.commandLine);
      try {
        this.process = Runtime.getRuntime().exec(this.commandLine, (String[])null, new File(this.dir));
      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
      } 
    }
    
    public void end() {
      if (this.process != null)
        this.process.destroyForcibly(); 
    }
  }
}
