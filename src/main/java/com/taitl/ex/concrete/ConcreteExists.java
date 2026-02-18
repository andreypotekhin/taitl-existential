package com.taitl.ex.concrete;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

public class ConcreteExists<V> implements Predicate<Transaction>
{
    Collection<V> coll;
    Stream<V> stream; // TODO
    // Set<V> values;
    Predicate<Collection<V>> cpredicate;
    BiPredicate<Collection<V>, Transaction> cbipredicate;
    // Predicate<Set<V>> spredicate;
    // BiPredicate<Set<V>, Transaction> sbipredicate;
    Predicate<V> vpredicate;
    BiPredicate<V, Transaction> vbipredicate;

    /* Implement Predicate */

    /**
     * Tests if the predicate holds for given transaction.
     *
     * @param tran Transaction object
     * @return True if predicate holds
     */
    public boolean test(Transaction tran)
    {
        return testOnColl(tran);
        // if (coll != null)
        // {
        // return testOnColl(tran);
        // }
        // else if (values != null)
        // {
        // return testOnSet(tran);
        // }
        // else
        // {
        // throw new IllegalStateException("Neither collection nor set is defined");
        // }
    }

    protected boolean testOnColl(Transaction tran)
    {
        sane(tran, "tran");
        cool(coll, "values");
        if (cpredicate != null)
        {
            return cpredicate.test(coll);
        }
        if (cbipredicate != null)
        {
            return cbipredicate.test(coll, tran);
        }
        if (vpredicate != null)
        {
            for (V value : coll)
            {
                if (vpredicate.test(value))
                {
                    return true;
                }
            }
            return false;
        }
        if (vbipredicate != null)
        {
            for (V value : coll)
            {
                if (vbipredicate.test(value, tran))
                {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    // protected boolean testOnSet(Transaction tran)
    // {
    // Args.cool(tran, "tran");
    // State.cool(values, "values");
    // boolean result;
    // if (spredicate != null)
    // {
    // result = spredicate.test(values);
    // }
    // else if (sbipredicate != null)
    // {
    // result = sbipredicate.test(values, tran);
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
