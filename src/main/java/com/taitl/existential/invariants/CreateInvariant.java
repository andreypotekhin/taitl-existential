package com.taitl.existential.invariants;

import java.util.function.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.helper.*;

public interface CreateInvariant<T>
{
    Invariant<T> create(Predicate<? super T> condition, String description);
    // {
    // Args.cool(condition, "condition", description, "description");
    // return add(new OnCreate<T>(condition, null, description));
    // }
}
