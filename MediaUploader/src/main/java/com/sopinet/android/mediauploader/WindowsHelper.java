package com.sopinet.android.mediauploader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class WindowsHelper
{
  public static void showCancelMessage(Activity paramActivity)
  {
    Toast.makeText(paramActivity.getApplicationContext(), "Ha cancelado la carga de esta pantalla, para recargarla pulse: Menú -> Recargar", 1).show();
  }

  public static void showMessage(Context con, String text)
  {
    Toast.makeText(con, text, 1).show();
  }
  
  public static void showNoNetwork(Context con) {
	  WindowsHelper.showMessage(con, "No tiene conexión a Internet");
	  // TODO: Redirigir a intent por defecto, configuración
	  // Intent localIntent = new Intent().setClass(con, MediaUploader.Main_ACTIVITY.class);
	  // con.startActivity(localIntent);
  }
}