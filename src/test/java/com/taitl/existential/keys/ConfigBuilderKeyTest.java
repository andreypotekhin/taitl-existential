package com.taitl.existential.keys;

import org.junit.jupiter.api.*;

import com.taitl.existential.keys.OpKey;

class ConfigBuilderKeyTest
{
    final String op = "/app/module/op";
    OpKey o;

    @BeforeEach
    protected void setUp()
    {
        o = new OpKey(op);
    }

    @AfterEach
    protected void tearDown()
    {
        o = null;
    }

    @Nested
    class Constructor
    {
        @Test
        @DisplayName("Validates op key constructor")
        void validates()
        {
            new OpKey("/a/b/c");
            Assertions.assertThrows(IllegalArgumentException.class, () -> new OpKey(null));
            Assertions.assertThrows(IllegalArgumentException.class, () -> new OpKey(""));
            Assertions.assertThrows(IllegalArgumentException.class, () -> new OpKey("/"));
            Assertions.assertThrows(IllegalArgumentException.class, () -> new OpKey("/a/b/c/"));
        }

        @Test
        @DisplayName("To string")
        void toStringValue()
        {
            Assertions.assertEquals("/a/b/c", new OpKey("/a/b/c").toString());
        }
    }

    @Nested
    class Parent
    {
        @Test
        @DisplayName("Has parent")
        void hasParent()
        {
            Assertions.assertTrue(new OpKey("/a/b/c").hasParent());
            Assertions.assertTrue(new OpKey("/a/b").hasParent());
            Assertions.assertFalse(new OpKey("/a").hasParent());
        }

        @Test
        @DisplayName("Get parent")
        void getParent()
        {
            Assertions.assertEquals(OpKey.valueOf("/a/b"), new OpKey("/a/b/c").getParent());
            Assertions.assertEquals(OpKey.valueOf("/a"), new OpKey("/a/b").getParent());
            Assertions.assertThrows(IllegalStateException.class, () -> new OpKey("/a").getParent());
        }
    }
}
