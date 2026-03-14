package com.taitl.ex.concrete;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Backing implementation for {@link com.taitl.existential.indexes.SingleJoin}.
 */
public class ConcreteSingleJoin<V, W, K>
{
    protected static final String TROUBLESHOOTING_SECTION = "/Troubleshooting.md#index-key-mismatch";
    protected Map<K, V> leftByKey = new LinkedHashMap<>();
    protected Map<K, W> rightByKey = new LinkedHashMap<>();
    protected Map<V, W> rightByLeft = new LinkedHashMap<>();
    protected Map<W, V> leftByRight = new LinkedHashMap<>();

    protected final Function<V, K> getLeftKey;
    protected final Function<W, K> getRightKey;

    public ConcreteSingleJoin(Function<V, K> getLeftKey, Function<W, K> getRightKey)
    {
        sane(getLeftKey, "getLeftKey");
        sane(getRightKey, "getRightKey");
        this.getLeftKey = getLeftKey;
        this.getRightKey = getRightKey;
    }

    public Map<V, W> left()
    {
        return rightByLeft;
    }

    public Map<W, V> right()
    {
        return leftByRight;
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
            removeLeft(oldValue);
            return;
        }
        sane(oldValue, "oldValue", newValue, "newValue");
        K oldKey = getLeftKey.apply(oldValue);
        K newKey = getLeftKey.apply(newValue);
        synchronized (this)
        {
            removeLeftExact(oldKey, oldValue);
            leftByKey.put(newKey, newValue);
            rebuildViews();
        }
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
            removeRight(oldValue);
            return;
        }
        sane(oldValue, "oldValue", newValue, "newValue");
        K oldKey = getRightKey.apply(oldValue);
        K newKey = getRightKey.apply(newValue);
        synchronized (this)
        {
            removeRightExact(oldKey, oldValue);
            rightByKey.put(newKey, newValue);
            rebuildViews();
        }
    }

    public V getLeft(K key)
    {
        sane(key, "key");
        return leftByKey.get(key);
    }

    public W getRight(K key)
    {
        sane(key, "key");
        return rightByKey.get(key);
    }

    public W getRightByLeft(V left)
    {
        sane(left, "left");
        return rightByLeft.get(left);
    }

    public V getLeftByRight(W right)
    {
        sane(right, "right");
        return leftByRight.get(right);
    }

    public boolean containsLeft(Object value)
    {
        sane(value, "value");
        return containsLeftValue(value);
    }

    public boolean containsRight(Object value)
    {
        sane(value, "value");
        return containsRightValue(value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Object value)
    {
        sane(value, "value");
        try
        {
            V left = (V) value;
            K key = getLeftKey.apply(left);
            V current = leftByKey.get(key);
            if (current != null && current.equals(left))
            {
                return (T) rightByKey.get(key);
            }
        }
        catch (ClassCastException ignored)
        {
            // Value is not a left-side type.
        }
        try
        {
            W right = (W) value;
            K key = getRightKey.apply(right);
            W current = rightByKey.get(key);
            if (current != null && current.equals(right))
            {
                return (T) leftByKey.get(key);
            }
        }
        catch (ClassCastException ignored)
        {
            // Value is not a right-side type.
        }
        return null;
    }

    public V addLeft(K key, V value)
    {
        sane(key, "key");
        sane(value, "value");
        synchronized (this)
        {
            V result = leftByKey.put(key, value);
            rebuildViews();
            return result;
        }
    }

    public W addRight(K key, W value)
    {
        sane(key, "key");
        sane(value, "value");
        synchronized (this)
        {
            W result = rightByKey.put(key, value);
            rebuildViews();
            return result;
        }
    }

    public V addLeft(V value)
    {
        sane(value, "value");
        return addLeft(getLeftKey.apply(value), value);
    }

    public void addAllLeft(Collection<? extends V> values)
    {
        sane(values, "values");
        for (V value : values)
        {
            addLeft(value);
        }
    }

    public W addRight(W value)
    {
        sane(value, "value");
        return addRight(getRightKey.apply(value), value);
    }

    public void addAllRight(Collection<? extends W> values)
    {
        sane(values, "values");
        for (W value : values)
        {
            addRight(value);
        }
    }

    public V removeLeft(K key, V value)
    {
        sane(key, "key");
        sane(value, "value");
        synchronized (this)
        {
            V removed = removeLeftExact(key, value);
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
            W removed = removeRightExact(key, value);
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
        K key = getLeftKey.apply(value);
        if (!newKey.equals(key))
        {
            throw new IllegalArgumentException(String.format(
                    "Argument 'newKey' value '%s' does not match key value '%s' returned by left 'getKey'"
                            + " function. See " + TROUBLESHOOTING_SECTION,
                    newKey, key));
        }
        synchronized (this)
        {
            V removed = removeLeftExact(oldKey, value);
            if (removed != null)
            {
                leftByKey.put(newKey, value);
            }
            rebuildViews();
        }
    }

    public void reindexRight(K oldKey, K newKey, W value)
    {
        sane(oldKey, "oldKey");
        sane(newKey, "newKey");
        sane(value, "value");
        K key = getRightKey.apply(value);
        if (!newKey.equals(key))
        {
            throw new IllegalArgumentException(String.format(
                    "Argument 'newKey' value '%s' does not match key value '%s' returned by right 'getKey'"
                            + " function. See " + TROUBLESHOOTING_SECTION,
                    newKey, key));
        }
        synchronized (this)
        {
            W removed = removeRightExact(oldKey, value);
            if (removed != null)
            {
                rightByKey.put(newKey, value);
            }
            rebuildViews();
        }
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

    protected V removeLeftExact(K key, V value)
    {
        V current = leftByKey.get(key);
        if (current == null || !current.equals(value))
        {
            return null;
        }
        leftByKey.remove(key);
        return current;
    }

    protected W removeRightExact(K key, W value)
    {
        W current = rightByKey.get(key);
        if (current == null || !current.equals(value))
        {
            return null;
        }
        rightByKey.remove(key);
        return current;
    }

    @SuppressWarnings("unchecked")
    protected boolean containsLeftValue(Object value)
    {
        try
        {
            V left = (V) value;
            K key = getLeftKey.apply(left);
            V current = leftByKey.get(key);
            return current != null && current.equals(left);
        }
        catch (ClassCastException ignored)
        {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    protected boolean containsRightValue(Object value)
    {
        try
        {
            W right = (W) value;
            K key = getRightKey.apply(right);
            W current = rightByKey.get(key);
            return current != null && current.equals(right);
        }
        catch (ClassCastException ignored)
        {
            return false;
        }
    }

    protected void rebuildViews()
    {
        rightByLeft.clear();
        leftByRight.clear();

        for (Map.Entry<K, V> entry : leftByKey.entrySet())
        {
            K key = entry.getKey();
            W right = rightByKey.get(key);
            if (right == null)
            {
                continue;
            }
            V left = entry.getValue();
            rightByLeft.put(left, right);
            leftByRight.put(right, left);
        }
    }
}
