// ---------------------------------------------------------------------------
// Copyright (c) 2011 Asim Ihsan (asim dot ihsan at gmail dot com)
// Distributed under the MIT/X11 software license, see the accompanying
// file license.txt or http://www.opensource.org/licenses/mit-license.php.
// ---------------------------------------------------------------------------

package com.gyrovague.iching;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;

import android.util.AttributeSet;
import android.util.Log;

public class DrawViewBackgroundProgress extends DrawView {
    private static final String TAG = "DrawViewBackgroundProgress";  
    private Handler mHandlerUI;
    private Paint mPaintCanvas = new Paint();
    
    /**
     * When 0, the entire view's background is solid gray.  When 1, the
     * background is black.  When some value (0, 1) = x then set the x
     * proportion of the view black, and (1-x) gray. 
     */
    private float mRatio;

    public DrawViewBackgroundProgress(Context context) {
        super(context);
        commonConstructor(context);        
    } // public DrawViewBackgroundProgress(Context context)
    
    public DrawViewBackgroundProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonConstructor(context);
    } // public DrawViewBackgroundProgress(Context context, AttributeSet attrs)
    
    private void commonConstructor(Context context) {        
        //setFocusable(true);
        setRatio(0.0f);
    } // private void commonConstructor(Context context)
    
    public void setmHandlerUI(Handler mHandlerUI) {
        this.mHandlerUI = mHandlerUI;
    } // public void setmHandlerUI(Handler mHandlerUI)

    @Override
    public void draw(Canvas canvas) {
        final String SUB_TAG = TAG + "::draw()";
        
        int height_gray =  (int) ((1 - mRatio) * mRectCanvas.height());
        int height_black =  mRectCanvas.height() - height_gray;
        //Log.i(SUB_TAG, "height_gray: " + height_gray + ", height_black: " + height_black);
        Rect mRectGray = new Rect(mRectCanvas.left, mRectCanvas.top, mRectCanvas.right, mRectCanvas.top + height_gray);
        Rect mRectBlack = new Rect(mRectCanvas.left, mRectGray.bottom, mRectCanvas.right, mRectCanvas.bottom);            
        //Log.i(SUB_TAG, "mRectGray: " + mRectGray);
        //Log.i(SUB_TAG, "mRectBlack: " + mRectBlack);
        
        mPaintCanvas.setColor(mResources.getColor(R.color.solid_gray));        
        canvas.drawRect(mRectGray, mPaintCanvas);
        mPaintCanvas.setColor(mResources.getColor(R.color.solid_black));        
        canvas.drawRect(mRectBlack, mPaintCanvas);                

    } // public void draw(Canvas canvas)

    public void setRatio(float ratio) {
        final String SUB_TAG = TAG + "::setRatio";
        Log.i(SUB_TAG, "setRatio: " + ratio);
        this.mRatio = ratio;
    } // private void setmRatio(float ratio)

} // public class DrawViewBackgroundProgress
