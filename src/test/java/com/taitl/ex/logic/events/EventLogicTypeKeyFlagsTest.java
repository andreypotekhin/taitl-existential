package com.taitl.ex.logic.events;

import com.taitl.ex.core.existential.ExistentialEvents;
import com.taitl.ex.logic.indexing.IndexingLogic;
import com.taitl.existential.Existential;
import com.taitl.existential.constants.Flags;
import com.taitl.existential.events.access_events.Change;
import com.taitl.existential.keys.EventKey;
import com.taitl.existential.keys.RuntimeKey;
import com.taitl.existential.keys.TypeKey;
import com.taitl.existential.transactions.Tr;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class EventLogicTypeKeyFlagsTest
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
        ex.on(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
        EventLogic logic = new EventLogic(new ExistentialEvents(ex));

        assertThat(logic.typeKey("ok").toString(), is("java.lang.String"));
    }

    @Test
    void runtimeIndexingUsesFullNamesAcrossTypeAndEventKeysWhenFlagEnabled()
    {
        ex = new Existential();
        ex.on(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
        ExistentialEvents ev = new ExistentialEvents(ex);
        EventLogic logic = new EventLogic(ev);
        IndexingLogic indexing = new IndexingLogic(ev);
        Tr tr = new Tr("/api/test", UUID.randomUUID(), ex.transactions().logic());

        Change<String> event = new Change<>("ok");
        TypeKey<String> type = logic.typeKey("ok");
        indexing.indexEvent(event, "ok", type, tr);

        assertThat(tr.runtimeIndexes().encounteredTypeKeys.contains(TypeKey.valueOfFull(String.class)), is(true));
        assertThat(tr.runtimeIndexes().encounteredEventKeys.contains(EventKey.valueOf(event, type, true)), is(true));
        assertThat(tr.runtimeIndexes().encounteredUniqueEvents.contains(RuntimeKey.valueOfFull(event, type, "ok")),
                is(true));
    }
}
