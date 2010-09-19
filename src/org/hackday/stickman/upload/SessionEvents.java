package org.hackday.stickman.upload;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;

import com.facebook.android.FacebookError;

public class SessionEvents
{
    private static LinkedList<AuthListener> mAuthListeners = new LinkedList<AuthListener>();
    private static LinkedList<LogoutListener> mLogoutListeners = new LinkedList<LogoutListener>();
    private static LinkedList<UploadListener> mUploadListeners = new LinkedList<UploadListener>();

    public static void addAuthListener(AuthListener listener)
    {
        mAuthListeners.add(listener);
    }

    public static void removeAuthListener(AuthListener listener)
    {
        mAuthListeners.remove(listener);
    }

    public static void addLogoutListener(LogoutListener listener)
    {
        mLogoutListeners.add(listener);
    }

    public static void removeLogoutListener(LogoutListener listener)
    {
        mLogoutListeners.remove(listener);
    }

    public static void addUploadListener(UploadListener listener)
    {
        mUploadListeners.add(listener);
    }
    
    public static void removeUploadListener(UploadListener listener)
    {
        mUploadListeners.remove(listener);
    }
    
    public static void onLoginSuccess()
    {
        for (AuthListener listener : mAuthListeners)
        {
            listener.onAuthSucceed();
        }
    }

    public static void onLoginError(String error)
    {
        for (AuthListener listener : mAuthListeners)
        {
            listener.onAuthFail(error);
        }
    }

    public static void onLogoutBegin()
    {
        for (LogoutListener l : mLogoutListeners)
        {
            l.onLogoutBegin();
        }
    }

    public static void onLogoutFinish()
    {
        for (LogoutListener l : mLogoutListeners)
        {
            l.onLogoutFinish();
        }
    }

    public static void onFacebookError(FacebookError e)
    {
        for (UploadListener l : mUploadListeners)
        {
            l.onFacebookError(e);
        }
    }
    
    public static void onFileNotFoundException(FileNotFoundException e)
    {
        for (UploadListener l : mUploadListeners)
        {
            l.onFileNotFoundException(e);
        }
    }
    
    public static void onIOException(IOException e)
    {
        for (UploadListener l : mUploadListeners)
        {
            l.onIOException(e);
        }
    }
    
    public static void onMalformedURLException(MalformedURLException e)
    {
        for (UploadListener l : mUploadListeners)
        {
            l.onMalformedURLException(e);
        }
    }
    
    public static void onUploadComplete(final String response)
    {
        for (UploadListener l : mUploadListeners)
        {
            l.onComplete(response);
        }
    }
    public static interface AuthListener
    {
        public void onAuthSucceed();

        public void onAuthFail(String error);
    }

    public static interface LogoutListener
    {
        public void onLogoutBegin();

        public void onLogoutFinish();
    }
    
    public static interface UploadListener
    {
        public void onFacebookError(FacebookError e);
        public void onFileNotFoundException(FileNotFoundException e);
        public void onIOException(IOException e);
        public void onMalformedURLException(MalformedURLException e);
        public void onComplete(final String response);
    }
}
