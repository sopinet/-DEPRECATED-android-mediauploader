/*
 * Copyright (C) 2010 AndroidWorks.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.sopinet.android.mediauploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class SelectImageTask extends AsyncTask<File, SelectImageTask.ProcessingState, Bitmap> {


	public enum ProcessingState {
		STARTING,
		PROCESSING_LARGE,
		FINISHED
	}
	
   
    public static final String TAG = "ProcessProfilePhotoTask";
	
	public SelectImageTask() {
		super();
	}

	@Override
	protected Bitmap doInBackground(File... files) {
		ProcessingState[] s = new ProcessingState[1];
		//BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		if (files.length != 1) {
			throw new IllegalArgumentException("We expect to process only one file");
		}
		try {
			s[0] = ProcessingState.PROCESSING_LARGE;
			publishProgress(s);
			
			Bitmap largePhoto = BitmapFactory.decodeStream(new FileInputStream(files[0]));
			int height = largePhoto.getHeight();
            int width = largePhoto.getWidth();
            int density = largePhoto.getDensity();
            Log.d(TAG,"large image processing "+ height+"x"+width+"den="+density+"type=");
            
            return largePhoto;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
