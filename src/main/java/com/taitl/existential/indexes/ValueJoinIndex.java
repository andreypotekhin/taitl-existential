package com.taitl.existential.indexes;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.concrete.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Dynamic index optimized for a quick retrieval of two values V and W based on key functions K(V), K(W).
 * Conceptually, this allows to 'join' two value collections of types V and W by a shared key type (K),
 * and map from a value of one type to a matching value of the other type.
 * The index exposes 'left' and 'right' maps (Map<V, W>, Map<W, V>)
 * for matching from V to W and from W to V.
 * Internally, it uses two ValueIndex<K,V> and ValueIndex<K,W> to store and map the values.
 * The index is dynamic in the sense of allowing to change the key of any value without need to reinsert.
 * Note: null is not allowed as a key or as a value.
 *
 * @param <V>
 *            Left value type
 * @param <W>
 *            Right value type
 * @param <K>
 *            Shared key type
 */
public class ValueJoinIndex<V, W, K>
{
    protected ConcreteValueJoinIndex<V, W, K> concrete;

    public ValueJoinIndex(Function<V, K> getLeftKey, Function<W, K> getRightKey)
    {
        sane(getLeftKey, "getLeftKey");
        sane(getRightKey, "getRightKey");
        concrete = createConcrete(getLeftKey, getRightKey);
    }

    /**
     * Returns a dynamic read-only map from left values to matching right value.
     */
    public Map<V, W> left()
    {
        return concrete.left();
    }

    /**
     * Returns a dynamic read-only map from right values to matching left value.
     */
    public Map<W, V> right()
    {
        return concrete.right();
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

    public V getLeft(K key)
    {
        return concrete.getLeft(key);
    }

    public W getRight(K key)
    {
        return concrete.getRight(key);
    }

    public W getRightByLeft(V left)
    {
        return concrete.getRightByLeft(left);
    }

    public V getLeftByRight(W right)
    {
        return concrete.getLeftByRight(right);
    }

    public V addLeft(K key, V value)
    {
        return concrete.addLeft(key, value);
    }

    public V addLeft(V value)
    {
        return concrete.addLeft(value);
    }

    public void addAllLeft(Collection<? extends V> values)
    {
        concrete.addAllLeft(values);
    }

    public W addRight(K key, W value)
    {
        return concrete.addRight(key, value);
    }

    public W addRight(W value)
    {
        return concrete.addRight(value);
    }

    public void addAllRight(Collection<? extends W> values)
    {
        concrete.addAllRight(values);
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

    public void clear()
    {
        concrete.clear();
    }

    @SuppressWarnings("unchecked")
    protected ConcreteValueJoinIndex<V, W, K> createConcrete(Function<V, K> getLeftKey, Function<W, K> getRightKey)
    {
        return Creator.create(ConcreteValueJoinIndex.class,
                new Class<?>[] { Function.class, Function.class },
                getLeftKey,
                getRightKey);
    }
}
