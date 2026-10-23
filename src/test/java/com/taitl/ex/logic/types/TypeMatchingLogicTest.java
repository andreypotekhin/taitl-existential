package com.taitl.ex.logic.types;

import com.taitl.existential.keys.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class TypeMatchingLogicTest
{
    static class Box<T>
    {
    }

    @Test
    void boundsWarningSignaturesAcrossOperationsAndClearsThemOnClose()
    {
        TypeMatchingLogic logic = new TypeMatchingLogic();
        for (int i = 0; i < 100; i++)
        {
            logic.warnUnresolvedGenerics("/warnings/" + i, TypeKey.valueOf("Box"), Box.class, true);
        }
        assertEquals(64, logic.warned.size());
        logic.close();
        assertEquals(0, logic.warned.size());
        logic.warnUnresolvedGenerics("/warnings/off", TypeKey.valueOf("Box"), Box.class, false);
        assertEquals(0, logic.warned.size());
    }
}
