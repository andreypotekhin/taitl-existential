package com.taitl.ex.common.helper.io;

import java.io.*;

/**
 * Input stream wrapper that caps the total number of bytes that can be read.
 */
public class LimitedInputStream extends FilterInputStream
{
    private final PushbackInputStream probeStream;
    private final long maxBytes;
    private long readBytes;

    public LimitedInputStream(InputStream in, long maxBytes)
    {
        super(wrap(in));
        this.probeStream = (PushbackInputStream) this.in;
        this.maxBytes = maxBytes;
    }

    private static PushbackInputStream wrap(InputStream in)
    {
        if (in instanceof PushbackInputStream)
        {
            return (PushbackInputStream) in;
        }
        return new PushbackInputStream(in, 1);
    }

    public int read() throws IOException
    {
        if (readBytes >= maxBytes)
        {
            int probe = probeStream.read();
            if (probe == -1)
            {
                return -1;
            }
            probeStream.unread(probe);
            throw new MaxSizeExceededException(maxBytes);
        }
        int value = super.read();
        if (value != -1)
        {
            readBytes++;
        }
        return value;
    }

    public int read(byte[] b, int off, int len) throws IOException
    {
        if (len == 0)
        {
            return 0;
        }
        if (readBytes >= maxBytes)
        {
            int probe = probeStream.read();
            if (probe == -1)
            {
                return -1;
            }
            probeStream.unread(probe);
            throw new MaxSizeExceededException(maxBytes);
        }
        long remaining = maxBytes - readBytes;
        int allowed = (int) Math.min(len, remaining);
        int read = super.read(b, off, allowed);
        if (read == -1)
        {
            return -1;
        }
        readBytes += read;
        return read;
    }

    public static final class MaxSizeExceededException extends IOException
    {
        public MaxSizeExceededException(long maxBytes)
        {
            super(String.format("Configuration data exceeds max size of %d bytes", maxBytes));
        }
    }
}
