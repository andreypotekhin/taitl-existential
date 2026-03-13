package com.taitl.existential.indexes;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.concrete.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Dynamic index optimized for quick retrieval of two sets of values Set<V>, Set<W>
 * based on key functions K(V), K(W) (specified at construction time).
 * Conceptually, this allows to 'join' two collections of types V and W by a shared key type (K),
 * and 'map' from a value of one type to a set of matching values of the other type.
 * The index exposes 'left' and 'right' maps (Map<V, Set<W>>, Map<V, Set<W>>)
 * for matching from V to a set of W and from W to a set of V.
 * Internally, it uses a pair SetIndex<K,V> and SetIndex<K,W> to store and map the values.
 * The index is dynamic in the sense of allowing to change the key of any value stored in it
 * without need to remove and reinsert (you still need to call index()/reindex()).
 * Note: null is not allowed as a key or as a value.
 *
 * Usage:
 * join.addAllLeft(collV);
 * join.addAllRight(collW);
 * boolean containsV = join.containsLeft(v);
 * boolean containsW = join.containsRight(w);
 * Set<W> ws = join.get(v);
 * Set<V> vs = join.get(w);
 *
 * @param <V>
 *            Left value type
 * @param <W>
 *            Right value type
 * @param <K>
 *            Shared key type
 */
public class MultiJoin<V, W, K>
{
    protected ConcreteMultiJoin<V, W, K> concrete;

    public MultiJoin(Function<V, K> getLeftKey, Function<W, K> getRightKey)
    {
        sane(getLeftKey, "getLeftKey");
        sane(getRightKey, "getRightKey");
        concrete = createConcrete(getLeftKey, getRightKey);
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
     * Returns true if the provided value is present on the left side of the join.
     */
    public boolean containsLeft(Object value)
    {
        return concrete.containsLeft(value);
    }

    /**
     * Returns true if the provided value is present on the right side of the join.
     */
    public boolean containsRight(Object value)
    {
        return concrete.containsRight(value);
    }

    /**
     * Returns values matched by the same join key as the provided value.
     * If the value is on the left side, returns right-side values; if on the right side, returns left-side values.
     */
    public <T> Set<T> get(Object value)
    {
        return concrete.get(value);
    }

    public Set<V> addLeft(K key, V value)
    {
        return concrete.addLeft(key, value);
    }

    public Set<V> addLeft(V value)
    {
        return concrete.addLeft(value);
    }

    public void addAllLeft(Collection<? extends V> values)
    {
        concrete.addAllLeft(values);
    }

    public Set<W> addRight(K key, W value)
    {
        return concrete.addRight(key, value);
    }

    public Set<W> addRight(W value)
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
    protected ConcreteMultiJoin<V, W, K> createConcrete(Function<V, K> getLeftKey, Function<W, K> getRightKey)
    {
        return Creator.create(ConcreteMultiJoin.class,
                new Class<?>[] { Function.class, Function.class },
                getLeftKey,
                getRightKey);
    }
}
