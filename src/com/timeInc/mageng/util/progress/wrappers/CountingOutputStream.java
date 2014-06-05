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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.timeInc.mageng.util.progress.ProgressListener;

/**
 * Class that wraps an OutputStream so it can keep
 * track of the number of bytes written.
 * 
 * Not thread-safe
 * 
 *
 */
public class CountingOutputStream extends FilterOutputStream {

    private final ProgressListener listener;
    private final long totalSize;
    private long transferred;

    /**
     * Instantiates a new counting output stream.
     *
     * @param out the out
     * @param listener the listener
     * @param totalSize the total size
     */
    public CountingOutputStream(final OutputStream out, final ProgressListener listener, long totalSize) {
        super(out);
        this.listener = listener;
        this.transferred = 0;
        this.totalSize = totalSize;
    }

    /* (non-Javadoc)
     * @see java.io.FilterOutputStream#write(byte[], int, int)
     */
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        this.transferred += len;
        this.listener.inProgress(this.totalSize,this.transferred);
    }

    /* (non-Javadoc)
     * @see java.io.FilterOutputStream#write(int)
     */
    public void write(int b) throws IOException {
        out.write(b);
        this.transferred++;
        this.listener.inProgress(this.totalSize,this.transferred);
    }
}
