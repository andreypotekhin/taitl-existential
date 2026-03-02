package com.taitl.existential.keys;

import com.taitl.existential.events.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class RuntimeKeyTest
{

    @Test
    @DisplayName("Value of uses identity for equality")
    void valueOfUsesIdentityForEquality()
    {
        String one = new String("alpha");
        String two = new String("alpha");
        Create<String> event = new Create<>(one);
        RuntimeKey<String> key1 = RuntimeKey.valueOf(event, new TypeKey<>(String.class), one, false);
        RuntimeKey<String> key2 = RuntimeKey.valueOf(event, new TypeKey<>(String.class), one, false);
        RuntimeKey<String> key3 = RuntimeKey.valueOf(event, new TypeKey<>(String.class), two, false);
        assertThat(key1.equals(key2), is(true));
        assertThat(key1.equals(key3), is(false));
        assertThat(key1.equals(null), is(false));
        assertThat(key1.equals("alpha"), is(false));
    }

    @Test
    @DisplayName("To string includes key and entity")
    void toStringIncludesKeyAndEntity()
    {
        String value = "alpha";
        Create<String> event = new Create<>(value);
        RuntimeKey<String> key = RuntimeKey.valueOf(event, new TypeKey<>(String.class), value, false);
        assertThat(key.toString(), is("Create<String>+alpha"));
    }

    @Test
    @DisplayName("Hash code matches equals")
    void hashCodeMatchesEquals()
    {
        String value = new String("alpha");
        Create<String> event = new Create<>(value);
        TypeKey<String> type = new TypeKey<>(String.class);
        RuntimeKey<String> key1 = RuntimeKey.valueOf(event, type, value, false);
        RuntimeKey<String> key2 = RuntimeKey.valueOf(event, type, value, false);
        assertThat(key1.equals(key2), is(true));
        assertThat(key1.hashCode(), is(key2.hashCode()));
    }

    @Test
    @DisplayName("Validate requires key and entity")
    void validateRequiresKeyAndEntity()
    {
        RuntimeKeyProbe<String> probe = new RuntimeKeyProbe<>("alpha");
        assertDoesNotThrow(probe::validate);
        IllegalStateException keyError = assertThrows(IllegalStateException.class,
                () -> RuntimeKeyProbe.nullKeyProbe("alpha"));
        assertEquals("RuntimeKey key should not be null", keyError.getMessage());

        IllegalStateException typeKeyError = assertThrows(IllegalStateException.class,
                () -> RuntimeKeyProbe.nullTypeKeyProbe("alpha"));
        assertEquals("RuntimeKey typeKey should not be null", typeKeyError.getMessage());

        IllegalStateException entityError = assertThrows(IllegalStateException.class, RuntimeKeyProbe::nullEntityProbe);
        assertEquals("RuntimeKey entity should not be null", entityError.getMessage());
    }

    static class RuntimeKeyProbe<T> extends RuntimeKey<T>
    {
        RuntimeKeyProbe(T t)
        {
            super(t);
        }

        static <T> void nullKeyProbe(T entity)
        {
            new RuntimeKeyProbe<>(null, entity).validate();
        }

        static <T> void nullTypeKeyProbe(T entity)
        {
            new RuntimeKeyProbe<>(EventKey.valueOf("Create<String>"), null, entity).validate();
        }

        static void nullEntityProbe()
        {
            new RuntimeKeyProbe<>(EventKey.valueOf("Create<String>"), null).validate();
        }

        RuntimeKeyProbe(EventKey<T> key, T entity)
        {
            super(key, entity);
        }

        RuntimeKeyProbe(EventKey<T> key, TypeKey<T> typeKey, T entity)
        {
            super(key, typeKey, null, entity);
        }
    }
}
