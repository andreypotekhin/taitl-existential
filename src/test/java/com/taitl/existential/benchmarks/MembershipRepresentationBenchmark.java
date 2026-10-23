package com.taitl.existential.benchmarks;

import org.junit.jupiter.api.*;
import org.roaringbitmap.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MembershipRepresentationBenchmark
{
    @Test
    void comparesMembershipRows()
    {
        int warmup = Integer.getInteger("benchmark.warmup.iterations", 3);
        int iterations = Integer.getInteger("benchmark.measure.iterations", 5);
        for (String shape : List.of("sparse", "broad", "clustered"))
        {
            int[][] expected = rows(shape, 20, 256);
            int[] reference = unionArrays(shape, 20, 256);
            int[] bits = unionBits(shape, 20, 256);
            int[] roaring = unionRoaring(shape, 20, 256);
            assertArrayEquals(reference, bits);
            assertArrayEquals(reference, roaring);
            long arrays = measure(warmup, iterations, () -> unionArrays(shape, 20, 256));
            long bitsets = measure(warmup, iterations, () -> unionBits(shape, 20, 256));
            long bitmaps = measure(warmup, iterations, () -> unionRoaring(shape, 20, 256));
            System.out.printf(Locale.ROOT,
                    "membership shape=%s rows=%d slots=%d iterations=%d arrays=%d ns/op bitset=%d ns/op "
                            + "roaring=%d ns/op matches=%d%n",
                    shape, expected.length, 256, iterations, arrays, bitsets, bitmaps, reference.length);
        }
    }

    protected int[][] rows(String shape, int rowCount, int slots)
    {
        int[][] result = new int[rowCount][];
        for (int row = 0; row < rowCount; row++)
        {
            List<Integer> members = new ArrayList<>();
            for (int id = 0; id < slots; id++)
            {
                if (matches(shape, row, id))
                {
                    members.add(id);
                }
            }
            result[row] = members.stream().mapToInt(id -> id).toArray();
        }
        return result;
    }

    protected boolean matches(String shape, int row, int id)
    {
        if ("sparse".equals(shape))
        {
            return (id + row * 7) % 23 == 0;
        }
        if ("broad".equals(shape))
        {
            return (id + row) % 2 == 0;
        }
        int start = row * 8;
        return id >= start && id < start + 32;
    }

    protected int[] unionArrays(String shape, int rowCount, int slots)
    {
        int[] union = new int[0];
        for (int row = 0; row < rowCount; row++)
        {
            int[] values = new int[slots];
            int size = 0;
            for (int id = 0; id < slots; id++)
            {
                if (matches(shape, row, id))
                {
                    values[size++] = id;
                }
            }
            union = merge(union, Arrays.copyOf(values, size));
        }
        return union;
    }

    protected int[] merge(int[] left, int[] right)
    {
        int[] result = new int[left.length + right.length];
        int i = 0;
        int j = 0;
        int size = 0;
        while (i < left.length && j < right.length)
        {
            if (left[i] < right[j])
            {
                result[size++] = left[i++];
            }
            else if (left[i] > right[j])
            {
                result[size++] = right[j++];
            }
            else
            {
                result[size++] = left[i++];
                j++;
            }
        }
        while (i < left.length)
        {
            result[size++] = left[i++];
        }
        while (j < right.length)
        {
            result[size++] = right[j++];
        }
        return Arrays.copyOf(result, size);
    }

    protected int[] unionBits(String shape, int rowCount, int slots)
    {
        BitSet union = new BitSet(slots);
        for (int row = 0; row < rowCount; row++)
        {
            BitSet bits = new BitSet(slots);
            for (int id = 0; id < slots; id++)
            {
                if (matches(shape, row, id))
                {
                    bits.set(id);
                }
            }
            union.or(bits);
        }
        int[] result = new int[union.cardinality()];
        int size = 0;
        for (int id = union.nextSetBit(0); id >= 0; id = union.nextSetBit(id + 1))
        {
            result[size++] = id;
        }
        return result;
    }

    protected int[] unionRoaring(String shape, int rowCount, int slots)
    {
        RoaringBitmap union = new RoaringBitmap();
        for (int row = 0; row < rowCount; row++)
        {
            RoaringBitmap bitmap = new RoaringBitmap();
            for (int id = 0; id < slots; id++)
            {
                if (matches(shape, row, id))
                {
                    bitmap.add(id);
                }
            }
            union.or(bitmap);
        }
        int[] result = new int[union.getCardinality()];
        IntIterator values = union.getIntIterator();
        int size = 0;
        while (values.hasNext())
        {
            result[size++] = values.next();
        }
        return result;
    }

    protected long measure(int warmup, int iterations, Runnable work)
    {
        for (int i = 0; i < warmup; i++)
        {
            work.run();
        }
        long started = System.nanoTime();
        for (int i = 0; i < iterations; i++)
        {
            work.run();
        }
        return (System.nanoTime() - started) / iterations;
    }
}
