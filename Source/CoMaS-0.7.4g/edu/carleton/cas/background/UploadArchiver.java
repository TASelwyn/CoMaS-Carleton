/*    */ package edu.carleton.cas.background;
/*    */ 
/*    */ import edu.carleton.cas.constants.Shared;
/*    */ import edu.carleton.cas.exam.Invigilator;
/*    */ import edu.carleton.cas.logging.Logger;
/*    */ import edu.carleton.cas.utility.ClientHelper;
/*    */ import edu.carleton.cas.utility.Sleeper;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.util.logging.Level;
/*    */ import javax.ws.rs.client.Client;
/*    */ import javax.ws.rs.client.Entity;
/*    */ import javax.ws.rs.client.Invocation;
/*    */ import javax.ws.rs.client.WebTarget;
/*    */ import javax.ws.rs.core.MediaType;
/*    */ import javax.ws.rs.core.Response;
/*    */ import org.glassfish.jersey.media.multipart.BodyPart;
/*    */ import org.glassfish.jersey.media.multipart.FormDataMultiPart;
/*    */ import org.glassfish.jersey.media.multipart.MultiPart;
/*    */ import org.glassfish.jersey.media.multipart.MultiPartFeature;
/*    */ import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UploadArchiver
/*    */   extends Archiver
/*    */ {
/*    */   private String passkey;
/*    */   
/*    */   public UploadArchiver(Invigilator login, String target, String service, String passkey, String name) {
/* 38 */     super(login, target, service, name);
/* 39 */     this.passkey = passkey;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean doWork(Object obj) throws IOException {
/* 47 */     String item = (String)obj;
/* 48 */     return uploadArchive(new File(item), this.target, this.type);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public synchronized boolean uploadArchive(File file, String target, String service) throws IOException {
/*    */     boolean rtn;
/* 55 */     if (!this.login.isAllowedToUpload) {
/* 56 */       return true;
/*    */     }
/* 58 */     long timeInMillisBefore = System.currentTimeMillis();
/* 59 */     Client client = ClientHelper.createClient(Shared.PROTOCOL);
/* 60 */     client.register(MultiPartFeature.class);
/* 61 */     WebTarget webTarget = client.target(target).path(service);
/* 62 */     Invocation.Builder invocationBuilder = webTarget.request(new MediaType[] { MediaType.MULTIPART_FORM_DATA_TYPE });
/* 63 */     invocationBuilder.accept(new String[] { "application/json" });
/*    */     
/* 65 */     FileDataBodyPart filePart = new FileDataBodyPart("file", file);
/* 66 */     FormDataMultiPart fdmp = new FormDataMultiPart();
/* 67 */     fdmp.field("passkey", this.passkey);
/* 68 */     fdmp.field("course", this.login.course);
/* 69 */     fdmp.field("activity", this.login.activity);
/* 70 */     MultiPart multipartEntity = fdmp.bodyPart((BodyPart)filePart);
/*    */     
/* 72 */     Response response = null;
/*    */     
/*    */     try {
/* 75 */       response = invocationBuilder.post(Entity.entity(fdmp, fdmp.getMediaType()));
/* 76 */       if (response.getStatus() == 401) {
/* 77 */         this.login.alert("Illegal version detected (" + Shared.VERSION + ").\nEnding session");
/* 78 */         Sleeper.sleepAndExit(5000, -5);
/*    */       } 
/* 80 */       rtn = (response.getStatus() < 204);
/* 81 */     } catch (Exception e) {
/*    */       
/* 83 */       rtn = false;
/*    */     } 
/*    */     
/* 86 */     fdmp.cleanup();
/* 87 */     fdmp.close();
/* 88 */     multipartEntity.cleanup();
/* 89 */     multipartEntity.close();
/* 90 */     long timeInMillisAfter = System.currentTimeMillis();
/*    */     
/* 92 */     if (rtn) {
/* 93 */       Logger.log(Level.CONFIG, file.getName(), " uploaded to: " + target + "/" + service + " (" + (timeInMillisAfter - timeInMillisBefore) + " msecs)");
/*    */     } else {
/* 95 */       Logger.log(Level.WARNING, file.getName(), " upload failed to: " + target + "/" + service + " (" + (timeInMillisAfter - timeInMillisBefore) + " msecs)");
/* 96 */     }  return rtn;
/*    */   }
/*    */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\background\UploadArchiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */