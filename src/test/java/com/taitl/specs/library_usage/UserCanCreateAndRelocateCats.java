package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;
import com.taitl.specs.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanCreateAndRelocateCats extends SpecBase
{
    private static final String OP = "/night-city/cats/save";

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

    @Test
    void createsBlackCatOnlyWhenItHasADwelling() throws Exception
    {
        Cat cat = new Cat("Black", "Park");
        Dwelling<Cat, Rugs> dwelling = new Dwelling<>("Grey", "Park");
        new ConfigureCats().configureLifecycle(OP, Map.of(cat, List.of(dwelling)));

        Tr tr = ex.begin(OP);
        tr.create(cat);
        tr.commit();

        Cat nonBlack = new Cat("Grey", "Park");
        Tr rejected = ex.begin(OP);
        rejected.create(nonBlack);
        ValidationStageExceptions violation = assertThrows(ValidationStageExceptions.class, rejected::commit);
        assertTrue(violation.getCause().getMessage().contains("Cats are born black"));
        rejected.rollback();
    }

    @Test
    void rejectsCatWithoutDwellingAtItsLocation() throws Exception
    {
        Cat cat = new Cat("Black", "Rooftop");
        new ConfigureCats().configureLifecycle(OP, Map.of(cat, List.of(new Dwelling<Cat, Rugs>("Grey", "Park"))));

        Tr tr = ex.begin(OP);
        tr.create(cat);
        ValidationStageExceptions violation = assertThrows(ValidationStageExceptions.class, tr::commit);
        assertTrue(violation.getCause().getMessage().contains("Every new cat must have a dwelling"));
        tr.rollback();
    }

    @Test
    void allowsRelocationWhenColorIsPreservedAndDestinationHasDwelling() throws Exception
    {
        Cat before = new Cat("Black", "Park");
        Cat after = new Cat("Black", "Garden");
        Map<Cat, Collection<Dwelling<Cat, ?>>> dwellings = new HashMap<>();
        dwellings.put(before, List.of(new Dwelling<Cat, Rugs>("Grey", "Park"),
                new Dwelling<Cat, Rugs>("Red", "Garden")));
        dwellings.put(after, dwellings.get(before));
        new ConfigureCats().configureLifecycle(OP, dwellings);

        Tr tr = ex.begin(OP);
        tr.transit(before, after);
        tr.commit();
    }

    @Test
    void rejectsRelocationThatChangesColor() throws Exception
    {
        Cat before = new Cat("Black", "Park");
        Cat after = new Cat("White", "Garden");
        Map<Cat, Collection<Dwelling<Cat, ?>>> dwellings = new HashMap<>();
        Collection<Dwelling<Cat, ?>> available = List.of(new Dwelling<Cat, Rugs>("Grey", "Park"),
                new Dwelling<Cat, Rugs>("Red", "Garden"));
        dwellings.put(before, available);
        dwellings.put(after, available);
        new ConfigureCats().configureLifecycle(OP, dwellings);

        Tr tr = ex.begin(OP);
        tr.transit(before, after);
        ValidationStageExceptions violation = assertThrows(ValidationStageExceptions.class, tr::commit);
        assertTrue(violation.getCause().getMessage().contains("A cat's color cannot change"));
        tr.rollback();
    }
}
