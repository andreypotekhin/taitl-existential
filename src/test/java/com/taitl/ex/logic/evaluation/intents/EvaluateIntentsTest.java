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

        public boolean shouldSplitElementary()
        {
            return splitElementaryToCompound;
        }
    }

    protected Existential ex;
    protected String entity;

    @BeforeEach
    void setup()
    {
        ex = new Existential();
        entity = new String("new");
    }

    @AfterEach
    void cleanup()
    {
        ex.close();
    }

    RuntimeKey<String> createKey()
    {
        return RuntimeKey.valueOf(new Create<>(entity), new TypeKey<>(String.class), entity, false);
    }

    EvaluateIntents evaluateIntents(boolean splitElementary)
    {
        EvaluationLogic logic = new TestEvaluationLogic(ex.transactions().logic(), splitElementary);
        return new EvaluateIntents(logic);
    }

    @Nested
    class SplitAndGroupByEventType
    {
        @Test
        @DisplayName("Includes port family when elementary to compound is enabled")
        void includesPortFamily()
        {
            Map<EventType, List<RuntimeKey<String>>> grouped =
                    evaluateIntents(true).splitAndGroupByEventType(createKey());

            assertTrue(hasEventType(grouped, Create.class));
            assertTrue(hasEventType(grouped, Port.class));
            assertTrue(hasEventType(grouped, CUD.class));
        }

        @Test
        @DisplayName("Skips port family when elementary to compound is disabled")
        void skipsPortFamily()
        {
            Map<EventType, List<RuntimeKey<String>>> grouped =
                    evaluateIntents(false).splitAndGroupByEventType(createKey());

            assertTrue(hasEventType(grouped, Create.class));
            assertFalse(hasEventType(grouped, Port.class));
            assertFalse(hasEventType(grouped, CUD.class));
        }
    }

    protected boolean hasEventType(Map<EventType, List<RuntimeKey<String>>> grouped, Class<?> eventClass)
    {
        return grouped.keySet().stream().anyMatch(eventType -> eventType.eventClass().equals(eventClass));
    }
}
