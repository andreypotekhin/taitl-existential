package com.taitl.ex.common.helper;

import java.io.*;

/**
 * Input stream wrapper that caps the total number of bytes that can be read.
 */
public class LimitedInputStream extends FilterInputStream
{
    private final long maxBytes;
    private long readBytes;

    public LimitedInputStream(InputStream in, long maxBytes)
    {
        super(in);
        this.maxBytes = maxBytes;
    }

    @Override
    public int read() throws IOException
    {
        if (readBytes >= maxBytes)
        {
            int probe = super.read();
            if (probe == -1)
            {
                return -1;
            }
            readBytes++;
            throw new MaxSizeExceededException(maxBytes);
        }
        int value = super.read();
        if (value != -1)
        {
            readBytes++;
        }
        return value;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        if (len == 0)
        {
            return 0;
        }
        if (readBytes >= maxBytes)
        {
            int probe = super.read();
            if (probe == -1)
            {
                return -1;
            }
            readBytes++;
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
