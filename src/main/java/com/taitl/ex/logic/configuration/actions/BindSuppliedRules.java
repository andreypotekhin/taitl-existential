package com.taitl.ex.logic.configuration.actions;

import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

/** An explicit binding type must not overwrite a definition reused by another owner. */
public class BindSuppliedRules
{
    public static <T> Intent<T> intent(Intent<T> supplied, TypeKey<T> type)
    {
        if (java.util.Objects.equals(supplied.typeKey(), type))
        {
            return supplied;
        }
        Intent<T> bound = new Intent<>(type);
        for (Ev<T> leaf : supplied.list())
        {
            bound.add(leaf);
        }
        return bound;
    }
}
