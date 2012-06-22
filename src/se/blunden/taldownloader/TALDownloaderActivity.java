package se.blunden.taldownloader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.os.Bundle;
import android.os.Environment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.NetworkInfo;
import android.util.Log;
import java.util.regex.*;

/**
 * This Activity receives Share URL intents from the
 * episode pages of thisamericanlife.org and downloads
 * the mp3 of the episode in question by parsing the URL
 * and generating the direct link to the mp3 file on their
 * server.
 * 
 * TODO: Register for intent to handle clicking on the notification.
 * 
 * @author blunden
 *
 */

public class TALDownloaderActivity extends Activity {
    
	static final int DIALOG_ERROR_EPISODE_ID = 0;
	static final int DIALOG_ERROR_CONNECTION_ID = 1;
	static final int DIALOG_ERROR_DOMAIN_ID = 2;
	
	@SuppressWarnings("deprecation") // I don't feel like learning to use the new DialogFragment right now
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Intent intent = getIntent();
    	
        if (intent.getAction().equals(Intent.ACTION_SEND)) {
        	Log.d("TALDownloader","Intent received");
        	
        	String receivedUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
        	
        	String talBaseUrl = "http://audio.thisamericanlife.org/jomamashouse/ismymamashouse/";
        	
        	if(!isThisAmericanLife(receivedUrl)) {
        		Log.e("TALDownloader","Invalid domain!");
        		
        		showDialog(DIALOG_ERROR_DOMAIN_ID);
        		return;
        	}
        	
        	String episode = getEpisode(receivedUrl);
        	if(episode.compareTo("") == 0) {
        		Log.e("TALDownloader","No episode number found in URL: " + receivedUrl);
        		
        		showDialog(DIALOG_ERROR_EPISODE_ID);
        		return;
        	}
        	
        	// Make sure we have an internet connection
        	if(!isConnected()) {
        		Log.e("TALDownloader","No internet connection detected!");
        		
        		showDialog(DIALOG_ERROR_CONNECTION_ID);
        		return;
        	}
        	
        	// Build download URL
        	String talUrl = talBaseUrl + episode + ".mp3";
        	
        	DownloadManager.Request request = new DownloadManager.Request(Uri.parse(talUrl));
        	
        	// Download to the Podcast directory 
        	request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS , episode + ".mp3");
        	//Log.d("TAG","Environment.DIRECTORY_PODCASTS: " + Environment.DIRECTORY_PODCASTS);
        	
        	// Run Media Scanner when done to make it show up in music players
        	request.allowScanningByMediaScanner();
        	
        	// Notify user of download status
        	request.setTitle("TAL Downloader");
        	request.setDescription("Downloading This American Life " + episode + "...");
        	request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        	
        	// Initiate the download
        	DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        	dm.enqueue(request);

        }        
        finish();
    }
    
    public String getEpisode(String url) {
    	Log.d("TALDownloader","getEpisode(" + url + ") called");
    	
    	// Include "/" in the regexp to be slightly more resistant to URL format changes and act numbers
    	Pattern pattern = Pattern.compile("/[0-9]+/");
    	Matcher m = pattern.matcher(url); 
    	
    	if(m.find()) {
    		String match = m.group();
    		Log.d("TALDownloader","match = " + match);
    		return match.substring(1, match.length() - 1); // remove slashes
    	} else {
    		// No match
    		// TODO: Find suitable exception to throw instead
    		return "";
    	}
    }
    
    public boolean isThisAmericanLife(String url) {
    	// TODO: Check if this can be handled by a more specific intent-filter instead
    	Pattern pattern = Pattern.compile("http://[a-zA-Z0-9\\.\\-]+thisamericanlife.org[a-zA-Z0-9\\./\\-]+");
    	Matcher m = pattern.matcher(url); 
    	
    	return m.matches();
    }
    
    public boolean isConnected() {
    	ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    	
    	return (activeNetwork != null && activeNetwork.isConnected());
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog;
        AlertDialog.Builder builder;
        switch(id) {
        case DIALOG_ERROR_EPISODE_ID:
        	builder = new AlertDialog.Builder(this);
    		builder.setTitle("No episode number found")
    			   .setMessage("No episode number found in URL.")
    			   .setIconAttribute(android.R.attr.alertDialogIcon)
    			   .setCancelable(false)
    			   .setNeutralButton("OK", new DialogInterface.OnClickListener() {
    				   public void onClick(DialogInterface dialog, int id) {
    		                finish();
    		           }
    			   });
    		dialog = builder.create();
            break;
        case DIALOG_ERROR_CONNECTION_ID:
        	builder = new AlertDialog.Builder(this);
    		builder.setTitle("Unable to download file")
    			   .setMessage("No network connectivity detected.")
    			   .setIconAttribute(android.R.attr.alertDialogIcon)
    			   .setCancelable(false)
    			   .setNeutralButton("OK", new DialogInterface.OnClickListener() {
    				   public void onClick(DialogInterface dialog, int id) {
    		                finish();
    		           }
    			   });
    		dialog = builder.create();
            break;
        case DIALOG_ERROR_DOMAIN_ID:
        	builder = new AlertDialog.Builder(this);
    		builder.setTitle("Invalid domain")
    			   .setMessage("Domain is not thisamericanlife.org.")
    			   .setIconAttribute(android.R.attr.alertDialogIcon)
    			   .setCancelable(false)
    			   .setNeutralButton("OK", new DialogInterface.OnClickListener() {
    				   public void onClick(DialogInterface dialog, int id) {
    		                finish();
    		           }
    			   });
    		dialog = builder.create();
        	break;
        default:
            dialog = null;
        }
        return dialog;
    }
}