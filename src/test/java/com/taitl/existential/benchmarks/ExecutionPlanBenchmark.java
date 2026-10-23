package com.taitl.existential.benchmarks;

import com.taitl.ex.examples.night_city.app.*;
import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.evaluation.events.split_events.*;
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
 * Profiles setup, first selection, warm reuse, and complete validation workflows separately.
 * Night City supplies real generic descriptors and configured callbacks; selection phases
 * avoid callback execution, while complete workflows assert the expected deferred rule count.
 *
 * An optional test agent measures reachable cache graphs before use, after bounded churn, and
 * after close. Assertions check cache bounds, payload exclusion, and deterministic cleanup.
 * These exploratory samples establish baselines rather than numerical performance gates.
 */
class ExecutionPlanBenchmark
{
    static final String OP = "/night-city/prepared-selection";
    static volatile long consumed;

    @Test
    void profilesColdWarmSelectionAndRetainedGraphs() throws Exception
    {
        int count = Integer.getInteger("benchmark.types", 200);
        int rules = Integer.getInteger("benchmark.rules", 100);
        CityWorkload workload = new CityWorkload(Integer.getInteger("benchmark.entities", count), count);
        AtomicLong callbacks = new AtomicLong();
        Existential ex = new Existential();
        Existential previous = Ex.instance(ex);
        EventField field = null;
        try
        {
            long started = System.nanoTime();
            new ConfigureLooks().configureSelectionWorkload(OP, workload, rules,
                    System.getProperty("benchmark.shape", "broad"), callbacks);
            Tr preparation = ex.begin(OP);
            preparation.close();
            field = ex.configs().config(OP).indexes(OP, StageName.VALIDATION).eventField();
            System.out.printf(Locale.ROOT, "setup %.1f µs; configured-field=%d bytes%n",
                    (System.nanoTime() - started) / 1_000d, BenchmarkMemory.reachable(field));
            List<RuntimeKey<?>> keys = keys(workload);
            SplitEvent split = new SplitEvent();
            measure("cold", keys, field, split, 1);
            assertEquals(keys.size(), field.cachedSelections());
            int iterations = Integer.getInteger("benchmark.measure.iterations", 5);
            for (int i = 0; i < Integer.getInteger("benchmark.warmup.iterations", 3); i++)
            {
                evaluate(keys, field, split);
            }
            measure("warm", keys, field, split, iterations);
            long warmBytes = BenchmarkMemory.reachable(field);
            if (BenchmarkMemory.available())
            {
                for (Object value : BenchmarkMemory.graph(field))
                {
                    assertFalse(value instanceof Look<?> || value instanceof Cat || value instanceof RuntimeKey<?>);
                }
            }
            Tr tr = ex.begin(OP);
            try
            {
                for (CityWorkload.Report report : workload.looks)
                {
                    create(tr, report);
                }
                tr.commit();
            }
            finally
            {
                tr.close();
            }
            assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
            long disposed = BenchmarkMemory.reachable(tr.runtimeIndexes());
            measureComplete(ex, workload, rules, callbacks);
            Cat cat = new Cat("Black", "Park");
            for (int i = 0; i < PreparedSelections.CAPACITY + 17; i++)
            {
                RuntimeKey<Cat> key = new RuntimeKey<>(new Create<>(cat), TypeKey.valueOf("Unmatched" + i), cat);
                assertTrue(split.call(key, field, false, true, null).evaluables().isEmpty());
            }
            assertEquals(PreparedSelections.CAPACITY, field.cachedSelections());
            System.out.printf(Locale.ROOT,
                    "retention warm=%d bytes churn=%d bytes entries=%d disposed-index=%d bytes callbacks=%d%n",
                    warmBytes, BenchmarkMemory.reachable(field), field.cachedSelections(), disposed, callbacks.get());
        }
        finally
        {
            ex.close();
            Ex.instance(previous);
        }
        assertNotNull(field);
        assertEquals(0, field.cachedSelections());
        System.out.printf(Locale.ROOT, "closed-field=%d bytes entries=%d%n",
                BenchmarkMemory.reachable(field), field.cachedSelections());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    List<RuntimeKey<?>> keys(CityWorkload workload)
    {
        List<RuntimeKey<?>> keys = new ArrayList<>();
        boolean mixed = Boolean.getBoolean("benchmark.mixed");
        for (int i = 0; i < workload.descriptors; i++)
        {
            CityWorkload.Report report = workload.looks.get(i);
            keys.add(new RuntimeKey(new Create(report.entity), report.type, report.entity));
            if (mixed)
            {
                keys.add(new RuntimeKey(new Update(report.entity), report.type, report.entity));
                keys.add(new RuntimeKey(new Delete(report.entity), report.type, report.entity));
                keys.add(new RuntimeKey(new Transit(report.entity, report.entity), report.type, report.entity));
                keys.add(new RuntimeKey(new Port(null, report.entity), report.type, report.entity));
            }
        }
        return keys;
    }

    void measureComplete(Existential ex, CityWorkload workload, int rules, AtomicLong callbacks) throws Exception
    {
        int repeats = Integer.getInteger("benchmark.reports", 1);
        int warmup = Integer.getInteger("benchmark.warmup.iterations", 3);
        int iterations = Integer.getInteger("benchmark.measure.iterations", 5);
        for (int i = 0; i < warmup; i++)
        {
            complete(ex, workload, repeats, rules, callbacks);
        }
        long before = BenchmarkMemory.allocated();
        long started = System.nanoTime();
        for (int i = 0; i < iterations; i++)
        {
            complete(ex, workload, repeats, rules, callbacks);
        }
        long elapsed = System.nanoTime() - started;
        long allocated = BenchmarkMemory.allocated() - before;
        System.out.printf(Locale.ROOT,
                "complete entities=%d reports=%d rules=%d %.1f µs/workflow %.1f bytes/workflow%n",
                workload.size(), repeats, rules, elapsed / 1_000d / iterations, (double) allocated / iterations);
    }

    void complete(Existential ex, CityWorkload workload, int repeats, int rules, AtomicLong callbacks) throws Exception
    {
        long before = callbacks.get();
        Tr tr = ex.begin(OP);
        try
        {
            for (CityWorkload.Report report : workload.looks)
            {
                for (int i = 0; i < repeats; i++)
                {
                    create(tr, report);
                }
            }
            assertEquals(before, callbacks.get());
            tr.commit();
            assertEquals((long) workload.size() * rules, callbacks.get() - before);
        }
        finally
        {
            tr.close();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    void create(Tr tr, CityWorkload.Report report) throws Exception
    {
        tr.create(report.entity, (TypeKey) report.type);
    }

    void measure(String phase, List<RuntimeKey<?>> keys, EventField field, SplitEvent split, int iterations)
            throws Exception
    {
        long before = BenchmarkMemory.allocated();
        long started = System.nanoTime();
        long checksum = 0;
        for (int i = 0; i < iterations; i++)
        {
            checksum += evaluate(keys, field, split);
        }
        long elapsed = System.nanoTime() - started;
        long allocated = BenchmarkMemory.allocated() - before;
        consumed = checksum;
        assertTrue(checksum > 0);
        System.out.printf(Locale.ROOT,
                "selection %s signatures=%d %.1f µs/selection %.1f bytes/selection checksum=%d%n",
                phase, keys.size(), elapsed / 1_000d / iterations / keys.size(),
                (double) allocated / iterations / keys.size(), checksum);
    }

    long evaluate(List<RuntimeKey<?>> keys, EventField field, SplitEvent split) throws Exception
    {
        long count = 0;
        for (RuntimeKey<?> key : keys)
        {
            count += split.call(key, field, false, true, null).evaluables().size() + 1;
        }
        return count;
    }
}
