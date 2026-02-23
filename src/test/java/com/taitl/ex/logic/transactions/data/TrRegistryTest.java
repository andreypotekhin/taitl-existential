package com.taitl.ex.logic.transactions.data;

import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.existential.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class TrRegistryTest
{
    @Test
    void registryInitializesCreateTran()
    {
        Existential ex = new Existential();
        try
        {
            TestTransactions transactions = new TestTransactions(ex);
            TransactionLogic logic = transactions.logic();

            assertThat(logic.registry().createTran, is(notNullValue()));
        }
        finally
        {
            ex.close();
        }
    }

    static class TestTransactions extends ExistentialTransactions
    {
        TestTransactions(Existential ex)
        {
            super(ex);
        }

        TransactionLogic logic()
        {
            return transactionLogic;
        }
    }
}
