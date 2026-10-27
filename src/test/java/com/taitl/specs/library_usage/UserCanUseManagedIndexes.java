package com.taitl.specs.library_usage;

import com.taitl.existential.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.quantifiers.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class UserCanUseManagedIndexes
{
    private static final String OP = "/orders/save";

    @Test
    void maintainsSeedReadUpdateReplacementAndDeletionWithoutResurrection()
    {
        IndexDefinition<SingleIndex<Integer, Order>> orders =
                IndexDefinition.single("orders", Order.class, order -> order.id);
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            ex.configure().context(OP).index(orders);
            Tr tr = ex.begin(OP);
            Order first = new Order(42, "draft");
            Order next = new Order(42, "approved");
            SingleIndex<Integer, Order> index = tr.index(orders);
            index.addAll(List.of(first));
            tr.read(next);
            assertSame(next, index.get(42));
            next.id = 43;
            tr.update(next);
            assertNull(index.get(42));
            assertSame(next, index.get(43));
            tr.delete(next);
            assertNull(index.get(43));
            assertFalse(index.contains(first));
            tr.commit();
            assertThrows(ExistentialException.class, () -> index.get(42));
        }
    }

    @Test
    void distinctEqualObjectsRemainDistinctAndMutableHashesDoNotStrandEntries()
    {
        IndexDefinition<MultiIndex<Integer, Order>> orders =
                IndexDefinition.multi("orders", Order.class, order -> order.id);
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            ex.configure().context(OP).index(orders);
            Tr tr = ex.begin(OP);
            Order first = new Order(42, "draft");
            Order equal = new Order(42, "draft");
            tr.create(first);
            tr.create(equal);
            MultiIndex<Integer, Order> index = tr.index(orders);
            assertEquals(2, index.get(42).size());
            first.id = 43;
            tr.update(first);
            assertSame(equal, index.get(42).iterator().next());
            assertSame(first, index.get(43).iterator().next());
            Order replacement = new Order(44, "approved");
            tr.transit(first, replacement);
            assertNull(index.get(43));
            assertSame(replacement, index.get(44).iterator().next());
            tr.commit();
        }
    }

    @Test
    void resolvesManagedAllAndExistsPerHandleAndPreservesSuccessfulCheckpoints()
    {
        IndexDefinition<SingleIndex<Integer, Order>> orders =
                IndexDefinition.single("orders", Order.class, order -> order.id);
        IndexDefinition<MultiJoin<Order, Approval, Integer>> approvals =
                IndexDefinition.multiJoin("approvals", Order.class, Approval.class,
                        order -> order.id, approval -> approval.orderId);
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            // @formatter:off
            ex.configure().context(OP)
                    .index(orders).index(approvals)
                    .invariant(Order.class)
                        .all(orders, (order, index) -> index.get(order.id) == order, "Current order snapshot")
                        .exists(approvals, join -> join.left(), "Every order has approval");
            // @formatter:on
            Tr tr = ex.begin(OP);
            Order order = new Order(42, "approved");
            tr.read(new Approval(42));
            tr.create(order);
            tr.checkpoint();
            order.status = "ready";
            tr.update(order);
            tr.checkpoint();
            tr.commit();

            Tr independent = ex.begin(OP);
            independent.create(new Order(43, "draft"));
            assertThrows(ValidationStageExceptions.class, () -> independent.commit());
            assertThrows(ExistentialException.class, () -> independent.index(orders).size());
            independent.rollback();
        }
    }

    @Test
    void composesManagedExistsInsideAllWithoutAnImplicitCurrentTransaction()
    {
        IndexDefinition<MultiJoin<Order, Approval, Integer>> approvals =
                IndexDefinition.multiJoin("approvals", Order.class, Approval.class,
                        order -> order.id, approval -> approval.orderId);
        Exists<Order> exists = new Exists<>(approvals, join -> join.left(), "Approval exists");
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            ex.configure()
                    .context(OP)
                    .index(approvals)
                    .invariant(Order.class)
                    .all(exists, "Approved order");
            Tr tr = ex.begin(OP);
            tr.read(new Approval(42));
            tr.create(new Order(42, "approved"));
            tr.commit();
            assertThrows(IllegalStateException.class, () -> exists.test(new Order(42, "approved")));
        }
    }

    @Test
    void matchesExplicitGenericSubjectsAndRejectsUnregisteredOrConflictingTokens()
    {
        IndexDefinition<MultiIndex<String, Box<String>>> strings = IndexDefinition.multi("strings",
                new com.taitl.existential.keys.TypeKey<Box<String>>() {
                }, box -> box.value);
        try (Existential ex = new Existential())
        {
            ex.shutdownPolicy(ShutdownPolicy.graceful(java.time.Duration.ofMillis(100)));
            ex.configure().context(OP).index(strings);
            Tr tr = ex.begin(OP);
            Box<String> value = new Box<>("text");
            tr.create(value, new com.taitl.existential.keys.TypeKey<Box<String>>() {
            });
            tr.create(new Box<>(42), new com.taitl.existential.keys.TypeKey<Box<Integer>>() {
            });
            assertSame(value, tr.index(strings).get("text").iterator().next());
            assertEquals(1, tr.index(strings).size());
            IndexDefinition<SingleIndex<Integer, String>> unknown =
                    IndexDefinition.single("unknown", String.class, word -> word.length());
            assertThrows(IllegalArgumentException.class, () -> tr.index(unknown));
            tr.commit();
        }
        try (Existential ex = new Existential())
        {
            IndexDefinition<SingleIndex<Integer, String>> conflict =
                    IndexDefinition.single("strings", String.class, word -> word.length());
            ex.configure().context(OP).index(strings).index(conflict);
            assertThrows(IllegalArgumentException.class, () -> ex.begin(OP));
            assertEquals(0, ex.transactions().logic().registry().size());
        }
    }

    static class Box<T>
    {
        final T value;

        Box(T value)
        {
            this.value = value;
        }
    }

    static class Order
    {
        int id;
        String status;

        Order(int id, String status)
        {
            this.id = id;
            this.status = status;
        }

        public boolean equals(Object other)
        {
            return other instanceof Order && id == ((Order) other).id
                    && Objects.equals(status, ((Order) other).status);
        }

        public int hashCode()
        {
            return Objects.hash(id, status);
        }
    }

    static class Approval
    {
        final int orderId;

        Approval(int orderId)
        {
            this.orderId = orderId;
        }
    }
}
