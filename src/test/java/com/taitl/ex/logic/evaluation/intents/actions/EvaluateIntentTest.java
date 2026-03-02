package com.taitl.ex.logic.evaluation.intents.actions;

import com.taitl.existential.events.access_events.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.access_handlers.*;
import com.taitl.existential.handlers.types.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class EvaluateIntentTest
{
    EvaluateIntent evaluateIntent = new EvaluateIntent();

    @Nested
    class Call
    {
        @Nested
        class Condition
        {
            @Test
            @DisplayName("Returns true when on handler condition is met")
            void met() throws Exception
            {
                EventHandler<?> intent = new OnRead<String>(value -> true, null);
                assertTrue(evaluateIntent.call(intent, new Read<>("cat")));
            }

            @Test
            @DisplayName("Returns false when on handler condition is not met")
            void notMet() throws Exception
            {
                EventHandler<?> intent = new OnRead<String>(value -> false, null);
                assertFalse(evaluateIntent.call(intent, new Read<>("cat")));
            }
        }

        @Nested
        class EventMatching
        {
            @Test
            @DisplayName("Returns false when bi intent does not match non bi event")
            void biIntentAgainstSingleEvent() throws Exception
            {
                EventHandler<?> intent = new OnMutate<String>((left, right) -> true, null, "must match");
                assertFalse(evaluateIntent.call(intent, new Read<>("cat")));
            }
        }

        @Nested
        class UnsupportedHandlers
        {
            @Test
            @DisplayName("Wraps unsupported handler types as intent violation")
            void wrapsAsIntentViolation()
            {
                EventHandler<Object> unsupported = new EventHandler<>() {
                    public String description()
                    {
                        return "unsupported";
                    }
                };

                IntentViolation ex =
                        assertThrows(IntentViolation.class, () -> evaluateIntent.call(unsupported, new Read<>("cat")));
                assertTrue(ex.getMessage().contains("Intent evaluation failed"));
            }
        }
    }
}
