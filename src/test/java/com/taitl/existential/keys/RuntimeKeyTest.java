package com.taitl.existential.keys;

import com.taitl.existential.events.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class RuntimeKeyTest
{

    @Test
    void valueOfUsesIdentityForEquality()
    {
        String one = new String("alpha");
        String two = new String("alpha");
        Create<String> event = new Create<>(one);
        RuntimeKey<String> key1 = RuntimeKey.valueOf(event, new TypeKey<>(String.class), one);
        RuntimeKey<String> key2 = RuntimeKey.valueOf(event, new TypeKey<>(String.class), one);
        RuntimeKey<String> key3 = RuntimeKey.valueOf(event, new TypeKey<>(String.class), two);
        assertThat(key1.equals(key2), is(true));
        assertThat(key1.equals(key3), is(false));
        assertThat(key1.equals(null), is(false));
        assertThat(key1.equals("alpha"), is(false));
    }

    @Test
    void toStringIncludesKeyAndEntity()
    {
        String value = "alpha";
        Create<String> event = new Create<>(value);
        RuntimeKey<String> key = RuntimeKey.valueOf(event, new TypeKey<>(String.class), value);
        assertThat(key.toString(), is("Create<String>+alpha"));
    }

    @Test
    void validateRequiresKeyAndEntity()
    {
        RuntimeKeyProbe<String> probe = new RuntimeKeyProbe<>("alpha");
        assertDoesNotThrow(probe::validate);
        probe.nullKey();
        assertThrows(IllegalStateException.class, probe::validate);
        probe = new RuntimeKeyProbe<>("alpha");
        probe.nullEntity();
        assertThrows(IllegalStateException.class, probe::validate);
    }

    static class RuntimeKeyProbe<T> extends RuntimeKey<T>
    {
        RuntimeKeyProbe(T t)
        {
            super(t);
        }

        void nullKey()
        {
            key = null;
        }

        void nullEntity()
        {
            entity = null;
        }
    }
}
