package com.taitl.existential.indexes;

import com.taitl.existential.*;
import com.taitl.ex.logic.types.*;
import com.taitl.ex.common.creator.*;
import com.taitl.existential.keys.*;
import java.util.function.*;
import static com.taitl.ex.common.helper.Args.*;

/**
 * Immutable, typed declaration of a transaction-local index or join.
 * Register a definition in a context or transaction, then resolve its existing facade shape
 * with Tr.index(definition). Definitions share type/extractor metadata, never entity contents.
 * Single-valued definitions replace the previous value at a key without later resurrection;
 * multi-valued definitions retain distinct object identities. Class forms are for non-generic
 * subjects; TypeKey forms retain explicit generic matching information.
 *
 * @param <I> bound index facade type
 */
public final class IndexDefinition<I>
{
    /** Declared lookup cardinality and number of subject sides. */
    public enum Shape
    {
        /** One current value per key. */
        SINGLE,
        /** All distinct enrolled identities per key. */
        MULTI,
        /** One current value on each side of a shared key. */
        SINGLE_JOIN,
        /** Groups of distinct enrolled identities on both sides of a shared key. */
        MULTI_JOIN
    }

    private final String name;
    private final Shape shape;
    private final TypeKey<?> leftType;
    private final TypeKey<?> rightType;
    private final Function<Object, Object> leftKey;
    private final Function<Object, Object> rightKey;
    private final IndexingPolicy policy;

    private IndexDefinition(String name, Shape shape, TypeKey<?> leftType, TypeKey<?> rightType,
            Function<Object, Object> leftKey, Function<Object, Object> rightKey, IndexingPolicy policy)
    {
        sane(name, "name", shape, "shape", leftType, "leftType", leftKey, "leftKey", policy, "policy");
        if (name.isBlank())
        {
            throw new IllegalArgumentException("Managed index name must not be blank");
        }
        TypeMatchingLogic matching = Creator.create(TypeMatchingLogic.class);
        matching.validateRuleType(leftType);
        if (rightType != null)
        {
            matching.validateRuleType(rightType);
        }
        this.name = name;
        this.shape = shape;
        this.leftType = leftType;
        this.rightType = rightType;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.policy = policy;
    }

    /**
     * Declares a single-valued index using an explicit subject Class.
     *
     * @param <K> lookup key type
     * @param <V> indexed subject type
     * @param name nonblank diagnostic name, unique among effective definitions
     * @param type explicit subject metadata
     * @param key pure key extractor returning a stable non-null key
     * @return immutable token to register and resolve through Tr.index
     * @throws IllegalArgumentException if metadata is missing, blank, or unsupported
     */
    public static <K, V> IndexDefinition<SingleIndex<K, V>> single(
            String name, Class<V> type, Function<V, K> key)
    {
        return single(name, new TypeKey<>(type), key);
    }

    /**
     * Declares a single-valued index using an explicit subject TypeKey.
     *
     * @param <K> lookup key type
     * @param <V> indexed subject type
     * @param name nonblank diagnostic name, unique among effective definitions
     * @param type explicit subject metadata
     * @param key pure key extractor returning a stable non-null key
     * @return immutable token to register and resolve through Tr.index
     * @throws IllegalArgumentException if metadata is missing, blank, or unsupported
     */
    public static <K, V> IndexDefinition<SingleIndex<K, V>> single(
            String name, TypeKey<V> type, Function<V, K> key)
    {
        sane(key, "key");
        return new IndexDefinition<>(name, Shape.SINGLE, type, null,
                value -> key.apply(cast(value)), null, IndexingPolicy.inherit());
    }

    /**
     * Declares a multi-valued index using an explicit subject Class.
     *
     * @param <K> lookup key type
     * @param <V> indexed subject type
     * @param name nonblank diagnostic name, unique among effective definitions
     * @param type explicit subject metadata
     * @param key pure key extractor returning a stable non-null key
     * @return immutable token to register and resolve through Tr.index
     * @throws IllegalArgumentException if metadata is missing, blank, or unsupported
     */
    public static <K, V> IndexDefinition<MultiIndex<K, V>> multi(
            String name, Class<V> type, Function<V, K> key)
    {
        return multi(name, new TypeKey<>(type), key);
    }

    /**
     * Declares a multi-valued index using an explicit subject TypeKey.
     *
     * @param <K> lookup key type
     * @param <V> indexed subject type
     * @param name nonblank diagnostic name, unique among effective definitions
     * @param type explicit subject metadata
     * @param key pure key extractor returning a stable non-null key
     * @return immutable token to register and resolve through Tr.index
     * @throws IllegalArgumentException if metadata is missing, blank, or unsupported
     */
    public static <K, V> IndexDefinition<MultiIndex<K, V>> multi(
            String name, TypeKey<V> type, Function<V, K> key)
    {
        sane(key, "key");
        return new IndexDefinition<>(name, Shape.MULTI, type, null,
                value -> key.apply(cast(value)), null, IndexingPolicy.inherit());
    }

    /**
     * Declares a SingleJoin using explicit subject Class metadata for both sides.
     *
     * @param <K> lookup key type
     * @param <V> indexed subject type
     * @param <W> related subject type
     * @param name nonblank diagnostic name, unique among effective definitions
     * @param left explicit left subject metadata
     * @param right explicit right subject metadata
     * @param leftKey pure left key extractor returning a stable non-null key
     * @param rightKey pure right key extractor returning a stable non-null key
     * @return immutable token to register and resolve through Tr.index
     * @throws IllegalArgumentException if metadata is missing, blank, or unsupported
     */
    public static <V, W, K> IndexDefinition<SingleJoin<V, W, K>> singleJoin(
            String name, Class<V> left, Class<W> right, Function<V, K> leftKey, Function<W, K> rightKey)
    {
        return singleJoin(name, new TypeKey<>(left), new TypeKey<>(right), leftKey, rightKey);
    }

    /**
     * Declares a SingleJoin using explicit subject TypeKey metadata for both sides.
     *
     * @param <K> lookup key type
     * @param <V> indexed subject type
     * @param <W> related subject type
     * @param name nonblank diagnostic name, unique among effective definitions
     * @param left explicit left subject metadata
     * @param right explicit right subject metadata
     * @param leftKey pure left key extractor returning a stable non-null key
     * @param rightKey pure right key extractor returning a stable non-null key
     * @return immutable token to register and resolve through Tr.index
     * @throws IllegalArgumentException if metadata is missing, blank, or unsupported
     */
    public static <V, W, K> IndexDefinition<SingleJoin<V, W, K>> singleJoin(
            String name, TypeKey<V> left, TypeKey<W> right, Function<V, K> leftKey, Function<W, K> rightKey)
    {
        sane(right, "right", leftKey, "leftKey", rightKey, "rightKey");
        return new IndexDefinition<>(name, Shape.SINGLE_JOIN, left, right,
                value -> leftKey.apply(cast(value)), value -> rightKey.apply(cast(value)), IndexingPolicy.inherit());
    }

    /**
     * Declares a MultiJoin using explicit subject Class metadata for both sides.
     *
     * @param <K> lookup key type
     * @param <V> indexed subject type
     * @param <W> related subject type
     * @param name nonblank diagnostic name, unique among effective definitions
     * @param left explicit left subject metadata
     * @param right explicit right subject metadata
     * @param leftKey pure left key extractor returning a stable non-null key
     * @param rightKey pure right key extractor returning a stable non-null key
     * @return immutable token to register and resolve through Tr.index
     * @throws IllegalArgumentException if metadata is missing, blank, or unsupported
     */
    public static <V, W, K> IndexDefinition<MultiJoin<V, W, K>> multiJoin(
            String name, Class<V> left, Class<W> right, Function<V, K> leftKey, Function<W, K> rightKey)
    {
        return multiJoin(name, new TypeKey<>(left), new TypeKey<>(right), leftKey, rightKey);
    }

    /**
     * Declares a MultiJoin using explicit subject TypeKey metadata for both sides.
     *
     * @param <K> lookup key type
     * @param <V> indexed subject type
     * @param <W> related subject type
     * @param name nonblank diagnostic name, unique among effective definitions
     * @param left explicit left subject metadata
     * @param right explicit right subject metadata
     * @param leftKey pure left key extractor returning a stable non-null key
     * @param rightKey pure right key extractor returning a stable non-null key
     * @return immutable token to register and resolve through Tr.index
     * @throws IllegalArgumentException if metadata is missing, blank, or unsupported
     */
    public static <V, W, K> IndexDefinition<MultiJoin<V, W, K>> multiJoin(
            String name, TypeKey<V> left, TypeKey<W> right, Function<V, K> leftKey, Function<W, K> rightKey)
    {
        sane(right, "right", leftKey, "leftKey", rightKey, "rightKey");
        return new IndexDefinition<>(name, Shape.MULTI_JOIN, left, right,
                value -> leftKey.apply(cast(value)), value -> rightKey.apply(cast(value)), IndexingPolicy.inherit());
    }

    /**
     * Returns a new declaration with index-level overrides; register and access that returned token.
     *
     * @param overrides independent index policy overrides
     * @return new definition identity; the original remains unchanged
     */
    public IndexDefinition<I> withPolicy(IndexingPolicy overrides)
    {
        return new IndexDefinition<>(name, shape, leftType, rightType, leftKey, rightKey, overrides);
    }

    /**
     * Returns the diagnostic name, unique among distinct definitions in an active transaction.
     *
     * @return nonblank name
     */
    public String name()
    {
        return name;
    }

    /**
     * Returns the declared facade shape.
     *
     * @return lookup cardinality and sides
     */
    public Shape shape()
    {
        return shape;
    }

    /**
     * Returns this declaration's independent policy overrides.
     *
     * @return immutable override policy
     */
    public IndexingPolicy policy()
    {
        return policy;
    }

    /**
     * Returns the explicit subject metadata for one side; a simple index has only a left side.
     *
     * @param right selects the right join side when true
     * @return declared type, or null for an absent right side
     */
    public TypeKey<?> type(boolean right)
    {
        return right ? rightType : leftType;
    }

    /**
     * Extracts a declared side's key; used by transaction binding after subject matching.
     *
     * @param right selects the right join side when true
     * @param value matching subject instance; a selected right side must exist
     * @return extracted lookup key
     */
    public Object key(boolean right, Object value)
    {
        return (right ? rightKey : leftKey).apply(value);
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object value)
    {
        return (T) value;
    }
}
