package com.taitl.ex.cross.logging;

import java.io.*;
import com.taitl.ex.cross.logging.Log.*;

/**
 * Default logger for Log class.
 */
@SuppressWarnings("rawtypes")
public class Logger
{
    protected PrintStream out = System.out;
    protected PrintStream err = System.err;
    protected static final String NEWLINE_ESCAPED = "\\n";
    protected static final String CARRIAGE_ESCAPED = "\\r";
    protected static final String CONTROL_PREFIX = "\\u00";
    protected static final char[] HEX = "0123456789abcdef".toCharArray();

    /**
     * Outputs a log message of the specified class and level. Messages of LEVEL_ERROR
     * are output to std err, all other messages to std out.
     * Override this method to customize behavior.
     *
     * Q: Why 'format' parameter does not affect the output of key-value pairs?
     * A: Because we do not know in advance how many key-value pairs will be provided.
     *
     * @param level
     * @param clz
     * @param format
     * @param message
     * @param keyValuePairs
     */
    protected void log(LogLevel level, Class clz, String format, String message, Object... keyValuePairs)
    {
        if (message == null)
        {
            throw new IllegalArgumentException("Argument 'message' must not be null");
        }
        if (keyValuePairs != null && keyValuePairs.length % 2 != 0)
        {
            throw new IllegalArgumentException("Argument 'keyValuePairs' must be of even length");
        }
        PrintStream outStream = out;
        if (level == LogLevel.LEVEL_ERROR)
        {
            outStream = err;
        }
        StringBuilder output = new StringBuilder();
        String sanitizedMessage = sanitize(message);
        if (clz != null)
        {
            output.append(clz.getName()).append(' ');
        }
        if (format != null)
        {
            output.append(formatMessage(format, sanitizedMessage));
        }
        else
        {
            output.append(sanitizedMessage);
        }
        if (keyValuePairs != null && keyValuePairs.length > 0)
        {
            output.append(" {");
            for (int i = 0; i < keyValuePairs.length; i += 2)
            {
                output.append(' ')
                        .append(sanitize(keyValuePairs[i]))
                        .append('=')
                        .append(sanitize(keyValuePairs[i + 1]))
                        .append("; ");
            }
            output.append("}");
        }
        outStream.print(output.toString());
    }

    protected String formatMessage(String format, String message)
    {
        try
        {
            return String.format(format, message);
        }
        catch (java.util.IllegalFormatException ignored)
        {
            return message;
        }
    }

    protected String sanitize(Object value)
    {
        String rendered = String.valueOf(value);
        if (rendered.isEmpty())
        {
            return rendered;
        }
        StringBuilder sanitized = null;
        for (int i = 0; i < rendered.length(); i++)
        {
            char ch = rendered.charAt(i);
            String replacement = null;
            if (ch == '\r')
            {
                replacement = CARRIAGE_ESCAPED;
            }
            else if (ch == '\n')
            {
                replacement = NEWLINE_ESCAPED;
            }
            else if (ch < 0x20 || ch == 0x7f)
            {
                replacement = toControlEscape(ch);
            }
            if (replacement != null)
            {
                if (sanitized == null)
                {
                    sanitized = new StringBuilder(rendered.length() + 8);
                    sanitized.append(rendered, 0, i);
                }
                sanitized.append(replacement);
            }
            else if (sanitized != null)
            {
                sanitized.append(ch);
            }
        }
        return sanitized == null ? rendered : sanitized.toString();
    }

    protected String toControlEscape(char ch)
    {
        return CONTROL_PREFIX + HEX[(ch >> 4) & 0x0f] + HEX[ch & 0x0f];
    }
}
