package com.taitl.existential.indexes;

/**
 * Composition of indexes that provides an Index-like interface over multiple indexes.
 * Convenience for adding a document to multiple indexes with a single call.
 *
 * <pre>
 * {@code
 * indexes.add(doc1); // same as calling .add() on each underlying index
 * }
 * </pre>
 *
 * @see Index
 */
public class Indexes
{
    // TODO:
    // addIndex(Index<?, ?> index)
    // T add(<T> elem)
}
