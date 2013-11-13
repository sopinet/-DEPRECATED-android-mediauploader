package com.sopinet.mediauploader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class UtilsHelper
{
  private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
  private static final char[] HEXADECIMAL;
  private static final int IO_BUFFER_SIZE = 4096;
  private static Matcher matcher;
  private static Pattern pattern;

  static
  {
    char[] arrayOfChar = new char[16];
    arrayOfChar[0] = 48;
    arrayOfChar[1] = 49;
    arrayOfChar[2] = 50;
    arrayOfChar[3] = 51;
    arrayOfChar[4] = 52;
    arrayOfChar[5] = 53;
    arrayOfChar[6] = 54;
    arrayOfChar[7] = 55;
    arrayOfChar[8] = 56;
    arrayOfChar[9] = 57;
    arrayOfChar[10] = 97;
    arrayOfChar[11] = 98;
    arrayOfChar[12] = 99;
    arrayOfChar[13] = 100;
    arrayOfChar[14] = 101;
    arrayOfChar[15] = 102;
    HEXADECIMAL = arrayOfChar;
  }

  static void copy(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte[4096];
    while (true)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i == -1)
        return;
      paramOutputStream.write(arrayOfByte, 0, i);
    }
  }

  public static String md5(String paramString)
  {
    String str;
    try
    {
      byte[] arrayOfByte = MessageDigest.getInstance("MD5").digest(paramString.getBytes());
      StringBuilder localStringBuilder = new StringBuilder(2 * arrayOfByte.length);
      for (int i = 0; ; i++)
      {
        if (i >= arrayOfByte.length)
        {
          str = localStringBuilder.toString();
          break;
        }
        int j = 0xF & arrayOfByte[i];
        int k = (0xF0 & arrayOfByte[i]) >> 4;
        localStringBuilder.append(HEXADECIMAL[k]);
        localStringBuilder.append(HEXADECIMAL[j]);
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      str = null;
    }
    return str;
  }

  public static boolean validateEmail(String paramString)
  {
    pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    matcher = pattern.matcher(paramString);
    return matcher.matches();
  }
  
  public static boolean isOnline(Context act) {
      ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo ni = cm.getActiveNetworkInfo();
      if (ni!=null && ni.isAvailable() && ni.isConnected()) {
    	  if (MediaUploader.MODE.equals("wifi")) {
    		  if (UtilsHelper.isOnlineWifi(act)) {
    			  return true;
    		  } else {
    			  return false;
    		  }
    	  } else {
    		  return true;
    	  }
      } else {
          return false; 
      }
  }
  public static boolean isOnline(Activity act) {
  	Context con = act.getApplicationContext();
  	return isOnline(con);
  }
  
  public static boolean isOnline3G(Context act) {
	ConnectivityManager manager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);

	//For 3G check
	if (manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) == null) return false;
	boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
	            .isConnectedOrConnecting();
	
	return is3g;
  }
  
  public static String isOnlineTEXT(Context act) {
	  //Log.d("TEMA", MediaUploader.MODE);
	  if (UtilsHelper.isOnline3G(act) && MediaUploader.MODE.equals("wifi")) {
		  return "La subida se efectuará automáticamente cuando disponga de conexión Wifi.";
	  } else {
		  return "No hay conexión a Internet, se enviará cuando la haya.";
	  }
  }
  
  public static boolean isOnlineWifi(Context act) {
	ConnectivityManager manager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
	
	//For WiFi Check
	boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
	            .isConnectedOrConnecting();
	
	return isWifi;
  }
  
  public static String getRealPathFromURI(Activity act, Uri contentUri) {
	    if (contentUri.toString().substring(0, 7).equals("file://")) {
	    	return contentUri.toString().substring(7);
	    } else {
		  	// can post image
		  	String [] proj={MediaStore.Images.Media.DATA};
		  	Cursor cursor = act.managedQuery( contentUri,
		  	proj, // Which columns to return
		  	null, // WHERE clause; which rows to return (all rows)
		  	null, // WHERE clause selection arguments (none)
		  	null); // Order-by clause (ascending by name)
		  	int column_index;
		  	column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		  	cursor.moveToFirst();
		  	return cursor.getString(column_index);
	    }
	  }   
  
  static public String getContents(File aFile) {
      //...checks on aFile are elided
      StringBuilder contents = new StringBuilder();
      
      try {
        //use buffering, reading one line at a time
        //FileReader always assumes default encoding is OK!
        BufferedReader input =  new BufferedReader(new FileReader(aFile));
        try {
          String line = null; //not declared within while loop
          /*
          * readLine is a bit quirky :
          * it returns the content of a line MINUS the newline.
          * it returns null only for the END of the stream.
          * it returns an empty String if two newlines appear in a row.
          */
          while (( line = input.readLine()) != null){
            contents.append(line);
            contents.append(System.getProperty("line.separator"));
          }
        }
        finally {
          input.close();
        }
      }
      catch (IOException ex){
        ex.printStackTrace();
      }
      
      return contents.toString();
    }  
  
	  public static String convertURL(String str) {
	  	return str.trim();
	  }
	  
	  public static String getAPP(Context con) {
			final PackageManager pm = con.getApplicationContext().getPackageManager();
			ApplicationInfo ai;
			try {
			    ai = pm.getApplicationInfo( con.getPackageName(), 0);
			} catch (final NameNotFoundException e) {
			    ai = null;
			}
			final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
			return applicationName;
	  }
}