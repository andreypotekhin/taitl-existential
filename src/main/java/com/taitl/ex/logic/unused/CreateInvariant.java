package com.taitl.ex.logic.unused;

import com.taitl.existential.constraints.*;

import java.util.function.*;

@Deprecated
public interface CreateInvariant<T>
{
    Invariant<T> create(Predicate<? super T> condition, String description);
    // {
    // Args.cool(condition, "condition", description, "description");
    // return add(new OnCreate<T>(condition, null, description));
    // }
}
