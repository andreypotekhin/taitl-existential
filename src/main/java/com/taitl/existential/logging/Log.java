package com.taitl.existential.logging;

import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
public final class Log
{
    public enum LogLevel
    {
        LEVEL_TRACE, LEVEL_DEBUG, LEVEL_INFO, LEVEL_WARNING, LEVEL_ERROR
    };

    private static Logger logger = new Logger();
    private static LogLevel logLevel = LogLevel.LEVEL_WARNING;

    public static void factory(Supplier<Logger> factory)
    {
        if (factory == null)
        {
            throw new IllegalArgumentException("Argument 'factory' must not be null");
        }
        logger = factory.get();
    }

    public boolean isTracing()
    {
        return logLevel.compareTo(LogLevel.LEVEL_TRACE) <= 0;
    }

    public boolean isDebug()
    {
        return logLevel.compareTo(LogLevel.LEVEL_DEBUG) <= 0;
    }

    public static void error(Class clz, String format, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_ERROR) <= 0)
        {
            logger.log(LogLevel.LEVEL_ERROR, clz, format, message, keyValuePairs);
        }
    }

    public static void error(Class clz, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_ERROR) >= 0)
        {
            logger.log(LogLevel.LEVEL_ERROR, clz, null, message, keyValuePairs);
        }
    }

    public static void warn(Class clz, String format, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_WARNING) <= 0)
        {
            logger.log(LogLevel.LEVEL_WARNING, clz, format, message, keyValuePairs);
        }
    }

    public static void warn(Class clz, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_WARNING) <= 0)
        {
            logger.log(LogLevel.LEVEL_WARNING, clz, null, message, keyValuePairs);
        }
    }

    public static void info(Class clz, String format, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_INFO) <= 0)
        {
            logger.log(LogLevel.LEVEL_INFO, clz, format, message, keyValuePairs);
        }
    }

    public static void info(Class clz, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_INFO) <= 0)
        {
            logger.log(LogLevel.LEVEL_INFO, clz, null, message, keyValuePairs);
        }
    }

    public static void debug(Class clz, String format, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_DEBUG) <= 0)
        {
            logger.log(LogLevel.LEVEL_DEBUG, clz, format, message, keyValuePairs);
        }
    }

    public static void debug(Class clz, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_DEBUG) <= 0)
        {
            logger.log(LogLevel.LEVEL_DEBUG, clz, null, message, keyValuePairs);
        }
    }

    public static void trace(Class clz, String format, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_TRACE) <= 0)
        {
            logger.log(LogLevel.LEVEL_TRACE, clz, format, message, keyValuePairs);
        }
    }

    public static void trace(Class clz, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_TRACE) <= 0)
        {
            logger.log(LogLevel.LEVEL_TRACE, clz, null, message, keyValuePairs);
        }
    }
}
