package com.taitl.existential.benchmarks;

import org.roaringbitmap.*;
import com.taitl.ex.logic.execution.data.*;

import java.util.*;

/**
 * Test-only representation adapter for identical sorted declaration membership.
 * Each candidate owns copied prepared rows; native combination returns that representation.
 * Single rows are reused without union allocation and multirow accumulators are private.
 * Direct selection emits the same immutable ordered handlers without forcing bitmaps through
 * an intermediate identifier array. The legacy combine adapter remains for historical controls.
 *
 * The adapter retains all three alternatives for reproducible comparisons. RoaringBitmap is
 * strictly a benchmark dependency; this class neither selects production policy nor enters
 * the library distribution.
 */
class MembershipRows
{
    String kind;
    Object[] rows;
    int slots;

    MembershipRows(String kind, int[][] input)
    {
        this.kind = kind;
        rows = new Object[input.length];
        for (int i = 0; i < input.length; i++)
        {
            if (input[i].length > 0)
                slots = Math.max(slots, input[i][input[i].length - 1] + 1);
            if (kind.equals("arrays"))
            {
                rows[i] = input[i].clone();
            }
            else if (kind.equals("bitset"))
            {
                BitSet row = new BitSet(input[i].length == 0 ? 0 : input[i][input[i].length - 1] + 1);
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
        Object nativeResult = nativeCombine(intersection);
        if (nativeResult instanceof int[])
        {
            return (int[]) nativeResult;
        }
        if (nativeResult instanceof BitSet)
        {
            BitSet bits = (BitSet) nativeResult;
            int[] result = new int[bits.cardinality()];
            int count = 0;
            for (int id = bits.nextSetBit(0); id >= 0; id = bits.nextSetBit(id + 1))
            {
                result[count++] = id;
            }
            return result;
        }
        return ((RoaringBitmap) nativeResult).toArray();
    }

    RuleSelection select(RuleInventory inventory)
    {
        Object members = nativeCombine(false);
        if (members instanceof int[])
        {
            return inventory.selection((int[]) members);
        }
        if (members instanceof BitSet)
        {
            return inventory.selection((BitSet) members, false);
        }
        RoaringBitmap bitmap = (RoaringBitmap) members;
        IntIterator iterator = bitmap.getIntIterator();
        return inventory.selection(new PrimitiveIterator.OfInt() {
            public boolean hasNext()
            {
                return iterator.hasNext();
            }

            public int nextInt()
            {
                return iterator.next();
            }
        }, bitmap.getCardinality(), false);
    }

    Object nativeCombine(boolean intersection)
    {
        if (rows.length == 1)
        {
            return rows[0];
        }
        if (kind.equals("arrays"))
        {
            int[] result = rows.length == 0 ? new int[0] : (int[]) rows[0];
            for (int i = 1; i < rows.length; i++)
            {
                result = merge(result, (int[]) rows[i], intersection);
            }
            return result;
        }
        if (kind.equals("bitset"))
        {
            BitSet result = intersection && rows.length > 0 ? (BitSet) ((BitSet) rows[0]).clone() : new BitSet(slots);
            for (int i = intersection ? 1 : 0; i < rows.length; i++)
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
            return result;
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
        return result;
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
        return count == result.length ? result : Arrays.copyOf(result, count);
    }
}
