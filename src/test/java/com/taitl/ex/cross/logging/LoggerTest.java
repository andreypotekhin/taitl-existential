package com.taitl.ex.cross.logging;

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
}
