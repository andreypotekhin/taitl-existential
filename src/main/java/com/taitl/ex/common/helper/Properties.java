package com.taitl.ex.common.helper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Lightweight helpers for {@link java.util.Properties}.
 */
public class Properties
{
    /**
     * Protected constructor for a utility class.
     */
    protected Properties()
    {
    }

    /**
     * Loads a properties document from the input stream.
     */
    public static java.util.Properties load(InputStream stream) throws IOException
    {
        Args.sane(stream, "stream");
        java.util.Properties props = new java.util.Properties();
        props.load(stream);
        return props;
    }

    /**
     * Loads a properties document while capping input size.
     */
    public static java.util.Properties load(InputStream stream, long maxBytes) throws IOException
    {
        Args.sane(stream, "stream");
        Args.check(maxBytes >= 0, "Argument 'maxBytes' must be >= 0");
        return load(new LimitedInputStream(stream, maxBytes));
    }

    /**
     * Parses a boolean value from a properties string.
     */
    public static boolean parseBoolean(String value)
    {
        Args.sane(value, "value");
        String trimmed = value.trim();
        if ("true".equalsIgnoreCase(trimmed))
        {
            return true;
        }
        if ("false".equalsIgnoreCase(trimmed))
        {
            return false;
        }
        throw new IllegalArgumentException(
                String.format("Invalid boolean value '%s'. Use true/false", value));
    }
}
