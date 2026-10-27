package com.taitl.existential.benchmarks;

import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.logic.managed_indexes.data.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class ManagedIndexBenchmark
{
    static final String OP = "/night-city/managed/workload";
    static volatile long consumed;

    @Test
    void measuresFoldedMaintenanceAndCompleteWorkflows() throws Exception
    {
        int sample = Integer.getInteger("benchmark.sample", 0);
        int[][] cases = {
                { 10000, 100, 1, 100 }, { 10000, 1, 16, 100 },
                { 100000, 1000, 1, 100 }, { 10000, 100, 1, 500 }, { 100000, 1, 16, 100 }
        };
        for (int[] shape : cases)
        {
            try (Fixture fixture = new Fixture(shape[0], shape[1], shape[2], shape[3]))
            {
                fixture.workflow(false);
                System.out.printf(Locale.ROOT, "sample=%d entities=%d groups=%d repeats=%d rules=%d%n",
                        sample, shape[0], shape[1], shape[2], shape[3]);
                profile(fixture);
                measure("success", () -> fixture.workflow(false));
                measure("rejection-rollback", () -> fixture.workflow(true));
                measure("recovery", () -> {
                    fixture.workflow(true);
                    fixture.workflow(false);
                });
                measure("manual-control", () -> fixture.manual());
            }
            try (Fixture eager = new Fixture(shape[0], shape[1], shape[2], shape[3], true))
            {
                eager.workflow(false);
                measure("eager-maintenance-success", () -> eager.workflow(false));
            }
        }
    }

    private void profile(Fixture fixture) throws Exception
    {
        long started = System.nanoTime();
        Tr tr = fixture.ex.begin(OP);
        double construction = micros(started);
        try
        {
            ManagedBinding binding = fixture.binding(tr);
            long beforeBytes = BenchmarkMemory.allocated();
            started = System.nanoTime();
            fixture.workload.report(tr, fixture.repeats);
            double reporting = micros(started);
            long reportBytes = BenchmarkMemory.allocated() - beforeBytes;
            assertEquals(0, binding.left.updates());
            assertEquals(0, binding.left.extractions());
            assertEquals(0, binding.right.extractions());
            assertEquals(0, binding.right.updates());
            started = System.nanoTime();
            tr.indexMaintenance().prepare();
            double population = micros(started);
            assertEquals(fixture.workload.cats.size(), binding.left.updates());
            assertEquals(fixture.workload.homes.size(), binding.right.updates());
            assertEquals(fixture.workload.cats.size(), binding.left.extractions());
            MultiJoin<Cat, Dwelling<Cat, ?>, String> join = tr.index(fixture.homes);
            started = System.nanoTime();
            for (int i = 0; i < 10000; i++)
            {
                consumed += join.getLeft("Zone-0").size();
            }
            double warm = micros(started) / 10000;
            started = System.nanoTime();
            for (int i = 0; i < 1000; i++)
            {
                Cat added = new Cat("Black", "Zone-0");
                for (int repeat = 0; repeat < fixture.repeats; repeat++)
                {
                    tr.create(added);
                }
            }
            double pending = micros(started);
            long prior = binding.left.updates();
            started = System.nanoTime();
            tr.indexMaintenance().prepare();
            double flush = micros(started);
            assertEquals(1000, binding.left.updates() - prior);
            Object storage = Arrays.asList(storage(binding.left), storage(binding.right));
            long liveBytes = BenchmarkMemory.reachable(storage);
            started = System.nanoTime();
            tr.commit();
            double boundary = micros(started);
            assertEquals(0, binding.left.retainedReferences());
            assertEquals(0, binding.right.retainedReferences());
            long closedBytes = BenchmarkMemory.reachable(storage);
            if (BenchmarkMemory.available())
            {
                for (Object retained : BenchmarkMemory.graph(storage))
                {
                    assertFalse(retained instanceof Cat || retained instanceof Dwelling<?, ?>);
                }
            }
            System.out.printf(Locale.ROOT,
                    "  phases-us begin=%.1f report=%.1f populate=%.1f warm-query=%.1f "
                            + "pending-report=%.1f pending-flush=%.1f commit-dispose=%.1f "
                            + "report-bytes=%d live-storage=%d closed-storage=%d%n",
                    construction, reporting, population, warm, pending, flush, boundary,
                    reportBytes, liveBytes, closedBytes);
        }
        finally
        {
            tr.close();
        }
    }

    private Object storage(ManagedSide<?, ?> side) throws Exception
    {
        Field field = ManagedSide.class.getDeclaredField("membership");
        field.setAccessible(true);
        return field.get(side);
    }

    private void measure(String name, Runnable action)
    {
        long bytes = BenchmarkMemory.allocated();
        long started = System.nanoTime();
        action.run();
        double elapsed = micros(started);
        long allocated = BenchmarkMemory.allocated() - bytes;
        System.out.printf(Locale.ROOT, "  %s %.1f us/workflow allocated=%d%n", name, elapsed, allocated);
    }

    private double micros(long started)
    {
        return (System.nanoTime() - started) / 1000d;
    }

    static class Fixture implements AutoCloseable
    {
        final Existential ex = new Existential();
        final ManagedRelationshipWorkload workload;
        final IndexDefinition<MultiJoin<Cat, Dwelling<Cat, ?>, String>> homes;
        final IndexDefinition<MultiIndex<String, Cat>> manual;
        final AtomicLong checks = new AtomicLong();
        final int repeats;
        final int rules;
        final boolean eager;

        Fixture(int count, int groups, int repeats, int rules)
        {
            this(count, groups, repeats, rules, false);
        }

        Fixture(int count, int groups, int repeats, int rules, boolean eager)
        {
            this.eager = eager;
            this.repeats = repeats;
            this.rules = rules;
            workload = new ManagedRelationshipWorkload(count, groups);
            Existential previous = Ex.instance(ex);
            try
            {
                homes = new ConfigureManagedRelationships().configure(OP,
                        eager ? IndexingPolicy.inherit().automatic(false) : IndexingPolicy.inherit());
                ContextBuilder checksContext = ex.configure().context(OP);
                for (int i = 0; i < rules; i++)
                {
                    checksContext.invariant(Cat.class).create(cat -> {
                        checks.incrementAndGet();
                        return true;
                    }, "Managed workload check " + i);
                }
                manual = IndexDefinition.multi("manual", Cat.class, cat -> cat.location())
                        .withPolicy(IndexingPolicy.inherit().automatic(false));
                ex.configure().context(OP).index(manual);
            }
            finally
            {
                Ex.instance(previous);
            }
            Tr setup = ex.begin(OP);
            setup.close();
        }

        ManagedBinding binding(Tr tr)
        {
            for (ManagedBinding binding : tr.indexMaintenance().bindings())
            {
                if (binding.definition == homes)
                {
                    return binding;
                }
            }
            throw new AssertionError("Missing managed relationship binding");
        }

        void workflow(boolean reject)
        {
            long prior = checks.get();
            if (reject)
            {
                workload.cats.get(0).color = "Grey";
            }
            Tr tr = ex.begin(OP);
            try
            {
                if (eager)
                {
                    workload.reportManual(tr, repeats, tr.index(homes));
                }
                else
                {
                    workload.report(tr, repeats);
                }
                if (reject)
                {
                    assertThrows(ValidationStageExceptions.class, () -> tr.commit());
                    tr.rollback();
                }
                else
                {
                    tr.commit();
                }
                assertEquals((long) workload.cats.size() * rules, checks.get() - prior);
            }
            finally
            {
                workload.cats.get(0).color = "Black";
                tr.close();
            }
        }

        void manual()
        {
            Tr tr = ex.begin(OP);
            try
            {
                MultiIndex<String, Cat> index = tr.index(manual);
                index.addAll(workload.cats);
                assertEquals(workload.homes.size(), index.size());
            }
            finally
            {
                tr.close();
            }
        }

        public void close()
        {
            ex.close();
        }
    }
}
