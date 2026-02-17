package com.taitl.existential.quantifiers;

import java.util.*;
import java.util.function.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.concrete.*;
import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.Args.*;

public class Exists<V> implements Predicate<Transaction>
{
    ConcreteExists<V> concrete;

    public Exists(Collection<V> coll, Predicate<V> predicate)
    {
        sane(coll, "coll", predicate, "predicate");
        concrete = createBuilder()
                .coll(coll)
                .predicate(predicate)
                .build();
    }

    public Exists(Collection<V> coll, BiPredicate<V, Transaction> bipredicate)
    {
        sane(coll, "coll", bipredicate, "bipredicate");
        concrete = createBuilder()
                .coll(coll)
                .bipredicate(bipredicate)
                .build();
    }

    public Exists(Collection<V> coll, Predicate<Collection<V>> predicate, int placeholder)
    {
        sane(coll, "coll", predicate, "predicate");
        concrete = createBuilder()
                .coll(coll)
                .cpredicate(predicate)
                .build();
    }

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
     * Tests if the predicate holds for given transaction.
     *
     * @param tran Transaction object
     * @return True if predicate holds
     */
    public boolean test(Transaction tran)
    {
        return concrete.test(tran);
    }

    @SuppressWarnings("unchecked")
    ConcreteExistsBuilder<V> createBuilder()
    {
        return Creator.create(ConcreteExistsBuilder.class);
    }
}
