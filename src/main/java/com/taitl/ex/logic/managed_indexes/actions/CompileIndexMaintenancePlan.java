package com.taitl.ex.logic.managed_indexes.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.managed_indexes.data.*;
import com.taitl.ex.logic.managed_indexes.rules.*;
import com.taitl.ex.logic.types.*;
import com.taitl.ex.logic.types.data.*;
import java.util.*;

/** Groups automatic side bindings by raw subject before matching reported descriptors. */
public class CompileIndexMaintenancePlan
{
    @Logic
    private TypeMatchingLogic matching = Creator.create(TypeMatchingLogic.class);

    @Logic
    private RequiresIndexMaintenance requiresMaintenance = Creator.create(RequiresIndexMaintenance.class);

    public Map<String, List<ManagedSide<Object, Object>>> call(Collection<ManagedBinding> bindings, boolean reads)
    {
        Map<String, List<ManagedSide<Object, Object>>> result = new HashMap<>();
        for (ManagedBinding binding : bindings)
        {
            for (ManagedSide<Object, Object> side : binding.sides())
            {
                matching.validateRuleType(side.type());
                if (requiresMaintenance.in(binding.policy, reads))
                {
                    String raw = new TypePattern(side.type().toString()).raw();
                    result.computeIfAbsent(raw, ignored -> new ArrayList<>()).add(side);
                }
            }
        }
        return result;
    }
}
