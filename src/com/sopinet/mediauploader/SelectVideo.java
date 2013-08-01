package com.sopinet.mediauploader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

public class SelectVideo extends SelectMedia {

	public SelectVideo(Activity act) {
		super(act);
	}
	
	public SelectVideo(Activity act, ImageView photo) {
		super(act, photo);
	}

	public void launchSI() {
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("video/*");
			this.act.startActivityForResult(intent,this.PHOTO_PICKED);			
		} catch (ActivityNotFoundException e) {
            Toast.makeText(act, "Video no encontrado", Toast.LENGTH_LONG).show();
        }
	}
	
	public Boolean onactSI(int requestCode, int resultCode, Intent data) {
		if (requestCode == this.PHOTO_PICKED) {
			if (resultCode == Activity.RESULT_OK) {
				already_uri = data.getData();
			}
		}
		return true;
	}
	
	public void processPhotoUpdate(File tempFile) {		
		SelectImageTask task = new SelectImageTask(){
		    @Override
		    protected void onPreExecute() {
		        progressDialog = ProgressDialog.show(
		        		act,
		        		UtilsHelper.getAPP(act),
		                "Procesando el vídeo...",
		                true,
		                true,
		                new DialogInterface.OnCancelListener(){
		                    public void onCancel(DialogInterface dialog) {
		                    	WindowsHelper.showCancelMessage(act);
		                    	cancel(true);
		                        //finish();
		                    }
		                }
		        );     	
		    }
			@Override
			protected void onPostExecute(Bitmap result) {
				//android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(result.getWidth(),result.getHeight());
				//params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
				//photo.setLayoutParams(params);
				//if (photo != null) photo.setImageBitmap(result);
				//res_bitmap = result;
				//res_filename = TEMP_PHOTO_FILE;
				ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			    //res_bitmap.compress(Bitmap.CompressFormat.PNG, 85, localByteArrayOutputStream);
			    res_string = Base64.encodeToString(localByteArrayOutputStream.toByteArray(),0);				
				progressDialog.dismiss();
				//if (namefile != null) {
				//if (uploadfilename != null) {
				//	uploadSI(uploadfilename);
				//}
				//}
			}
			
		};
		task.execute(tempFile);
	}	
	
	public String getRealPath() {
		return super.getRealPath();
	}
}