package com.taitl.existential.events.types;

import com.taitl.existential.events.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class EventEqualityTest
{
    @Nested
    class EntityEvents
    {
        @Test
        @DisplayName("Are equal when class and entity reference match")
        void equalByClassAndReference()
        {
            String entity = new String("v");
            Create<String> left = new Create<>(entity);
            Create<String> right = new Create<>(entity);

            assertEquals(left, right);
            assertEquals(left.hashCode(), right.hashCode());
        }

        @Test
        @DisplayName("Are not equal when event classes differ")
        void notEqualWhenClassDiffers()
        {
            String entity = new String("v");

            assertNotEquals(new Create<>(entity), new Update<>(entity));
        }

        @Test
        @DisplayName("Are not equal when entity references differ")
        void notEqualWhenReferenceDiffers()
        {
            String leftEntity = new String("v");
            String rightEntity = new String("v");

            assertNotEquals(new Create<>(leftEntity), new Create<>(rightEntity));
        }
    }

    @Nested
    class BiEvents
    {
        @Test
        @DisplayName("Are equal when class and both references match")
        void equalByClassAndReferences()
        {
            String before = new String("before");
            String after = new String("after");
            Port<String> left = new Port<>(before, after);
            Port<String> right = new Port<>(before, after);

            assertEquals(left, right);
            assertEquals(left.hashCode(), right.hashCode());
        }

        @Test
        @DisplayName("Are not equal when event classes differ")
        void notEqualWhenClassDiffers()
        {
            String before = new String("before");
            String after = new String("after");

            assertNotEquals(new Transit<>(before, after), new Port<>(before, after));
        }

        @Test
        @DisplayName("Are not equal when before or after references differ")
        void notEqualWhenReferencesDiffer()
        {
            String before = new String("before");
            String after = new String("after");

            assertNotEquals(new Port<>(before, after), new Port<>(new String("before"), after));
            assertNotEquals(new Port<>(before, after), new Port<>(before, new String("after")));
        }
    }
}
