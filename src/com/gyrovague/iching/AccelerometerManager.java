// ---------------------------------------------------------------------------
// Copyright (c) 2011 Asim Ihsan (asim dot ihsan at gmail dot com)
// Distributed under the MIT/X11 software license, see the accompanying
// file license.txt or http://www.opensource.org/licenses/mit-license.php.
// ---------------------------------------------------------------------------

/*
TODO
- why is it when I implement the SensorEventListener this doesn't work,
but it does work with the deprecated SensorListener interface?  Weird.
- provide some mechanism for calibration (ask the user to shake, measure
min/max, then ask the user to keep still, measure min/max, and set
FORCE_THRESHOLD accordingly).
- store the force changes in a circular buffer to provide a source of entry,
some pair of 64 bit numbers to seed the RNG.
*/

package com.gyrovague.iching;

import java.util.Vector;

import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

final class Datum {
    public Long mTime;
    public Float mX;
    public Float mY;
    public Float mZ;
    
    public Datum(long time, float x, float y, float z) {
        mTime = time;
        mX = x;
        mY = y;
        mZ = z;        
    } // public Datum(time, x, y, z)    
} // final class Datum

public class AccelerometerManager implements SensorListener {
    private static final String TAG = "AccelerometerManager";
    private static final double FORCE_THRESHOLD = SensorManager.GRAVITY_EARTH + 1.0f;    
    private SensorManager mSensorManager = null;
    private Handler mUIHandler;
    private Context mContext;
    private Handler mChildHandler = null;
    private int mState;
    private AccelerometerManager me = this;
    
    // -------------------------------------------------------------------------
    //  Circular buffer of accelerometer measurements.
    // -------------------------------------------------------------------------
    private CircularBuffer<Datum> mHistory;
    private static final int HISTORY_SIZE = 100;
    // -------------------------------------------------------------------------
    
    // once the phone starts shaking, what is the minimum period of unshaken
    // time before we consider the phone to be still, in milliseconds.
    private static final long UNSHAKEN_TIME = 250;
    private long mLastUnshaken;
    
    private static final int MSG_TYPE_START_SHAKING     = 1;
    private static final int MSG_TYPE_STOP_SHAKING      = 2;
    private static final int MSG_TYPE_CURRENT_ACCEL     = 3;
    
    private static final int STATE_SHAKING              = 1;
    private static final int STATE_NOT_SHAKING          = 2;
    
    class ChildThread extends Thread {
        private static final String TAG = "ChildThread";
        public void run() {            
            // -------------------------------------------------
            //  Register for the accelerometer's changes.
            // -------------------------------------------------        
            mSensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
            mSensorManager.registerListener(me,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);        
            // -------------------------------------------------            
    
            this.setName("child");
            Looper.prepare();
            mChildHandler = new Handler() {
                public void handleMessage(Message msg) {
                    final String SUB_TAG = TAG + "::handleMessage";
                    Message message;
                    switch(msg.what) {
                        case MSG_TYPE_START_SHAKING:
                            assert(mState == STATE_SHAKING);                                 
                            message = mUIHandler.obtainMessage(ShakeActivity.MSG_TYPE_START_SHAKING, msg.obj);
                            mUIHandler.sendMessage(message);
                            break;
                            
                        case MSG_TYPE_STOP_SHAKING:         
                            assert(mState == STATE_SHAKING);
                            mState = STATE_NOT_SHAKING;
                            message = mUIHandler.obtainMessage(ShakeActivity.MSG_TYPE_STOP_SHAKING, msg.obj);                                
                            mUIHandler.sendMessage(message);                            
                            break;
                            
                        case MSG_TYPE_CURRENT_ACCEL:
                            // -------------------------------------------------
                            //  Store this particular acceleration occurrence in the 
                            //  history.
                            // -------------------------------------------------
                            Datum datum = (Datum)msg.obj;
                            mHistory.insert(datum);
                            // -------------------------------------------------                
                            
                            long time_since_event = System.currentTimeMillis() - datum.mTime;
                            long correct_unshaken_time = UNSHAKEN_TIME - time_since_event;
                            if (correct_unshaken_time < 0) correct_unshaken_time = 0;
                            
                            double total_force = 0.0f;
                            total_force += Math.pow(datum.mX, 2.0);
                            total_force += Math.pow(datum.mY, 2.0);
                            total_force += Math.pow(datum.mZ, 2.0);
                            total_force = Math.sqrt(total_force);                            
                            
                            switch (mState) {
                                case STATE_SHAKING:
                                    if (total_force > FORCE_THRESHOLD) {
                                        // we're shaking, and the force is sufficient to keep us shaking.
                                        mChildHandler.removeMessages(MSG_TYPE_STOP_SHAKING);
                                    } else {
                                        // phone has just stopped being shaken
                                        if (!mChildHandler.hasMessages(MSG_TYPE_STOP_SHAKING)) {
                                            message = mChildHandler.obtainMessage(MSG_TYPE_STOP_SHAKING, total_force);
                                            mChildHandler.sendMessageDelayed(message, correct_unshaken_time);    
                                        } // if (!mChildHandler.hasMessages(MSG_TYPE_STOP_SHAKING))                                                                        
                                    } // if (total_force > FORCE_THRESHOLD)                                    
                                    break;
                                    
                                case STATE_NOT_SHAKING:
                                    if (total_force > FORCE_THRESHOLD) {
                                        // phone is starting to be shaken
                                        mState = STATE_SHAKING;                                
                                        message = mChildHandler.obtainMessage(MSG_TYPE_START_SHAKING, total_force);
                                        mChildHandler.sendMessage(message);                                        
                                    } // if (total_force > FORCE_THRESHOLD)
                                    break;
                            } // switch (mState)
                            
                            break;                            
                    } // switch(msg.what)                    
                } // public void handleMessage(Message msg)
            }; // mChildHandler = new Handler()
            
            mState = STATE_NOT_SHAKING;
            Looper.loop();
        } // public void run()
    } // class ChildThread extends Thread
    
    public AccelerometerManager(Context ui_context, Handler ui_handler) {
        final String SUB_TAG = TAG + "::onCreate";
        Log.i(SUB_TAG, "entry");        
        mContext = ui_context;
        mUIHandler = ui_handler;        
        mHistory = new CircularBuffer<Datum>(HISTORY_SIZE);
        mLastUnshaken = 0;
    }
    
    public void start() {
        new ChildThread().start();        
    } // public void start()
    
    public void stop() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }        
        try {
            mChildHandler.getLooper().quit();
        } catch (NullPointerException e) {
            // already been stopped, but calling quit() sets
            // the Looper's internal mQueue to null and
            // I can't see a way of querying mQueue's state.
        }        
        mChildHandler = null;
    } // public void stop()
   
    public void onAccuracyChanged(int sensor, int accuracy) {                
    } // public void onAccuracyChanged(Sensor sensor, int accuracy)

    public void onSensorChanged(int sensor, float[] values) {        
        synchronized (me) {
            if (mChildHandler == null) {
                return;
            }
            
            final String SUB_TAG = TAG + "::onSensorChanged";                       
            if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
                Datum datum =  new Datum(System.currentTimeMillis(),
                                         values[SensorManager.DATA_X],
                                         values[SensorManager.DATA_Y],
                                         values[SensorManager.DATA_Z]);                
                Message message = mChildHandler.obtainMessage(MSG_TYPE_CURRENT_ACCEL, datum);
                mChildHandler.sendMessage(message);                
            } // if (sensor == SensorManager.SENSOR_ACCELEROMETER)
        } // synchronized (this)        
    } // public void onSensorChanged(SensorEvent event)
    
    
    /**
     * Return an array of integers suitable for seeing a random number
     * generator.  Since we use the history of acceleration changes to
     * populate this result there's some connection between the user's
     * shaking actions and the random numbers.
     * @param points Number of integers you want returned.  Must be a multiple
     * of 5.
     * @return An array of int suitable for seeing a PRNG.
     */
    public int[] getEntropy(int points) {
        final String SUB_TAG = TAG + "::getEntropy";
        assert(points % 5 == 0);
        int[] results = new int[points];
        int marker = 0;         
        int buffer_size = mHistory.getSize();
        Datum datum;        
        Log.i(SUB_TAG, "buffer_size: " + buffer_size);
        while (((marker / 5) < buffer_size) && ((marker / 5) < points)) {
            datum = mHistory.get(marker);
            if (datum == null) {
                break;
            } // if (datum == null)
            results[marker] = (int)(datum.mTime & 0xFFFFFFFF);
            results[marker+1] = (int)(datum.mTime >> 32);
            results[marker+2] = Float.floatToIntBits(datum.mX);
            results[marker+3] = Float.floatToIntBits(datum.mY);
            results[marker+4] = Float.floatToIntBits(datum.mZ);
            marker += 5;            
        } // while(marker <= buffer_size)
        while (marker < points) {
            results[marker] = 0;
            marker++;
        } // while (marker <= points)
        
        for(int i = 0; i < points; i++) {
            Log.d(SUB_TAG, "entropy: " + results[i]);
        }
        return results;        
    } // public int[] getEntropy(int points)
    
    
} // public class AccelerometerManager