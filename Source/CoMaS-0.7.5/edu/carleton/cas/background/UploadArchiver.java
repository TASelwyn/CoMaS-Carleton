package edu.carleton.cas.background;

import edu.carleton.cas.constants.ClientShared;
import edu.carleton.cas.exam.Invigilator;
import edu.carleton.cas.logging.Level;
import edu.carleton.cas.logging.Logger;
import edu.carleton.cas.utility.ClientHelper;
import java.io.File;
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
  
  public boolean doWork(Object obj) {
    String item = (String)obj;
    return uploadArchive(new File(item), this.target, this.type);
  }
  
  public synchronized boolean uploadArchive(File file, String target, String service) {
    boolean rtn;
    int status;
    if (!this.login.isAllowedToUpload)
      return true; 
    long timeInMillisBefore = System.currentTimeMillis();
    Client client = ClientHelper.createClient(ClientShared.PROTOCOL);
    client.register(MultiPartFeature.class);
    WebTarget webTarget = client.target(target).path(service);
    Invocation.Builder invocationBuilder = webTarget.request(new MediaType[] { MediaType.MULTIPART_FORM_DATA_TYPE });
    invocationBuilder.accept(new String[] { "application/json" });
    invocationBuilder.cookie("token", this.login.getToken());
    FileDataBodyPart filePart = new FileDataBodyPart("file", file);
    FormDataMultiPart fdmp = new FormDataMultiPart();
    fdmp.field("passkey", this.passkey);
    fdmp.field("course", this.login.course);
    fdmp.field("activity", this.login.activity);
    MultiPart multipartEntity = fdmp.bodyPart((BodyPart)filePart);
    Response response = null;
    try {
      response = invocationBuilder.post(Entity.entity(fdmp, fdmp.getMediaType()));
      rtn = (response.getStatus() < 204);
    } catch (Exception e) {
      rtn = false;
    } finally {
      try {
        fdmp.cleanup();
        fdmp.close();
        multipartEntity.cleanup();
        multipartEntity.close();
      } catch (Exception exception) {}
    } 
    long timeInMillisAfter = System.currentTimeMillis();
    if (response == null) {
      status = 503;
    } else {
      status = response.getStatus();
    } 
    if (rtn) {
      String msg = String.format("[%d] %s uploaded to: %s%s (%d msecs)", new Object[] { Integer.valueOf(status), file.getName(), target, 
            service, Long.valueOf(timeInMillisAfter - timeInMillisBefore) });
      Logger.log(Level.CONFIG, msg, "");
    } else {
      String msg = String.format("[%d] %s upload failed to: %s%s (%d msecs)", new Object[] { Integer.valueOf(status), file.getName(), 
            target, service, Long.valueOf(timeInMillisAfter - timeInMillisBefore) });
      Logger.log((Level)Level.NOTED, msg, "");
    } 
    return rtn;
  }
}
