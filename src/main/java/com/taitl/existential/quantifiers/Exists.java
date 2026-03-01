package com.taitl.existential.quantifiers;

import java.util.*;
import java.util.function.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.concrete.*;
import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Existential quantifier over a collection, evaluated in the scope of a transaction.
 * Used within invariants to assert that a predicate holds for at least one value.
 *
 * @param <V>
 *            Element type in the examined collection
 */
public class Exists<V> implements Predicate<Transaction>
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
    public Exists(Collection<V> coll, BiPredicate<Collection<V>, Transaction> bipredicate, int placeholder)
    {
        sane(coll, "coll", bipredicate, "bipredicate");
        concrete = createBuilder()
                .coll(coll)
                .cbipredicate(bipredicate)
                .build();
    }

    /* Implement Predicate */

    /**
     * Tests if the predicate holds for the given transaction.
     *
     * @param transaction
     *            Transaction object
     * @return True if predicate holds
     */
    public boolean test(Transaction transaction)
    {
        return concrete.test(transaction);
    }

    @SuppressWarnings("unchecked")
    ConcreteExistsBuilder<V> createBuilder()
    {
        return Creator.create(ConcreteExistsBuilder.class);
    }
}
