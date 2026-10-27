package com.taitl.ex.logic.managed_indexes;

import com.taitl.ex.logic.managed_indexes.data.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ManagedMembershipTest
{
    @Test
    void foldedSummaryMatchesEagerReplacementAtEveryQuerySchedule()
    {
        for (int queryEvery : new int[] { 1, 7, 0 })
        {
            ManagedMembership<Integer, Object> folded = new ManagedMembership<>(false);
            Map<Integer, Object> eager = new HashMap<>();
            Map<Object, Integer> oldKeys = new IdentityHashMap<>();
            Object[] values = new Object[20];
            Arrays.setAll(values, i -> new Object());
            Random random = new Random(712);
            for (int i = 0; i < 2000; i++)
            {
                Object value = values[random.nextInt(values.length)];
                Integer oldKey = oldKeys.remove(value);
                if (oldKey != null && eager.get(oldKey) == value)
                {
                    eager.remove(oldKey);
                }
                if (random.nextInt(4) == 0)
                {
                    folded.remove(value);
                }
                else
                {
                    int key = random.nextInt(10);
                    Object displaced = eager.put(key, value);
                    if (displaced != null)
                    {
                        oldKeys.remove(displaced);
                    }
                    oldKeys.put(value, key);
                    folded.add(key, value);
                }
                if (queryEvery > 0 && i % queryEvery == 0)
                {
                    assertContents(eager, folded);
                }
                assertTrue(folded.pendingChanges() <= 20);
            }
            assertContents(eager, folded);
        }
    }

    @Test
    void repeatedReportsDoNotRepeatPhysicalMaintenance()
    {
        ManagedMembership<Integer, Object> state = new ManagedMembership<>(false);
        Object value = new Object();
        for (int i = 0; i < 10000; i++)
        {
            state.add(42, value);
        }
        assertEquals(0, state.updates());
        state.flush();
        assertEquals(1, state.updates());
        state.flush();
        assertEquals(1, state.updates());
    }

    @Test
    void earlyQueriesUpdateOnlyChangedMembersOfLargeGroups()
    {
        ManagedMembership<Integer, Object> state = new ManagedMembership<>(true);
        for (int i = 0; i < 10000; i++)
        {
            state.add(42, new Object());
        }
        assertEquals(0, state.updates());
        state.flush();
        assertEquals(10000, state.updates());
        Object added = new Object();
        state.add(42, added);
        state.flush();
        assertEquals(10001, state.updates());
        state.remove(added);
        state.flush();
        assertEquals(10002, state.updates());
    }

    private void assertContents(Map<Integer, Object> expected, ManagedMembership<Integer, Object> state)
    {
        state.flush();
        assertEquals(expected.keySet(), state.applied().keySet());
        expected.forEach((key, value) -> assertSame(value, state.applied().get(key).iterator().next()));
    }
}
