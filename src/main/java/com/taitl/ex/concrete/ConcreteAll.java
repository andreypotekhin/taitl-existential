package com.taitl.ex.concrete;

import java.util.function.*;
import com.taitl.ex.common.helper.strings.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.exceptions.*;

public class ConcreteAll<T> implements Expression<T>
{
    Predicate<? super T> condition;
    Predicate<? super T> predicate;
    String description;

    public Object evaluate(T t) throws ExistentialException
    {
        if (condition == null || condition.test(t))
        {
            if (!predicate.test(t))
            {
                throw new PredicateFailure(description());
            }
        }
        return null;
    }

    public String description()
    {
        return Descriptions.text(description);
    }
}
