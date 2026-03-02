package com.taitl.ex.common.helper;

import java.io.ByteArrayInputStream;
import java.io.PushbackInputStream;

import com.taitl.ex.common.helper.io.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class LimitedInputStreamTest
{
    @Test
    @DisplayName("Read does not consume extra byte when limit reached")
    void readDoesNotConsumeExtraByteWhenLimitReached() throws Exception
    {
        byte[] data = new byte[] { 1, 2, 3 };
        PushbackInputStream pushback = new PushbackInputStream(new ByteArrayInputStream(data), 1);
        LimitedInputStream limited = new LimitedInputStream(pushback, 2);

        assertEquals(1, limited.read());
        assertEquals(2, limited.read());

        assertThrows(LimitedInputStream.MaxSizeExceededException.class, limited::read);
        assertEquals(3, pushback.read());
    }

    @Test
    @DisplayName("Read array does not consume extra byte when limit reached")
    void readArrayDoesNotConsumeExtraByteWhenLimitReached() throws Exception
    {
        byte[] data = new byte[] { 1, 2, 3, 4 };
        PushbackInputStream pushback = new PushbackInputStream(new ByteArrayInputStream(data), 1);
        LimitedInputStream limited = new LimitedInputStream(pushback, 3);

        byte[] buffer = new byte[2];
        assertEquals(2, limited.read(buffer, 0, 2));
        assertEquals(1, limited.read(buffer, 0, 2));

        assertThrows(LimitedInputStream.MaxSizeExceededException.class,
                () -> limited.read(buffer, 0, 2));
        assertEquals(4, pushback.read());
    }
}
