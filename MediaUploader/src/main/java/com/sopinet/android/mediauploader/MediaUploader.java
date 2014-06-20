package com.sopinet.android.mediauploader;

public class MediaUploader
{
  public static String URL = "http://configureyourdomain.com/api.php";
  public static String MODE = "any";
  public static Integer SQLVERSION = 6;
  public static String SENDINGCLASS = "SendingActivity";
  public static String SENDINGCONTEXT = "com.x.y";
  public static String RES_OK = null;
  public static Boolean RES_VIEWERROR = false;
  public static Integer ICON_GREEN = android.R.drawable.presence_online;
  public static Integer ICON_YELLOW = android.R.drawable.presence_offline; 
  public static Integer ICON_RED = android.R.drawable.presence_busy;
  public static Integer ICON_BLUE = android.R.drawable.presence_away;
  
  public static Integer TEXT_SENDING = R.string.sending;
  public static Integer TEXT_UPLOADWIFI = R.string.uploadwifi;
  public static Integer TEXT_NONETWORK = R.string.nonetwork;
  public static Integer TEXT_SENDOK = R.string.sendok;
  public static Integer TEXT_SENDERROR = R.string.senderror;
}