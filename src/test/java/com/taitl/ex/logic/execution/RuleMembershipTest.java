package com.taitl.ex.logic.execution;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.logic.execution.actions.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class RuleMembershipTest
{
    RulePattern row(int... ids)
    {
        return new RulePattern(new RuleDeclaration(0, "Create<Cat>",
                new OnCreate<Cat>(cat -> {
                }, "Observe each declaration")), ids);
    }

    @Test
    void unionsWithoutChangingPreparedRows()
    {
        RulePattern left = row(0, 2, 255);
        RulePattern right = row(1, 2, 254);
        BitSet before = (BitSet) left.members.clone();
        BitSet result = new MergeRuleMembership().call(List.of(left, right, left), 256);
        assertArrayEquals(new int[] { 0, 1, 2, 254, 255 }, result.stream().toArray());
        assertEquals(before, left.members);
        result.clear();
        assertEquals(before, left.members);
        assertEquals(3, right.members.cardinality());
    }

    @Test
    void avoidsUnionForOneRowAndKeepsEmptyResultsIndependent()
    {
        RulePattern one = row(1, 8);
        MergeRuleMembership merge = new MergeRuleMembership();
        assertSame(one.members, merge.call(List.of(one), 9));
        BitSet empty = merge.call(List.of(), 9);
        assertTrue(empty.isEmpty());
        empty.set(1);
        assertTrue(merge.call(List.of(), 9).isEmpty());
    }

    @Test
    void givesConcurrentResolutionsPrivateAccumulators() throws Exception
    {
        RulePattern first = row(0, 128);
        RulePattern second = row(1, 255);
        ExecutorService pool = Executors.newFixedThreadPool(8);
        try
        {
            List<Callable<BitSet>> work = new ArrayList<>();
            for (int i = 0; i < 32; i++)
            {
                work.add(() -> new MergeRuleMembership().call(List.of(first, second), 256));
            }
            List<Future<BitSet>> results = pool.invokeAll(work);
            Set<BitSet> identities = Collections.newSetFromMap(new IdentityHashMap<>());
            for (Future<BitSet> future : results)
            {
                assertArrayEquals(new int[] { 0, 1, 128, 255 }, future.get().stream().toArray());
                identities.add(future.get());
            }
            assertEquals(32, identities.size());
            assertEquals(2, first.members.cardinality());
            assertEquals(2, second.members.cardinality());
        }
        finally
        {
            pool.shutdownNow();
            assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    @Test
    void materializesNativeBitsAndCopiesCallerCollections()
    {
        OnCreate<Cat> create = new OnCreate<>(cat -> {
        }, "Observe a create");
        OnTransit<Cat> move = new OnTransit<>((before, after) -> {
        }, "Observe a move");
        RuleInventory inventory = new RuleInventory(List.of(new RuleDeclaration(0, "Create<Cat>", create),
                new RuleDeclaration(1, "Transit<Cat>", move)));
        BitSet members = new BitSet(2);
        members.set(0, 2);
        RuleSelection selection = inventory.selection(members, true);
        assertEquals(List.of(create, move), selection.handlers());
        assertTrue(selection.biHandler());
        assertTrue(selection.memoRequired());
        assertEquals(2, members.cardinality());
        List<Ev<?>> supplied = new ArrayList<>(List.of(create));
        RuleSelection copied = new RuleSelection(supplied, false);
        supplied.clear();
        assertEquals(List.of(create), copied.handlers());
        assertThrows(UnsupportedOperationException.class, () -> selection.handlers().clear());
    }
}
