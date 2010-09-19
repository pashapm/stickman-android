package org.hackday.stickman.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.util.Arrays;

import org.hackday.stickman.R;
import org.hackday.stickman.upload.SessionEvents.AuthListener;
import org.hackday.stickman.upload.SessionEvents.LogoutListener;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;

public class FacebookHelper
{
    public static final String APP_ID = "122541961129611";
    public static final String API_KEY = "cdf1eaeaea9edc4f1351345ceeac602c";
    public static final String APP_SECRET = "687c0301449a4eac3c96ec998e848ee2";

    private static final String[] PERMISSIONS =
        new String[] {"publish_stream", "read_stream", "offline_access", "video_upload" };

    private Facebook mFacebook;
    private AsyncFacebookRunner mAsyncRunner;
    private Context mContext;
    
    private SessionListener mSessionListener = new SessionListener();

    public FacebookHelper(Context context)
    {
        mContext = context;

        mFacebook = new Facebook();
        SessionStore.restore(mFacebook, mContext);

        mAsyncRunner = new AsyncFacebookRunner(mFacebook);

        //SessionEvents.addAuthListener(mSessionListener);
        //SessionEvents.addLogoutListener(mSessionListener);
    }
    
    public void upload(String path, String title, String description)
    {
        Log.i("Uploader", "Upload starting");
        // Initializing POST parameters
        Bundle params = new Bundle();
        params.putString("v", "1.0");
        params.putString("title", title);
        params.putString("method", "facebook.video.upload");
        params.putString("format", "json");
        params.putString("description", description);
        params.putString("call_id", String.valueOf(System.currentTimeMillis()));
        params.putString("api_key", API_KEY);
        params.putString("session_key", getSessionKey());
        params.putString("sig", createSignature(params, APP_SECRET));
        
        // Reading input file
        try
        {
            File videoFile = new File(path);
            byte[] data =new byte[(int) videoFile.length()];
            int len = data.length;

            InputStream is = new FileInputStream(videoFile);
            is.read(data);
            params.putByteArray(videoFile.getName(), data); 
        }
        catch (Exception ex)
        {
            Log.e("Uploader", "Cannot read file", ex);
        }
        
        // Sending POST request to Facebook
        
        try
        {
            String url = "http://api-video.facebook.com/restserver.php";
            String response = Util.openUrl(url, "POST", params);
            SessionEvents.onUploadComplete(response);
        } 
        catch (FileNotFoundException e)
        {
            SessionEvents.onFileNotFoundException(e);
        }
        catch (MalformedURLException e)
        {
            SessionEvents.onMalformedURLException(e);
        }
        catch (IOException e)
        {
            SessionEvents.onIOException(e);
        }
        
        Log.i("Uploader", "Uploading complete");
    }
    
    public boolean isLoggedIn()
    {
        return mFacebook.isSessionValid();
    }
    
    public void login()
    {
        mFacebook.authorize(mContext, APP_ID, PERMISSIONS, new LoginDialogListener());
    }
    
    public void logout()
    {
        try
        {
            mFacebook.logout(mContext);
        }
        catch (MalformedURLException e)
        {
            Toast.makeText(mContext, "Cannot log out: Invalid URL", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Toast.makeText(mContext, "Cannot log out: IOException", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    
    public void confirmUser()
    {
        DialogInterface.OnClickListener pl = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == Dialog.BUTTON_NEGATIVE)
                {
                    FacebookHelper.this.logout();
                }
                dialog.dismiss();
            }
        };
        
        Builder builder = new AlertDialog.Builder(mContext)
            .setTitle("You are")
            .setIcon(R.drawable.facebook_icon)
            .setMessage("Vasya")
            .setPositiveButton("Yes", pl)
            .setNegativeButton("No", pl);
        
        builder.show();
    }

    private String getSessionKey()
    {
        String accessToken = mFacebook.getAccessToken();
        Log.i("Uploader", String.format("Access token: %s", accessToken));
        String[] parts = accessToken.split("%7C");
        
        return parts[1];
    }
    
    private static String createSignature(Bundle params, String secret)
    {
        String info = "";
        
        Object[] keys = params.keySet().toArray();
        Arrays.sort(keys);
        
        for (Object k: keys)
        {
            String key = (String)k;
            info += String.format("%s=%s", key, params.getString(key));
        }
        info += secret;
        
        try
        {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(info.getBytes("UTF-8"));
            final byte[] bytes = digest.digest();
            final StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < bytes.length; i++)
            {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1)
                {
                    hex = "0" + hex;
                }
                buffer.append(hex);
            }
            return buffer.toString();
        }
        catch (Exception e)
        {
            return null;
        }

    }
    
    private final class LoginDialogListener implements DialogListener
    {
        public void onComplete(Bundle values)
        {
            Log.i("Uploader", "Login succeed");
            SessionEvents.onLoginSuccess();
        }

        public void onFacebookError(FacebookError error)
        {
            Log.e("Uploader", "Facebook error", error);
            SessionEvents.onLoginError(error.getMessage());
        }
        
        public void onError(DialogError error)
        {
            Log.e("Uploader", "Cannot login", error);
            SessionEvents.onLoginError(error.getMessage());
        }

        public void onCancel()
        {
            Log.w("Uploader", "Action Canceled");
            SessionEvents.onLoginError("Action Canceled");
        }
    }

    private class SessionListener implements AuthListener, LogoutListener
    {        
        public void onAuthSucceed()
        {
            SessionStore.save(mFacebook, mContext);
            Log.i("Uploader", "Session is saved");
        }

        public void onAuthFail(String error) {
        }
        
        public void onLogoutBegin() {           
        }
        
        public void onLogoutFinish()
        {
            SessionStore.clear(mContext);
            Log.i("Uploader", "Session is cleared");
        }
    }

}
