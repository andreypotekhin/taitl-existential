package com.taitl.ex.common.helper.strings;

import com.taitl.ex.common.helper.*;

/**
 * Lightweight helpers for working with text values.
 */
public class Text
{
    /**
     * Protected constructor for a utility class.
     */
    protected Text()
    {
    }

    /**
     * Trims a non-null string.
     *
     * @param value   String to trim
     * @param argName Argument name for error reporting
     * @return Trimmed string
     */
    public static String trimmed(String value, String argName)
    {
        Args.sane(value, argName);
        return value.trim();
    }
}
