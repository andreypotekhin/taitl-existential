package com.taitl.ex.concrete;

import com.taitl.existential.configs.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

public class ConcreteExistsBuilder<V, K>
{
    Collection<V> coll;
    Set<V> set;
    Map<V, K> map;
    Predicate<Collection<V>> cpredicate;
    BiPredicate<V, Collection<V>> cbipredicate;
    Predicate<Set<V>> spredicate;
    BiPredicate<V, Set<V>> sbipredicate;
    Predicate<V> vpredicate;
    BiPredicate<V, V> vbipredicate;
    BiPredicate<V, K> mbipredicate;
    Transaction transaction;
    String description;

    public ConcreteExists<V, K> build()
    {
        validate();
        ConcreteExists<V, K> result = new ConcreteExists<>();
        result.coll = coll;
        result.set = set;
        result.map = map;
        result.cpredicate = cpredicate;
        result.cbipredicate = cbipredicate;
        result.spredicate = spredicate;
        result.sbipredicate = sbipredicate;
        result.vpredicate = vpredicate;
        result.vbipredicate = vbipredicate;
        result.mbipredicate = mbipredicate;
        result.tran = transaction;
        result.description = description;
        result.validate();
        return result;
    }

    void validate()
    {
        int sources = 0;
        sources += coll == null ? 0 : 1;
        sources += set == null ? 0 : 1;
        sources += map == null ? 0 : 1;
        verify(sources == 1, "Exactly one source (coll, set, or map) must be provided.");

        int predicates = 0;
        predicates += cpredicate == null ? 0 : 1;
        predicates += cbipredicate == null ? 0 : 1;
        predicates += spredicate == null ? 0 : 1;
        predicates += sbipredicate == null ? 0 : 1;
        predicates += vpredicate == null ? 0 : 1;
        predicates += vbipredicate == null ? 0 : 1;
        predicates += mbipredicate == null ? 0 : 1;
        verify(predicates == 1, "Exactly one predicate must be specified.");
    }

    public ConcreteExistsBuilder<V, K> coll(Collection<V> coll)
    {
        sane(coll, "coll");
        this.coll = coll;
        return this;
    }

    public ConcreteExistsBuilder<V, K> set(Set<V> set)
    {
        sane(set, "set");
        this.set = set;
        return this;
    }

    public ConcreteExistsBuilder<V, K> map(Map<V, K> map)
    {
        sane(map, "map");
        this.map = map;
        return this;
    }

    public ConcreteExistsBuilder<V, K> transaction(Transaction tran)
    {
        sane(tran, "tran");
        this.transaction = tran;
        return this;
    }

    public ConcreteExistsBuilder<V, K> description(String description)
    {
        sane(description, "description");
        this.description = description;
        return this;
    }

    public ConcreteExistsBuilder<V, K> predicate(Predicate<V> predicate)
    {
        sane(predicate, "predicate");
        this.vpredicate = predicate;
        return this;
    }

    public ConcreteExistsBuilder<V, K> bipredicate(BiPredicate<V, V> bipredicate)
    {
        sane(bipredicate, "bipredicate");
        this.vbipredicate = bipredicate;
        return this;
    }

    public ConcreteExistsBuilder<V, K> mbipredicate(BiPredicate<V, K> bipredicate)
    {
        verify(map != null, "Map must be provided before map bipredicate assignment.");
        sane(bipredicate, "bipredicate");
        this.mbipredicate = bipredicate;
        return this;
    }

    public ConcreteExistsBuilder<V, K> cpredicate(Predicate<Collection<V>> predicate)
    {
        verify(coll != null || map != null, "Either coll or map must be provided before predicate assignment.");
        sane(predicate, "predicate");
        this.cpredicate = predicate;
        return this;
    }

    public ConcreteExistsBuilder<V, K> cbipredicate(BiPredicate<V, Collection<V>> bipredicate)
    {
        verify(coll != null || map != null, "Either coll or map must be provided before predicate assignment.");
        sane(bipredicate, "bipredicate");
        this.cbipredicate = bipredicate;
        return this;
    }

    public ConcreteExistsBuilder<V, K> spredicate(Predicate<Set<V>> predicate)
    {
        verify(set != null, "Set must be provided before set predicate assignment.");
        sane(predicate, "predicate");
        this.spredicate = predicate;
        return this;
    }

    public ConcreteExistsBuilder<V, K> sbipredicate(BiPredicate<V, Set<V>> bipredicate)
    {
        verify(set != null, "Set must be provided before set bipredicate assignment.");
        sane(bipredicate, "bipredicate");
        this.sbipredicate = bipredicate;
        return this;
    }
}
