package com.taitl.ex.concrete;

import com.taitl.existential.configs.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

public class ConcreteExistsBuilder<V>
{
    Collection<V> coll;
    Map<V, ?> map;
    Stream<V> stream; // TODO
    Predicate<Collection<V>> cpredicate;
    BiPredicate<Collection<V>, Transaction> cbipredicate;
    Predicate<Map<V, ?>> mpredicate;
    BiPredicate<Map<V, ?>, Transaction> mbipredicate;
    Predicate<V> vpredicate;
    BiPredicate<V, Transaction> vbipredicate;
    Transaction transaction;
    String description;

    public ConcreteExists<V> build()
    {
        validate();
        ConcreteExists<V> result = new ConcreteExists<>();
        result.coll = coll;
        result.map = map;
        result.cpredicate = cpredicate;
        result.cbipredicate = cbipredicate;
        result.mpredicate = mpredicate;
        result.mbipredicate = mbipredicate;
        result.vpredicate = vpredicate;
        result.vbipredicate = vbipredicate;
        result.tran = transaction;
        result.description = description;
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

    public ConcreteExistsBuilder<V> map(Map<V, ?> map)
    {
        sane(map, "map");
        this.map = map;
        return this;
    }

    public ConcreteExistsBuilder<V> transaction(Transaction tran)
    {
        sane(tran, "tran");
        this.transaction = tran;
        return this;
    }

    public ConcreteExistsBuilder<V> description(String description)
    {
        sane(description, "description");
        this.description = description;
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

    public ConcreteExistsBuilder<V> mpredicate(Predicate<Map<V, ?>> predicate)
    {
        sane(coll, "coll", predicate, "predicate");
        this.mpredicate = predicate;
        return this;
    }

    public ConcreteExistsBuilder<V> mbipredicate(BiPredicate<Map<V, ?>, Transaction> bipredicate)
    {
        sane(coll, "coll", bipredicate, "bipredicate");
        this.mbipredicate = bipredicate;
        return this;
    }
}
