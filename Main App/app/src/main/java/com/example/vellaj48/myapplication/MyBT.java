package com.example.vellaj48.myapplication;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by vellaj48 on 3/30/2015.
 */
public class MyBT extends AsyncTask<Context, Void, Long> {
    // Do the long-running work in here
    protected Long doInBackground(Context... urls) {
        return new Long(3);
    }

    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(Long result) {
        //showNotification("Downloaded " + result + " bytes");
    }
}