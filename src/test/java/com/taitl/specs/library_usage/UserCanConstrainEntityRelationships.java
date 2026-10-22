package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;
import com.taitl.ex.logic.indexing.data.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.quantifiers.*;
import com.taitl.existential.transactions.*;
import com.taitl.specs.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Acceptance for QuantifiersAndIndexes.spec.md#all-exists-composition and
 * EventsAndConstraints.spec.md#explicit-evolution-and-endpoint-validation.
 */
class UserCanConstrainEntityRelationships extends SpecBase
{
    static final String OP = "/cats/relationships";

    {
        autoConfigure = false;
    }

    @BeforeEach
    void setUp()
    {
        super.setup();
    }

    @AfterEach
    void tearDown()
    {
        super.cleanup();
    }

    @ParameterizedTest
    @EnumSource(Relationship.class)
    @DisplayName("All-Exists validates a caller-supplied dwelling relationship and releases failed transactions")
    void dwelling(Relationship relationship) throws Exception
    {
        Cat cat = new Cat("Black", "Park");
        Map<Cat, Dwelling<Cat, ?>> dwellings = new HashMap<>();
        if (relationship == Relationship.MATCH || relationship == Relationship.WRONG_LOCATION)
        {
            dwellings.put(cat, new Dwelling<Cat, Rugs>("Grey",
                    relationship == Relationship.MATCH ? "Park" : "Garden"));
        }
        if (relationship == Relationship.MISSING)
        {
            dwellings.put(new Cat("Grey", "Garden"), new Dwelling<Cat, Rugs>("Grey", "Garden"));
        }
        if (relationship == Relationship.NULL_DWELLING)
        {
            dwellings.put(cat, null);
        }
        new ConfigureCats().configureRelationships(OP, dwellings);
        Map<Cat, Dwelling<Cat, ?>> original = new HashMap<>(dwellings);
        Tr tr = ex.begin(OP);
        IndexData indexes = tr.runtimeIndexes();
        tr.create(cat);
        if (relationship == Relationship.MATCH)
        {
            tr.commit();
        }
        else
        {
            ValidationStageExceptions failure = assertThrows(ValidationStageExceptions.class, tr::commit);
            assertTrue(failure.getMessage().contains(OP));
            assertTrue(failure.getCause().getMessage().contains("Every cat must have a dwelling"));
            assertThrows(NotFoundException.class, () -> ex.transactions().tr(tr.id()));
            tr.rollback();
        }
        assertEquals(original, dwellings);
        assertSame(indexes, tr.runtimeIndexes());
        assertFalse(indexes.encounteredUniqueEvents.iterator().hasNext());
        assertEquals(0, indexes.encounteredEventKeys.stream().count());
        assertEquals(0, indexes.encounteredTypeKeys.stream().count());
    }

    @Test
    @DisplayName("All-Exists membership admits equal snapshots and folds duplicate reports")
    void membership() throws Exception
    {
        Cat source = new Cat("Black", "Park");
        Cat reported = new Cat("Black", "Park");
        new ConfigureCats().configureMembership(OP, List.of(source));
        Tr accepted = ex.begin(OP);
        accepted.read(reported);
        accepted.read(reported);
        assertEquals(1, accepted.runtimeIndexes().encounteredUniqueEvents.stream().count());
        accepted.commit();
        Tr rejected = ex.begin(OP);
        rejected.read(new Cat("Black", "Garden"));
        assertThrows(ValidationStageExceptions.class, rejected::commit);
        rejected.rollback();
    }

    @Test
    @DisplayName("Conditional All-Exists skips unselected cats and rejects missing selected cats")
    void conditionalMembership() throws Exception
    {
        Cat black = new Cat("Black", "Park");
        ex.configure()
                .context(OP)
                .invariant(Cat.class)
                .all(cat -> "Black".equals(cat.color), new Exists<>(List.of(black)),
                        "Black cats belong to the supplied collection");
        Tr accepted = ex.begin(OP);
        accepted.read(new Cat("Grey", "Garden"));
        accepted.read(black);
        accepted.commit();
        Tr rejected = ex.begin(OP);
        rejected.read(new Cat("Black", "Garden"));
        assertThrows(ValidationStageExceptions.class, rejected::commit);
        rejected.rollback();
    }

    @Test
    @DisplayName("Exists predicates receive the stored source key and value for equal snapshots")
    void storedSourceObjects() throws Exception
    {
        Cat source = new Cat("Black", "Park");
        Dwelling<Cat, Rugs> dwelling = new Dwelling<>("Grey", "Park");
        ex.configure()
                .context(OP)
                .invariant(Cat.class)
                .all(new Exists<>(Map.of(source, dwelling), (matched, home) -> {
                    assertSame(source, matched);
                    assertSame(dwelling, home);
                    return true;
                }), "Source key and dwelling remain available to the relationship predicate");
        Tr tr = ex.begin(OP);
        tr.read(new Cat("Black", "Park"));
        tr.commit();
    }

    @Test
    @DisplayName("Caller can repair source membership after rejected commit using a fresh transaction")
    void repairSource() throws Exception
    {
        Cat cat = new Cat("Black", "Park");
        List<Cat> cats = new ArrayList<>();
        new ConfigureCats().configureMembership(OP, cats);
        Tr rejected = ex.begin(OP);
        rejected.create(cat);
        assertThrows(ValidationStageExceptions.class, rejected::commit);
        rejected.rollback();
        cats.add(cat);
        Tr accepted = ex.begin(OP);
        accepted.create(cat);
        accepted.commit();
        assertEquals(List.of(cat), cats);
    }

    @ParameterizedTest
    @ValueSource(strings = { "create", "delete", "relocate" })
    @DisplayName("Port creation deletion and relocation validate their selected entity against the supplied map")
    void port(String action) throws Exception
    {
        Cat before = new Cat("Black", "Park");
        Cat after = new Cat("Black", "Garden");
        new ConfigureCats().configureRelationships(OP, Map.of(
                before, new Dwelling<Cat, Rugs>("Grey", "Park"),
                after, new Dwelling<Cat, Rugs>("Grey", "Garden")));
        Tr tr = ex.begin(OP);
        tr.port("create".equals(action) ? null : before, "delete".equals(action) ? null : after);
        tr.commit();
        assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
    }

    @ParameterizedTest
    @ValueSource(strings = { "transit-before", "transit-after", "port-both" })
    @DisplayName("Invalid endpoints fail early and allow a corrected event on the same transaction")
    void invalidEndpoints(String action) throws Exception
    {
        Cat cat = new Cat("Black", "Park");
        new ConfigureCats().configureRelationships(OP, Map.of(cat, new Dwelling<Cat, Rugs>("Grey", "Park")));
        Tr tr = ex.begin(OP);
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class, () -> {
            if ("port-both".equals(action))
            {
                tr.port((Cat) null, null);
            }
            else
            {
                tr.transit("transit-before".equals(action) ? null : cat,
                        "transit-after".equals(action) ? null : cat);
            }
        });
        assertTrue(failure.getMessage().contains("/Troubleshooting.md#event-endpoints"));
        assertFalse(tr.runtimeIndexes().encounteredUniqueEvents.iterator().hasNext());
        tr.port(null, cat);
        tr.commit();
    }

    enum Relationship
    {
        MATCH, MISSING, EMPTY, WRONG_LOCATION, NULL_DWELLING
    }
}
