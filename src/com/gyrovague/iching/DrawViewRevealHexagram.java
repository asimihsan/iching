// ---------------------------------------------------------------------------
// Copyright (c) 2011 Asim Ihsan (asim dot ihsan at gmail dot com)
// Distributed under the MIT/X11 software license, see the accompanying
// file license.txt or http://www.opensource.org/licenses/mit-license.php.
// ---------------------------------------------------------------------------

package com.gyrovague.iching;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

public class DrawViewRevealHexagram extends DrawView {
    
    private int mHexagram = -1;
    private boolean mIsVisible = false;
    private Paint mHexagramPaint;    
    private Paint mBackgroundPaint;
    
    private static final double GOLDEN_RATIO = 1.6180339887498948482045868343656;
    private static final double RATIO_X = 0.8;    
    private static final double RATIO_Y = 0.8;
    private static final int HORIZONTAL_SLOTS = 11;
    private static final double MIDDLE_RECT = 0.2;
    private static final String[] NUMBER_TO_PATTERN = 
        {
        null,    // empty, to make array 1-based
        "999999", // 1, Ch'ien
        "666666", // 2, K'un
        "966696", // 3, Chun
        "696669", // 4, M&ecirc;ng
        "999696", // 5, Hsu
        "696999", // 6, Sung
        "696666", // 7, Shih
        "666696", // 8, Pi
        "999699", // 9, Hsiao Ch'u
        "996999", // 10, Lu
        "999666", // 11, T'ai
        "666999", // 12, P'i
        "969999", // 13, T'ung J&ecirc;n
        "999969", // 14, Ta Yu
        "669666", // 15, Ch'ien
        "666966", // 16, Yu
        "966996", // 17, Sui
        "699669", // 18, Ku
        "996666", // 19, Lin
        "666699", // 20, Kuan
        "966969", // 21, Shih Ho
        "969669", // 22, Pi
        "666669", // 23, Po
        "966666", // 24, Fu
        "966999", // 25, Wu Wang
        "999669", // 26, Ta Ch'u
        "966669", // 27, I
        "699996", // 28, Ta Kuo
        "696696", // 29, K'an
        "969969", // 30, Li
        "669996", // 31, Hsien
        "699966", // 32, H&ecirc;ng
        "669999", // 33, Tun
        "999966", // 34, Ta Chuang
        "666969", // 35, Chin
        "969666", // 36, Ming I
        "969699", // 37, Chia J&ecirc;n
        "996969", // 38, K'uei
        "669696", // 39, Chien
        "696966", // 40, Hsieh
        "996669", // 41, Sun
        "966699", // 42, I
        "999996", // 43, Kuai
        "699999", // 44, Kou
        "666996", // 45, Ts'ui
        "699666", // 46, Sh&ecirc;ng
        "696996", // 47, K'un
        "699696", // 48, Ching
        "969996", // 49, Ko
        "699969", // 50, Ting
        "966966", // 51, Ch&ecirc;n
        "669669", // 52, K&ecirc;n
        "669699", // 53, Chien
        "996966", // 54, Kuei Mei
        "969966", // 55, F&ecirc;ng
        "669969", // 56, Lu
        "699699", // 57, Sun
        "996996", // 58, Tui
        "696699", // 59, Huan
        "996696", // 60, Chieh
        "996699", // 61, Chung Fu
        "669966", // 62, Hsiao Kuo
        "969696", // 63, Chi Chi
        "696969", // 64, Wei Chi
         };

    public DrawViewRevealHexagram(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonConstructor(context);
        // TODO Auto-generated constructor stub
    }

    public DrawViewRevealHexagram(Context context) {
        super(context);
        commonConstructor(context);
        // TODO Auto-generated constructor stub
    }

    private void commonConstructor(Context context) {
        mHexagramPaint = new Paint();
        mHexagramPaint.setColor(mResources.getColor(R.color.solid_white));
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mResources.getColor(R.color.solid_black));
    }
    
    @Override
    public void draw(Canvas canvas) {
        if (mIsVisible && mHexagram > 0) {
            canvas.drawRect(mRectCanvas, mBackgroundPaint);            
            int max_width = (int) (mRectCanvas.width() * RATIO_X);
            int max_height = (int) (mRectCanvas.height() * RATIO_Y);            
            int height_slot = (max_height / HORIZONTAL_SLOTS);
            
            int current_slot = 0;
            int width = max_width;
            int height = max_height;
            
            String pattern = NUMBER_TO_PATTERN[mHexagram];
            CharacterIterator it = new StringCharacterIterator(pattern);
            for (char ch=it.first(); ch != CharacterIterator.DONE; ch=it.next()) {
                int left = (mRectCanvas.width() - width)/2;
                int top = mRectCanvas.top + (mRectCanvas.height() - height)/2 + (2 * current_slot * height_slot);
                int right = mRectCanvas.right - left;
                int bottom = top + height_slot;
                Rect line = new Rect(left, top, right, bottom);
                canvas.drawRect(line, mHexagramPaint);
                
                if (ch == '6') {
                    // dashed line - draw a small background-coloured rect.
                    // left, top, right, bottom
                    int middle_rect_width = (int) ((right - left) * MIDDLE_RECT);
                    int middle_rect_left = ((right + left) / 2) - (middle_rect_width / 2);
                    int middle_rect_right = middle_rect_left + middle_rect_width;
                    Rect middle_rect = new Rect(middle_rect_left, top, middle_rect_right, bottom);
                    canvas.drawRect(middle_rect, mBackgroundPaint);
                } // if (ch == '6')
                
                current_slot +=1;
            } // for (char ch=it.first(); ch != CharacterIterator.DONE, ch=it.next())
        } else {
            
        } // if (mIsVisible)
    } // public void draw(Canvas canvas)

    public void setHexagram(int hexagram) {
        this.mHexagram = hexagram;
    }

    public void setIsVisible(boolean isVisible) {
        this.mIsVisible = isVisible;
    }

    public boolean isVisible() {
        return mIsVisible;
    }

}
