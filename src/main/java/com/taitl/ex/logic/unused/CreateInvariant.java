package com.taitl.ex.logic.unused;

import java.util.function.*;
import com.taitl.existential.invariants.*;

@Deprecated
public interface CreateInvariant<T>
{
    Invariant<T> create(Predicate<? super T> condition, String description);
    // {
    // Args.cool(condition, "condition", description, "description");
    // return add(new OnCreate<T>(condition, null, description));
    // }
}
