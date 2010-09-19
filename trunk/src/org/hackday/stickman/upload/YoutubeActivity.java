package org.hackday.stickman.upload;

import java.io.File;

import org.hackday.stickman.R;
import org.hackday.stickman.upload.youtube.model.Entry;
import org.hackday.stickman.upload.youtube.model.Incomplete;
import org.hackday.stickman.upload.youtube.model.MediaCategory;
import org.hackday.stickman.upload.youtube.model.MediaGroup;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin.Response;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.MultipartRelatedContent;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.client.xml.atom.AtomContent;


public class YoutubeActivity extends Activity implements OnClickListener
{    
    public Button mUploadButton;
    public Button mCloseButton;
    
    public EditText mTitle;
    public EditText mDescription;
    public EditText mUsername;
    public EditText mPassword;
    
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) 
        {
            mProgress.dismiss();
        }
    };
    
    private ProgressDialog mProgress;

    private String devKey = "AI39si6dsEjkbv-Vdzs3-2MGHGCsoFjkfmfZbcq6eqP6qYZQveoxTDVjhfauzol_kS9lN5M_V25OSosF2745Fvb0ueHKRMsZMQ";
    private String appName = "fireman-youtube-upload";
    
    private String mPath;
    
    public static final String EXTRA_PATH = "PATH";

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yt_details);
        
        mUploadButton = (Button)findViewById(R.id.yt_upload);
        mCloseButton = (Button)findViewById(R.id.yt_close);
        
        mTitle = (EditText)findViewById(R.id.yt_title);
        mDescription = (EditText)findViewById(R.id.yt_description);
        mUsername = (EditText)findViewById(R.id.yt_username);
        mPassword = (EditText)findViewById(R.id.yt_password);
        
        mUploadButton.setOnClickListener(this);
        mCloseButton.setOnClickListener(this);
        
        Bundle extras = getIntent().getExtras();
        mPath = extras.getString(EXTRA_PATH);
    }

    private String getUsername()
    {
        return mUsername.getText().toString();
    }

    private String getPassword()
    {
        return mPassword.getText().toString();
    }

    private String getVideoTitle()
    {
        return mTitle.getText().toString();
    }

    public void onClick(View v)
    {
        if (v == mUploadButton)
        {
            mProgress = ProgressDialog.show(this, "Uploader", "Uploading to YouTube", true, false);
            
            new Thread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        HttpTransport transport = setUpTransport();
                        ClientLogin login = new ClientLogin();
                        login.applicationName = appName;
                        login.authTokenType = "youtube";
                        login.username = getUsername();
                        login.password = getPassword();
                        Response response = login.authenticate();
                        response.setAuthorizationHeader(transport);

                        showState("Autorization passed");

                        Entry entry = new Entry();
                        entry.group = new MediaGroup();
                        entry.group.title = getVideoTitle();
                        entry.group.description = mDescription.getText().toString();
                        entry.group.incomplete = new Incomplete();
                        entry.group.category = new MediaCategory();
                        entry.group.category.Cat = "People\n";

                        XmlNamespaceDictionary namespaceDictionary = new XmlNamespaceDictionary();
                        namespaceDictionary.addNamespace("",
                                "http://www.w3.org/2005/Atom");
                        namespaceDictionary.addNamespace("media",
                                "http://search.yahoo.com/mrss/");
                        namespaceDictionary.addNamespace("gd",
                                "http://schemas.google.com/g/2005");
                        namespaceDictionary.addNamespace("yt",
                                "http://gdata.youtube.com/schemas/2007");

                        AtomContent aContent = new AtomContent();
                        aContent.entry = entry;
                        aContent.namespaceDictionary = namespaceDictionary;

                        InputStreamContent bContent = new InputStreamContent();
                        bContent.setFileInput(new File(mPath));
                        bContent.type = "video/mp4";

                        MultipartRelatedContent multiContent = new MultipartRelatedContent();
                        multiContent.parts.add(aContent);
                        multiContent.parts.add(bContent);

                        HttpRequest request = transport.buildPostRequest();
                        request.setUrl("http://uploads.gdata.youtube.com/feeds/api/users/default/uploads");
                        GoogleHeaders headers = (GoogleHeaders) request.headers;
                        headers.setSlugFromFileName(mPath);
                        request.content = multiContent;
                        request.execute();
                        showResult("Success", "Video uploaded");
                    }
                    catch (Exception e)
                    {
                        showResult("Error", e.getMessage());
                    }
                    finally
                    {
                        handler.sendEmptyMessage(0);
                    }
                }

                private void showState(final String message)
                {
                    handler.post(new Runnable()
                    {
                        public void run()
                        {
                            Toast.makeText(YoutubeActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                
                private void showResult(final String title, final String message)
                {
                    handler.post(new Runnable()
                    {
                        public void run()
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(YoutubeActivity.this)
                                .setTitle(title)
                                .setMessage(message)
                                .setNegativeButton("Ok", new DialogInterface.OnClickListener()
                                {                    
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        YoutubeActivity.this.finish();
                                    }
                                });
                            builder.show();
                        }
                    });
                }

            }).start();

        }
        else if (v == mCloseButton)
        {
            finish();
        }

    }

    private HttpTransport setUpTransport()
    {
        HttpTransport transport = GoogleTransport.create();
        GoogleHeaders headers = (GoogleHeaders) transport.defaultHeaders;
        headers.setApplicationName(appName);
        headers.setDeveloperId(devKey);

        headers.gdataVersion = "2";
        transport.addParser(new JsonCParser());
        return transport;
    }
}
