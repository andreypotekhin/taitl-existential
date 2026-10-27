package com.taitl.ex.logic.managed_indexes.data;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.managed_indexes.views.*;
import com.taitl.existential.*;
import com.taitl.existential.indexes.*;
import java.util.*;

/** Owns the typed facade and independently enrolled sides of one bound immutable definition. */
public class ManagedBinding
{
    public final IndexDefinition<?> definition;
    public final IndexingPolicy policy;
    public final ManagedSide<Object, Object> left;
    public final ManagedSide<Object, Object> right;
    private final Object view;

    @SuppressWarnings("unchecked")
    public ManagedBinding(IndexDefinition<?> definition, IndexingPolicy policy, ManagedAccess access)
    {
        this.definition = definition;
        this.policy = policy;
        boolean multi = definition.shape() == IndexDefinition.Shape.MULTI
                || definition.shape() == IndexDefinition.Shape.MULTI_JOIN;
        left = Creator.create(ManagedSide.class,
                new Class<?>[] { IndexDefinition.class, boolean.class, boolean.class, ManagedAccess.class },
                definition, false, multi, access);
        right = definition.type(true) == null ? null
                : Creator.create(ManagedSide.class,
                        new Class<?>[] { IndexDefinition.class, boolean.class, boolean.class, ManagedAccess.class },
                        definition, true, multi, access);
        Class<?> facade;
        switch (definition.shape())
        {
        case SINGLE:
            facade = ManagedSingleIndex.class;
            break;
        case MULTI:
            facade = ManagedMultiIndex.class;
            break;
        case SINGLE_JOIN:
            facade = ManagedSingleJoin.class;
            break;
        default:
            facade = ManagedMultiJoin.class;
        }
        view = right == null
                ? Creator.create(facade, new Class<?>[] { ManagedSide.class, ManagedAccess.class }, left, access)
                : Creator.create(facade, new Class<?>[] { ManagedSide.class, ManagedSide.class, ManagedAccess.class },
                        left, right, access);
    }

    public Object view()
    {
        return view;
    }

    public List<ManagedSide<Object, Object>> sides()
    {
        return right == null ? Collections.singletonList(left) : Arrays.asList(left, right);
    }

    public void close()
    {
        left.close();
        if (right != null)
        {
            right.close();
        }
    }
}
