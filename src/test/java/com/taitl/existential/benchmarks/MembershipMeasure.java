package com.taitl.existential.benchmarks;

import com.taitl.ex.logic.execution.data.*;
import org.roaringbitmap.*;

import java.util.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Measures native membership work after a time-based warmup in fixed-size batches.
 * Every result escapes through a volatile reference, preventing discarded-result elimination;
 * checksums and equivalence assertions remain outside the measured region. Allocation is
 * current-thread storage, independent of the optional reachable-graph instrumentation.
 *
 * A minimum batch amortizes clock reads without fixing an iteration count across fast and slow
 * cases. Each benchmark process rotates candidates; this helper supplies no performance gate
 * and reports average microseconds per batch rather than a latency percentile.
 */
class MembershipMeasure
{
    static volatile Object materialized;
    static volatile long consumed;
    static final int BATCH = 64;

    void call(String description, Supplier<Object> work)
    {
        long warmup = Long.getLong("benchmark.warmup.millis", 200L) * 1_000_000;
        long duration = Long.getLong("benchmark.measure.millis", 300L) * 1_000_000;
        run(work, warmup);
        long allocated = BenchmarkMemory.allocated();
        long started = System.nanoTime();
        long batches = run(work, duration);
        long elapsed = System.nanoTime() - started;
        long bytes = BenchmarkMemory.allocated() - allocated;
        consumed = checksum(materialized);
        materialized = null;
        assertTrue(consumed > 0);
        System.out.printf(Locale.ROOT, "%s batch=%d %.1f µs/batch %.1f bytes/selection batches=%d checksum=%d%n",
                description, BATCH, elapsed / 1_000d / batches, (double) bytes / batches / BATCH,
                batches, consumed);
    }

    long run(Supplier<Object> work, long duration)
    {
        long end = System.nanoTime() + duration;
        long batches = 0;
        do
        {
            for (int i = 0; i < BATCH; i++)
            {
                materialized = work.get();
            }
            batches++;
        }
        while (System.nanoTime() < end);
        return batches;
    }

    long checksum(Object value)
    {
        if (value instanceof RuleSelection)
            return ((RuleSelection) value).handlers().size() + 1;
        if (value instanceof int[])
            return ((int[]) value).length + 1;
        if (value instanceof BitSet)
            return ((BitSet) value).cardinality() + 1;
        if (value instanceof RoaringBitmap)
            return ((RoaringBitmap) value).getCardinality() + 1;
        return ((MembershipRows) value).rows.length + 1;
    }
}
