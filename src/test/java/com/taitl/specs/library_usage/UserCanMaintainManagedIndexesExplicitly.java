package com.taitl.specs.library_usage;

import com.taitl.existential.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class UserCanMaintainManagedIndexesExplicitly
{
    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void maintainsSnapshotsAndGroupsWithoutBypassingIdentity(boolean automatic)
    {
        IndexDefinition<SingleIndex<Integer, Item>> single =
                IndexDefinition.single("single", Item.class, item -> item.key);
        IndexDefinition<MultiIndex<Integer, Item>> multi = IndexDefinition.multi("multi", Item.class, item -> item.key);
        try (Existential ex = new Existential())
        {
            ex.configure()
                    .context("/manual")
                    .index(single)
                    .index(multi)
                    .indexingPolicy(IndexingPolicy.inherit().automatic(automatic));
            Tr tr = ex.begin("/manual");
            try
            {
                SingleIndex<Integer, Item> snapshots = tr.index(single);
                MultiIndex<Integer, Item> groups = tr.index(multi);
                Item draft = new Item(1);
                Item approved = new Item(1);
                snapshots.add(draft);
                assertSame(draft, snapshots.put(1, approved));
                assertFalse(snapshots.contains(draft));
                assertTrue(snapshots.containsValue(approved));
                assertTrue(snapshots.contains(1, item -> item == approved));
                assertFalse(snapshots.remove(1, draft));
                assertNull(snapshots.removeMatching(1, item -> false));
                approved.key = 2;
                snapshots.reindex(1, approved);
                assertFalse(snapshots.containsKey(1));
                assertEquals(Set.of(2), snapshots.keySet());
                assertEquals(List.of(approved), new ArrayList<>(snapshots.values()));
                assertSame(approved, snapshots.entrySet().iterator().next().getValue());
                assertSame(approved, snapshots.removeMatching(2, item -> true));
                assertNull(snapshots.remove(2));
                snapshots.putAll(Map.of(1, draft, 2, approved));
                assertTrue(snapshots.remove(2, approved));
                snapshots.index(draft, approved);
                approved.key = 3;
                snapshots.reindex(2, 3, approved);
                assertSame(approved, snapshots.remove(3));
                snapshots.addAll(List.of(draft));
                snapshots.clear();
                assertTrue(snapshots.isEmpty());
                groups.addAll(List.of(draft, approved));
                Item sibling = new Item(1);
                groups.add(sibling);
                assertTrue(groups.contains(draft));
                assertTrue(groups.contains(1, values -> values.size() == 2));
                assertTrue(groups.containsValue(groups.get(1)));
                assertEquals(2, groups.keySet().size());
                assertEquals(2, groups.values().size());
                assertEquals(2, groups.entrySet().size());
                assertNull(groups.removeValue(7, draft));
                assertNull(groups.remove(7, item -> true));
                assertNull(groups.remove(1, item -> false));
                assertEquals(Set.of(sibling), groups.remove(1, item -> item == sibling));
                draft.key = 2;
                groups.reindex(1, draft);
                assertFalse(groups.containsKey(1));
                draft.key = 4;
                groups.reindex(2, 4, draft);
                groups.index(approved, sibling);
                assertFalse(groups.contains(approved));
                assertSame(draft, groups.removeValue(4, draft));
                groups.putAll(Map.of(5, Set.of(draft)));
                assertEquals(Set.of(draft), groups.put(5, Set.of(approved)));
                assertEquals(Set.of(approved), groups.remove(5));
                groups.add(1, draft);
                assertThrows(UnsupportedOperationException.class, () -> groups.get(1).clear());
                groups.clear();
                assertTrue(groups.isEmpty());
                tr.commit();
            }
            finally
            {
                tr.close();
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void keepsBothJoinDirectionsConsistentThroughExplicitMoves(boolean automatic)
    {
        IndexDefinition<SingleJoin<Item, Item, Integer>> single = IndexDefinition.singleJoin("single", Item.class,
                Item.class, item -> item.key, item -> item.key);
        IndexDefinition<MultiJoin<Item, Item, Integer>> multi = IndexDefinition.multiJoin("multi", Item.class,
                Item.class, item -> item.key, item -> item.key);
        try (Existential ex = new Existential())
        {
            ex.configure()
                    .context("/join")
                    .index(single)
                    .index(multi)
                    .indexingPolicy(IndexingPolicy.inherit().automatic(automatic));
            Tr tr = ex.begin("/join");
            try
            {
                Item left = new Item(1);
                Item right = new Item(1);
                Item replacement = new Item(2);
                SingleJoin<Item, Item, Integer> snapshots = tr.index(single);
                snapshots.addAllLeft(List.of(left));
                snapshots.addAllRight(List.of(right));
                assertTrue(snapshots.containsLeft(left));
                assertTrue(snapshots.containsRight(right));
                assertSame(right, snapshots.get(left));
                assertSame(left, snapshots.get(right));
                assertSame(left, snapshots.getLeftByRight(right));
                assertSame(right, snapshots.getRightByLeft(left));
                assertEquals(1, snapshots.right().size());
                left.key = 2;
                right.key = 2;
                snapshots.reindexLeft(1, 2, left);
                snapshots.reindexRight(1, 2, right);
                snapshots.indexLeft(left, replacement);
                snapshots.indexRight(right, right);
                assertSame(replacement, snapshots.getLeft(2));
                assertSame(right, snapshots.removeRight(2, right));
                assertNull(snapshots.getRightByLeft(replacement));
                snapshots.addRight(right);
                assertSame(right, snapshots.removeRight(right));
                snapshots.addLeft(left);
                assertSame(left, snapshots.removeLeft(left));
                snapshots.addLeft(2, replacement);
                assertSame(replacement, snapshots.removeLeft(2, replacement));
                snapshots.clear();
                assertTrue(snapshots.left().isEmpty());
                MultiJoin<Item, Item, Integer> groups = tr.index(multi);
                groups.addAllLeft(List.of(left));
                groups.addAllRight(List.of(right));
                assertTrue(groups.containsLeft(left));
                assertTrue(groups.containsRight(right));
                assertEquals(Set.of(right), groups.get(left));
                assertEquals(Set.of(left), groups.get(right));
                assertEquals(Set.of(left), groups.getLeftByRight(right));
                assertEquals(1, groups.right().size());
                left.key = 3;
                right.key = 3;
                groups.reindexLeft(2, 3, left);
                groups.reindexRight(2, 3, right);
                replacement.key = 3;
                groups.indexLeft(left, replacement);
                groups.indexRight(right, right);
                assertEquals(Set.of(right), groups.getRightByLeft(replacement));
                assertSame(right, groups.removeRight(right));
                groups.addRight(right);
                assertSame(right, groups.removeRight(3, right));
                groups.addLeft(left);
                assertSame(left, groups.removeLeft(left));
                assertSame(replacement, groups.removeLeft(3, replacement));
                groups.clear();
                assertNull(groups.getRight(3));
                assertTrue(groups.left().isEmpty());
                tr.commit();
            }
            finally
            {
                tr.close();
            }
        }
    }

    static class Item
    {
        int key;

        Item(int key)
        {
            this.key = key;
        }
    }
}
