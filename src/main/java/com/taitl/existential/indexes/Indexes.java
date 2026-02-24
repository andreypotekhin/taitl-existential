package com.taitl.existential.indexes;

/**
 * Composition of indexes that provides a single facade over multiple indexes.
 * Useful when the same value should be added to every index in one call.
 * Example: indexes.add(doc1) (equivalent to calling add on each index).
 *
 * @see Index
 */
public class Indexes
{
    // TODO:
    // addIndex(Index<?, ?> index)
    // T add(<T> elem)
}
