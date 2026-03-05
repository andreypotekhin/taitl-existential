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
 * @param <T>
 *            Element type in the examined collection
 */
public class Exists<T> implements Expression<T>, Predicate<T>
{
    ConcreteExists<T, ?> concrete;

    /**
     * Builds a collection-based exists predicate that matches by identity.
     *
     * @param coll
     *            Collection to scan
     */
    public Exists(Collection<T> coll)
    {
        sane(coll, "coll");
        concrete = createBuilder()
                .coll(coll)
                .predicate(value -> true)
                .build();
    }

    /**
     * Builds a collection-based exists predicate that matches by identity.
     *
     * @param coll
     *            Collection to scan
     * @param description
     *            Human-friendly description used in violations
     */
    public Exists(Collection<T> coll, String description)
    {
        sane(coll, "coll", description, "description");
        concrete = createBuilder()
                .coll(coll)
                .predicate(value -> true)
                .description(description)
                .build();
    }

    /**
     * Builds a collection-based exists predicate.
     *
     * @param coll
     *            Collection to scan
     * @param predicate
     *            Predicate that must hold for at least one element
     */
    public Exists(Collection<T> coll, Predicate<T> predicate)
    {
        sane(coll, "coll", predicate, "predicate");
        concrete = createBuilder()
                .coll(coll)
                .predicate(predicate)
                .build();
    }

    /**
     * Builds a collection-based exists predicate.
     *
     * @param coll
     *            Collection to scan
     * @param predicate
     *            Predicate that must hold for at least one element
     * @param description
     *            Human-friendly description used in violations
     */
    public Exists(Collection<T> coll, Predicate<T> predicate, String description)
    {
        sane(coll, "coll", predicate, "predicate", description, "description");
        concrete = createBuilder()
                .coll(coll)
                .predicate(predicate)
                .description(description)
                .build();
    }

    /**
     * Builds a collection-based exists predicate that compares evaluated and matched values.
     *
     * @param coll
     *            Collection to scan
     * @param bipredicate
     *            Predicate that inspects the evaluated entity and the matching collection value
     */
    public Exists(Collection<T> coll, BiPredicate<T, T> bipredicate)
    {
        sane(coll, "coll", bipredicate, "bipredicate");
        concrete = createBuilder()
                .coll(coll)
                .bipredicate(bipredicate)
                .build();
    }

    /**
     * Builds a collection-based exists predicate that compares evaluated and matched values.
     *
     * @param coll
     *            Collection to scan
     * @param bipredicate
     *            Predicate that inspects the evaluated entity and the matching collection value
     * @param description
     *            Human-friendly description used in violations
     */
    public Exists(Collection<T> coll, BiPredicate<T, T> bipredicate, String description)
    {
        sane(coll, "coll", bipredicate, "bipredicate", description, "description");
        concrete = createBuilder()
                .coll(coll)
                .bipredicate(bipredicate)
                .description(description)
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
    public Exists(Collection<T> coll, Predicate<Collection<T>> predicate, int placeholder)
    {
        sane(coll, "coll", predicate, "predicate");
        concrete = createBuilder()
                .coll(coll)
                .cpredicate(predicate)
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
     * @param description
     *            Human-friendly description used in violations
     */
    public Exists(Collection<T> coll, Predicate<Collection<T>> predicate, int placeholder, String description)
    {
        sane(coll, "coll", predicate, "predicate", description, "description");
        concrete = createBuilder()
                .coll(coll)
                .cpredicate(predicate)
                .description(description)
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
    public Exists(Collection<T> coll, BiPredicate<Collection<T>, Transaction> bipredicate, int placeholder)
    {
        sane(coll, "coll", bipredicate, "bipredicate");
        concrete = createBuilder()
                .coll(coll)
                .cbipredicate(bipredicate)
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
     * @param description
     *            Human-friendly description used in violations
     */
    public Exists(Collection<T> coll, BiPredicate<Collection<T>, Transaction> bipredicate, int placeholder,
            String description)
    {
        sane(coll, "coll", bipredicate, "bipredicate", description, "description");
        concrete = createBuilder()
                .coll(coll)
                .cbipredicate(bipredicate)
                .description(description)
                .build();
    }

    /**
     * Builds a map-based exists predicate that matches by identity.
     *
     * @param map
     *            Map to scan
     */
    public <D> Exists(Map<T, D> map)
    {
        sane(map, "map");
        concrete = this.<D> createBuilder()
                .map(map)
                .predicate(value -> true)
                .build();
    }

    /**
     * Builds a map-based exists predicate that matches by identity.
     *
     * @param map
     *            Map to scan
     * @param description
     *            Human-friendly description used in violations
     */
    public <D> Exists(Map<T, D> map, String description)
    {
        sane(map, "map", description, "description");
        concrete = this.<D> createBuilder()
                .map(map)
                .predicate(value -> true)
                .description(description)
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
    public <D> Exists(Map<T, D> map, Predicate<T> predicate)
    {
        sane(map, "map", predicate, "predicate");
        concrete = this.<D> createBuilder()
                .map(map)
                .predicate(predicate)
                .build();
    }

    /**
     * Builds a map-based exists predicate.
     *
     * @param map
     *            Map to scan
     * @param predicate
     *            Predicate that must hold for at least one element
     * @param description
     *            Human-friendly description used in violations
     */
    public <D> Exists(Map<T, D> map, Predicate<T> predicate, String description)
    {
        sane(map, "map", predicate, "predicate", description, "description");
        concrete = this.<D> createBuilder()
                .map(map)
                .predicate(predicate)
                .description(description)
                .build();
    }

    /**
     * Builds a map-based exists predicate that depends on mapped values.
     *
     * @param map
     *            Map to scan
     * @param bipredicate
     *            Predicate that inspects the key and mapped value
     */
    public <D> Exists(Map<T, D> map, BiPredicate<T, D> bipredicate)
    {
        sane(map, "map", bipredicate, "bipredicate");
        concrete = this.<D> createBuilder()
                .map(map)
                .mbipredicate(bipredicate)
                .build();
    }

    /**
     * Builds a map-based exists predicate that depends on mapped values.
     *
     * @param map
     *            Map to scan
     * @param bipredicate
     *            Predicate that inspects the key and mapped value
     * @param description
     *            Human-friendly description used in violations
     */
    public <D> Exists(Map<T, D> map, BiPredicate<T, D> bipredicate, String description)
    {
        sane(map, "map", bipredicate, "bipredicate", description, "description");
        concrete = this.<D> createBuilder()
                .map(map)
                .mbipredicate(bipredicate)
                .description(description)
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
    public <D> Exists(Map<T, D> map, Predicate<Collection<T>> predicate, int placeholder)
    {
        sane(map, "map", predicate, "predicate");
        concrete = this.<D> createBuilder()
                .map(map)
                .cpredicate(predicate)
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
     * @param description
     *            Human-friendly description used in violations
     */
    public <D> Exists(Map<T, D> map, Predicate<Collection<T>> predicate, int placeholder, String description)
    {
        sane(map, "map", predicate, "predicate", description, "description");
        concrete = this.<D> createBuilder()
                .map(map)
                .cpredicate(predicate)
                .description(description)
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
    public <D> Exists(Map<T, D> map, BiPredicate<Collection<T>, Transaction> bipredicate, int placeholder)
    {
        sane(map, "map", bipredicate, "bipredicate");
        concrete = this.<D> createBuilder()
                .map(map)
                .cbipredicate(bipredicate)
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
     * @param description
     *            Human-friendly description used in violations
     */
    public <D> Exists(Map<T, D> map, BiPredicate<Collection<T>, Transaction> bipredicate, int placeholder,
            String description)
    {
        sane(map, "map", bipredicate, "bipredicate", description, "description");
        concrete = this.<D> createBuilder()
                .map(map)
                .cbipredicate(bipredicate)
                .description(description)
                .build();
    }

    /* Implement Expression interface */

    public Object evaluate(T entity) throws ExistentialException
    {
        return concrete.evaluate(entity);
    }

    public String description()
    {
        return concrete.description();
    }

    /* Implement Predicate interface */

    /**
     * Tests if the predicate holds for the given entity.
     *
     * @param entity Entity object
     * @return True if predicate holds
     */
    public boolean test(T entity)
    {
        return concrete.test(entity);
    }

    @SuppressWarnings("unchecked")
    <K> ConcreteExistsBuilder<T, K> createBuilder()
    {
        return Creator.create(ConcreteExistsBuilder.class);
    }
}
