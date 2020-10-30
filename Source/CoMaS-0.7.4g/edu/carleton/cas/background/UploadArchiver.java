package edu.carleton.cas.background;

import edu.carleton.cas.constants.Shared;
import edu.carleton.cas.exam.Invigilator;
import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.utility.ClientHelper;
import edu.carleton.cas.utility.Sleeper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

public class UploadArchiver extends Archiver {
  private String passkey;
  
  public UploadArchiver(Invigilator login, String target, String service, String passkey, String name) {
    super(login, target, service, name);
    this.passkey = passkey;
  }
  
  public boolean doWork(Object obj) throws IOException {
    String item = (String)obj;
    return uploadArchive(new File(item), this.target, this.type);
  }
  
  public synchronized boolean uploadArchive(File file, String target, String service) throws IOException {
    boolean rtn;
    if (!this.login.isAllowedToUpload)
      return true; 
    long timeInMillisBefore = System.currentTimeMillis();
    Client client = ClientHelper.createClient(Shared.PROTOCOL);
    client.register(MultiPartFeature.class);
    WebTarget webTarget = client.target(target).path(service);
    Invocation.Builder invocationBuilder = webTarget.request(new MediaType[] { MediaType.MULTIPART_FORM_DATA_TYPE });
    invocationBuilder.accept(new String[] { "application/json" });
    FileDataBodyPart filePart = new FileDataBodyPart("file", file);
    FormDataMultiPart fdmp = new FormDataMultiPart();
    fdmp.field("passkey", this.passkey);
    fdmp.field("course", this.login.course);
    fdmp.field("activity", this.login.activity);
    MultiPart multipartEntity = fdmp.bodyPart((BodyPart)filePart);
    Response response = null;
    try {
      response = invocationBuilder.post(Entity.entity(fdmp, fdmp.getMediaType()));
      if (response.getStatus() == 401) {
        this.login.alert("Illegal version detected (" + Shared.VERSION + ").\nEnding session");
        Sleeper.sleepAndExit(5000, -5);
      } 
      rtn = (response.getStatus() < 204);
    } catch (Exception e) {
      rtn = false;
    } 
    fdmp.cleanup();
    fdmp.close();
    multipartEntity.cleanup();
    multipartEntity.close();
    long timeInMillisAfter = System.currentTimeMillis();
    if (rtn) {
      Logger.log(Level.CONFIG, file.getName(), " uploaded to: " + target + "/" + service + " (" + (timeInMillisAfter - timeInMillisBefore) + " msecs)");
    } else {
      Logger.log(Level.WARNING, file.getName(), " upload failed to: " + target + "/" + service + " (" + (timeInMillisAfter - timeInMillisBefore) + " msecs)");
    } 
    return rtn;
  }
}
