package se.blunden.taldownloader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LauncherActivity extends Activity {
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
                	// TODO: Display error to the user if invalid
                	// ie. check the return value and create alert dialog
                	String episode = episodeInputField.getText().toString();
                	downloader.download(episode);
                	
                	Log.d(TAG, "input: " + episode);
                }
            });
        
        //TODO: Check download status
	}
}
