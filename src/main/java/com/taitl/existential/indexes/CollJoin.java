package com.taitl.existential.indexes;

import com.taitl.ex.common.helper.collections.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Joins two value collections (V and W) by a shared key type (K).
 * Stores V and W values in separate {@link SetMap} indexes and allows querying either side
 * through the other side's key.
 *
 * @param <V>
 *            Left value type
 * @param <W>
 *            Right value type
 * @param <K>
 *            Shared key type
 */
public class CollJoin<V, W, K>
{
    protected static final String TROUBLESHOOTING_SECTION = "/Troubleshooting.md#index-key-mismatch";
    protected static final String ARG_LEFT_KEY_VALUE = "Argument 'newKey' value '%s' does not match key value '%s'"
            + " returned by left 'getKey' function. See " + TROUBLESHOOTING_SECTION;
    protected static final String ARG_RIGHT_KEY_VALUE = "Argument 'newKey' value '%s' does not match key value '%s'"
            + " returned by right 'getKey' function. See " + TROUBLESHOOTING_SECTION;

    protected SetMap<K, V> leftByKey = new SetMap<>();
    protected SetMap<K, W> rightByKey = new SetMap<>();

    protected Function<V, K> getLeftKey;
    protected Function<W, K> getRightKey;

    public CollJoin()
    {
    }

    public CollJoin(Function<V, K> getLeftKey, Function<W, K> getRightKey)
    {
        sane(getLeftKey, "getLeftKey");
        sane(getRightKey, "getRightKey");
        this.getLeftKey = getLeftKey;
        this.getRightKey = getRightKey;
    }

    public Set<V> getLeft(K key)
    {
        sane(key, "key");
        return leftByKey.get(key);
    }

    public Set<W> getRight(K key)
    {
        sane(key, "key");
        return rightByKey.get(key);
    }

    public Set<W> getRightByLeft(V left)
    {
        sane(left, "left");
        verify(getLeftKey != null, "You need to call 'setGetLeftKey()' first");
        return getRight(getLeftKey.apply(left));
    }

    public Set<V> getLeftByRight(W right)
    {
        sane(right, "right");
        verify(getRightKey != null, "You need to call 'setGetRightKey()' first");
        return getLeft(getRightKey.apply(right));
    }

    public Set<V> addLeft(K key, V value)
    {
        sane(key, "key");
        sane(value, "value");
        return leftByKey.put(key, value);
    }

    public Set<W> addRight(K key, W value)
    {
        sane(key, "key");
        sane(value, "value");
        return rightByKey.put(key, value);
    }

    public Set<V> addLeft(V value)
    {
        sane(value, "value");
        verify(getLeftKey != null, "You need to call 'setGetLeftKey()' first");
        return addLeft(getLeftKey.apply(value), value);
    }

    public Set<W> addRight(W value)
    {
        sane(value, "value");
        verify(getRightKey != null, "You need to call 'setGetRightKey()' first");
        return addRight(getRightKey.apply(value), value);
    }

    public V removeLeft(K key, V value)
    {
        sane(key, "key");
        sane(value, "value");
        return leftByKey.remove(key, value);
    }

    public W removeRight(K key, W value)
    {
        sane(key, "key");
        sane(value, "value");
        return rightByKey.remove(key, value);
    }

    public void reindexLeft(K oldKey, K newKey, V value)
    {
        sane(oldKey, "oldKey");
        sane(newKey, "newKey");
        sane(value, "value");
        if (getLeftKey != null)
        {
            K key = getLeftKey.apply(value);
            if (!newKey.equals(key))
            {
                throw new IllegalArgumentException(String.format(ARG_LEFT_KEY_VALUE, newKey, key));
            }
        }
        synchronized (this)
        {
            removeLeft(oldKey, value);
            addLeft(newKey, value);
        }
    }

    public void reindexRight(K oldKey, K newKey, W value)
    {
        sane(oldKey, "oldKey");
        sane(newKey, "newKey");
        sane(value, "value");
        if (getRightKey != null)
        {
            K key = getRightKey.apply(value);
            if (!newKey.equals(key))
            {
                throw new IllegalArgumentException(String.format(ARG_RIGHT_KEY_VALUE, newKey, key));
            }
        }
        synchronized (this)
        {
            removeRight(oldKey, value);
            addRight(newKey, value);
        }
    }

    public void setGetLeftKey(Function<V, K> getLeftKey)
    {
        sane(getLeftKey, "getLeftKey");
        this.getLeftKey = getLeftKey;
    }

    public void setGetRightKey(Function<W, K> getRightKey)
    {
        sane(getRightKey, "getRightKey");
        this.getRightKey = getRightKey;
    }

    public void clear()
    {
        leftByKey.clear();
        rightByKey.clear();
    }
}
