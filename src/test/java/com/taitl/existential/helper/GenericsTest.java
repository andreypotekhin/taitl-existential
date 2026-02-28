package com.taitl.existential.helper;

import com.taitl.ex.common.helper.lang.Generics;
import org.junit.jupiter.api.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GenericsTest
{
    static class Box<T>
    {
    }

    static class StringBox extends Box<String>
    {
    }

    static class RawBox extends Box
    {
    }

    static class StringList extends ArrayList<String>
    {
    }

    @Test
    void anonymousSuperclassTypeArgumentExtractsType()
    {
        Type type = Generics.anonymousSuperclassTypeArgument(StringBox.class, Box.class);

        assertThat(type, is(String.class));
    }

    @Test
    void anonymousSuperclassTypeArgumentRejectsRawSubclass()
    {
        assertThrows(IllegalArgumentException.class,
                () -> Generics.anonymousSuperclassTypeArgument(RawBox.class, Box.class));
    }

    @Test
    void typeNameRendersParameterizedTypes()
    {
        Type type = StringList.class.getGenericSuperclass();

        assertThat(Generics.typeName(type, false), is("ArrayList<String>"));
    }
}
