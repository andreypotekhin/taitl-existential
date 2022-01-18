package com.taitl.existential;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OpKeyTest
{
    final String op = "/app/module/op";
    OpKey o;

    @BeforeEach
    protected void setUp() throws Exception
    {
        o = new OpKey(op);
    }

    @AfterEach
    protected void tearDown() throws Exception
    {
        o = null;
    }

    @Test
    void testOpKeyConstructor()
    {
        new OpKey("/a/b/c");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new OpKey(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new OpKey(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new OpKey("/"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new OpKey("/a/b/c/"));
    }

    @Test
    void testToString()
    {
        Assertions.assertEquals("/a/b/c", new OpKey("/a/b/c").toString());
    }

    @Test
    void testHasParent()
    {
        Assertions.assertTrue(new OpKey("/a/b/c").hasParent());
        Assertions.assertTrue(new OpKey("/a/b").hasParent());
        Assertions.assertFalse(new OpKey("/a").hasParent());
    }

    @Test
    void testGetParent()
    {
        Assertions.assertEquals(OpKey.valueOf("/a/b"), new OpKey("/a/b/c").getParent());
        Assertions.assertEquals(OpKey.valueOf("/a"), new OpKey("/a/b").getParent());
        Assertions.assertThrows(IllegalStateException.class, () -> new OpKey("/a").getParent());
    }
}
