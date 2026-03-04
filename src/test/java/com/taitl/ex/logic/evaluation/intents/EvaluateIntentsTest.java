package com.taitl.ex.logic.evaluation.intents;

import com.taitl.ex.logic.evaluation.*;
import com.taitl.existential.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EvaluateIntentsTest
{
    static class TestEvaluationLogic extends EvaluationLogic
    {
        protected boolean splitElementaryToCompound;

        TestEvaluationLogic(
                com.taitl.ex.logic.transactions.TransactionLogic tl,
                boolean splitElementaryToCompound)
        {
            super(tl);
            this.splitElementaryToCompound = splitElementaryToCompound;
        }

        public boolean splitElementaryToCompound()
        {
            return splitElementaryToCompound;
        }
    }

    protected Existential ex;

    @AfterEach
    void cleanup()
    {
        if (ex != null)
        {
            ex.close();
        }
    }

    @Test
    @DisplayName("Split and group includes port family when elementary to compound is enabled")
    void splitIncludesPortWhenEnabled()
    {
        ex = new Existential();
        EvaluationLogic logic = new TestEvaluationLogic(ex.transactions().logic(), true);
        EvaluateIntents evaluateIntents = new EvaluateIntents(logic);
        String entity = new String("new");
        RuntimeKey<String> runtimeKey =
                RuntimeKey.valueOf(new Create<>(entity), new TypeKey<>(String.class), entity, false);

        Map<EventType, List<RuntimeKey<String>>> grouped = evaluateIntents.splitAndGroupByEventType(runtimeKey);

        assertTrue(hasEventType(grouped, Create.class));
        assertTrue(hasEventType(grouped, Port.class));
        assertTrue(hasEventType(grouped, CUD.class));
    }

    @Test
    @DisplayName("Split and group skips port family when elementary to compound is disabled")
    void splitSkipsPortWhenDisabled()
    {
        ex = new Existential();
        EvaluationLogic logic = new TestEvaluationLogic(ex.transactions().logic(), false);
        EvaluateIntents evaluateIntents = new EvaluateIntents(logic);
        String entity = new String("new");
        RuntimeKey<String> runtimeKey =
                RuntimeKey.valueOf(new Create<>(entity), new TypeKey<>(String.class), entity, false);

        Map<EventType, List<RuntimeKey<String>>> grouped = evaluateIntents.splitAndGroupByEventType(runtimeKey);

        assertTrue(hasEventType(grouped, Create.class));
        assertFalse(hasEventType(grouped, Port.class));
        assertFalse(hasEventType(grouped, CUD.class));
    }

    protected boolean hasEventType(Map<EventType, List<RuntimeKey<String>>> grouped, Class<?> eventClass)
    {
        return grouped.keySet().stream().anyMatch(eventType -> eventType.eventClass().equals(eventClass));
    }
}
