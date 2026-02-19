package com.taitl.ex.cross.logging;

import java.util.function.*;

@SuppressWarnings("rawtypes")
public final class Log
{
    public enum LogLevel
    {
        LEVEL_TRACE, LEVEL_DEBUG, LEVEL_INFO, LEVEL_WARNING, LEVEL_ERROR
    }

    private static final Supplier<Logger> FACTORY = Logger::new;
    private static Supplier<Logger> loggerFactory = FACTORY;
    private static LogLevel logLevel = LogLevel.LEVEL_WARNING;

    private static final class InstanceHolder
    {
        private static volatile Logger logger;

        private InstanceHolder()
        {
        }
    }

    public static void factory(Supplier<Logger> factory)
    {
        if (factory == null)
        {
            throw new IllegalArgumentException("Argument 'factory' must not be null");
        }
        loggerFactory = factory;
        InstanceHolder.logger = null;
    }

    private static Logger logger()
    {
        Logger current = InstanceHolder.logger;
        if (current != null)
        {
            return current;
        }
        synchronized (InstanceHolder.class)
        {
            current = InstanceHolder.logger;
            if (current == null)
            {
                current = loggerFactory.get();
                if (current == null)
                {
                    throw new IllegalArgumentException("Logger factory must not return null");
                }
                InstanceHolder.logger = current;
            }
            return current;
        }
    }

    public static boolean isTracing()
    {
        return logLevel.compareTo(LogLevel.LEVEL_TRACE) <= 0;
    }

    public static boolean isDebug()
    {
        return logLevel.compareTo(LogLevel.LEVEL_DEBUG) <= 0;
    }

    public static void error(Class clz, String format, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_ERROR) <= 0)
        {
            logger().log(LogLevel.LEVEL_ERROR, clz, format, message, keyValuePairs);
        }
    }

    public static void error(Class clz, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_ERROR) <= 0)
        {
            logger().log(LogLevel.LEVEL_ERROR, clz, null, message, keyValuePairs);
        }
    }

    public static void warn(Class clz, String format, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_WARNING) <= 0)
        {
            logger().log(LogLevel.LEVEL_WARNING, clz, format, message, keyValuePairs);
        }
    }

    public static void warn(Class clz, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_WARNING) <= 0)
        {
            logger().log(LogLevel.LEVEL_WARNING, clz, null, message, keyValuePairs);
        }
    }

    public static void info(Class clz, String format, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_INFO) <= 0)
        {
            logger().log(LogLevel.LEVEL_INFO, clz, format, message, keyValuePairs);
        }
    }

    public static void info(Class clz, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_INFO) <= 0)
        {
            logger().log(LogLevel.LEVEL_INFO, clz, null, message, keyValuePairs);
        }
    }

    public static void debug(Class clz, String format, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_DEBUG) <= 0)
        {
            logger().log(LogLevel.LEVEL_DEBUG, clz, format, message, keyValuePairs);
        }
    }

    public static void debug(Class clz, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_DEBUG) <= 0)
        {
            logger().log(LogLevel.LEVEL_DEBUG, clz, null, message, keyValuePairs);
        }
    }

    public static void trace(Class clz, String format, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_TRACE) <= 0)
        {
            logger().log(LogLevel.LEVEL_TRACE, clz, format, message, keyValuePairs);
        }
    }

    public static void trace(Class clz, String message, Object... keyValuePairs)
    {
        if (logLevel.compareTo(LogLevel.LEVEL_TRACE) <= 0)
        {
            logger().log(LogLevel.LEVEL_TRACE, clz, null, message, keyValuePairs);
        }
    }
}
