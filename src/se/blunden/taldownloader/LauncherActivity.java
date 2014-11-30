package se.blunden.taldownloader;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LauncherActivity extends ActionBarActivity {
	private static final String TAG = "TALDownloader";
	
	private Button downloadButton;
	private EditText episodeInputField;
	private Downloader downloader;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        downloader = new Downloader(this);
        
        downloadButton = (Button) findViewById(R.id.download_episode);
        episodeInputField = (EditText) findViewById(R.id.input_episode);

        downloadButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                	String episode = episodeInputField.getText().toString().trim();
                	int status = downloader.download(episode);
                	
                	// Clear the input field
                	episodeInputField.setText("");
                	
                	checkStatus(status);
                	
                	Log.d(TAG, "input: " + episode);
                }
            });
	}
	
	private void checkStatus(int status) {
		switch(status) {
			case StatusCode.DIALOG_ERROR_CONNECTION_ID:
				Toast.makeText(this, getString(R.string.error_connection_message), Toast.LENGTH_LONG).show();
				break;
			
			case StatusCode.DIALOG_ERROR_EPISODE_ID:
				Toast.makeText(this, getString(R.string.error_episode_title), Toast.LENGTH_LONG).show();
				break;
			
			case StatusCode.DIALOG_ERROR_STORAGE_ID:
				Toast.makeText(this, getString(R.string.error_storage_message), Toast.LENGTH_LONG).show();
				break;
				
			case StatusCode.SUCCESS:
				// All is well, nothing to do
				break;
			
			default:
				// Assume all is well
				break;
		}
	}
}
