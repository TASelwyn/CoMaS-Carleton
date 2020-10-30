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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Shared
/*     */ {
/*     */   public static final String ACTUAL_VERSION_OF_CLIENT = "0.7.5";
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
/* 129 */   public static String SERVER_CHOSEN = null;
/* 130 */   public static String DIRECTORY_HOST = "comas.cogerent.com";
/* 131 */   public static String EXAM_HOST = "comas.cogerent.com";
/* 132 */   public static String UPLOAD_HOST = "comas.cogerent.com";
/* 133 */   public static String LOG_HOST = "comas.cogerent.com";
/* 134 */   public static String VIDEO_HOST = "comas.cogerent.com";
/* 135 */   public static String CMS_HOST = "comas.cogerent.com";
/* 136 */   public static String WEBSOCKET_HOST = "comas.cogerent.com";
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
/* 148 */   public static String COMAS_DOT_INI = "comas.ini";
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
/* 159 */     if (host.startsWith("https://"))
/* 160 */       return String.format("%s:%s/CMS/rest/exam/exam.ini", new Object[] { host, "8443" }); 
/* 161 */     if (host.startsWith("http://")) {
/* 162 */       return String.format("%s:%s/CMS/rest/exam/exam.ini", new Object[] { host, "8080" });
/*     */     }
/* 164 */     return String.format("%s://%s:%s/CMS/rest/exam/exam.ini", new Object[] { PROTOCOL, host, PORT });
/*     */   }
/*     */   
/* 167 */   public static String EXAM_CONFIGURATION_FILE = "https://comas.cogerent.com:8443/CMS/rest/exam/exam.ini";
/* 168 */   public static String LOCAL_EXAM_CONFIGURATION_FILE = "http://192.168.86.26:8080/CMS/rest/exam/exam.ini";
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
/* 179 */   public static String PROTOCOL = "https";
/* 180 */   public static String WS_PROTOCOL = "wss";
/*     */   
/*     */   public static final String HTTP_PORT = "8080";
/*     */   public static final String HTTPS_PORT = "8443";
/* 184 */   public static String PORT = "8443";
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
/* 200 */   public static String LOG_PATH = "log";
/* 201 */   public static String VIDEO_PATH = "video";
/* 202 */   public static String UPLOAD_PATH = "upload";
/* 203 */   public static String REGISTER_PATH = "register";
/*     */   
/*     */   public static final String BASE_REGISTRATION_SERVICE = "/COMP4601-Directory/rest/directory/";
/*     */   
/*     */   public static final String BASE_UPLOAD_SERVICE = "/COMP4601-FileUpload/rest/file/";
/*     */   public static final String BASE_LOG_SERVICE = "/COMP4601-Log/rest/logger/";
/*     */   public static final String BASE_VIDEO_SERVICE = "/COMP4601-Video/rest/logger/";
/*     */   public static final String BASE_CMS_SERVICE = "/CMS/rest/";
/*     */   public static final String BASE_EXAM_SERVICE = "/Exam/rest/exam/";
/*     */   public static final String BASE_WEBSOCKET_SERVICE = "/WebSocket/";
/* 213 */   public static final String REGISTRATION_SERVICE = "/COMP4601-Directory/rest/directory/" + REGISTER_PATH;
/* 214 */   public static final String UPLOAD_SERVICE = "/COMP4601-FileUpload/rest/file/" + UPLOAD_PATH;
/* 215 */   public static final String LOG_SERVICE = "/COMP4601-Log/rest/logger/" + LOG_PATH;
/* 216 */   public static final String VIDEO_SERVICE = "/COMP4601-Video/rest/logger/" + VIDEO_PATH;
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
/* 233 */   public static String BASE_LOGIN = String.valueOf(PROTOCOL) + "://" + DIRECTORY_HOST + ":" + PORT + "/COMP4601-Directory/rest/directory/";
/* 234 */   public static String BASE_LOG = String.valueOf(PROTOCOL) + "://" + LOG_HOST + ":" + PORT + "/COMP4601-Log/rest/logger/";
/* 235 */   public static String BASE_UPLOAD = String.valueOf(PROTOCOL) + "://" + UPLOAD_HOST + ":" + PORT + "/COMP4601-FileUpload/rest/file/";
/* 236 */   public static String BASE_VIDEO = String.valueOf(PROTOCOL) + "://" + VIDEO_HOST + ":" + PORT + "/COMP4601-Video/rest/logger/";
/* 237 */   public static String BASE_CMS = String.valueOf(PROTOCOL) + "://" + CMS_HOST + ":" + PORT + "/CMS/rest/";
/* 238 */   public static String BASE_EXAM = String.valueOf(PROTOCOL) + "://" + CMS_HOST + ":" + PORT + "/Exam/rest/exam/";
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
/* 253 */   public static String LOGIN_URL = String.valueOf(PROTOCOL) + "://" + DIRECTORY_HOST + ":" + PORT + REGISTRATION_SERVICE;
/* 254 */   public static String EXAM_ZIP = String.valueOf(BASE_CMS) + "/exam/" + "exam.zip";
/* 255 */   public static String TOOLS_ZIP = String.valueOf(BASE_CMS) + "/exam/" + "tools.zip";
/* 256 */   public static String RESOURCES_ZIP = String.valueOf(BASE_CMS) + "/exam/" + "resources.zip";
/* 257 */   public static String EXAM_URL = String.valueOf(PROTOCOL) + "://" + UPLOAD_HOST + ":" + PORT + UPLOAD_SERVICE;
/* 258 */   public static String LOG_URL = String.valueOf(PROTOCOL) + "://" + LOG_HOST + ":" + PORT + LOG_SERVICE;
/* 259 */   public static String VIDEO_URL = String.valueOf(PROTOCOL) + "://" + VIDEO_HOST + ":" + PORT + VIDEO_SERVICE;
/* 260 */   public static String CMS_URL = String.valueOf(PROTOCOL) + "://" + CMS_HOST + ":" + PORT + "/CMS/rest/";
/* 261 */   public static String HANDLE_EXE = String.valueOf(PROTOCOL) + "://" + EXAM_HOST + "/exam/" + "handle.exe";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DESKTOP = "Desktop";
/*     */ 
/*     */   
/* 268 */   public static String DESKTOP_DIR = "CoMaS";
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
/* 280 */   public static final String HOME = System.getProperty("user.home");
/* 281 */   public static String DIR = String.valueOf(getDesktopDirectory()) + File.separator + DESKTOP_DIR;
/* 282 */   public static final String ZIP = HOME;
/* 283 */   public static String DOWNLOADS_DIR = String.valueOf(getDownloadsDirectory()) + File.separator;
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
/* 297 */     String os = System.getProperty("os.name").toLowerCase();
/* 298 */     return os.startsWith("win");
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
/* 310 */     String desktopDirectory = null;
/*     */     
/* 312 */     String os = System.getProperty("os.name").toLowerCase();
/* 313 */     if (os.startsWith("win")) {
/* 314 */       desktopDirectory = WindowsRegistry.readRegistry("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Desktop");
/* 315 */       if (desktopDirectory == null) {
/* 316 */         desktopDirectory = System.getenv("USERPROFILE");
/* 317 */         if (desktopDirectory != null) {
/* 318 */           desktopDirectory = String.valueOf(desktopDirectory) + File.separator + "Desktop";
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 323 */     if (isWriteableDirectory(desktopDirectory))
/* 324 */       return desktopDirectory; 
/* 325 */     return String.valueOf(HOME) + File.separator + "Desktop";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getDownloadsDirectory() {
/* 332 */     String dir = String.valueOf(HOME) + File.separator + "Downloads";
/* 333 */     if (isWriteableDirectory(dir)) {
/* 334 */       return dir;
/*     */     }
/* 336 */     String downloadsDirectory = null;
/* 337 */     String os = System.getProperty("os.name").toLowerCase();
/* 338 */     if (os.startsWith("win")) {
/* 339 */       downloadsDirectory = WindowsRegistry.readRegistry("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Personal");
/* 340 */       if (isWriteableDirectory(downloadsDirectory))
/* 341 */         return downloadsDirectory; 
/* 342 */       downloadsDirectory = System.getenv("USERPROFILE");
/* 343 */       if (downloadsDirectory != null)
/* 344 */         downloadsDirectory = String.valueOf(downloadsDirectory) + File.separator + "Downloads"; 
/* 345 */       if (isWriteableDirectory(downloadsDirectory)) {
/* 346 */         return downloadsDirectory;
/*     */       }
/*     */     } 
/* 349 */     return DIR;
/*     */   }
/*     */   
/*     */   public static boolean isWriteableDirectory(String dir) {
/* 353 */     if (dir != null) {
/* 354 */       File directoryFile = new File(dir);
/* 355 */       if (directoryFile.exists() && 
/* 356 */         directoryFile.isDirectory() && 
/* 357 */         directoryFile.canWrite())
/* 358 */         return true; 
/*     */     } 
/* 360 */     return false;
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
/* 380 */   public static int LOG_GENERATION_FREQUENCY = 10;
/*     */   
/*     */   public static final String LOG_FILE_BASE = "comas-system";
/*     */   
/*     */   public static final String LOG_FILE_ENDING = "-log.html";
/*     */   
/*     */   public static final String LOG_FILE_NAME = "comas-system-log.html";
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
/*     */   public static boolean AUTO_ARCHIVE = true;
/*     */   
/* 412 */   public static int AUTO_ARCHIVE_FREQUENCY = 20;
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
/* 424 */   public static int ABSOLUTE_MAX_INTERVAL = 60;
/* 425 */   public static int ABSOLUTE_MIN_INTERVAL = 1;
/* 426 */   public static int MAX_INTERVAL = 50;
/* 427 */   public static int MIN_INTERVAL = 10;
/* 428 */   public static int MAX_FAILURES = 100;
/* 429 */   public static int MAX_SESSION_FAILURES = 10000;
/* 430 */   public static int ALERT_FAILURE_FREQUENCY = 20;
/* 431 */   public static int MAX_MSECS_TO_WAIT_TO_END = 60000;
/* 432 */   public static int UPLOAD_THRESHOLD_IN_MSECS = 60000;
/* 433 */   public static int MIN_MSECS_BETWEEN_USER_UPLOADS = 300000;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 440 */   public static int RETRY_TIME = 20000;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 445 */   public static int FREQUENCY_TO_CHECK_EXAM_DIRECTORY = 900000;
/* 446 */   public static int MIN_EVENTS_IN_EXAM_DIRECTORY = 0;
/* 447 */   public static int MAX_NUMBER_OF_FILE_WATCHING_FAILURES = 10;
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
/*     */   public static final String ISSUE = "Issue:";
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
/*     */   public static final String PROBLEM_FILES = "Issue:Files";
/*     */ 
/*     */   
/*     */   public static final String NO_DOWNLOAD_REQUIRED = "NO_DOWNLOAD_REQUIRED";
/*     */ 
/*     */   
/* 492 */   public static Level LOGGING_LEVEL = Level.INFO;
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
/* 504 */   public static float IMAGE_COMPRESSION = 0.2F;
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
/* 516 */   public static String VERSION = "0.7.5";
/* 517 */   public static String PASSKEY_DIRECTORY = "SimpleDirectory";
/* 518 */   public static String PASSKEY_LOG = "LoggerService";
/* 519 */   public static String PASSKEY_FILE_UPLOAD = "UploadFileService";
/* 520 */   public static String PASSKEY_VIDEO = "VideoService";
/* 521 */   public static String PASSKEY_EXAM = "ExamService";
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
/* 533 */     return String.valueOf(protocol) + "://" + host + ":" + port + service;
/*     */   }
/*     */   
/*     */   public static String getCourseDirectory(String course) {
/* 537 */     return String.valueOf(DIR) + File.separator + course;
/*     */   }
/*     */   
/*     */   public static String getActivityDirectory(String course, String activity) {
/* 541 */     return String.valueOf(getCourseDirectory(course)) + File.separator + activity;
/*     */   }
/*     */   
/*     */   public static String getBaseDirectory(String course, String activity) {
/* 545 */     return String.valueOf(DIR) + File.separator + course + File.separator + activity + File.separator;
/*     */   }
/*     */   
/*     */   public static String getExamDirectory(String course, String activity) {
/* 549 */     return String.valueOf(getBaseDirectory(course, activity)) + "exam" + File.separator;
/*     */   }
/*     */   
/*     */   public static String getLogsDirectory(String course, String activity) {
/* 553 */     return String.valueOf(getBaseDirectory(course, activity)) + "logs" + File.separator;
/*     */   }
/*     */   
/*     */   public static String getResourcesDirectory(String course, String activity) {
/* 557 */     return String.valueOf(getBaseDirectory(course, activity)) + "resources" + File.separator;
/*     */   }
/*     */   
/*     */   public static String getScreensDirectory(String course, String activity) {
/* 561 */     return String.valueOf(getBaseDirectory(course, activity)) + "screens" + File.separator;
/*     */   }
/*     */   
/*     */   public static String getArchivesDirectory(String course, String activity) {
/* 565 */     return String.valueOf(getBaseDirectory(course, activity)) + "archives" + File.separator;
/*     */   }
/*     */   
/*     */   public static String getToolsDirectory(String course, String activity) {
/* 569 */     return String.valueOf(getBaseDirectory(course, activity)) + "tools" + File.separator;
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
/* 584 */   public static Properties CONFIGS = new Properties();
/*     */   
/*     */   public static final String DEFAULT_HOST = "hostname";
/*     */   
/*     */   public static final String DEFAULT_PROTOCOL = "protocol";
/*     */   
/*     */   public static void setupURLs(String course) {
/*     */     Properties configs;
/* 592 */     if (!CONFIGS.isEmpty()) {
/* 593 */       configs = CONFIGS;
/*     */     } else {
/* 595 */       Logger.output("Accessing " + EXAM_CONFIGURATION_FILE);
/* 596 */       configs = Utils.getProperties(EXAM_CONFIGURATION_FILE);
/* 597 */       if (configs.isEmpty()) {
/* 598 */         configs = Utils.getProperties(LOCAL_EXAM_CONFIGURATION_FILE);
/*     */       }
/*     */     } 
/* 601 */     String defaultGlobalHost = configs.getProperty("hostname", DIRECTORY_HOST);
/* 602 */     String defaultGlobalProtocol = configs.getProperty("protocol", PROTOCOL);
/*     */     
/* 604 */     if (course != null) {
/* 605 */       String defaultHost = configs.getProperty(String.valueOf(course) + ".host", defaultGlobalHost);
/* 606 */       DIRECTORY_HOST = configs.getProperty(String.valueOf(course) + ".directory", defaultHost);
/*     */       
/* 608 */       EXAM_HOST = configs.getProperty(String.valueOf(course) + ".exam", defaultHost);
/*     */       
/* 610 */       UPLOAD_HOST = configs.getProperty(String.valueOf(course) + ".upload", defaultHost);
/*     */       
/* 612 */       LOG_HOST = configs.getProperty(String.valueOf(course) + ".log", defaultHost);
/*     */       
/* 614 */       VIDEO_HOST = configs.getProperty(String.valueOf(course) + ".video", defaultHost);
/*     */       
/* 616 */       CMS_HOST = configs.getProperty(String.valueOf(course) + ".cms", defaultHost);
/*     */       
/* 618 */       WEBSOCKET_HOST = configs.getProperty(String.valueOf(course) + ".websocket", defaultHost);
/* 619 */       PROTOCOL = configs.getProperty(String.valueOf(course) + ".protocol", defaultGlobalProtocol);
/* 620 */       if (PROTOCOL.equals("https")) {
/* 621 */         WS_PROTOCOL = "wss";
/* 622 */         PORT = "8443";
/*     */       } else {
/* 624 */         WS_PROTOCOL = "ws";
/* 625 */         PORT = "8080";
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 631 */     updateURLs();
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
/* 642 */     LOGIN_URL = service(PROTOCOL, DIRECTORY_HOST, PORT, REGISTRATION_SERVICE);
/*     */     
/* 644 */     EXAM_URL = service(PROTOCOL, UPLOAD_HOST, PORT, UPLOAD_SERVICE);
/* 645 */     LOG_URL = service(PROTOCOL, LOG_HOST, PORT, LOG_SERVICE);
/* 646 */     VIDEO_URL = service(PROTOCOL, VIDEO_HOST, PORT, VIDEO_SERVICE);
/* 647 */     CMS_URL = service(PROTOCOL, CMS_HOST, PORT, "/CMS/rest/");
/*     */     
/* 649 */     BASE_LOGIN = service(PROTOCOL, DIRECTORY_HOST, PORT, 
/* 650 */         "/COMP4601-Directory/rest/directory/");
/* 651 */     BASE_LOG = service(PROTOCOL, LOG_HOST, PORT, "/COMP4601-Log/rest/logger/");
/* 652 */     BASE_UPLOAD = service(PROTOCOL, UPLOAD_HOST, PORT, "/COMP4601-FileUpload/rest/file/");
/* 653 */     BASE_VIDEO = service(PROTOCOL, VIDEO_HOST, PORT, "/COMP4601-Video/rest/logger/");
/* 654 */     BASE_CMS = service(PROTOCOL, CMS_HOST, PORT, "/CMS/rest/");
/* 655 */     BASE_EXAM = service(PROTOCOL, CMS_HOST, PORT, "/Exam/rest/exam/");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 664 */     EXAM_ZIP = String.valueOf(BASE_CMS) + "exam" + "/" + "exam.zip";
/* 665 */     TOOLS_ZIP = String.valueOf(BASE_CMS) + "exam" + "/" + "tools.zip";
/* 666 */     RESOURCES_ZIP = String.valueOf(BASE_CMS) + "exam" + "/" + "resources.zip";
/* 667 */     LOGIN_CONFIGURATION_URL = String.valueOf(BASE_CMS) + "exam" + "/" + "login.ini";
/* 668 */     HANDLE_EXE = String.valueOf(BASE_CMS) + "exam" + "/" + "handle.exe";
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
/*     */   public static boolean PROCESS_MONITORING = false;
/*     */   public static boolean BLUETOOTH_MONITORING = true;
/*     */   public static boolean AUDIO_MONITORING = false;
/*     */   public static boolean VIDEO_MONITORING = false;
/*     */   public static boolean SCREEN_SHOT_QR_CODE_REQUIRED = true;
/*     */   public static boolean SCREEN_SHOT_TIMESTAMP_REQUIRED = true;
/* 691 */   public static float SCREEN_SHOT_TIMESTAMP_HEIGHT = 0.0F;
/* 692 */   public static float SCREEN_SHOT_TIMESTAMP_WIDTH = 0.5F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 700 */   public static int MAX_SUPPORTED_JAVA_VERSION = 20;
/* 701 */   public static int MIN_SUPPORTED_JAVA_VERSION = 8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 707 */   public static int MIN_DRIVE_SPACE_THRESHOLD_PERCENTAGE = 5;
/* 708 */   public static int MIN_DRIVE_SPACE_THRESHOLD_MB = 2000;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 713 */   public static String SUPPORT_MESSAGE = "";
/* 714 */   public static String STARTUP_MESSAGE = "";
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
/*     */   public static Properties getLoginProperties(String course) {
/* 728 */     Properties configs = Utils.getProperties(LOGIN_CONFIGURATION_URL);
/*     */     
/* 730 */     if (configs == null) {
/* 731 */       configs = new Properties();
/*     */     } else {
/*     */       String logging_level;
/* 734 */       SUPPORT_MESSAGE = Utils.getStringOrDefault(configs, "application.support.message", "Please contact support.");
/* 735 */       STARTUP_MESSAGE = Utils.getStringOrDefault(configs, "application.startup.message", "");
/*     */ 
/*     */       
/* 738 */       MIN_DRIVE_SPACE_THRESHOLD_PERCENTAGE = Utils.getIntegerOrDefaultInRange(configs, 
/* 739 */           "min_free_drive_space_percentage", MIN_DRIVE_SPACE_THRESHOLD_PERCENTAGE, 0, 5);
/* 740 */       MIN_DRIVE_SPACE_THRESHOLD_MB = Utils.getIntegerOrDefaultInRange(configs, "min_free_drive_space_mb", 
/* 741 */           MIN_DRIVE_SPACE_THRESHOLD_MB, 0, 2000);
/*     */       
/* 743 */       MAX_SUPPORTED_JAVA_VERSION = Utils.getIntegerOrDefaultInRange(configs, "max_supported_java_version", 
/* 744 */           MAX_SUPPORTED_JAVA_VERSION, 8, 100);
/* 745 */       MIN_SUPPORTED_JAVA_VERSION = Utils.getIntegerOrDefaultInRange(configs, "min_supported_java_version", 
/* 746 */           MIN_SUPPORTED_JAVA_VERSION, 8, MAX_SUPPORTED_JAVA_VERSION);
/*     */ 
/*     */ 
/*     */       
/* 750 */       AUTO_ARCHIVE = Utils.getBooleanOrDefault(configs, "auto_archive", AUTO_ARCHIVE);
/* 751 */       AUTO_ARCHIVE_FREQUENCY = Utils.getIntegerOrDefaultInRange(configs, "auto_archive_frequency", 
/* 752 */           AUTO_ARCHIVE_FREQUENCY, 0, 100);
/*     */       
/* 754 */       if (!AUTO_ARCHIVE) {
/* 755 */         AUTO_ARCHIVE_FREQUENCY = Integer.MAX_VALUE;
/*     */       }
/* 757 */       LOG_GENERATION_FREQUENCY = Utils.getIntegerOrDefaultInRange(configs, "log_generation_frequency", 
/* 758 */           LOG_GENERATION_FREQUENCY, 1, 2147483647);
/*     */ 
/*     */       
/* 761 */       if (configs.contains("logging_level")) {
/* 762 */         logging_level = configs.getProperty("logging_level");
/*     */       } else {
/* 764 */         logging_level = Utils.getStringOrDefault(configs, "logs.level", "INFO");
/*     */       } 
/*     */       try {
/* 767 */         LOGGING_LEVEL = Level.parse(logging_level);
/* 768 */       } catch (IllegalArgumentException e) {
/* 769 */         LOGGING_LEVEL = Level.INFO;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 776 */       VERSION = Utils.getStringOrDefault(configs, "version", VERSION);
/*     */       
/* 778 */       MAX_INTERVAL = Utils.getIntegerOrDefaultInRange(configs, "max_interval", MAX_INTERVAL, 1, ABSOLUTE_MAX_INTERVAL);
/* 779 */       MIN_INTERVAL = Utils.getIntegerOrDefaultInRange(configs, "min_interval", MIN_INTERVAL, 1, MAX_INTERVAL);
/* 780 */       MAX_FAILURES = Utils.getIntegerOrDefaultInRange(configs, "max_failures", MAX_FAILURES, 1, 
/* 781 */           2147483647);
/* 782 */       MAX_MSECS_TO_WAIT_TO_END = Utils.getIntegerOrDefaultInRange(configs, "max_msecs_to_wait_to_end", 
/* 783 */           MAX_MSECS_TO_WAIT_TO_END, 1, MAX_MSECS_TO_WAIT_TO_END * 10);
/* 784 */       UPLOAD_THRESHOLD_IN_MSECS = Utils.getIntegerOrDefaultInRange(configs, "upload_threshold_in_msecs", 
/* 785 */           UPLOAD_THRESHOLD_IN_MSECS, 1, 2147483647);
/* 786 */       MIN_MSECS_BETWEEN_USER_UPLOADS = Utils.getIntegerOrDefaultInRange(configs, "min_msecs_between_user_uploads", 
/* 787 */           MIN_MSECS_BETWEEN_USER_UPLOADS, 60000, 21600000);
/*     */ 
/*     */       
/* 790 */       RETRY_TIME = Utils.getIntegerOrDefaultInRange(configs, "retry_time", RETRY_TIME, 1000, 60000);
/*     */       
/* 792 */       FREQUENCY_TO_CHECK_EXAM_DIRECTORY = Utils.getIntegerOrDefaultInRange(configs, 
/* 793 */           "frequency_to_check_exam_directory", FREQUENCY_TO_CHECK_EXAM_DIRECTORY, 1, 1000) * 60 * 1000;
/* 794 */       MIN_EVENTS_IN_EXAM_DIRECTORY = Utils.getIntegerOrDefaultInRange(configs, "min_events_in_exam_directory", 
/* 795 */           MIN_EVENTS_IN_EXAM_DIRECTORY, 0, 2147483647);
/*     */       
/* 797 */       IMAGE_COMPRESSION = Utils.getFloatOrDefaultInRange(configs, "image_compression", IMAGE_COMPRESSION, 0.0F, 
/* 798 */           1.0F);
/*     */       
/* 800 */       USE_ACTIVITY_CODES = Utils.getBooleanOrDefault(configs, "use_activity_codes", USE_ACTIVITY_CODES);
/* 801 */       WEB_CAM_MANDATORY = Utils.getBooleanOrDefault(configs, "webcam.required", false);
/* 802 */       USE_WEB_CAM = Utils.getBooleanOrDefault(configs, "webcam.enabled", true);
/* 803 */       USE_WEB_CAM_ON_SCREEN_SHOT = Utils.getBooleanOrDefault(configs, "webcam.on_screen_shot", false);
/*     */       
/* 805 */       if (USE_WEB_CAM_ON_SCREEN_SHOT) {
/* 806 */         USE_WEB_CAM = false;
/*     */       }
/* 808 */       if (!USE_WEB_CAM_ON_SCREEN_SHOT && !USE_WEB_CAM) {
/* 809 */         WEB_CAM_MANDATORY = false;
/*     */       }
/*     */ 
/*     */       
/* 813 */       USE_SCREEN_SHOTS = Utils.getBooleanOrDefault(configs, "monitoring.screenshots.required", true);
/* 814 */       if (!USE_SCREEN_SHOTS) {
/* 815 */         USE_WEB_CAM_ON_SCREEN_SHOT = false;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 820 */       SCREEN_SHOT_QR_CODE_REQUIRED = Utils.getBooleanOrDefault(configs, "monitoring.screenshots.qr_code", false);
/* 821 */       SCREEN_SHOT_TIMESTAMP_REQUIRED = Utils.getBooleanOrDefault(configs, "monitoring.screenshots.timestamp", false);
/*     */       
/* 823 */       SCREEN_SHOT_TIMESTAMP_HEIGHT = Utils.getIntegerOrDefaultInRange(configs, "screenshot.timestamp.height", 0, 0, 100) / 100.0F;
/* 824 */       SCREEN_SHOT_TIMESTAMP_WIDTH = Utils.getIntegerOrDefaultInRange(configs, "screenshot.timestamp.width", 50, 0, 100) / 100.0F;
/*     */ 
/*     */       
/* 827 */       NETWORK_MONITORING = Utils.getBooleanOrDefault(configs, "monitoring.network.required", true);
/*     */       
/* 829 */       FILE_MONITORING = Utils.getBooleanOrDefault(configs, "monitoring.file.required", true);
/*     */       
/* 831 */       PROCESS_MONITORING = Utils.getBooleanOrDefault(configs, "monitoring.process.required", false);
/*     */       
/* 833 */       BLUETOOTH_MONITORING = Utils.getBooleanOrDefault(configs, "monitoring.bluetooth.required", true);
/*     */       
/* 835 */       AUDIO_MONITORING = Utils.getBooleanOrDefault(configs, "monitoring.audio.required", false);
/*     */       
/* 837 */       VIDEO_MONITORING = Utils.getBooleanOrDefault(configs, "monitoring.video.required", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 845 */       PASSKEY_DIRECTORY = Utils.getStringOrDefault(configs, "passkey.directory", VERSION);
/* 846 */       PASSKEY_LOG = Utils.getStringOrDefault(configs, "passkey.log", "0.6.0");
/* 847 */       PASSKEY_VIDEO = Utils.getStringOrDefault(configs, "passkey.video", "0.6.0");
/* 848 */       PASSKEY_FILE_UPLOAD = Utils.getStringOrDefault(configs, "passkey.file_upload", "0.6.0");
/* 849 */       PASSKEY_EXAM = Utils.getStringOrDefault(configs, "passkey.exam", "0.6.1");
/*     */     } 
/*     */     
/* 852 */     return configs;
/*     */   }
/*     */ }


/* Location:              C:\Users\Thomas\Desktop\Gamer Chair\CoMaS\CoMaS-0.7.4g\!\edu\carleton\cas\constants\Shared.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */