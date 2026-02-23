package com.taitl.ex.common.helper;

/**
 * Utilities for slash-separated path keys.
 */
public class PathSupport
{
    /**
     * Protected constructor for an utility class.
     */
    protected PathSupport()
    {
    }

    public static boolean hasParent(String path)
    {
        return path.lastIndexOf('/') != 0;
    }

    public static String parentOrThrow(String path, String label)
    {
        if (!hasParent(path))
        {
            throw new IllegalStateException(String.format("%s '%s' has no parent key", label, path));
        }
        return path.substring(0, path.lastIndexOf('/'));
    }
}
