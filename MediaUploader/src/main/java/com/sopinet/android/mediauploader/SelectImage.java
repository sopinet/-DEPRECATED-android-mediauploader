package com.sopinet.android.mediauploader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class SelectImage extends SelectMedia {
	// Opciones
	protected int outputX = 600;
	protected int outputY = 600;
	protected int aspectX = 1;
	protected int aspectY = 1;
	protected boolean crop = true;
	protected boolean scale = true;
	protected boolean faceDetection = true;
	protected boolean circleCrop = true;	
	
	// Variables Temporales
	protected boolean return_data = false;
	private final static String TAG = "MediaStoreTest";
	private static final String TEMP_PHOTO_FILE = "tempPhoto.jpg";
	private String uploadfilename = null;
	private SelectImage si_pre = null; 
	
	// Resultados
	protected Bitmap res_bitmap = null;
	protected String res_filename = null;
	protected boolean cropped = false;
	
	// Constructor base
	public SelectImage(Activity act) {
		super(act);
	}	
	
	public SelectImage(Activity act, ImageView photo, int outputX, int outputY) {
		super(act, photo);
		this.outputX = outputX;
		this.outputY = outputY;
		if (outputX != outputY) {
			int scX = outputX / outputY;
			if (scX > 1) {
				this.aspectX = scX;
			} else {
				scX = outputY / outputX;
				this.aspectY = scX;
			}
		}
	}
	
	public SelectImage(Activity act, ImageView photo, int outputX, int outputY, Uri uri) {
		this(act, photo, outputX, outputY);
		this.already_uri = uri;
	}
	
	public SelectImage(Activity act, ImageView photo, boolean crop) {
		super(act, photo);
		this.crop = crop;
	}
	
	public SelectImage(Activity act, ImageView photo, int outputX, int outputY, boolean crop) {
		this(act, photo, outputX, outputY);
		this.crop = crop;
	}	
	
	public void launchSI() {
    	try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            if (this.crop) {
            	intent.putExtra("crop", "true");
            }
            intent.putExtra("aspectX", this.aspectX);
            intent.putExtra("aspectY", this.aspectY);
            intent.putExtra("outputX", this.outputX);	
            intent.putExtra("outputY", this.outputY);
            intent.putExtra("scale", this.scale);
            if (this.crop) {
            	intent.putExtra("return-data", false);
            	intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
            	intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            } else {
            	intent.putExtra("return-data", true);
            }
            intent.putExtra("noFaceDetection",!this.faceDetection); // lol, negative boolean noFaceDetection
            if (this.circleCrop) {
            	intent.putExtra("circleCrop", true);
            }
            
            this.act.startActivityForResult(intent, this.PHOTO_PICKED);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(act, "FOTO NO ENCONTRADA", Toast.LENGTH_LONG).show();
        }	        
	}
	
	public Boolean onactSI(int requestCode, int resultCode, Intent data, boolean crop) {
		if (requestCode == this.PHOTO_PICKED) {
			if (resultCode == Activity.RESULT_OK) {		
				if (!this.cropped && crop) {
					this.already_uri = data.getData();
					this.cropSI();
				} else {
					this.cropped = false;
					processSI(requestCode, resultCode, data, null);
				}
			}
		}
		return true;
	}
	
	public Boolean processSI(int requestCode, int resultCode, Intent data, String namefile) {
		if (requestCode == this.PHOTO_PICKED) {
			if (resultCode == Activity.RESULT_OK) {
				if (data == null) {
					Log.w(TAG, "Null data, but RESULT_OK, from image picker!");
	                Toast t = Toast.makeText(this.act, "FOTO NO PICKADA",
	                                         Toast.LENGTH_SHORT);
	                t.show();
	                return false;	
				}
				
				final Bundle extras = data.getExtras();
				if (extras != null) {
					File tempFile = getTempFile();
					if (Build.VERSION.SDK_INT > 13 || data.getAction() != null) {
						uploadfilename = namefile;
						processPhotoUpdate(tempFile);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public String cropSI() {
		/*this.onactSI(this.PHOTO_PICKED, Activity.RESULT_OK, this.already_uri, false);
		return null;*/
		this.cropped = true;
		try
		{
		    final List<CropOption> cropOptions = new ArrayList<CropOption>();

		    // this 2 lines are all you need to find the intent!!!
		    Intent intent = new Intent( "com.android.camera.action.CROP" );
		    intent.setType("image/*");;

		    List<ResolveInfo> list = this.act.getPackageManager().queryIntentActivities( intent, 0 );
		    if ( list.size() == 0 )
		    {
		        // I tend to put any kind of text to be presented to the user as a resource for easier translation (if it ever comes to that...)
		        Toast.makeText( this.act, "No puede recortar la imagen", Toast.LENGTH_LONG );
		        // this is the URI returned from the camera, it could be a file or a content URI, the crop app will take any
		        //_captureUri = null; // leave the picture there
		        //break; // leave this switch case...
		    }
	        intent.setType("image/*");
	        intent.setData(this.already_uri);
	        intent.putExtra("outputX", this.outputX);
	        intent.putExtra("outputY", this.outputY);
	        intent.putExtra("aspectX", this.aspectX);
	        intent.putExtra("aspectY", this.aspectY);
	        intent.putExtra("scale", true);
	        intent.putExtra("return-data", this.return_data);
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
	        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	        intent.putExtra("noFaceDetection",!this.faceDetection);
	        
	        for ( ResolveInfo res : list )
	        {
	            final CropOption co = new CropOption();
	            co.TITLE = this.act.getPackageManager().getApplicationLabel( res.activityInfo.applicationInfo );
	            co.ICON = this.act.getPackageManager().getApplicationIcon( res.activityInfo.applicationInfo );
	            co.CROP_APP = new Intent( intent );
	            co.CROP_APP.setComponent( new ComponentName( res.activityInfo.packageName, res.activityInfo.name ) );
	            cropOptions.add( co );
	        }
	        System.out.println("LLEGA 0");
	        this.act.startActivityForResult(cropOptions.get(0).CROP_APP, this.PHOTO_PICKED);
	        
	        // TODO: No es necesario elegir, set up the chooser dialog
	        /*
	        CropOptionAdapter adapter = new CropOptionAdapter( this.act, cropOptions );
	        AlertDialog.Builder builder = new AlertDialog.Builder( this.act );
	        builder.setTitle( "Elija una aplicación para recortar su imagen" );
	        final int CODE_TEMP = PHOTO_PICKED;
	        final Activity ACT_TEMP = this.act;
	        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
	            public void onClick( DialogInterface dialog, int item )
	            {
	                ACT_TEMP.startActivityForResult( cropOptions.get( item ).CROP_APP, CODE_TEMP );
	            }
	        } );
	        AlertDialog alert = builder.create();
	        alert.show();	   
	        */     
	        
	        /*builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
	            @Override
	            public void onCancel( DialogInterface dialog )
	            {
	                // we don't want to keep the capture around if we cancel the crop because we don't want it anymore
	                if ( _captureUri != null )
	                {
	                    getContentResolver().delete( _captureUri, null, null );
	                    _captureUri = null;
	                }
	            }
	        } );*/
	    }
	    catch ( Exception e )
	    {
	        Log.e( TAG, "processing capture", e );
	    }
        //Intent intent = new Intent("com.android.camera.action.CROP");
        //this.act.startActivityForResult(intent, this.PHOTO_PICKED);
        
        return null;
	}
	
	public boolean issetSI() {
		if (res_bitmap != null) return true;
		else return false;
	}
	
	public int getCode() {
		return this.PHOTO_PICKED;
	}
	
	public String uploadSI(String urlstring) {
		return uploadSI(urlstring, null, null, null);
	}
	
	private boolean isSDCARDMounted(){
        String status = Environment.getExternalStorageState();
       
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }
	
    private Uri getTempUri() {
		return Uri.fromFile(getTempFile());
	}
	
	private File getTempFile() {
		if (isSDCARDMounted()) {
			
			File f = new File(Environment.getExternalStorageDirectory(),TEMP_PHOTO_FILE);
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// Toast.makeText(this.act, "Problema IOFILE", Toast.LENGTH_LONG).show();
			}
			return f;
		} else {
			return null;
		}
	}
	
	private boolean removeTempFile() {
		File file = getTempFile();
		boolean deleted = file.delete();
		return deleted;
	}
	
	public void processPhotoUpdate(File tempFile) {		
		SelectImageTask task = new SelectImageTask(){
		    @Override
		    protected void onPreExecute() {
		        progressDialog = ProgressDialog.show(
		        		act,
		        		UtilsHelper.getAPP(act),
		                "Procesando la imagen...",
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
				if (photo != null) photo.setImageBitmap(result);
				res_bitmap = result;
				res_filename = TEMP_PHOTO_FILE;
				ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			    res_bitmap.compress(Bitmap.CompressFormat.PNG, 85, localByteArrayOutputStream);
			    res_string = Base64.encodeToString(localByteArrayOutputStream.toByteArray(),0);				
				progressDialog.dismiss();
				//if (namefile != null) {
				if (uploadfilename != null) {
					uploadSI(uploadfilename);
				}
				//}
			}
			
		};
		task.execute(tempFile);
	}	
}