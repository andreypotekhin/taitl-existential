package com.taitl.ex.core.transactions;

import com.taitl.existential.configs.*;
import com.taitl.existential.indexes.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class TransactionIndexesTest
{
    Transaction tr;

    @BeforeEach
    void setup()
    {
        tr = new Transaction("/op", "name");
    }

    @Nested
    class SetIndexLookup
    {
        @Test
        @DisplayName("Index creates on first access")
        void creates()
        {
            SetIndex<String, String> index = tr.index("accounts", s -> s);
            assertThat(index, is(notNullValue()));
        }

        @Test
        @DisplayName("Index returns same instance")
        void reuses()
        {
            SetIndex<String, String> first = tr.index("accounts", s -> s);
            SetIndex<String, String> second = tr.index("accounts", s -> s);
            assertThat(second, is(sameInstance(first)));
        }
    }
}
