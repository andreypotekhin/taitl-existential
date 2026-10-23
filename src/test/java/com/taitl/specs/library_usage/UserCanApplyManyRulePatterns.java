package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.app.*;
import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.logic.execution.actions.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Applications combine many subject patterns and event families in declaration order")
class UserCanApplyManyRulePatterns
{
    static final String OP = "/dwellings/save";

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3, 4, 8, 16, 20, 32 })
    void preservesOrderAndFolding(int count) throws Exception
    {
        List<Integer> calls = new ArrayList<>();
        DwellingWorkload workload = new DwellingWorkload(3);
        try (Existential ex = new Existential())
        {
            Existential previous = Ex.instance(ex);
            try
            {
                new ConfigureDwellings().configure(OP, 100, count, false, declaration -> calls.add(declaration));
                Tr tr = ex.begin(OP);
                try
                {
                    RuleInventory inventory =
                            ex.configs().config(OP).indexes(OP, StageName.VALIDATION).configuredHandlers.inventory();
                    RuntimeKey<?> key = new RuntimeKey<>(new Create<>(workload.dwellings.get(0)), workload.type,
                            workload.dwellings.get(0));
                    SelectionInput input = new ResolveSelectionInput().call(key, false, true, null);
                    List<RulePattern> rows = new ResolveRuleSelection().rows(inventory, input.types, input.events);
                    Set<RulePattern> distinct = Collections.newSetFromMap(new IdentityHashMap<>());
                    distinct.addAll(rows);
                    assertEquals(count, distinct.size());
                    new DwellingOperations().report(tr, workload, 16);
                    assertTrue(calls.isEmpty());
                    tr.commit();
                    assertEquals(300, calls.size());
                    for (int i = 0; i < calls.size(); i++)
                    {
                        assertEquals(i % 100, calls.get(i));
                    }
                }
                finally
                {
                    tr.close();
                }
            }
            finally
            {
                Ex.instance(previous);
            }
        }
    }

    @Test
    void rejectsThenRecoversWithAFreshTransaction() throws Exception
    {
        DwellingWorkload workload = new DwellingWorkload(1);
        try (Existential ex = new Existential())
        {
            Existential previous = Ex.instance(ex);
            try
            {
                new ConfigureDwellings().configure(OP, 100, 32, false, declaration -> {
                });
                workload.dwellings.get(0).color = null;
                Tr rejected = ex.begin(OP);
                try
                {
                    new DwellingOperations().report(rejected, workload, 1);
                    assertThrows(ValidationStageExceptions.class, () -> rejected.commit());
                    rejected.rollback();
                }
                finally
                {
                    rejected.close();
                }
                workload.dwellings.get(0).color = "Grey";
                Tr fresh = ex.begin(OP);
                try
                {
                    new DwellingOperations().report(fresh, workload, 1);
                    fresh.commit();
                }
                finally
                {
                    fresh.close();
                }
            }
            finally
            {
                Ex.instance(previous);
            }
        }
    }
}
