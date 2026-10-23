package com.taitl.ex.logic.evaluation.events.split_events.event_splitter.type_keys;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.types.*;
import com.taitl.existential.keys.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Adapts resolved native hierarchy metadata to the legacy expanded-key interface.
 * TypeHierarchyLogic performs generic substitution and ClassValue caching; this action renders
 * each non-Object signature and adds the declared variants and upper-bound lookup keys expected
 * by existing split-key callers.
 *
 * The returned set is immutable and contains descriptors rather than reported entities.
 * New compact dispatch uses the same hierarchy facts directly through structural TypePatterns,
 * avoiding expanded wildcard strings on its selection path.
 */
public class SplitInheritedTypeKeys
{
    @Logic
    protected TypeHierarchyLogic hierarchy = Creator.create(TypeHierarchyLogic.class);

    @Logic
    protected SplitDeclaredTypeKey declared = Creator.create(SplitDeclaredTypeKey.class);

    @Logic
    protected AddUpperBoundTypeKeys bounds = Creator.create(AddUpperBoundTypeKeys.class);

    public Set<TypeKey<?>> call(Class<?> runtimeClass, boolean fullNames)
    {
        Set<TypeKey<?>> result = new LinkedHashSet<>();
        for (Type type : hierarchy.types(runtimeClass))
        {
            if (type.equals(Object.class))
            {
                continue;
            }
            result.addAll(declared.call(TypeKey.valueOf(type, fullNames)));
            if (type instanceof ParameterizedType)
            {
                bounds.call((ParameterizedType) type, result, fullNames);
            }
        }
        return Collections.unmodifiableSet(result);
    }
}
