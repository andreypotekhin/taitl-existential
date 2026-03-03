package com.taitl.existential.specs.library_usage;

import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.handlers.combined_event_handlers.*;
import com.taitl.existential.specs.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanHandleCombinedEventEffects extends SpecBase
{
    {
        autoConfigure = false;
    }

    @BeforeEach
    public void setup()
    {
        super.setup();
    }

    @AfterEach
    public void cleanup()
    {
        super.cleanup();
    }

    @Test
    @DisplayName("User can define effects for combined events (CU, UD, CUD)")
    void handleCombinedEventEffects()
    {
        AtomicInteger cudCalls = new AtomicInteger();
        AtomicInteger udCalls = new AtomicInteger();
        Effect<Cat> effect = new Effect<>(Cat.class)
                .add(new OnCUD<>(c -> cudCalls.incrementAndGet(), "cud"))
                .add(new OnUD<>(c -> udCalls.incrementAndGet(), "ud"));

        assertDoesNotThrow(() -> {
            // @formatter:off
            ex.configure()
                    .context(op)
                        .effect(effect)
                        .done();
            // @formatter:on
            String tran = ex.begin(op).id();
            Cat created = new Cat(CityTestData.BLACK_CAT.color(), CityTestData.BLACK_CAT.location());
            Cat updated = new Cat(CityTestData.BLACK_CAT.color(), "library");
            ex.port(null, created, tran);
            ex.port(created, updated, tran);
            ex.port(updated, null, tran);
            ex.commit(tran);
        });

        assertEquals(3, cudCalls.get());
        assertEquals(2, udCalls.get());
    }
}
