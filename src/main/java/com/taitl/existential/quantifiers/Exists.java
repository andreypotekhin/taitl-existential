package com.taitl.existential.quantifiers;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.concrete.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.expressions.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Existential quantifier over a collection, evaluated in the scope of a transaction.
 * Used within invariants to assert that a predicate holds for at least one value.
 *
 * @param <V>
 *            Element type in the examined collection
 */
public class Exists<V> implements Expression<V>
{
    ConcreteExists<V> concrete;

    /**
     * Builds a collection-based exists predicate.
     *
     * @param coll
     *            Collection to scan
     * @param predicate
     *            Predicate that must hold for at least one element
     */
    // TODO: add predicate-less variant
    // TODO: add description parameter
    public Exists(Collection<V> coll, Predicate<V> predicate)
    {
        sane(coll, "coll", predicate, "predicate");
        concrete = createBuilder()
                .coll(coll)
                .predicate(predicate)
                .build();
    }

    /**
     * Builds a collection-based exists predicate that depends on transaction state.
     *
     * @param coll
     *            Collection to scan
     * @param bipredicate
     *            Predicate that inspects the element and transaction
     */
    // TODO: add description parameter
    public Exists(Collection<V> coll, BiPredicate<V, Transaction> bipredicate)
    {
        sane(coll, "coll", bipredicate, "bipredicate");
        concrete = createBuilder()
                .coll(coll)
                .bipredicate(bipredicate)
                .build();
    }

    /**
     * Builds a collection-level exists predicate.
     * The placeholder parameter disambiguates constructor overloads.
     *
     * @param coll
     *            Collection to scan
     * @param predicate
     *            Predicate applied to the collection
     * @param placeholder
     *            Placeholder value used only for overload resolution
     */
    // TODO: add description parameter
    public Exists(Collection<V> coll, Predicate<Collection<V>> predicate, int placeholder)
    {
        sane(coll, "coll", predicate, "predicate");
        concrete = createBuilder()
                .coll(coll)
                .cpredicate(predicate)
                .build();
    }

    /**
     * Builds a transaction-aware collection-level exists predicate.
     * The placeholder parameter disambiguates constructor overloads.
     *
     * @param coll
     *            Collection to scan
     * @param bipredicate
     *            Predicate applied to the collection and transaction
     * @param placeholder
     *            Placeholder value used only for overload resolution
     */
    // TODO: add description parameter
    public Exists(Collection<V> coll, BiPredicate<Collection<V>, Transaction> bipredicate, int placeholder)
    {
        sane(coll, "coll", bipredicate, "bipredicate");
        concrete = createBuilder()
                .coll(coll)
                .cbipredicate(bipredicate)
                .build();
    }

    /**
     * Builds a map-based exists predicate.
     *
     * @param map
     *            Map to scan
     * @param predicate
     *            Predicate that must hold for at least one element
     */
    // TODO: add description parameter
    public <K> Exists(Map<V, K> map, Predicate<V> predicate)
    {
        sane(map, "map", predicate, "predicate");
        concrete = createBuilder()
                .map(map)
                .predicate(predicate)
                .build();
    }

    /**
     * Builds a map-based exists predicate that depends on transaction state.
     *
     * @param map
     *            Map to scan
     * @param bipredicate
     *            Predicate that inspects the element and transaction
     */
    // TODO: add description parameter
    public <K> Exists(Map<V, K> map, BiPredicate<V, Transaction> bipredicate)
    {
        sane(map, "map", bipredicate, "bipredicate");
        concrete = createBuilder()
                .map(map)
                .bipredicate(bipredicate)
                .build();
    }

    /**
     * Builds a map-based exists predicate.
     * The placeholder parameter disambiguates constructor overloads.
     *
     * @param map
     *            Map to scan
     * @param predicate
     *            Predicate applied to the collection
     * @param placeholder
     *            Placeholder value used only for overload resolution
     */
    // TODO: add description parameter
    public <K> Exists(Map<V, K> map, Predicate<Collection<V>> predicate, int placeholder)
    {
        sane(map, "map", predicate, "predicate");
        concrete = createBuilder()
                .map(map)
                .cpredicate(predicate)
                .build();
    }

    /**
     * Builds a transaction-aware map-based exists predicate.
     * The placeholder parameter disambiguates constructor overloads.
     *
     * @param map
     *            Map to scan
     * @param bipredicate
     *            Predicate applied to the collection and transaction
     * @param placeholder
     *            Placeholder value used only for overload resolution
     */
    // TODO: add description parameter
    public <K> Exists(Map<V, K> map, BiPredicate<Collection<V>, Transaction> bipredicate, int placeholder)
    {
        sane(map, "map", bipredicate, "bipredicate");
        concrete = createBuilder()
                .map(map)
                .cbipredicate(bipredicate)
                .build();
    }

    /* Implement Expression */

    public Object evaluate(V entity) throws ExistentialException
    {
        return concrete.evaluate(entity);
    }

    public String description()
    {
        return concrete.description();
    }

    /* Implement Predicate */

    /**
     * Tests if the predicate holds for the given entity.
     *
     * @param entity Entity object
     * @return True if predicate holds
     */
    public boolean test(V entity)
    {
        return concrete.test(entity);
    }

    @SuppressWarnings("unchecked")
    ConcreteExistsBuilder<V> createBuilder()
    {
        return Creator.create(ConcreteExistsBuilder.class);
    }
}
