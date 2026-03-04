package com.taitl.ex.logic.events;

import com.taitl.ex.core.existential.ExistentialEvents;
import com.taitl.ex.logic.indexing.IndexingLogic;
import com.taitl.existential.Existential;
import com.taitl.existential.constants.Flags;
import com.taitl.existential.events.Update;
import com.taitl.existential.keys.EventKey;
import com.taitl.existential.keys.RuntimeKey;
import com.taitl.existential.keys.TypeKey;
import com.taitl.existential.transactions.Tr;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class EventLogicTypeKeyFlagsTest
{
    Existential ex;
    EventLogic logic;
    ExistentialEvents events;
    String shortName;
    String fullName;

    @BeforeEach
    void setup()
    {
        ex = new Existential();
        events = new ExistentialEvents(ex);
        logic = new EventLogic(events);
        shortName = String.class.getSimpleName();
        fullName = String.class.getName();
    }

    @AfterEach
    void cleanup()
    {
        ex.close();
    }

    void useFullNames()
    {
        ex.on(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
        logic = new EventLogic(events);
    }

    @Nested
    class InferredType
    {
        @Test
        @DisplayName("Uses short class name by default")
        void shortNameByDefault()
        {
            TypeKey<String> a = logic.typeKey("ok");
            TypeKey<String> b = logic.typeKey("again");

            assertThat(a.toString(), is(shortName));
            assertThat(a == b, is(true));
        }

        @Test
        @DisplayName("Uses full class name when flag enabled")
        void fullNameWithFlag()
        {
            useFullNames();
            assertThat(logic.typeKey("ok").toString(), is(fullName));
        }
    }

    @Nested
    class RuntimeIndexes
    {
        @Test
        @DisplayName("Uses full names across type and event keys when flag enabled")
        void fullNamesAcrossKeys()
        {
            useFullNames();
            IndexingLogic indexing = new IndexingLogic(events);
            Tr tr = new Tr("/api/test", UUID.randomUUID(), ex.transactions().logic(), logic);

            Update<String> event = new Update<>("ok");
            TypeKey<String> type = logic.typeKey("ok");
            indexing.indexEvent(event, "ok", type, tr);

            assertThat(tr.runtimeIndexes().encounteredTypeKeys.contains(TypeKey.valueOf(String.class, true)), is(true));
            assertThat(tr.runtimeIndexes().encounteredEventKeys.contains(EventKey.valueOf(event, type, true)),
                    is(true));
            assertThat(tr.runtimeIndexes().encounteredUniqueEvents.contains(
                    RuntimeKey.valueOf(event, type, "ok", true)), is(true));
        }
    }
}
