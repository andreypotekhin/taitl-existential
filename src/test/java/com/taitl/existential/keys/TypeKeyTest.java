package com.taitl.existential.keys;

import java.util.*;
import com.taitl.ex.examples.night_city.model.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class TypeKeyTest
{

    @Test
    void constructors()
    {
        assertThat(new TypeKey<TypeKey<Cat>>() {
        }.toString(), is("TypeKey<Cat>"));
        assertThat(new TypeKey<TypeKey<Cat>>(true) {
        }.toString(),
                is("com.taitl.existential.keys.TypeKey<com.taitl.ex.examples.night_city.model.Cat>"));
        assertThat(new TypeKey("Doc").toString(), is("Doc"));
        assertThat(new TypeKey(TypeKey.class).toString(), is("TypeKey"));
        assertThat(new TypeKey(TypeKeyTest.class, true).toString(), is("com.taitl.existential.keys.TypeKeyTest"));
        assertThat(new TypeKey(Set.class, "Document").toString(), is("Set<Document>"));
        assertThat(new TypeKey(Set.class, "<Document>").toString(), is("Set<Document>"));
        assertThat(new TypeKey("Set<Document>").toString(), is("Set<Document>"));
        assertThat(new TypeKey(Set.class, "List<Document>").toString(), is("Set<List<Document>>"));
        assertThat(new TypeKey(Set.class, "List<Document>", true).toString(),
                is("java.util.Set<List<Document>>"));
        assertThrows(IllegalArgumentException.class, () -> {
            new TypeKey((String) null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TypeKey(Set.class, (String) null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TypeKey(Set.class, (String) "");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TypeKey(Set.class, (String) "<Document");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TypeKey(Set.class, (String) "Document>");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new TypeKey(Set.class, (String) ">Document<");
        });
        assertThat(assertThrows(IllegalArgumentException.class, () -> {
            new TypeKey(Set.class, (String) "<Document<JSON>");
        }).getMessage(), containsString("must be of proper format"));
    }

    @Nested
    class FactoryFlags
    {
        @Test
        @DisplayName("Rejects raw anonymous subclass")
        void rejectsRawAnonymousSubclass()
        {
            assertThat(assertThrows(IllegalArgumentException.class, () -> {
                new RawTypeKey();
            }).getMessage(), containsString("anonymous subclass"));
        }

        @Test
        @DisplayName("Value of uses full name flag")
        void valueOfUsesFullNameFlag()
        {
            assertThat(TypeKey.valueOf(TypeKeyTest.class, true).toString(),
                    is("com.taitl.existential.keys.TypeKeyTest"));
            assertThat(TypeKey.valueOf(Set.class, "List<Document>", true).toString(),
                    is("java.util.Set<List<Document>>"));
            assertThat(TypeKey.valueOf(new TypeKeyTest(), true).toString(),
                    is("com.taitl.existential.keys.TypeKeyTest"));
        }
    }

    @Test
    @DisplayName("Test value of")
    void testValueOf()
    {
        assertThat(TypeKey.valueOf(TypeKeyTest.class, false).toString(), is("TypeKeyTest"));
    }

    @Test
    @DisplayName("Test value of1")
    void testValueOf1()
    {
        assertThat(TypeKey.valueOf(Set.class, "Document", false).toString(), is("Set<Document>"));
    }

    @Test
    @DisplayName("Test value of2")
    void testValueOf2()
    {
        assertThat(TypeKey.valueOf("Set<Document>").toString(), is("Set<Document>"));
    }

    @Test
    @DisplayName("Test value of3")
    void testValueOf3()
    {
        TypeKeyTest t = new TypeKeyTest();
        assertThat(TypeKey.valueOf(t, false).toString(), is("TypeKeyTest"));
        assertThat(TypeKey.valueOf(t, "JSON", false).toString(), is("TypeKeyTest<JSON>"));
    }

    @Nested
    class ValueOfRejects
    {
        @Test
        @DisplayName("Value of rejects null instance")
        void nullInstance()
        {
            assertThat(assertThrows(IllegalArgumentException.class, () -> {
                TypeKey.valueOf((TypeKeyTest) null, false);
            }).getMessage(), containsString("'t' must not be null"));
            assertThat(assertThrows(IllegalArgumentException.class, () -> {
                TypeKey.valueOf((TypeKeyTest) null, "JSON", false);
            }).getMessage(), containsString("'t' must not be null"));
        }
    }

    @Test
    @DisplayName("Test hash code")
    void testHashCode()
    {
        TypeKey<?> a = new TypeKey("Doc");
        TypeKey<?> b = new TypeKey("Doc");
        TypeKey<?> c = new TypeKey("Doc<JSON>");
        assertThat(a.hashCode(), is(b.hashCode()));
        assertThat(a.hashCode(), is(not(c.hashCode())));
    }

    @Test
    void testEquals()
    {
        TypeKey<?> a = new TypeKey("Doc");
        TypeKey<?> b = new TypeKey("Doc");
        TypeKey<?> c = new TypeKey("Doc<JSON>");
        assertThat(a.equals(a), is(true));
        assertThat(a.equals(b), is(true));
        assertThat(a.equals(c), is(false));
        assertThat(a.equals(null), is(false));
        assertThat(a.equals("Doc"), is(false));
    }

    @Test
    @DisplayName("Test to string")
    void testToString()
    {
        assertThat(new TypeKey("Document<JSON>").toString(), is("Document<JSON>"));
    }

    @Test
    void createKey()
    {
        assertThat(TypeKeyProbe.createKeyForTest(Set.class, "<Doc>", false), is("Set<Doc>"));
        assertThat(TypeKeyProbe.createKeyForTest(Set.class, "", true), is("java.util.Set"));
    }

    static class TypeKeyProbe<T> extends TypeKey<T>
    {
        TypeKeyProbe(Class<?> clz)
        {
            super(clz);
        }

        static String createKeyForTest(Class<?> clz, String genericQualifier, boolean useFullName)
        {
            return createKey(clz, genericQualifier, useFullName);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    static class RawTypeKey extends TypeKey
    {
        RawTypeKey()
        {
            super(true);
        }
    }
}
