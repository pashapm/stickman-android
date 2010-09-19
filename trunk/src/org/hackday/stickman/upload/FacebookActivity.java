package org.hackday.stickman.upload;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.hackday.stickman.R;
import org.json.JSONException;
import org.json.JSONObject;


import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FacebookActivity extends Activity implements OnClickListener
{
    public static final String EXTRA_PATH = "PATH";

    public String mPath;
    
    public Button mUploadButton;
    public Button mCloseButton;
    
    public EditText mTitle;
    public EditText mDescription;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb_details);

        Bundle extras = getIntent().getExtras();
        mPath = extras.getString(EXTRA_PATH);
        
        mUploadButton = (Button)findViewById(R.id.fb_upload);
        mUploadButton.setOnClickListener(this);
        
        mCloseButton = (Button)findViewById(R.id.fb_close);
        mCloseButton.setOnClickListener(this);
        
        mTitle = (EditText)findViewById(R.id.fb_title);
        mDescription = (EditText)findViewById(R.id.fb_description);
    }

    public void onClick(View v)
    {
        if (v == mUploadButton)
        {
            final String title = mTitle.getText().toString();
            final String description = mDescription.getText().toString();
            
            final FacebookHelper facebook = new FacebookHelper(this);            
            facebook.logout();
            
            SessionEvents.addAuthListener(new SessionEvents.AuthListener()
            {   
                public void onAuthSucceed()
                {
                    facebook.upload(mPath, title, description);
                }
                
                public void onAuthFail(String error)
                {
                }
            });
            SessionEvents.addUploadListener(new SampleUploadListener());
            
            facebook.login();
        }
        else if (v == mCloseButton)
        {
            finish();
        }
    }
    
    public class SampleUploadListener implements SessionEvents.UploadListener
    {
        private void showMessage(String message)
        {
            Toast.makeText(FacebookActivity.this, message, Toast.LENGTH_SHORT);            
        }
        
        private void showAlert(String title, String message)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(FacebookActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener()
                {                    
                    public void onClick(DialogInterface dialog, int which)
                    {
                        FacebookActivity.this.finish();
                    }
                });
            builder.show();
        }
        
        public void onFacebookError(FacebookError e)
        {
            showMessage("Facebook error: " + e.getMessage());
            Log.e("Facebook", e.getMessage());
            e.printStackTrace();
        }

        public void onFileNotFoundException(FileNotFoundException e)
        {
            showMessage("File is not found");
            Log.e("Facebook", e.getMessage());
            e.printStackTrace();
        }

        public void onIOException(IOException e)
        {
            showMessage("IOException: " + e.getMessage());
            Log.e("Facebook", e.getMessage());
            e.printStackTrace();
        }

        public void onMalformedURLException(MalformedURLException e) 
        {
            showMessage(e.getMessage());
            Log.e("Facebook", e.getMessage());
            e.printStackTrace();
        }
        
        public void onComplete(final String response)
        {
            try 
            {
                // process the response here: (executed in background thread)
                Log.d("Uploader", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                showAlert("Upload", "Upload is complete");
            }
            catch (JSONException e) 
            {
                Log.w("Uploader", "JSON Error in response: " + e.getMessage());
                showAlert("Error", e.getMessage());
            }
            catch (FacebookError e) 
            {
                Log.w("Uploader", "Facebook Error: " + e.getMessage());
                showAlert("Error", e.getMessage());
            }
        }
    }
}
