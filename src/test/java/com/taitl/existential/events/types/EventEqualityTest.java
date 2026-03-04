package com.taitl.existential.events.types;

import com.taitl.existential.events.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class EventEqualityTest
{
    @Test
    @DisplayName("Entity events are equal when class and entity reference match")
    void entityEventsEqualByClassAndReference()
    {
        String entity = new String("v");
        Create<String> left = new Create<>(entity);
        Create<String> right = new Create<>(entity);

        assertEquals(left, right);
        assertEquals(left.hashCode(), right.hashCode());
    }

    @Test
    @DisplayName("Entity events are not equal when event classes differ")
    void entityEventsNotEqualWhenClassDiffers()
    {
        String entity = new String("v");

        assertNotEquals(new Create<>(entity), new Update<>(entity));
    }

    @Test
    @DisplayName("Entity events are not equal when entity references differ")
    void entityEventsNotEqualWhenReferenceDiffers()
    {
        String leftEntity = new String("v");
        String rightEntity = new String("v");

        assertNotEquals(new Create<>(leftEntity), new Create<>(rightEntity));
    }

    @Test
    @DisplayName("Bi events are equal when class and both references match")
    void biEventsEqualByClassAndReferences()
    {
        String before = new String("before");
        String after = new String("after");
        Port<String> left = new Port<>(before, after);
        Port<String> right = new Port<>(before, after);

        assertEquals(left, right);
        assertEquals(left.hashCode(), right.hashCode());
    }

    @Test
    @DisplayName("Bi events are not equal when event classes differ")
    void biEventsNotEqualWhenClassDiffers()
    {
        String before = new String("before");
        String after = new String("after");

        assertNotEquals(new Transit<>(before, after), new Port<>(before, after));
    }

    @Test
    @DisplayName("Bi events are not equal when before or after references differ")
    void biEventsNotEqualWhenReferencesDiffer()
    {
        String before = new String("before");
        String after = new String("after");

        assertNotEquals(new Port<>(before, after), new Port<>(new String("before"), after));
        assertNotEquals(new Port<>(before, after), new Port<>(before, new String("after")));
    }
}
