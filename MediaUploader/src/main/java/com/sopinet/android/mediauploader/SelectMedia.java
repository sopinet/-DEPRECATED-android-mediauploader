package com.sopinet.android.mediauploader;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

public class SelectMedia {
	// Opciones
	protected ImageView photo;
	
	// Variables Temporales
	protected int PHOTO_PICKED = 0;
	protected Activity act = null;
	protected ProgressDialog progressDialog;
	public Uri already_uri = null;
	
	// Resultados
	protected long res_date = 0;
	protected String res_string = null;	
	protected String res_upload = null;	
	
	// Constructor base
	protected SelectMedia(Activity act) {
		this.act = act;
	}	
	
	protected SelectMedia(Activity act, ImageView photo) {
		this (act);
		this.photo = photo;
		this.PHOTO_PICKED = this.hashCode();
		if (this.PHOTO_PICKED < 0) this.PHOTO_PICKED = this.PHOTO_PICKED * -1;
	}
	
	public long getDateSI() {
		if (this.res_date == 0) {
	        // Obtenemos fecha de última modificación
	        String filename = UtilsHelper.getRealPathFromURI(this.act,this.already_uri);
	        System.out.println("FILE: "+filename);
	        File file = new File(filename);
	        this.res_date = file.lastModified();
		}
        return this.res_date;		
	}	
	
	public String getString() {
		return this.res_string;
	}
	
	public String uploadSI(String urlstring, final String message, final Intent localIntent, final String data[]) {
		HttpPostTask task = new HttpPostTask(){
		    @Override
		    protected void onPreExecute() {
		    	pd = new ProgressDialog(act);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.setMessage("Enviando información...");
				pd.setCancelable(false);
				pd.show();   	
		    }
			@Override
			protected void onPostExecute(String result) {
				pd.dismiss();
				res_upload = result;
				if (message != null) {
					WindowsHelper.showMessage(act, message);
					act.startActivity(localIntent);
				}
			}			
		};
		SharedPreferences localSharedPreferences = this.act.getSharedPreferences("user", 0);
		String email = localSharedPreferences.getString("email", "");
		String password = localSharedPreferences.getString("password", "");
		System.out.println("ACTION: "+urlstring);
		if (data == null) {
			task.execute(urlstring, this.res_string, email, password);
		} else {
			int longitud = data.length + 4;
			String data2[] = new String[longitud];
			data2[0] = urlstring;
			data2[1] = this.res_string;
			data2[2] = email;
			data2[3] = password;
			for(int i = 0; i < data.length; i++) {
				data2[i+4] = data[i];
			}
			task.execute(data2);
		}
		return null;
	}	
	
	protected String getRealPath() {
		if (already_uri == null) return null;
	    String[] proj = { MediaStore.Images.Media.DATA };
	    CursorLoader loader = new CursorLoader(this.act, already_uri, proj, null, null, null);
	    Cursor cursor = loader.loadInBackground();
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
}