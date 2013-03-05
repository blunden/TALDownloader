package se.blunden.taldownloader;

import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class Downloader {
	private static final String TAG = "TALDownloader";
	private static final String talBaseUrl = "http://audio.thisamericanlife.org/jomamashouse/ismymamashouse/";
	
	Context context;
	
	public Downloader(Context context) {
    	this.context = context;
	}
	
	public boolean isConnected() {
    	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    	
    	return (activeNetwork != null && activeNetwork.isConnected());
    }
	
	public int download(String episode) {
		// Make sure we have an internet connection
    	if(!isConnected()) {
    		Log.e(TAG, "No internet connection detected!");
    		
    		return StatusCode.DIALOG_ERROR_CONNECTION_ID;
    	}
    	
    	// Make sure external storage is mounted and writable
    	if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		Log.e(TAG, "Storage not mounted or not writeable!");
    		
    		return StatusCode.DIALOG_ERROR_STORAGE_ID;
    	}
    	
    	// Build download URL
    	String talUrl = talBaseUrl + episode + ".mp3";
    	
    	DownloadManager.Request request = new DownloadManager.Request(Uri.parse(talUrl));
    	
    	// Download to the Podcast directory 
    	request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS , episode + ".mp3");
    	Log.d(TAG, "Download directory set to: " + Environment.DIRECTORY_PODCASTS);
    	
    	// Run Media Scanner when done to make it show up in music players
    	request.allowScanningByMediaScanner();
    	
    	// Notify user of download status
    	request.setTitle(context.getString(R.string.app_name));
    	request.setDescription(context.getString(R.string.download_description) + " " + episode + "...");
    	request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // API 11
    	
    	// Initiate the download
    	DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    	dm.enqueue(request);
    	
    	return StatusCode.SUCCESS;
	}
}
