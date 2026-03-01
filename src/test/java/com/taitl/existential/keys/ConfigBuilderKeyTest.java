package com.taitl.existential.keys;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    @DisplayName("Test op key constructor")
    void testOpKeyConstructor()
    {
        new OpKey("/a/b/c");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new OpKey(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new OpKey(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new OpKey("/"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new OpKey("/a/b/c/"));
    }

    @Test
    @DisplayName("Test to string")
    void testToString()
    {
        Assertions.assertEquals("/a/b/c", new OpKey("/a/b/c").toString());
    }

    @Test
    @DisplayName("Test has parent")
    void testHasParent()
    {
        Assertions.assertTrue(new OpKey("/a/b/c").hasParent());
        Assertions.assertTrue(new OpKey("/a/b").hasParent());
        Assertions.assertFalse(new OpKey("/a").hasParent());
    }

    @Test
    @DisplayName("Test get parent")
    void testGetParent()
    {
        Assertions.assertEquals(OpKey.valueOf("/a/b"), new OpKey("/a/b/c").getParent());
        Assertions.assertEquals(OpKey.valueOf("/a"), new OpKey("/a/b").getParent());
        Assertions.assertThrows(IllegalStateException.class, () -> new OpKey("/a").getParent());
    }
}
