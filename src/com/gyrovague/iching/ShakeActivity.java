// ---------------------------------------------------------------------------
// Copyright (c) 2011 Asim Ihsan (asim dot ihsan at gmail dot com)
// Distributed under the MIT/X11 software license, see the accompanying
// file license.txt or http://www.opensource.org/licenses/mit-license.php.
// ---------------------------------------------------------------------------

package com.gyrovague.iching;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import ec.util.MersenneTwister;

public class ShakeActivity extends Activity {
    final static String TAG = "ShakeActivity";
    private Handler mHandler;
    private AccelerometerManager mAM;
    private Activity myActivity = this;
    private Context myContext = this;
    private double mCurrentForce;
    private MersenneTwister mRNG;
    private int current_hexagram;
    private int mState;
    private DrawViewBackgroundProgress mDrawViewBackgroundProgress;
    private DrawViewHelpText mDrawViewHelpText;
    private DrawViewRevealHexagram mDrawViewRevealHexagram;
    
    public static final int MSG_TYPE_START_SHAKING      = 1;
    public static final int MSG_TYPE_STOP_SHAKING       = 2;
    public static final int MSG_TYPE_CURRENT_FORCE      = 3;    
    public static final int MSG_TYPE_STILL_SHAKING      = 4;
    public static final int MSG_TYPE_CREATE             = 5;
    public static final int MSG_TYPE_FADE_OUT_HEXAGRAM  = 6;
    public static final int MSG_TYPE_SWITCH_TO_DESCRIPTION = 7;
    
    private static final int STATE_SHAKING_NEED_MORE    = 1;
    private static final int STATE_SHAKING_LONG_ENOUGH  = 2;
    private static final int STATE_NOT_SHAKING          = 3;   
    private static final int STATE_FINISHED             = 4;
    private static final int STATE_CREATING             = 5;
    private static final int STATE_HEXAGRAM_SHOWN       = 6;
    
    /**
     * What during of shaking is required to generate a hexagram.  If the user
     * stops shaking before the total time of shaking exceeds this number
     * then prompt them to shake more. 
     */
    private static final int SHAKING_THRESHOLD = 5000;
    
    private static final int SHAKING_PRECISION = 100;
    
    /**
     * Cumulative time for which the phone has been shaken for. 
     */
    private long mCurrentShakingTime;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        final String SUB_TAG = TAG + "::onCreate";
        Log.i(SUB_TAG, "entry");
        super.onCreate(savedInstanceState);        
                
    } // public void onCreate(Bundle savedInstanceState)
    
    private void initializeLayout() {
        setContentView(R.layout.main);
        mDrawViewBackgroundProgress = (DrawViewBackgroundProgress)findViewById(R.id.draw_view_background_progress);
        mDrawViewHelpText = (DrawViewHelpText)findViewById(R.id.draw_view_help_text);
        mDrawViewRevealHexagram = (DrawViewRevealHexagram)findViewById(R.id.draw_view_reveal_hexagram);
        mDrawViewBackgroundProgress.setActivity(myActivity);
        mDrawViewHelpText.setActivity(myActivity);
        mDrawViewRevealHexagram.setActivity(myActivity);
    } // private void initializeLayout()
    
    private Animation fadeInHelpText() {
        if (!mDrawViewHelpText.isVisible() ) {
          mDrawViewHelpText.setVisible(true);
          Animation animation = mDrawViewHelpText.runFadeInAnimation();
          return animation;
        } 
        return null;
    } // private void fadeInHelpText()
    
    private Animation fadeInHexagram() {
        if (!mDrawViewRevealHexagram.isVisible() ) {
          mDrawViewRevealHexagram.setIsVisible(true);
          Animation animation = mDrawViewRevealHexagram.runFadeInSlowAnimation();
          return animation;
        } 
        return null;
    } // private void fadeInHexagram()    
    
    private Animation fadeOutHexagram() {
        if (mDrawViewRevealHexagram.isVisible() ) {
          mDrawViewRevealHexagram.setIsVisible(false);
          Animation animation = mDrawViewRevealHexagram.runFadeOutAnimation();
          return animation;
        } 
        return null;
    } // private void fadeInHexagram()    
    
    @Override
    public void onResume() {        
        super.onResume();
        mCurrentShakingTime = 0;        
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                final String SUB_TAG = TAG + "::handleMessage";               
                switch(msg.what) {
                    case MSG_TYPE_CREATE:
                        initializeLayout();
                        mCurrentForce = 0;
                        fadeInHelpText();                        
                        mState = STATE_NOT_SHAKING;                        
                        break;
                    
                    case MSG_TYPE_START_SHAKING:                      
                        Log.i(SUB_TAG, "shaking: " + mCurrentShakingTime);
                        switch (mState) {
                            case STATE_HEXAGRAM_SHOWN:
                            case STATE_FINISHED:
                                break;
                                
                            default:
                                mState = STATE_SHAKING_NEED_MORE;
                                mHandler.sendEmptyMessageDelayed(MSG_TYPE_STILL_SHAKING, SHAKING_PRECISION);                                
                        } // switch (mState)
                        break;
                        
                    case MSG_TYPE_STOP_SHAKING:                      
                        Log.i(SUB_TAG, "not shaking: " + mCurrentShakingTime);                        
                        mHandler.removeMessages(MSG_TYPE_STILL_SHAKING);
                        switch (mState) {
                            case STATE_SHAKING_LONG_ENOUGH:
                                Log.i(SUB_TAG, "shaken enough");
                                
                                mRNG = new MersenneTwister(mAM.getEntropy(100));
                                current_hexagram = mRNG.nextInt(64) + 1;
                                Log.i(SUB_TAG, "hexagram: " + current_hexagram);                            
                                mDrawViewHelpText.setVisible(false);
                                mDrawViewRevealHexagram.setHexagram(current_hexagram);
                                Animation animation = fadeInHexagram();
                                Log.i(SUB_TAG, "hexagram fade in duration: " + animation.getDuration());
                                mHandler.sendEmptyMessageDelayed(MSG_TYPE_FADE_OUT_HEXAGRAM, (long) (animation.getDuration() * 1.5));                            
                                mState = STATE_HEXAGRAM_SHOWN;
                                break;
                            
                            case STATE_HEXAGRAM_SHOWN:
                            case STATE_FINISHED:
                                break;
                                
                            default:
                                fadeInHelpText();
                                mState = STATE_NOT_SHAKING;
                                break;
                        }  // switch (mState)                                                
                        break;
                        
                    case MSG_TYPE_STILL_SHAKING:                        
                        mCurrentShakingTime += SHAKING_PRECISION;
                        Log.i(SUB_TAG, "still shaking: " + mCurrentShakingTime);                        
                        if (mCurrentShakingTime >= SHAKING_THRESHOLD) {
                            mState = STATE_SHAKING_LONG_ENOUGH;
                            mHandler.sendEmptyMessage(MSG_TYPE_STOP_SHAKING);
                            mDrawViewBackgroundProgress.setRatio(1.0f);                            
                        } else {
                            mHandler.sendEmptyMessageDelayed(MSG_TYPE_STILL_SHAKING, SHAKING_PRECISION);
                            mDrawViewBackgroundProgress.setRatio((float)mCurrentShakingTime / (float)SHAKING_THRESHOLD);
                        } // // if (mCurrentShakingTime >= SHAKING_THRESHOLD)
                        mDrawViewHelpText.setVisible(false);
                        break;
                        
                    case MSG_TYPE_FADE_OUT_HEXAGRAM:
                        Log.i(SUB_TAG, "fade out hexagram");
                        Animation animation = fadeOutHexagram();
                        mHandler.sendEmptyMessageDelayed(MSG_TYPE_SWITCH_TO_DESCRIPTION, animation.getDuration() + 500);
                        break;
                        
                    case MSG_TYPE_SWITCH_TO_DESCRIPTION:
                        Log.i(SUB_TAG, "switch to description");
                        // since the "right" thing to do here is to launch
                        // another activity (can't imagine how a fourth,
                        // permanent DrawView in the FrameLayout makes sense)
                        // then make it so.
                        mState = STATE_FINISHED;
                        mAM.stop();
                        break;
                        
                } // switch(msg.what)                
                
                if (mState != STATE_CREATING) {
                    mDrawViewBackgroundProgress.invalidate();
                    mDrawViewHelpText.invalidate();
                    mDrawViewRevealHexagram.invalidate();
                }
                
            } // public void handleMessage(Message msg)            
        }; // mHandler = new Handler()
        
        mAM = new AccelerometerManager(myContext, mHandler);
        mAM.start();
        mHandler.sendEmptyMessage(MSG_TYPE_CREATE);
        mState = STATE_CREATING;
    } // public void onResume()
    
    @Override
    public void onPause() {
        super.onPause();
        mAM.stop();
    } // public void onPause()
}
