package com.taitl.ex.common.helper;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesTest
{
    @Test
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
    void parseBooleanAcceptsTrimmedCaseInsensitiveValues()
    {
        assertTrue(Properties.parseBoolean(" true "));
        assertTrue(Properties.parseBoolean("TrUe"));
        assertFalse(Properties.parseBoolean(" FALSE "));
    }

    @Test
    void parseBooleanRejectsInvalidValues()
    {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> Properties.parseBoolean("YES"));

        assertTrue(ex.getMessage().contains("Use true/false"));
    }
}
