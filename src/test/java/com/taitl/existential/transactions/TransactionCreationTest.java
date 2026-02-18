package com.taitl.existential.transactions;

import java.util.*;
import com.taitl.ex.common.helper.*;
import com.taitl.ex.logic.transactions.actions.*;
import com.taitl.existential.configs.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class TransactionCreationTest
{
    @Test
    void forConfigBuildsContextTransactionsAndCustom()
    {
        Context c1 = context("/ctx1");
        Context c2 = context("/ctx2");
        Config config = new Config();
        config.addContext(c1);
        config.addContext(c2);
        Transaction custom = new Transaction("custom", "custom");

        Tr tr = TransactionCreation.forConfig("/op", config, custom);

        assertThat(tr.op, is("/op"));
        assertThat(tr.transactions, hasSize(3));
        assertThat(tr.transactions.get(0).context, is(c1));
        assertThat(tr.transactions.get(0).name, is(c1.name()));
        assertThat(tr.transactions.get(0).op, is("/op"));
        assertThat(tr.transactions.get(1).context, is(c2));
        assertThat(tr.transactions.get(1).name, is(c2.name()));
        assertThat(tr.transactions.get(1).op, is("/op"));
        assertThat(tr.transactions.get(2), sameInstance(custom));
        assertThat(custom.op, is("/op"));
    }

    @Test
    void forContextsBuildsFromCreateTransaction()
    {
        Context c1 = context("/ctx1");
        Context c2 = context("/ctx2");
        CreateTransaction builder = new CreateTransaction();

        Tr tr = builder.forContexts("/op2", List.of(c1, c2), null);

        assertThat(tr.op, is("/op2"));
        assertThat(tr.transactions, hasSize(2));
        assertThat(tr.transactions.get(0).context, is(c1));
        assertThat(tr.transactions.get(0).name, is(c1.name()));
        assertThat(tr.transactions.get(0).op, is("/op2"));
        assertThat(tr.transactions.get(1).context, is(c2));
        assertThat(tr.transactions.get(1).name, is(c2.name()));
        assertThat(tr.transactions.get(1).op, is("/op2"));
    }

    private static Context context(String name)
    {
        return new Context(name).transaction(() -> new Transaction("seed", "seed"));
    }
}
