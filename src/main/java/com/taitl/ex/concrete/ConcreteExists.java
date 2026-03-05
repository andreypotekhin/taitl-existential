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
    Map<T, K> map;
    Predicate<T> vpredicate;
    BiPredicate<T, T> vbipredicate;
    Predicate<Collection<T>> cpredicate;
    BiPredicate<Collection<T>, Transaction> cbipredicate;
    BiPredicate<T, K> mbipredicate;
    Transaction tran;
    String description;

    void validate()
    {
        verify(coll != null || map != null, "Either coll or map must be provided.");
        verify(coll == null || map == null, "Coll or map must be provided, but not both.");
        verify(cpredicate != null || cbipredicate != null || mbipredicate != null
                || vpredicate != null || vbipredicate != null,
                "At least one predicate must be provided.");
        verify(cpredicate == null || cbipredicate == null || mbipredicate == null
                || vpredicate == null || vbipredicate == null,
                "Only one predicate should be specified.");
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

    /**
     * Tests if the predicate holds for given transaction.
     *
     * @return True if predicate holds
     */
    public boolean test(T entity)
    {
        if (coll != null)
        {
            return testOnColl(entity);
        }
        else if (map != null)
        {
            return testOnMap(entity);
        }
        else
        {
            throw new IllegalStateException("Neither collection nor set is defined");
        }
    }

    protected boolean testOnColl(T entity)
    {
        sane(entity, "entity");
        cool(coll, "coll");
        verify(map == null, "Only one of coll or map fields can be set, not both.");
        if (cpredicate != null)
        {
            Collection<T> matching = new ArrayList<>();
            for (T v : coll)
            {
                if (entity.equals(v))
                {
                    matching.add(v);
                }
            }
            return cpredicate.test(matching);
        }
        if (cbipredicate != null)
        {
            cool(tran, "tran");
            Collection<T> matching = new ArrayList<>();
            for (T v : coll)
            {
                if (entity.equals(v))
                {
                    matching.add(v);
                }
            }
            return cbipredicate.test(matching, tran);
        }
        if (vpredicate != null)
        {
            for (T v : coll)
            {
                if (entity.equals(v) && vpredicate.test(v))
                {
                    return true;
                }
            }
            return false;
        }
        if (vbipredicate != null)
        {
            for (T v : coll)
            {
                if (entity.equals(v) && vbipredicate.test(entity, v))
                {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    protected boolean testOnMap(T entity)
    {
        sane(entity, "entity");
        cool(map, "map");
        verify(coll == null, "Only one of coll or map fields can be set, not both.");
        if (cpredicate != null)
        {
            Collection<T> matching = new ArrayList<>();
            for (T v : map.keySet())
            {
                if (entity.equals(v))
                {
                    matching.add(v);
                }
            }
            return cpredicate.test(matching);
        }
        if (cbipredicate != null)
        {
            cool(tran, "tran");
            Collection<T> matching = new ArrayList<>();
            for (T v : map.keySet())
            {
                if (entity.equals(v))
                {
                    matching.add(v);
                }
            }
            return cbipredicate.test(matching, tran);
        }
        if (vpredicate != null)
        {
            for (T v : map.keySet())
            {
                if (entity.equals(v) && vpredicate.test(v))
                {
                    return true;
                }
            }
            return false;
        }
        if (mbipredicate != null)
        {
            for (Map.Entry<T, K> entry : map.entrySet())
            {
                T key = entry.getKey();
                if (entity.equals(key) && mbipredicate.test(key, entry.getValue()))
                {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
