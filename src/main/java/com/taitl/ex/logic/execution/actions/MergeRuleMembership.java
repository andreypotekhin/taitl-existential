package com.taitl.ex.logic.execution.actions;

import java.util.*;

/**
 * Unions sorted declaration-membership rows while retaining configured execution order.
 * A declaration reached through several matching type or event paths appears once in the result;
 * distinct declaration identifiers remain independent regardless of callback equality.
 *
 * Two-pointer array merges avoid bitmap allocation for the library's compact rule inventories.
 * Input rows are immutable after compilation, allowing empty merges to reuse an existing row.
 * The result is consumed to materialize an immutable ordered handler selection.
 */
public class MergeRuleMembership
{
    public int[] call(List<int[]> rows)
    {
        int[] result = new int[0];
        for (int[] row : rows)
        {
            if (result != row)
            {
                result = merge(result, row);
            }
        }
        return result;
    }

    protected int[] merge(int[] left, int[] right)
    {
        if (left.length == 0)
        {
            return right;
        }
        if (right.length == 0)
        {
            return left;
        }
        int[] result = new int[left.length + right.length];
        int i = 0;
        int j = 0;
        int count = 0;
        while (i < left.length || j < right.length)
        {
            if (j == right.length || i < left.length && left[i] < right[j])
            {
                result[count++] = left[i++];
            }
            else if (i == left.length || right[j] < left[i])
            {
                result[count++] = right[j++];
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
