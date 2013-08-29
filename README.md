android-mediauploader
=====================

Easy library utility for upload any content using multipart process.

---

It is Alpha State, no documentation, but you should can use any from it.
![Capture from Library use](https://raw.github.com/sopinet/android-mediauploader/master/screen.png)

DOCUMENTATION
=============

(ALPHA STATE; IT CAN CHANGE)

You can integrate this library with https://github.com/coomar2841/image-chooser-library

Here sample documentation

1.Add android-mediauploader library to your project
2.Add image-chooser-library to your project (optional)
3.Add to manifiest:

		<uses-permission android:name="android.permission.INTERNET" />
		<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
		<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
		<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
		
		<activity android:name="com.x.y.SendingActivity"></activity>
		
  <receiver android:name="com.sopinet.mediauploader.ChangeConnectivity">
      <intent-filter>
          <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
      </intent-filter>
  </receiver>
  
4.Create anything like sending_activity.xml

    <LinearLayout...>
      <com.sopinet.mediauploader.SendingView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        >    
    </LinearLayout>
  
5.Create anything like SendingActivity.java, and only show sending_activity.xml here

In your main activity (or other), configure MediaUploader:
```java
    public void onCreate(Bundle savedInstanceState) {
    // Configuration
      MediaUploader.URL = "http://yourserver/upload.php";
      MediaUploader.MODE = "wifi";
      MediaUploader.SENDINGCONTEXT = "com.x.y";
      MediaUploader.SENDINGCLASS = "com.x.y.SendingActivity";
      ...
    }
```
  
6.You can send now anything so:

```java
	     String data[] = new String[2];
	     data[0] = "title";
	     data[1] = "Hello world";
	     HttpPostHelper.send(MainActivity.this, data);
```

7.If you are working with image-chooser-library, you can do anything like it:

```java
    @Override
    public void onVideoChosen(final ChosenVideo video) {
       runOnUiThread(new Runnable() {
       @Override
       public void run() {
          if (video != null) {
             String data[] = new String[2];
	            data[0] = "file_video";
	            data[1] = video.getVideoFilePath();
	            HttpPostHelper.send(MainActivity.this, data);
	         }
	      }
	   }
```

8.Dont you forget create upload.php file:
```php
 <?php
   $file = $_FILES['file_video'];
   move_uploaded_file($file['tmp_name'], "/var....?");
 ?>
```

TODO
====
  * Finish version 0.1
  * Add sample code
