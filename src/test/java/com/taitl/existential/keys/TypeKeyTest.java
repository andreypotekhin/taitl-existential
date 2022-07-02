package com.taitl.existential.keys;

import com.taitl.existential.helper.Args;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TypeKeyTest
{

    @Test
    void constructors()
    {
        assertThat(new TypeKey("Doc").toString(), is("Doc"));
        assertThat(new TypeKey(TypeKey.class).toString(), is("TypeKey"));
        assertThat(new TypeKey(Set.class, "Document").toString(), is("Set<Document>"));
        assertThat(new TypeKey(Set.class, "<Document>").toString(), is("Set<Document>"));
        assertThat(new TypeKey("Set<Document>").toString(), is("Set<Document>"));
        assertThat(new TypeKey(Set.class, "List<Document>").toString(), is("Set<List<Document>>"));
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
    void valueOf()
    {
    }

    @Test
    void testValueOf()
    {
    }

    @Test
    void testValueOf1()
    {
    }

    @Test
    void testValueOf2()
    {
    }

    @Test
    void testValueOf3()
    {
    }

    @Test
    void testHashCode()
    {
    }

    @Test
    void testEquals()
    {
    }

    @Test
    void testToString()
    {
    }

    @Test
    void setTypeid()
    {
    }
}