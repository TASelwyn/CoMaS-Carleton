/*     */ package edu.carleton.cas.utility;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.net.MalformedURLException;
/*     */ import java.util.Vector;
/*     */ import javax.media.Buffer;
/*     */ import javax.media.ControllerEvent;
/*     */ import javax.media.ControllerListener;
/*     */ import javax.media.DataSink;
/*     */ import javax.media.Format;
/*     */ import javax.media.Manager;
/*     */ import javax.media.MediaLocator;
/*     */ import javax.media.Processor;
/*     */ import javax.media.Time;
/*     */ import javax.media.control.TrackControl;
/*     */ import javax.media.datasink.DataSinkEvent;
/*     */ import javax.media.datasink.DataSinkListener;
/*     */ import javax.media.format.VideoFormat;
/*     */ import javax.media.protocol.ContentDescriptor;
/*     */ import javax.media.protocol.DataSource;
/*     */ import javax.media.protocol.PullBufferDataSource;
/*     */ import javax.media.protocol.PullBufferStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JpegImagesToMovie
/*     */   implements ControllerListener, DataSinkListener
/*     */ {
/*     */   public boolean doIt(int width, int height, int frameRate, Vector<String> inFiles, MediaLocator outML) throws MalformedURLException {
/*     */     Processor p;
/*  74 */     ImageDataSource ids = new ImageDataSource(width, height, frameRate, inFiles);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  81 */       p = Manager.createProcessor((DataSource)ids);
/*  82 */     } catch (Exception e) {
/*  83 */       System.err.println("Yikes!  Cannot create a processor from the data source.");
/*  84 */       return false;
/*     */     } 
/*     */     
/*  87 */     p.addControllerListener(this);
/*     */ 
/*     */ 
/*     */     
/*  91 */     p.configure();
/*  92 */     if (!waitForState(p, 180)) {
/*  93 */       System.err.println("Failed to configure the processor.");
/*  94 */       return false;
/*     */     } 
/*     */ 
/*     */     
/*  98 */     p.setContentDescriptor(new ContentDescriptor("video.quicktime"));
/*     */ 
/*     */ 
/*     */     
/* 102 */     TrackControl[] tcs = p.getTrackControls();
/* 103 */     Format[] f = tcs[0].getSupportedFormats();
/* 104 */     if (f == null || f.length <= 0) {
/* 105 */       System.err.println("The mux does not support the input format: " + tcs[0].getFormat());
/* 106 */       return false;
/*     */     } 
/*     */     
/* 109 */     tcs[0].setFormat(f[0]);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 115 */     p.realize();
/* 116 */     if (!waitForState(p, 300)) {
/* 117 */       System.err.println("Failed to realize the processor.");
/* 118 */       return false;
/*     */     } 
/*     */     
/*     */     DataSink dsink;
/*     */     
/* 123 */     if ((dsink = createDataSink(p, outML)) == null) {
/* 124 */       System.err.println("Failed to create a DataSink for the given output MediaLocator: " + outML);
/* 125 */       return false;
/*     */     } 
/*     */     
/* 128 */     dsink.addDataSinkListener(this);
/* 129 */     this.fileDone = false;
/*     */     
/* 131 */     System.out.println("Generating the video : " + outML.getURL().toString());
/*     */ 
/*     */     
/*     */     try {
/* 135 */       p.start();
/* 136 */       dsink.start();
/* 137 */     } catch (IOException e) {
/* 138 */       System.err.println("IO error during processing");
/* 139 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 143 */     waitForFileDone();
/*     */ 
/*     */     
/*     */     try {
/* 147 */       dsink.close();
/* 148 */     } catch (Exception exception) {}
/*     */     
/* 150 */     p.removeControllerListener(this);
/*     */     
/* 152 */     System.out.println("Video creation completed.");
/* 153 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   DataSink createDataSink(Processor p, MediaLocator outML) {
/*     */     DataSink dsink;
/*     */     DataSource ds;
/* 163 */     if ((ds = p.getDataOutput()) == null) {
/* 164 */       System.err.println("Something is really wrong: the processor does not have an output DataSource");
/* 165 */       return null;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 172 */       dsink = Manager.createDataSink(ds, outML);
/* 173 */       dsink.open();
/* 174 */     } catch (Exception e) {
/* 175 */       System.err.println("Cannot create the DataSink: " + e);
/* 176 */       return null;
/*     */     } 
/*     */     
/* 179 */     return dsink;
/*     */   }
/*     */   
/* 182 */   Object waitSync = new Object();
/*     */ 
/*     */   
/*     */   boolean stateTransitionOK = true;
/*     */ 
/*     */ 
/*     */   
/*     */   boolean waitForState(Processor p, int state) {
/* 190 */     synchronized (this.waitSync) {
/*     */       try {
/* 192 */         while (p.getState() < state && this.stateTransitionOK)
/* 193 */           this.waitSync.wait(); 
/* 194 */       } catch (Exception exception) {}
/*     */     } 
/*     */     
/* 197 */     return this.stateTransitionOK;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void controllerUpdate(ControllerEvent evt) {
/* 205 */     if (evt instanceof javax.media.ConfigureCompleteEvent || evt instanceof javax.media.RealizeCompleteEvent || 
/* 206 */       evt instanceof javax.media.PrefetchCompleteEvent) {
/* 207 */       synchronized (this.waitSync) {
/* 208 */         this.stateTransitionOK = true;
/* 209 */         this.waitSync.notifyAll();
/*     */       } 
/* 211 */     } else if (evt instanceof javax.media.ResourceUnavailableEvent) {
/* 212 */       synchronized (this.waitSync) {
/* 213 */         this.stateTransitionOK = false;
/* 214 */         this.waitSync.notifyAll();
/*     */       } 
/* 216 */     } else if (evt instanceof javax.media.EndOfMediaEvent) {
/* 217 */       evt.getSourceController().stop();
/* 218 */       evt.getSourceController().close();
/*     */     } 
/*     */   }
/*     */   
/* 222 */   Object waitFileSync = new Object();
/*     */   
/*     */   boolean fileDone = false;
/*     */   
/*     */   boolean fileSuccess = true;
/*     */ 
/*     */   
/*     */   boolean waitForFileDone() {
/* 230 */     synchronized (this.waitFileSync) {
/*     */       try {
/* 232 */         while (!this.fileDone)
/* 233 */           this.waitFileSync.wait(); 
/* 234 */       } catch (Exception exception) {}
/*     */     } 
/*     */     
/* 237 */     return this.fileSuccess;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dataSinkUpdate(DataSinkEvent evt) {
/* 245 */     if (evt instanceof javax.media.datasink.EndOfStreamEvent) {
/* 246 */       synchronized (this.waitFileSync) {
/* 247 */         this.fileDone = true;
/* 248 */         this.waitFileSync.notifyAll();
/*     */       } 
/* 250 */     } else if (evt instanceof javax.media.datasink.DataSinkErrorEvent) {
/* 251 */       synchronized (this.waitFileSync) {
/* 252 */         this.fileDone = true;
/* 253 */         this.fileSuccess = false;
/* 254 */         this.waitFileSync.notifyAll();
/*     */       } 
/*     */     } 
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void prUsage() {
/* 304 */     System.err.println(
/* 305 */         "Usage: java JpegImagesToMovie -w <width> -h <height> -f <frame rate> -o <output URL> <input JPEG file 1> <input JPEG file 2> ...");
/* 306 */     System.exit(-1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static MediaLocator createMediaLocator(String url) {
/*     */     MediaLocator ml;
/* 317 */     if (url.indexOf(":") > 0 && (ml = new MediaLocator(url)) != null) {
/* 318 */       return ml;
/*     */     }
/* 320 */     if (url.startsWith(File.separator)) {
/* 321 */       if ((ml = new MediaLocator("file:" + url)) != null)
/* 322 */         return ml; 
/*     */     } else {
/* 324 */       String file = "file:" + System.getProperty("user.dir") + File.separator + url;
/* 325 */       if ((ml = new MediaLocator(file)) != null) {
/* 326 */         return ml;
/*     */       }
/*     */     } 
/* 329 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   class ImageDataSource
/*     */     extends PullBufferDataSource
/*     */   {
/*     */     JpegImagesToMovie.ImageSourceStream[] streams;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     ImageDataSource(int width, int height, int frameRate, Vector<String> images) {
/* 346 */       this.streams = new JpegImagesToMovie.ImageSourceStream[1];
/* 347 */       this.streams[0] = new JpegImagesToMovie.ImageSourceStream(width, height, frameRate, images);
/*     */     }
/*     */ 
/*     */     
/*     */     public void setLocator(MediaLocator source) {}
/*     */     
/*     */     public MediaLocator getLocator() {
/* 354 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getContentType() {
/* 362 */       return "raw";
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void connect() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void disconnect() {}
/*     */ 
/*     */     
/*     */     public void start() {}
/*     */ 
/*     */     
/*     */     public void stop() {}
/*     */ 
/*     */     
/*     */     public PullBufferStream[] getStreams() {
/* 381 */       return (PullBufferStream[])this.streams;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Time getDuration() {
/* 389 */       return DURATION_UNKNOWN;
/*     */     }
/*     */     
/*     */     public Object[] getControls() {
/* 393 */       return new Object[0];
/*     */     }
/*     */     
/*     */     public Object getControl(String type) {
/* 397 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   class ImageSourceStream
/*     */     implements PullBufferStream
/*     */   {
/*     */     Vector<String> images;
/*     */     
/*     */     int width;
/*     */     int height;
/*     */     VideoFormat format;
/* 410 */     int nextImage = 0;
/*     */     boolean ended = false;
/*     */     
/*     */     public ImageSourceStream(int width, int height, int frameRate, Vector<String> images) {
/* 414 */       this.width = width;
/* 415 */       this.height = height;
/* 416 */       this.images = images;
/*     */       
/* 418 */       this.format = new VideoFormat("jpeg", new Dimension(width, height), -1, 
/* 419 */           Format.byteArray, frameRate);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean willReadBlock() {
/* 426 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void read(Buffer buf) throws IOException {
/* 436 */       if (this.nextImage >= this.images.size()) {
/*     */ 
/*     */         
/* 439 */         buf.setEOM(true);
/* 440 */         buf.setOffset(0);
/* 441 */         buf.setLength(0);
/* 442 */         this.ended = true;
/*     */         
/*     */         return;
/*     */       } 
/* 446 */       String imageFile = this.images.elementAt(this.nextImage);
/* 447 */       this.nextImage++;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 453 */       RandomAccessFile raFile = new RandomAccessFile(imageFile, "r");
/*     */       
/* 455 */       byte[] data = null;
/*     */ 
/*     */ 
/*     */       
/* 459 */       if (buf.getData() instanceof byte[]) {
/* 460 */         data = (byte[])buf.getData();
/*     */       }
/*     */       
/* 463 */       if (data == null || data.length < raFile.length()) {
/* 464 */         data = new byte[(int)raFile.length()];
/* 465 */         buf.setData(data);
/*     */       } 
/*     */ 
/*     */       
/* 469 */       raFile.readFully(data, 0, (int)raFile.length());
/*     */ 
/*     */ 
/*     */       
/* 473 */       buf.setOffset(0);
/* 474 */       buf.setLength((int)raFile.length());
/* 475 */       buf.setFormat((Format)this.format);
/* 476 */       buf.setFlags(buf.getFlags() | 0x10);
/*     */ 
/*     */       
/* 479 */       raFile.close();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Format getFormat() {
/* 486 */       return (Format)this.format;
/*     */     }
/*     */     
/*     */     public ContentDescriptor getContentDescriptor() {
/* 490 */       return new ContentDescriptor("raw");
/*     */     }
/*     */     
/*     */     public long getContentLength() {
/* 494 */       return 0L;
/*     */     }
/*     */     
/*     */     public boolean endOfStream() {
/* 498 */       return this.ended;
/*     */     }
/*     */     
/*     */     public Object[] getControls() {
/* 502 */       return new Object[0];
/*     */     }
/*     */     
/*     */     public Object getControl(String type) {
/* 506 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\ca\\utility\JpegImagesToMovie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */