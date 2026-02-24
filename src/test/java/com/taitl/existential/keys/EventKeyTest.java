package com.taitl.existential.keys;

import com.taitl.existential.events.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class EventKeyTest
{

    @Test
    void valueOfString()
    {
        EventKey key = EventKey.valueOf("Create<String>");
        assertThat(key.toString(), is("Create<String>"));
    }

    @Test
    void valueOfObject()
    {
        EventKey key = EventKey.valueOf(new Sample());
        assertThat(key.toString(), is("Sample"));
    }

    @Test
    void valueOfFullObject()
    {
        EventKey key = EventKey.valueOfFull(new Sample());
        assertThat(key.toString(), is(Sample.class.getName()));
    }

    @Test
    void valueOfEventAndTypeKey()
    {
        Create<String> event = new Create<>("alpha");
        TypeKey<String> typeKey = new TypeKey<>() {
        };
        EventKey key = EventKey.valueOf(event, typeKey);
        assertThat(key.toString(), is("Create<String>"));
    }

    @Test
    void valueOfEventAndTypeKeyFull()
    {
        Create<String> event = new Create<>("alpha");
        TypeKey<String> typeKey = TypeKey.valueOfFull(String.class);
        EventKey key = EventKey.valueOfFull(event, typeKey);
        assertThat(key.toString(), is(event.getClass().getName() + "<" + typeKey + ">"));
    }

    @Test
    void valueOfEventAndType()
    {
        Create<String> event = new Create<>("alpha");
        EventKey key = EventKey.valueOf(event, "String");
        assertThat(key.toString(), is("Create<String>"));
    }

    @Test
    void valueOfClassAndType()
    {
        EventKey key = EventKey.valueOf(Create.class, "String");
        assertThat(key.toString(), is("Create<String>"));
    }

    @Test
    void valueOfStringRejectsNull()
    {
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            EventKey.valueOf((String) null);
        }).getMessage(), containsString("'key' must not be null"));
    }

    @Test
    void valueOfEventRejectsNullParts()
    {
        Create<String> event = new Create<>("alpha");
        TypeKey<String> typeKey = new TypeKey<>() {
        };
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            EventKey.valueOf(null, typeKey);
        }).getMessage(), containsString("'event' must not be null"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            EventKey.valueOf(event, (TypeKey<String>) null);
        }).getMessage(), containsString("'typeKey' must not be null"));
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            EventKey.valueOf(event, (String) null);
        }).getMessage(), containsString("'type' must not be null"));
    }

    @Test
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

    static class Sample
    {
    }
}
