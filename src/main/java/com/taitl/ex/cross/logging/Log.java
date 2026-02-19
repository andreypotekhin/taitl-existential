package com.taitl.ex.cross.logging;

import java.util.function.*;

import static com.taitl.ex.common.helper.Args.sane;

@SuppressWarnings("rawtypes")
public final class Log
{
    public enum LogLevel
    {
        LEVEL_TRACE, LEVEL_DEBUG, LEVEL_INFO, LEVEL_WARNING, LEVEL_ERROR
    }

    private static final Supplier<Logger> FACTORY = Logger::new;
    private static Supplier<Logger> factory = FACTORY;
    private static LogLevel logLevel = LogLevel.LEVEL_WARNING;
    private static volatile boolean loggerInitialized;

    private static final class InstanceHolder
    {
        private static final Logger logger = createLogger();
    }

    public static void factory(Supplier<Logger> factory)
    {
        sane(factory, "factory");
        synchronized (Log.class)
        {
            if (loggerInitialized)
            {
                throw new IllegalStateException("Logger factory cannot be changed after logger initialization");
            }
            Log.factory = factory;
        }
    }

    private static Logger logger()
    {
        return InstanceHolder.logger;
    }

    private static Logger createLogger()
    {
        Logger logger = factory.get();
        if (logger == null)
        {
            throw new IllegalArgumentException("Logger factory must not return null");
        }
        loggerInitialized = true;
        return logger;
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
