package com.taitl.existential.keys;

import com.taitl.existential.events.*;
import com.taitl.existential.events.types.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class EventKeyTest
{
    @Nested
    class ValueOf
    {
        @Test
        @DisplayName("From string")
        void fromString()
        {
            EventKey key = EventKey.valueOf("Create<String>");
            assertThat(key.toString(), is("Create<String>"));
            assertThat(key.typeKey().toString(), is("String"));
        }

        @Test
        @DisplayName("From object")
        void fromObject()
        {
            EventKey key = EventKey.valueOf(new Sample(), false);
            assertThat(key.toString(), is("Sample<Sample>"));
        }

        @Test
        @DisplayName("From object stores type key")
        void objectStoresTypeKey()
        {
            EventKey key = EventKey.valueOf(new Sample(), false);
            assertThat(key.typeKey().toString(), is("Sample"));
        }

        @Test
        @DisplayName("From full object")
        void fromFullObject()
        {
            EventKey key = EventKey.valueOf(new Sample(), true);
            assertThat(key.toString(), is(Sample.class.getName() + "<" + Sample.class.getName() + ">"));
        }

        @Test
        @DisplayName("From event and type key")
        void fromEventAndTypeKey()
        {
            Create<String> event = new Create<>("alpha");
            TypeKey<String> typeKey = new TypeKey<>() {
            };
            EventKey key = EventKey.valueOf(event, typeKey, false);
            assertThat(key.toString(), is("Create<String>"));
        }

        @Test
        @DisplayName("From event and type key full")
        void fromEventAndTypeKeyFull()
        {
            Create<String> event = new Create<>("alpha");
            TypeKey<String> typeKey = TypeKey.valueOf(String.class, false);
            EventKey key = EventKey.valueOf(event, typeKey, true);
            assertThat(key.toString(), is(event.getClass().getName() + "<" + typeKey + ">"));
        }

        @Test
        @DisplayName("From event and type")
        void fromEventAndType()
        {
            Create<String> event = new Create<>("alpha");
            EventKey key = EventKey.valueOf(event, "String", false);
            assertThat(key.toString(), is("Create<String>"));
        }

        @Test
        @DisplayName("From class and type")
        void fromClassAndType()
        {
            EventKey key = EventKey.valueOf(Create.class, "String", false);
            assertThat(key.toString(), is("Create<String>"));
        }
    }

    @Nested
    class Rejects
    {
        @Test
        @DisplayName("String null")
        void nullString()
        {
            assertThat(assertThrows(IllegalArgumentException.class, () -> {
                EventKey.valueOf((String) null);
            }).getMessage(), containsString("'key' must not be null"));
        }

        @Test
        @DisplayName("Event null parts")
        void eventNullParts()
        {
            Create<String> event = new Create<>("alpha");
            TypeKey<String> typeKey = new TypeKey<>() {
            };
            assertThat(assertThrows(IllegalArgumentException.class, () -> {
                EventKey.valueOf((Event<String>) null, typeKey, false);
            }).getMessage(), containsString("'event' must not be null"));
            assertThat(assertThrows(IllegalArgumentException.class, () -> {
                EventKey.valueOf(event, (TypeKey<String>) null, false);
            }).getMessage(), containsString("'typeKey' must not be null"));
            assertThat(assertThrows(IllegalArgumentException.class, () -> {
                EventKey.valueOf(event, (String) null, false);
            }).getMessage(), containsString("'type' must not be null"));
        }
    }

    @Nested
    class Equality
    {
        @Test
        @DisplayName("Equals and hash code")
        void equalsAndHashCode()
        {
            EventKey a = EventKey.valueOf("Create<String>");
            EventKey b = EventKey.valueOf("Create<String>");
            EventKey c = EventKey.valueOf("Update<String>");
            assertThat(a.equals(a), is(true));
            assertThat(a.equals(b), is(true));
            assertThat(a.equals(c), is(false));
            assertThat(a.equals(null), is(false));
            assertThat(a.equals("Create<String>"), is(false));
            assertThat(a.hashCode(), is(b.hashCode()));
        }
    }

    static class Sample
    {
    }
}
