package com.taitl.existential.benchmarks;

import java.lang.instrument.*;
import java.lang.management.*;
import java.lang.reflect.*;
import java.util.*;
import com.sun.management.ThreadMXBean;

/**
 * Test-only instrumentation for allocation and reachable-object-graph measurements.
 * Per-thread allocation comes from ThreadMXBean; a premain agent supplies shallow object sizes,
 * which are summed once per object reached through instance fields and reference arrays.
 *
 * Graph traversal excludes Class objects, static roots, and weak referents. The result is
 * reachable owned storage, not exclusive dominator retention or process heap usage. Missing
 * instrumentation reports -1 unless required explicitly; inaccessible fields fail the run.
 */
public class BenchmarkMemory
{
    protected static Instrumentation instrumentation;
    protected static ThreadMXBean allocations = (ThreadMXBean) ManagementFactory.getThreadMXBean();

    public static void premain(String options, Instrumentation agent)
    {
        instrumentation = agent;
    }

    public static long allocated()
    {
        if (!allocations.isThreadAllocatedMemoryEnabled())
        {
            allocations.setThreadAllocatedMemoryEnabled(true);
        }
        return allocations.getThreadAllocatedBytes(Thread.currentThread().getId());
    }

    public static boolean available()
    {
        return instrumentation != null;
    }

    public static long reachable(Object root)
    {
        if (instrumentation == null)
        {
            if (Boolean.getBoolean("benchmark.heap.required"))
            {
                throw new IllegalStateException("Run with the test-only BenchmarkMemory javaagent for heap evidence");
            }
            return -1;
        }
        long bytes = 0;
        for (Object value : graph(root))
        {
            bytes += instrumentation.getObjectSize(value);
        }
        return bytes;
    }

    public static Set<Object> graph(Object root)
    {
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        Deque<Object> pending = new ArrayDeque<>();
        pending.add(root);
        while (!pending.isEmpty())
        {
            Object value = pending.removeLast();
            if (value instanceof Class<?> || !visited.add(value))
            {
                continue;
            }
            Class<?> type = value.getClass();
            if (type.isArray())
            {
                if (!type.getComponentType().isPrimitive())
                {
                    for (Object item : (Object[]) value)
                    {
                        if (item != null)
                        {
                            pending.add(item);
                        }
                    }
                }
                continue;
            }
            for (Class<?> parent = type; parent != null; parent = parent.getSuperclass())
            {
                for (Field field : parent.getDeclaredFields())
                {
                    if (!Modifier.isStatic(field.getModifiers()) && !field.getType().isPrimitive()
                            && !(parent.equals(java.lang.ref.Reference.class) && field.getName().equals("referent")))
                    {
                        try
                        {
                            if (!field.trySetAccessible())
                            {
                                throw new IllegalStateException(
                                        "Open " + parent.getPackageName() + " for heap profiling");
                            }
                            Object referenced = field.get(value);
                            if (referenced != null)
                            {
                                pending.add(referenced);
                            }
                        }
                        catch (IllegalAccessException failure)
                        {
                            throw new IllegalStateException("Cannot profile " + field, failure);
                        }
                    }
                }
            }
        }
        return visited;
    }
}
