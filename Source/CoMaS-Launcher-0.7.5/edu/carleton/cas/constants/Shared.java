/*     */ package edu.carleton.cas.constants;
/*     */ 
/*     */ import edu.carleton.cas.file.Utils;
/*     */ import edu.carleton.cas.logging.Logger;
/*     */ import edu.carleton.cas.utility.WindowsRegistry;
/*     */ import java.io.File;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.Level;
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
/*     */ public class Shared
/*     */ {
/*     */   public static final String LOCATION_STATE = "LOCATION";
/*     */   public static final String START_STATE = "START";
/*     */   public static final String END_STATE = "END";
/*     */   public static final String DURATION_STATE = "DURATION";
/*     */   public static final String STOP_STATE = "stop";
/*     */   public static final String ID_STATE = "ID";
/*     */   public static final String PASSWORD_STATE = "PASSWORD";
/*     */   public static final String EMAIL_STATE = "EMAIL";
/*     */   public static final String COURSE_STATE = "COURSE";
/*     */   public static final String ACTIVITY_STATE = "ACTIVITY";
/*     */   public static final String SESSION_STATE = "SESSION";
/*     */   public static final String ACCESS_TIME = "ACCESS";
/*     */   public static final String CURRENT_TIME = "CURRENT_TIME";
/*     */   public static final String TRUE = "true";
/*     */   public static final String FALSE = "false";
/*     */   public static final String YES = "yes";
/*     */   public static final String NO = "no";
/*     */   public static final String OS_NAME = "os.name";
/*     */   public static final String USER_NAME = "user.name";
/*     */   public static final String HOME_DIR = "user.home";
/*     */   public static String STUDENT_FIRST_NAME;
/*     */   public static String STUDENT_LAST_NAME;
/*     */   public static String STUDENT_ID;
/*     */   public static String STUDENT_COURSE;
/*     */   public static String STUDENT_ACTIVITY;
/*     */   public static final String DOT_ZIP = ".zip";
/*     */   public static final String EXAM = "exam";
/*     */   public static final String EXAM_DOT_ZIP = "exam.zip";
/*     */   public static final String TOOLS = "tools";
/*     */   public static final String TOOLS_DOT_ZIP = "tools.zip";
/*     */   public static final String RESOURCES = "resources";
/*     */   public static final String RESOURCES_DOT_ZIP = "resources.zip";
/*     */   public static final String DOT_HOST = ".host";
/*     */   public static final String DOT_DIRECTORY = ".directory";
/*     */   public static final String DOT_EXAM = ".exam";
/*     */   public static final String DOT_UPLOAD = ".upload";
/*     */   public static final String DOT_LOG = ".log";
/*     */   public static final String DOT_VIDEO = ".video";
/*     */   public static final String DOT_CMS = ".cms";
/*     */   public static final String DOT_WEBSOCKET = ".websocket";
/*     */   public static final String DOT_PROTOCOL = ".protocol";
/*     */   public static final String COURSES = "courses";
/*     */   public static boolean LOOK_FOR_SERVICES = false;
/*     */   public static final String BONJOUR_EXAM = "EXAM";
/*     */   public static final String BONJOUR_FILE_UPLOAD = "EFUP";
/*     */   public static final String BONJOUR_DIRECTORY = "EDIR";
/*     */   public static final String BONJOUR_LOG = "ELOG";
/*     */   public static final String BONJOUR_VIDEO = "EVID";
/*     */   public static final String BONJOUR_CMS = "ECMS";
/*     */   public static final String FIND_SERVICES = "find_services";
/* 123 */   public static String SERVER_CHOSEN = null;
/* 124 */   public static String DIRECTORY_HOST = "comas.cogerent.com";
/* 125 */   public static String EXAM_HOST = "comas.cogerent.com";
/* 126 */   public static String UPLOAD_HOST = "comas.cogerent.com";
/* 127 */   public static String LOG_HOST = "comas.cogerent.com";
/* 128 */   public static String VIDEO_HOST = "comas.cogerent.com";
/* 129 */   public static String CMS_HOST = "comas.cogerent.com";
/* 130 */   public static String WEBSOCKET_HOST = "comas.cogerent.com";
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
/* 142 */   public static String COMAS_DOT_INI = "comas.ini";
/* 143 */   public static String COMAS_DOT_JAR_FORMAT = "CoMaS-%s.jar";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String examDotIni(String host) {
/* 154 */     if (host.startsWith("https://"))
/* 155 */       return String.format("%s:%s/CMS/rest/exam/exam.ini", new Object[] { host, "8443" }); 
/* 156 */     if (host.startsWith("http://")) {
/* 157 */       return String.format("%s:%s/CMS/rest/exam/exam.ini", new Object[] { host, "8080" });
/*     */     }
/* 159 */     return String.format("%s://%s:%s/CMS/rest/exam/exam.ini", new Object[] { PROTOCOL, host, PORT });
/*     */   }
/*     */   
/* 162 */   public static String EXAM_CONFIGURATION_FILE = "https://comas.cogerent.com:8443/CMS/rest/exam/exam.ini";
/* 163 */   public static String LOCAL_EXAM_CONFIGURATION_FILE = "http://192.168.86.26:8080/CMS/rest/exam/exam.ini";
/*     */ 
/*     */   
/*     */   public static final String HTTPS = "https";
/*     */   
/*     */   public static final String HTTP = "http";
/*     */   
/*     */   public static final String WS = "ws";
/*     */   
/*     */   public static final String WSS = "wss";
/*     */   
/* 174 */   public static String PROTOCOL = "https";
/* 175 */   public static String WS_PROTOCOL = "wss";
/*     */   
/*     */   public static final String HTTP_PORT = "8080";
/*     */   public static final String HTTPS_PORT = "8443";
/* 179 */   public static String PORT = "8443";
/*     */ 
/*     */   
/*     */   public static final String OK = "{\"OK\"}";
/*     */ 
/*     */   
/*     */   public static final String STOPPED = "{\"STOPPED\"}";
/*     */ 
/*     */   
/*     */   public static final String MONITOR = "{\"MONITOR\"}";
/*     */ 
/*     */   
/*     */   public static final String DOES_NOT_EXIST = "{\"DOES NOT EXIST\"}";
/*     */   
/*     */   public static final String ILLEGAL_VERSION = "{\"ILLEGAL VERSION\"}";
/*     */   
/* 195 */   public static String LOG_PATH = "log";
/* 196 */   public static String VIDEO_PATH = "video";
/* 197 */   public static String UPLOAD_PATH = "upload";
/* 198 */   public static String REGISTER_PATH = "register";
/*     */   
/*     */   public static final String BASE_REGISTRATION_SERVICE = "/COMP4601-Directory/rest/directory/";
/*     */   
/*     */   public static final String BASE_UPLOAD_SERVICE = "/COMP4601-FileUpload/rest/file/";
/*     */   public static final String BASE_LOG_SERVICE = "/COMP4601-Log/rest/logger/";
/*     */   public static final String BASE_VIDEO_SERVICE = "/COMP4601-Video/rest/logger/";
/*     */   public static final String BASE_CMS_SERVICE = "/CMS/rest/";
/*     */   public static final String BASE_EXAM_SERVICE = "/Exam/rest/exam/";
/*     */   public static final String BASE_WEBSOCKET_SERVICE = "/WebSocket/";
/* 208 */   public static final String REGISTRATION_SERVICE = "/COMP4601-Directory/rest/directory/" + REGISTER_PATH;
/* 209 */   public static final String UPLOAD_SERVICE = "/COMP4601-FileUpload/rest/file/" + UPLOAD_PATH;
/* 210 */   public static final String LOG_SERVICE = "/COMP4601-Log/rest/logger/" + LOG_PATH;
/* 211 */   public static final String VIDEO_SERVICE = "/COMP4601-Video/rest/logger/" + VIDEO_PATH;
/*     */ 
/*     */   
/*     */   public static final String CMS_SERVICE = "/CMS/rest/";
/*     */   
/*     */   public static final String EXAM_SERVICE = "/Exam/rest/exam/";
/*     */   
/*     */   public static final String WEBSOCKET_SERVICE = "/WebSocket/";
/*     */   
/*     */   public static final String COMMAND_AND_CONTROL_SERVICE = "/WebSocket/channel/";
/*     */   
/*     */   public static final String LOGIN_DOT_INI = "login.ini";
/*     */   
/*     */   public static final String LOGIN_CONFIGURATION_FILE = "/CMS/rest/exam/login.ini";
/*     */   
/*     */   public static String LOGIN_CONFIGURATION_URL;
/*     */   
/* 228 */   public static String BASE_LOGIN = String.valueOf(PROTOCOL) + "://" + DIRECTORY_HOST + ":" + PORT + "/COMP4601-Directory/rest/directory/";
/* 229 */   public static String BASE_LOG = String.valueOf(PROTOCOL) + "://" + LOG_HOST + ":" + PORT + "/COMP4601-Log/rest/logger/";
/* 230 */   public static String BASE_UPLOAD = String.valueOf(PROTOCOL) + "://" + UPLOAD_HOST + ":" + PORT + "/COMP4601-FileUpload/rest/file/";
/* 231 */   public static String BASE_VIDEO = String.valueOf(PROTOCOL) + "://" + VIDEO_HOST + ":" + PORT + "/COMP4601-Video/rest/logger/";
/* 232 */   public static String BASE_CMS = String.valueOf(PROTOCOL) + "://" + CMS_HOST + ":" + PORT + "/CMS/rest/";
/* 233 */   public static String BASE_EXAM = String.valueOf(PROTOCOL) + "://" + CMS_HOST + ":" + PORT + "/Exam/rest/exam/";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String HANDLE_FILE = "handle.exe";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String SLASH_EXAM = "/exam/";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 248 */   public static String LOGIN_URL = String.valueOf(PROTOCOL) + "://" + DIRECTORY_HOST + ":" + PORT + REGISTRATION_SERVICE;
/* 249 */   public static String EXAM_ZIP = String.valueOf(BASE_CMS) + "/exam/" + "exam.zip";
/* 250 */   public static String TOOLS_ZIP = String.valueOf(BASE_CMS) + "/exam/" + "tools.zip";
/* 251 */   public static String RESOURCES_ZIP = String.valueOf(BASE_CMS) + "/exam/" + "resources.zip";
/* 252 */   public static String EXAM_URL = String.valueOf(PROTOCOL) + "://" + UPLOAD_HOST + ":" + PORT + UPLOAD_SERVICE;
/* 253 */   public static String LOG_URL = String.valueOf(PROTOCOL) + "://" + LOG_HOST + ":" + PORT + LOG_SERVICE;
/* 254 */   public static String VIDEO_URL = String.valueOf(PROTOCOL) + "://" + VIDEO_HOST + ":" + PORT + VIDEO_SERVICE;
/* 255 */   public static String CMS_URL = String.valueOf(PROTOCOL) + "://" + CMS_HOST + ":" + PORT + "/CMS/rest/";
/* 256 */   public static String HANDLE_EXE = String.valueOf(PROTOCOL) + "://" + EXAM_HOST + "/exam/" + "handle.exe";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DESKTOP = "Desktop";
/*     */ 
/*     */   
/* 263 */   public static String DESKTOP_DIR = "CoMaS";
/*     */   
/*     */   public static final String EXAM_DIR = "exam";
/*     */   
/*     */   public static final String LOGS_DIR = "logs";
/*     */   
/*     */   public static final String SCREENS_DIR = "screens";
/*     */   
/*     */   public static final String RESOURCES_DIR = "resources";
/*     */   public static final String TOOLS_DIR = "tools";
/*     */   public static final String ARCHIVES_DIR = "archives";
/*     */   public static final String DOWNLOADS = "Downloads";
/* 275 */   public static final String HOME = System.getProperty("user.home");
/* 276 */   public static String DIR = String.valueOf(getDesktopDirectory()) + File.separator + DESKTOP_DIR;
/* 277 */   public static final String ZIP = HOME;
/* 278 */   public static String DOWNLOADS_DIR = String.valueOf(getDownloadsDirectory()) + File.separator;
/*     */   
/*     */   public static final String REGISTRY_LOCATION = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders";
/*     */   
/*     */   public static final String DESKTOP_REGISTRY_KEY = "Desktop";
/*     */   
/*     */   public static final String PERSONAL_REGISTRY_KEY = "Personal";
/*     */   
/*     */   public static final String WINDOWS_USERPROFILE = "USERPROFILE";
/*     */   public static final boolean USING_WINDOWS_REGISTRY = true;
/*     */   public static final String STUDENT_NOTES_FILE_NAME = "STUDENT_NOTES_FILE_NAME";
/*     */   public static final String STUDENT_NOTES_FILE_CHECKSUM = "STUDENT_NOTES_FILE_CHECKSUM";
/*     */   
/*     */   public static boolean isWindows() {
/* 292 */     String os = System.getProperty("os.name").toLowerCase();
/* 293 */     return os.startsWith("win");
/*     */   }
/*     */   
/*     */   public static final String LOG_SEVERE = "SEVERE";
/*     */   public static final String LOG_MINOR = "MINOR";
/*     */   public static final String LOG_WARNING = "WARNING";
/*     */   public static final String LOG_TEST = "TEST";
/*     */   public static final String LOG_INFO = "INFO";
/*     */   public static final String LOG_ALERT = "ALERT";
/*     */   public static final String LOG_CLOSE = "CLOSE";
/*     */   
/*     */   public static String getDesktopDirectory() {
/* 305 */     String desktopDirectory = null;
/*     */     
/* 307 */     String os = System.getProperty("os.name").toLowerCase();
/* 308 */     if (os.startsWith("win")) {
/* 309 */       desktopDirectory = WindowsRegistry.readRegistry("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Desktop");
/* 310 */       if (desktopDirectory == null) {
/* 311 */         desktopDirectory = System.getenv("USERPROFILE");
/* 312 */         if (desktopDirectory != null) {
/* 313 */           desktopDirectory = String.valueOf(desktopDirectory) + File.separator + "Desktop";
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 318 */     if (isWriteableDirectory(desktopDirectory))
/* 319 */       return desktopDirectory; 
/* 320 */     return String.valueOf(HOME) + File.separator + "Desktop";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getDownloadsDirectory() {
/* 327 */     String dir = String.valueOf(HOME) + File.separator + "Downloads";
/* 328 */     if (isWriteableDirectory(dir)) {
/* 329 */       return dir;
/*     */     }
/* 331 */     String downloadsDirectory = null;
/* 332 */     String os = System.getProperty("os.name").toLowerCase();
/* 333 */     if (os.startsWith("win")) {
/* 334 */       downloadsDirectory = WindowsRegistry.readRegistry("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Personal");
/* 335 */       if (isWriteableDirectory(downloadsDirectory))
/* 336 */         return downloadsDirectory; 
/* 337 */       downloadsDirectory = System.getenv("USERPROFILE");
/* 338 */       if (downloadsDirectory != null)
/* 339 */         downloadsDirectory = String.valueOf(downloadsDirectory) + File.separator + "Downloads"; 
/* 340 */       if (isWriteableDirectory(downloadsDirectory)) {
/* 341 */         return downloadsDirectory;
/*     */       }
/*     */     } 
/* 344 */     return DIR;
/*     */   }
/*     */   
/*     */   public static boolean isWriteableDirectory(String dir) {
/* 348 */     if (dir != null) {
/* 349 */       File directoryFile = new File(dir);
/* 350 */       if (directoryFile.exists() && 
/* 351 */         directoryFile.isDirectory() && 
/* 352 */         directoryFile.canWrite())
/* 353 */         return true; 
/*     */     } 
/* 355 */     return false;
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
/* 375 */   public static int LOG_GENERATION_FREQUENCY = 10;
/*     */   
/*     */   public static final String LOG_FILE_BASE = "comas-launcher";
/*     */   
/*     */   public static final String LOG_FILE_ENDING = "-log.html";
/*     */   
/*     */   public static final String LOG_FILE_NAME = "comas-launcher-log.html";
/*     */   
/*     */   public static final String SESSION_FILE_NAME = ".es";
/*     */   
/*     */   public static final String KEY_DIRECTORY_HOST = "directory_host";
/*     */   
/*     */   public static final String KEY_LOG_HOST = "log_host";
/*     */   
/*     */   public static final String KEY_UPLOAD_HOST = "upload_host";
/*     */   
/*     */   public static final String KEY_VIDEO_HOST = "video_host";
/*     */   
/*     */   public static final String KEY_CMS_HOST = "cms_host";
/*     */   
/*     */   public static final String KEY_PROTOCOL = "protocol";
/*     */   
/*     */   public static final String KEY_WS_PROTOCOL = "ws_protocol";
/*     */   
/*     */   public static final String KEY_PORT = "port";
/*     */   
/*     */   public static final String KEY_COURSE = "course";
/*     */   
/*     */   public static final String KEY_ACTIVITY = "activity";
/*     */   
/*     */   public static final String KEY_VERSION = "version";
/*     */   
/*     */   public static final String KEY_APPLICATION_CLASS = "application.class";
/*     */   public static final String KEY_APPLICATION_METHOD = "application.method";
/*     */   public static boolean AUTO_ARCHIVE = true;
/* 410 */   public static int AUTO_ARCHIVE_FREQUENCY = 20;
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
/* 422 */   public static int ABSOLUTE_MAX_INTERVAL = 60;
/* 423 */   public static int MAX_INTERVAL = 50;
/* 424 */   public static int MIN_INTERVAL = 10;
/* 425 */   public static int MAX_FAILURES = 100;
/* 426 */   public static int MAX_SESSION_FAILURES = 10000;
/* 427 */   public static int ALERT_FAILURE_FREQUENCY = 20;
/* 428 */   public static int MAX_MSECS_TO_WAIT_TO_END = 60000;
/* 429 */   public static int UPLOAD_THRESHOLD_IN_MSECS = 60000;
/* 430 */   public static int MIN_MSECS_BETWEEN_USER_UPLOADS = 300000;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 437 */   public static int RETRY_TIME = 10000;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 442 */   public static int FREQUENCY_TO_CHECK_EXAM_DIRECTORY = 15;
/* 443 */   public static int MIN_EVENTS_IN_EXAM_DIRECTORY = 0;
/* 444 */   public static int MAX_NUMBER_OF_FILE_WATCHING_FAILURES = 10;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String INSANE = "Insane";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String LOGIN = "Login";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String LOGGED_OUT = "Logged out";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String UNKNOWN = "Unknown";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String PROBLEM_WEBCAM = "Issue:Webcam";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String PROBLEM_SCREENSHOTS = "Issue:Screen";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String PROBLEM_ARCHIVES = "Issue:Archive";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String PROBLEM_LOGS = "Issue:Log";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String PROBLEM_FILES = "Issue:Files";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String NO_DOWNLOAD_REQUIRED = "NO_DOWNLOAD_REQUIRED";
/*     */ 
/*     */ 
/*     */   
/* 488 */   public static Level LOGGING_LEVEL = Level.INFO;
/*     */ 
/*     */   
/*     */   public static final String VIDEO = "video";
/*     */ 
/*     */   
/*     */   public static final String UPLOAD = "upload";
/*     */ 
/*     */   
/*     */   public static final String TEST = "test";
/*     */ 
/*     */   
/* 500 */   public static float IMAGE_COMPRESSION = 0.2F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String IMAGE_FORMAT = "jpg";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 512 */   public static String VERSION = "0.7.5";
/* 513 */   public static String DEFAULT_COMAS_DOT_JAR = String.format(COMAS_DOT_JAR_FORMAT, new Object[] { VERSION });
/* 514 */   public static String PASSKEY_DIRECTORY = "SimpleDirectory";
/* 515 */   public static String PASSKEY_LOG = "LoggerService";
/* 516 */   public static String PASSKEY_FILE_UPLOAD = "UploadFileService";
/* 517 */   public static String PASSKEY_VIDEO = "VideoService";
/* 518 */   public static String PASSKEY_EXAM = "ExamService";
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
/*     */   public static String service(String protocol, String host, String port, String service) {
/* 530 */     return String.valueOf(protocol) + "://" + host + ":" + port + service;
/*     */   }
/*     */   
/*     */   public static String getCourseDirectory(String course) {
/* 534 */     return String.valueOf(DIR) + File.separator + course;
/*     */   }
/*     */   
/*     */   public static String getActivityDirectory(String course, String activity) {
/* 538 */     return String.valueOf(getCourseDirectory(course)) + File.separator + activity;
/*     */   }
/*     */   
/*     */   public static String getBaseDirectory(String course, String activity) {
/* 542 */     return String.valueOf(DIR) + File.separator + course + File.separator + activity + File.separator;
/*     */   }
/*     */   
/*     */   public static String getExamDirectory(String course, String activity) {
/* 546 */     return String.valueOf(getBaseDirectory(course, activity)) + "exam" + File.separator;
/*     */   }
/*     */   
/*     */   public static String getLogsDirectory(String course, String activity) {
/* 550 */     return String.valueOf(getBaseDirectory(course, activity)) + "logs" + File.separator;
/*     */   }
/*     */   
/*     */   public static String getResourcesDirectory(String course, String activity) {
/* 554 */     return String.valueOf(getBaseDirectory(course, activity)) + "resources" + File.separator;
/*     */   }
/*     */   
/*     */   public static String getScreensDirectory(String course, String activity) {
/* 558 */     return String.valueOf(getBaseDirectory(course, activity)) + "screens" + File.separator;
/*     */   }
/*     */   
/*     */   public static String getArchivesDirectory(String course, String activity) {
/* 562 */     return String.valueOf(getBaseDirectory(course, activity)) + "archives" + File.separator;
/*     */   }
/*     */   
/*     */   public static String getToolsDirectory(String course, String activity) {
/* 566 */     return String.valueOf(getBaseDirectory(course, activity)) + "tools" + File.separator;
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
/* 581 */   public static Properties CONFIGS = new Properties();
/*     */   
/*     */   public static final String DEFAULT_HOST = "hostname";
/*     */   
/*     */   public static final String DEFAULT_PROTOCOL = "protocol";
/*     */   
/*     */   public static void setupURLs(String course) {
/*     */     Properties configs;
/* 589 */     if (!CONFIGS.isEmpty()) {
/* 590 */       configs = CONFIGS;
/*     */     } else {
/* 592 */       Logger.output("Accessing " + EXAM_CONFIGURATION_FILE);
/* 593 */       configs = Utils.getProperties(EXAM_CONFIGURATION_FILE);
/* 594 */       if (configs.isEmpty()) {
/* 595 */         configs = Utils.getProperties(LOCAL_EXAM_CONFIGURATION_FILE);
/*     */       }
/*     */     } 
/* 598 */     String defaultGlobalHost = configs.getProperty("hostname", DIRECTORY_HOST);
/* 599 */     String defaultGlobalProtocol = configs.getProperty("protocol", PROTOCOL);
/*     */     
/* 601 */     if (course != null) {
/* 602 */       String defaultHost = configs.getProperty(String.valueOf(course) + ".host", defaultGlobalHost);
/* 603 */       DIRECTORY_HOST = configs.getProperty(String.valueOf(course) + ".directory", defaultHost);
/*     */       
/* 605 */       EXAM_HOST = configs.getProperty(String.valueOf(course) + ".exam", defaultHost);
/*     */       
/* 607 */       UPLOAD_HOST = configs.getProperty(String.valueOf(course) + ".upload", defaultHost);
/*     */       
/* 609 */       LOG_HOST = configs.getProperty(String.valueOf(course) + ".log", defaultHost);
/*     */       
/* 611 */       VIDEO_HOST = configs.getProperty(String.valueOf(course) + ".video", defaultHost);
/*     */       
/* 613 */       CMS_HOST = configs.getProperty(String.valueOf(course) + ".cms", defaultHost);
/*     */       
/* 615 */       WEBSOCKET_HOST = configs.getProperty(String.valueOf(course) + ".websocket", defaultHost);
/* 616 */       PROTOCOL = configs.getProperty(String.valueOf(course) + ".protocol", defaultGlobalProtocol);
/* 617 */       if (PROTOCOL.equals("https")) {
/* 618 */         WS_PROTOCOL = "wss";
/* 619 */         PORT = "8443";
/*     */       } else {
/* 621 */         WS_PROTOCOL = "ws";
/* 622 */         PORT = "8080";
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 628 */     updateURLs();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void updateURLs() {
/* 639 */     LOGIN_URL = service(PROTOCOL, DIRECTORY_HOST, PORT, REGISTRATION_SERVICE);
/*     */     
/* 641 */     EXAM_URL = service(PROTOCOL, UPLOAD_HOST, PORT, UPLOAD_SERVICE);
/* 642 */     LOG_URL = service(PROTOCOL, LOG_HOST, PORT, LOG_SERVICE);
/* 643 */     VIDEO_URL = service(PROTOCOL, VIDEO_HOST, PORT, VIDEO_SERVICE);
/* 644 */     CMS_URL = service(PROTOCOL, CMS_HOST, PORT, "/CMS/rest/");
/*     */     
/* 646 */     BASE_LOGIN = service(PROTOCOL, DIRECTORY_HOST, PORT, 
/* 647 */         "/COMP4601-Directory/rest/directory/");
/* 648 */     BASE_LOG = service(PROTOCOL, LOG_HOST, PORT, "/COMP4601-Log/rest/logger/");
/* 649 */     BASE_UPLOAD = service(PROTOCOL, UPLOAD_HOST, PORT, "/COMP4601-FileUpload/rest/file/");
/* 650 */     BASE_VIDEO = service(PROTOCOL, VIDEO_HOST, PORT, "/COMP4601-Video/rest/logger/");
/* 651 */     BASE_CMS = service(PROTOCOL, CMS_HOST, PORT, "/CMS/rest/");
/* 652 */     BASE_EXAM = service(PROTOCOL, CMS_HOST, PORT, "/Exam/rest/exam/");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 661 */     EXAM_ZIP = String.valueOf(BASE_CMS) + "exam" + "/" + "exam.zip";
/* 662 */     TOOLS_ZIP = String.valueOf(BASE_CMS) + "exam" + "/" + "tools.zip";
/* 663 */     RESOURCES_ZIP = String.valueOf(BASE_CMS) + "exam" + "/" + "resources.zip";
/* 664 */     LOGIN_CONFIGURATION_URL = String.valueOf(BASE_CMS) + "exam" + "/" + "login.ini";
/* 665 */     HANDLE_EXE = String.valueOf(BASE_CMS) + "exam" + "/" + "handle.exe";
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean USE_ACTIVITY_CODES = false;
/*     */   
/*     */   public static boolean USE_WEB_CAM = true;
/*     */   
/*     */   public static boolean WEB_CAM_MANDATORY = false;
/*     */   
/*     */   public static boolean USE_WEB_CAM_ON_SCREEN_SHOT = false;
/*     */   
/*     */   public static boolean USE_SCREEN_SHOTS = true;
/*     */   
/*     */   public static boolean NETWORK_MONITORING = true;
/*     */   
/*     */   public static boolean FILE_MONITORING = true;
/*     */   
/*     */   public static boolean PROCESS_MONITORING = false;
/*     */   
/*     */   public static boolean BLUETOOTH_MONITORING = true;
/*     */   
/*     */   public static boolean AUDIO_MONITORING = false;
/*     */   
/*     */   public static boolean VIDEO_MONITORING = false;
/*     */   
/*     */   public static boolean SCREEN_SHOT_QR_CODE_REQUIRED = true;
/*     */   public static boolean SCREEN_SHOT_TIMESTAMP_REQUIRED = true;
/* 693 */   public static int MAX_SUPPORTED_JAVA_VERSION = 20;
/* 694 */   public static int MIN_SUPPORTED_JAVA_VERSION = 8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 700 */   public static int MIN_DRIVE_SPACE_THRESHOLD_PERCENTAGE = 0;
/* 701 */   public static int MIN_DRIVE_SPACE_THRESHOLD_MB = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean VERIFY_CODE_SIGNATURE = false;
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean CODE_MUST_BE_SIGNED = false;
/*     */ 
/*     */ 
/*     */   
/*     */   public static String PUBLIC_KEY;
/*     */ 
/*     */ 
/*     */   
/*     */   public static String HASH_OF_APPLICATION_JAR;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Properties getLoginProperties(String course) {
/* 724 */     Properties configs = Utils.getProperties(LOGIN_CONFIGURATION_URL);
/*     */     
/* 726 */     if (configs == null) {
/* 727 */       configs = new Properties();
/*     */     } else {
/*     */       
/* 730 */       HASH_OF_APPLICATION_JAR = Utils.getStringOrDefault(configs, "application.hash", "");
/* 731 */       COMAS_DOT_JAR_FORMAT = Utils.getStringOrDefault(configs, "application.name", "CoMaS-%s.jar");
/* 732 */       VERIFY_CODE_SIGNATURE = Utils.getBooleanOrDefault(configs, "application.verify", false);
/* 733 */       if (VERIFY_CODE_SIGNATURE) {
/* 734 */         PUBLIC_KEY = Utils.getStringOrDefault(configs, "application.public_key", "");
/*     */       } else {
/* 736 */         PUBLIC_KEY = null;
/* 737 */       }  CODE_MUST_BE_SIGNED = Utils.getBooleanOrDefault(configs, "application.signed", false);
/*     */       
/* 739 */       MIN_DRIVE_SPACE_THRESHOLD_PERCENTAGE = Utils.getIntegerOrDefaultInRange(configs, 
/* 740 */           "min_free_drive_space_percentage", MIN_DRIVE_SPACE_THRESHOLD_PERCENTAGE, 0, 5);
/* 741 */       MIN_DRIVE_SPACE_THRESHOLD_MB = Utils.getIntegerOrDefaultInRange(configs, "min_free_drive_space_mb", 
/* 742 */           MIN_DRIVE_SPACE_THRESHOLD_MB, 0, 5000);
/*     */       
/* 744 */       MAX_SUPPORTED_JAVA_VERSION = Utils.getIntegerOrDefaultInRange(configs, "max_supported_java_version", 
/* 745 */           MAX_SUPPORTED_JAVA_VERSION, 8, 100);
/* 746 */       MIN_SUPPORTED_JAVA_VERSION = Utils.getIntegerOrDefaultInRange(configs, "min_supported_java_version", 
/* 747 */           MIN_SUPPORTED_JAVA_VERSION, 8, MAX_SUPPORTED_JAVA_VERSION);
/*     */ 
/*     */ 
/*     */       
/* 751 */       AUTO_ARCHIVE = Utils.getBooleanOrDefault(configs, "auto_archive", AUTO_ARCHIVE);
/* 752 */       AUTO_ARCHIVE_FREQUENCY = Utils.getIntegerOrDefaultInRange(configs, "auto_archive_frequency", 
/* 753 */           AUTO_ARCHIVE_FREQUENCY, 0, 100);
/*     */       
/* 755 */       LOG_GENERATION_FREQUENCY = Utils.getIntegerOrDefaultInRange(configs, "log_generation_frequency", 
/* 756 */           LOG_GENERATION_FREQUENCY, 1, 2147483647);
/*     */       
/* 758 */       String logging_level = Utils.getStringOrDefault(configs, "logs.level", "INFO");
/*     */       try {
/* 760 */         LOGGING_LEVEL = Level.parse(logging_level);
/* 761 */       } catch (IllegalArgumentException e) {
/* 762 */         LOGGING_LEVEL = Level.INFO;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 769 */       VERSION = Utils.getStringOrDefault(configs, "version", VERSION);
/*     */       
/* 771 */       MAX_INTERVAL = Utils.getIntegerOrDefaultInRange(configs, "max_interval", MAX_INTERVAL, 1, ABSOLUTE_MAX_INTERVAL);
/* 772 */       MIN_INTERVAL = Utils.getIntegerOrDefaultInRange(configs, "min_interval", MIN_INTERVAL, 1, MAX_INTERVAL);
/* 773 */       MAX_FAILURES = Utils.getIntegerOrDefaultInRange(configs, "max_failures", MAX_FAILURES, 1, 
/* 774 */           2147483647);
/* 775 */       MAX_MSECS_TO_WAIT_TO_END = Utils.getIntegerOrDefaultInRange(configs, "max_msecs_to_wait_to_end", 
/* 776 */           MAX_MSECS_TO_WAIT_TO_END, 1, MAX_MSECS_TO_WAIT_TO_END * 10);
/* 777 */       UPLOAD_THRESHOLD_IN_MSECS = Utils.getIntegerOrDefaultInRange(configs, "upload_threshold_in_msecs", 
/* 778 */           UPLOAD_THRESHOLD_IN_MSECS, 1, 2147483647);
/* 779 */       MIN_MSECS_BETWEEN_USER_UPLOADS = Utils.getIntegerOrDefaultInRange(configs, "min_msecs_between_user_uploads", 
/* 780 */           MIN_MSECS_BETWEEN_USER_UPLOADS, 60000, 21600000);
/*     */ 
/*     */       
/* 783 */       RETRY_TIME = Utils.getIntegerOrDefaultInRange(configs, "retry_time", RETRY_TIME, 1000, 60000);
/*     */       
/* 785 */       FREQUENCY_TO_CHECK_EXAM_DIRECTORY = Utils.getIntegerOrDefaultInRange(configs, 
/* 786 */           "frequency_to_check_exam_directory", FREQUENCY_TO_CHECK_EXAM_DIRECTORY, 1, 1000) * 60 * 1000;
/* 787 */       MIN_EVENTS_IN_EXAM_DIRECTORY = Utils.getIntegerOrDefaultInRange(configs, "min_events_in_exam_directory", 
/* 788 */           MIN_EVENTS_IN_EXAM_DIRECTORY, 0, 2147483647);
/*     */       
/* 790 */       IMAGE_COMPRESSION = Utils.getFloatOrDefaultInRange(configs, "image_compression", IMAGE_COMPRESSION, 0.0F, 
/* 791 */           1.0F);
/*     */       
/* 793 */       USE_ACTIVITY_CODES = Utils.getBooleanOrDefault(configs, "use_activity_codes", USE_ACTIVITY_CODES);
/* 794 */       WEB_CAM_MANDATORY = Utils.getBooleanOrDefault(configs, "webcam.required", false);
/* 795 */       USE_WEB_CAM = Utils.getBooleanOrDefault(configs, "webcam.enabled", true);
/* 796 */       USE_WEB_CAM_ON_SCREEN_SHOT = Utils.getBooleanOrDefault(configs, "webcam.on_screen_shot", false);
/*     */       
/* 798 */       if (USE_WEB_CAM_ON_SCREEN_SHOT) {
/* 799 */         USE_WEB_CAM = false;
/*     */       }
/* 801 */       if (!USE_WEB_CAM_ON_SCREEN_SHOT && !USE_WEB_CAM) {
/* 802 */         WEB_CAM_MANDATORY = false;
/*     */       }
/*     */ 
/*     */       
/* 806 */       USE_SCREEN_SHOTS = Utils.getBooleanOrDefault(configs, "monitoring.screenshots.required", true);
/* 807 */       if (!USE_SCREEN_SHOTS) {
/* 808 */         USE_WEB_CAM_ON_SCREEN_SHOT = false;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 813 */       SCREEN_SHOT_QR_CODE_REQUIRED = Utils.getBooleanOrDefault(configs, "monitoring.screenshots.qr_code", false);
/* 814 */       SCREEN_SHOT_TIMESTAMP_REQUIRED = Utils.getBooleanOrDefault(configs, "monitoring.screenshots.timestamp", false);
/*     */ 
/*     */       
/* 817 */       NETWORK_MONITORING = Utils.getBooleanOrDefault(configs, "monitoring.network.required", true);
/*     */       
/* 819 */       FILE_MONITORING = Utils.getBooleanOrDefault(configs, "monitoring.file.required", true);
/*     */       
/* 821 */       PROCESS_MONITORING = Utils.getBooleanOrDefault(configs, "monitoring.process.required", false);
/*     */       
/* 823 */       BLUETOOTH_MONITORING = Utils.getBooleanOrDefault(configs, "monitoring.bluetooth.required", true);
/*     */       
/* 825 */       AUDIO_MONITORING = Utils.getBooleanOrDefault(configs, "monitoring.audio.required", false);
/*     */       
/* 827 */       VIDEO_MONITORING = Utils.getBooleanOrDefault(configs, "monitoring.video.required", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 835 */       PASSKEY_DIRECTORY = Utils.getStringOrDefault(configs, "passkey.directory", VERSION);
/* 836 */       PASSKEY_LOG = Utils.getStringOrDefault(configs, "passkey.log", "0.6.0");
/* 837 */       PASSKEY_VIDEO = Utils.getStringOrDefault(configs, "passkey.video", "0.6.0");
/* 838 */       PASSKEY_FILE_UPLOAD = Utils.getStringOrDefault(configs, "passkey.file_upload", "0.6.0");
/* 839 */       PASSKEY_EXAM = Utils.getStringOrDefault(configs, "passkey.exam", "0.6.1");
/*     */     } 
/*     */     
/* 842 */     return configs;
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-Launcher-0.7.5\!\edu\carleton\cas\constants\Shared.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */