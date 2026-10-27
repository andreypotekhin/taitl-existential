package com.taitl.ex.logic.managed_indexes.views;

import com.taitl.ex.logic.managed_indexes.data.*;
import com.taitl.existential.indexes.*;
import java.util.*;
import java.util.function.*;
import static com.taitl.ex.common.helper.Args.*;

/**
 * Transaction-bound SingleJoin facade backed by independent key groups.
 * Relationship projections resolve opposite groups on demand and never materialize a
 * Cartesian product of entities sharing one join key.
 */
public class ManagedSingleJoin<V, W, K> extends SingleJoin<V, W, K>
{
    private final ManagedSide<K, V> left;
    private final ManagedSide<K, W> right;
    private final ManagedAccess access;
    private final Map<V, W> leftView;
    private final Map<W, V> rightView;

    public ManagedSingleJoin(ManagedSide<K, V> left, ManagedSide<K, W> right, ManagedAccess access)
    {
        super(value -> left.extract(value), value -> right.extract(value));
        this.left = left;
        this.right = right;
        this.access = access;
        leftView = new ManagedQueryMap<>(access, value -> {
            K key = left.key(value);
            return key == null ? null : right.single(key);
        }, () -> left.values());
        rightView = new ManagedQueryMap<>(access, value -> {
            K key = right.key(value);
            return key == null ? null : left.single(key);
        }, () -> right.values());
    }

    public Map<V, W> left()
    {
        access.check();
        return leftView;
    }

    public Map<W, V> right()
    {
        access.check();
        return rightView;
    }

    public W getRightByLeft(V value)
    {
        return leftView.get(value);
    }

    public V getLeftByRight(W value)
    {
        return rightView.get(value);
    }

    public V getLeft(K key)
    {
        return left.single(key);
    }

    public boolean containsLeft(Object value)
    {
        return left.contains(value);
    }

    public void indexLeft(V before, V after)
    {
        left.index(before, after);
    }

    public V addLeft(K key, V value)
    {
        V previous = left.single(key);
        left.accept(key, value);
        return previous;
    }

    public V addLeft(V value)
    {
        return addLeft(left.extract(value), value);
    }

    public void addAllLeft(Collection<? extends V> values)
    {
        left.addAll(values);
    }

    public V removeLeft(K key, V value)
    {
        return left.remove(key, value);
    }

    public V removeLeft(V value)
    {
        return left.remove(value);
    }

    public void reindexLeft(K oldKey, K newKey, V value)
    {
        left.reindex(oldKey, newKey, value);
    }

    public W getRight(K key)
    {
        return right.single(key);
    }

    public boolean containsRight(Object value)
    {
        return right.contains(value);
    }

    public void indexRight(W before, W after)
    {
        right.index(before, after);
    }

    public W addRight(K key, W value)
    {
        W previous = right.single(key);
        right.accept(key, value);
        return previous;
    }

    public W addRight(W value)
    {
        return addRight(right.extract(value), value);
    }

    public void addAllRight(Collection<? extends W> values)
    {
        right.addAll(values);
    }

    public W removeRight(K key, W value)
    {
        return right.remove(key, value);
    }

    public W removeRight(W value)
    {
        return right.remove(value);
    }

    public void reindexRight(K oldKey, K newKey, W value)
    {
        right.reindex(oldKey, newKey, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Object value)
    {
        access.check();
        return (T) (left.contains(value) ? leftView.get(value) : rightView.get(value));
    }

    public void clear()
    {
        left.clear();
        right.clear();
    }
}
