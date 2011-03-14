// ---------------------------------------------------------------------------
// Copyright (c) 2011 Asim Ihsan (asim dot ihsan at gmail dot com)
// Distributed under the MIT/X11 software license, see the accompanying
// file license.txt or http://www.opensource.org/licenses/mit-license.php.
// ---------------------------------------------------------------------------

package com.gyrovague.iching;

import java.util.Vector;

public class CircularBuffer<T> {
    private Vector<T> mBuffer;
    private int mFirst;
    private int mNext;
    
    public CircularBuffer(int size) {        
        mBuffer = new Vector<T>(size + 1);
        mBuffer.setSize(size + 1);        
        clear();
    } // public CircularBuffer(int size)
    
    public void clear() {
        for (int i = 0; i < mBuffer.size(); i++) {
            mBuffer.set(i, null);
        } // for (int i = 0; i < mBuffer.size(); i++)
        mFirst = 0;
        mNext = 0;
    } // public void clear()
    
    public T get(int index) {
        if (isEmpty()) {
            return null;
        } // if (isEmpty())
        return mBuffer.get(mod(mFirst + index, mBuffer.size()));
    } // public T get()
    
    public int getSize() {
        int result = mod(mBuffer.size() + mNext - mFirst, mBuffer.size());
        //System.out.println("getSize() result: " + result);
        return result;
    } // public int getSize()
    
    public boolean isFull() {
        //System.out.println("isFull");
        return mod(mNext + 1, mBuffer.size()) == mFirst;
    } // public boolean isFull()
    
    public boolean isEmpty() {
        //System.out.println("isEmpty: " + (mFirst == mNext));
        return mFirst == mNext;
    } // public boolean empty()
    
    public void insert(T item) {
        //System.out.println("insert entry");
        //System.out.println("mFirst: " + mFirst + ", mNext: " + mNext);
        if (isFull()) {
            //System.out.println("isFull returns true");
            pop();
        }
        mBuffer.set(mNext, item);
        mNext = mod(mNext + 1, mBuffer.size());
        //System.out.println("insert exit");
        //System.out.println("mFirst: " + mFirst + ", mNext: " + mNext);        
    } // public void insert (T item)
    
    public T remove() {
        if (isEmpty()) {
            return null;
        }
        T result = mBuffer.get(mFirst);
        mBuffer.set(mFirst, null);
        mFirst = mod(mFirst + 1, mBuffer.size());                
        return result;
    } // public void remove()
    
    public void pop() {        
        if (!isEmpty()) {
            mBuffer.set(mFirst, null);
            mFirst = mod(mFirst + 1, mBuffer.size());
        } // if (!isEmpty())
    } // public void pop()
    
    /*
     * In Java, it seems that, if a < b, then a % b = a, in that this is
     * actually the remainder operator.
     * 
     * @param a First integer.
     * @param b Second integer.
     * @return
     */
    public int mod(int a, int b) {
        if (a < 0) {            
            int b_orig = b;
            while (a < b) {
                b -= b_orig;
            }
            int result = a - b + 1;
            //System.out.println("a: " + a + ", b: " + b + ", a % b: " + result);
            return result;            
        } else {
            int result = a;
            while (result >= b) {
                result -= b;
            } // while (result > b)
            //System.out.println("a: " + a + ", b: " + b + ", a % b: " + result);
            return result;            
        }        
    } // public int mod(a, b)
    
} // final class CircularBuffer
