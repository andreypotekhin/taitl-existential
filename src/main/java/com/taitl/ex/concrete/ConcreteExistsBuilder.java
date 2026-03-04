package com.taitl.ex.concrete;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

public class ConcreteExistsBuilder<V>
{
    Collection<V> coll;
    Stream<V> stream; // TODO
    Predicate<Collection<V>> cpredicate;
    BiPredicate<Collection<V>, Transaction> cbipredicate;
    Predicate<V> vpredicate;
    BiPredicate<V, Transaction> vbipredicate;

    public ConcreteExists<V> build()
    {
        validate();
        ConcreteExists<V> result = new ConcreteExists<>();
        result.coll = coll;
        result.cpredicate = cpredicate;
        result.cbipredicate = cbipredicate;
        result.vpredicate = vpredicate;
        result.vbipredicate = vbipredicate;
        return result;
    }

    void validate()
    {
        sane(coll, "coll");
        verify(cpredicate != null || cbipredicate != null || vpredicate != null || vbipredicate != null,
                "At least one predicate must be provided.");
    }

    public ConcreteExistsBuilder<V> coll(Collection<V> coll)
    {
        sane(coll, "coll");
        this.coll = coll;
        return this;
    }

    public ConcreteExistsBuilder<V> predicate(Predicate<V> predicate)
    {
        sane(predicate, "predicate");
        this.vpredicate = predicate;
        return this;
    }

    public ConcreteExistsBuilder<V> bipredicate(BiPredicate<V, Transaction> bipredicate)
    {
        sane(bipredicate, "bipredicate");
        this.vbipredicate = bipredicate;
        return this;
    }

    public ConcreteExistsBuilder<V> cpredicate(Predicate<Collection<V>> predicate)
    {
        sane(coll, "coll", predicate, "predicate");
        this.cpredicate = predicate;
        return this;
    }

    public ConcreteExistsBuilder<V> cbipredicate(BiPredicate<Collection<V>, Transaction> bipredicate)
    {
        sane(coll, "coll", bipredicate, "bipredicate");
        this.cbipredicate = bipredicate;
        return this;
    }
}
