package edu.carleton.cas.jetty.embedded;

import edu.carleton.cas.constants.ClientShared;
import edu.carleton.cas.file.Utils;
import edu.carleton.cas.logging.Logger;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

public class ServletProcessor implements Runnable, Closeable {
  private static ServletProcessor instance;
  
  private int port;
  
  private Thread thread;
  
  private String course;
  
  private String activity;
  
  public ServletProcessor(String course, String activity) {
    this.port = 8888;
    this.course = course;
    this.activity = activity;
  }
  
  public ServletProcessor(int port, String course, String activity) {
    this.port = port;
    this.course = course;
    this.activity = activity;
  }
  
  public static ServletProcessor getInstance(String course, String activity) {
    if (instance == null) {
      Logger.log(Level.INFO, "Web Server: ", "Starting");
      try {
        instance = new ServletProcessor(course, activity);
        instance.thread = new Thread(instance);
        instance.thread.start();
      } catch (Exception exception) {}
    } 
    return instance;
  }
  
  public void run() {
    try {
      Server server = new Server(this.port);
      ServletHandler handler = new ServletHandler();
      server.setHandler((Handler)handler);
      handler.addServletWithMapping(MultipleChoiceQuestionServlet.class, "/*");
      server.start();
      Logger.log(Level.INFO, "Web Server: ", "Running on " + instance.port);
      server.join();
    } catch (Exception e) {
      Logger.log(Level.SEVERE, "Web Server: ", "Failed to start on " + instance.port + "," + e.getMessage());
    } 
  }
  
  public static class MultipleChoiceQuestionServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String name = request.getRequestURI();
      String mimeType = "application/octet-stream";
      ServletOutputStream os = response.getOutputStream();
      FileInputStream fis = new FileInputStream(String.valueOf(ClientShared.getExamDirectory(ServletProcessor.instance.course, ServletProcessor.instance.activity)) + name);
      Utils.copyInputStream(fis, (OutputStream)os);
      response.setContentType(mimeType);
      response.setStatus(200);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      Enumeration<String> mapn = request.getParameterNames();
      String[] questionFileName = request.getParameterValues("QUESTION");
      if (questionFileName == null || questionFileName.length == 0) {
        String name = "";
        while (mapn.hasMoreElements())
          name = String.valueOf(name) + " " + (String)mapn.nextElement(); 
        Logger.log(Level.SEVERE, "Unknown question processed: ", name);
        response.setContentType("text/html");
        response.setStatus(200);
        response.getWriter().println("<h1>No question name found</h1>");
      } else {
        PrintWriter answerFile = new PrintWriter(
            String.valueOf(ClientShared.getExamDirectory(ServletProcessor.instance.course, ServletProcessor.instance.activity)) + File.separator + questionFileName[0] + ".txt");
        answerFile.append("# Generated by CoMaS on ");
        answerFile.append((new Date()).toString());
        answerFile.append(". DO NOT EDIT\n");
        while (mapn.hasMoreElements()) {
          String name = mapn.nextElement();
          String value = request.getParameterValues(name)[0];
          if (!name.equalsIgnoreCase("QUESTION")) {
            answerFile.append(name);
            answerFile.append("=");
            answerFile.append(value);
            answerFile.append("\n");
          } 
        } 
        answerFile.close();
        response.setContentType("text/plain");
        response.setStatus(200);
        response.getWriter().println("Saved answers for " + questionFileName[0]);
      } 
    }
  }
  
  public void close() throws IOException {
    if (this.thread != null) {
      this.thread.interrupt();
      this.thread = null;
    } 
    if (instance != null)
      instance = null; 
  }
}
