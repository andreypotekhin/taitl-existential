package com.taitl.ex.logic.transactions.data;

import com.taitl.ex.core.existential.ExistentialTransactions;
import com.taitl.ex.logic.transactions.TransactionLogic;
import com.taitl.existential.Existential;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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
    }
}
