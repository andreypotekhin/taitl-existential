package com.taitl.ex.logic.types;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.types.data.*;
import com.taitl.existential.keys.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Compares configured subject patterns with reported structural type information.
 * Raw subjects must agree; raw configured rules accept any arguments, direct wildcards accept
 * any corresponding argument, and concrete generic arguments remain invariant.
 *
 * An upper bound is proven using the reported argument's captured Type and projected hierarchy,
 * including parameterized bounds. Each argument is checked independently without generating
 * wildcard combinations. Erased information is not guessed, and lower-bounded rule validation
 * is handled separately by TypeMatchingLogic during configuration.
 */
public class MatchTypePattern
{
    @Logic
    protected TypeHierarchyLogic hierarchy = Creator.create(TypeHierarchyLogic.class);

    public boolean call(TypePattern configured, TypePattern reported)
    {
        if (!configured.raw().equals(reported.raw()))
        {
            return false;
        }
        List<String> wanted = configured.arguments();
        if (wanted.isEmpty())
        {
            return true;
        }
        List<String> actual = reported.arguments();
        if (wanted.size() != actual.size())
        {
            return false;
        }
        for (int i = 0; i < wanted.size(); i++)
        {
            if (!wanted.get(i).equals("?") && !wanted.get(i).equals(actual.get(i)))
            {
                TypePattern bound = configured.bound(i);
                if (bound == null || !within(bound, reported.argument(i)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean within(TypePattern bound, Type argument)
    {
        if (bound.arguments().isEmpty() && (bound.raw().equals("Object") || bound.raw().equals("java.lang.Object")))
        {
            return true;
        }
        if (argument instanceof WildcardType && ((WildcardType) argument).getLowerBounds().length == 0)
        {
            for (Type upper : ((WildcardType) argument).getUpperBounds())
            {
                if (within(bound, upper))
                {
                    return true;
                }
            }
            return false;
        }
        if (argument instanceof TypeVariable<?>)
        {
            for (Type upper : ((TypeVariable<?>) argument).getBounds())
            {
                if (within(bound, upper))
                {
                    return true;
                }
            }
            return false;
        }
        if (argument == null)
        {
            return false;
        }
        for (Type projected : hierarchy.resolve(argument))
        {
            for (boolean full : new boolean[] { false, true })
            {
                if (call(bound, new TypePattern(TypeKey.valueOf(projected, full).toString(), projected)))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
