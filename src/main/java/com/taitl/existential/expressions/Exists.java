package com.taitl.existential.expressions;

import java.util.*;
import java.util.function.*;

import com.taitl.existential.helper.Args;
import com.taitl.existential.helper.State;
import com.taitl.existential.transactions.Transaction;

public class Exists<V> implements Predicate<Transaction>
{
    Collection<V> coll;
    Set<V> values;
    Predicate<Collection<V>> cpredicate;
    BiPredicate<Collection<V>, Transaction> cbipredicate;
    Predicate<Set<V>> spredicate;
    BiPredicate<Set<V>, Transaction> sbipredicate;
    Predicate<V> vpredicate;
    BiPredicate<V, Transaction> vbipredicate;

    public Exists(Collection<V> coll, Predicate<V> predicate)
    {
        Args.cool(coll, "coll", predicate, "predicate");
        this.coll = coll;
        this.vpredicate = predicate;
    }

    public Exists(Collection<V> coll, BiPredicate<V, Transaction> bipredicate)
    {
        Args.cool(coll, "coll", bipredicate, "bipredicate");
        this.coll = coll;
        this.vbipredicate = bipredicate;
    }

    public Exists(Collection<V> coll, Predicate<Collection<V>> predicate, int placeholder)
    {
        Args.cool(coll, "coll", predicate, "predicate");
        this.coll = coll;
        this.cpredicate = predicate;
    }

    public Exists(Collection<V> coll, BiPredicate<Collection<V>, Transaction> bipredicate, int placeholder)
    {
        Args.cool(coll, "coll", bipredicate, "bipredicate");
        this.coll = coll;
        this.cbipredicate = bipredicate;
    }

    public Exists(Set<V> values, Predicate<V> predicate)
    {
        Args.cool(values, "values", predicate, "predicate");
        this.values = values;
        this.vpredicate = predicate;
    }

    public Exists(Set<V> values, BiPredicate<V, Transaction> bipredicate)
    {
        Args.cool(values, "values", bipredicate, "bipredicate");
        this.values = values;
        this.vbipredicate = bipredicate;
    }

    public Exists(Set<V> values, Predicate<Set<V>> predicate, int placeholder)
    {
        Args.cool(values, "values", predicate, "predicate");
        this.values = values;
        this.spredicate = predicate;
    }

    public Exists(Set<V> values, BiPredicate<Set<V>, Transaction> bipredicate, int placeholder)
    {
        Args.cool(values, "values", bipredicate, "bipredicate");
        this.values = values;
        this.sbipredicate = bipredicate;
    }

    public boolean test(Transaction tran)
    {
        if (coll != null)
        {
            return testOnColl(tran);
        }
        else if (values != null)
        {
            return testOnSet(tran);
        }
        else
        {
            throw new IllegalStateException("Neither collection nor set is defined");
        }
    }

    public boolean testOnColl(Transaction tran)
    {
        Args.cool(tran, "tran");
        State.cool(coll, "values");
        boolean result;
        if (spredicate != null)
        {
            result = cpredicate.test(coll);
        }
        else if (sbipredicate != null)
        {
            result = cbipredicate.test(coll, tran);
        }
        else
        {
            result = false;
            for (V value : coll)
            {
                if (vpredicate != null)
                {
                    result = vpredicate.test(value);
                }
                else if (vbipredicate != null)
                {
                    result = vbipredicate.test(value, tran);
                }
                if (result)
                {
                    break;
                }
            }
        }
        return result;
    }

    public boolean testOnSet(Transaction tran)
    {
        Args.cool(tran, "tran");
        State.cool(values, "values");
        boolean result;
        if (spredicate != null)
        {
            result = spredicate.test(values);
        }
        else if (sbipredicate != null)
        {
            result = sbipredicate.test(values, tran);
        }
        else if (values.isEmpty())
        {
            result = false;
        }
        else
        {
            result = false;
            for (V value : values)
            {
                if (vpredicate != null)
                {
                    result = vpredicate.test(value);
                }
                else if (vbipredicate != null)
                {
                    result = vbipredicate.test(value, tran);
                }
                if (result)
                {
                    break;
                }
            }
        }
        return result;
    }
}
