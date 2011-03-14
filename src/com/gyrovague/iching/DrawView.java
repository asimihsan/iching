// ---------------------------------------------------------------------------
// Copyright (c) 2011 Asim Ihsan (asim dot ihsan at gmail dot com)
// Distributed under the MIT/X11 software license, see the accompanying
// file license.txt or http://www.opensource.org/licenses/mit-license.php.
// ---------------------------------------------------------------------------

package com.gyrovague.iching;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class DrawView extends View {

    protected Context mContext;
    private Activity mActivity;
    protected String mTag;
    protected Resources mResources;
    protected Rect mRectCanvas;    

    public DrawView(Context context) {
        super(context);
        commonConstructor(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonConstructor(context);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        commonConstructor(context);
    }
    
    private void commonConstructor(Context context) {
        // TODO Auto-generated method stub
        mContext = context;
        mTag = context.getString(R.string.app_name);
        mResources = context.getResources();
        invalidate();        
    }    
    
    @Override
    protected void onDraw(Canvas canvas) {        
        draw(canvas);
    } // protected void onDraw(Canvas canvas)
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setupDrawingCoordinates();     
    }    
    
    private void setupDrawingCoordinates() {
        /**
         * Measurements.
         */
        int width = getWidth();
        int height = getHeight();
        int top = getTop();
        int left = getLeft();        
        mRectCanvas = new Rect(left, top, left + width, top + height);
    } // private void setupDrawingCoordinates()    
    
    public final Animation runFadeInAnimation() {
        final String SUB_TAG = mTag + "::runFadeInAnimation::" + this;
        Log.i(SUB_TAG, "entry");
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.fade_in);
        this.startAnimation(animation);
        return animation;
    } // private void fadeInHelpText()    
    
    public final Animation runFadeInSlowAnimation() {
        final String SUB_TAG = mTag + "::runFadeInSlowAnimation::" + this;
        Log.i(SUB_TAG, "entry");
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.fade_in_slow);
        this.startAnimation(animation);
        return animation;
    } // private void fadeInHelpText()    
    
    public final Animation runFadeOutAnimation() {
        final String SUB_TAG = mTag + "::runFadeOutAnimation::" + this;
        Log.i(SUB_TAG, "entry");
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.fade_out);
        this.startAnimation(animation);
        return animation;
    } // private void fadeInHelpText()    

    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }
}
