package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Acceptance for QuantifiersAndIndexes.spec.md#entity-quantifier-validation-scope.
 */
class UserCanValidateEntityQuantifiers
{
    static final String OP = "/cats/validate";
    Existential ex;

    @BeforeEach
    void setup()
    {
        ex = new Existential();
    }

    @AfterEach
    void cleanup()
    {
        ex.close();
    }

    @ParameterizedTest
    @EnumSource(Reported.class)
    @DisplayName("All rejects each admitted event and evaluates its selected endpoint once")
    void rejectsAll(Reported reported) throws Exception
    {
        List<Cat> seen = new ArrayList<>();
        ex.configure().context(OP).invariant(Cat.class).all(cat -> {
            seen.add(cat);
            return false;
        }, "Cat must have a location");
        Cat before = new Cat("White", "Park");
        Cat after = new Cat("Black", "Garden");
        Tr tr = ex.begin(OP);
        reported.send(tr, before, after);
        ValidationStageExceptions failure = assertThrows(ValidationStageExceptions.class, tr::commit);
        assertInstanceOf(PredicateFailure.class, failure.getCause());
        assertTrue(failure.getCause().getMessage().contains("Cat must have a location"));
        assertTrue(failure.getCause().getMessage().contains(PredicateFailure.TROUBLESHOOTING_SECTION));
        assertEquals(1, seen.size());
        assertSame(reported.deleted() ? before : after, seen.get(0));
        tr.rollback();
    }

    @ParameterizedTest
    @EnumSource(Reported.class)
    @DisplayName("Exists rejects a missing key for every admitted event")
    void rejectsMissingKey(Reported reported) throws Exception
    {
        ex.configure()
                .context(OP)
                .invariant(Cat.class)
                .exists(Map.<Cat, String> of(), "Cat must have a dwelling");
        Tr tr = ex.begin(OP);
        reported.send(tr, new Cat("White", "Park"), new Cat("Black", "Garden"));
        ValidationStageExceptions failure = assertThrows(ValidationStageExceptions.class, tr::commit);
        assertInstanceOf(PredicateFailure.class, failure.getCause());
        assertTrue(failure.getCause().getMessage().contains("Cat must have a dwelling"));
        tr.rollback();
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    @DisplayName("Expressions keep handler order, execute once, and use live sources through cached lookup")
    void orderedCachedLookup(boolean fullNames) throws Exception
    {
        if (fullNames)
        {
            ex.on(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
        }
        Cat cat = new Cat("Black", "Park");
        TypeKey<Cat> key = new TypeKey<>(Cat.class, fullNames);
        List<String> order = new ArrayList<>();
        Map<Cat, Boolean> dwellings = new HashMap<>(Map.of(cat, true));
        // @formatter:off
        ex.configure().context("/cats")
            .effect(key).create(value -> order.add("before"))
            .invariant(key)
                .all(value -> order.add("all"), "All runs")
                .exists(dwellings, (value, present) -> order.add("exists") && present, "Exists runs")
            .effect(key).create(value -> order.add("after"));
        // @formatter:on
        for (int i = 0; i < 2; i++)
        {
            Tr tr = ex.begin(OP);
            tr.create(cat, key);
            tr.create(cat, key);
            tr.commit();
            assertEquals(List.of("before", "all", "exists", "after"), order);
            order.clear();
        }
        dwellings.clear();
        Tr rejected = ex.begin(OP);
        rejected.create(cat, key);
        assertThrows(ValidationStageExceptions.class, rejected::commit);
        rejected.rollback();
    }

    @Test
    @DisplayName("Conditional All skips nonmatching entities and does not leak failures into later transactions")
    void conditionalAll() throws Exception
    {
        AtomicInteger calls = new AtomicInteger();
        ex.configure()
                .context(OP)
                .invariant(Cat.class)
                .all(cat -> "Black".equals(cat.color), cat -> {
                    calls.incrementAndGet();
                    return "Park".equals(cat.location());
                }, "Black cats live in the Park");
        Tr skipped = ex.begin(OP);
        skipped.create(new Cat("Grey", "Garden"));
        skipped.commit();
        assertEquals(0, calls.get());
        Tr rejected = ex.begin(OP);
        rejected.create(new Cat("Black", "Garden"));
        assertThrows(ValidationStageExceptions.class, rejected::commit);
        rejected.rollback();
        Tr accepted = ex.begin(OP);
        accepted.create(new Cat("Black", "Park"));
        accepted.commit();
        assertEquals(2, calls.get());
    }

    @Test
    @DisplayName("Checkpoint rejects missing membership and repeats validation after caller repairs its source")
    void checkpoint() throws Exception
    {
        Cat cat = new Cat("Black", "Park");
        Collection<Cat> cats = new ArrayList<>();
        AtomicInteger calls = new AtomicInteger();
        ex.configure()
                .context(OP)
                .invariant(Cat.class)
                .all(value -> calls.incrementAndGet() > 0, "Count validation attempts")
                .exists(cats, "Cat must remain in its collection");
        Tr tr = ex.begin(OP);
        tr.create(cat);
        assertThrows(ValidationStageExceptions.class, tr::checkpoint);
        cats.add(cat);
        tr.checkpoint();
        tr.commit();
        assertEquals(3, calls.get());
    }

    @Test
    @DisplayName("Collection, set and map existence accept matches and reject absent entities")
    void sources() throws Exception
    {
        Cat cat = new Cat("Black", "Park");
        ex.configure()
                .context(OP)
                .invariant(Cat.class)
                .exists(List.of(cat), "collection membership")
                .exists(Set.of(cat), "set membership")
                .exists(Map.of(cat, "dwelling"), "map membership");
        Tr accepted = ex.begin(OP);
        accepted.create(cat);
        accepted.commit();
        Tr rejected = ex.begin(OP);
        rejected.create(new Cat("Grey", "Garden"));
        ValidationStageExceptions failure = assertThrows(ValidationStageExceptions.class, rejected::commit);
        assertTrue(failure.getCause().getMessage().contains("collection membership"));
        rejected.rollback();
    }

    @Test
    @DisplayName("Event-bound create predicates retain their narrower scope")
    void eventScope() throws Exception
    {
        ex.configure()
                .context(OP)
                .invariant(Cat.class)
                .create(cat -> false, "Creation is forbidden")
                .all(cat -> true, "Updates are admitted");
        Tr accepted = ex.begin(OP);
        accepted.update(new Cat("Black", "Park"));
        accepted.commit();
        Tr rejected = ex.begin(OP);
        rejected.create(new Cat("Black", "Park"));
        assertThrows(ValidationStageExceptions.class, rejected::commit);
        rejected.rollback();
    }

    @Test
    @DisplayName("Transition validation uses the destination rather than the source")
    void destination() throws Exception
    {
        Cat before = new Cat("Grey", "Park");
        Cat after = new Cat("Black", "Garden");
        ex.configure()
                .context(OP)
                .invariant(Cat.class)
                .all(cat -> "Black".equals(cat.color), "Destination is black")
                .exists(Map.of(after, "dwelling"), "Destination has a dwelling");
        Tr tr = ex.begin(OP);
        tr.transit(before, after);
        tr.commit();
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    @DisplayName("Generic expression matches execute once per declaration and do not affect unrelated entity types")
    void genericTypes(boolean fullNames) throws Exception
    {
        if (fullNames)
        {
            ex.on(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
        }
        AtomicInteger exact = new AtomicInteger();
        AtomicInteger wildcard = new AtomicInteger();
        AtomicInteger handler = new AtomicInteger();
        TypeKey<Dwelling<Cat, Rugs>> type = new TypeKey<Dwelling<Cat, Rugs>>(fullNames) {
        };
        TypeKey<Dwelling<Cat, ?>> broad = new TypeKey<Dwelling<Cat, ?>>(fullNames) {
        };
        ex.configure()
                .context(OP)
                .invariant(type)
                .all(value -> exact.incrementAndGet() > 0, "Exact dwelling")
                .invariant(broad)
                .all(value -> wildcard.incrementAndGet() > 0, "Generic dwelling")
                .effect(type)
                .create(value -> handler.incrementAndGet())
                .invariant(Cat.class)
                .all(value -> false, "Unreported cats must not be scanned");
        Tr tr = ex.begin(OP);
        tr.create(new Dwelling<Cat, Rugs>("Grey", "Park"), type);
        tr.commit();
        assertEquals(1, exact.get());
        assertEquals(1, wildcard.get());
        assertEquals(1, handler.get());
    }

    @Test
    @DisplayName("Transaction-local expressions join context expressions in declaration order")
    void transactionRules() throws Exception
    {
        List<String> order = new ArrayList<>();
        ex.configure()
                .context(OP)
                .invariant(Cat.class)
                .all(cat -> order.add("context"), "Context expression")
                .transaction(() -> {
                    Transaction transaction = new Transaction(OP, "quantifiers");
                    transaction.invariant(new Invariant<>(Cat.class)
                            .all(cat -> order.add("transaction"), "Transaction expression"));
                    return transaction;
                });
        Tr tr = ex.begin(OP);
        tr.create(new Cat("Black", "Park"));
        tr.commit();
        assertEquals(List.of("context", "transaction"), order);
    }

    enum Reported
    {
        CREATE, UPDATE, TRANSIT, PORT_CREATE, PORT_DELETE, DELETE, READ, WRITE;

        boolean deleted()
        {
            return this == DELETE || this == PORT_DELETE;
        }

        void send(Tr tr, Cat before, Cat after) throws Exception
        {
            switch (this)
            {
            case CREATE:
                tr.create(after);
                break;
            case UPDATE:
                tr.update(after);
                break;
            case TRANSIT:
                tr.transit(before, after);
                break;
            case PORT_CREATE:
                tr.port(null, after);
                break;
            case PORT_DELETE:
                tr.port(before, null);
                break;
            case DELETE:
                tr.delete(before);
                break;
            case READ:
                tr.read(after);
                break;
            case WRITE:
                tr.write(after);
                break;
            }
        }
    }
}
