package com.taitl.existential.indexes;

/**
 * Composition of indexes with Index-like interface to multiple indexes.
 * Convenience for adding a document to multiple indexes with a single call.
 * Example:
 * <pre>
 * {@code
 * indexes.add(doc1) <-- same as calling .add() on each underlying index
 * }
 * </pre><p>
 *
 * @see Index
 */
public class Indexes
{
    // TODO:
    // addIndex(Index<?, ?> index)
    // T add(<T> elem)
}
