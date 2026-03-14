package com.taitl.existential.specs.library_usage;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.specs.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanUseMemoForBiEvents extends SpecBase
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

    @Nested
    class Scenarios
    {
        @Test
        @DisplayName("User can memo before-state so update transit handlers see both states")
        void memoSupportsTransitHandlers() throws Exception
        {
            AtomicReference<String> beforeColor = new AtomicReference<>();
            AtomicReference<String> afterColor = new AtomicReference<>();

            // @formatter:off
            ex.configure()
                    .context(op)
                    .immediate()
                        .effect(cat.getClass())
                            .transit((before, after) -> {
                                beforeColor.set(before.color);
                                afterColor.set(after.color);
                            }, "track memo-backed transit");
            // @formatter:on

            Tr tr = ex.begin(op);
            Cat live = new Cat("Black", cat.location());
            Cat before = new Cat(live.color, live.location());
            tr.memo(before, live, Cat.class);
            live.color = "White";

            ex.update(live, tr.id());

            assertEquals("Black", beforeColor.get());
            assertEquals("White", afterColor.get());
            tr.commit();
        }

        @Test
        @DisplayName("Missing memo fails fast when transit handler would be evaluated from update")
        void missingMemoFailsForTransitHandler() throws Exception
        {
            // @formatter:off
            ex.configure()
                    .context(op)
                    .immediate()
                        .effect(cat.getClass())
                            .transit((before, after) -> { }, "require memo");
            // @formatter:on

            Tr tr = ex.begin(op);
            Cat live = new Cat("Black", cat.location());
            live.color = "White";

            MemoException error = assertThrows(MemoException.class, () -> ex.update(live, tr.id()));

            assertTrue(error.getMessage().contains("Use memo()"));
            assertTrue(error.getMessage().contains("/Troubleshooting.md#memo-state-missing"));
            tr.rollback();
        }

        @Test
        @DisplayName("Missing memo fails fast when transit intent would be evaluated from update")
        void missingMemoFailsForTransitIntent() throws Exception
        {
            // @formatter:off
            ex.configure()
                    .context(op)
                    .immediate()
                        .intent(cat.getClass())
                            .transit(after -> after.color.startsWith("W"));
            // @formatter:on

            Tr tr = ex.begin(op);
            Cat live = new Cat("Black", cat.location());
            live.color = "White";

            MemoException error = assertThrows(MemoException.class, () -> ex.update(live, tr.id()));

            assertTrue(error.getMessage().contains("Use memo()"));
            tr.rollback();
        }
    }
}
