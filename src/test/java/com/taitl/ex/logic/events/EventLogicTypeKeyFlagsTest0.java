package com.taitl.ex.logic.events;

import java.util.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.indexing.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

class EventLogicTypeKeyFlagsTest0
{
    Existential ex;

    @AfterEach
    void cleanup()
    {
        if (ex != null)
        {
            ex.close();
        }
    }

    @Test
    void inferredTypeUsesShortClassNameByDefault()
    {
        ex = new Existential();
        EventLogic logic = new EventLogic(new ExistentialEvents(ex));

        TypeKey<String> a = logic.typeKey("ok");
        TypeKey<String> b = logic.typeKey("again");

        assertThat(a.toString(), is("String"));
        assertThat(a == b, is(true));
    }

    @Test
    void inferredTypeUsesFullClassNameWhenFlagEnabled()
    {
        ex = new Existential();
        ex.on(Flags.BEHAVIOR_TYPE_KEYS_USE_FULL_CLASS_NAMES);
        EventLogic logic = new EventLogic(new ExistentialEvents(ex));

        assertThat(logic.typeKey("ok").toString(), is("java.lang.String"));
    }

    @Test
    void runtimeIndexingUsesFullNamesAcrossTypeAndEventKeysWhenFlagEnabled()
    {
        ex = new Existential();
        ex.on(Flags.BEHAVIOR_TYPE_KEYS_USE_FULL_CLASS_NAMES);
        ExistentialEvents ev = new ExistentialEvents(ex);
        EventLogic logic = new EventLogic(ev);
        IndexingLogic indexing = new IndexingLogic(ev);
        Tr tr = new Tr("/api/test", UUID.randomUUID());

        Change<String> event = new Change<>("ok");
        TypeKey<String> type = logic.typeKey("ok");
        indexing.indexEvent(event, "ok", type, tr);

        assertThat(tr.runtimeIndexes().encounteredTypeKeys.contains(TypeKey.valueOfFull(String.class)), is(true));
        assertThat(tr.runtimeIndexes().encounteredEventKeys.contains(EventKey.valueOfFull(event, type)), is(true));
        assertThat(tr.runtimeIndexes().encounteredUniqueEvents.contains(RuntimeKey.valueOfFull(event, type, "ok")),
                is(true));
    }
}
