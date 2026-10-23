package com.taitl.existential.benchmarks;

import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.examples.night_city.app.*;
import com.taitl.existential.*;
import java.lang.management.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import com.sun.management.ThreadMXBean;

/**
 * Runs a warmed rejection-and-rollback workflow with interchangeable library JARs.
 * The same Night City fixture and rejection operation are reused for baseline and candidate,
 * with reflection limited to invoking the unchanged example benchmark operation.
 *
 * Warmup is separate from the timed batch; the probe reports per-workflow elapsed time,
 * thread allocation, and an asserted callback count from the shared fixture. Each attempt
 * closes through the example operation, and the enclosing library always closes on exit.
 */
public class RejectionProbe
{
    public static void main(String[] args) throws Exception
    {
        CityWorkload workload = new CityWorkload(10000, 200);
        AtomicLong counter = new AtomicLong();
        Method reject = NightCityExecutionBenchmark.class.getDeclaredMethod("reject", Existential.class);
        reject.setAccessible(true);
        ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        bean.setThreadAllocatedMemoryEnabled(true);
        try (Existential ex = new Existential())
        {
            Existential previous = Ex.instance(ex);
            try
            {
                String op = "/night-city/cats/execution-benchmark";
                new ConfigureCats().configureWorkload(op, workload.dwellings, 100, counter);
                new ConfigureLooks().configureWorkload(op, 100, new AtomicLong());
                for (int i = 0; i < 200; i++)
                    reject.invoke(null, ex);
                long before = bean.getThreadAllocatedBytes(Thread.currentThread().getId());
                long started = System.nanoTime();
                for (int i = 0; i < 1000; i++)
                    reject.invoke(null, ex);
                long elapsed = System.nanoTime() - started;
                long allocated = bean.getThreadAllocatedBytes(Thread.currentThread().getId()) - before;
                System.out.printf(Locale.ROOT, "rejection-probe %.1f µs/workflow %d bytes/workflow callbacks=%d%n",
                        elapsed / 1000d / 1000, allocated / 1000, counter.get());
                if (counter.get() != 120000)
                {
                    throw new AssertionError("Expected 120000 rejected-rule callbacks");
                }
            }
            finally
            {
                Ex.instance(previous);
            }
        }
    }
}
