package com.taitl.ex.common.helper;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import com.taitl.ex.common.helper.io.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesTest
{
    @Test
    @DisplayName("Load reads properties")
    void loadReadsProperties()
            throws Exception
    {
        ByteArrayInputStream stream =
                new ByteArrayInputStream("flag=true\nname=value".getBytes(StandardCharsets.UTF_8));

        java.util.Properties props = Properties.load(stream, 1024);

        assertEquals("true", props.getProperty("flag"));
        assertEquals("value", props.getProperty("name"));
    }

    @Test
    @DisplayName("Load rejects oversized input")
    void loadRejectsOversizedInput()
    {
        StringBuilder text = new StringBuilder("flag=");
        for (int i = 0; i < 256; i++)
        {
            text.append('x');
        }

        ByteArrayInputStream stream =
                new ByteArrayInputStream(text.toString().getBytes(StandardCharsets.UTF_8));

        assertThrows(LimitedInputStream.MaxSizeExceededException.class,
                () -> Properties.load(stream, 32));
    }

    @Test
    @DisplayName("Parse boolean accepts trimmed case insensitive values")
    void parseBooleanAcceptsTrimmedCaseInsensitiveValues()
    {
        assertTrue(Properties.parseBoolean(" true "));
        assertTrue(Properties.parseBoolean("TrUe"));
        assertFalse(Properties.parseBoolean(" FALSE "));
    }

    @Test
    @DisplayName("Parse boolean rejects invalid values")
    void parseBooleanRejectsInvalidValues()
    {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> Properties.parseBoolean("YES"));

        assertTrue(ex.getMessage().contains("Use true/false"));
    }
}
