package com.taitl.existential.indexes;

import com.taitl.ex.common.helper.collections.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Joins two value collections of types V and W by a shared key type (K).
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
// Todo: delegate to ConcreteJoinIndex
public class JoinIndex<V, W, K>
{
    protected static final String TROUBLESHOOTING_SECTION = "/Troubleshooting.md#index-key-mismatch";
    protected static final String ARG_LEFT_KEY_VALUE = "Argument 'newKey' value '%s' does not match key value '%s'"
            + " returned by left 'getKey' function. See " + TROUBLESHOOTING_SECTION;
    protected static final String ARG_RIGHT_KEY_VALUE = "Argument 'newKey' value '%s' does not match key value '%s'"
            + " returned by right 'getKey' function. See " + TROUBLESHOOTING_SECTION;

    protected SetMap<K, V> leftByKey = new SetMap<>();
    protected SetMap<K, W> rightByKey = new SetMap<>();
    protected SetMap<V, W> rightByLeft = new SetMap<>();
    protected SetMap<W, V> leftByRight = new SetMap<>();

    protected Function<V, K> getLeftKey;
    protected Function<W, K> getRightKey;

    public JoinIndex()
    {
    }

    public JoinIndex(Function<V, K> getLeftKey, Function<W, K> getRightKey)
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
        return rightByLeft.get(left);
    }

    public Set<V> getLeftByRight(W right)
    {
        sane(right, "right");
        return leftByRight.get(right);
    }

    /**
     * Returns a dynamic read-only map from left values to matching right values.
     */
    public Map<V, Set<W>> left()
    {
        return rightByLeft;
    }

    /**
     * Returns a dynamic read-only map from right values to matching left values.
     */
    public Map<W, Set<V>> right()
    {
        return leftByRight;
    }

    public Set<V> addLeft(K key, V value)
    {
        sane(key, "key");
        sane(value, "value");
        synchronized (this)
        {
            Set<V> result = leftByKey.add(key, value);
            rebuildViews();
            return result;
        }
    }

    public Set<W> addRight(K key, W value)
    {
        sane(key, "key");
        sane(value, "value");
        synchronized (this)
        {
            Set<W> result = rightByKey.add(key, value);
            rebuildViews();
            return result;
        }
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
        synchronized (this)
        {
            V removed = leftByKey.removeValue(key, value);
            if (removed != null)
            {
                rebuildViews();
            }
            return removed;
        }
    }

    public V removeLeft(V value)
    {
        sane(value, "value");
        return removeLeft(getLeftKey.apply(value), value);
    }

    public W removeRight(K key, W value)
    {
        sane(key, "key");
        sane(value, "value");
        synchronized (this)
        {
            W removed = rightByKey.removeValue(key, value);
            if (removed != null)
            {
                rebuildViews();
            }
            return removed;
        }
    }

    public W removeRight(W value)
    {
        sane(value, "value");
        return removeRight(getRightKey.apply(value), value);
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
            V removed = leftByKey.removeValue(oldKey, value);
            if (removed != null)
            {
                leftByKey.add(newKey, value);
            }
            rebuildViews();
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
            W removed = rightByKey.removeValue(oldKey, value);
            if (removed != null)
            {
                rightByKey.add(newKey, value);
            }
            rebuildViews();
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

    /**
     * Indexes left value.
     * This method tolerates nulls in oldValue and newValue parameters.
     * When oldValue is null, this method interprets adds newValue to left index.
     * When newValue is null, this method removes the oldValue from left index.
     */
    public void indexLeft(V oldValue, V newValue)
    {
        if (oldValue == null)
        {
            addLeft(newValue);
            return;
        }
        if (newValue == null)
        {
            removeLeft(newValue);
            return;
        }
        sane(oldValue, "oldValue", newValue, "value");
        K oldKey = getLeftKey.apply(oldValue);
        K newKey = getLeftKey.apply(newValue);
        reindexLeft(oldKey, newKey, newValue);
    }

    /**
     * Indexes right value.
     * This method tolerates nulls in oldValue and newValue parameters.
     * When oldValue is null, this method interprets adds newValue to right index.
     * When newValue is null, this method removes the oldValue from right index.
     */
    public void indexRight(W oldValue, W newValue)
    {
        if (oldValue == null)
        {
            addRight(newValue);
            return;
        }
        if (newValue == null)
        {
            removeRight(newValue);
            return;
        }
        sane(oldValue, "oldValue", newValue, "value");
        K oldKey = getRightKey.apply(oldValue);
        K newKey = getRightKey.apply(newValue);
        reindexRight(oldKey, newKey, newValue);
    }

    public void clear()
    {
        synchronized (this)
        {
            leftByKey.clear();
            rightByKey.clear();
            rightByLeft.clear();
            leftByRight.clear();
        }
    }

    protected void rebuildViews()
    {
        rightByLeft.clear();
        leftByRight.clear();

        for (Map.Entry<K, Set<V>> entry : leftByKey.entrySet())
        {
            K key = entry.getKey();
            Set<W> right = rightByKey.get(key);
            if (right == null || right.isEmpty())
            {
                continue;
            }

            for (V leftValue : entry.getValue())
            {
                for (W rightValue : right)
                {
                    rightByLeft.add(leftValue, rightValue);
                    leftByRight.add(rightValue, leftValue);
                }
            }
        }
    }
}
