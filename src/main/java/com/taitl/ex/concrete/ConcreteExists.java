package com.taitl.ex.concrete;

import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.expressions.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

public class ConcreteExists<V> implements Expression<V>
{
    Collection<V> coll;
    Map<V, ?> map;
    Predicate<V> vpredicate;
    BiPredicate<V, Transaction> vbipredicate;
    Predicate<Collection<V>> cpredicate;
    BiPredicate<Collection<V>, Transaction> cbipredicate;
    Predicate<Map<V, ?>> mpredicate;
    BiPredicate<Map<V, ?>, Transaction> mbipredicate;
    Transaction tran;
    String description;

    /* Implement Expression */

    public Object evaluate(V t) throws ExistentialException
    {
        if (!test(t))
        {
            throw new PredicateFailure(description());
        }
        return null;
    }

    public String description()
    {
        return description;
    }

    /* Implement Predicate */

    /**
     * Tests if the predicate holds for given transaction.
     *
     * @return True if predicate holds
     */
    public boolean test(V entity)
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

    protected boolean testOnColl(V entity)
    {
        sane(entity, "entity");
        cool(coll, "coll");
        if (cpredicate != null)
        {
            Collection<V> matching = new ArrayList<>();
            for (V v : coll)
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
            Collection<V> matching = new ArrayList<>();
            for (V v : coll)
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
            for (V v : coll)
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
            cool(tran, "tran");
            for (V v : coll)
            {
                if (entity.equals(v) && vbipredicate.test(v, tran))
                {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    protected boolean testOnMap(V entity)
    {
        sane(entity, "entity");
        cool(map, "map");
        // TODO

        // if (cpredicate != null)
        // {
        // return cpredicate.test(coll);
        // }
        // if (cbipredicate != null)
        // {
        // return cbipredicate.test(coll, tran);
        // }
        // if (vpredicate != null)
        // {
        // for (V value : coll)
        // {
        // if (vpredicate.test(value))
        // {
        // return true;
        // }
        // }
        // return false;
        // }
        // if (vbipredicate != null)
        // {
        // for (V value : coll)
        // {
        // if (vbipredicate.test(value, tran))
        // {
        // return true;
        // }
        // }
        // return false;
        // }
        return false;
    }

    // protected boolean testOnSet()
    // {
    // Args.cool(tran, "tran");
    // State.cool(values, "values");
    // boolean result;
    // if (mpredicate != null)
    // {
    // result = mpredicate.test(values);
    // }
    // else if (mbipredicate != null)
    // {
    // result = mbipredicate.test(values, tran);
    // }
    // else if (values.isEmpty())
    // {
    // result = false;
    // }
    // else
    // {
    // result = false;
    // for (V value : values)
    // {
    // if (vpredicate != null)
    // {
    // result = vpredicate.test(value);
    // }
    // else if (vbipredicate != null)
    // {
    // result = vbipredicate.test(value, tran);
    // }
    // if (result)
    // {
    // break;
    // }
    // }
    // }
    // return result;
    // }
}
