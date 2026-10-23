package com.taitl.existential.benchmarks;

import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.logic.execution.actions.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.ex.logic.types.data.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Compares sorted arrays, BitSet, and RoaringBitmap using actual rule-selection membership.
 * Night City descriptors pass through the production inventory and structural matcher before
 * rows are encoded in each representation. Overlapping fixtures combine broad, exact, clustered,
 * and upper-bound declarations to exercise merging several applicable rows. Construction, prepared union/intersection, and
 * ordered handler materialization are measured as separate phases with identical result checks.
 *
 * Each measured result escapes through a volatile reference; checksum consumption occurs outside
 * the timed region. Reported time covers a descriptor batch, while allocation is per selection.
 * Optional identifier gaps are synthetic stress evidence, separate from compact production IDs.
 */
class MembershipRepresentationBenchmark
{
    static final String OP = "/night-city/selection";
    static volatile long consumed;
    static volatile Object materialized;
    static final int ID_STRIDE = Integer.getInteger("benchmark.id.stride", 1);

    @Test
    void comparesActualDeclarationMembership() throws Exception
    {
        int types = Integer.getInteger("benchmark.types", 200);
        int rules = Integer.getInteger("benchmark.rules", 256);
        int rotation = Integer.getInteger("benchmark.sample", 1) - 1;
        CityWorkload workload = new CityWorkload(types, types);
        List<String> kinds = new ArrayList<>(List.of("arrays", "bitset", "roaring"));
        Collections.rotate(kinds, rotation);
        for (String shape : Boolean.getBoolean("benchmark.bounds") ? List.of("upper-bound")
                : List.of("sparse", "broad", "clustered", "overlapping"))
        {
            try (Existential ex = new Existential())
            {
                Existential previous = Ex.instance(ex);
                try
                {
                    configure(workload, rules, shape);
                    Tr tr = ex.begin(OP);
                    tr.close();
                    RuleInventory inventory =
                            ex.configs().config(OP).indexes(OP, StageName.VALIDATION).configuredHandlers.inventory();
                    int[][][] groups = groups(workload, inventory);
                    int paths = 0;
                    int multiple = 0;
                    for (int[][] rows : groups)
                    {
                        paths += rows.length;
                        multiple += rows.length > 1 ? 1 : 0;
                    }
                    System.out.printf(Locale.ROOT, "rows shape=%s paths=%d multiple=%d%n", shape, paths, multiple);
                    if ("overlapping".equals(shape))
                    {
                        assertTrue(multiple > 0);
                    }
                    if (ID_STRIDE != 1)
                    {
                        System.out.printf(Locale.ROOT, "synthetic ID stress stride=%d slots=%d%n",
                                ID_STRIDE, rules * ID_STRIDE);
                    }
                    for (String kind : kinds)
                    {
                        compare(kind, shape, groups, inventory);
                    }
                }
                finally
                {
                    Ex.instance(previous);
                }
            }
        }
        assertNotEquals(0, consumed);
    }

    void configure(CityWorkload workload, int rules, String shape)
    {
        ConfigureLooks configure = new ConfigureLooks();
        if (!"overlapping".equals(shape))
        {
            configure.configureSelectionWorkload(OP, workload, rules, shape, new AtomicLong());
            return;
        }
        List<String> shapes = List.of("broad", "sparse", "clustered", "upper-bound");
        for (int i = 0; i < shapes.size(); i++)
        {
            int count = rules / shapes.size() + (i < rules % shapes.size() ? 1 : 0);
            configure.configureSelectionWorkload(OP, workload, count, shapes.get(i), new AtomicLong());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    int[][][] groups(CityWorkload workload, RuleInventory inventory)
    {
        int[][][] groups = new int[workload.descriptors][][];
        ResolveSelectionInput resolver = new ResolveSelectionInput();
        for (int i = 0; i < groups.length; i++)
        {
            CityWorkload.Report report = workload.looks.get(i);
            RuntimeKey<?> key = new RuntimeKey(new Create(report.entity), report.type, report.entity);
            Set<TypePattern> types = resolver.types(key, false);
            groups[i] = new ResolveRuleSelection().rows(inventory, types, Set.of("Create")).toArray(new int[0][]);
            for (int j = 0; j < groups[i].length; j++)
            {
                int[] ids = groups[i][j].clone();
                for (int k = 0; k < ids.length; k++)
                {
                    ids[k] *= ID_STRIDE;
                }
                groups[i][j] = ids;
            }
        }
        return groups;
    }

    void compare(String kind, String shape, int[][][] groups, RuleInventory inventory)
    {
        MembershipRows[] prepared = prepare(kind, groups);
        int[][] unions = new int[groups.length][];
        for (int i = 0; i < groups.length; i++)
        {
            unions[i] = prepared[i].combine(false);
            MembershipRows reference = new MembershipRows("arrays", groups[i]);
            assertArrayEquals(reference.combine(false), unions[i]);
            assertArrayEquals(reference.combine(true), prepared[i].combine(true));
        }
        measure(kind, shape, "construction", groups.length, () -> prepare(kind, groups));
        measure(kind, shape, "union", groups.length, () -> combine(prepared, false));
        measure(kind, shape, "intersection", groups.length, () -> combine(prepared, true));
        measure(kind, shape, "ordered-selection", groups.length, () -> selections(unions, inventory));
        System.out.printf(Locale.ROOT,
                "membership shape=%s representation=%s descriptors=%d rules=%d retained=%d bytes%n",
                shape, kind, groups.length, inventory.size(), BenchmarkMemory.reachable(prepared));
    }

    MembershipRows[] prepare(String kind, int[][][] groups)
    {
        MembershipRows[] result = new MembershipRows[groups.length];
        for (int i = 0; i < groups.length; i++)
        {
            result[i] = new MembershipRows(kind, groups[i]);
        }
        return result;
    }

    int[][] combine(MembershipRows[] prepared, boolean intersection)
    {
        int[][] result = new int[prepared.length][];
        for (int i = 0; i < prepared.length; i++)
        {
            result[i] = prepared[i].combine(intersection);
        }
        return result;
    }

    RuleSelection[] selections(int[][] unions, RuleInventory inventory)
    {
        RuleSelection[] result = new RuleSelection[unions.length];
        for (int i = 0; i < unions.length; i++)
        {
            int[] ids = unions[i];
            if (ID_STRIDE != 1)
            {
                ids = ids.clone();
                for (int j = 0; j < ids.length; j++)
                {
                    ids[j] /= ID_STRIDE;
                }
            }
            result[i] = inventory.selection(ids);
        }
        return result;
    }

    void measure(String kind, String shape, String phase, int types, Supplier<Object> work)
    {
        int warmup = Integer.getInteger("benchmark.warmup.iterations", 50);
        int iterations = Integer.getInteger("benchmark.measure.iterations", 500);
        for (int i = 0; i < warmup; i++)
        {
            materialized = work.get();
        }
        long before = BenchmarkMemory.allocated();
        long started = System.nanoTime();
        for (int i = 0; i < iterations; i++)
        {
            materialized = work.get();
        }
        long elapsed = System.nanoTime() - started;
        long allocated = BenchmarkMemory.allocated() - before;
        long checksum = checksum(materialized);
        consumed = checksum;
        materialized = null;
        assertTrue(checksum > 0);
        System.out.printf(Locale.ROOT,
                "membership shape=%s representation=%s phase=%s selections=%d %.1f µs/batch %.1f bytes/selection checksum=%d%n",
                shape, kind, phase, types, elapsed / 1_000d / iterations,
                (double) allocated / iterations / types, checksum);
    }

    long checksum(Object value)
    {
        if (value instanceof int[])
        {
            long result = 1;
            for (int id : (int[]) value)
            {
                result += id + 1;
            }
            return result;
        }
        if (value instanceof MembershipRows)
        {
            long count = 1;
            for (Object row : ((MembershipRows) value).rows)
            {
                if (row instanceof int[])
                {
                    count += ((int[]) row).length;
                }
                else if (row instanceof BitSet)
                {
                    count += ((BitSet) row).cardinality();
                }
                else
                {
                    count += ((org.roaringbitmap.RoaringBitmap) row).getCardinality();
                }
            }
            return count;
        }
        if (value instanceof RuleSelection)
        {
            return ((RuleSelection) value).handlers().size() + 1;
        }
        long result = 1;
        for (Object item : (Object[]) value)
        {
            result += checksum(item);
        }
        return result;
    }
}
