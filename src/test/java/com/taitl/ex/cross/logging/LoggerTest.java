package com.taitl.ex.cross.logging;

import com.taitl.ex.cross.logging.Log.LogLevel;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class LoggerTest
{
    static class TestLogger extends Logger
    {
        String sanitizePublic(Object value)
        {
            return sanitize(value);
        }

        void logPublic(LogLevel level, Class clz, String format, String message, Object... keyValuePairs)
        {
            log(level, clz, format, message, keyValuePairs);
        }
    }

    TestLogger logger;

    @BeforeEach
    void setup()
    {
        logger = new TestLogger();
    }

    @Nested
    class Sanitize
    {
        @Test
        @DisplayName("Escapes control characters")
        void escapesControlCharacters()
        {
            String input = "start\nmid\rend\t\u001b[31mX\u007f";
            String output = logger.sanitizePublic(input);
            assertEquals("start\\nmid\\rend\\u0009\\u001b[31mX\\u007f", output);
        }
    }

    @Nested
    class LogMessage
    {
        @Test
        @DisplayName("Rejects odd key-value pairs")
        void rejectsOddKeyValuePairs()
        {
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> {
                logger.logPublic(LogLevel.LEVEL_INFO, LoggerTest.class, null, "message", "key");
            });
            assertEquals("Argument 'keyValuePairs' must be of even length", error.getMessage());
        }
    }
}
