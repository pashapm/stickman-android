package org.hackday.stickman.upload;

import java.io.File;

import org.hackday.stickman.R;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class UploadActivity extends Activity implements OnClickListener
{
    private static final String LOG_TAG = "Uploader";

    private VideoView mVideoView;
    
    private Button mEmailButton;
    private Button mFacebookButton;
    private Button mYoutubeButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mEmailButton = (Button)findViewById(R.id.email);
        mEmailButton.setOnClickListener(this);
        
        mFacebookButton = (Button)findViewById(R.id.facebook);
        mFacebookButton.setOnClickListener(this);

        mYoutubeButton = (Button)findViewById(R.id.youtube);
        mYoutubeButton.setOnClickListener(this);
        
        mVideoView = (VideoView) findViewById(R.id.video_play);
        mVideoView.setVideoPath("/sdcard/stickman/video.mp4");
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.requestFocus();  
        mVideoView.start();
     }
    
    public void onClick(View v)
    {
        int id = v.getId();
        final String path = "/sdcard/stickman/video.mp4";
        
        if (id == R.id.email)
        {
            sendEmail(path);
        }
        else if (id == R.id.facebook)
        {
            postFacebook(path);            
        }
        else if (id == R.id.youtube)
        {
            postYouTube(path);
        }        
    }
 
    private void sendEmail(String path)
    {
        Uri uri = Uri.fromFile(new File(path));
        Log.i(LOG_TAG, uri.toString());
        
        Intent postIntent = new Intent(Intent.ACTION_SEND);
        postIntent.setType("video/mp4"); 
        
        postIntent.putExtra(Intent.EXTRA_SUBJECT, "Video" );
        postIntent.putExtra(Intent.EXTRA_STREAM, uri); 
        postIntent.putExtra(Intent.EXTRA_TEXT, "Sample text" );
        postIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try
        {
            startActivity(Intent.createChooser(postIntent, "Post your video"));
            //startActivity(email);
        }
        catch (ActivityNotFoundException ex)
        {
            Toast.makeText(this, "There are no email clients installed", Toast.LENGTH_SHORT).show();
        }
}

    private void postYouTube(String path)
    {
        Intent i = new Intent();
        i.setClass(this, YoutubeActivity.class);
        i.putExtra(YoutubeActivity.EXTRA_PATH, path);        
        startActivity(i);
    }
    
    private void postFacebook(String path)
    {
        Intent i = new Intent();
        i.setClass(this, FacebookActivity.class);
        i.putExtra(FacebookActivity.EXTRA_PATH, path);        
        startActivity(i);
    }
}