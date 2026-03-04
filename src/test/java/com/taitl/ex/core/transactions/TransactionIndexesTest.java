package com.taitl.ex.core.transactions;

import com.taitl.existential.configs.Transaction;
import com.taitl.existential.indexes.Index;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

class TransactionIndexesTest
{
    Transaction tr;

    @BeforeEach
    void setup()
    {
        tr = new Transaction("/op", "name");
    }

    @Nested
    class IndexLookup
    {
        @Test
        @DisplayName("Index creates on first access")
        void creates()
        {
            Index<String, String> index = tr.index("accounts");
            assertThat(index, is(notNullValue()));
        }

        @Test
        @DisplayName("Index returns same instance")
        void reuses()
        {
            Index<String, String> first = tr.index("accounts");
            Index<String, String> second = tr.index("accounts");
            assertThat(second, is(sameInstance(first)));
        }
    }
}
