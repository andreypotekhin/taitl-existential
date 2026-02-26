package com.taitl.ex.core.transactions;

import com.taitl.existential.configs.Transaction;
import com.taitl.existential.indexes.Index;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

class TransactionIndexesTest
{
    @Test
    void indexCreatesOnFirstAccess()
    {
        Transaction tr = new Transaction("/op", "name");

        Index<String, String> index = tr.index("accounts");

        assertThat(index, is(notNullValue()));
    }

    @Test
    void indexReturnsSameInstance()
    {
        Transaction tr = new Transaction("/op", "name");

        Index<String, String> first = tr.index("accounts");
        Index<String, String> second = tr.index("accounts");

        assertThat(second, is(sameInstance(first)));
    }
}
