package com.taitl.ex.common.helper;

import java.util.function.Predicate;

/**
 * Predicate helpers.
 */
public class Predicates
{
    protected static final Predicate<Object> TRUTH = value -> true;

    /**
     * Protected constructor for an utility class.
     */
    protected Predicates()
    {
    }

    /**
     * Returns a predicate that always evaluates to true.
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> truth()
    {
        return (Predicate<T>) TRUTH;
    }
}
