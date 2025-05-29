package com.taitl.exlogic.unused.indexes;

/**
 * A composition of indexes which provides Index-like interface to multiple indexes.
 * Allows to add a document to multiple indexes with a single call to add() method.
 *
 * Example:
 * <pre>
 * {@code
 * indexes.addIndex(index1)
 * indexes.addIndex(index2)
 * ...
 * indexes.add(doc1) <-- same as calling .add() on each index
 * }
 * </pre><p>
 *
 * @author Andrey Potekhin
 *
 * @see Index
 */
public class Indexes
{
    // TODO:
    // addIndex(Index<?, ?> index)
    // T add(<T> elem)
}
