// ---------------------------------------------------------------------------
// Copyright (c) 2011 Asim Ihsan (asim dot ihsan at gmail dot com)
// Distributed under the MIT/X11 software license, see the accompanying
// file license.txt or http://www.opensource.org/licenses/mit-license.php.
// ---------------------------------------------------------------------------

package com.gyrovague.iching;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author Asim Ihsan
 *
 */
public class DivinationActivity extends Activity {    
    private WebView mBrowser;
    private Context mContext = this;
    private boolean mGameLaunched = false;
    private String TAG = "Instructions::";

    @Override
    public void onCreate(Bundle savedInstanceState) {        
        final String SUB_TAG = "onCreate";
        Log.i(TAG+SUB_TAG, "entry");        
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.divination);
        mBrowser = (WebView)findViewById(R.id.divination);
        mBrowser.setWebViewClient(new Callback());
        
        int value = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            value = Integer.parseInt(extras.getString("hexagram"));
            Log.i(TAG+SUB_TAG, "hexagram value is: " + value);
        } // if (extras != null)        
        
        AssetManager asset_manager = getAssets();
        InputStream input_stream = null;
        try {
            input_stream = asset_manager.open("divinations/" + Integer.toString(value) + ".html");
        } catch (IOException e) {
            Log.e(TAG+SUB_TAG, e.getMessage());
        }
        String data = readTextFile(input_stream);
        
        // the baseUrl argument is arbitrary, and just necessary to be able to access images
        // packaged as assets in the HTML.
        mBrowser.loadDataWithBaseURL("file:///android_asset/", data, "text/html", "UTF-8", "");
    }
    
    @Override
    public void onPause() {
        final String SUB_TAG = "onPause";
        Log.d(TAG+SUB_TAG, "entry");
        super.onPause();     
    }
    
    @Override
    public void onResume() {
        final String SUB_TAG = "onResume";
        Log.d(TAG+SUB_TAG, "entry");
        super.onResume();
        if (mGameLaunched) {
            Log.d(TAG+SUB_TAG, "Game launched, so don't show instructions.");
            finish();
        } // if (mGameLaunched)
    } // public void onResume()
    
    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            final String SUB_TAG = "urlClick";
            Log.d(TAG+SUB_TAG, "entry");
            
            // Don't care what link got clicked, start the game.
            Intent intent = new Intent(Intent.ACTION_VIEW);            
            intent.setClassName(mContext, ShakeActivity.class.getName());
            startActivity(intent);
            mGameLaunched = true;
            
            // We've handled this URL loading, so return true.
            return(true);
            
        } // public boolean shouldOverrideUrlLoading(WebView view, String url)
        
    } // private class Callback extends WebViewClient
    
    private String readTextFile(InputStream inputStream) {
        // ref: http://thedevelopersinfo.com/2009/11/17/using-assets-in-android/
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            
        }
        return outputStream.toString();
    } // private String readTextFile(InputStream inputStream)
    
} // public class DivinationActivity extends Activity