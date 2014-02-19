package com.sopinet.mediauploader;

import com.sopinet.mediauploader.ChangeConnectivity;
import com.sopinet.utils.CheckParser;

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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

public class HttpPostHelper {
	//public static Notification notification = null;
	public static NotificationManager notificationManager = null;
	public static NotificationCompat.Builder mBuilder = null;
	
	private static void initNotify(int indice, String item, String state, Context act, Intent localIntent) {
		initNotify(indice, item, state, act, localIntent, "");
	}
	
	private static void initNotify(int indice, String item, String state, Context act, Intent localIntent, String adderror) {
		mBuilder = new NotificationCompat.Builder(act);
		/*
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("Default text")
		        .setContentText("Default text content: " + item);
		        */		
		
		/*
    	notification = new Notification(R.drawable.upload, "Espere...", System
                .currentTimeMillis());
        
    	notification.contentView = new RemoteViews(act.getApplicationContext().getPackageName(), R.layout.download_progress);
		PendingIntent contentIntent = PendingIntent.getActivity(act, 0, localIntent, 0);
        notification.contentIntent = contentIntent;
        */
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(act);
		Class<?> cls = null;
		try {
			cls = Class.forName(MediaUploader.SENDINGCLASS);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stackBuilder.addParentStack(cls);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(localIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);		
		
		
		String texto = null;
		if (state.equals("sending")) {
			texto = act.getResources().getString(MediaUploader.TEXT_SENDING);
			mBuilder.setSmallIcon(MediaUploader.ICON_BLUE);
			mBuilder.setNumber(0);
			mBuilder.setProgress(100, 0, false);
			mBuilder.setContentText(item);
			/*
			notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
	        notification.contentView.setImageViewResource(R.id.status_icon, android.R.drawable.presence_away);
	        notification.contentView.setTextViewText(R.id.status_porcentage, "0%");
	        notification.contentView.setProgressBar(R.id.status_progress, 100, 0, false);
	        */	        
		} else if (state.equals("nonetwork")) {
			texto = UtilsHelper.isOnlineTEXT(act);
			mBuilder.setSmallIcon(MediaUploader.ICON_YELLOW);
			mBuilder.setNumber(100);
			mBuilder.setProgress(100, 0, false);
			mBuilder.setContentText(item);
			/*
			notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
	        notification.contentView.setImageViewResource(R.id.status_icon, android.R.drawable.presence_offline);
	        notification.contentView.setTextViewText(R.id.status_porcentage, "0%");
	        notification.contentView.setProgressBar(R.id.status_progress, 100, 0, false);
	        */	        
		} else if (state.equals("ok")) {
			texto = act.getResources().getString(MediaUploader.TEXT_SENDOK);
			mBuilder.setSmallIcon(MediaUploader.ICON_GREEN);
			mBuilder.setNumber(100);
			mBuilder.setProgress(100, 100, false);
			mBuilder.setContentText(item);
			/*
			notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
	        notification.contentView.setImageViewResource(R.id.status_icon, android.R.drawable.presence_online);
	        notification.contentView.setTextViewText(R.id.status_porcentage, "100%");
	        notification.contentView.setProgressBar(R.id.status_progress, 100, 100, false);
	        */	        
		} else if (state.equals("error")) {
			texto = UtilsHelper.isOnlineTEXT(act);
			mBuilder.setSmallIcon(MediaUploader.ICON_RED);
			mBuilder.setNumber(100);
			mBuilder.setProgress(100, 0, false);
			mBuilder.setContentText(item);
			/*
			notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
	        notification.contentView.setImageViewResource(R.id.status_icon, android.R.drawable.presence_busy);
	        notification.contentView.setTextViewText(R.id.status_porcentage, "0%");
	        notification.contentView.setProgressBar(R.id.status_progress, 100, 0, false);
	        */			
		}
		String texto_error = act.getResources().getString(MediaUploader.TEXT_SENDERROR);
		if (adderror != "") {
			mBuilder.setContentTitle(texto_error);
			//notification.contentView.setTextViewText(R.id.status_text, texto_error);
		} else {
			mBuilder.setContentTitle(texto);
			//notification.contentView.setTextViewText(R.id.status_text, texto);
		}
		/*
        notificationManager = (NotificationManager) act.getApplicationContext().getSystemService(
                act.getApplicationContext().NOTIFICATION_SERVICE);
        notificationManager.notify(indice, notification);
        */
		notificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(indice, mBuilder.build());
		
        if (state.equals("error")) {
        	if (adderror != "" && MediaUploader.RES_VIEWERROR == true) {
        		WindowsHelper.showMessage(act, adderror);
        	} else {
        		WindowsHelper.showMessage(act, texto_error);
        	}
        } else {
        	WindowsHelper.showMessage(act, texto);	
        }
	}
	
	public static String cancelNotify(final Context con, final Intent localIntent) {
		HttpPostSQL usdbh = HttpPostSQL.getInstance(con);
		SQLiteDatabase db = usdbh.getWritable();
        if(db != null)
        {
        	String[] args = new String[] {"savedDB"};
        	Cursor c = db.rawQuery(" SELECT indice, item FROM http_index WHERE status=? ", args);
        	if (c.moveToFirst()) {
        		WindowsHelper.showMessage(con, UtilsHelper.isOnlineTEXT(con));
        	}
        	c.close();
        	args = new String[] {"sending"};		
        	c = db.rawQuery(" SELECT indice, item FROM http_index WHERE status=? ", args);
        	//Nos aseguramos de que existe al menos un registro
        	if (c.moveToFirst()) {
				//Recorremos el cursor hasta que no haya más registros
				do {
				    String indice = c.getString(0);
				    String item = c.getString(1);
				    initNotify(Integer.parseInt(indice), item, "nonetwork", con, localIntent);
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
        	Cursor c = db.rawQuery(" SELECT indice, item FROM http_index WHERE status=? ", args);
        	//Nos aseguramos de que existe al menos un registro
        	if (c.moveToFirst()) {        		
        	     //Recorremos el cursor hasta que no haya más registros
        	     do {
        	          String indice = c.getString(0);
        	          String item = c.getString(1);
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
							    	initNotify(this.indice, this.item, "sending", con, localIntent);
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
							    		// Resultado devuelto del POST por JSON
							    		if (MediaUploader.RES_OK.equals("json")) {
							    			CheckParser check = new CheckParser();
							    			if (result != null && check.parse(result)) {
							    				initNotify(this.indice, this.item, "ok", con, localIntent);
							    			} else {
							    				initNotify(this.indice, this.item, "error", con, localIntent, result);
							    			}
							    		// Resultado devuelto del POST por STRING
							    		} else {
								    		if (MediaUploader.RES_OK == null || result.equals(MediaUploader.RES_OK)) {
								    			initNotify(this.indice, this.item, "ok", con, localIntent);
								    		} else {
								    			initNotify(this.indice, this.item, "error", con, localIntent, result);
								    		}
							    		}
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
							            initNotify(this.indice, this.item, "error", con, localIntent);
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
										if ((this.timer % 5) == 0) {
											// Save barprogress UI
											mBuilder.setContentText(this.item);
											mBuilder.setProgress(100, prog, false);
											mBuilder.setNumber(prog);
											//notification.contentView.setTextViewText(R.id.status_porcentage, String.valueOf(prog)+"%");
											//notification.contentView.setProgressBar(R.id.status_progress, 100, prog, false);
											notificationManager.notify(this.indice, mBuilder.build());
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
				        	        // Si hemos cambiado el tipo de conexión, se cancela esta subida
				        	        if (UtilsHelper.isOnline3G(con) && MediaUploader.MODE.equals("wifi")) {
							        	ContentValues values = new ContentValues();
							        	values.put("status", "savedDB");
							        	dbw.update("http_index", values, "indice="+String.valueOf(indice), null);
				        	        	this.cancel(true);
				        	        	this.httpPost.abort();
				        	        	notificationManager.cancel(indice);	
				        	        }				        	        
				        	        d.close();
								}								
							};   
					        if (UtilsHelper.isOnline(con)) {
					        	// Asignamos al TASK el indice y el item
								task.indice = Integer.valueOf(indice);
								task.item = item;
								task.execute(data);  					        	
					        } else {
					        	ContentValues valuesf = new ContentValues();
					        	valuesf.put("status", "savedDB");
					        	db.update("http_index", valuesf, "indice="+String.valueOf(indice), null);
								WindowsHelper.showMessage(con, UtilsHelper.isOnlineTEXT(con));			        	
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
	
	public static String send(final Context con, final String data[]) {
		return send(con, data, "Nombre de su item");
	}
	
	public static String send (final Context con, final String data[], String item) {
		HttpPostSQL usdbh = HttpPostSQL.getInstance(con);
		SQLiteDatabase db = usdbh.getWritable();
		
        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {   
            ContentValues values = new ContentValues();
            values.put("status", "savingDB");
            long row_id = db.insert("http_index", null, values);

        	ContentValues valuesData = new ContentValues();
        	
            for(int i=0; i < data.length; i = i+2)
            {
            	Log.d("TEMA", "KEY: " + data[i]);
            	Log.d("TEMA", "VALUE: " + data[i+1]);            	
            	valuesData.put("indice", String.valueOf(row_id));
            	valuesData.put("key", data[i]);
            	valuesData.put("value", data[i+1]);
            	db.insert("http_string", null, valuesData);
            }
            
            values = new ContentValues();
            values.put("item", item);
            values.put("status", "savedDB");
            db.update("http_index", values, "indice="+String.valueOf(row_id), null);
 
            //Cerramos la base de datos
            //db.close();
        }		
        
        Intent toggleIntent = new Intent(con, ChangeConnectivity.class);
        con.sendBroadcast(toggleIntent);
        
		return null;
	}
}