package com.taitl.ex.concrete;

import java.util.function.*;
import com.taitl.ex.common.helper.strings.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;

public class ConcreteAll<T> implements Expression<T>
{
    Predicate<? super T> condition;
    Predicate<? super T> predicate;
    String description;

    public Object evaluate(T t)
    {
        if (condition == null || condition.test(t))
        {
            if (!predicate.test(t))
            {
                throw new PredicateFailure(description() + " See " + PredicateFailure.TROUBLESHOOTING_SECTION);
            }
        }
        return null;
    }

    public Object evaluate(T entity, Tr tr)
    {
        if ((condition == null || test(condition, entity, tr)) && !test(predicate, entity, tr))
        {
            throw new PredicateFailure(description() + " See " + PredicateFailure.TROUBLESHOOTING_SECTION);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected boolean test(Predicate<? super T> predicate, T entity, Tr tr)
    {
        return predicate instanceof TransactionPredicate<?>
                ? ((TransactionPredicate<T>) predicate).test(entity, tr)
                : predicate.test(entity);
    }

    public String description()
    {
        return Descriptions.text(description);
    }
}
