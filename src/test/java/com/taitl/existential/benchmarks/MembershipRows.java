package com.taitl.existential.benchmarks;

import org.roaringbitmap.*;

import java.util.*;

/**
 * Test-only representation adapter for identical sorted declaration membership.
 * Each candidate owns copied prepared rows and returns sorted identifiers for union or
 * intersection, allowing the harness to compare both results and ordered handler materialization.
 *
 * The adapter retains all three alternatives for reproducible comparisons. RoaringBitmap is
 * strictly a benchmark dependency; this class neither selects production policy nor enters
 * the library distribution.
 */
class MembershipRows
{
    String kind;
    Object[] rows;

    MembershipRows(String kind, int[][] input)
    {
        this.kind = kind;
        rows = new Object[input.length];
        for (int i = 0; i < input.length; i++)
        {
            if (kind.equals("arrays"))
            {
                rows[i] = input[i].clone();
            }
            else if (kind.equals("bitset"))
            {
                BitSet row = new BitSet();
                for (int id : input[i])
                {
                    row.set(id);
                }
                rows[i] = row;
            }
            else
            {
                rows[i] = RoaringBitmap.bitmapOf(input[i]);
            }
        }
    }

    int[] combine(boolean intersection)
    {
        if (kind.equals("arrays"))
        {
            int[] result = rows.length == 0 ? new int[0] : ((int[]) rows[0]).clone();
            for (int i = 1; i < rows.length; i++)
            {
                result = merge(result, (int[]) rows[i], intersection);
            }
            return result;
        }
        if (kind.equals("bitset"))
        {
            BitSet result = rows.length == 0 ? new BitSet() : (BitSet) ((BitSet) rows[0]).clone();
            for (int i = 1; i < rows.length; i++)
            {
                if (intersection)
                {
                    result.and((BitSet) rows[i]);
                }
                else
                {
                    result.or((BitSet) rows[i]);
                }
            }
            int[] ids = new int[result.cardinality()];
            int count = 0;
            for (int id = result.nextSetBit(0); id >= 0; id = result.nextSetBit(id + 1))
            {
                ids[count++] = id;
            }
            return ids;
        }
        RoaringBitmap result = rows.length == 0 ? new RoaringBitmap() : ((RoaringBitmap) rows[0]).clone();
        for (int i = 1; i < rows.length; i++)
        {
            if (intersection)
            {
                result.and((RoaringBitmap) rows[i]);
            }
            else
            {
                result.or((RoaringBitmap) rows[i]);
            }
        }
        return result.toArray();
    }

    static int[] merge(int[] left, int[] right, boolean intersection)
    {
        int[] result = new int[left.length + right.length];
        int i = 0;
        int j = 0;
        int count = 0;
        while (i < left.length || j < right.length)
        {
            if (j == right.length || i < left.length && left[i] < right[j])
            {
                if (!intersection)
                {
                    result[count++] = left[i];
                }
                i++;
            }
            else if (i == left.length || right[j] < left[i])
            {
                if (!intersection)
                {
                    result[count++] = right[j];
                }
                j++;
            }
            else
            {
                result[count++] = left[i++];
                j++;
            }
        }
        return Arrays.copyOf(result, count);
    }
}
