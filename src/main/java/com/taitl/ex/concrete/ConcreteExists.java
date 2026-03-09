package com.taitl.ex.concrete;

import com.taitl.ex.common.helper.strings.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.expressions.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

public class ConcreteExists<T, K> implements Expression<T>, Predicate<T>
{
    Collection<T> coll;
    Set<T> set;
    Map<T, K> map;
    Predicate<T> vpredicate;
    BiPredicate<T, T> vbipredicate;
    Predicate<Collection<T>> cpredicate;
    BiPredicate<T, Collection<T>> cbipredicate;
    Predicate<Set<T>> spredicate;
    BiPredicate<T, Set<T>> sbipredicate;
    BiPredicate<T, K> mbipredicate;
    Transaction tran;
    String description;

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
        predicates += mbipredicate == null ? 0 : 1;
        predicates += vpredicate == null ? 0 : 1;
        predicates += vbipredicate == null ? 0 : 1;
        verify(predicates == 1, "Exactly one predicate must be specified.");
    }

    /* Implement Expression interface */

    public Object evaluate(T t) throws ExistentialException
    {
        if (!test(t))
        {
            throw new PredicateFailure(description());
        }
        return null;
    }

    public String description()
    {
        return Descriptions.text(description);
    }

    /* Implement Predicate interface */

    public boolean test(T entity)
    {
        if (coll != null)
        {
            return testOnCollection(entity);
        }
        if (set != null)
        {
            return testOnSet(entity);
        }
        if (map != null)
        {
            return testOnMap(entity);
        }
        throw new IllegalStateException("Neither coll, set nor map is defined");
    }

    protected boolean testOnCollection(T entity)
    {
        sane(entity, "entity");
        cool(coll, "coll");
        verify(set == null && map == null, "Only one of coll, set, or map fields can be set.");
        if (cpredicate != null)
        {
            Collection<T> matching = findCollectionMatches(entity);
            return cpredicate.test(matching);
        }
        if (cbipredicate != null)
        {
            Collection<T> matching = findCollectionMatches(entity);
            return cbipredicate.test(entity, matching);
        }
        if (vpredicate != null)
        {
            for (T value : coll)
            {
                if (entity.equals(value) && vpredicate.test(value))
                {
                    return true;
                }
            }
            return false;
        }
        if (vbipredicate != null)
        {
            for (T value : coll)
            {
                if (entity.equals(value) && vbipredicate.test(entity, value))
                {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    protected boolean testOnSet(T entity)
    {
        sane(entity, "entity");
        cool(set, "set");
        verify(coll == null && map == null, "Only one of coll, set, or map fields can be set.");
        T match = findSetMatch(entity);
        Set<T> matching = match == null ? Set.of() : Set.of(match);
        if (spredicate != null)
        {
            return spredicate.test(matching);
        }
        if (sbipredicate != null)
        {
            return sbipredicate.test(entity, matching);
        }
        if (vpredicate != null)
        {
            return match != null && vpredicate.test(match);
        }
        if (vbipredicate != null)
        {
            return match != null && vbipredicate.test(entity, match);
        }
        return false;
    }

    protected boolean testOnMap(T entity)
    {
        sane(entity, "entity");
        cool(map, "map");
        verify(coll == null && set == null, "Only one of coll, set, or map fields can be set.");
        T match = findMapKeyMatch(entity);
        if (cpredicate != null)
        {
            Collection<T> matching = match == null ? List.of() : List.of(match);
            return cpredicate.test(matching);
        }
        if (cbipredicate != null)
        {
            Collection<T> matching = match == null ? List.of() : List.of(match);
            return cbipredicate.test(entity, matching);
        }
        if (vpredicate != null)
        {
            return match != null && vpredicate.test(match);
        }
        if (mbipredicate != null)
        {
            return match != null && mbipredicate.test(match, map.get(match));
        }
        return false;
    }

    private Collection<T> findCollectionMatches(T entity)
    {
        List<T> matching = new ArrayList<>();
        for (T value : coll)
        {
            if (entity.equals(value))
            {
                matching.add(value);
            }
        }
        return matching;
    }

    private T findSetMatch(T entity)
    {
        if (!set.contains(entity))
        {
            return null;
        }
        for (T value : set)
        {
            if (entity.equals(value))
            {
                return value;
            }
        }
        return null;
    }

    private T findMapKeyMatch(T entity)
    {
        if (!map.containsKey(entity))
        {
            return null;
        }
        for (T key : map.keySet())
        {
            if (entity.equals(key))
            {
                return key;
            }
        }
        return null;
    }
}
