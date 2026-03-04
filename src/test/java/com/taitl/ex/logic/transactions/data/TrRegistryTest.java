package com.taitl.ex.logic.transactions.data;

import com.taitl.ex.core.existential.ExistentialTransactions;
import com.taitl.ex.logic.transactions.TransactionLogic;
import com.taitl.existential.Existential;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class TrRegistryTest
{
    Existential ex;

    @BeforeEach
    void setup()
    {
        ex = new Existential();
    }

    @AfterEach
    void cleanup()
    {
        ex.close();
    }

    @Nested
    class Registry
    {
        @Test
        @DisplayName("Initializes create tran")
        void initializesCreateTran()
        {
            TestTransactions transactions = new TestTransactions(ex);
            TransactionLogic logic = transactions.logic();
            assertThat(logic.registry().createTran, is(notNullValue()));
        }
    }

    static class TestTransactions extends ExistentialTransactions
    {
        TestTransactions(Existential ex)
        {
            super(ex);
        }
    }
}
