/*******************************************************************************
 * Copyright 2014 Time Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.timeInc.mageng.util.progress.wrappers;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.timeInc.mageng.util.progress.ProgressListener;


/**
 * A class that decorates the InputStream to keep track of the number
 * of bytes read so that it can inform a ProgressListener.
 * 
 * This class is not thread-safe.
 * 
 *
 */

public class CountingInputFileStream extends FilterInputStream {
    private final ProgressListener listener;
    private final long inputSize;
    private long totalRead;
    
    /**
     * Constructs an instance with the specified ProgressListener, the InputStream to
     * decorate, and the estimated total size of the InputStream
     * @param listener the listener to inform when bytes are being read
     * @param input the input stream to count the number of bytes read
     * @param inputSize the estimated total size of the InputStream
     */
	public CountingInputFileStream(ProgressListener listener, InputStream input, long inputSize) {
		super(input);
		this.listener = listener;
		this.inputSize = inputSize;
		totalRead = 0;
	}
    
	
	/**
	 * Informs the listener the total size currently read and delegates
	 * the reading to the constructed InputStream
	 * @see InputStream#read()
	 */
    @Override
    public int read() throws IOException {
    	totalRead++;
    	this.listener.inProgress(this.inputSize,this.totalRead);
    	return super.read();
    }
    
    /**
     * Informs the listener the total size currently read
     * @see InputStream#read(byte[])
     */
    @Override
    public int read(byte[] b) throws IOException {
    	totalRead += b.length;
    	this.listener.inProgress(this.inputSize,this.totalRead);
    	return super.read(b);
    }
    
    /**
     * Informs the listener the total size currently read
     * @see InputStream#read(byte[], int, int)
     */
    public int read(byte[] b, int off, int len) throws IOException {
    	totalRead += len;
    	this.listener.inProgress(this.inputSize,this.totalRead);
    	return super.read(b,off,len);
    }
    
}
