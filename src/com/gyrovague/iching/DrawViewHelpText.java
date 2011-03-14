// ---------------------------------------------------------------------------
// Copyright (c) 2011 Asim Ihsan (asim dot ihsan at gmail dot com)
// Distributed under the MIT/X11 software license, see the accompanying
// file license.txt or http://www.opensource.org/licenses/mit-license.php.
// ---------------------------------------------------------------------------

package com.gyrovague.iching;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

public class DrawViewHelpText extends DrawView {
    
    private TextPaint mPaintText;
    private String mHelpText;
    private StaticLayout mLayout = null;
    private boolean mIsVisible = false;
    private Paint mPaintBlank;
    
    @SuppressWarnings("hiding")
    private String mTag;

    public DrawViewHelpText(Context context) {        
        super(context);
        @SuppressWarnings("hiding")
        String mTag = "DrawViewHelpText";
        final String SUB_TAG = mTag + "::DrawViewHelpText(context)";
        Log.i(SUB_TAG, "entry");
        commonConstructor(context);
    }

    public DrawViewHelpText(Context context, AttributeSet attrs) {
        super(context, attrs);
        @SuppressWarnings("hiding")
        String mTag = "DrawViewHelpText";
        final String SUB_TAG = mTag + "::DrawViewHelpText(context, attrs)";
        Log.i(SUB_TAG, "entry");        
        commonConstructor(context);
    }
    
    private void commonConstructor(Context context) {
        final String SUB_TAG = mTag + "::commonConstructor()";
        Log.i(SUB_TAG, "entry");                
        mPaintText = new TextPaint();
        mPaintText.setColor(mResources.getColor(R.color.solid_white));
        mHelpText = mContext.getString(R.string.help_text);
        mPaintBlank = new Paint();
        mPaintBlank.setColor(mResources.getColor(R.drawable.transparent_background));
    } // private void commonConstructor(Context context)

    @Override
    public void draw(Canvas canvas) {
        final String SUB_TAG = mTag + "::draw()";
        //Log.i(SUB_TAG, "entry");
        
        if (mIsVisible) {
            //Log.i(SUB_TAG, "display text");
            if (mLayout == null) {
                //Log.i(SUB_TAG, "creating mLayout");
                mLayout = new StaticLayout(mHelpText,
                                           mPaintText,
                                           mRectCanvas.width(),
                                           android.text.Layout.Alignment.ALIGN_CENTER,
                                           (float)1.0,
                                           (float)0.0,
                                           true);            
            } // if (mLayout == null)            
            canvas.save();
            canvas.translate(0, (mRectCanvas.height() / 2) - (mLayout.getHeight() / 2));
            mLayout.draw(canvas);            
            canvas.restore();            
        } else {
            //Log.i(SUB_TAG, "do not display text");
            canvas.drawRect(mRectCanvas, mPaintBlank);
        } // if (mIsVisible)        

    } // public void draw(Canvas canvas)        

    public void setVisible(boolean isVisible) {
        this.mIsVisible = isVisible;
    }

    public boolean isVisible() {
        return mIsVisible;
    }

} // public class DrawViewHelpText
