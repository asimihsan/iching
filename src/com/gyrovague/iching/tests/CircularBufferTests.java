// ---------------------------------------------------------------------------
// Copyright (c) 2011 Asim Ihsan (asim dot ihsan at gmail dot com)
// Distributed under the MIT/X11 software license, see the accompanying
// file license.txt or http://www.opensource.org/licenses/mit-license.php.
// ---------------------------------------------------------------------------

package com.gyrovague.iching.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.gyrovague.iching.CircularBuffer;

public class CircularBufferTests extends TestCase {
    private static final int DEFAULT_SIZE = 5;
    
    public CircularBufferTests(String s) {
        super(s);
    } // public CircularBufferTests(String s)

    public void testFill() {
        CircularBuffer<Integer> CB = new CircularBuffer<Integer>(DEFAULT_SIZE);
        List<Integer> comparison = new ArrayList<Integer>();        
        
        for (int i = 0; i < DEFAULT_SIZE * 5; i++) {
            CB.insert(i);
            comparison.add(i);
            verify_buffer(CB, comparison);
        } // for (int i = 0; i < DEFAULT_SIZE; i++)        
    } // public void testFails()
    
    public void verify_buffer(CircularBuffer<Integer> CB, List<Integer> comparison) {
        System.out.println("-----------");
        System.out.println("verify_buffer(): " + comparison.size());
        int comparison_index;
        int max;
        if (comparison.size() > DEFAULT_SIZE) {
            assertEquals(CB.getSize(), DEFAULT_SIZE);
            comparison_index = comparison.size() - DEFAULT_SIZE;
            max = DEFAULT_SIZE;
        } else {
            assertEquals(CB.getSize(), comparison.size());
            comparison_index = 0;
            max = comparison.size();
        }        
        
        for (int i = 0; i < max; i++, comparison_index++) {
            System.out.println("CB[" + i + "]: " + CB.get(i) + ", comparison[" + comparison_index + "]: " + comparison.get(comparison_index));
            assertEquals(CB.get(i), comparison.get(comparison_index));
        }
    } // public boolean verify_buffer<T>(CircularBuffer<T>, List<T>)
}
