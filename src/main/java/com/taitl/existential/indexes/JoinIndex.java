package com.taitl.existential.indexes;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.concrete.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Joins two value collections of types V and W by a shared key type (K).
 * Stores V and W values in separate {@link com.taitl.ex.common.helper.collections.SetMap} indexes
 * and allows querying either side
 * through the other side's key.
 *
 * @param <V>
 *            Left value type
 * @param <W>
 *            Right value type
 * @param <K>
 *            Shared key type
 */
public class JoinIndex<V, W, K>
{
    protected ConcreteJoinIndex<V, W, K> concrete;

    public JoinIndex()
    {
        concrete = createConcrete();
    }

    public JoinIndex(Function<V, K> getLeftKey, Function<W, K> getRightKey)
    {
        sane(getLeftKey, "getLeftKey");
        sane(getRightKey, "getRightKey");
        concrete = createConcrete();
        setGetLeftKey(getLeftKey);
        setGetRightKey(getRightKey);
    }

    public Set<V> getLeft(K key)
    {
        return concrete.getLeft(key);
    }

    public Set<W> getRight(K key)
    {
        return concrete.getRight(key);
    }

    public Set<W> getRightByLeft(V left)
    {
        return concrete.getRightByLeft(left);
    }

    public Set<V> getLeftByRight(W right)
    {
        return concrete.getLeftByRight(right);
    }

    /**
     * Returns a dynamic read-only map from left values to matching right values.
     */
    public Map<V, Set<W>> left()
    {
        return concrete.left();
    }

    /**
     * Returns a dynamic read-only map from right values to matching left values.
     */
    public Map<W, Set<V>> right()
    {
        return concrete.right();
    }

    public Set<V> addLeft(K key, V value)
    {
        return concrete.addLeft(key, value);
    }

    public Set<W> addRight(K key, W value)
    {
        return concrete.addRight(key, value);
    }

    public Set<V> addLeft(V value)
    {
        return concrete.addLeft(value);
    }

    public Set<W> addRight(W value)
    {
        return concrete.addRight(value);
    }

    public V removeLeft(K key, V value)
    {
        return concrete.removeLeft(key, value);
    }

    public V removeLeft(V value)
    {
        return concrete.removeLeft(value);
    }

    public W removeRight(K key, W value)
    {
        return concrete.removeRight(key, value);
    }

    public W removeRight(W value)
    {
        return concrete.removeRight(value);
    }

    public void reindexLeft(K oldKey, K newKey, V value)
    {
        concrete.reindexLeft(oldKey, newKey, value);
    }

    public void reindexRight(K oldKey, K newKey, W value)
    {
        concrete.reindexRight(oldKey, newKey, value);
    }

    public void setGetLeftKey(Function<V, K> getLeftKey)
    {
        concrete.setGetLeftKey(getLeftKey);
    }

    public void setGetRightKey(Function<W, K> getRightKey)
    {
        concrete.setGetRightKey(getRightKey);
    }

    /**
     * Indexes left value.
     * This method tolerates nulls in oldValue and newValue parameters.
     * When oldValue is null, this method interprets adds newValue to left index.
     * When newValue is null, this method removes the oldValue from left index.
     */
    public void indexLeft(V oldValue, V newValue)
    {
        concrete.indexLeft(oldValue, newValue);
    }

    /**
     * Indexes right value.
     * This method tolerates nulls in oldValue and newValue parameters.
     * When oldValue is null, this method interprets adds newValue to right index.
     * When newValue is null, this method removes the oldValue from right index.
     */
    public void indexRight(W oldValue, W newValue)
    {
        concrete.indexRight(oldValue, newValue);
    }

    public void clear()
    {
        concrete.clear();
    }

    @SuppressWarnings("unchecked")
    protected ConcreteJoinIndex<V, W, K> createConcrete()
    {
        return Creator.create(ConcreteJoinIndex.class);
    }
}
