package com.sopinet.mediauploader;

import com.sopinet.mediauploader.ChangeConnectivity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.RemoteViews;

import com.sopinet.mediauploader.*;

public class HttpPostHelper {
	public static Notification notification = null;
	public static NotificationManager notificationManager = null;
	
	private static void initNotify(int indice, String state, Context act, Intent localIntent) {
    	notification = new Notification(R.drawable.upload, "Espere...", System
                .currentTimeMillis());
    	notification.contentView = new RemoteViews(act.getApplicationContext().getPackageName(), R.layout.download_progress);
		PendingIntent contentIntent = PendingIntent.getActivity(act, 0, localIntent, 0);
        notification.contentIntent = contentIntent;		
		String texto = null;
		if (state.equals("sending")) {
			texto = "Enviando...";
			notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
	        notification.contentView.setImageViewResource(R.id.status_icon, android.R.drawable.presence_away);
	        notification.contentView.setTextViewText(R.id.status_porcentage, "0%");
	        notification.contentView.setProgressBar(R.id.status_progress, 100, 0, false);	        
		} else if (state.equals("nonetwork")) {
			texto = "No hay conexión a Internet, se volverá a enviar cuando la haya.";
			notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
	        notification.contentView.setImageViewResource(R.id.status_icon, android.R.drawable.presence_offline);
	        notification.contentView.setTextViewText(R.id.status_porcentage, "0%");
	        notification.contentView.setProgressBar(R.id.status_progress, 100, 0, false);	        
		} else if (state.equals("ok")) {
			texto = "Envío realizado con éxito.";
			notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
	        notification.contentView.setImageViewResource(R.id.status_icon, android.R.drawable.presence_online);
	        notification.contentView.setTextViewText(R.id.status_porcentage, "100%");
	        notification.contentView.setProgressBar(R.id.status_progress, 100, 100, false);	        
		} else if (state.equals("error")) {
			texto = "No hay conexión a Internet, se volverá a enviar cuando la haya.";
			notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
	        notification.contentView.setImageViewResource(R.id.status_icon, android.R.drawable.presence_busy);
	        notification.contentView.setTextViewText(R.id.status_porcentage, "0%");
	        notification.contentView.setProgressBar(R.id.status_progress, 100, 0, false);			
		}
        notification.contentView.setTextViewText(R.id.status_text, texto);
        notificationManager = (NotificationManager) act.getApplicationContext().getSystemService(
                act.getApplicationContext().NOTIFICATION_SERVICE);
        notificationManager.notify(indice, notification);
        WindowsHelper.showMessage(act, texto);
	}
	
	public static String cancelNotify(final Context con, final Intent localIntent) {
		HttpPostSQL usdbh = HttpPostSQL.getInstance(con);
		SQLiteDatabase db = usdbh.getWritable();
        if(db != null)
        {
        	String[] args = new String[] {"savedDB"};
        	Cursor c = db.rawQuery(" SELECT indice FROM http_index WHERE status=? ", args);
        	if (c.moveToFirst()) {
        		WindowsHelper.showMessage(con, "No hay conexión a Internet, se enviará cuando la haya.");
        	}
        	c.close();
        	args = new String[] {"sending"};		
        	c = db.rawQuery(" SELECT indice FROM http_index WHERE status=? ", args);
        	//Nos aseguramos de que existe al menos un registro
        	if (c.moveToFirst()) {
				//Recorremos el cursor hasta que no haya más registros
				do {
				    String indice = c.getString(0);
				    initNotify(Integer.parseInt(indice), "nonetwork", con, localIntent);
			        ContentValues values = new ContentValues();
		            values.put("status", "savedDB");
		            db.update("http_index", values, "indice="+String.valueOf(indice), null);				     
				} while(c.moveToNext());
        	}
        	c.close();
        	//db.close();
        }
        //usdbh.close();
        return null;
	}
	
	public static synchronized String execNotify(final Context con, final Intent localIntent) {
		// Cogemos datos de base de datos
		HttpPostSQL usdbh = HttpPostSQL.getInstance(con);
		SQLiteDatabase db = usdbh.getWritable();
        if(db != null)
        {
        	String[] args = new String[] {"savedDB"};
        	Cursor c = db.rawQuery(" SELECT indice FROM http_index WHERE status=? ", args);
        	//Nos aseguramos de que existe al menos un registro
        	if (c.moveToFirst()) {        		
        	     //Recorremos el cursor hasta que no haya más registros
        	     do {
        	          String indice = c.getString(0);
             		 // Especificamos que vamos a empezar
     	        	 ContentValues values = new ContentValues();
     	        	 values.put("status", "sending");
     	        	 db.update("http_index", values, "indice="+String.valueOf(indice), null);
     	        	 
        	          String[] args2 = new String[] {String.valueOf(indice)};
        	          Cursor d = db.rawQuery(" SELECT key, value FROM http_string WHERE indice=? ", args2);
        	          if (d.moveToFirst()) {
        	        	  String data[] = new String[d.getCount() * 2];
        	        	  data[0] = String.valueOf(indice);
        	        	  int i = 0;
        	        	  do {
        	        		  data[i] = d.getString(0);
        	        		  i++;
        	        		  data[i] = d.getString(1);
        	        		  i++;
        	        	  } while (d.moveToNext());
							HttpPostTask task = new HttpPostTask(){
							    @Override
							    protected void onPreExecute() {
							    	initNotify(this.indice, "sending", con, localIntent);
							    }
								@Override
								protected void onPostExecute(String result) {
									if (result != null) {
								        ContentValues values = new ContentValues();
							            values.put("status", "finished");
							            HttpPostSQL usdbh = HttpPostSQL.getInstance(con);
							    		SQLiteDatabase db = usdbh.getWritable();
							    		if (db != null) {
							    			db.delete("http_index", "indice="+String.valueOf(this.indice), null);
							    			//db.close();
							    		}
							    		//usdbh.close();
							            initNotify(this.indice, "ok", con, localIntent);
									} else {
								        ContentValues values = new ContentValues();
							            values.put("status", "savedDB");
							            HttpPostSQL usdbh = HttpPostSQL.getInstance(con);
							    		SQLiteDatabase db = usdbh.getWritable();
							    		if (db != null) {
							    			db.update("http_index", values, "indice="+String.valueOf(this.indice), null);
							    			//db.close();
							    		}
							    		//usdbh.close();
							            initNotify(this.indice, "error", con, localIntent);
									}
							        Intent toggleIntent = new Intent(con, ChangeConnectivity.class);
							        con.sendBroadcast(toggleIntent);
								}
								@Override
								protected void onProgressUpdate(Integer... progress)
								{
									HttpPostSQL usdbh = HttpPostSQL.getInstance(con);
						    		SQLiteDatabase dbw = usdbh.getWritable();
						    		SQLiteDatabase dbr = usdbh.getReadable();
									
						    		String[] args2 = new String[] {String.valueOf(indice)};
				        	        Cursor d = dbr.rawQuery("SELECT status FROM http_index WHERE indice=? ", args2);
				        	        if (d.moveToFirst()) {
										int prog = (int) (progress[0]);
										this.timer++;
										if ((this.timer % 30) == 0) {
											// Save barprogress UI
											notification.contentView.setTextViewText(R.id.status_porcentage, String.valueOf(prog)+"%");
											notification.contentView.setProgressBar(R.id.status_progress, 100, prog, false);
											notificationManager.notify(this.indice, notification);
											// Save database porcentage
								    		ContentValues values = new ContentValues();
								    		values.put("porcentage", prog);
								    		if (dbw != null) {
								    			dbr.update("http_index", values, "indice="+String.valueOf(this.indice), null);
								    			//db.close();
								    		}
								    		//usdbh.close();
										}
				        	        } else {
				        	        	this.cancel(true);
				        	        	this.httpPost.abort();
				        	        	notificationManager.cancel(indice);	
				        	        }
				        	        d.close();
								}								
							};   
					        if (UtilsHelper.isOnline(con)) {
/*<<<<<<< HttpPostHelper.java
					        	ContentValues values2 = new ContentValues();
					        	values2.put("status", "sending");
					        	db.update("http_index", values2, "indice="+String.valueOf(indice), null);
=======
>>>>>>> 1.4*/
								task.indice = Integer.valueOf(indice);
								task.execute(data);  					        	
					        } else {
					        	ContentValues valuesf = new ContentValues();
					        	valuesf.put("status", "savedDB");
					        	db.update("http_index", values, "indice="+String.valueOf(indice), null);
								WindowsHelper.showMessage(con, "No hay conexión a Internet, se enviará cuando la haya.");							        	
					        }					        	
      	        	  
        	          }
        	          d.close();
        	     //} while(c.moveToNext());
        		} while (false);
        	}
        	c.close();
        	//db.close();
        }	
		
        //usdbh.close();
		return null;
	}
	public static String send (final Activity act, final String data[], final Intent localIntent) {
		HttpPostSQL usdbh = HttpPostSQL.getInstance(act);
		SQLiteDatabase db = usdbh.getWritable();
		
        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
            /*db.execSQL("INSERT INTO httpIndex (indice) " +
                    "VALUES ()");*/
            
            ContentValues values = new ContentValues();
            values.put("status", "savingDB");
            long row_id = db.insert("http_index", null, values);

        	ContentValues valuesData = new ContentValues();
        	
        	String data2[] = null;
    		SharedPreferences localSharedPreferences = act.getSharedPreferences("user", 0);
    		if (localSharedPreferences != null) {
	    		String email = localSharedPreferences.getString("email", "");
	    		String password = localSharedPreferences.getString("password", "");
	    		int longitud = data.length + 4;
	    		data2 = new String[longitud];
	    		data2[0] = "email";
	    		data2[1] = email;
	    		data2[2] = "password";
	    		data2[3] = password;
	    		for(int i = 0; i < data.length; i++) {
	    			data2[i+4] = data[i];
	    		}
    		} else {
    			data2 = new String[data.length];
	    		for(int i = 0; i < data.length; i++) {
	    			data2[i] = data[i];
	    		}    			
    		}
        	
            for(int i=0; i < data2.length; i = i+2)
            {
            	valuesData.put("indice", String.valueOf(row_id));
            	valuesData.put("key", data2[i]);
            	valuesData.put("value", data2[i+1]);
            	db.insert("http_string", null, valuesData);
            }
            
            values = new ContentValues();
            values.put("status", "savedDB");
            db.update("http_index", values, "indice="+String.valueOf(row_id), null);
 
            //Cerramos la base de datos
            //db.close();
        }		
        
        //usdbh.close();
        
        Intent toggleIntent = new Intent(act, ChangeConnectivity.class);
        act.sendBroadcast(toggleIntent);

//        execNotify(act, localIntent);
        
		return null;
	}
}