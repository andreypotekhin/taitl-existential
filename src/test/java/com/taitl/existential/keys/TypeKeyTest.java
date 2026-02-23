package com.taitl.existential.keys;

import java.util.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class TypeKeyTest
{

    @Test
    void constructors()
    {
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

    @Test
    void valueOfFull()
    {
        assertThat(TypeKey.valueOfFull(TypeKeyTest.class).toString(),
                is("com.taitl.existential.keys.TypeKeyTest"));
        assertThat(TypeKey.valueOfFull(Set.class, "List<Document>").toString(),
                is("java.util.Set<List<Document>>"));
        assertThat(TypeKey.valueOfFull(new TypeKeyTest()).toString(),
                is("com.taitl.existential.keys.TypeKeyTest"));
    }

    @Test
    void testValueOf()
    {
        assertThat(TypeKey.valueOf(TypeKeyTest.class).toString(), is("TypeKeyTest"));
    }

    @Test
    void testValueOf1()
    {
        assertThat(TypeKey.valueOf(Set.class, "Document").toString(), is("Set<Document>"));
    }

    @Test
    void testValueOf2()
    {
        assertThat(TypeKey.valueOf("Set<Document>").toString(), is("Set<Document>"));
    }

    @Test
    void testValueOf3()
    {
        TypeKeyTest t = new TypeKeyTest();
        assertThat(TypeKey.valueOf(t).toString(), is("TypeKeyTest"));
        assertThat(TypeKey.valueOf(t, "JSON").toString(), is("TypeKeyTest<JSON>"));
    }

    @Test
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
    void testToString()
    {
        assertThat(new TypeKey("Document<JSON>").toString(), is("Document<JSON>"));
    }

    @Test
    void setTypeid()
    {
        TypeKeyProbe<?> probe = new TypeKeyProbe<>(TypeKeyTest.class);
        probe.setTypeidForTest(Set.class, "<Doc>", false);
        assertThat(probe.toString(), is("Set<Doc>"));
        probe.setTypeidForTest(Set.class, "", true);
        assertThat(probe.toString(), is("java.util.Set"));
    }

    static class TypeKeyProbe<T> extends TypeKey<T>
    {
        TypeKeyProbe(Class<?> clz)
        {
            super(clz);
        }

        void setTypeidForTest(Class<?> clz, String genericQualifier, boolean useFullName)
        {
            setTypeid(clz, genericQualifier, useFullName);
        }
    }
}
