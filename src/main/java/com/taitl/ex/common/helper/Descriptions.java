package com.taitl.ex.common.helper;

/**
 * Utilities for optional human-friendly descriptions.
 */
public class Descriptions
{
    /**
     * Protected constructor for an utility class.
     */
    protected Descriptions()
    {
    }

    /**
     * Returns the description or an empty string when absent.
     */
    public static String text(String description)
    {
        return description == null ? "" : description;
    }

    /**
     * Appends description to base message if present.
     */
    public static String message(String base, String description)
    {
        if (description == null || description.isEmpty())
        {
            return base;
        }
        return base + ": " + description;
    }
}
