/*
 * Copyright (c) 2009-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonatype.gossip.support;

import org.slf4j.Logger;
import org.sonatype.gossip.Level;

import java.io.IOException;
import java.io.OutputStream;

/**
 * {@link OutputStream} which logs lines to a {@link Logger}.
 *
 * @author <a href="mailto://Jim.Moore@rocketmail.com">Jim Moore</a>
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 1.8
 *
 * @link https://github.com/apache/log4j/blob/trunk/contribs/JimMoore/LoggingOutputStream.java
 */
public class LoggingOutputStream
    extends OutputStream
{
    protected static final String LINE_SEPERATOR = System.getProperty("line.separator");

    /**
     * Used to maintain the contract of {@link #close()}.
     */
    protected boolean hasBeenClosed = false;

    /**
     * The internal buffer where data is stored.
     */
    protected byte[] buf;

    /**
     * The number of valid bytes in the buffer. This value is always in the range <tt>0</tt> through <tt>buf.length</tt>; elements <tt>buf[0]</tt>
     * through <tt>buf[count-1]</tt> contain valid byte data.
     */
    protected int count;

    /**
     * Remembers the size of the buffer for speed.
     */
    private int bufLength;

    /**
     * The default number of bytes in the buffer. =2048
     */
    public static final int DEFAULT_BUFFER_LENGTH = 2048;

    protected Logger logger;

    protected Level level;

    public LoggingOutputStream(final Logger logger, final Level level) {
        if (logger == null) {
            throw new NullPointerException();
        }
        if (level == null) {
            throw new NullPointerException();
        }

        this.level = level;
        this.logger = logger;
        bufLength = DEFAULT_BUFFER_LENGTH;
        buf = new byte[DEFAULT_BUFFER_LENGTH];
        count = 0;
    }


    /**
     * Closes this output stream and releases any system resources associated with this stream. The general contract of <code>close</code> is that it
     * closes the output stream. A closed stream cannot perform output operations and cannot be reopened.
     */
    public void close() {
        flush();
        hasBeenClosed = true;
    }


    /**
     * Writes the specified byte to this output stream. The general contract for <code>write</code> is that one byte is written to the output stream. The
     * byte to be written is the eight low-order bits of the argument <code>b</code>. The 24 high-order bits of <code>b</code> are ignored.
     *
     * @param b the <code>byte</code> to write
     * @throws IOException if an I/O error occurs. In particular, an <code>IOException</code> may be thrown if the output stream has been closed.
     */
    public void write(final int b) throws IOException {
        if (hasBeenClosed) {
            throw new IOException("The stream has been closed.");
        }

        // don't log nulls
        if (b == 0) {
            return;
        }

        // would this be writing past the buffer?
        if (count == bufLength) {
            // grow the buffer
            final int newBufLength = bufLength + DEFAULT_BUFFER_LENGTH;
            final byte[] newBuf = new byte[newBufLength];

            System.arraycopy(buf, 0, newBuf, 0, bufLength);

            buf = newBuf;
            bufLength = newBufLength;
        }

        buf[count] = (byte) b;
        count++;
    }

    /**
     * Flushes this output stream and forces any buffered output bytes to be written out. The general contract of <code>flush</code> is that calling it
     * is an indication that, if any bytes previously written have been buffered by the implementation of the output stream, such bytes should
     * immediately be written to their intended destination.
     */
    public void flush() {
        if (count == 0) {
            return;
        }

        // don't print out blank lines; flushing from PrintStream puts out these
        if (count == LINE_SEPERATOR.length()) {
            if (((char) buf[0]) == LINE_SEPERATOR.charAt(0) &&
                ((count == 1) ||  // <- Unix & Mac, -> Windows
                    ((count == 2) && ((char) buf[1]) == LINE_SEPERATOR.charAt(1)))) {
                reset();
                return;
            }
        }

        final byte[] theBytes = new byte[count];

        System.arraycopy(buf, 0, theBytes, 0, count);

        level.log(logger, new String(theBytes));

        reset();
    }

    private void reset() {
        // not resetting the buffer -- assuming that if it grew that it
        //   will likely grow similarly again
        count = 0;
    }
}
