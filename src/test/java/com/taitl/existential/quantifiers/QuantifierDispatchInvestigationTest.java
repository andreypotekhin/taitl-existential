package com.taitl.existential.quantifiers;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class QuantifierDispatchInvestigationTest
{
    @Test
    void directExistsRejectsMissingKey()
    {
        Exists<Cat> exists = new Exists<>(Map.of(), "missing dwelling");
        Cat cat = new Cat("Black", "Rooftop");
        assertFalse(exists.test(cat));
        assertThrows(PredicateFailure.class, () -> exists.evaluate(cat));
    }

    @Test
    void fluentExistsIsNotInvoked() throws Exception
    {
        try (Existential ex = new Existential())
        {
            AtomicInteger calls = new AtomicInteger();
            Cat cat = new Cat("Black", "Rooftop");
            ex.configure()
                    .context("/probe/exists")
                    .invariant(Cat.class)
                    .exists(Map.of(cat, "dwelling"), (subject, dwelling) -> {
                        calls.incrementAndGet();
                        return false;
                    }, "must reject");
            Tr tr = ex.begin("/probe/exists");
            tr.create(cat);
            assertDoesNotThrow(tr::commit);
            assertEquals(0, calls.get());
        }
    }

    @Test
    void fluentAllIsNotInvoked() throws Exception
    {
        try (Existential ex = new Existential())
        {
            AtomicInteger calls = new AtomicInteger();
            ex.configure()
                    .context("/probe/all")
                    .invariant(Cat.class)
                    .all(cat -> {
                        calls.incrementAndGet();
                        return false;
                    }, "must reject");
            Tr tr = ex.begin("/probe/all");
            tr.create(new Cat("Black", "Rooftop"));
            assertDoesNotThrow(tr::commit);
            assertEquals(0, calls.get());
        }
    }

    @Test
    void eventBoundExistsDoesReject() throws Exception
    {
        try (Existential ex = new Existential())
        {
            ex.configure()
                    .context("/probe/create")
                    .invariant(Cat.class)
                    .create(new Exists<Cat>(Map.of()), "missing dwelling");
            Tr tr = ex.begin("/probe/create");
            tr.create(new Cat("Black", "Rooftop"));
            assertThrows(ValidationStageExceptions.class, tr::commit);
            tr.rollback();
        }
    }
}
