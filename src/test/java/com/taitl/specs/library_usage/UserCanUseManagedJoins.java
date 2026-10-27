package com.taitl.specs.library_usage;

import com.taitl.existential.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static com.taitl.specs.library_usage.UserCanUseManagedIndexes.*;

class UserCanUseManagedJoins
{
    @Test
    void maintainsBothJoinDirectionsAcrossReplacementMovesAndDeletes()
    {
        IndexDefinition<SingleJoin<Order, Approval, Integer>> orders = IndexDefinition.singleJoin(
                "approved", Order.class, Approval.class, order -> order.id, approval -> approval.orderId);
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            ex.configure().context("/join").index(orders);
            Tr tr = ex.begin("/join");
            SingleJoin<Order, Approval, Integer> join = tr.index(orders);
            Order draft = new Order(42, "draft");
            Order approved = new Order(42, "approved");
            Approval approval = new Approval(42);
            join.addAllLeft(List.of(draft));
            join.addAllRight(List.of(approval));
            Map<Order, Approval> left = join.left();
            assertSame(approval, left.get(draft));
            tr.read(approved);
            assertFalse(left.containsKey(draft));
            assertSame(approved, join.right().get(approval));
            approved.id = 43;
            tr.update(approved);
            assertFalse(join.right().containsKey(approval));
            assertNull(join.getLeft(42));
            tr.delete(approved);
            assertNull(join.getLeft(43));
            assertThrows(UnsupportedOperationException.class, () -> left.put(draft, approval));
            tr.commit();
        }
    }

    @Test
    void indexesLargeGroupsWithoutPairMaterializationAndProtectsGroupViews()
    {
        IndexDefinition<MultiJoin<Order, Order, Integer>> related = IndexDefinition.multiJoin(
                "related", Order.class, Order.class, order -> order.id, order -> order.id);
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            ex.configure().context("/groups").index(related);
            Tr tr = ex.begin("/groups");
            for (int i = 0; i < 100; i++)
            {
                tr.read(new Order(42, "item"));
            }
            MultiJoin<Order, Order, Integer> join = tr.index(related);
            assertEquals(100, join.getLeft(42).size());
            assertEquals(100, join.getRight(42).size());
            assertEquals(100, join.left().size());
            Set<Order> group = join.getLeft(42);
            assertThrows(UnsupportedOperationException.class, () -> group.clear());
            tr.commit();
            assertThrows(com.taitl.existential.exceptions.ExistentialException.class, () -> group.size());
        }
    }
}
