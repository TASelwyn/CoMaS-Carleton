package edu.carleton.cas.background;

import edu.carleton.cas.constants.ClientShared;
import edu.carleton.cas.exam.Invigilator;
import edu.carleton.cas.logging.Level;
import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.utility.ClientHelper;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

public class LogArchiver extends Archiver {
  private HashMap<String, Long> logs;
  
  public LogArchiver(Invigilator login, String target, String type, String name) {
    super(login, target, type, name);
    this.logs = new HashMap<>();
  }
  
  public synchronized void put(Level level, String description) {
    put(new LogRecord(level, description));
  }
  
  public boolean doWork(Object obj) {
    LogRecord item = (LogRecord)obj;
    return log(item.getLevel(), item.getMessage());
  }
  
  public synchronized boolean log(Level severity, String description) {
    if (!this.login.isAllowedToUpload) {
      System.out.println("Not allowed to upload logs");
      return true;
    } 
    try {
      Long logged = Long.valueOf(((Long)this.logs.getOrDefault(description, Long.valueOf(0L))).longValue() + 1L);
      if (logged.longValue() % ClientShared.LOG_GENERATION_FREQUENCY == 1L) {
        String logMsg;
        if (logged.longValue() > 1L) {
          logMsg = String.valueOf(description) + " (" + logged + ")";
        } else {
          logMsg = description;
        } 
        Client client = ClientHelper.createClient(ClientShared.PROTOCOL);
        WebTarget webTarget = client.target(ClientShared.BASE_LOG).path(ClientShared.LOG_PATH);
        Invocation.Builder invocationBuilder = webTarget.request(new String[] { "application/x-www-form-urlencoded" });
        invocationBuilder.accept(new String[] { "application/json" });
        invocationBuilder.cookie("token", this.login.getToken());
        Form form = new Form();
        form.param("passkey", ClientShared.PASSKEY_LOG);
        form.param("name", this.login.name);
        form.param("id", this.login.id);
        form.param("course", this.login.course);
        form.param("activity", this.login.activity);
        form.param("url", String.valueOf(ClientShared.LOG_URL) + "s/" + this.login.course + "/" + this.login.activity + "/" + this.login.name);
        form.param("severity", severity.getName());
        form.param("description", logMsg);
        form.param("time", System.currentTimeMillis());
        Response response = invocationBuilder.post(Entity.entity(form, "application/x-www-form-urlencoded"));
        Logger.log(severity, "", " [" + response.getStatus() + "] " + logMsg);
        boolean rtnValue = (response.getStatus() < 204);
        if (rtnValue)
          this.logs.put(description, logged); 
        return rtnValue;
      } 
      this.logs.put(description, logged);
      return true;
    } catch (Exception e) {
      Logger.log((Level)Level.NOTED, "Failed to log: ", description);
      return false;
    } 
  }
}
