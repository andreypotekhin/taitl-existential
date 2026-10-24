package com.taitl.specs.library_usage;

import com.taitl.existential.transactions.Tr;
import com.taitl.specs.SpecBase;
import org.junit.jupiter.api.*;

class UserCanPropagateExistentialExceptions extends SpecBase
{
    @BeforeEach
    public void setup()
    {
        super.setup();
    }

    @AfterEach
    public void cleanup()
    {
        super.cleanup();
    }

    @Test
    @DisplayName("User can call transaction methods without declaring ExistentialException")
    void useTransactionApiWithoutThrowsDeclaration()
    {
        Tr transaction = ex.begin(op);
        ex.update(cat, transaction.id());
        transaction.commit();
    }
}
