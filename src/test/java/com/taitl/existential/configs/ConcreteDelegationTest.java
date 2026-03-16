package com.taitl.existential.configs;

import com.taitl.ex.concrete.*;
import com.taitl.existential.constants.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class ConcreteDelegationTest
{
    @Test
    @DisplayName("Config keeps concrete delegate")
    void configDelegate()
    {
        Config config = new Config();

        assertThat(config.concrete, is(instanceOf(ConcreteConfig.class)));
        assertThat(config.indexes(StageName.VALIDATION), is(notNullValue()));
    }

    @Test
    @DisplayName("Context keeps concrete delegate")
    void contextDelegate()
    {
        Context context = new Context("/app");

        assertThat(context.concrete, is(instanceOf(ConcreteContext.class)));
        assertThat(context.stage(), is(notNullValue()));
    }

    @Test
    @DisplayName("Transaction keeps concrete delegate")
    void transactionDelegate()
    {
        Transaction transaction = new Transaction("/app", "tx");

        assertThat(transaction.concrete, is(instanceOf(ConcreteTransaction.class)));
        assertThat(transaction.stage(), is(notNullValue()));
        assertThat(transaction.index("cats", String::length), is(notNullValue()));
    }
}
