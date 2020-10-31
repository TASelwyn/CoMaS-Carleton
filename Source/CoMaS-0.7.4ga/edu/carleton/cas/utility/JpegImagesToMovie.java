package edu.carleton.cas.utility;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.util.Vector;
import javax.media.Buffer;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.Time;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;

public class JpegImagesToMovie implements ControllerListener, DataSinkListener {
  public boolean doIt(int width, int height, int frameRate, Vector<String> inFiles, MediaLocator outML) throws MalformedURLException {
    Processor p;
    ImageDataSource ids = new ImageDataSource(width, height, frameRate, inFiles);
    try {
      p = Manager.createProcessor((DataSource)ids);
    } catch (Exception e) {
      System.err.println("Yikes!  Cannot create a processor from the data source.");
      return false;
    } 
    p.addControllerListener(this);
    p.configure();
    if (!waitForState(p, 180)) {
      System.err.println("Failed to configure the processor.");
      return false;
    } 
    p.setContentDescriptor(new ContentDescriptor("video.quicktime"));
    TrackControl[] tcs = p.getTrackControls();
    Format[] f = tcs[0].getSupportedFormats();
    if (f == null || f.length <= 0) {
      System.err.println("The mux does not support the input format: " + tcs[0].getFormat());
      return false;
    } 
    tcs[0].setFormat(f[0]);
    p.realize();
    if (!waitForState(p, 300)) {
      System.err.println("Failed to realize the processor.");
      return false;
    } 
    DataSink dsink;
    if ((dsink = createDataSink(p, outML)) == null) {
      System.err.println("Failed to create a DataSink for the given output MediaLocator: " + outML);
      return false;
    } 
    dsink.addDataSinkListener(this);
    this.fileDone = false;
    System.out.println("Generating the video : " + outML.getURL().toString());
    try {
      p.start();
      dsink.start();
    } catch (IOException e) {
      System.err.println("IO error during processing");
      return false;
    } 
    waitForFileDone();
    try {
      dsink.close();
    } catch (Exception exception) {}
    p.removeControllerListener(this);
    System.out.println("Video creation completed.");
    return true;
  }
  
  DataSink createDataSink(Processor p, MediaLocator outML) {
    DataSink dsink;
    DataSource ds;
    if ((ds = p.getDataOutput()) == null) {
      System.err.println("Something is really wrong: the processor does not have an output DataSource");
      return null;
    } 
    try {
      dsink = Manager.createDataSink(ds, outML);
      dsink.open();
    } catch (Exception e) {
      System.err.println("Cannot create the DataSink: " + e);
      return null;
    } 
    return dsink;
  }
  
  Object waitSync = new Object();
  
  boolean stateTransitionOK = true;
  
  boolean waitForState(Processor p, int state) {
    synchronized (this.waitSync) {
      try {
        while (p.getState() < state && this.stateTransitionOK)
          this.waitSync.wait(); 
      } catch (Exception exception) {}
    } 
    return this.stateTransitionOK;
  }
  
  public void controllerUpdate(ControllerEvent evt) {
    if (evt instanceof javax.media.ConfigureCompleteEvent || evt instanceof javax.media.RealizeCompleteEvent || 
      evt instanceof javax.media.PrefetchCompleteEvent) {
      synchronized (this.waitSync) {
        this.stateTransitionOK = true;
        this.waitSync.notifyAll();
      } 
    } else if (evt instanceof javax.media.ResourceUnavailableEvent) {
      synchronized (this.waitSync) {
        this.stateTransitionOK = false;
        this.waitSync.notifyAll();
      } 
    } else if (evt instanceof javax.media.EndOfMediaEvent) {
      evt.getSourceController().stop();
      evt.getSourceController().close();
    } 
  }
  
  Object waitFileSync = new Object();
  
  boolean fileDone = false;
  
  boolean fileSuccess = true;
  
  boolean waitForFileDone() {
    synchronized (this.waitFileSync) {
      try {
        while (!this.fileDone)
          this.waitFileSync.wait(); 
      } catch (Exception exception) {}
    } 
    return this.fileSuccess;
  }
  
  public void dataSinkUpdate(DataSinkEvent evt) {
    if (evt instanceof javax.media.datasink.EndOfStreamEvent) {
      synchronized (this.waitFileSync) {
        this.fileDone = true;
        this.waitFileSync.notifyAll();
      } 
    } else if (evt instanceof javax.media.datasink.DataSinkErrorEvent) {
      synchronized (this.waitFileSync) {
        this.fileDone = true;
        this.fileSuccess = false;
        this.waitFileSync.notifyAll();
      } 
    } 
  }
  
  static void prUsage() {
    System.err.println(
        "Usage: java JpegImagesToMovie -w <width> -h <height> -f <frame rate> -o <output URL> <input JPEG file 1> <input JPEG file 2> ...");
    System.exit(-1);
  }
  
  static MediaLocator createMediaLocator(String url) {
    MediaLocator ml;
    if (url.indexOf(":") > 0 && (ml = new MediaLocator(url)) != null)
      return ml; 
    if (url.startsWith(File.separator)) {
      if ((ml = new MediaLocator("file:" + url)) != null)
        return ml; 
    } else {
      String file = "file:" + System.getProperty("user.dir") + File.separator + url;
      if ((ml = new MediaLocator(file)) != null)
        return ml; 
    } 
    return null;
  }
  
  class ImageDataSource extends PullBufferDataSource {
    JpegImagesToMovie.ImageSourceStream[] streams;
    
    ImageDataSource(int width, int height, int frameRate, Vector<String> images) {
      this.streams = new JpegImagesToMovie.ImageSourceStream[1];
      this.streams[0] = new JpegImagesToMovie.ImageSourceStream(width, height, frameRate, images);
    }
    
    public void setLocator(MediaLocator source) {}
    
    public MediaLocator getLocator() {
      return null;
    }
    
    public String getContentType() {
      return "raw";
    }
    
    public void connect() {}
    
    public void disconnect() {}
    
    public void start() {}
    
    public void stop() {}
    
    public PullBufferStream[] getStreams() {
      return (PullBufferStream[])this.streams;
    }
    
    public Time getDuration() {
      return DURATION_UNKNOWN;
    }
    
    public Object[] getControls() {
      return new Object[0];
    }
    
    public Object getControl(String type) {
      return null;
    }
  }
  
  class ImageSourceStream implements PullBufferStream {
    Vector<String> images;
    
    int width;
    
    int height;
    
    VideoFormat format;
    
    int nextImage = 0;
    
    boolean ended = false;
    
    public ImageSourceStream(int width, int height, int frameRate, Vector<String> images) {
      this.width = width;
      this.height = height;
      this.images = images;
      this.format = new VideoFormat("jpeg", new Dimension(width, height), -1, 
          Format.byteArray, frameRate);
    }
    
    public boolean willReadBlock() {
      return false;
    }
    
    public void read(Buffer buf) throws IOException {
      if (this.nextImage >= this.images.size()) {
        buf.setEOM(true);
        buf.setOffset(0);
        buf.setLength(0);
        this.ended = true;
        return;
      } 
      String imageFile = this.images.elementAt(this.nextImage);
      this.nextImage++;
      RandomAccessFile raFile = new RandomAccessFile(imageFile, "r");
      byte[] data = null;
      if (buf.getData() instanceof byte[])
        data = (byte[])buf.getData(); 
      if (data == null || data.length < raFile.length()) {
        data = new byte[(int)raFile.length()];
        buf.setData(data);
      } 
      raFile.readFully(data, 0, (int)raFile.length());
      buf.setOffset(0);
      buf.setLength((int)raFile.length());
      buf.setFormat((Format)this.format);
      buf.setFlags(buf.getFlags() | 0x10);
      raFile.close();
    }
    
    public Format getFormat() {
      return (Format)this.format;
    }
    
    public ContentDescriptor getContentDescriptor() {
      return new ContentDescriptor("raw");
    }
    
    public long getContentLength() {
      return 0L;
    }
    
    public boolean endOfStream() {
      return this.ended;
    }
    
    public Object[] getControls() {
      return new Object[0];
    }
    
    public Object getControl(String type) {
      return null;
    }
  }
}
