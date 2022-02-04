package com.taitl.existential;

import java.io.PrintStream;

import com.taitl.existential.Log.LogLevel;

/**
 * Implements default logger for use by Log class.
 * 
 * @author Andrey Potekhin
 *
 */
@SuppressWarnings("rawtypes")
public class Logger
{
    protected PrintStream out = System.out;
    protected PrintStream err = System.err;

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
            throw new IllegalArgumentException("Argument 'keyValuePairs' must have even number of elements");
        }
        PrintStream outStream = out;
        if (level == LogLevel.LEVEL_ERROR)
        {
            outStream = err;
        }
        StringBuffer output = new StringBuffer();
        if (clz != null)
        {
            output.append(clz.getName() + " ");
        }
        if (format != null)
        {
            output.append(String.format(format, message));
        }
        else
        {
            output.append(message);
        }
        if (keyValuePairs != null && keyValuePairs.length > 0)
        {
            output.append(" {");
            for (int i = 0; i < keyValuePairs.length; i += 2)
            {
                output.append(" " + keyValuePairs[i] + "=" + keyValuePairs[i + 1] + "; ");
            }
            output.append("}");
        }
        outStream.print(output.toString());
    }
}
