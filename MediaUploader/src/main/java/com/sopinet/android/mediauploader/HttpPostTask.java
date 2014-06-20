package com.sopinet.android.mediauploader;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class HttpPostTask extends AsyncTask<String, Integer, String>
	{
		HttpPost httpPost = null;
		ProgressDialog pd;
		long totalSize;
		int indice;
		int timer = 0;
		String item;
 
		@Override
		protected String doInBackground(String... arg0)
		{
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext httpContext = new BasicHttpContext();
			String url = MediaUploader.URL;
			httpPost = new HttpPost(url);

			try
			{
				HttpPostMime multipartContent = new HttpPostMime(new HttpPostMime.ProgressListener()
				{
					public void transferred(long num)
					{
						publishProgress((int) ((num / (float) totalSize) * 100));
					}
				});
				//multipartContent.isChunked();
				// We use FileBody to transfer an image
				//multipartContent.addPart("image", new StringBody(arg0[1]));
				//multipartContent.addPart("email", new StringBody(arg0[2]));
				//multipartContent.addPart("password", new StringBody(arg0[3]));
				if (arg0.length > 0) {
					for (int i = 0; i < arg0.length; i = i+2) {
						if (arg0[i] != null && arg0[i+1] != null) {
							if (arg0[i].length() > 4 && arg0[i].substring(0,5).equals("file_")) {
								File file = new File(arg0[i+1]);
								// ContentBody cbFile = new FileBody(file, "video/mp4");
								ContentBody cbFile = new FileBody(file);
								multipartContent.addPart(arg0[i].substring(5), cbFile);								
							} else {
								multipartContent.addPart(arg0[i], new StringBody(arg0[i+1]));
							}
						}
					}
				}
				totalSize = multipartContent.getContentLength();
				
				// Send it
				httpPost.setEntity(multipartContent);
				HttpResponse response = httpClient.execute(httpPost, httpContext);
				String serverResponse = EntityUtils.toString(response.getEntity());
				return serverResponse;
			}
 
			catch (Exception e)
			{
				System.out.println(e);
			}
			return null;
		}
 
		@Override
		protected void onProgressUpdate(Integer... progress)
		{
			timer++;
			pd.setProgress((int) (progress[0]));
		}
	}