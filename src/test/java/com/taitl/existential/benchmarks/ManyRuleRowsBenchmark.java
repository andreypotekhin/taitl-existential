package com.taitl.existential.benchmarks;

import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.logic.execution.actions.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Compares native membership representations at verified many-row dispatch cardinalities.
 * Existing dwelling types and production event/type resolution supply real membership rows;
 * row count varies independently of declaration count. Synthetic original-prototype sets
 * remain a separately labelled control and are never presented as configured rule partitions.
 *
 * Candidates share final handler order and zero/one-row shortcuts. Construction, native union,
 * and union plus direct handler materialization are timed independently after setup. Prepared
 * row graphs retain no reported payload; each library and its temporary transaction close on
 * failure as well as success. Time-based warmup and candidate rotation reduce startup bias.
 */
class ManyRuleRowsBenchmark
{
    static final String OP = "/night-city/dwellings/selection";

    @Test
    void comparesVerifiedRows() throws Exception
    {
        int rules = Integer.getInteger("benchmark.rules", 256);
        boolean synthetic = Boolean.getBoolean("benchmark.synthetic");
        List<String> kinds = new ArrayList<>(List.of("arrays", "bitset", "roaring"));
        Collections.rotate(kinds, Integer.getInteger("benchmark.sample", 1) - 1);
        List<Integer> counts = synthetic ? List.of(20)
                : Boolean.getBoolean("benchmark.small") ? List.of(2, 3)
                        : Integer.getInteger("benchmark.rows", 0) > 0
                                ? List.of(Integer.getInteger("benchmark.rows"))
                                : List.of(1, 4, 8, 16, 20, 32);
        for (int count : counts)
        {
            try (Existential ex = new Existential())
            {
                Existential previous = Ex.instance(ex);
                try
                {
                    AtomicLong calls = new AtomicLong();
                    new ConfigureDwellings().configure(OP, rules, count,
                            Boolean.getBoolean("benchmark.grouped"), declaration -> calls.incrementAndGet());
                    Tr tr = ex.begin(OP);
                    tr.close();
                    RuleInventory inventory =
                            ex.configs().config(OP).indexes(OP, StageName.VALIDATION).configuredHandlers.inventory();
                    int[][] input = actualRows(inventory, count);
                    for (String shape : synthetic ? List.of("sparse", "broad", "clustered") : List.of("configured"))
                    {
                        int[][] rows = synthetic ? originalRows(shape, rules) : input;
                        compare(kinds, rows, inventory, shape);
                    }
                    assertEquals(0, calls.get());
                }
                finally
                {
                    Ex.instance(previous);
                }
            }
        }
    }

    int[][] actualRows(RuleInventory inventory, int count)
    {
        DwellingWorkload workload = new DwellingWorkload(1);
        RuntimeKey<?> key = new RuntimeKey<>(new Create<>(workload.dwellings.get(0)), workload.type,
                workload.dwellings.get(0));
        SelectionInput input = new ResolveSelectionInput().call(key, false, true, null);
        List<RulePattern> paths = new ResolveRuleSelection().rows(inventory, input.types, input.events);
        assertEquals(count, paths.size());
        System.out.printf(Locale.ROOT, "configured rules=%d paths=%d distinct=%d families=%s grouped=%s%n",
                inventory.size(), paths.size(), paths.size(), input.events,
                Boolean.getBoolean("benchmark.grouped"));
        int[][] result = new int[paths.size()][];
        for (int i = 0; i < paths.size(); i++)
        {
            result[i] = paths.get(i).members.stream().toArray();
        }
        return result;
    }

    void compare(List<String> kinds, int[][] rows, RuleInventory inventory, String shape)
    {
        int[] expected = new MembershipRows("arrays", rows).combine(false);
        if (shape.equals("configured"))
            assertEquals(inventory.size(), expected.length);
        for (String kind : kinds)
        {
            MembershipRows prepared = new MembershipRows(kind, rows);
            assertArrayEquals(expected, prepared.combine(false));
            assertArrayEquals(new MembershipRows("arrays", rows).combine(true), prepared.combine(true));
            RuleSelection selection = prepared.select(inventory);
            assertEquals(inventory.selection(expected).handlers(), selection.handlers());
            assertThrows(UnsupportedOperationException.class, () -> selection.handlers().clear());
            String label = "many-rows shape=" + shape + " rules=" + inventory.size() + " rows=" + rows.length
                    + " kind=" + kind;
            MembershipMeasure measure = new MembershipMeasure();
            measure.call(label + " phase=construction", () -> new MembershipRows(kind, rows));
            measure.call(label + " phase=native-union", () -> prepared.nativeCombine(false));
            measure.call(label + " phase=selection", () -> prepared.select(inventory));
            System.out.printf(Locale.ROOT, "%s row-bytes=%d selected=%d%n",
                    label, BenchmarkMemory.reachable(prepared), expected.length);
        }
    }

    int[][] originalRows(String shape, int rules)
    {
        int[][] result = new int[20][];
        for (int row = 0; row < result.length; row++)
        {
            List<Integer> values = new ArrayList<>();
            for (int id = 0; id < rules; id++)
            {
                boolean match = shape.equals("sparse") ? (id + row * 7) % 23 == 0
                        : shape.equals("broad") ? (id + row) % 2 == 0 : id >= row * 8 && id < row * 8 + 32;
                if (match)
                    values.add(id);
            }
            result[row] = values.stream().mapToInt(id -> id).toArray();
        }
        return result;
    }
}
