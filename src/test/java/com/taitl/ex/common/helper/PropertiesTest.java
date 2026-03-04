package com.taitl.ex.common.helper;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import com.taitl.ex.common.helper.io.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesTest
{
    @Nested
    class Load
    {
        @Test
        @DisplayName("Reads properties")
        void readsProperties() throws Exception
        {
            ByteArrayInputStream stream =
                    new ByteArrayInputStream("flag=true\nname=value".getBytes(StandardCharsets.UTF_8));

            java.util.Properties props = Properties.load(stream, 1024);

            assertEquals("true", props.getProperty("flag"));
            assertEquals("value", props.getProperty("name"));
        }

        @Test
        @DisplayName("Rejects oversized input")
        void rejectsOversizedInput()
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
    }

    @Nested
    class ParseBoolean
    {
        @Test
        @DisplayName("Accepts trimmed case insensitive values")
        void acceptsTrimmedCaseInsensitiveValues()
        {
            assertTrue(Properties.parseBoolean(" true "));
            assertTrue(Properties.parseBoolean("TrUe"));
            assertFalse(Properties.parseBoolean(" FALSE "));
        }

        @Test
        @DisplayName("Rejects invalid values")
        void rejectsInvalidValues()
        {
            IllegalArgumentException ex =
                    assertThrows(IllegalArgumentException.class, () -> Properties.parseBoolean("YES"));

            assertTrue(ex.getMessage().contains("Use true/false"));
        }
    }
}
